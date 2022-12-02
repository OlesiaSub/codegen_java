package codegen.tree

import codegen.CLASSES
import codegen.Elem

const val MAX_DEPTH = 4

class Tree {

    private val builder = TreeBuilder()
    val heads: MutableList<Node> = mutableListOf()

    fun build() {
        for (i in 0 until CLASSES) {
            val classNode = Node(Elem.CLASS)
            heads.add(classNode)
            builder.buildClass(classNode)
        }
        println("done")
    }

    data class Node(val type: Elem) {
        val children: MutableList<Node> = mutableListOf()
    }
}
