package JPGSegments

import datatypes.BitStream

data class SOS (val componentCount: Int, val components: ArrayList<UByte>) {

    private var bitStream = BitStream()
    init {
        checkInput()

        val length : UShort = (6+componentCount*2).toUShort();
        val lengthLow = length.toUByte()
        val lengthHigh = (length.toInt() shr 8).toUByte()

        bitStream.addBitStream(
            BitStream(arrayListOf(0xffu, 0xdau, lengthHigh, lengthLow)))
        bitStream.addByteToStream(components)
        bitStream.addBitStream(BitStream(arrayListOf(0x00u, 0x3fu, 0x00u)))
        checkFor0xff(bitStream)
    }
fun checkInput(){
    if(componentCount.toInt()<1||componentCount.toInt()>4){
        throw Exception("componentCount has to be between 1 and 4 inclusive")
    }
}

    fun checkFor0xff(bitStream: BitStream) :BitStream{
        var byteList = bitStream.getAllBytes()

            val indexHits = ArrayList<Int>()
            for (i in 0..<byteList.size) {
                if (byteList[i] == 0xff.toUByte()) {
                    indexHits.add(i)
                }
            }
            var indexHitsUsed = 0
            for (index in indexHits) {
                val bitStreamBack = BitStream()
                val bitStreamFront = BitStream()
                val byteListSize = byteList.size
                for (i in 0..<byteListSize+indexHitsUsed) {
                    bitStreamFront.addByteToStream(byteList[i])
                    if(i>=index){
                        bitStreamBack.addByteToStream(byteList[i])
                }
                    indexHitsUsed++
                    bitStreamFront.addByteToStream(0x00.toUByte()).addBitStream(bitStreamBack)
                    byteList = bitStreamFront.getAllBytes()
                }

            }
        return BitStream(byteList)
        }
    fun getBitStream(): BitStream{
        return bitStream
    }
    }
