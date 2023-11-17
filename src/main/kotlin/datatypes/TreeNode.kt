package datatypes

<<<<<<< Updated upstream
class TreeNode<T>(value:T){
    var value:T = value
    var parent: TreeNode<T>? = null
=======
data class TreeNode(val symbol: Int, val frequency: Int, var depth: Int){
    var parent: TreeNode? = null
>>>>>>> Stashed changes

    var children:MutableList<TreeNode<T>> = mutableListOf()

    fun addChild(node: TreeNode<T>){
        children.add(node)
        node.parent = this
    }
    override fun toString(): String {
        var s = "${value}"
        if (!children.isEmpty()) {
            s += " {" + children.map { it.toString() } + " }"
        }
        return s
    }
<<<<<<< Updated upstream
=======

    companion object {
        fun empty(): TreeNode{
            return TreeNode(Int.MIN_VALUE, Int.MAX_VALUE, 0)
        }
    }
>>>>>>> Stashed changes
}