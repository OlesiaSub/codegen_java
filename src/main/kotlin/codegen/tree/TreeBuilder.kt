package codegen.tree

import codegen.*
import kotlin.random.Random

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
    }

    private fun buildMethod(): Tree.Node {
        val methodNode = Tree.Node(Elem.METHOD)
        val methodCallNum = rand(1, MAX_METHOD_CALLS)
        val varDeclNum = rand(0, MAX_VAR_DECLS)
        val ifNum = rand(1, MAX_IFS)
//        val forNum = rand(0, MAX_FORS) // todo
        for (i in 0 until methodCallNum) {
            methodNode.children.add(Tree.Node(Elem.METHOD_CALL))
        }
        for (i in 0 until varDeclNum) {
            methodNode.children.add(Tree.Node(Elem.VAR_DECL))
        }
        for (i in 0 until ifNum) {
            buildIf(0)?.let { methodNode.children.add(it) }
        }
        return methodNode
    }

    private fun buildIf(depth: Int): Tree.Node? { // todo use grammar
        if (depth >= MAX_DEPTH || rand(1, MAX_DEPTH) > 2) {
            return null
        } else {
            val ifNode = Tree.Node(Elem.IF)
            val methodCallNum = rand(1, MAX_METHOD_CALLS - 1)
            val varDeclNum = rand(0, MAX_VAR_DECLS - 1)
            val ifNum = rand(0, MAX_IFS - 1)
            for (i in 0 until methodCallNum) {
                ifNode.children.add(Tree.Node(Elem.METHOD_CALL))
            }
            for (i in 0 until varDeclNum) {
                ifNode.children.add(Tree.Node(Elem.VAR_DECL))
            }
            for (i in 0 until ifNum) {
                buildIf(0)?.let { ifNode.children.add(it) }
            }
            return ifNode
        }
    }

    private fun buildFor(depth: Int): Tree.Node? {
        if (depth >= MAX_DEPTH || rand(1, MAX_DEPTH) > 2) {
            return null
        }
        return null
    }

    private fun rand(from: Int, to: Int) = Random.nextInt(from, to)
}