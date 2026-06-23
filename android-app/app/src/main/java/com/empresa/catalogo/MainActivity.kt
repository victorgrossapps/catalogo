package com.empresa.catalogo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.empresa.catalogo.data.file.CatalogFileManager
import com.empresa.catalogo.data.local.CatalogPreferences
import com.empresa.catalogo.data.remote.ApiClient
import com.empresa.catalogo.data.repository.CatalogRepository
import com.empresa.catalogo.presentation.CatalogUiState
import com.empresa.catalogo.presentation.CatalogViewModel
import com.empresa.catalogo.presentation.PdfViewer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CatalogApp()
                }
            }
        }
    }
}

private enum class Screen(val label: String) {
    Home("Inicio"),
    About("Quienes somos"),
    Catalog("Catalogo"),
    Update("Actualizar"),
    Settings("Configuracion")
}

@Composable
private fun CatalogApp() {
    val context = LocalContext.current
    val viewModel: CatalogViewModel = viewModel(factory = remember {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = CatalogRepository(
                    api = ApiClient.create(),
                    preferences = CatalogPreferences(context.applicationContext),
                    files = CatalogFileManager(context.applicationContext)
                )
                return CatalogViewModel(repository) as T
            }
        }
    })

    val state by viewModel.state.collectAsState()
    var screen by remember { mutableStateOf(Screen.Home) }

    Row(modifier = Modifier.fillMaxSize()) {
        NavigationRail {
            Screen.entries.forEach { item ->
                NavigationRailItem(
                    selected = screen == item,
                    onClick = { screen = item },
                    icon = { Text(item.label.take(1)) },
                    label = { Text(item.label) }
                )
            }
        }

        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Header(state)
            Spacer(modifier = Modifier.height(16.dp))

            when (screen) {
                Screen.Home -> HomeScreen(state, onOpenCatalog = { screen = Screen.Catalog })
                Screen.About -> AboutScreen(state)
                Screen.Catalog -> CatalogScreen(state)
                Screen.Update -> UpdateScreen(state, viewModel::refresh) { viewModel.download() }
                Screen.Settings -> SettingsScreen(state, viewModel::refresh)
            }
        }
    }
}

@Composable
private fun Header(state: CatalogUiState) {
    Text(
        text = state.empresa?.nombreComercial ?: "Catalogo Comercial",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
    state.message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
    state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
}

@Composable
private fun HomeScreen(state: CatalogUiState, onOpenCatalog: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Bienvenido", style = MaterialTheme.typography.headlineSmall)
                Text("Presentacion comercial y catalogo offline para tablet.")
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onOpenCatalog) { Text("Ver catalogo") }
            }
        }
        if (state.loading) CircularProgressIndicator()
    }
}

@Composable
private fun AboutScreen(state: CatalogUiState) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Quienes somos", style = MaterialTheme.typography.headlineSmall)
            Text(state.empresa?.textoQuienesSomos ?: "Informacion institucional no disponible.")
            state.empresa?.telefono?.let { Text("Telefono: $it") }
            state.empresa?.whatsapp?.let { Text("WhatsApp: $it") }
            state.empresa?.correo?.let { Text("Correo: $it") }
            state.empresa?.direccion?.let { Text("Direccion: $it") }
        }
    }
}

@Composable
private fun CatalogScreen(state: CatalogUiState) {
    val localPath = state.local?.localPath
    if (localPath.isNullOrBlank()) {
        Text("No hay catalogo local. Se requiere conexion inicial para descargarlo.")
    } else {
        PdfViewer(localPath = localPath, modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun UpdateScreen(
    state: CatalogUiState,
    onRefresh: () -> Unit,
    onDownload: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Actualizacion de catalogo", style = MaterialTheme.typography.headlineSmall)
        state.updateAvailable?.let {
            Text("Nueva version: ${it.versionCodigo}")
            Text(it.mensajeActualizacion ?: "Nuevo catalogo disponible.")
            Button(onClick = onDownload, enabled = !state.downloading) { Text("Actualizar ahora") }
        } ?: Text("No hay actualizacion pendiente.")

        if (state.downloading) {
            LinearProgressIndicator(progress = { state.progress }, modifier = Modifier.fillMaxWidth())
            Text("${(state.progress * 100).toInt()}%")
        }

        Button(onClick = onRefresh, enabled = !state.downloading) { Text("Verificar actualizacion") }
    }
}

@Composable
private fun SettingsScreen(state: CatalogUiState, onRefresh: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Configuracion", style = MaterialTheme.typography.headlineSmall)
        Text("Version app: ${BuildConfig.VERSION_NAME}")
        Text("Version catalogo: ${state.local?.versionCodigo ?: "Sin catalogo"}")
        Text("Device UUID: ${state.local?.deviceUuid ?: "Pendiente"}")
        Text("Ultima descarga: ${state.local?.downloadedAt ?: "Pendiente"}")
        Button(onClick = onRefresh) { Text("Verificar actualizacion") }
    }
}
