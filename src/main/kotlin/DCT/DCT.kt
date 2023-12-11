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

            for (i in 0..<N step tileSize) {
                for (j in 0..<N step tileSize) {
                    val tileChannel = Channel(8,8, Array(tileSize) { row -> Array(tileSize) { col -> data.getValue(j + col,i + row)  } })
                    val dctTile = directDCT8x8(tileChannel)
                    for (row in 0..<tileSize) {
                        for (col in 0..<tileSize) {
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
            for (i in 0..<N ) {
                for (j in 0..<N ) {
                    val CI =
                        if (i == 0) (1.0/ sqrt(2.0))
                        else 1.0
                    val CJ =
                        if (j == 0) (1.0/ sqrt(2.0))
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
                    result.setValue(x, y, sum + 128)
                }
            }
            return result;
        }
        private fun calculateInverseDirectDCTValue(data: Channel, x: Int, y: Int, N: Int): Double {
            var result = 0.0
            for (i in 0..<N) {
                for (j in 0..<N) {
                    //TODO: Test and check if logic for CI and CJ are correct (big ???)
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

        fun araiDct(data: Channel): SimpleMatrix {
         var result  = data.toSimpleMatrix()
           for(i in 0..<8){
               when(i){
                   0-> {
                       result[i,1] = result.get(0, i) + result.get(7,i)
                       result[1,1] = result.get(1, i) + result.get(6,i)
                       result[2,1] = result.get(3, i) - result.get(4,i)
                       result[3,1] = result.get(1, i) - result.get(6,i)
                       result[4,1] = result.get(2, i) + result.get(5,i)
                       result[5,1] = result.get(3, i) + result.get(4,i)
                       result[6,1] = result.get(2, i) - result.get(5,i)
                       result[7,1] = result.get(0, i) + result.get(7,i)
                   }
                   1->{
                       result[i,2] = result.get(0, i) + result.get(5,i)
                       result[1,2] = result.get(1, i) - result.get(4,i)
                       result[2,2] = result.get(2, i) - result.get(6,i)
                       result[3,2] = result.get(1, i) - result.get(4,i)
                       result[4,2] = result.get(0, i) - result.get(5,i)
                       result[5,2] = result.get(3, i) + result.get(7,i)
                       result[6,2] = result.get(3, i) + result.get(6,i)
                       result[7,2] = result.get(7,i)
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