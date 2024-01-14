import DCT.DCT
import JPGSegments.*
import datatypes.BitStream
import datatypes.ImageRGB
import datatypes.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.ejml.simple.SimpleMatrix
import java.math.BigDecimal
import java.util.HashMap
import java.util.zip.CRC32

class JPG {
    companion object {

        fun encode(imgData: ImageRGB) {
            var colorConvertedData = imgData.toYCbCr()
            val subsampledData = colorConvertedData.subsample(4, 2, 0)

            var y = subsampledData.getChannel(0)
            var cb = subsampledData.getChannel(1)
            var cr = subsampledData.getChannel(2)
            val channels = listOf(y, cb, cr)
            val quantMatrix = TODO()

            val allRunlengthCodedLuminanceData: ArrayList<Pair<Int, Int>>
            val allRunlengthCodedCbData: ArrayList<Pair<Int, Int>>
            val allRunlengthCodedCrData: ArrayList<Pair<Int, Int>>
            val dcListY: ArrayList<Int>
            val dcListCb: ArrayList<Int>
            val dcListCr: ArrayList<Int>

            var zickZacked: SimpleMatrix
            val NW = imgData.w
            val NH = imgData.h

            for((channelNum, channel) in channels.withIndex()){

                val dctChannel = DCT.seperateDCT(channel)
                val quantizedChannel = DCT.quantize(dctChannel, quantMatrix)
                zickZacked: SimpleMatrix = TODO()



                //TODO: first loop over all 8x8, get DC components -> dcList


                runBlocking {
                    for (i in 0..<NH step DCT.tileSize) {
                        for (j in 0..<NW step DCT.tileSize) {
                            launch(Dispatchers.Default) {
                                val dcVal: Double = zickZacked.extractMatrix(i, i + DCT.tileSize, j, j + DCT.tileSize).get(0,0)
                                if(channelNum == 0) dcListY.add(dcVal.toInt())
                                else if(channelNum == 1) dcListCb.add(dcVal.toInt())
                                else dcListCr.add(dcVal.toInt())
                            }
                        }
                    }
                }
            }

            for((channelNum, channel) in channels.withIndex()){

                var dcDiffList =
                if(channelNum == 0) DCT.dcDifference(dcListY)
                else if (channelNum == 1) DCT.dcDifference(dcListCb)
                else  DCT.dcDifference(dcListCr)


                runBlocking {
                    for (i in 0..<NH step DCT.tileSize) {
                        for (j in 0..<NW step DCT.tileSize) {
                            launch(Dispatchers.Default) {
                                val curTile = zickZacked.extractMatrix(i, i + DCT.tileSize, j, j + DCT.tileSize)
                                val codedTile: Pair<BitStream, ArrayList<Pair<Int, Int>>> = DCT.runlengthCoding(curTile)

                                val acCoefficient = codedTile.first
                                val dcCoefficient = 

                                if(channelNum == 0) allRunlengthCodedLuminanceData.addAll(codedTile.second)
                                else if (channelNum == 1) allRunlengthCodedCbData.addAll(codedTile.second)
                                else  allRunlengthCodedCrData.addAll(codedTile.second)


                            }
                        }
                    }
                }
            }
            val tableACY = createACYTable(allRunlengthCodedLuminanceData)
            val tableACChroma = createACCbCrTable(allRunlengthCodedCbData, allRunlengthCodedCrData)
            val tableDCY = createDCYTable(dcListY)
            val tableDCChroma = createDCCbCrTable(dcListCb, dcListCr)


            //TODO Quanticise

            //TODO Zick-Zack sorting
            var matrix: SimpleMatrix = TODO()
            val zickZackSorted: ArrayList<UByte> = DQT.simpleMatrixToUByte(matrix)

            //TODO lauflängen codierung
            //TODO Huffmann Tabellen AC/DC, Y/CbCr
                var bitStreamToBuild = BitStream()
            bitStreamToBuild.addByteToStream(arrayListOf(0xffu, 0xd8u))
            bitStreamToBuild.addBitStream(APP0(1.toUByte(),1.toUByte(),0.toUByte(),0x0048u, 0x0048u,0.toUByte(),0.toUByte()).getBitStream())

            bitStreamToBuild.addBitStream(DQT(DQT.simpleMatrixToUByte(DQT.luminanceQuantTable()), 0, 0).getBitStream())
            bitStreamToBuild.addBitStream(DQT(DQT.simpleMatrixToUByte(DQT.chrominanceQuantTable()), 1, 0).getBitStream())
            var sizeXHigh = TODO()
            var sizeYHigh = TODO()
            var sizeXLow = TODO()
            var sizeYLow = TODO()
            var components:ArrayList<UByte> = TODO()
            bitStreamToBuild.addBitStream(SOF0(8.toUByte(),sizeYHigh, sizeYLow, sizeXHigh, sizeYLow, components.size().toUByte(), components).getBitStream())
            bitStreamToBuild.addBitStream(DHT(TODO(), TODO(), TODO()).getBitStream())
            //TODO SOS + Bilddaten
            var lenghtHigh = TODO()
            var lenghtLow = TODO()
            bitStreamToBuild.addBitStream(SOS(lenghtHigh, lenghtLow, TODO()).getBitStream())
        }


    fun createACYTable(data: ArrayList<Pair<Int, Int>>): HashMap<Int, BitStream> {
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

    fun createDCYTable(data: ArrayList<Int>): HashMap<Int, BitStream> {
        val huffman = Huffman()
        val (_, result) = huffman.encode(data.toIntArray())
        return result
    }

    fun createDCCbCrTable(dataCb: ArrayList<Int>, dataCr: ArrayList<Int>): HashMap<Int, BitStream> {
        var huffman = Huffman()
        val (_, result) = huffman.encode(dataCb.toIntArray() + dataCr.toIntArray())
        return result
    }
}
}