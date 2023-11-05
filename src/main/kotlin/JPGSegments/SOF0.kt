package JPGSegments

import datatypes.BitStream
class SOF0 (dataAccuracy:UByte, pictureSizeY: ArrayList<UByte>, pictureSizeX: ArrayList<UByte>, componentCount: UByte,
            components: ArrayList<UByte>){

    private var bitStream = BitStream()
    init {
        checkInput(pictureSizeX, pictureSizeY,dataAccuracy, componentCount, components)
        bitStream.addBitStream(BitStream(arrayListOf(0xff.toUByte(),0xc0.toUByte(), (8+componentCount.toInt()*3).toUByte(),
            dataAccuracy)))
        bitStream.addByteToStream(pictureSizeY).addByteToStream(pictureSizeX).addByteToStream(componentCount)
        bitStream.addByteToStream(components)

    }

    private fun checkInput(pictureSizeX: ArrayList<UByte>, pictureSizeY: ArrayList<UByte>,
                           dataAccuracy: UByte, componentCount: UByte, components: ArrayList<UByte>) {

        if (componentCount.toInt() != 3 && componentCount.toInt() != 1) {
            throw Exception("componentCount has to be 1 or 3")
        }
        if ((componentCount).toInt() * 3 != components.size) {
            throw Exception("componentCount*3 has to be the same size as components")
        }
        if (pictureSizeY.size != 2) {
            throw Exception("pictureSizeY size has to be 2")
        }
        if (pictureSizeY[0].toInt() == 0 && pictureSizeY[1].toInt() == 0) {
            throw Exception("pictureSizeY is not allowed to be 0")
        }

        if (pictureSizeX.size != 2) {
            throw Exception("pictureSizeX size has to be 2")
        }
        if (pictureSizeX[0].toInt() == 0 && pictureSizeX[1].toInt() == 0) {
            throw Exception("pictureSizeX is not allowed to be 0")
        }

        if (dataAccuracy.toInt() != 8 || dataAccuracy.toInt() != 12 || dataAccuracy.toInt() != 16) {
            throw Exception("dataAccuracy has to be 8, 12, 16")
        }
    }
}


