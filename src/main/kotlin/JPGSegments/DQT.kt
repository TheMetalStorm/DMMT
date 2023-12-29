package JPGSegments

import datatypes.BitStream

data class DQT (val quantizationTable: ArrayList<UByte>, val quantizationTableNum: Int, val quantizationTablePrecision: Int){
    private var bitStream = BitStream()

    init {
        checkInput()
        val length =  quantizationTable.size + 1 + 2
        val lengthLow = length.toUByte()
        val lengthHigh = (length shr 8).toUByte()

        var quantTableInfo: UInt = 0u;
        quantTableInfo = quantTableInfo or quantizationTableNum.toUInt()
        quantTableInfo = quantTableInfo or ((quantizationTablePrecision shl 4).toUInt())


        bitStream.addByteToStream(arrayListOf(0xffu, 0xdbu, lengthHigh, lengthLow, quantTableInfo.toUByte()))

        val sortedTable = zickZackSort(quantizationTable)
        bitStream.addByteToStream(sortedTable)
    }

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

    private fun checkInput() {
        if(!listOf(0).contains(quantizationTablePrecision)){
            throw Exception("Quantization Table Precision should be 0 or 1, we only allow 0")
        }

        if(quantizationTable.size != 64){
            throw Exception("There should be 64 values in the Quantization Table")
        }
    }

    fun getBitStream(): BitStream{
        return bitStream
    }
}

