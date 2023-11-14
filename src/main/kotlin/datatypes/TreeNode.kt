package datatypes

data class TreeNode(val symbol: Int, val frequency: Int, val depth: Int){
    var parent: TreeNode? = null

    var children:MutableList<TreeNode> = mutableListOf()

    fun addChild(node: TreeNode){
        children.add(node)
        node.parent = this
    }

    override fun toString(): String {
        return "TreeNode(symbol=$symbol, frequency=$frequency, depth=$depth)"
    }


}