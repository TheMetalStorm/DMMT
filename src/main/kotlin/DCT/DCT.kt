package DCT

import datatypes.Channel
import org.ejml.simple.SimpleMatrix
import java.lang.Exception
import kotlin.math.cos
import kotlin.math.sqrt


class DCT{
    companion object {

        const val tileSize = 8
        private fun seperateDCT8x8(input: SimpleMatrix): SimpleMatrix {

            var X = input
            X = X.minus(128.0)
            val N = 8
            val A: SimpleMatrix = SimpleMatrix(N,N)
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

        fun directDCT(data: Channel){
            check(data)
            val N = data.width

            for (i in 0..<N step tileSize) {
                for (j in 0..<N step tileSize) {
                    val tileChannel = Channel(8,8, Array(tileSize) { row -> Array(tileSize) { col -> data.getValue(j + col,i + row)  } })
                    val dctTile = directDCT8x8(tileChannel)
                    for (row in 0..<tileSize) {
                        for (col in 0..<tileSize) {
                            data.setValue(j + col, i + row, dctTile.getValue(col,row))
                        }
                    }
                }
            }
        }
        private fun directDCT8x8(data: Channel): Channel {
            val N = 8
            val sqrt2 = 1.41421356237
            val result = Channel(N, N)
            for (i in 0..<N ) {
                for (j in 0..<N ) {
                    val CI: Double =
                        if (i == 0) (1.0/ sqrt2)
                        else 1.0
                    val CJ: Double =
                        if (j == 0) (1.0/ sqrt2)
                        else 1.0
                    val sum = calculateDirectDCTValue(data, i, j)
                    val newValue= 0.25 * CI * CJ * sum
                    result.setValue(i, j, newValue)
                }
            }

            return result;
        }
        private fun calculateDirectDCTValue(data: Channel, i: Int, j: Int): Double {
            var result = 0.0
            for (x in 0..<tileSize) {
                for (y in 0..<tileSize) {

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
            val result = Channel(N, N)

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
            val result = Channel(N, N)
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

        fun araiDct1D(data: Array<Array<Double>>): Array<Array<Double>> {

            val a1 = cos(4 * Math.PI / 16)
            val a2 = cos(2 * Math.PI / 16) - cos(6 * Math.PI / 16)
            val a3 = cos(4 * Math.PI / 16)
            val a4 = cos(6 * Math.PI / 16) + cos(2 * Math.PI / 16)
            val a5 = cos(6 * Math.PI / 16)
            val s0 = 1 / (2 * sqrt(2.0))
            val s1 = 1 / (4 * cos(1 * Math.PI / 16))
            val s2 = 1 / (4 * cos(2 * Math.PI / 16))
            val s3 = 1 / (4 * cos(3 * Math.PI / 16))
            val s4 = 1 / (4 * cos(4 * Math.PI / 16))
            val s5 = 1 / (4 * cos(5 * Math.PI / 16))
            val s6 = 1 / (4 * cos(6 * Math.PI / 16))
            val s7 = 1 / (4 * cos(7 * Math.PI / 16))

            for(i in 0.. 7)
            {
                val b0 = data[0 ][ i] + data[7 ][i];
                val b1 = data[1 ][ i] + data[6 ][i];
                val b2 = data[2 ][ i] + data[5 ][i];
                val b3 = data[3 ][ i] + data[4 ][i];
                val b4 = data[3 ][ i] - data[4 ][i];
                val b5 = -data[5 ][ i] + data[2 ][ i];
                val b6 = -data[6 ][ i] + data[1 ][ i];
                val b7 = -data[7 ][ i] + data[0 ][ i];

                val c0 = b0 + b3;
                val c1 = b1 + b2;
                val c2 = -b2 + b1;
                val c3 = -b3 + b0;
                val c4 = -b4 - b5;
                val c5 = b5 + b6;
                val c6 = b6 + b7;
                val c7 = b7;

                val d0 = c0 + c1;
                val d1 = -c1 + c0;
                val d2 = c2 + c3;
                val d3 = c3;
                val d4 = c4;
                val d5 = c5;
                val d6 = c6;
                val d7 = c7;
                val d8 = (d4 + d6) * a5;

                val e0 = d0;
                val e1 = d1;
                val e2 = d2 * a1;
                val e3 = d3;
                val e4 = -d4 * a2 - d8;
                val e5 = d5 * a3;
                val e6 = d6 * a4 - d8;
                val e7 = d7;

                val f0 = e0;
                val f1 = e1;
                val f2 = e2 + e3;
                val f3 = e3 - e2;
                val f4 = e4;
                val f5 = e5 + e7;
                val f6 = e6;
                val f7 = e7 - e5;

                val g0 = f0;
                val g1 = f1;
                val g2 = f2;
                val g3 = f3;
                val g4 = f4 + f7;
                val g5 = f5 + f6;
                val g6 = -f6 + f5;
                val g7 = f7 - f4;

                data[0 ][ i] = g0 * s0;
                data[4 ][ i] = g1 * s4;
                data[2 ][ i] = g2 * s2;
                data[6 ][ i] = g3 * s6;
                data[5 ][ i] = g4 * s5;
                data[1 ][ i] = g5 * s1;
                data[7 ][ i] = g6 * s7;
                data[3 ][ i] = g7 * s3;


        }

            for(i in 0.. 7)
            {
                val b0 = data[i ][ 0]  + data[i ][ 7];
                val b1 = data[i ][ 1]  + data[i ][ 6];
                val b2 = data[i ][ 2]  + data[i ][ 5];
                val b3 = data[i ][ 3]  + data[i ][ 4];
                val b4 =-data[i ][ 4]  + data[i ][ 3];
                val b5 =-data[i ][ 5]  + data[i ][ 2];
                val b6 =-data[i ][ 6]  + data[i ][ 1];
                val b7 =-data[i ][ 7]  + data[i ][ 0] ;

                val c0 = b0 + b3;
                val c1 = b1 + b2;
                val c2 =-b2 + b1;
                val c3 =-b3 + b0;
                val c4 =-b4 - b5;
                val c5 = b5 + b6;
                val c6 = b6 + b7;
                val c7 = b7;

                val d0 = c0 + c1;
                val d1 =-c1 + c0;
                val d2 = c2 + c3;
                val d3 = c3;
                val d4 = c4;
                val d5 = c5;
                val d6 = c6;
                val d7 = c7;
                val d8 = (d4+d6) * a5;

                val e0 = d0;
                val e1 = d1;
                val e2 = d2 * a1;
                val e3 = d3;
                val e4 = -d4 * a2 - d8;
                val e5 = d5 * a3;
                val e6 = d6 * a4 - d8;
                val e7 = d7;

                val f0 = e0;
                val f1 = e1;
                val f2 = e2 + e3;
                val f3 = e3 - e2;
                val f4 = e4;
                val f5 = e5 + e7;
                val f6 = e6;
                val f7 = e7 - e5;

                val g0 = f0;
                val g1 = f1;
                val g2 = f2;
                val g3 = f3;
                val g4 = f4 + f7;
                val g5 = f5 + f6;
                val g6 = -f6 + f5;
                val g7 = f7 - f4;

                data[i][0] = g0 * s0;
                data[i][4] = g1 * s4;
                data[i][2] = g2 * s2;
                data[i][6] = g3 * s6;
                data[i][5] = g4 * s5;
                data[i][1] = g5 * s1;
                data[i][7] = g6 * s7;
                data[i][3] = g7 * s3;


            }

            return data
        }

        //TODO: we get weird values when result should be 0, otherwise the results seem fine?
        fun araiDct2D(data: Channel): Channel{
            var matrix: Array<Array<Double>> = Array(8){ Array(8){ 0.0}}
            var dataCopy = Channel(8,8)
            for (y in 0 .. 7) {
                for (x in 0..7) {
                    dataCopy.setValue(x, y, data.getValue(x, y) - 128)
                }
            }

            matrix = araiDct1D(dataCopy.data)

//            var transposed: Array<Array<Double>> = Array(8){ Array(8){ 0.0}}
//            for (y in 0 .. 7) {
//                for (x in 0 .. 7) {
//                    transposed[x][y] = matrix[y][x]
//                }
//            }
//            for (y in 0 .. 7) {
//                transposed[y] = araiDct1D(transposed[y])
//            }
//            for (y in 0 .. 7) {
//                for (x in 0 .. 7) {
//                    matrix[x][y] = transposed[y][x]
//                }
//            }
            return Channel(8,8, matrix)
        }

        private fun check(data: Channel) {
            val width = data.width
            val height = data.height
            if(width % 8 != 0){
                throw Exception("Error: DCT expects width/height to be divisible by 8, w: ${width} h: ${height}")
            }
            if(height % 8 != 0){
                throw Exception("Error: DCT expects width/height to be divisible by 8, w: ${width} h: ${height}")
            }
        }

    }
}