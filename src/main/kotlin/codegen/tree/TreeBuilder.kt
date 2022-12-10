package codegen.tree

import codegen.*

/**
 * Builds the tree using [Tree] representation
 */
class TreeBuilder {
    fun buildClass(classNode: Tree.Node) {
        val fieldsNum = rand(1, MAX_FIELDS)
        val methodsNum = rand(1, MAX_METHODS)
        for (i in 0 until fieldsNum) {
            classNode.children.add(Tree.Node(Elem.FIELD))
        }
        for (i in 0 until methodsNum) {
            classNode.children.add(buildMethod())
        }
        println("H")
    }

    private fun parseElem(elem: Elem, node: Tree.Node, depth: Int = 0, from: Elem = Elem.CLASS) {
        val fieldsNum = rand(1, MAX_FIELDS) // random constraints generation
        val methodsNum = rand(1, MAX_METHODS)
        val methodCallNum = rand(1, MAX_METHOD_CALLS)
        val varDeclNum = rand(0, MAX_VAR_DECLS)
        val ifNum = rand(0, MAX_IFS)
        val forNum = rand(0, MAX_FORS)
        for (i in 0 until 5) {
            when (elem) {
                Elem.FIELD -> if (i < fieldsNum) node.children.add(Tree.Node(elem))
                Elem.METHOD -> if (i < methodsNum) node.children.add(buildMethod())
                Elem.IF -> if (i < ifNum) buildIf(depth)?.let { node.children.add(it) }
                Elem.VAR_DECL -> if (i < varDeclNum) node.children.add(Tree.Node(elem))
                Elem.METHOD_CALL -> if (i < methodCallNum) node.children.add(Tree.Node(elem))
                Elem.FOR -> if (i < forNum) buildFor(depth)?.let { node.children.add(it) }
                Elem.RETURN -> {
                    if (rand(0, 10) > 7 && (from == Elem.FOR || from == Elem.IF)) {
                        node.children.add(Tree.Node(elem))
                    }
                    break
                }
                else -> {}
            }
        }
    }

    private fun buildMethod(): Tree.Node {
        val node = Tree.Node(Elem.METHOD)
        rules[Elem.METHOD]?.shuffled()?.forEach { parseElem(it, node) }
        println()
        return node
    }

    private fun buildIf(depth: Int): Tree.Node? {
        return if (depth > MAX_DEPTH || rand(1, 6) > 2) {
            null
        } else {
            val node = Tree.Node(Elem.IF)
            rules[Elem.IF]?.shuffled()?.forEach { parseElem(it, node, depth + 1, Elem.IF) }
            node
        }
    }

    private fun buildFor(depth: Int): Tree.Node? {
        return if (depth > MAX_DEPTH || rand(1, 5) > 2) {
            null
        } else {
            val node = Tree.Node(Elem.FOR)
            rules[Elem.FOR]?.shuffled()?.forEach { parseElem(it, node, depth + 1, Elem.FOR) }
            node
        }
    }
}