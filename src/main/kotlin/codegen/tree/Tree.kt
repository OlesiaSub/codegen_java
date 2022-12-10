package codegen.tree

import codegen.CLASSES
import codegen.Elem

const val MAX_DEPTH = 3

/**
 * Builds basic tree representation of classes, hsa a limited amount of nodes
 * The generated tree will be used as a scheme for the next generation step
 */
class Tree {

    private val builder = TreeBuilder()
    val heads: MutableList<Node> = mutableListOf()

    fun build() {
        for (i in 0 until CLASSES) {
            val classNode = Node(Elem.CLASS)
            heads.add(classNode)
            builder.buildClass(classNode)
        }
    }

    data class Node(val type: Elem) {
        val children: MutableList<Node> = mutableListOf()
    }
}
