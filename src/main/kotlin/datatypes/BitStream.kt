package datatypes

/**
 * This structure can be created with given byte. Attention:
 * for more byte control after creation -> use no parameter at creation
 */
data class BitStream (private var values: ArrayList<Byte> = arrayListOf(), var lastRelevantBit: Int =0){
    fun addToList(valuesToAdd: ArrayList<Boolean>) {
        for (value in valuesToAdd) {
            addToList(value)
        }
    }

    fun addToList(booleanToAdd: Boolean) {
        val bitToAdd: Int = if (booleanToAdd) 1 else 0
        if(lastRelevantBit == 7){
            values.add((bitToAdd shl 7).toByte())
            lastRelevantBit = 0
        } else {
            values[values.size - 1] = modifyByteAfterLastRelevantBit(getLastByte(), bitToAdd)
            lastRelevantBit++
        }
    }

    private fun  getLastByte(): Byte{
        return values.last
    }

    private fun modifyByteAfterLastRelevantBit(byte: Byte, bit: Int): Byte {
         var intVal: Int = byte.toInt()
         intVal = intVal or bit shl (7-lastRelevantBit-1)
        return intVal.toByte()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BitStream

        if (values != other.values) return false
        if (lastRelevantBit != other.lastRelevantBit) return false

        return true
    }

    override fun hashCode(): Int {
        var result = values.hashCode()
        result = 31 * result + lastRelevantBit
        return result
    }

    fun print() {
        for (byte in values) {
            val st = byte.toString(2)
            print("[" + st + "] ")
        }
        
    }

}

