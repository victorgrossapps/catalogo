package com.empresa.catalogo.data.file

import android.content.Context
import com.empresa.catalogo.core.util.sha256
import com.empresa.catalogo.domain.model.Catalogo
import okhttp3.ResponseBody
import java.io.File

class CatalogFileManager(context: Context) {
    private val catalogDir = File(context.filesDir, "catalogos").apply { mkdirs() }

    fun activeFile(): File = File(catalogDir, "catalogo_active.pdf")

    fun pendingFile(): File = File(catalogDir, "catalogo_pending.pdf")

    fun hasActiveCatalog(localPath: String?): Boolean =
        localPath != null && File(localPath).exists()

    fun writePending(body: ResponseBody, onProgress: (Float) -> Unit): File {
        val target = pendingFile()
        body.byteStream().use { input ->
            target.outputStream().use { output ->
                val total = body.contentLength().coerceAtLeast(1L)
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var copied = 0L

                while (true) {
                    val read = input.read(buffer)
                    if (read == -1) break
                    output.write(buffer, 0, read)
                    copied += read
                    onProgress((copied.toFloat() / total.toFloat()).coerceIn(0f, 1f))
                }
            }
        }
        return target
    }

    fun validatePending(catalogo: Catalogo) {
        val pending = pendingFile()
        require(pending.exists()) { "No existe archivo temporal descargado." }
        require(pending.length() == catalogo.pesoBytes) { "El tamaño descargado no coincide." }
        require(pending.sha256().equals(catalogo.checksum, ignoreCase = true)) {
            "El checksum descargado no coincide."
        }
    }

    fun promotePending(): File {
        val active = activeFile()
        val pending = pendingFile()
        require(pending.exists()) { "No existe archivo temporal para promover." }

        if (active.exists()) {
            val backup = File(catalogDir, "catalogo_previous.pdf")
            if (backup.exists()) backup.delete()
            active.renameTo(backup)
        }

        pending.renameTo(active)
        return active
    }

    fun discardPending() {
        val pending = pendingFile()
        if (pending.exists()) pending.delete()
    }
}
