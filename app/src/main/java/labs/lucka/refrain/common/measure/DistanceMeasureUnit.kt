package labs.lucka.refrain.common.measure

import android.icu.util.MeasureUnit

enum class DistanceMeasureUnit {
    METER {
        override val measureUnit: MeasureUnit = MeasureUnit.METER
        override fun convert(value: Float): Float = value
    },

    @Suppress("unused")
    KILOMETER {
        override val measureUnit: MeasureUnit = MeasureUnit.KILOMETER
        override fun convert(value: Float): Float = value / 1000F
    };

    abstract val measureUnit: MeasureUnit
    abstract fun convert(value: Float): Float
}