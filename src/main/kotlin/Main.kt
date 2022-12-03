import codegen.*
import codegen.tree.Traverser
import codegen.tree.Tree
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.outputStream

// todo можно сделать базовый класс с рандомными методами и из него тоже дергать, чтобы было меньше рисков зациклиться

fun main(args: Array<String>) { // todo construct many cases
    for (i in 0 until NUMBER_OF_CASES) {
        init()
        val tree = Tree()
        tree.build()
        val traverser = Traverser(tree)
        traverser.traverse()
        traverser.heads.forEachIndexed { i, node ->
            run {
                val path = Path.of("$DEST_FOLDER${File.separatorChar}Class_$i.java")
                if (Files.exists(path)) Files.delete(path)
                val file = Files.createFile(path)
                printTree(node, file.outputStream())
            }
        }
    }
}

fun init() {
    for (i in 0 until CLASSES) {
        methodNamesCountMap[i] = 0
        fieldsNamesCountMap[i] = 0
    }
}