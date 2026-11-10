package br.com.seldonfleet.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.seldonfleet.domain.Classification
import br.com.seldonfleet.domain.DeviceStatus
import br.com.seldonfleet.domain.Signal
import java.util.Locale

@Composable
internal fun FavoriteButton(favorite: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(if (favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, contentDescription = if (favorite) "Remover favorito" else "Marcar favorito", tint = if (favorite) Color(0xFFB3261E) else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
internal fun StatusBadge(status: DeviceStatus) {
    Surface(color = statusColor(status).copy(alpha = .13f), shape = RoundedCornerShape(50)) {
        Text(status.label(), color = statusColor(status), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
    }
}

internal fun DeviceStatus.label() = when (this) {
    DeviceStatus.OK -> "OK"
    DeviceStatus.ATENCAO -> "Atenção"
    DeviceStatus.CRITICO -> "Crítico"
    DeviceStatus.INDETERMINADO -> "Sem dados"
}
internal fun statusColor(status: DeviceStatus) = when (status) {
    DeviceStatus.OK -> Color(0xFF006B52)
    DeviceStatus.ATENCAO -> Color(0xFF845400)
    DeviceStatus.CRITICO -> Color(0xFFB3261E)
    DeviceStatus.INDETERMINADO -> Color(0xFF505F6B)
}
internal fun number(value: Double) = String.format(Locale("pt", "BR"), "%.1f", value)
internal fun explanation(result: Classification): String {
    val prefix = result.status.label()
    return when (result.signal) {
        Signal.CAPACITY -> "$prefix: capacidade da bateria ausente ou não positiva."
        Signal.SCREEN_TIME -> "$prefix: tempo de tela ausente ou não positivo."
        Signal.ENERGY -> "$prefix: consumo ou unidade de energia inválida, ou tensão nominal indisponível."
        Signal.JANK_MISSING -> "$prefix: percentual de quadros perdidos indisponível ou inválido."
        Signal.CRASHES_MISSING -> "$prefix: contagem de falhas indisponível ou inválida."
        Signal.DRAIN -> "$prefix: dreno de ${number(result.value!!)}%/h, limiar de ${number(result.threshold!!)}%/h."
        Signal.CRASHES -> "$prefix: ${result.value!!.toInt()} falhas em 7 dias, limiar de ${result.threshold!!.toInt()}."
        Signal.JANK -> "$prefix: ${number(result.value!!)}% de quadros perdidos, limiar de ${number(result.threshold!!)}%."
        Signal.NONE -> "OK: dreno de ${number(result.value!!)}%/h, sem sinais acima dos limiares."
    }
}
