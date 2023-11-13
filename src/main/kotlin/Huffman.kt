import datatypes.BitStream
import datatypes.TreeNode
import java.util.*

data class Huffman (val symbols: IntArray) {

    fun encode(toEncode: IntArray): HufEncode{
        val sortedOccurences = getOccurences(toEncode)
        val tree = createTree(PriorityQueue(sortedOccurences))
        val symbolToBitstreamMap = getSymbolToBitstreamMap(tree, sortedOccurences)
        val encoded = BitStream()
        for (symbol in toEncode) {
            encoded.addBitStreamUntilByteInsertIndex(symbolToBitstreamMap.getValue(symbol))
        }
        return HufEncode(encoded, symbolToBitstreamMap)
    }

    private fun getSymbolToBitstreamMap(tree: TreeNode<NodeData>, sortedOccurences: PriorityQueue<TreeNode<NodeData>>): HashMap<Int, BitStream> {
        var result: HashMap<Int, BitStream> = hashMapOf()
        while (sortedOccurences.isNotEmpty()){
            val currentSymbol = sortedOccurences.poll().value.symbol
            val bitstreamForSymbol = BitStream()
            result.put(currentSymbol, getBitstreamFromTree(currentSymbol, tree, bitstreamForSymbol, 0))
        }
        return result;
    }

    private fun getBitstreamFromTree(currentSymbol: Int, tree: TreeNode<NodeData>, bitstreamForSymbol: BitStream, curBit: Int) : BitStream {
        // Check if current node is a leaf and contains the symbol we're looking for
        if (tree.children.isEmpty()) {
            if (currentSymbol == tree.value.symbol) {
                // Found the symbol, so trim the bitstream and set the insert index
                bitstreamForSymbol.removeBytesNotNeededAfterIndex(curBit)
                bitstreamForSymbol.byteInsertIndex = curBit
                return bitstreamForSymbol
            } else {
                // Not the symbol we're looking for, backtrack
                return BitStream() // Or some indication that the symbol was not found in this path
            }
        } else {
            // Traverse the left subtree with '0' added to the bitstream
            bitstreamForSymbol.addToList(0)
            val leftSearch = getBitstreamFromTree(currentSymbol, tree.children[0], bitstreamForSymbol, curBit + 1)
            if (leftSearch != BitStream()) {
                return leftSearch // Found the symbol in the left subtree
            }
            bitstreamForSymbol.revert() // Backtrack the bit added for the left subtree

            // Traverse the right subtree with '1' added to the bitstream
            bitstreamForSymbol.addToList(1)
            val rightSearch = getBitstreamFromTree(currentSymbol, tree.children[1], bitstreamForSymbol, curBit + 1)
            if (rightSearch != BitStream()) {
                return rightSearch // Found the symbol in the right subtree
            }
            bitstreamForSymbol.revert() // Backtrack the bit added for the right subtree
        }

        return BitStream() // Or some indication that the symbol was not found in this path
    }


    private fun createTree(sortedOccurences: PriorityQueue<TreeNode<NodeData>>): TreeNode<NodeData> {
        while (sortedOccurences.size != 1){
            val one = sortedOccurences.poll();
            val two = sortedOccurences.poll();
            var currentNode: TreeNode<NodeData> = TreeNode(NodeData(Integer.MIN_VALUE, one.value.frequency + two.value.frequency));
            currentNode.addChild(one)
            currentNode.addChild(two)
            sortedOccurences.add(currentNode)
        }
        return sortedOccurences.poll();
    }

    fun getOccurences(toEncode: IntArray): PriorityQueue<TreeNode<NodeData>> {
        val occurences = PriorityQueue<TreeNode<NodeData>> { node1, node2 ->
            node1.value.frequency - node2.value.frequency
        }
        for (symbol in symbols) {
            val numOccurences = toEncode.filter { it == symbol }.size

            //save to Map
            occurences.add(TreeNode(NodeData(symbol, numOccurences)))
        }
        return occurences
    }

    fun decode(hufEncode: HufEncode): IntArray{

        val returnMessage: MutableList<Int> = mutableListOf();
        val message = hufEncode.encodedMessage
        val map = hufEncode.symbolToCodeMap

        val relevantBits = ((message.getAllBytes().size-1)*8)+message.byteInsertIndex
        var searchingFor = BitStream()
        for (i in (0..<relevantBits)){
            searchingFor.addToList(message.getBit(i))
            for (mutableEntry in map) {
                val b = mutableEntry.value
                if(b == searchingFor){
                    returnMessage.add(mutableEntry.key)
                    searchingFor = BitStream()
                    break
                }
            }
        }
        return  returnMessage.toIntArray()
    }
}

data class NodeData(val symbol: Int, val frequency: Int)
data class HufEncode(val encodedMessage: BitStream, val symbolToCodeMap: HashMap<Int, BitStream>)