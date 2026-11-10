package br.com.seldonfleet.data

object EnergyNormalizer {
    fun toMah(value: Double?, unit: String?, voltage: Double?): Double? {
        if (value == null || !value.isFinite() || value < 0) return null
        val result = when (unit) {
            "mAh" -> value
            "mWh" -> if (voltage != null && voltage.isFinite() && voltage > 0) value / voltage else return null
            else -> return null
        }
        return result.takeIf { it.isFinite() }
    }
}
