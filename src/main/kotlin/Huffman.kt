import datatypes.BitStream
import datatypes.TreeNode
import java.util.*

data class Huffman (val symbols: IntArray) {

    fun encode(toEncode: IntArray): HufEncode{
        val sortedOccurences = getOccurences(toEncode)
        val tree = createTree(toEncode, PriorityQueue(sortedOccurences))
        //TODO: create dictionary which maps the symbols(Int) to binary codes(bitstream)
        val symbolToBitstreamMap = getSymbolToBitstreamMap(tree, sortedOccurences)
        val encoded = BitStream()
        for (symbol in toEncode) {
            //TODO: use dictionary to add to Bitstream
            symbolToBitstreamMap.get(symbol)?.let { encoded.addBitStream(it) }
        }
        return HufEncode(encoded, symbolToBitstreamMap)
    }

    private fun getSymbolToBitstreamMap(tree: TreeNode<HufNode>, sortedOccurences: PriorityQueue<TreeNode<HufNode>>): HashMap<Int, BitStream> {
        var result: HashMap<Int, BitStream> = hashMapOf()
        while (sortedOccurences.isNotEmpty()){
            val currentSymbol = sortedOccurences.poll().value.symbol
            val bitstreamForSymbol = BitStream()
            result.put(currentSymbol, getBitstreamFromTree(currentSymbol, tree, bitstreamForSymbol, 0))
        }
        return result;
    }

    //TODO: does not work rn
    private fun getBitstreamFromTree(currentSymbol: Int, tree: TreeNode<HufNode>, bitstreamForSymbol: BitStream, curBit: Int) : BitStream{

        if(tree.children.isNotEmpty()){
            bitstreamForSymbol.addToList(1)
            getBitstreamFromTree(currentSymbol, tree.children[1], bitstreamForSymbol, curBit + 1 );
            bitstreamForSymbol.revert()

            bitstreamForSymbol.addToList(0)
            getBitstreamFromTree(currentSymbol, tree.children[0], bitstreamForSymbol, curBit + 1 );
            bitstreamForSymbol.revert()
        }

        if(tree.children.isEmpty() && currentSymbol == tree.value.symbol){
            bitstreamForSymbol.removeBytesNotNeededAfterIndex(curBit)
            bitstreamForSymbol.byteInsertIndex = curBit + 1
            return bitstreamForSymbol;
        }

        return BitStream()
    }


    private fun createTree(toEncode: IntArray, sortedOccurences: PriorityQueue<TreeNode<HufNode>>): TreeNode<HufNode> {

        while (sortedOccurences.size != 1){
            val one = sortedOccurences.poll();
            val two = sortedOccurences.poll();
            var currentNode: TreeNode<HufNode> = TreeNode(HufNode(Integer.MIN_VALUE, one.value.frequency + two.value.frequency));
            currentNode.addChild(one)
            currentNode.addChild(two)
            sortedOccurences.add(currentNode)
        }
        return sortedOccurences.poll();

    }

    private fun getOccurences(toEncode: IntArray): PriorityQueue<TreeNode<HufNode>> {
        val occurences = PriorityQueue<TreeNode<HufNode>> { node1, node2 ->
            node1.value.frequency - node2.value.frequency
        }
        for (symbol in symbols) {
            val numOccurences = toEncode.filter { it == symbol }.size

            //save to Map
            occurences.add(TreeNode(HufNode(symbol, numOccurences)))
        }
        return occurences
    }

    fun decode(hufEncode: HufEncode){
        TODO("Not yet implemented")
    }
}

data class HufNode(val symbol: Int, val frequency: Int)
data class HufEncode(val encodedMessage: BitStream, val symbolToCodeMap: HashMap<Int, BitStream>)