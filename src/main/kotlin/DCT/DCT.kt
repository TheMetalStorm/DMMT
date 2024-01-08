package DCT

import HufEncode
import Huffman
import datatypes.BitStream
import datatypes.Channel
import org.ejml.simple.SimpleMatrix
import kotlin.math.cos
import kotlinx.coroutines.*
import java.util.HashMap


class DCT {
    companion object {

        const val tileSize = 8
        const val sqrt2 = 1.41421356237

        private fun zickZackSort(quantizationTable: ArrayList<UByte>): ArrayList<UByte> {
            val sortedTable = ArrayList<UByte>()

            val zickZackOrder = intArrayOf(0, 1, 5, 6, 14, 15, 27, 28,
                2, 4, 7, 13, 16, 26, 29, 42,
                3, 8, 12, 17, 25, 30, 41, 43,
                9, 11, 18, 24, 31, 40, 44, 53,
                10, 19, 23, 32, 39, 45, 52, 54,
                20, 22, 33, 38, 46, 51, 55, 60,
                21, 34, 37, 47, 50, 56, 59, 61,
                35, 36, 48, 49, 57, 58, 62, 63)

            for (i in 0..63) {
                sortedTable.add(quantizationTable[zickZackOrder[i]])
            }

            return sortedTable
        }

        fun quantMatrix50(): SimpleMatrix {
            val result = SimpleMatrix(tileSize, tileSize)
            result.fill(50.0)
            return result
        }

        //TODO: make real
//        fun a(){
//            superduperlisteY: ArrayList<Pair<Int, Int>>>
//            superduperlisteChroma: ArrayList<Pair<Int, Int>>>


//            loop pro 8x8
//                _, liste = rungleghtCodign(block.y)
//                _, listeCb = rungleghtCodign(block.cb)
//                _, listeCr = rungleghtCodign(block.cr)
//                superduperlisteY.addAll(liste)
//                superduperlisteChroma.addAll(listeCb, listeCr)
//            end loop

//            tableACY = createAcYTable(superduperlisteY)
//            tableACChroma = createAcCbCrTable(superduperlisteChroma)
//        }

        fun runlengthCoding(data: SimpleMatrix): Pair<BitStream, ArrayList<Pair<Int, Int>>>  {
            val result = ArrayList<Pair<Int, Int>>()
            var zeroCounter = 0

            var numZeroesAtEnd = 0
            for (n in data.toArray2().toList().flatMap { it.toList() }.reversed()) {
                if (n == 0.0) numZeroesAtEnd++
                else break
            }

            for (i in 1..63 - numZeroesAtEnd) {
                val value = data.get(i / 8, i % 8)
                if (value == 0.0) {
                    zeroCounter += 1
                    if (zeroCounter == 16) {
                        result.add(Pair(zeroCounter, value.toInt()))
                        zeroCounter = 0
                    }
                } else {
                    result.add(Pair(zeroCounter, value.toInt()))
                    zeroCounter = 0
                }
            }
            if (numZeroesAtEnd != 0) {
                result.add(Pair(0, 0))
            }

            //result to bit
            var bitResult = BitStream()
            val hufValues = IntArray(result.size)
            for ((index, pair ) in result.withIndex()) {
                val binary = Integer.toBinaryString(pair.second)
                val hufInput = "0x${pair.first}${binary.length}".toInt(radix = 16)
                hufValues.set(index, hufInput)
            }

            val huffman = Huffman()
            val (_, symbolToCodeMap) = huffman.encode(hufValues)

            for (pair in result) {
                val binary = Integer.toBinaryString(pair.second)
                val hufVal = symbolToCodeMap.get("0x${pair.first}${binary.length}".toInt(radix = 16))
                    ?: throw Exception("No hufVal was found for pair: $pair")

                bitResult.addBitStreamUntilByteInsertIndex(hufVal)
                binary.toCharArray().forEach { bitResult.addToList(Integer.parseInt(it.toString())) }

            }
            return Pair(bitResult, result)
        }

        fun dcDifference(data: ArrayList<Int>): ArrayList<Pair<Int,String >> {
            var difPairs = ArrayList<Pair<Int,String >>()
            for(date in data) {
                val bitsAsString =  Integer.toBinaryString(date)
                val category: Int = bitsAsString.length

                if(category == 1 && bitsAsString.equals("0")){
                    difPairs.add(Pair(category, ""))
                    continue
                }

                if (date>0){
                    val lowerOrderBits = bitsAsString.takeLast(category)
                    difPairs.add(Pair(category, lowerOrderBits))
                }
                else{
                    var stringBitMinusOne =  Integer.toBinaryString(date-1)
                    var lowerOrderBits = stringBitMinusOne.takeLast(category)
                    difPairs.add(Pair(category, lowerOrderBits))
                }
            }

            return difPairs
        }
        fun getCategoryHuffmanCodes(data: ArrayList<Int>): HashMap<Int, BitStream> {
            val dataIntArray: IntArray = data.toIntArray()
            val huffman = Huffman()
            val hufResult: HufEncode = huffman.encode(dataIntArray)

            return hufResult.symbolToCodeMap
        }
//TODO: • Erstellen der vier Huffman-Tabellen AC/DC × Y/CbCr
//        Wegen Unterabtastung ist Reihenfolge der kodierten
//        Blöcke wichtig
// 4 8x8 blöcke beachten -> 4 y = 1 CB = 1 CR von pixelbereich
// deswegen erfolgt Ausgabe nach Schema:Y1 Y2 Y3 Y4 Cb Cr
fun createACYTable(data: ArrayList<Pair<Int, Int>>): HashMap<Int, BitStream>{
    val toEncode = data.stream().map {
        val result: Int
        result = if (it.second == 10) {
            10 * (it.first + 1)
        } else {
            Integer.parseInt(it.first.toString() + it.second.toString())
        }
        result

    }.toList().toIntArray()


    val huffman = Huffman()
    val (_, result) = huffman.encode(toEncode)
    return result
}

fun createACCbCrTable(dataCb: ArrayList<Pair<Int, Int>>, dataCr: ArrayList<Pair<Int, Int>>): HashMap<Int, BitStream> {
    val data = dataCb+dataCr
    val toEncode = data.stream().map {
        val result: Int
        result = if (it.second == 10) {
            10 * (it.first + 1)
        } else {
            Integer.parseInt(it.first.toString() + it.second.toString())
        }
        result

    }.toList().toIntArray()


    val huffman = Huffman()
    val (_, result) = huffman.encode(toEncode)
    return result
}

fun createDCYTable(data: ArrayList<Int>): HashMap<Int, BitStream>{
    val huffman = Huffman()
    val (_, result) = huffman.encode(data.toIntArray())
    return result
}

fun createDCCbCrTable(dataCb: ArrayList<Int>, dataCr: ArrayList<Int>): HashMap<Int, BitStream> {
    var huffman = Huffman()
    val (_, result) = huffman.encode(dataCb.toIntArray() + dataCr.toIntArray())
    return result
}



        fun luminanceQuantTable(): SimpleMatrix {
            return SimpleMatrix( 8, 8, true,
                16.0,	11.0,	10.0,	16.0,	24.0,	40.0,	51.0,	61.0,
                12.0,	12.0,	14.0,	19.0,	26.0,	58.0,	60.0,	55.0,
                14.0,	13.0,	16.0,	24.0,	40.0,	57.0,	69.0,	56.0,
                14.0,	17.0,	22.0,	29.0,	51.0,	87.0,	80.0,	62.0,
                18.0,	22.0,	37.0,	56.0,	68.0,	109.0,	103.0,	77.0,
                24.0,	35.0,	55.0,	64.0,	81.0,	104.0,	113.0,	92.0,
                49.0,	64.0,	78.0,	87.0,	103.0,	121.0,	120.0,	101.0,
                72.0,	92.0,	95.0,	98.0,	112.0,	100.0,	103.0,	99.0,
            )
        }

        fun chrominanceQuantTable(): SimpleMatrix {
            return SimpleMatrix( 8, 8, true,
                17.0,	18.0,	24.0,	47.0,	99.0,	99.0,	99.0,	99.0,
                18.0,	21.0,	26.0,	66.0,	99.0,	99.0,	99.0,	99.0,
                24.0,	26.0,	56.0,	99.0,	99.0,	99.0,	99.0,	99.0,
                47.0,	66.0,	99.0,	99.0,	99.0,	99.0,	99.0,	99.0,
                99.0,	99.0,	99.0,	99.0,	99.0,	99.0,	99.0,	99.0,
                99.0,	99.0,	99.0,	99.0,	99.0,	99.0,	99.0,	99.0,
                99.0,	99.0,	99.0,	99.0,	99.0,	99.0,	99.0,	99.0,
                99.0,	99.0,	99.0,	99.0,	99.0,	99.0,	99.0,	99.0,
            )
        }
        fun quantize(data: SimpleMatrix, quantizationMatrix: SimpleMatrix): SimpleMatrix{

            val NW = data.numCols
            val NH = data.numRows

            val result = SimpleMatrix(NH, NW)

            runBlocking {
                for (i in 0..<NH step tileSize) {
                    for (j in 0..<NW step tileSize) {
                        launch(Dispatchers.Default) {
                            val tileChannel = data.extractMatrix(i, i + tileSize, j, j + tileSize)
                            val quantizedTile = quantize8x8(tileChannel, quantizationMatrix)
                            for (row in 0..<tileSize) {
                                for (col in 0..<tileSize) {
                                    result.set(i + row, j + col,  quantizedTile.get(row, col))
                                }
                            }
                        }
                    }
                }
            }
            return result
        }

        private fun quantize8x8(tileChannel: SimpleMatrix, quantizationMatrix: SimpleMatrix): SimpleMatrix {

            val a  = tileChannel.elementDiv(quantizationMatrix)
            //NOTE: disgusting and slow
            val result = SimpleMatrix(tileSize, tileSize)
            for (i in 0..<tileSize) {
                for (j in 0..<tileSize) {
                    result[i, j] = Math.round(a[i, j]).toDouble()
                }
            }
            return result
        }

        private fun seperateDCT8x8(input: SimpleMatrix): SimpleMatrix {

            var X = input
            X = X.minus(128.0)
            val N = 8
            val A: SimpleMatrix = SimpleMatrix(N, N)

            for (k in 0..<N) {
                for (n in 0..<N) {
                    val C0 =
                        if (k == 0) (1.0 / sqrt2)
                        else 1.0

                    val cosine: Double = cos(((2.0 * n) + 1.0) * ((k * Math.PI) / (2.0 * N)))
                    val newValue: Double = C0 * 0.5 * cosine
                    A[k, n] = newValue
                }
            }

            return A.mult(X).mult(A.transpose())
        }


        fun seperateDCT(data: Channel): SimpleMatrix {
            check(data)

            val NW = data.width
            val NH = data.height
            val result = SimpleMatrix(NH, NW)
            runBlocking {

                for (i in 0..<NH step tileSize) {
                    for (j in 0..<NW step tileSize) {
                        launch(Dispatchers.Default) {

                            val tileChannel = Channel(
                                8,
                                8,
                                Array(tileSize) { row -> Array(tileSize) { col -> data.getValue(j + col, i + row) } })
                            val tcMatrix = tileChannel.toSimpleMatrix()
                            val dctTile = seperateDCT8x8(tcMatrix)
                            for (row in 0..<tileSize) {
                                for (col in 0..<tileSize) {
                                    result[row + i, col + j] = dctTile[row, col]
                                }
                            }
                        }
                    }
                }
            }
            return result
        }

        fun directDCT(data: Channel) {
            check(data)
            val NW = data.width
            val NH = data.height

            runBlocking {
                for (i in 0..<NH step tileSize) {
                    for (j in 0..<NW step tileSize) {
                        launch(Dispatchers.Default) {
                            val tileChannel = Channel(
                                8,
                                8,
                                Array(tileSize) { row -> Array(tileSize) { col -> data.getValue(j + col, i + row) } })
                            val dctTile = directDCT8x8(tileChannel)
                            for (row in 0..<tileSize) {
                                for (col in 0..<tileSize) {
                                    data.setValue(j + col, i + row, dctTile.getValue(col, row))
                                }
                            }
                        }
                    }
                }
            }
        }

        private fun directDCT8x8(data: Channel): Channel {
            val N = 8
            val sqrt2 = 1.41421356237
            val result = Channel(N, N)
            for (i in 0..<N) {
                for (j in 0..<N) {
                    val CI: Double =
                        if (i == 0) (1.0 / sqrt2)
                        else 1.0
                    val CJ: Double =
                        if (j == 0) (1.0 / sqrt2)
                        else 1.0
                    val sum = calculateDirectDCTValue(data, i, j)
                    val newValue = 0.25 * CI * CJ * sum
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
                    val firstCos: Double = cos(((2.0 * x.toDouble() + 1.0) * i.toDouble() * Math.PI) / 16.0)
                    val secondCos: Double = cos(((2.0 * y.toDouble() + 1.0) * j.toDouble() * Math.PI) / 16.0)

                    val curResult: Double = X * firstCos * secondCos

                    result += curResult

                }
            }
            return result
        }


        fun inverseDirectDCT(data: Channel): Channel {
            check(data)
            val N = data.width
            val result = Channel(N, N)

            for (i in 0..<N step tileSize) {
                for (j in 0..<N step tileSize) {
                    val tileChannel = Channel(
                        8,
                        8,
                        Array(tileSize) { row -> Array(tileSize) { col -> data.getValue(j + col, i + row) } })
                    val idctTile = inverseDirectDCT8x8(tileChannel)
                    for (row in 0..<tileSize) {
                        for (col in 0..<tileSize) {
                            result.setValue(j + col, i + row, idctTile.getValue(col, row))
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
            for (x in 0..<N) {
                for (y in 0..<N) {
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
                        if (i == 0) (1.0 / sqrt2)
                        else 1.0
                    val CJ =
                        if (j == 0) (1.0 / sqrt2)
                        else 1.0
                    val Y = data.getValue(i, j)
                    val firstCos = cos(((2.0 * x + 1.0) * i * Math.PI) / (2.0 * N))
                    val secondCos = cos((((2.0 * y) + 1.0) * j * Math.PI) / (2.0 * N))

                    val curResult = (2.0 / N) * CI * CJ * Y * firstCos * secondCos

                    result += curResult

                }
            }
            return result
        }

        fun araiDct8x8(data: SimpleMatrix): SimpleMatrix{

            val a1 = cos(4 * Math.PI / 16)
            val a2 = cos(2 * Math.PI / 16) - cos(6 * Math.PI / 16)
            val a3 = cos(4 * Math.PI / 16)
            val a4 = cos(6 * Math.PI / 16) + cos(2 * Math.PI / 16)
            val a5 = cos(6 * Math.PI / 16)
            val s0 = 1 / (2 * sqrt2)
            val s1 = 1 / (4 * cos(1 * Math.PI / 16))
            val s2 = 1 / (4 * cos(2 * Math.PI / 16))
            val s3 = 1 / (4 * cos(3 * Math.PI / 16))
            val s4 = 1 / (4 * cos(4 * Math.PI / 16))
            val s5 = 1 / (4 * cos(5 * Math.PI / 16))
            val s6 = 1 / (4 * cos(6 * Math.PI / 16))
            val s7 = 1 / (4 * cos(7 * Math.PI / 16))

            for(i in 0.. 7)
            {
                val b0 = data.get(0, i) + data.get(7, i)
                val b1 = data.get(1 , i) + data.get(6 ,i)
                val b2 = data.get(2 , i) + data.get(5, i)
                val b3 = data.get(3 , i) + data.get(4, i)
                val b4 = data.get(3 , i) - data.get(4, i)
                val b5 = -data.get(5, i) + data.get(2, i)
                val b6 = -data.get(6, i) + data.get(1, i)
                val b7 = -data.get(7, i) + data.get(0, i)

                val c0 = b0 + b3
                val c1 = b1 + b2
                val c2 = -b2 + b1
                val c3 = -b3 + b0
                val c4 = -b4 - b5
                val c5 = b5 + b6
                val c6 = b6 + b7
                val c7 = b7

                val d0 = c0 + c1
                val d1 = -c1 + c0
                val d2 = c2 + c3
                val d3 = c3
                val d4 = c4
                val d5 = c5
                val d6 = c6
                val d7 = c7
                val d8 = (d4 + d6) * a5

                val e0 = d0
                val e1 = d1
                val e2 = d2 * a1
                val e3 = d3
                val e4 = -d4 * a2 - d8
                val e5 = d5 * a3
                val e6 = d6 * a4 - d8
                val e7 = d7

                val f0 = e0
                val f1 = e1
                val f2 = e2 + e3
                val f3 = e3 - e2
                val f4 = e4
                val f5 = e5 + e7
                val f6 = e6
                val f7 = e7 - e5

                val g0 = f0
                val g1 = f1
                val g2 = f2
                val g3 = f3
                val g4 = f4 + f7
                val g5 = f5 + f6
                val g6 = -f6 + f5
                val g7 = f7 - f4

                data.set(0, i, g0 * s0)
                data.set(4, i, g1 * s4)
                data.set(2, i, g2 * s2)
                data.set(6, i, g3 * s6)
                data.set(5, i, g4 * s5)
                data.set(1, i, g5 * s1)
                data.set(7, i, g6 * s7)
                data.set(3, i, g7 * s3)


        }

            for(i in 0.. 7)
            {
                val b0 = data.get(i , 0)  + data.get(i , 7)
                val b1 = data.get(i , 1)  + data.get(i , 6)
                val b2 = data.get(i , 2)  + data.get(i , 5)
                val b3 = data.get(i , 3)  + data.get(i , 4)
                val b4 =-data.get(i , 4)  + data.get(i , 3)
                val b5 =-data.get(i , 5)  + data.get(i , 2)
                val b6 =-data.get(i , 6)  + data.get(i , 1)
                val b7 =-data.get(i , 7)  + data.get(i , 0);

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

                data.set(i, 0, g0 * s0);
                data.set(i, 4, g1 * s4);
                data.set(i, 2, g2 * s2);
                data.set(i, 6, g3 * s6);
                data.set(i, 5, g4 * s5);
                data.set(i, 1, g5 * s1);
                data.set(i, 7, g6 * s7);
                data.set(i, 3, g7 * s3);


            }

            return data
        }

        fun araiDct2D(data: SimpleMatrix): SimpleMatrix{
            val dataCopy = data.copy().minus(128.0)
            return araiDct8x8(dataCopy)
        }
        fun araiDCT(data: Channel): SimpleMatrix {
            check(data)
            val dataSM = data.toSimpleMatrix()

            val NW = data.width
            val NH = data.height

            val result = SimpleMatrix(NH, NW)
            runBlocking {
                for (i in 0..<NH step tileSize) {
                    for (j in 0..<NW step tileSize) {
                        launch(Dispatchers.Default) {
                            val tileChannel = dataSM.extractMatrix(i, i + tileSize, j, j + tileSize)
                            val dctTile = araiDct2D(tileChannel)
                            result.insertIntoThis(i, j, dctTile)
                        }
                    }
                }
            }
            return result
        }

        private fun check(data: Channel) {
            val width = data.width
            val height = data.height
            if (width % 8 != 0) {
                throw Exception("Error: DCT expects width/height to be divisible by 8, w: ${width} h: ${height}")
            }
            if (height % 8 != 0) {
                throw Exception("Error: DCT expects width/height to be divisible by 8, w: ${width} h: ${height}")
            }
        }

    }
}