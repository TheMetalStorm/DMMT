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
import java.util.SortedMap
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
            val allRunlengthCodedCbData:  ArrayList<Pair<Int, Int>>
            val allRunlengthCodedCrData:  ArrayList<Pair<Int, Int>>

            val dcListY:  HashMap<Int, Int>
            val dcListCb: HashMap<Int, Int>
            val dcListCr: HashMap<Int, Int>

            val encodedYs:  HashMap<Int, BitStream>
            val encodedCbs: HashMap<Int, BitStream>
            val encodedCrs: HashMap<Int, BitStream>

            var zickZacked: SimpleMatrix
            val NW = imgData.w
            val NH = imgData.h

            for((channelNum, channel) in channels.withIndex()){

                val dctChannel = DCT.seperateDCT(channel)
                val quantizedChannel = DCT.quantize(dctChannel, quantMatrix)
                zickZacked = TODO()

                //TODO: when using runBlocking and adding stuff into the list, the list may not be in the order that we expect
                //which would f us later on. MAybe save in hashtable with index as key?
                runBlocking {
                    for (col in 0..<NH step DCT.tileSize) {
                        for (row in 0..<NW step DCT.tileSize) {
                            val index = row*NW+col
                            launch(Dispatchers.Default) {
                                val dcVal: Double = zickZacked.extractMatrix(col, col + DCT.tileSize, row, row + DCT.tileSize).get(0,0)
                                if(channelNum == 0) dcListY.put(index,dcVal.toInt())
                                else if(channelNum == 1) dcListCb.put(index,dcVal.toInt())
                                else dcListCr.put(index,dcVal.toInt())
                            }
                        }
                    }
                }
            }

            for((channelNum, channel) in channels.withIndex()){

                var dcDiffList =
                if(channelNum == 0) DCT.dcDifference(hashMapToArrayList(dcListY))
                else if (channelNum == 1) DCT.dcDifference(hashMapToArrayList(dcListCb))
                else  DCT.dcDifference(hashMapToArrayList( dcListCr))

                //TODO: when using runBlocking and adding stuff into the list, the list may not be in the order that we expect
                //which would f us later on. MAybe save in hashtable with index as key?
                runBlocking {
                    for (i in 0..<NH step DCT.tileSize) {
                        for (j in 0..<NW step DCT.tileSize) {
                            var index = j*NW+i
                            launch(Dispatchers.Default) {
                                val curTile = zickZacked.extractMatrix(i, i + DCT.tileSize, j, j + DCT.tileSize)
                                val codedTile: Pair<BitStream, ArrayList<Pair<Int, Int>>> = DCT.runlengthCoding(curTile)
                                val acCoefficient: BitStream = codedTile.first

                                //TODO: check if index is right
                                val dcCoefficientString = dcDiffList.get(i / DCT.tileSize * NW / DCT.tileSize + j / DCT.tileSize).second
                                val dcCoefficient = BitStream()
                                for(i in 0..dcCoefficientString.length){
                                    if(dcCoefficientString[i] == '1') dcCoefficient.addToList(1)
                                    else dcCoefficient.addToList(0)
                                }

                                dcCoefficient.addBitStreamUntilByteInsertIndex(acCoefficient)

                                if(channelNum == 0) {
                                    allRunlengthCodedLuminanceData.addAll(index,codedTile.second)
                                    encodedYs.put(index,dcCoefficient)
                                }
                                else if (channelNum == 1){
                                    allRunlengthCodedCbData.addAll(index,codedTile.second)
                                    encodedCbs.put(index,dcCoefficient)
                                }
                                else{
                                    allRunlengthCodedCrData.addAll(index,codedTile.second)
                                    encodedCrs.put(index,dcCoefficient)
                                }


                            }
                        }
                    }
                }
            }

            val bitStreamToSave = BitStream()

            for (col in 0..<NH step 2) {
                for (row in 0..<NW step 2) {
                    val Y1 = encodedYs.get(row * NW + col)
                    val Y2 = encodedYs.get(row * NW + (col+1))
                    val Y3 = encodedYs.get((row+1) * NW + col)
                    val Y4 = encodedYs.get((row+1) * NW + (col+1))

                    val Cb = encodedCbs.get(row * NW + col)
                    val Cr = encodedCrs.get(row * NW + col)

                    bitStreamToSave.addBitStream(Y1!!)
                    bitStreamToSave.addBitStream(Y2!!)
                    bitStreamToSave.addBitStream(Y3!!)
                    bitStreamToSave.addBitStream(Y4!!)
                    bitStreamToSave.addBitStream(Cb!!)
                    bitStreamToSave.addBitStream(Cr!!)
                }
            }

            val tableACY = createACYTable(allRunlengthCodedLuminanceData)
            val tableACChroma = createACCbCrTable(allRunlengthCodedCbData, allRunlengthCodedCrData)
            val tableDCY = createDCYTable(hashMapToArrayList(dcListY))
            val tableDCChroma = createDCCbCrTable(hashMapToArrayList(dcListCb), hashMapToArrayList(dcListCr))



            //TODO Quanticise

            //TODO Zick-Zack sorting
            var matrix: SimpleMatrix = TODO()
            val zickZackSorted = DCT.zickZackSort(matrix)

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
            val SOScomponents: ArrayList<UByte> = arrayListOf(0x01u, TODO(),0x02u, TODO(),0x03u, TODO())
            bitStreamToBuild.addBitStream(SOS(3, SOScomponents).getBitStream())

            bitStreamToBuild.addBitStream(bitStreamToSave)
            bitStreamToBuild.addByteToStream(arrayListOf(0xffu, 0xd9u))
        }





        fun <V> hashMapToArrayList(hashMap: HashMap<Int, V>): ArrayList<V> {
            val arrayList = ArrayList<V>()
            for (i in 0..<hashMap.size) {
                hashMap[i]?.let { arrayList.add(it) }
            }
            return arrayList
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