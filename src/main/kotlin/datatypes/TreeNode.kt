package datatypes


data class TreeNode(val symbol: Int, val frequency: Int, var largestAmountOfStepsToLeaf: Int){
    var parent: TreeNode? = null

    var children:MutableList<TreeNode> = mutableListOf()

    fun addChild(node: TreeNode){
        children.add(node)
        node.parent = this
    }
    override fun toString(): String {
        val buffer = java.lang.StringBuilder(500)
        print(buffer, "", "")
        return buffer.toString()    }

    private fun print(buffer: StringBuilder, prefix: String, childrenPrefix: String) {
        buffer.append(prefix)
        buffer.append(symbol)
        buffer.append('\n')
        val it: Iterator<TreeNode> = children.iterator()
        while (it.hasNext()) {

            val next = it.next()
            if (it.hasNext()) {
                next.print(buffer, "$childrenPrefix├0─ ", "$childrenPrefix│   ")
            } else {
                next.print(buffer, "$childrenPrefix└1─ ", "$childrenPrefix    ")
            }
        }
    }
    companion object {
        fun empty(): TreeNode{
            return TreeNode(Int.MIN_VALUE, Int.MAX_VALUE, 0)
        }


    }


}