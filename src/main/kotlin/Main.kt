import codegen.*
import codegen.traverse.Traverser
import scheme.Tree
import config.CLASSES
import config.DEST_FOLDER
import config.NUMBER_OF_CASES
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.io.path.outputStream

fun main(args: Array<String>) {
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
                Logger.getLogger("Main").log(Level.INFO, "Successfully generated Class_$i.java")
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