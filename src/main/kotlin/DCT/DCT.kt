package DCT

import datatypes.Channel
import org.ejml.simple.SimpleMatrix
import java.lang.Exception
import kotlin.math.cos
import kotlin.math.sqrt


class DCT{
    companion object {

        val tileSize = 8
        private fun seperateDCT8x8(input: SimpleMatrix): SimpleMatrix {

            val X = input.minus(128.0)
            val N = 8
            var A: SimpleMatrix = SimpleMatrix(N,N)
            for (k in 0..<N ) {
                for (n in 0..<N ) {
                    val C0 =
                        if (k == 0) (1.0/ sqrt(2.0))
                        else 1.0

                    val cosine: Double = cos(((2.0*n)+1.0) * ((k*Math.PI)/(2.0*N)))
                    val newValue: Double= C0 * sqrt(2.0/N) * cosine
                    A[k,n] = newValue
                }
            }

            return A.mult(X).mult(A.transpose())
        }


        fun seperateDCT(data: Channel) : SimpleMatrix{
            check(data)

            val N = data.width
            val result = SimpleMatrix(N, N)
            for (i in 0..<N step tileSize) {
                for (j in 0..<N step tileSize) {

                    val tileChannel = Channel(8,8, Array(tileSize) { row -> Array(tileSize) { col -> data.getValue(j + col,i + row)  } })
                    val tcMatrix = tileChannel.toSimpleMatrix()
                    val dctTile = seperateDCT8x8(tcMatrix)
                    for (row in 0..<tileSize) {
                        for (col in 0..<tileSize) {
                            result[row + i, col + j] = dctTile[row, col]
                        }
                    }
                }
            }
            return result
        }

        fun directDCT(data: Channel): Channel{
            check(data)
            val N = data.width
            var result = Channel(N, N)

            for (i in 0..N step tileSize) {
                for (j in 0..N step tileSize) {
                    val tileChannel = Channel(8,8, Array(tileSize) { row -> Array(tileSize) { col -> data.getValue(j + col,i + row)  } })
                    val dctTile = directDCT8x8(tileChannel)
                    for (row in 0..tileSize) {
                        for (col in 0..tileSize) {
                            result.setValue(j + col, i + row, dctTile.getValue(col,row))
                        }
                    }
                }
            }
            return result
        }
        private fun directDCT8x8(data: Channel): Channel {
            val N = 8
            var result = Channel(N, N)
            for (i in 0..N ) {
                for (j in 0..N ) {
                    val CI: Double =
                        if (i == 0) (1.0/ sqrt(2.0))
                        else 1.0
                    val CJ: Double =
                        if (j == 0) (1.0/ sqrt(2.0))
                        else 1.0
                    val sum = calculateDirectDCTValue(data, i, j, N)
                    val newValue= 0.25 * CI * CJ * sum
                    result.setValue(i, j, newValue)
                }
            }

            return result;
        }
        private fun calculateDirectDCTValue(data: Channel, i: Int, j: Int, N: Int): Double {
            var result = 0.0
            for (x in 0..<N) {
                for (y in 0..<N) {

                    val X = data.getValue(x, y) - 128.0
                    val firstCos: Double  = cos( ((2.0*x.toDouble()+1.0) * i.toDouble() * Math.PI) /16.0)
                    val secondCos: Double  = cos( ((2.0*y.toDouble()+1.0) * j.toDouble() * Math.PI) /16.0)

                    val curResult: Double = X * firstCos * secondCos

                    result += curResult

                }
            }
            return result
        }


        fun inverseDirectDCT(data: Channel): Channel{
            check(data)
            val N = data.width
            var result = Channel(N, N)

            for (i in 0..<N step tileSize) {
                for (j in 0..<N step tileSize) {
                    val tileChannel = Channel(8,8, Array(tileSize) { row -> Array(tileSize) { col -> data.getValue(j + col,i + row)  } })
                    val idctTile = inverseDirectDCT8x8(tileChannel)
                    for (row in 0..<tileSize) {
                        for (col in 0..<tileSize) {
                            result.setValue(j + col, i + row, idctTile.getValue(col,row))
                        }
                    }
                }
            }
            return result
        }

        /**
         * After calculateInverseDirectDCTValue is no further calculation needed therefore the changes in the
         * function structure compared to  calculateDirectDCTValue
         */
        fun inverseDirectDCT8x8(data: Channel): Channel {
            check(data)
            val N = data.width
            var result = Channel(N, N)
            for (x in 0..<N ) {
                for (y in 0..<N ) {
                    val sum = calculateInverseDirectDCTValue(data, x, y, N)
                    result.setValue(x, y, sum + 128.0)
                }
            }
            return result;
        }
        private fun calculateInverseDirectDCTValue(data: Channel, x: Int, y: Int, N: Int): Double {
            var result = 0.0
            for (i in 0..<N) {
                for (j in 0..<N) {
                    val CI =
                        if (i == 0) (1.0/ sqrt(2.0))
                        else 1.0
                    val CJ =
                        if (j == 0) (1.0/ sqrt(2.0))
                        else 1.0
                    val Y = data.getValue(i, j)
                    val firstCos = cos(((2.0*x+1.0)*i*Math.PI)/(2.0*N))
                    val secondCos = cos( (((2.0*y)+1.0) * j * Math.PI)/(2.0*N))

                    val curResult = (2.0/N)*CI*CJ*Y * firstCos * secondCos

                    result += curResult

                }
            }
            return result
        }

        fun araiDct(data: Channel): Array<Double?>{
            var result: Array<Double?> = arrayOfNulls(8)
            var dataMatrix = data.toSimpleMatrix()
            for (i in 0..<7) {
                when (i) {
                    0 -> {
                        dataMatrix[0, i + 1] = dataMatrix.get(0, i) + dataMatrix.get(7, i)
                        dataMatrix[1, i + 1] = dataMatrix.get(1, i) + dataMatrix.get(6, i)
                        dataMatrix[2, i + 1] = dataMatrix.get(2, i) + dataMatrix.get(5, i)
                        dataMatrix[3, i + 1] = dataMatrix.get(3, i) + dataMatrix.get(4, i)
                        dataMatrix[4, i + 1] = dataMatrix.get(3, i) - dataMatrix.get(4, i)
                        dataMatrix[5, i + 1] = dataMatrix.get(2, i) - dataMatrix.get(5, i)
                        dataMatrix[6, i + 1] = dataMatrix.get(1, i) - dataMatrix.get(6, i)
                        dataMatrix[7, i + 1] = dataMatrix.get(0, i) - dataMatrix.get(7, i)
                    }

                    1 -> {
                        dataMatrix[0, i + 1] = dataMatrix.get(0, i) + dataMatrix.get(3, i)
                        dataMatrix[1, i + 1] = dataMatrix.get(1, i) + dataMatrix.get(2, i)
                        dataMatrix[2, i + 1] = dataMatrix.get(1, i) - dataMatrix.get(2, i)
                        dataMatrix[3, i + 1] = dataMatrix.get(1, i) - dataMatrix.get(3, i)
                        dataMatrix[4, i + 1] = -dataMatrix.get(4, i) - dataMatrix.get(5, i)
                        dataMatrix[5, i + 1] = dataMatrix.get(5, i) + dataMatrix.get(6, i)
                        dataMatrix[6, i + 1] = dataMatrix.get(6, i) + dataMatrix.get(7, i)
                        dataMatrix[7, i + 1] = dataMatrix.get(7, i)
                    }

                    2 -> {
                        dataMatrix[0, i + 1] = dataMatrix.get(0, i) + dataMatrix.get(1, i)
                        dataMatrix[1, i + 1] = dataMatrix.get(0, i) - dataMatrix.get(1, i)
                        dataMatrix[2, i + 1] = dataMatrix.get(2, i) + dataMatrix.get(3, i)
                        dataMatrix[3, i + 1] = dataMatrix.get(3, i)
                        dataMatrix[4, i + 1] = dataMatrix.get(4, i)
                        dataMatrix[5, i + 1] = dataMatrix.get(5, i)
                        dataMatrix[6, i + 1] = dataMatrix.get(6, i)
                        dataMatrix[7, i + 1] = dataMatrix.get(7, i)

                    }

                    3 -> {
                        val a1 = cos(4 * Math.PI / 16)
                        val a2 = cos(2 * Math.PI / 16) - cos(6 * Math.PI / 16)
                        val a3 = cos(4 * Math.PI / 16)
                        val a4 = cos(6 * Math.PI / 16) + cos(2 * Math.PI / 16)
                        val a5 = cos(6 * Math.PI / 16)

                        dataMatrix[0, i + 1] = dataMatrix.get(0, i)
                        dataMatrix[1, i + 1] = dataMatrix.get(1, i)
                        dataMatrix[2, i + 1] = dataMatrix.get(2, i) * a1
                        dataMatrix[3, i + 1] = dataMatrix.get(3, i)
                        dataMatrix[4, i + 1] =
                            -(dataMatrix.get(4, i) * a2) - ((dataMatrix.get(4, i) + dataMatrix.get(6, i)) * a5)
                        dataMatrix[5, i + 1] = dataMatrix.get(5, i) * a3
                        dataMatrix[6, i + 1] =
                            (dataMatrix.get(6, i) * a4) - ((dataMatrix.get(4, i) + dataMatrix.get(6, i)) * a5)
                        dataMatrix[7, i + 1] = dataMatrix.get(7, i)
                    }

                    4 -> {
                        dataMatrix[0, i + 1] = dataMatrix.get(0, i)
                        dataMatrix[1, i + 1] = dataMatrix.get(1, i)
                        dataMatrix[2, i + 1] = dataMatrix.get(2, i) + dataMatrix.get(3, i)
                        dataMatrix[3, i + 1] = dataMatrix.get(3, i) - dataMatrix.get(2, i)
                        dataMatrix[4, i + 1] = dataMatrix.get(4, i)
                        dataMatrix[5, i + 1] = dataMatrix.get(5, i) + dataMatrix.get(7, i)
                        dataMatrix[6, i + 1] = dataMatrix.get(6, i)
                        dataMatrix[7, i + 1] = dataMatrix.get(7, i) - dataMatrix.get(5, i)
                    }

                    5 -> {
                        dataMatrix[0, i + 1] = dataMatrix.get(0, i)
                        dataMatrix[1, i + 1] = dataMatrix.get(1, i)
                        dataMatrix[2, i + 1] = dataMatrix.get(2, i)
                        dataMatrix[3, i + 1] = dataMatrix.get(3, i)
                        dataMatrix[4, i + 1] = dataMatrix.get(4, i) + dataMatrix.get(7, i)
                        dataMatrix[5, i + 1] = dataMatrix.get(5, i) + dataMatrix.get(6, i)
                        dataMatrix[6, i + 1] = dataMatrix.get(5, i) - dataMatrix.get(6, i)
                        dataMatrix[7, i + 1] = dataMatrix.get(7, i) - dataMatrix.get(4, i)
                    }

                    6 -> {
                        val s0 = 1 / (2 * sqrt(2.0))
                        val s1 = 1 / (4 * cos(1 * Math.PI / 16))
                        val s2 = 1 / (4 * cos(2 * Math.PI / 16))
                        val s3 = 1 / (4 * cos(3 * Math.PI / 16))
                        val s4 = 1 / (4 * cos(4 * Math.PI / 16))
                        val s5 = 1 / (4 * cos(5 * Math.PI / 16))
                        val s6 = 1 / (4 * cos(6 * Math.PI / 16))
                        val s7 = 1 / (4 * cos(7 * Math.PI / 16))

                        dataMatrix[0, i + 1] = dataMatrix.get(0, i) * s0
                        dataMatrix[1, i + 1] = dataMatrix.get(1, i) * s4
                        dataMatrix[2, i + 1] = dataMatrix.get(2, i) * s2
                        dataMatrix[3, i + 1] = dataMatrix.get(3, i) * s6
                        dataMatrix[4, i + 1] = dataMatrix.get(4, i) * s5
                        dataMatrix[5, i + 1] = dataMatrix.get(5, i) * s1
                        dataMatrix[6, i + 1] = dataMatrix.get(5, i) * s7
                        dataMatrix[7, i + 1] = dataMatrix.get(7, i) * s3

                        result[0] = dataMatrix[0, i + 1]
                        result[1] = dataMatrix[5, i + 1]
                        result[2] = dataMatrix[2, i + 1]
                        result[3] = dataMatrix[7, i + 1]
                        result[4] = dataMatrix[1, i + 1]
                        result[5] = dataMatrix[4, i + 1]
                        result[6] = dataMatrix[3, i + 1]
                        result[7] = dataMatrix[6, i + 1]

                    }

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