package com.empresa.catalogo.presentation

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun PdfViewer(
    localPath: String,
    modifier: Modifier = Modifier,
    fullScreen: Boolean = false,
    onClose: (() -> Unit)? = null
) {
    val file = remember(localPath) { File(localPath) }
    var pageIndex by remember(localPath) { mutableIntStateOf(0) }
    var pageCount by remember(localPath) { mutableIntStateOf(1) }
    var bitmap by remember(localPath, pageIndex) { mutableStateOf<Bitmap?>(null) }
    var zoom by remember(localPath, pageIndex) { mutableFloatStateOf(1f) }
    var offset by remember(localPath, pageIndex) { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(if (fullScreen) Color.Black else MaterialTheme.colorScheme.background)
    ) {
        val availableWidthPx = with(density) { maxWidth.toPx() }.toInt().coerceAtLeast(1)

        LaunchedEffect(localPath, pageIndex, availableWidthPx) {
            bitmap = null
            val renderResult = withContext(Dispatchers.IO) {
                val descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = PdfRenderer(descriptor)
                try {
                    val safeIndex = pageIndex.coerceIn(0, renderer.pageCount - 1)
                    val page = renderer.openPage(safeIndex)
                    try {
                        val scale = availableWidthPx.toFloat() / page.width.toFloat()
                        val bitmapWidth = availableWidthPx
                        val bitmapHeight = (page.height * scale).toInt().coerceAtLeast(1)
                        val rendered = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
                        page.render(rendered, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        renderer.pageCount to rendered
                    } finally {
                        page.close()
                    }
                } finally {
                    renderer.close()
                    descriptor.close()
                }
            }
            pageCount = renderResult.first
            bitmap = renderResult.second
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Página del catálogo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer(
                            scaleX = zoom,
                            scaleY = zoom,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, gestureZoom, _ ->
                                zoom = (zoom * gestureZoom).coerceIn(1f, 4f)
                                offset = if (zoom > 1f) offset + pan else Offset.Zero
                            }
                        }
                )
            } ?: Text(
                text = "Cargando página...",
                color = if (fullScreen) Color.White else MaterialTheme.colorScheme.onBackground
            )
        }

        PdfFloatingControls(
            pageIndex = pageIndex,
            pageCount = pageCount,
            fullScreen = fullScreen,
            onPrevious = {
                pageIndex--
                zoom = 1f
                offset = Offset.Zero
            },
            onNext = {
                pageIndex++
                zoom = 1f
                offset = Offset.Zero
            },
            onClose = onClose
        )
    }
}

@Composable
private fun PdfFloatingControls(
    pageIndex: Int,
    pageCount: Int,
    fullScreen: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onClose: (() -> Unit)?
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = if (fullScreen) 0.62f else 0.08f), RoundedCornerShape(999.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(enabled = pageIndex > 0, onClick = onPrevious) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                Spacer(Modifier.size(6.dp))
                Text("Anterior")
            }
            Text(
                text = "Página ${pageIndex + 1} de $pageCount",
                color = if (fullScreen) Color.White else MaterialTheme.colorScheme.onSurface
            )
            Button(enabled = pageIndex < pageCount - 1, onClick = onNext) {
                Text("Siguiente")
                Spacer(Modifier.size(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }

        onClose?.let {
            IconButton(
                onClick = it,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.62f), RoundedCornerShape(999.dp))
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Cerrar pantalla completa",
                    tint = Color.White
                )
            }
        }
    }
}
