package datatypes

data class YCbCr(var y: Double? = null, var cb: Double? = null, var cr: Double? = null){
    override fun toString(): String {
        return "(y=$y, cb=$cb, cr=$cr) "
    }
}
