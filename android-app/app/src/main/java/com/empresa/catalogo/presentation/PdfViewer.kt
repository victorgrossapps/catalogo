package com.empresa.catalogo.presentation

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun PdfViewer(localPath: String, modifier: Modifier = Modifier) {
    val file = remember(localPath) { File(localPath) }
    var pageIndex by remember(localPath) { mutableIntStateOf(0) }
    var pageCount by remember(localPath) { mutableIntStateOf(1) }
    var bitmap by remember(localPath, pageIndex) { mutableStateOf<Bitmap?>(null) }

    DisposableEffect(localPath, pageIndex) {
        val descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val renderer = PdfRenderer(descriptor)
        pageCount = renderer.pageCount
        val page = renderer.openPage(pageIndex.coerceIn(0, renderer.pageCount - 1))
        val rendered = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
        page.render(rendered, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        bitmap = rendered

        onDispose {
            page.close()
            renderer.close()
            descriptor.close()
        }
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(enabled = pageIndex > 0, onClick = { pageIndex-- }) { Text("Anterior") }
            Text("Pagina ${pageIndex + 1} de $pageCount", modifier = Modifier.padding(top = 12.dp))
            Button(enabled = pageIndex < pageCount - 1, onClick = { pageIndex++ }) { Text("Siguiente") }
        }

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Pagina del catalogo",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
