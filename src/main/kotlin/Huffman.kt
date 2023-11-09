import datatypes.BitStream
import datatypes.TreeNode
import java.util.*

data class Huffman (val symbols: IntArray) {

    fun encode(toEncode: IntArray): HufEncode{
        val tree = createTree(toEncode)
        val encoded = BitStream()
        for (symbol in toEncode) {
            encoded.addBitStream(findHufmannCode(symbol,tree))
        }
        return HufEncode(encoded, tree)
    }

    private fun findHufmannCode(symbol: Int, tree: TreeNode<HufNode>): BitStream {
        TODO("Not yet implemented")
    }

    private fun createTree(toEncode: IntArray): TreeNode<HufNode> {

        val sortedOccurences = getOccurences(toEncode)
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
data class HufEncode(val encodedMessage: BitStream, val hufmannTree: TreeNode<HufNode>)