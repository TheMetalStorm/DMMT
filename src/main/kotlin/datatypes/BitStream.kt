package datatypes

import java.io.PrintWriter

/**
 * This structure can be created with given byte. Attention:
 * for more byte control after creation -> use no parameter at creation
 */
data class BitStream (private var values: ArrayList<UByte> = arrayListOf(UByte.MIN_VALUE), var byteInsertIndex: Int =0){
    fun addToList(valuesToAdd: ArrayList<Int>) {
        checkNumbersToParse(valuesToAdd)
        for (value in valuesToAdd) {
            addToList(value)
        }
    }

    fun addToList(intToAdd: Int) {
        if(byteInsertIndex > 7){
            values.add((intToAdd shl 7).toUByte())
            byteInsertIndex = 1
        } else {
            values[values.size - 1] = modifyByteAfterLastRelevantBit(getLastByte(), intToAdd)
            byteInsertIndex++
        }
    }

    private fun  getLastByte(): UByte{
        return values.last
    }

    fun getByte(index: Int): UByte{
        return values[index];
    }

    private fun modifyByteAfterLastRelevantBit(byteToModify: UByte, bitToAdd: Int): UByte {
        var result: Int = byteToModify.toInt()
        result = calculateModifiedByte(result, bitToAdd)
        return result.toUByte()
    }

    private fun calculateModifiedByte(byteToModify: Int, bit: Int): Int {
        val shiftToRelevantBit = 7 - byteInsertIndex
        return byteToModify or (bit shl shiftToRelevantBit)
    }

    private fun checkNumbersToParse(valuesToAdd: ArrayList<Int>) {
        for (value in valuesToAdd)
        {
            require(value == 0 || value == 1) { "Not every given value is 0 or 1. Please check your data"}
        }
    }

    fun print() {
        for (byte in values) {
            val st: String = byte.toBinaryString()
            print("[$st] ")
        }
        
    }

    fun saveToFile(fileName: String){
        val writer = PrintWriter(fileName)
        for (byte in values) {

            writer.append(byte.toBinaryString())
        }
        writer.close()
    }

    private fun UByte.toBinaryString(): String{
        var result = this.toString(2)

        while(result.length < 8){
            result = "0$result"

        }
        return result;
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BitStream

        if (values != other.values) return false
        if (byteInsertIndex != other.byteInsertIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = values.hashCode()
        result = 31 * result + byteInsertIndex
        return result
    }

}
