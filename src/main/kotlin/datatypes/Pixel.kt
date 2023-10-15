package datatypes

data class Pixel(var r: Int = 0, var g: Int = 0, var b: Int = 0) {
    override fun toString(): String {
        return "(r=$r, g=$g, b=$b) "
    }
}
