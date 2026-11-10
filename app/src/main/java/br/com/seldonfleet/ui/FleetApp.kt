package br.com.seldonfleet.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

private val Navy = Color(0xFF142C42)
private val Teal = Color(0xFF006B61)
private val Palette = lightColorScheme(primary = Teal, secondary = Navy, background = Color(0xFFF5F7F7), surface = Color.White)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FleetApp(factory: FleetViewModel.Factory) {
    val model: FleetViewModel = viewModel(factory = factory)
    var filter by rememberSaveable { mutableStateOf<String?>(null) }
    var selected by rememberSaveable { mutableStateOf<String?>(null) }
    val detail = model.devices.firstOrNull { it.device.id == selected }
    BackHandler(enabled = selected != null) { selected = null }
    MaterialTheme(colorScheme = Palette) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (selected == null) "Seldon Fleet" else "Detalhes do dispositivo", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        if (selected != null) IconButton(onClick = { selected = null }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    },
                )
            },
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                model.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp)) }
                when {
                    model.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    detail != null -> DetailScreen(detail, detail.device.id in model.favorites) { model.toggleFavorite(detail.device.id) }
                    selected != null -> Text("Dispositivo não encontrado.", modifier = Modifier.padding(16.dp))
                    else -> FleetList(model.devices, model.favorites, filter, { filter = it }, { selected = it }, model::toggleFavorite)
                }
            }
        }
    }
}

