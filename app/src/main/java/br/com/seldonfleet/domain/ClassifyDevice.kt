package br.com.seldonfleet.domain

class ClassifyDevice {
    operator fun invoke(device: Device): Classification {
        val capacity = device.batteryCapacityMah
        val minutes = device.screenOnMinutes
        val energy = device.energyMah
        val jank = device.jankFramesPct
        val crashes = device.crashCount7d
        if (capacity == null || !capacity.isFinite() || capacity <= 0) return missing(Signal.CAPACITY)
        if (minutes == null || !minutes.isFinite() || minutes <= 0) return missing(Signal.SCREEN_TIME)
        if (energy == null || !energy.isFinite() || energy < 0) return missing(Signal.ENERGY)
        if (jank == null || !jank.isFinite() || jank !in 0.0..100.0) return missing(Signal.JANK_MISSING)
        if (crashes == null || crashes < 0) return missing(Signal.CRASHES_MISSING)
        val drain = (energy / (minutes / 60.0)) / capacity * 100.0
        if (!drain.isFinite()) return missing(Signal.ENERGY)
        return when {
            drain >= 12 -> Classification(DeviceStatus.CRITICO, Signal.DRAIN, drain, 12.0)
            crashes >= 3 -> Classification(DeviceStatus.CRITICO, Signal.CRASHES, crashes.toDouble(), 3.0)
            jank >= 15 -> Classification(DeviceStatus.CRITICO, Signal.JANK, jank, 15.0)
            drain >= 7 -> Classification(DeviceStatus.ATENCAO, Signal.DRAIN, drain, 7.0)
            crashes >= 1 -> Classification(DeviceStatus.ATENCAO, Signal.CRASHES, crashes.toDouble(), 1.0)
            jank >= 8 -> Classification(DeviceStatus.ATENCAO, Signal.JANK, jank, 8.0)
            else -> Classification(DeviceStatus.OK, Signal.NONE, drain)
        }
    }

    private fun missing(signal: Signal) = Classification(DeviceStatus.INDETERMINADO, signal)
}
