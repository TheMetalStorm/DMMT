import datatypes.BitStream
import datatypes.TreeNode
import java.util.*
import kotlin.collections.ArrayList

data class Huffman (val symbols: IntArray) {

    val maxDepth = 4 //L
    fun encode(toEncode: IntArray): HufEncode{
        val sortedOccurences = getOccurences(toEncode)
        val tree = createTree(PriorityQueue(sortedOccurences), true)
        var cutTree = cutTreeForDepth(tree)
        while(cutTree.largestAmountOfStepsToLeaf>maxDepth){ //repeat if to deep
            cutTree = cutTreeForDepth(cutTree)
        }
        //TODO: geht entweder ab hier oder im decode kaputt
        val symbolToBitstreamMap = getSymbolToBitstreamMap(cutTree, sortedOccurences)
        val encoded = BitStream()
        for (symbol in toEncode) {
            encoded.addBitStreamUntilByteInsertIndex(symbolToBitstreamMap.getValue(symbol))
        }
        return HufEncode(encoded, symbolToBitstreamMap)
    }
    private fun cutTreeForDepth(originalTree:TreeNode): TreeNode{
        val currentDepth = 0
        val cutNodes = arrayListOf<TreeNode>()
        //cut and collect nodes > then this.depth
        findNodesInMaxDepth(originalTree, currentDepth, cutNodes)
        if(cutNodes.isEmpty()){
            return originalTree
        }
        //build new tree with cut nodes
        val cutLeafs:PriorityQueue<TreeNode> = PriorityQueue(Comparator.comparing(TreeNode::largestAmountOfStepsToLeaf).thenComparing(TreeNode::frequency))
        for(node:TreeNode in cutNodes){
            if(node.largestAmountOfStepsToLeaf==0) {
                cutLeafs.add(node)
            }
        }

        val treeOfCutNodes = createTree(PriorityQueue(cutLeafs), false)
        //add new node to tree at depthToAddNewTree with minimum weight
        var depthToAddNewTree:Int =  maxDepth - treeOfCutNodes.largestAmountOfStepsToLeaf - 1 //test
        val newRoot = TreeNode.empty()
        if(depthToAddNewTree == 0){ //root for treeOfCutNodes == root of originalTree
            //set new node as parent -> set cutTree left and originalTree right
            newRoot.addChild(treeOfCutNodes)
            newRoot.addChild(originalTree)
            newRoot.largestAmountOfStepsToLeaf = Math.max(treeOfCutNodes.largestAmountOfStepsToLeaf, originalTree.largestAmountOfStepsToLeaf)+1
        }
        else {
            var iterateChild:TreeNode = TreeNode.empty()
            while (depthToAddNewTree > 0) { //root for treeOfCutNodes != root of originalTree
                iterateChild = treeOfCutNodes.children[0] // always move to the smaller one
                depthToAddNewTree--
            }
            newRoot.parent = iterateChild.parent
            if(treeOfCutNodes.frequency<=iterateChild.frequency){
                newRoot.addChild(treeOfCutNodes)
                newRoot.addChild(iterateChild)
            }
            else{
                newRoot.addChild(iterateChild)
                newRoot.addChild(treeOfCutNodes)
            }
        }
        return newRoot
    }

    private fun findNodesInMaxDepth(tree: TreeNode, currentDepth: Int, newTree: ArrayList<TreeNode>) {
        if(currentDepth == maxDepth-1){
            for(child in tree.children) {
                newTree.add(child)
            }
            tree.children.clear();
        }
        else{
            val oldDepth = currentDepth
            for(child in tree.children) {
                findNodesInMaxDepth(child, oldDepth+1, newTree)
            }
        }
    }

    private fun getSymbolToBitstreamMap(tree: TreeNode, sortedOccurences: PriorityQueue<TreeNode>): HashMap<Int, BitStream> {
        var result: HashMap<Int, BitStream> = hashMapOf()
        while (sortedOccurences.isNotEmpty()){
            val currentSymbol = sortedOccurences.poll().symbol
            val bitstreamForSymbol = BitStream()
            result.put(currentSymbol, getBitstreamFromTree(currentSymbol, tree, bitstreamForSymbol, 0))
        }
        return result;
    }

    private fun getBitstreamFromTree(currentSymbol: Int, tree: TreeNode, bitstreamForSymbol: BitStream, curBit: Int) : BitStream {
        // Check if current node is a leaf and contains the symbol we're looking for
        if (tree.children.isEmpty()) {
            if (currentSymbol == tree.symbol) {
                // Found the symbol, so trim the bitstream and set the insert index
                bitstreamForSymbol.removeBitsNotNeededStartFromIndex(curBit)
                bitstreamForSymbol.byteInsertIndex = curBit
                return bitstreamForSymbol
            } else {
                // Not the symbol we're looking for, backtrack
                return BitStream() // Or some indication that the symbol was not found in this path
            }
        } else {
            // Traverse the right subtree with '1' added to the bitstream
            bitstreamForSymbol.addToList(1)
            val rightSearch = getBitstreamFromTree(currentSymbol, tree.children[1], bitstreamForSymbol, curBit + 1)
            if (rightSearch != BitStream()) {
                return rightSearch // Found the symbol in the right subtree
            }
            bitstreamForSymbol.revert() // Backtrack the bit added for the right subtree

            // Traverse the left subtree with '0' added to the bitstream
            bitstreamForSymbol.addToList(0)
            val leftSearch = getBitstreamFromTree(currentSymbol, tree.children[0], bitstreamForSymbol, curBit + 1)
            if (leftSearch != BitStream()) {
                return leftSearch // Found the symbol in the left subtree
            }
            bitstreamForSymbol.revert() // Backtrack the bit added for the left subtree
        }

        return BitStream() // Or some indication that the symbol was not found in this path
    }


    private fun createTree(sortedOccurences: PriorityQueue<TreeNode>, addNewRoot: Boolean): TreeNode {
        while (sortedOccurences.size != 1){
            val one = sortedOccurences.poll();
            val two = sortedOccurences.poll();
            val currentNode: TreeNode = TreeNode(Int.MIN_VALUE, one.frequency + two.frequency, Math.max(one.largestAmountOfStepsToLeaf, two.largestAmountOfStepsToLeaf)+1);
            currentNode.addChild(one)
            currentNode.addChild(two)
            sortedOccurences.add(currentNode)
        }
        val newRoot: TreeNode
        val oldRoot = sortedOccurences.poll()
        if(addNewRoot){
            newRoot = TreeNode.empty()
            setupNewRoot(oldRoot, newRoot)
        }
        else
        {
            newRoot = oldRoot
        }
        return newRoot
    }

    private fun setupNewRoot(oldRoot: TreeNode, newRoot: TreeNode) {
        newRoot.addChild(TreeNode.empty())
        newRoot.addChild(oldRoot)
        newRoot.largestAmountOfStepsToLeaf = oldRoot.largestAmountOfStepsToLeaf + 1
    }

    fun getOccurences(toEncode: IntArray): PriorityQueue<TreeNode> {
        val occurences = PriorityQueue(Comparator.comparing(TreeNode::largestAmountOfStepsToLeaf).thenComparing(TreeNode::frequency))
        for (symbol in symbols) {
            val numOccurences = toEncode.filter { it == symbol }.size
            occurences.add(TreeNode(symbol, numOccurences, 0))
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

data class HufEncode(val encodedMessage: BitStream, val symbolToCodeMap: HashMap<Int, BitStream>)