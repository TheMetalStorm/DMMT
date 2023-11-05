package JPGSegments

import datatypes.BitStream
data class SOF0 (val dataAccuracy:UByte,val pictureSizeYHigh: UByte,val pictureSizeYLow: UByte,val pictureSizeXHigh: UByte,
            val pictureSizeXLow: UByte,val componentCount: UByte, val components: ArrayList<UByte>){

    private var bitStream = BitStream()
    init {
        checkInput()

        val length : UShort = (8+componentCount.toInt()*3).toUShort();
        val lengthLow = length.toUByte()
        val lengthHigh = (length.toInt() shr 8).toUByte()

        bitStream.addBitStream(BitStream(arrayListOf(0xff.toUByte(),0xc0.toUByte(), lengthHigh, lengthLow,
            dataAccuracy, pictureSizeYHigh, pictureSizeYLow, pictureSizeXHigh, pictureSizeXLow, componentCount)))
        bitStream.addByteToStream(components)
    }

    private fun checkInput() {
        if (this.componentCount.toInt() != 3 && componentCount.toInt() != 1) {
            throw Exception("componentCount has to be 1 or 3")
        }
        if ((componentCount).toInt() * 3 != components.size) {
            throw Exception("componentCount*3 has to be the same size as components")
        }
        if(pictureSizeXLow.toInt() <= 0 && pictureSizeXHigh.toInt() <= 0){
            throw Exception("pictureSizeX has to be greater than 0")
        }

        if(pictureSizeYHigh.toInt() <= 0 && pictureSizeYLow.toInt() <= 0){
            throw Exception("pictureSizeY has to be greater than 0")
        }

        if (dataAccuracy.toInt() != 8 && dataAccuracy.toInt() != 12 && dataAccuracy.toInt() != 16) {
            throw Exception("dataAccuracy has to be 8, 12, 16")
        }
    }

    fun getBitStream(): BitStream{
        return bitStream
    }
}


