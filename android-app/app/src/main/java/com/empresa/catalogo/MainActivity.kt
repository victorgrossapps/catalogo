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
import com.empresa.catalogo.presentation.ApiConnectionUiState
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
    Catalog("Catálogo"),
    Update("Actualizar"),
    Settings("Configuración")
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
                Screen.Settings -> SettingsScreen(
                    state = state,
                    onRefresh = viewModel::refresh,
                    onTestApi = viewModel::testApiConnection
                )
            }
        }
    }
}

@Composable
private fun Header(state: CatalogUiState) {
    Text(
        text = state.empresa?.nombreComercial ?: "Catálogo Comercial",
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
                Text("Presentación comercial y catálogo offline para tablet.")
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onOpenCatalog) { Text("Ver catálogo") }
            }
        }
        if (state.loading) CircularProgressIndicator()
    }
}

@Composable
private fun AboutScreen(state: CatalogUiState) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Quiénes somos", style = MaterialTheme.typography.headlineSmall)
            Text(state.empresa?.textoQuienesSomos ?: "Información institucional no disponible.")
            state.empresa?.telefono?.let { Text("Teléfono: $it") }
            state.empresa?.whatsapp?.let { Text("WhatsApp: $it") }
            state.empresa?.correo?.let { Text("Correo: $it") }
            state.empresa?.direccion?.let { Text("Dirección: $it") }
        }
    }
}

@Composable
private fun CatalogScreen(state: CatalogUiState) {
    val localPath = state.local?.localPath
    if (localPath.isNullOrBlank()) {
        Text("No hay catálogo local. Se requiere conexión inicial para descargarlo.")
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
        Text("Actualización de catálogo", style = MaterialTheme.typography.headlineSmall)
        state.updateAvailable?.let {
            Text("Nueva versión: ${it.versionCodigo}")
            Text(it.mensajeActualizacion ?: "Nuevo catálogo disponible.")
            Button(onClick = onDownload, enabled = !state.downloading) { Text("Actualizar ahora") }
        } ?: Text("No hay actualización pendiente.")

        if (state.downloading) {
            LinearProgressIndicator(progress = { state.progress }, modifier = Modifier.fillMaxWidth())
            Text("${(state.progress * 100).toInt()}%")
        }

        Button(onClick = onRefresh, enabled = !state.downloading) { Text("Verificar actualización") }
    }
}

@Composable
private fun SettingsScreen(
    state: CatalogUiState,
    onRefresh: () -> Unit,
    onTestApi: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Configuración", style = MaterialTheme.typography.headlineSmall)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Conexión API", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("API Base URL: ${BuildConfig.API_BASE_URL}")
                ApiConnectionStatus(state.apiConnection)
                Button(
                    onClick = onTestApi,
                    enabled = state.apiConnection !is ApiConnectionUiState.Testing
                ) {
                    Text("Probar conexión API")
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Estado del catálogo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Versión app: ${BuildConfig.VERSION_NAME}")
                Text("Versión catálogo: ${state.local?.versionCodigo ?: "Sin catálogo"}")
                Text("Device UUID: ${state.local?.deviceUuid ?: "Pendiente"}")
                Text("Última descarga: ${state.local?.downloadedAt ?: "Pendiente"}")
                Button(onClick = onRefresh) { Text("Verificar actualización de catálogo") }
            }
        }
    }
}

@Composable
private fun ApiConnectionStatus(status: ApiConnectionUiState) {
    when (status) {
        ApiConnectionUiState.NotTested -> Text("Estado API: No probado")
        ApiConnectionUiState.Testing -> Text("Estado API: Probando conexión...")
        is ApiConnectionUiState.Connected -> {
            Text("Estado API: API conectada correctamente", color = MaterialTheme.colorScheme.primary)
            Text("Servicio: ${status.service ?: "No informado"}")
            Text("Timestamp: ${status.timestamp ?: "No informado"}")
            Text("URL usada: ${status.url}")
        }
        is ApiConnectionUiState.Error -> {
            Text("Estado API: Error de conexión API", color = MaterialTheme.colorScheme.error)
            status.statusCode?.let { Text("Código HTTP: $it") }
            Text("Error: ${status.message}")
            Text("URL usada: ${status.url}")
        }
    }
}
