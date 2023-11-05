package datatypes

import java.io.File
import java.io.PrintWriter

/**
 * This structure can be created with given byte. Attention:
 * for more byte control after creation -> use no parameter at creation
 */
data class BitStream (private var values: ArrayList<UByte> = arrayListOf(), var byteInsertIndex: Int =0){
    fun addToList(valuesToAdd: ArrayList<Int>) {
        checkNumbersToParse(valuesToAdd)
        for (value in valuesToAdd) {
            addToList(value)
        }
    }

    /**
     * This methode add a Bitstreams to the end of the Bitstream and changes the byteInsertIndex to the new Bit location
     */
    fun addBitStream(streamToAdd: BitStream) {
            this.values.addAll(streamToAdd.values)
            this.byteInsertIndex = (this.byteInsertIndex + streamToAdd.byteInsertIndex)%7
    }
    /**
     * This method adds a uByteArray to the end of the Bitstream, it does not change existing byte.
     * It only adds and set the new index to 8.
     */
    fun addByteToStream(values: ArrayList<UByte>): BitStream {
        if(values.isEmpty()){
            return this
        }
        this.values.addAll(values)
        byteInsertIndex=0;
        return this
    }
    fun addByteToStream(value: UByte) : BitStream{
        values.add(value)
        return this
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

     fun  getLastByte(): UByte{
        return values.last
    }

    fun getByte(index: Int): UByte{
        return values[index];
    }

    fun getBit(index: Int): Int {
        val byteIndex = index / 8
        val bitIndex = index % 8
        val bit = getByte(byteIndex).toInt() shr (7 - bitIndex) and 1
        return bit
    }

     fun modifyByteAfterLastRelevantBit(byteToModify: UByte, bitToAdd: Int): UByte {
        var result: Int = byteToModify.toInt()
        result = calculateModifiedByte(result, bitToAdd)
        return result.toUByte()
    }

    private fun calculateModifiedByte(byteToModify: Int, bit: Int): Int {
        val shiftToRelevantBit = 7 - byteInsertIndex
        if(bit == 1)
            return byteToModify or (bit shl shiftToRelevantBit)
        return byteToModify and (bit shl shiftToRelevantBit).inv()
    }

    private fun checkNumbersToParse(valuesToAdd: ArrayList<Int>) {
        for (value in valuesToAdd)
        {
            require(value == 0 || value == 1) { "Not every given value is 0 or 1. Please check your data"}
        }
    }

    fun printBits() {
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

    fun saveToFileAsBytes(fileName: String){
        val file = File(fileName)
        file.writeBytes(values.toUByteArray().toByteArray())

    }
     fun UByte.toBinaryString(): String{
        var result = this.toString(2)

        while(result.length < 8){
            result = "0$result"

        }
        return result;
    }

    fun UByte.getBit(position: Int): Int {
        return (this.toInt() shr position) and 1;
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

