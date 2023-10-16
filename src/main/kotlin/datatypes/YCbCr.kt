package datatypes

data class YCbCr(var y: Double = 0.0, var cb: Double = 0.0, var cr: Double = 0.0){
    override fun toString(): String {
        return "(y=$y, cb=$cb, cr=$cr) "
    }
}
