package br.com.seldonfleet.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.seldonfleet.domain.Classification
import br.com.seldonfleet.domain.ClassifyDevice
import br.com.seldonfleet.domain.Device
import br.com.seldonfleet.domain.DeviceRepository
import br.com.seldonfleet.domain.FavoritesRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class FleetItem(val device: Device, val classification: Classification)

class FleetViewModel(
    private val repository: DeviceRepository,
    private val classifyDevice: ClassifyDevice,
    private val favoritesStore: FavoritesRepository,
) : ViewModel() {
    var devices by mutableStateOf<List<FleetItem>>(emptyList())
        private set
    var favorites by mutableStateOf<Set<String>>(emptySet())
        private set
    var loading by mutableStateOf(true)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            try {
                devices = repository.loadDevices().map { FleetItem(it, classifyDevice(it)) }
            } catch (exception: Exception) {
                error = "Não foi possível ler a frota. Verifique o arquivo local."
            } finally {
                loading = false
            }
        }
        viewModelScope.launch {
            favoritesStore.favorites
                .catch { error = "Não foi possível carregar os favoritos." }
                .collect { favorites = it }
        }
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            try {
                favoritesStore.toggle(id)
            } catch (exception: Exception) {
                error = "Não foi possível salvar o favorito."
            }
        }
    }

    class Factory(
        private val repository: DeviceRepository,
        private val classifyDevice: ClassifyDevice,
        private val favoritesStore: FavoritesRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            FleetViewModel(repository, classifyDevice, favoritesStore) as T
    }
}
