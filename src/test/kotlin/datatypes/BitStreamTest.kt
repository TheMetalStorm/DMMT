package datatypes

import kotlin.test.Test
import kotlin.test.assertEquals


class BitStreamTest {
    @Test
    fun addToList() {
        val bitstream = BitStream()
        val listToAdd : ArrayList<Int> = arrayListOf(0,1,1,1,1,1,0,1) //125 -> 1111101
        bitstream.addToList(listToAdd)
        val testInt = 125
        assertEquals(bitstream, BitStream(arrayListOf(testInt.toUByte()),7))
    }
    @Test
    fun modifyByteAfterLastRelevantBit() {
        val bitstream = BitStream()
        val listToAdd : ArrayList<Int> = arrayListOf(0,1,0,1,0,1,0)
        bitstream.addToList(listToAdd)
        bitstream.modifyByteAfterLastRelevantBit(bitstream.getLastByte(),1)
        val bitstreamTest = BitStream()
        val listToAddTest : ArrayList<Int> = arrayListOf(0,1,0,1,0,1,0,1)
        bitstreamTest.addToList(listToAddTest)
        assertEquals(bitstream, bitstreamTest)

    }
    @Test
    fun checkNumbersToParse() {
        try {
            val bitstream = BitStream()
            val listToAdd : ArrayList<Int> = arrayListOf(0,1,2)
            bitstream.addToList(listToAdd)
        }
        catch (e: IllegalArgumentException) {
            assertEquals(e.message.toString(), "Not every given value is 0 or 1. Please check your data")
        }
    }
}