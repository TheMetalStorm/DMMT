package DCT

import datatypes.Channel
import java.lang.Exception
import kotlin.math.cos
import kotlin.math.sqrt

class DCT{
    companion object {
        fun directDCT(data: Channel): Channel {
            check(data)
            val N = data.width
            var result = Channel(N, N)
            for (i in 0..<N ) {
                for (j in 0..<N ) {
                    val CI =
                        if (i == 0) (1/ sqrt(2.0))
                        else 1.0
                    val CJ =
                        if (j == 0) (1/ sqrt(2.0))
                        else 1.0
                    val sum = calculateDirectDCTValue(data, i, j, N)

                    val newValue= (2.0/N) * CI * CJ * sum
                    result.setValue(i, j, newValue)
                }
            }

            return result;
        }

        private fun calculateDirectDCTValue(data: Channel, i: Int, j: Int, N: Int): Double {
            var result = 1.0
            for (x in 0..<N) {
                for (y in 0..<N) {

                    val X = data.getValue(x, y) - 128
                    val firstCos = cos( (((2*x)+1) * i * Math.PI)/(2*N))
                    val secondCos = cos( (((2*y)+1) * j * Math.PI)/(2*N))

                    val curResult = X * firstCos * secondCos

                    result += curResult

                }
            }
            return result
        }

        private fun check(data: Channel) {
            val width = data.width
            val height = data.height
            if(width != height) {
                throw Exception("Error: DCT expects width and height to be the same, w: ${width} h: ${height}")
            }
            if(width % 8 != 0){
                throw Exception("Error: DCT expects width/height to be divisible by 8, w: ${width} h: ${height}")
            }
        }

    }
}