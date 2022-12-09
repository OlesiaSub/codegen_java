package codegen.tree

import codegen.*
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.Charset

class Traverser(private val tree: Tree) {

    val heads: MutableList<GenNode> = mutableListOf()

    fun traverse() {
        tree.heads.forEachIndexed { idx, head ->
            assert(head.type == Elem.CLASS)
            val genNode = GenClassNode()
            val name = generateClassName()
            genNode.type = Elem.CLASS
            val list = heads.subList(0, idx).filterIsInstance<GenClassNode>()
            var parentClass: Clazz? = null
            if (rand(0, 10) > -1 && list.isNotEmpty()) {
                parentClass = list[rand(0, list.size)].classInfo
            }
            genNode.classInfo = Clazz(name, parentClass)
            val actualMethods = parentClass?.methods
            actualMethods?.forEach { it.declaringClass = name }
            parentClass?.let { genNode.classInfo!!.methods.addAll(actualMethods!!) }
            genNode.text = classDecl(name, parentClass) + openFPar()
            heads.add(genNode)
            buildClassBody(head, genNode, name)
        }
    }

    private fun buildClassBody(treeNode: Tree.Node, classNode: GenClassNode, className: String) {
        treeNode.children.forEach { child ->
            when (child.type) {
                Elem.METHOD -> {
                    val genChild = GenMethodNode()
                    genChild.type = Elem.METHOD
                    val type = getRandomType()
                    val methodName = generateMethodName(className)
                    val methodBody = buildMethodBody(type, child, genChild, methodName, classNode)
                    val method = Method(methodName, type, methodBody, listOf(), className)
                    genChild.methodInfo = method
                    genChild.text = methodDecl(method)
                    classNode.children.add(genChild)
                    classNode.classInfo!!.methods.add(method)
                }

                Elem.FIELD -> {
                    val genChild = GenNode()
                    genChild.type = Elem.FIELD
                    val type = getRandomType(false)
                    val field = Field(generateFieldName(className), type, getDefaultValue(type))
                    genChild.text = fieldDecl(field) + semicolon()
                    classNode.children.add(genChild)
                    classNode.classInfo!!.fields.add(field)
                }

                else -> {}
            }
        }
    }

    private fun buildMethodBody(
        type: VarType,
        node: Tree.Node,
        genNode: GenNode,
        methodName: String,
        classNode: GenClassNode
    ): String {
        node.children.forEach { ch -> processChild(ch, type, genNode, methodName, classNode) }
        val baos = ByteArrayOutputStream()
        gatherMethodBody(genNode, baos)
        baos.write("${returnStmtDefault(type)}${semicolon()}".toByteArray())
        node.children.clear()
        genNode.children.clear()
        return baos.toString(Charset.defaultCharset())
    }

    private fun processChild(
        current: Tree.Node,
        type: VarType,
        genNode: GenNode,
        methodName: String,
        classNode: GenClassNode
    ) {
        when (current.type) {
            Elem.VAR_DECL -> {
                val genChild = GenNode()
                val variable = Variable(generateVarName(methodName), getRandomType(false))
                genChild.text = varDecl(variable) + semicolon()
                genNode.children.add(genChild)
            }

            Elem.METHOD_CALL -> {
                val methodsToCall = mutableListOf<Method>()
                heads.filterIsInstance<GenClassNode>().forEach { h ->
                    h.classInfo?.methods?.forEach { methodsToCall.add(it) }
                }
                if (methodsToCall.isEmpty()) return
                else {
                    val method = methodsToCall[rand(0, methodsToCall.size)]
                    val genChild = GenNode()
                    val variable = Variable(generateVarName(methodName), method.returnType)
                    val call =
                        methodCall(method.name, variable, listOf(), method.declaringClass)
                    genChild.text = call + semicolon()
                    genNode.children.add(genChild)
                }
            }

            Elem.IF -> { // todo add return statements into branches
                val genChild = GenNode()
                val ifStmt = ifStmt(constructCondition(classNode))
                genChild.text = ifStmt + openFPar()
                genNode.children.add(genChild)
                current.children.forEach { processChild(it, type, genChild, methodName, classNode) }
                val baos = ByteArrayOutputStream()
                gatherIfBody(genChild, baos)
                baos.write(closeFPar().toByteArray())
                genChild.text = baos.toString(Charset.defaultCharset())
                current.children.clear()
                genChild.children.clear()
            }

            Elem.FOR -> {
                val genChild = GenNode()
                val typee = listOf(VarType.INT, VarType.FLOAT, VarType.LONG)[rand(0, 3)]
                val i = Variable(generateVarName(methodName), typee)
                val n = getDefaultValue(i.type)
                var start = ""
                while (start > n || start.isEmpty()) {
                    start = getDefaultValue(i.type)
                }
                val forStmt = forStmt(i, n, start)
                genChild.text = forStmt + openFPar()
                genNode.children.add(genChild)
                current.children.forEach { processChild(it, type, genChild, methodName, classNode) }
                val baos = ByteArrayOutputStream()
                gatherIfBody(genChild, baos)
                baos.write(closeFPar().toByteArray())
                genChild.text = baos.toString(Charset.defaultCharset())
                current.children.clear()
                genChild.children.clear()
            }

            else -> {}
        }
    }

    private fun constructCondition(classNode: GenClassNode): String {
        if (rand(0, 2) == 0) {
            classNode.classInfo!!.fields.shuffle()
            classNode.classInfo!!.fields.forEach {
                return when (it.type) {
                    VarType.INT -> "${it.name} >= 15"
                    VarType.LONG -> "${it.name} > 35391L"
                    VarType.FLOAT -> "${it.name} < 21.5f"
                    VarType.STRING -> "${it.name}.length() >= 7"
                    VarType.BOOL -> it.name
                    VarType.CHAR -> "${it.name} < 'k'"
                    else -> "true"
                }
            }
        } else {
            classNode.classInfo!!.methods.shuffle()
            classNode.classInfo!!.methods.forEach {
               return when (it.returnType) {
                    VarType.INT -> "${it.name}() > 23"
                    VarType.LONG -> "${it.name}() < 84391L"
                    VarType.FLOAT -> "${it.name}() >= 42.44f"
                    VarType.STRING -> "${it.name}().length() < 12"
                    VarType.BOOL -> "${it.name}()"
                    VarType.CHAR -> "${it.name}() > 'g'"
                    else -> "true"
                }
            }
        }
        return "true"
    }

    private fun gatherMethodBody(node: GenNode, outputStream: OutputStream) {
        outputStream.write(node.text.toByteArray())
        node.children.forEach { gatherMethodBody(it, outputStream) }
    }

    private fun gatherIfBody(node: GenNode, outputStream: OutputStream) {
        outputStream.write(node.text.toByteArray())
        node.children.forEach { gatherIfBody(it, outputStream) }
    }
}

open class GenNode {
    val children: MutableList<GenNode> = mutableListOf()
    var text = ""
    lateinit var type: Elem
//    var methodInfo: Method? = null // супер костыль потому что мне нужна инфа о методе, если нода метод (лучше сделать отдельный класс!)
}

class GenMethodNode : GenNode() {
    var methodInfo: Method? = null
}

class GenClassNode : GenNode() {
    var classInfo: Clazz? = null
}