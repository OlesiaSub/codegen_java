package codegen

import java.util.*
import kotlin.random.Random

data class Variable(val name: String, val type: VarType)

data class Clazz(val name: String) {
    val fields: MutableList<Field> = mutableListOf()
    val methods: MutableList<Method> = mutableListOf()
}

data class Method(val name: String, val returnType: VarType, val body: String, val params: List<Variable>, val declaringClass: String)

data class Field(val name: String, val type: VarType, val initialValue: String?)

private var classNamesCount = 0

fun classDecl(className: String) = "public class $className"

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

fun forStmt(i: Variable, n: Variable, start: String) =
    "for (${vtMap[i.type]} ${i.name} = $start; ${i.name} < ${n.name}; ${i.name}++)"

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

fun generateVarName(methodName: String) = "var_${methodName}_${Random.nextInt(0, 10000)}"

fun getRandomType(canBeVoid: Boolean = true): VarType {
    val types = mutableListOf(VarType.INT, VarType.LONG, VarType.FLOAT, VarType.BOOL, VarType.CHAR, VarType.STRING)
    if (canBeVoid) types.addAll(listOf(VarType.VOID, VarType.VOID))
    return types[Random.nextInt(0, types.size)]
}

fun getDefaultValue(varType: VarType): String {
    return when (varType) {
        VarType.INT -> listOf("0", "24", "13", "134", "-12")[Random.nextInt(0, 5)]
        VarType.LONG -> listOf("0L", "1354624L", "1314352L", "8888L", "-121245L")[Random.nextInt(0, 5)]
        VarType.FLOAT -> listOf("0.00f", "24.24f", "58.00f", "124.90f", "2.23f")[Random.nextInt(0, 5)]
        VarType.BOOL -> listOf("true", "false")[Random.nextInt(0, 2)]
        VarType.CHAR -> listOf("'a'", "'b'", "'c'", "'d'", "'e'")[Random.nextInt(0, 5)]
        VarType.STRING -> listOf(
            "\"hello, world!\"",
            "\"hehehe\"",
            "\"StRiNg\"",
            "\"next \n line\"",
            "\"kill me please,,,\""
        )[Random.nextInt(0, 5)]
        VarType.VOID -> "void"
    }
}

private fun insertParams(params: List<Variable>): String {
    var res = ""
    params.forEach { res += "${it.name}, " }
    return if (res.length > 0) {
        res.substring(0, res.length - 2)
    } else res
}
