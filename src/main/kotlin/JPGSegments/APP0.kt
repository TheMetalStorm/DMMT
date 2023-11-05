package JPGSegments

import datatypes.BitStream

data class APP0 (val majorRev: UByte, val minorRev: UByte, val densityUnits: UByte, val xDensityHigh: UByte, val xDensityLow: UByte,
            val yDensityHigh: UByte, val yDensityLow: UByte ) {

    val bitStream = BitStream()

    init {
        checkInput()
        bitStream.addByteToStream(arrayListOf(0xff.toUByte(),0xe0.toUByte(), 0x4a.toUByte(), 0x46.toUByte(), 0x49.toUByte(), 0x46.toUByte(), 0x00.toUByte()))
            .addByteToStream(arrayListOf(majorRev, minorRev, densityUnits, xDensityHigh, xDensityLow, yDensityHigh, yDensityLow))
    }

    private fun checkInput(){
        if(!arrayListOf(0x00.toUByte(), 0x01.toUByte(), 0x02.toUByte()).contains(densityUnits)){
            throw Exception("Density Units should be 0, 1 or 2")
        }

        if(xDensityHigh.toInt() == 0 && xDensityLow.toInt() == 0 ){
            throw Exception("xDensity is not allowed to be 0")
        }

        if(yDensityHigh.toInt() == 0 && yDensityLow.toInt() == 0 ){
            throw Exception("yDensity is not allowed to be 0")
        }
    }

    fun getBitStream(): BitStream{
        return bitStream
    }

}