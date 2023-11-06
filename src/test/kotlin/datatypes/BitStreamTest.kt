package datatypes

import kotlin.test.Test
import kotlin.test.assertEquals


class BitStreamTest {

    @Test
    fun addToList() {
        val bitstream = BitStream()
        val listToAdd : ArrayList<Int> = arrayListOf(0,1,1,1,1,1,0,1)   //125 -> 1111101
        bitstream.addToList(listToAdd)
        val testInt = 125
        assertEquals(bitstream, BitStream(arrayListOf(testInt.toUByte()),8))
    }
    @Test
    fun modifyByteAfterLastRelevantBit() {
        val bitstream = BitStream()
        val listToAdd : ArrayList<Int> = arrayListOf(0,1,0,1,0,1,0)
        bitstream.addToList(listToAdd)
        bitstream.addToList(1)
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
    @Test
    fun addBitStream() {
        val bitstream = BitStream()
        val bitstreamToAdd = BitStream()
        val listToAdd : ArrayList<Int> = arrayListOf()
        for (i in 0..50000000) {    //500000000 =  heapspace, 50000000 = ok
            listToAdd.add(1)
            listToAdd.add(0)
        }
        bitstreamToAdd.addToList(listToAdd)
        bitstream.addBitStream(bitstreamToAdd)
        assertEquals(bitstream, bitstreamToAdd)

    }

    @Test
    fun addByte(){
        val bitStreamFromBytes = BitStream()
        val bitStreamFromBits = BitStream()
        bitStreamFromBytes.addByteToStream(56u)
        bitStreamFromBits.addToList(arrayListOf(0,0,1,1,1,0,0,0))   //56 in Binary
        assertEquals(bitStreamFromBytes, bitStreamFromBits)
    }

    @Test
    fun addBytes(){
        val bitStreamFromBytes = BitStream()
        val bitStreamFromBits = BitStream()
        bitStreamFromBytes.addByteToStream(arrayListOf(56u, 56u))
        bitStreamFromBits.addToList(arrayListOf(0,0,1,1,1,0,0,0,    //56 in Binary
                                                0,0,1,1,1,0,0,0))   //56 in Binary
        assertEquals(bitStreamFromBytes, bitStreamFromBits)
    }
}