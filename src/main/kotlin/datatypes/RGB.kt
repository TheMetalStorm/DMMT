package datatypes

import org.ejml.simple.SimpleMatrix


data class RGB(var r: Int = 0, var g: Int = 0, var b: Int = 0) {

    val yCbCrMatrix = SimpleMatrix(3,3,true,
        0.299, 0.587, 0.114,
        -0.1687, -0.3313, 0.5,
        0.5, -0.4187, -0.0813
    )

    override fun toString(): String {
        return "(r=$r, g=$g, b=$b) "
    }

    fun toYCbCr(): YCbCr {
        val originalRGB = SimpleMatrix(3, 1, true, r.toDouble() / 255, g.toDouble()/ 255, b.toDouble()/ 255)
        val addMatrix = SimpleMatrix(3, 1, true,0.0,.5,.5)
        val newPixel =  yCbCrMatrix.mult(originalRGB).plus(addMatrix)
        return YCbCr(newPixel[0], newPixel[1], newPixel [2])

    }
}

