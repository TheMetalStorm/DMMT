package datatypes



data class TreeNode(val symbol: Int, val frequency: Int, var largestAmountOfStepsToLeaf: Int){
    var parent: TreeNode? = null

    var leftChild: TreeNode? = null
    var rightChild: TreeNode? = null

    fun removeUnwantedNodes() {
        if (leftChild?.shouldRemove() == true) {
            leftChild = null
        } else {
            leftChild?.removeUnwantedNodes()
        }

        if (rightChild?.shouldRemove() == true) {
            rightChild = null
        } else {
            rightChild?.removeUnwantedNodes()
        }
    }

    private fun TreeNode?.shouldRemove(): Boolean {
        return this?.let { node ->
            node.symbol == Int.MIN_VALUE && (node.leftChild == null || node.leftChild?.symbol == Int.MIN_VALUE) &&
                    (node.rightChild == null || node.rightChild?.symbol == Int.MIN_VALUE)
        } ?: true
    }
//    fun addChild(node: TreeNode){
//        if(rightChild == null)
//        {
//            node.parent = this
//            rightChild = node
//        }
//
//        else{
//            node.parent = this
//            leftChild = node
//        }
//
//    }

    fun addLeft(node: TreeNode){

            node.parent = this
            leftChild = node


    }

    fun addRight(node: TreeNode){

        node.parent = this
        rightChild = node


    }

    fun addNode(node: TreeNode) {
        if (this.rightChild == null) {
            this.addRight(node)
        } else if (this.leftChild == null) {
            this.addLeft(node)
        } else {
            throw RuntimeException("something went wrong while writing node")
        }
//        node.setParent(this)
//        addWeight(node.getWeight())
        if (this.leftChild != null && this.rightChild != null) {
            if (this.rightChild!!.largestAmountOfStepsToLeaf == 1 && this.leftChild!!.largestAmountOfStepsToLeaf > 1) {
                val tmp = this.leftChild!!
                this.addLeft(this.rightChild!!)
                this.addRight(tmp)
            }
            if (this.leftChild!!.largestAmountOfStepsToLeaf > this.rightChild!!.largestAmountOfStepsToLeaf) {
                val tmp = this.leftChild!!
                this.addLeft(this.rightChild!!)
                this.addRight(tmp)
            }
            if (this.leftChild!!.largestAmountOfStepsToLeaf > this.rightChild!!.largestAmountOfStepsToLeaf) {
                val rightLeft = this.rightChild!!.leftChild!!
                this.rightChild!!.addLeft(this.leftChild!!.rightChild!!)
                this.leftChild!!.addRight(rightLeft)
            }
        }
    }

    override fun toString(): String {
        val buffer = StringBuilder(500)
        print(buffer, "", "")
        return buffer.toString()
    }

    private fun print(buffer: StringBuilder, prefix: String, childrenPrefix: String) {
        buffer.append(prefix)
        buffer.append("$symbol, frequency=$frequency")

        buffer.append('\n')
        if (leftChild != null || rightChild != null) {
            leftChild?.print(buffer, "$childrenPrefix├0── ", "$childrenPrefix│   ")
            rightChild?.print(buffer, "$childrenPrefix└1── ", "$childrenPrefix    ")
        }
    }

    companion object {
        fun empty(): TreeNode{
            return TreeNode(Int.MIN_VALUE, Int.MAX_VALUE, 0)
        }


    }


}