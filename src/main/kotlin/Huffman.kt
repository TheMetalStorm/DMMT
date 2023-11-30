import datatypes.BitStream
import datatypes.TreeNode
import java.util.*
import java.util.stream.Collectors.toList
import kotlin.collections.ArrayList

class Huffman {

    //limit to 15 so that we can later add a new node at the 1* Bitstream to prevent it, ends with depth = 16
    var symbols: IntArray = intArrayOf()
    val maxDepth = 15
    fun encode(toEncode: IntArray): HufEncode{

        getSymbols(toEncode)
        val sortedOccurences = getOccurences(toEncode)
        val tree = createTree(PriorityQueue(sortedOccurences))
        var treeWithRightLength: TreeNode

        if(tree.largestAmountOfStepsToLeaf > maxDepth){
            treeWithRightLength = cutTreeForDepth(tree)
            while(treeWithRightLength.largestAmountOfStepsToLeaf>maxDepth){ //repeat if to deep
                treeWithRightLength = cutTreeForDepth(treeWithRightLength)
            }
        }
        else{
            treeWithRightLength = tree
        }


        //5c, prevent 1* Bitstrean
        var child = treeWithRightLength
        while(child.rightChild != null){
            child = child.rightChild as TreeNode
        }
        val newBottomRight = TreeNode.empty()
        child.parent?.addRight(newBottomRight)
        newBottomRight.addLeft(child)
        newBottomRight.addRight(TreeNode.empty())


        val symbolToBitstreamMap = getSymbolToBitstreamMap(treeWithRightLength, sortedOccurences)
        val encoded = BitStream()
        for (symbol in toEncode) {
            encoded.addBitStreamUntilByteInsertIndex(symbolToBitstreamMap.getValue(symbol))
        }
        return HufEncode(encoded, symbolToBitstreamMap)
    }

    private fun getSymbols(toEncode: IntArray) {
        symbols = toEncode.distinct().toIntArray()
    }


    private fun cutTreeForDepth(originalTree:TreeNode): TreeNode{
        val currentDepth = 0
        val cutNodes = arrayListOf<TreeNode>()
        //cut and collect nodes > then this.depth
        findNodesInDepth(maxDepth, originalTree, currentDepth, cutNodes, true)
        if(cutNodes.isEmpty()){
            return originalTree
        }

        resetDepthsAfterCutNode(originalTree)
        //build new tree with cut nodes
        val cutLeafs:PriorityQueue<TreeNode> = getLeaves(cutNodes)
        val treeOfCutNodes = createTreeB(PriorityQueue(cutLeafs))
        //add new node to tree at depthToAddNewTree with minimum weight
        var depthToAddNewTree:Int =  maxDepth - treeOfCutNodes.largestAmountOfStepsToLeaf - 1 //test
        val newRoot = TreeNode.empty()
        if(depthToAddNewTree <= 0){ //root for treeOfCutNodes == root of originalTree
            //set new node as parent -> set cutTree left and originalTree right
            if(isEmptyTree(originalTree))
                return treeOfCutNodes
            newRoot.addLeft( treeOfCutNodes)
            newRoot.addRight(originalTree)
            newRoot.largestAmountOfStepsToLeaf = Math.max(treeOfCutNodes.largestAmountOfStepsToLeaf, originalTree.largestAmountOfStepsToLeaf)+1
            return newRoot
        }
        else {
//            while (depthToAddNewTree > 0) { //root for treeOfCutNodes != root of originalTree
//                iterateChild = iterateChild.leftChild!! // always move to the smaller one ATTENTION: small child could not have other needed children
//                depthToAddNewTree--
//            }
//            iterateChild.parent?.leftChild = newRoot

            val nodesOnLevelToAdd = arrayListOf<TreeNode>()
            findNodesInDepth(depthToAddNewTree, originalTree, currentDepth, nodesOnLevelToAdd, false)
            val iterateChild = nodesOnLevelToAdd.minBy { it.frequency }
            //TODO: add left or right?
            if(iterateChild.parent?.rightChild == iterateChild){
                iterateChild.parent?.addRight(newRoot)
            }
            if(iterateChild.parent?.leftChild == iterateChild){
                iterateChild.parent?.addLeft(newRoot)
            }

            if(treeOfCutNodes.frequency<=iterateChild.frequency){
                newRoot.addLeft(treeOfCutNodes)
                newRoot.addRight(iterateChild)
            }
            else{
                newRoot.addLeft(iterateChild)
                newRoot.addRight(treeOfCutNodes)
            }
            return originalTree

        }
    }

    private fun isEmptyTree(originalTree: TreeNode): Boolean {
        var notEmpty: MutableList<Boolean> = mutableListOf();

        isEmptyTreeRec(originalTree, notEmpty)


        return notEmpty.isEmpty()
    }

    private fun isEmptyTreeRec(originalTree: TreeNode, notEmpty: MutableList<Boolean>) {
        if(originalTree.symbol != Int.MIN_VALUE){
            notEmpty.add(true);
        }
        if(originalTree.leftChild != null)
        isEmptyTreeRec(originalTree.leftChild as TreeNode, notEmpty)
        if(originalTree.rightChild != null)

        isEmptyTreeRec(originalTree.rightChild as TreeNode, notEmpty)

    }

    private fun resetDepthsAfterCutNode(originalTree: TreeNode) {

        originalTree.largestAmountOfStepsToLeaf = 0
        setZero(originalTree.leftChild)
        setZero(originalTree.rightChild)
        val cutNodes = arrayListOf<TreeNode>()
        findNodesInDepth(maxDepth-1, originalTree, 0, cutNodes, false)
        for (cutNode in cutNodes) {
            var node = cutNode
            node.largestAmountOfStepsToLeaf = 0
            while(node.parent != null){
                val currentDepth = node.largestAmountOfStepsToLeaf
                node = node.parent!!
                if(node.largestAmountOfStepsToLeaf< currentDepth+1)
                    node.largestAmountOfStepsToLeaf = currentDepth +1
            }
        }
    }

    private fun setZero(child: TreeNode?) {
        if(child != null) {
            child.largestAmountOfStepsToLeaf = 0
                if(child.leftChild != null){
                    setZero(child.leftChild!!)
                }
                if(child.rightChild != null){
                    setZero(child.rightChild!!)
                }
            }

    }


    private fun getLeaves(cutNodes: java.util.ArrayList<TreeNode>): PriorityQueue<TreeNode> {
        val result = PriorityQueue(Comparator.comparing(TreeNode::largestAmountOfStepsToLeaf).thenComparing(TreeNode::frequency))
        for (cutNode in cutNodes) {
            getLeavesRec(cutNode, result)
        }
        return result
    }

    private fun getLeavesRec(node: TreeNode, result: PriorityQueue<TreeNode>) {
        if(node.largestAmountOfStepsToLeaf == 0 && node.symbol != Int.MIN_VALUE){
            result.add(node)
        }
        else{
            if(node.rightChild != null){
                getLeavesRec(node.rightChild!!, result)
            }
            if(node.leftChild != null){
                getLeavesRec(node.leftChild!!, result)
            }
        }

    }

    private fun findNodesInDepth(depthToCheck:Int, tree: TreeNode, currentDepth: Int, newTree: ArrayList<TreeNode>, cut: Boolean) {
        if(currentDepth == depthToCheck-1){
            if(tree.rightChild != null){
                newTree.add(tree.rightChild!!)
            }
            if(tree.leftChild != null){
                newTree.add(tree.leftChild!!)
            }

            if(cut){
                tree.leftChild = null
                tree.rightChild = null
            }
                ;
        }
        else{
            val oldDepth = currentDepth
            if(tree.rightChild != null){
                findNodesInDepth(depthToCheck, tree.rightChild!!,oldDepth+1, newTree, cut)
            }
            if(tree.leftChild != null){
                findNodesInDepth(depthToCheck, tree.leftChild!!,oldDepth+1, newTree, cut)
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
        if (tree.leftChild == null && tree.rightChild == null) {
            if (currentSymbol == tree.symbol) {
                // Found the symbol, so trim the bitstream and set the insert index
                bitstreamForSymbol.removeBitsNotNeededStartFromIndex(curBit)
                var newByteInsertIndex = curBit

                if(newByteInsertIndex >8){
                    newByteInsertIndex = (newByteInsertIndex + 1 ) % 8
                    if(newByteInsertIndex == 0) {
                        newByteInsertIndex = 8
                    }
                }
                bitstreamForSymbol.byteInsertIndex = newByteInsertIndex
                return bitstreamForSymbol
            } else {
                // Not the symbol we're looking for, backtrack
                return BitStream() // Or some indication that the symbol was not found in this path
            }
        } else {
            // Traverse the left subtree with '0' added to the bitstream
            if(tree.leftChild != null){
                bitstreamForSymbol.addToList(0)
                val leftSearch = getBitstreamFromTree(currentSymbol, tree.leftChild as TreeNode, bitstreamForSymbol, curBit + 1)
                if (leftSearch != BitStream()) {
                    return leftSearch // Found the symbol in the left subtree
                }
                bitstreamForSymbol.revert()// Backtrack the bit added for the left subtree
            }

            if(tree.rightChild != null) {
                // Traverse the right subtree with '1' added to the bitstream
                bitstreamForSymbol.addToList(1)
                val rightSearch = getBitstreamFromTree(currentSymbol, tree.rightChild as TreeNode, bitstreamForSymbol, curBit + 1)
                if (rightSearch != BitStream()) {
                    return rightSearch // Found the symbol in the right subtree
                }
                bitstreamForSymbol.revert() // Backtrack the bit added for the right subtree
            }

        }

        return BitStream() // Or some indication that the symbol was not found in this path
    }


    private fun createTree(sortedOccurences: PriorityQueue<TreeNode>): TreeNode {
        while (sortedOccurences.size != 1){
            val one = sortedOccurences.poll();
            val two = sortedOccurences.poll();
            val currentNode: TreeNode = TreeNode(Int.MIN_VALUE, one.frequency + two.frequency, Math.max(one.largestAmountOfStepsToLeaf, two.largestAmountOfStepsToLeaf)+1);
            currentNode.addNode(one)
            currentNode.addNode(two)
            sortedOccurences.add(currentNode)
        }
        return sortedOccurences.poll()
    }

    private fun createTreeB(sortedOccurences: PriorityQueue<TreeNode>): TreeNode {
        while (sortedOccurences.size != 1){
            val one = sortedOccurences.poll();
            val two = sortedOccurences.poll();
            val currentNode: TreeNode = TreeNode(Int.MIN_VALUE, one.frequency + two.frequency, Math.max(one.largestAmountOfStepsToLeaf, two.largestAmountOfStepsToLeaf)+1);
            currentNode.addLeft(one)
            currentNode.addRight(two)
            sortedOccurences.add(currentNode)
        }
        return sortedOccurences.poll()
    }

    fun getOccurences(toEncode: IntArray): PriorityQueue<TreeNode> {
        val occurences = PriorityQueue(Comparator.comparing(TreeNode::frequency))
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