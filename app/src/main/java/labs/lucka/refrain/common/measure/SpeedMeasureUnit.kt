package labs.lucka.refrain.common.measure

import android.icu.util.MeasureUnit

enum class SpeedMeasureUnit {
    METER_PER_SECOND {
        override val measureUnit: MeasureUnit = MeasureUnit.METER_PER_SECOND
        override fun convert(value: Float): Float = value
    },

    @Suppress("unused")
    KILOMETER_PER_HOUR {
        override val measureUnit: MeasureUnit = MeasureUnit.KILOMETER_PER_HOUR
        override fun convert(value: Float): Float = value * 3.6F
    };

    abstract val measureUnit: MeasureUnit
    abstract fun convert(value: Float): Float
}