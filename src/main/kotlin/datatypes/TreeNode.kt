package datatypes


data class TreeNode(val symbol: Int, val frequency: Int, var depth: Int){
    var parent: TreeNode? = null

    var children:MutableList<TreeNode> = mutableListOf()

    fun addChild(node: TreeNode){
        children.add(node)
        node.parent = this
    }

    companion object {
        fun empty(): TreeNode{
            return TreeNode(Int.MIN_VALUE, Int.MAX_VALUE, 0)
        }
    }
}