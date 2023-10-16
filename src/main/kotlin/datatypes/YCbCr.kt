package datatypes

import java.math.RoundingMode
import java.text.DecimalFormat

data class YCbCr(var y: Double = 0.0, var cb: Double = 0.0, var cr: Double = 0.0){
    override fun toString(): String {
        val df = DecimalFormat("0.000")
        df.roundingMode = RoundingMode.CEILING
        return "(y=${df.format(y)}, cb=${df.format(cb)}, cr=${df.format(cr)}) "
    }
}
