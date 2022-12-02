package codegen

enum class Elem {
    CLASS, FIELD, METHOD, IF, FOR, VAR_DECL, METHOD_CALL, RETURN
}

val rules = hashMapOf(
    Elem.CLASS to listOf(Elem.FIELD, Elem.METHOD),
    Elem.METHOD to listOf(Elem.IF, Elem.FOR, Elem.VAR_DECL, Elem.METHOD_CALL, Elem.RETURN),
    Elem.IF to listOf(Elem.IF, Elem.FOR, Elem.VAR_DECL, Elem.METHOD_CALL, Elem.RETURN),
    Elem.FOR to listOf(Elem.IF, Elem.FOR, Elem.VAR_DECL, Elem.METHOD_CALL, Elem.RETURN)
)

enum class VarType {
    INT, LONG, FLOAT, BOOL, CHAR, STRING, VOID // todo CUSTOM
}

val vtMap = mapOf(
    VarType.INT to "int",
    VarType.LONG to "long",
    VarType.FLOAT to "float",
    VarType.BOOL to "boolean",
    VarType.CHAR to "char",
    VarType.STRING to "String",
    VarType.VOID to "void"
)