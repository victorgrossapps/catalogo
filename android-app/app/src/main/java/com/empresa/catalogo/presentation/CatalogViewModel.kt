package com.empresa.catalogo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empresa.catalogo.data.local.LocalCatalogState
import com.empresa.catalogo.data.repository.CatalogRepository
import com.empresa.catalogo.data.repository.UpdateResult
import com.empresa.catalogo.domain.model.Catalogo
import com.empresa.catalogo.domain.model.Empresa
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CatalogUiState(
    val loading: Boolean = true,
    val empresa: Empresa? = null,
    val local: LocalCatalogState? = null,
    val updateAvailable: Catalogo? = null,
    val downloading: Boolean = false,
    val progress: Float = 0f,
    val message: String? = null,
    val error: String? = null
)

class CatalogViewModel(
    private val repository: CatalogRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CatalogUiState())
    val state: StateFlow<CatalogUiState> = _state

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null, message = null) }
            val local = repository.localState.first()
            val empresa = runCatching { repository.getEmpresa() }.getOrNull()

            runCatching { repository.checkForUpdate(local) }
                .onSuccess { result ->
                    when (result) {
                        is UpdateResult.Available -> {
                            _state.update {
                                it.copy(
                                    loading = false,
                                    empresa = empresa,
                                    local = local,
                                    updateAvailable = result.catalogo,
                                    message = result.catalogo.mensajeActualizacion ?: "Nuevo catalogo disponible."
                                )
                            }

                            if (result.catalogo.obligatorio) {
                                download(result.catalogo)
                            }
                        }
                        is UpdateResult.UpToDate -> _state.update {
                            it.copy(loading = false, empresa = empresa, local = result.local, message = "Catalogo actualizado.")
                        }
                        is UpdateResult.Downloaded -> Unit
                        is UpdateResult.NoLocalCatalog -> _state.update {
                            it.copy(loading = false, empresa = empresa, local = local, error = result.message)
                        }
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            loading = false,
                            empresa = empresa,
                            local = local,
                            error = if (repository.hasActiveCatalog(local)) "Modo offline. Se usara el ultimo catalogo descargado." else "Se requiere conexion inicial para descargar el catalogo."
                        )
                    }
                }
        }
    }

    fun download(catalogo: Catalogo? = _state.value.updateAvailable) {
        if (catalogo == null) return

        viewModelScope.launch {
            _state.update { it.copy(downloading = true, progress = 0f, error = null) }
            runCatching {
                repository.downloadCatalog(catalogo) { progress ->
                    _state.update { it.copy(progress = progress) }
                }
            }.onSuccess { result ->
                val local = repository.localState.first()
                _state.update {
                    it.copy(
                        downloading = false,
                        progress = 1f,
                        updateAvailable = null,
                        local = local.copy(localPath = result.localPath),
                        message = "Catalogo descargado correctamente."
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        downloading = false,
                        error = error.message ?: "No fue posible descargar el catalogo. Se conserva la version anterior."
                    )
                }
            }
        }
    }
}
