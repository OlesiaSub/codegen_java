package codegen

import codegen.traverse.GenNode
import java.io.OutputStream

fun printTree(node: GenNode, outputStream: OutputStream) {
    printRec(node, outputStream)
    outputStream.write(closeFPar().toByteArray())
}

fun printRec(node: GenNode, outputStream: OutputStream) {
    outputStream.write(node.text.toByteArray())
    node.children.forEach { printRec(it, outputStream) }
}