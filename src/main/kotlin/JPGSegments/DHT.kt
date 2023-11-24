package JPGSegments

import datatypes.BitStream
import java.util.HashMap

data class DHT(val symbolToCodeMap: HashMap<Int, BitStream>, val huffmanTableNum: Int, val huffmanTableType: Int){

    private val bitStream = BitStream()

    init{
        checkInput()

        val numSymbols = symbolToCodeMap.size
        //length including marker
        //marker + tableInfo + symbol count for each symbol length + numSymbols
        var length = 2 + 1 + 16 + numSymbols
        val lengthLow =  length.toUByte()
        val lengthHigh = (length shr 8).toUByte()
        var hufmanTableInfo: UInt = 0u;
        hufmanTableInfo = hufmanTableInfo or ((0b00000001u shl (7- huffmanTableNum)))
        hufmanTableInfo = hufmanTableInfo or ((huffmanTableType shl 3).toUInt())
        bitStream.addByteToStream(arrayListOf(0xffu, 0xc4u, lengthHigh, lengthLow, hufmanTableInfo.toUByte()))
        //TODO: Anzahl von Symbolen mit Kodelängen von 1..16 (Summe
        //dieser Anzahlen ist Gesamtzahl der Symbole, muss <= 256)


        //Tabelle mit den Symbolen in aufsteigender Folge der Kodelängen
        val sortedSymbols = symbolToCodeMap.toList().sortedBy { (_, value) -> value.getBitstreamLength() }.toMap()
        for (symbol in sortedSymbols) {
            val sym = symbol.key
            bitStream.addByteToStream(sym.toUByte())
        }


    }

    private fun checkInput() {
        if(!listOf(0,1,2,3).contains(huffmanTableNum)){
            throw Exception("Huffman Table Number should be 0, 1, 2 or 3")
        }

        if(!listOf(0,1).contains(huffmanTableType)){
            throw Exception("Huffman Table Type should be 0 or 1")
        }


    }

    fun getBitStream(): BitStream{
        return bitStream
    }
}