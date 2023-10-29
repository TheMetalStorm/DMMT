package datatypes.datatypesTest

import datatypes.BitStream
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class BitStreamTest {
    @Test
    fun addToList() {
        val bitstream : BitStream = BitStream()
        val listToAdd : ArrayList<Int> = arrayListOf(0,1,1,1,1,1,0,1) //125 -> 1111101
        bitstream.addToList(listToAdd)
        val testInt : Int = 125
        arrayListOf(testInt.toUByte())
        assertEquals(bitstream, BitStream(arrayListOf(testInt.toUByte()),7))
    }
    @Test
    fun modifyByteAfterLastRelevantBit() {
        val bitstream : BitStream = BitStream()
        val listToAdd : ArrayList<Int> = arrayListOf(0,1,0,1,0,1,0,0)

    }
    @Test
    fun checkNumbersToParse() {
        try {
            val bitstream : BitStream = BitStream()
            val listToAdd : ArrayList<Int> = arrayListOf(0,1,2)
        }
        catch (e: IllegalArgumentException) {
            assertEquals(e.message.toString(), "Not every given value is 0 or 1. Please check your data")
        }
    }
    @Test
    fun toBinaryString() {
    }
}