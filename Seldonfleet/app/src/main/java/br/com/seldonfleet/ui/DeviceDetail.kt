package br.com.seldonfleet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
internal fun DetailScreen(item: FleetItem, favorite: Boolean, onFavorite: () -> Unit) {
    val device = item.device
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(device.model, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(device.id, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                FavoriteButton(favorite, onFavorite)
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = statusColor(item.classification.status).copy(alpha = .10f))) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    StatusBadge(item.classification.status)
                    Spacer(Modifier.height(10.dp))
                    Text(explanation(item.classification), style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        item { Text("Telemetria", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) }
        val facts = listOf(
            "Processador" to "${device.soc} (${device.tier})",
            "Memória" to "${device.ramMb} MB",
            "Android" to "API ${device.androidApi}",
            "Bateria" to "${device.batteryLevelPct}%",
            "Capacidade" to (device.batteryCapacityMah?.let { "${number(it)} mAh" } ?: "Sem dados"),
            "Consumo normalizado" to (device.energyMah?.let { "${number(it)} mAh" } ?: "Sem dados"),
            "Tempo de tela" to (device.screenOnMinutes?.let { "${number(it)} min" } ?: "Sem dados"),
            "Quadros perdidos" to (device.jankFramesPct?.let { "${number(it)}%" } ?: "Sem dados"),
            "Falhas em 7 dias" to (device.crashCount7d?.toString() ?: "Sem dados"),
            "Último contato" to device.lastSeen,
        )
        items(facts) { (label, value) ->
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                Text(value, fontWeight = FontWeight.Medium)
            }
            HorizontalDivider()
        }
    }
}

