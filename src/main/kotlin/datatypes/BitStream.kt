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

    fun getAllBytes (): ArrayList<UByte>{
        return values
    }

    /**
     * This methode adds the contents of a Bitstreams to the end of our Bitstream
     * by adding single bytes until we hit the byteInsertIndex in the last byte
     */
    fun addBitStreamUntilByteInsertIndex(streamToAdd: BitStream) {


        for ((index, uByte) in streamToAdd.values.withIndex()) {
            if(index != streamToAdd.values.size-1){
                for(j in (0..7))
                this.addToList(uByte.getBit(j))
            }
            else{
                if(streamToAdd.byteInsertIndex == 0)
                    return
                for(j in (0..<streamToAdd.byteInsertIndex)){
                    this.addToList(uByte.getBit(j))
                }
            }
        }
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
        byteInsertIndex=8;
        return this
    }
    fun addByteToStream(value: UByte) : BitStream{
        values.add(value)
        byteInsertIndex=8;
        return this
    }
    fun addToList(intToAdd: Int) {
        if(values.size == 0){
            values.add(UByte.MIN_VALUE)
        }
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

    //COMMENT: MESSES WITH TIME CONTINUUM (ByteInsertIndex)
    fun setBit(index: Int, bitToAdd: Int) {
        if(values.size == 0){
            values.add(0u)
        }
        val byteIndex = index / 8
        val bitIndex = index % 8

        while(values.size-1 < byteIndex){
            values.add(0u)
        }

        values[byteIndex] = calculateModifiedByte(values[byteIndex].toInt(), bitIndex, bitToAdd).toUByte()

    }
    fun revert(){
        if (byteInsertIndex == 0){
            return
        }
        if (byteInsertIndex % 7 == 1 && byteInsertIndex>7){
            values.removeLast()
            byteInsertIndex = 7
        }
        else{
            byteInsertIndex--
        }
    }

     fun modifyByteAfterLastRelevantBit(byteToModify: UByte, bitToAdd: Int): UByte {
        var result: Int = byteToModify.toInt()
        result = calculateModifiedByte(result, byteInsertIndex, bitToAdd)
        return result.toUByte()
    }

    private fun calculateModifiedByte(byteToModify: Int, bitToModify: Int, bit: Int): Int {
        val shiftToRelevantBit = 7 - bitToModify
        if(bit == 1)
            return byteToModify or (1 shl shiftToRelevantBit)
        return byteToModify and (1 shl shiftToRelevantBit).inv()
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
        return (this.toInt() shr (7 - position) and 1)
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

    //TODO: WRITE TEST
    fun removeBytesNotNeededAfterIndex(index: Int) {
        val byteIndex = index / 8
        val bitIndex = index % 8

        while(values.size-1 > byteIndex){
            values.removeLast()
        }

        byteInsertIndex = bitIndex+1

        for(i in (index..<values.size * 8)){
            setBit(i, 0)
        }
        byteInsertIndex = bitIndex+1
    }
}

