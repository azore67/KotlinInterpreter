package proglang
import BoolExpr
import eval

interface Stmt{
    var next: Stmt?
    val lastInSequence: Stmt?
        get() = if (next == null) this else {next!!.lastInSequence}

    abstract fun toString(indent:Int):String

    class Assign(val name: String, val expr: IntExpr,override var next: Stmt? = null):Stmt{
        override fun toString(indent: Int): String {
            val sb = StringBuilder()
            sb.append(" ".repeat(indent))
            sb.append("$name = $expr \n")
            next?.let {
                sb.append(it.toString(indent))
            }
            return sb.toString()
        }
        // Override toString from Any for string representation with no indentation
        override fun toString(): String {
            return toString(0)
        }
    }
    class If(val condition: BoolExpr, val thenStmt: Stmt, val elseStmt:Stmt?= null, override var next: Stmt?= null): Stmt {
        private val branchIndent = 4

        override fun toString(indent: Int): String {
            val sb = StringBuilder()
            sb.append(" ".repeat(indent))
            sb.append("if (${condition.toString()}) {\n")
            sb.append(thenStmt.toString(indent + branchIndent))
            sb.append(" ".repeat(indent))
            sb.append("} ")
            elseStmt?.let {
                sb.append("else {\n")
                sb.append(it.toString(indent + branchIndent))
                sb.append(" ".repeat(indent))
                sb.append("}\n")
            }
            next?.let {
                sb.append(it.toString(indent))
            }
            return sb.toString()
        }

        // Override toString from Any for string representation with no indentation
        override fun toString(): String {
            return toString(0)
        }
    }
}

fun Stmt.step(store: MutableMap<String, Int>): Stmt? =  when (this) {
    is Stmt.Assign -> {
        try{
            val assignValue = expr.eval(store)
            store [name] = assignValue
            next
        }
        catch (e: UndefinedBehaviourException) {
            throw e
        }
    }
    is Stmt.If -> {
        val conditionValue = condition.eval(store)
        if (conditionValue) {
            thenStmt

        } else {
            elseStmt
        }



    }

    else -> next

}

















fun main() {
    val stmt1c = Stmt.Assign("c", IntExpr.Div(IntExpr.Var("c"), IntExpr.Var("a")))
    val stmt1b = Stmt.Assign(
        "b",
        IntExpr.Literal(12),
        stmt1c,
    )
    val stmt1a: Stmt = Stmt.Assign("a", IntExpr.Var("b"), stmt1b)
    val expected = """
            a = b
            b = 12
            c = c / a

        """.trimIndent()

    println(stmt1a.toString())
    println(expected)



}




