package scheme

import config.MAX_DEPTH
import config.MAX_FIELDS
import config.MAX_FORS
import config.MAX_IFS
import config.MAX_METHODS
import config.MAX_METHOD_CALLS
import config.MAX_VAR_DECLS
import config.MIN_FIELDS
import config.MIN_METHODS
import codegen.*

/**
 * Builds the tree using [Tree] representation
 */
class TreeBuilder {
    fun buildClass(classNode: Tree.Node) {
        val fieldsNum = rand(MIN_FIELDS, MAX_FIELDS)
        val methodsNum = rand(MIN_METHODS, MAX_METHODS)
        for (i in 0 until fieldsNum) {
            classNode.children.add(Tree.Node(Elem.FIELD))
        }
        for (i in 0 until methodsNum) {
            classNode.children.add(buildMethod())
        }
    }

    private fun parseElem(elem: Elem, node: Tree.Node, depth: Int = 0, from: Elem = Elem.CLASS) {
        val fieldsNum = rand(1, MAX_FIELDS) // random constraints generation
        val methodsNum = rand(1, MAX_METHODS)
        val methodCallNum = rand(0, MAX_METHOD_CALLS)
        val varDeclNum = rand(0, MAX_VAR_DECLS)
        val ifNum = rand(0, MAX_IFS)
        val forNum = rand(0, MAX_FORS)
        for (i in 0 until 5) {
            when (elem) {
                Elem.FIELD -> if (i < fieldsNum) node.children.add(Tree.Node(elem))
                Elem.METHOD -> if (i < methodsNum) node.children.add(buildMethod())
                Elem.IF -> {
                    if (i < ifNum) buildIf(depth)?.let {iff ->
                        node.children.add(iff)
                        if (rand(0, 3) == 2) buildElse(depth)?.let { node.children.add(it) }
                    }
                }

                Elem.VAR_DECL -> if (i < varDeclNum) node.children.add(Tree.Node(elem))
                Elem.METHOD_CALL -> if (i < methodCallNum) node.children.add(Tree.Node(elem))
                Elem.FOR -> if (i < forNum) buildFor(depth)?.let { node.children.add(it) }
                Elem.RETURN -> {
                    if (rand(0, 10) > 7 && (from == Elem.FOR || from == Elem.IF)) {
                        node.children.add(Tree.Node(elem))
                    }
                    break
                }

                Elem.EXCEPTION -> {
                    if (rand(0, 10) > 6 && (from == Elem.IF)) {
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
        return node
    }

    private fun buildIf(depth: Int): Tree.Node? {
        return if (depth > MAX_DEPTH || rand(1, 6) > 3) {
            null
        } else {
            val node = Tree.Node(Elem.IF)
            rules[Elem.IF]?.shuffled()?.forEach { parseElem(it, node, depth + 1, Elem.IF) }
            node
        }
    }

    private fun buildElse(depth: Int): Tree.Node? {
        return if (depth > MAX_DEPTH || rand(1, 6) > 4) {
            null
        } else {
            val node = Tree.Node(Elem.ELSE)
            rules[Elem.ELSE]?.shuffled()?.forEach { parseElem(it, node, depth + 1, Elem.ELSE) }
            node
        }
    }

    private fun buildFor(depth: Int): Tree.Node? {
        return if (depth > MAX_DEPTH || rand(1, 5) > 3) {
            null
        } else {
            val node = Tree.Node(Elem.FOR)
            rules[Elem.FOR]?.shuffled()?.forEach { parseElem(it, node, depth + 1, Elem.FOR) }
            node
        }
    }
}