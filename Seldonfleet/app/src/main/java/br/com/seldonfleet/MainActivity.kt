package br.com.seldonfleet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import br.com.seldonfleet.data.AssetDeviceRepository
import br.com.seldonfleet.data.FavoritesStore
import br.com.seldonfleet.domain.ClassifyDevice
import br.com.seldonfleet.ui.FleetApp
import br.com.seldonfleet.ui.FleetViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = FleetViewModel.Factory(
            AssetDeviceRepository(applicationContext),
            ClassifyDevice(),
            FavoritesStore(applicationContext),
        )
        setContent { FleetApp(factory) }
    }
}
