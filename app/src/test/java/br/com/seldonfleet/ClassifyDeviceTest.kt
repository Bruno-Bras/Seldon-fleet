package br.com.seldonfleet

import br.com.seldonfleet.data.EnergyNormalizer
import br.com.seldonfleet.domain.ClassifyDevice
import br.com.seldonfleet.domain.Device
import br.com.seldonfleet.domain.DeviceStatus
import br.com.seldonfleet.domain.Signal
import org.junit.Assert.*
import org.junit.Test

class ClassifyDeviceTest {
    private val classify = ClassifyDevice()
    private fun device(drain: Double = 1.0, jank: Double? = 0.0, crashes: Int? = 0) = Device(
        "id", "model", "soc", "mid", 4096, 34, 1000.0, 80, 60.0,
        drain * 10, jank, crashes, "2026-10-20T00:00:00Z",
    )

    @Test fun equivalentUnitsYieldSameDrainAndStatus() {
        val mah = EnergyNormalizer.toMah(980.0, "mAh", null)!!
        val mwh = EnergyNormalizer.toMah(3773.0, "mWh", 3.85)!!
        assertEquals(mah, mwh, 0.00001)
        val a = classify(device().copy(energyMah = mah))
        val b = classify(device().copy(energyMah = mwh))
        assertEquals(a.status, b.status)
        assertEquals(a.value!!, b.value!!, 0.00001)
    }

    @Test fun invalidDivisorsAreIndeterminate() {
        for (capacity in listOf(null, 0.0, -1.0)) {
            assertEquals(Signal.CAPACITY, classify(device().copy(batteryCapacityMah = capacity)).signal)
        }
        for (minutes in listOf(null, 0.0, -1.0)) {
            assertEquals(Signal.SCREEN_TIME, classify(device().copy(screenOnMinutes = minutes)).signal)
        }
        assertEquals(DeviceStatus.INDETERMINADO, classify(device().copy(batteryCapacityMah = 0.0, crashCount7d = 3)).status)
    }

    @Test fun invalidEnergyAndOtherSignalsAreIndeterminate() {
        assertNull(EnergyNormalizer.toMah(100.0, "mWh", 0.0))
        assertNull(EnergyNormalizer.toMah(100.0, "mWh", null))
        assertNull(EnergyNormalizer.toMah(100.0, "unknown", 3.8))
        assertEquals(DeviceStatus.INDETERMINADO, classify(device().copy(energyMah = null)).status)
        assertEquals(DeviceStatus.INDETERMINADO, classify(device(jank = null)).status)
        assertEquals(DeviceStatus.INDETERMINADO, classify(device(crashes = null)).status)
    }

    @Test fun drainBoundariesAreInclusive() {
        assertEquals(DeviceStatus.OK, classify(device(drain = 6.999)).status)
        assertEquals(DeviceStatus.ATENCAO, classify(device(drain = 7.0)).status)
        assertEquals(DeviceStatus.ATENCAO, classify(device(drain = 7.001)).status)
        assertEquals(DeviceStatus.ATENCAO, classify(device(drain = 11.999)).status)
        assertEquals(DeviceStatus.CRITICO, classify(device(drain = 12.0)).status)
        assertEquals(DeviceStatus.CRITICO, classify(device(drain = 12.001)).status)
    }

    @Test fun jankBoundariesAreInclusive() {
        assertEquals(DeviceStatus.OK, classify(device(jank = 7.999)).status)
        assertEquals(DeviceStatus.ATENCAO, classify(device(jank = 8.0)).status)
        assertEquals(DeviceStatus.ATENCAO, classify(device(jank = 8.001)).status)
        assertEquals(DeviceStatus.ATENCAO, classify(device(jank = 14.999)).status)
        assertEquals(DeviceStatus.CRITICO, classify(device(jank = 15.0)).status)
        assertEquals(DeviceStatus.CRITICO, classify(device(jank = 15.001)).status)
    }

    @Test fun crashBoundariesAreInclusive() {
        assertEquals(DeviceStatus.OK, classify(device(crashes = 0)).status)
        assertEquals(DeviceStatus.ATENCAO, classify(device(crashes = 1)).status)
        assertEquals(DeviceStatus.ATENCAO, classify(device(crashes = 2)).status)
        assertEquals(DeviceStatus.CRITICO, classify(device(crashes = 3)).status)
        assertEquals(DeviceStatus.CRITICO, classify(device(crashes = 4)).status)
    }

    @Test fun criticalSignalWinsOverAttentionAndReasonIsCritical() {
        val result = classify(device(drain = 8.0, crashes = 3))
        assertEquals(DeviceStatus.CRITICO, result.status)
        assertEquals(Signal.CRASHES, result.signal)
        assertEquals(3.0, result.value!!, 0.0)
    }

    @Test fun nonFiniteArithmeticIsNeverExposed() {
        val result = classify(device().copy(energyMah = Double.MAX_VALUE, screenOnMinutes = Double.MIN_VALUE))
        assertEquals(DeviceStatus.INDETERMINADO, result.status)
        assertNull(result.value)
    }
}
