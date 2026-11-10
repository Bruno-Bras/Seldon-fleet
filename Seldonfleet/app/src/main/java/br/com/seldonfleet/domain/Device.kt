package br.com.seldonfleet.domain

import kotlinx.coroutines.flow.Flow

data class Device(
    val id: String,
    val model: String,
    val soc: String,
    val tier: String,
    val ramMb: Int,
    val androidApi: Int,
    val batteryCapacityMah: Double?,
    val batteryLevelPct: Int,
    val screenOnMinutes: Double?,
    val energyMah: Double?,
    val jankFramesPct: Double?,
    val crashCount7d: Int?,
    val lastSeen: String,
)

enum class DeviceStatus { OK, ATENCAO, CRITICO, INDETERMINADO }
enum class Signal { CAPACITY, SCREEN_TIME, ENERGY, JANK_MISSING, CRASHES_MISSING, DRAIN, CRASHES, JANK, NONE }
data class Classification(val status: DeviceStatus, val signal: Signal, val value: Double? = null, val threshold: Double? = null)

interface DeviceRepository {
    suspend fun loadDevices(): List<Device>
}

interface FavoritesRepository {
    val favorites: Flow<Set<String>>
    suspend fun toggle(id: String)
}
