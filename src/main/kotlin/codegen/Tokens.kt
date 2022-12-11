package codegen

import java.util.*
import kotlin.random.Random

/**
 * File with representation of [Elem]s and util functions for code generation
 */

data class Variable(val name: String, val type: VarType)

data class Clazz(val name: String, val parentClass: Clazz?) {
    val fields: MutableList<Field> = mutableListOf()
    val methods: MutableList<Method> = mutableListOf()
}

data class Method(
    val name: String,
    val returnType: VarType,
    val body: String,
    val params: List<Variable>,
    var declaringClass: String
)

data class Field(val name: String, val type: VarType, val initialValue: String?)

private var classNamesCount = 0

fun classDecl(className: String, parentClass: Clazz?) =
    if (parentClass == null) {
        "public class $className"
    } else {
        "public class $className extends ${parentClass.name}"
    }

fun generateClassName() = "Class_${classNamesCount++}"

// todo remove static
fun methodDecl(method: Method) = "public static ${vtMap[method.returnType]} ${method.name}() {\n" +
        method.body +
        "}\n"

fun fieldDecl(field: Field) = "public static ${vtMap[field.type]} ${field.name} = ${field.initialValue}"

fun varDecl(variable: Variable) = "${vtMap[variable.type]} ${variable.name} = ${getDefaultValue(variable.type)}"

fun openFPar() = " {\n"

fun closeFPar() = "}\n"

fun semicolon() = ";\n"

fun ifStmt(cond: String) = "if ($cond)"

fun forStmt(i: Variable, n: String, start: String) =
    "for (${vtMap[i.type]} ${i.name} = $start; ${i.name} < $n; ${i.name}++)"

fun methodCall(methodName: String, assignTo: Variable, params: List<Variable>, declaringClass: String) =
    "${vtMap[assignTo.type]} ${assignTo.name} = $declaringClass.$methodName(${insertParams(params)})"

fun returnStmtDefault(type: VarType): String {
    val value = getDefaultValue(type)
    return if (value == "void") "return"
    else "return ${getDefaultValue(type)}"
}

var methodNamesCountMap = mutableMapOf<Int, Int>() // class -> number of methods

fun generateMethodName(className: String): String {
    val name = "${className.lowercase(Locale.getDefault())}_method_${
        methodNamesCountMap[className.substringAfterLast("_").toInt()]
    }"
    methodNamesCountMap[className.substringAfterLast("_").toInt()] =
        methodNamesCountMap[className.substringAfterLast("_").toInt()]?.plus(1)!!
    return name
}

var fieldsNamesCountMap = mutableMapOf<Int, Int>() // class -> number of fields

fun generateFieldName(className: String): String {
    val name = "${className.lowercase(Locale.getDefault())}_field_${
        fieldsNamesCountMap[className.substringAfterLast("_").toInt()]
    }"
    fieldsNamesCountMap[className.substringAfterLast("_").toInt()] =
        fieldsNamesCountMap[className.substringAfterLast("_").toInt()]?.plus(1)!!
    return name
}

fun generateVarName(methodName: String) = "var_${methodName}_${rand(0, 10000)}"

fun getRandomType(canBeVoid: Boolean = true): VarType {
    val types = mutableListOf(VarType.INT, VarType.LONG, VarType.FLOAT, VarType.BOOL, VarType.CHAR, VarType.STRING)
//    if (canBeVoid) types.addAll(listOf(VarType.VOID, VarType.VOID))
    return types[rand(0, types.size)]
}

/**
 * Gets random value that corresponds to the provided type
 */
fun getDefaultValue(varType: VarType): String {
    val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return when (varType) {
        VarType.INT -> rand(-1000, 2000).toString()
        VarType.LONG -> Random.nextLong(3214L, 136812497L).toString()
        VarType.FLOAT -> "${(Random.nextFloat() * Random.nextInt(2435))}f"
        VarType.BOOL -> Random.nextBoolean().toString()
        VarType.CHAR -> "'${charPool[rand(0, charPool.size)]}'"
        VarType.STRING -> "\"${
            (3..rand(5, 17))
                .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
                .joinToString("")
        }\""

//        VarType.VOID -> "void"
    }
}

fun rand(from: Int, to: Int) = Random.nextInt(from, to)

private fun insertParams(params: List<Variable>): String {
    var res = ""
    params.forEach { res += "${it.name}, " }
    return if (res.length > 0) {
        res.substring(0, res.length - 2)
    } else res
}
