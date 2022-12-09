package codegen.tree

import codegen.*

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

    private fun parseElem(elem: Elem, node: Tree.Node, depth: Int = 0) {
        val fieldsNum = rand(1, MAX_FIELDS)
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
//                    Elem.RETURN -> node.children.add(Tree.Node(r))
                else -> {}
            }
        }
    }

    private fun buildMethod(): Tree.Node {
        val node = Tree.Node(Elem.METHOD)
        rules[Elem.METHOD]?.forEach { parseElem(it, node) }
        println()
        return node
    }

    private fun buildIf(depth: Int): Tree.Node? {
        return if (depth > MAX_DEPTH || rand(1, 6) > 2) {
            null
        } else {
            val node = Tree.Node(Elem.IF)
            rules[Elem.IF]?.forEach { parseElem(it, node, depth + 1) }
            node
        }
    }

    private fun buildFor(depth: Int): Tree.Node? {
        return if (depth > MAX_DEPTH || rand(1, 5) > 2) {
            null
        } else {
            val node = Tree.Node(Elem.FOR)
            rules[Elem.FOR]?.forEach { parseElem(it, node, depth + 1) }
            node
        }
    }
}