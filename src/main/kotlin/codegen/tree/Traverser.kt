package codegen.tree

import codegen.*
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import kotlin.random.Random

class Traverser(private val tree: Tree) {

    val heads: MutableList<GenNode> = mutableListOf()

    fun traverse() {
        tree.heads.forEach { head ->
            assert(head.type == Elem.CLASS)
            val genNode = GenNode()
            genNode.type = Elem.CLASS
            val name = generateClassName()
            genNode.text = classDecl(name) + openFPar()
            heads.add(genNode)
            buildClassBody(head, genNode, name)
        }
    }

    fun buildClassBody(treeNode: Tree.Node, classNode: GenNode, className: String) {
        treeNode.children.forEach { child ->
            when (child.type) {
                Elem.METHOD -> {
                    val genChild = GenMethodNode()
                    genChild.type = Elem.METHOD
                    val type = getRandomType()
                    val methodName = generateMethodName(className)
                    val methodBody = buildMethodBody(type, child, genChild, methodName)
                    val method = Method(methodName, type, methodBody, listOf())
                    genChild.methodInfo = method
                    genChild.text = methodDecl(method)
                    classNode.children.add(genChild)
                }

                Elem.FIELD -> {
                    val genChild = GenNode()
                    genChild.type = Elem.FIELD
                    val type = getRandomType(false)
                    val field = Field(generateFieldName(className), type, getDefaultValue(type))
                    genChild.text = fieldDecl(field) + semicolon()
                    classNode.children.add(genChild)
                }

                else -> {}
            }
        }
    }

    private fun buildMethodBody(type: VarType, node: Tree.Node, genNode: GenNode, methodName: String): String {
        node.children.forEach { ch ->
            when (ch.type) {
                Elem.VAR_DECL -> {
                    val genChild = GenNode()
                    val variable = Variable(generateVarName(methodName), getRandomType(false))
                    genChild.text = varDecl(variable) + semicolon()
                    genNode.children.add(genChild)
                }

                Elem.METHOD_CALL -> {
                    val methodsToCall = mutableListOf<GenMethodNode>()
                    heads.forEach { h ->
                        h.children.filterIsInstance<GenMethodNode>().forEach { methodsToCall.add(it) }
                    }
                    if (methodsToCall.isEmpty()) return@forEach
                    else {
                        val method = methodsToCall[Random.nextInt(0, methodsToCall.size)]
                        val genChild = GenNode()
                        val variable = Variable(generateVarName(methodName), method.methodInfo!!.returnType)
                        val call = methodCall(method.methodInfo!!.name, variable, listOf())
                        genChild.text = call + semicolon()
                        genNode.children.add(genChild)
                    }
                }

                else -> {}
            }
        }
        val baos = ByteArrayOutputStream()
        gatherMethodBody(genNode, baos)
        baos.write("${returnStmtDefault(type)}${semicolon()}".toByteArray())
        node.children.clear()
        genNode.children.clear()
        return baos.toString()
    }

    private fun gatherMethodBody(node: GenNode, outputStream: OutputStream) {
        outputStream.write(node.text.toByteArray())
        node.children.forEach { gatherMethodBody(it, outputStream) }
    }
}

open class GenNode {
    val children: MutableList<GenNode> = mutableListOf()
    var text = ""
    lateinit var type: Elem
//    var methodInfo: Method? = null // супер костыль потому что мне нужна инфа о методе, если нода метод (лучше сделать отдельный класс!)
}

class GenMethodNode: GenNode() {
    var methodInfo: Method? = null
}