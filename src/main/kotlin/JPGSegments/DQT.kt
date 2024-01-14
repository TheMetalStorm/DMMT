package JPGSegments

import datatypes.BitStream
import org.ejml.simple.SimpleMatrix

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
    companion object {
        fun luminanceQuantTable(): SimpleMatrix {
            return SimpleMatrix(
                8, 8, true,
                16.0, 11.0, 10.0, 16.0, 24.0, 40.0, 51.0, 61.0,
                12.0, 12.0, 14.0, 19.0, 26.0, 58.0, 60.0, 55.0,
                14.0, 13.0, 16.0, 24.0, 40.0, 57.0, 69.0, 56.0,
                14.0, 17.0, 22.0, 29.0, 51.0, 87.0, 80.0, 62.0,
                18.0, 22.0, 37.0, 56.0, 68.0, 109.0, 103.0, 77.0,
                24.0, 35.0, 55.0, 64.0, 81.0, 104.0, 113.0, 92.0,
                49.0, 64.0, 78.0, 87.0, 103.0, 121.0, 120.0, 101.0,
                72.0, 92.0, 95.0, 98.0, 112.0, 100.0, 103.0, 99.0,
            )
        }

        fun chrominanceQuantTable(): SimpleMatrix {
            return SimpleMatrix(
                8, 8, true,
                17.0, 18.0, 24.0, 47.0, 99.0, 99.0, 99.0, 99.0,
                18.0, 21.0, 26.0, 66.0, 99.0, 99.0, 99.0, 99.0,
                24.0, 26.0, 56.0, 99.0, 99.0, 99.0, 99.0, 99.0,
                47.0, 66.0, 99.0, 99.0, 99.0, 99.0, 99.0, 99.0,
                99.0, 99.0, 99.0, 99.0, 99.0, 99.0, 99.0, 99.0,
                99.0, 99.0, 99.0, 99.0, 99.0, 99.0, 99.0, 99.0,
                99.0, 99.0, 99.0, 99.0, 99.0, 99.0, 99.0, 99.0,
                99.0, 99.0, 99.0, 99.0, 99.0, 99.0, 99.0, 99.0,
            )
        }
        fun simpleMatrixToUByte(matrix:SimpleMatrix): ArrayList<UByte>{
            val ubyteList = ArrayList<UByte>()
           for(col in 0 .. matrix.numCols){
               for(row in 0 .. matrix.numCols){
                   ubyteList.add(matrix.get(col, row).toInt().toUByte())
               }
           }
            return ubyteList
        }
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

