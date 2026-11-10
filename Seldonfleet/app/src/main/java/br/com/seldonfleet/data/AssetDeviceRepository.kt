package br.com.seldonfleet.data

import android.content.Context
import br.com.seldonfleet.domain.Device
import br.com.seldonfleet.domain.DeviceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AssetDeviceRepository(private val context: Context) : DeviceRepository {
    override suspend fun loadDevices(): List<Device> = withContext(Dispatchers.IO) {
        val json = context.assets.open("frota.json").bufferedReader().use { it.readText() }
        val devices = JSONObject(json).getJSONArray("devices")
        List(devices.length()) { index ->
            val item = devices.getJSONObject(index)
            Device(
                id = item.getString("id"),
                model = item.getString("model"),
                soc = item.getString("soc"),
                tier = item.getString("tier"),
                ramMb = item.getInt("ramMb"),
                androidApi = item.getInt("androidApi"),
                batteryCapacityMah = item.numberOrNull("batteryCapacityMah"),
                batteryLevelPct = item.getInt("batteryLevelPct"),
                screenOnMinutes = item.numberOrNull("screenOnMinutes"),
                energyMah = EnergyNormalizer.toMah(
                    item.numberOrNull("energyConsumed"),
                    item.optString("energyUnit"),
                    item.numberOrNull("nominalVoltage"),
                ),
                jankFramesPct = item.numberOrNull("jankFramesPct"),
                crashCount7d = item.numberOrNull("crashCount7d")?.toInt(),
                lastSeen = item.getString("lastSeen"),
            )
        }
    }
}

private fun JSONObject.numberOrNull(name: String): Double? =
    if (!has(name) || isNull(name)) null else optDouble(name, Double.NaN).takeIf { it.isFinite() }
