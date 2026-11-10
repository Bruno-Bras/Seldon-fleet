package br.com.seldonfleet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import br.com.seldonfleet.domain.DeviceStatus

@Composable
internal fun FleetList(
    devices: List<FleetItem>,
    favorites: Set<String>,
    filter: String?,
    onFilter: (String?) -> Unit,
    onSelect: (String) -> Unit,
    onFavorite: (String) -> Unit,
) {
    val visible = devices.filter { filter == null || it.classification.status.name == filter }
    Column {
        Text("Visão geral da frota", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp, top = 12.dp))
        Text("${devices.size} dispositivos monitorados", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 12.dp))
        Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = filter == null, onClick = { onFilter(null) }, label = { Text("Todos ${devices.size}") })
            DeviceStatus.entries.forEach { status ->
                val count = devices.count { it.classification.status == status }
                FilterChip(selected = filter == status.name, onClick = { onFilter(status.name) }, label = { Text("${status.label()} $count") })
            }
        }
        if (visible.isEmpty()) {
            Text("Nenhum dispositivo neste filtro.", modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(visible, key = { it.device.id }) { item ->
                    Card(Modifier.fillMaxWidth().clickable { onSelect(item.device.id) }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(item.device.model, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                Text(item.device.id + "  ·  Bateria ${item.device.batteryLevelPct}%", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(8.dp))
                                StatusBadge(item.classification.status)
                            }
                            FavoriteButton(item.device.id in favorites) { onFavorite(item.device.id) }
                        }
                    }
                }
            }
        }
    }
}

