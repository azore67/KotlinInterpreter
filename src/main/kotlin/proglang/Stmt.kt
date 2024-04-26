package proglang
import BoolExpr
import eval

interface Stmt{
    var next: Stmt?
    val lastInSequence: Stmt
        get(){
            var currentStmt: Stmt? = this

            while (currentStmt?.next != null) {
                currentStmt = currentStmt.next!!
            }
            return currentStmt!!
        }
    abstract fun toString(indent:Int):String
    abstract fun clone(): Stmt

    class Assign(val varName: String, val expr: IntExpr, override var next: Stmt? = null): Stmt {
        override fun toString(indent: Int): String {
            val indentSpaces = " ".repeat(indent)
            var completeStringRep = "$indentSpaces$varName = $expr\n"

            if (next != null) {
                completeStringRep += next!!.toString(indent)
            }
            return completeStringRep
        }
        // Override toString from Any for string representation with no indentation
        override fun toString(): String {
            return toString(0)
        }
        override fun clone(): Stmt {
            return Assign(varName,expr,next?.clone())
        }



    }


    //    Implement the version of toString required by the Stmt interface, which takes an indent
//    parameter. This should construct a string representing the sequence of statements headed
//    by the receiving If statement. The statements in this sequence should all be indented
//    using indent spaces.
//
//    The “then” branch of the conditional statement, and the “else” branch if it is present,
//    should be indented by indent + 4 spaces. However, you should use a constant so that it
//    would be easy to change the indentation level of 4 to some different value.
//    Refer to the examples under “Overview of the simple language” above, and the test cases
//    in Question3Tests, to guide you in how conditional statement should be represented as
//    suitably-indented strings.
//    Override toString from Any so that it provides a string representation of an If statement
//    with no indentation.
//
    class If(val condition: BoolExpr, val thenStmt: Stmt, val elseStmt: Stmt?= null, override var next: Stmt?= null):
        Stmt {
        private val branchIndent = 4
        override fun toString(indent: Int): String {
            val indentSpace = " ".repeat(indent)
            var ifStmt = "${indentSpace}if ($condition) {\n"
            ifStmt += thenStmt.toString(indent +branchIndent)
            ifStmt +="$indentSpace} "
            if (elseStmt!= null) {
                ifStmt += "else {\n"
                ifStmt += elseStmt.toString(indent + branchIndent)
                ifStmt +="$indentSpace}\n"
            }
            if (next != null) {
                ifStmt += next!!.toString(indent)
            }
            return ifStmt
        }
        // Override toString from Any for string representation with no indentation
        override fun toString(): String {
            return toString(0)
        }

        override fun clone(): Stmt {
            return If(condition, thenStmt.clone(), elseStmt?.clone(), next?.clone())
        }
    }

    class While(val condition: BoolExpr, val body: Stmt?= null, override var next:Stmt? = null): Stmt {
        private val branchIndent = 4
        override fun toString(indent: Int): String {
            val indentSpace = " ".repeat(indent)
            var whileStmt = "${indentSpace}while($condition){\n"
            if (body!= null) {
                whileStmt += body.toString(indent + branchIndent)
            }
            whileStmt += "$indentSpace}\n"
            if (next != null) {
                whileStmt += next!!.toString(indent)
            }
            return whileStmt


        }
        override fun toString(): String {
            return toString(0)
        }

        override fun clone(): Stmt {
            return While(condition, body?.clone(), next?.clone())
        }
    }

}

fun Stmt.step(store: MutableMap<String, Int>): Stmt? =  when (this) {
    is Stmt.Assign -> {
//         x=10
//        y = 10 + 13
//        z = 1927 &
        try{
            val assignValue = expr.eval(store)
            store [varName] = assignValue
            next
        } catch (e: UndefinedBehaviourException) {
            throw e
        }
    }
    is Stmt.If -> {
        val conditionValue = condition.eval(store)
        if (conditionValue) {
            thenStmt.lastInSequence.next = next
            thenStmt


        } else {
            if (elseStmt != null) {
                elseStmt.lastInSequence.next = next
                elseStmt
            }
            next

        }

    }

    is Stmt.While -> {
        val conditionValue = condition.eval(store)

        var r: Stmt? = null
    if (conditionValue) {
        if (body != null) {
            val newBody = body.clone()
            newBody.lastInSequence.next = this
            r = newBody
        }
        else {
            r = this
        }
    }
    else {
        r = next
    }
        r


    }

    else -> null

}


fun main() {
// x = 2
//   y = 23 + 89

    val assignmentExpre = Stmt.Assign("x", IntExpr.Literal(2))
    val assignmentAdd = Stmt.Assign("y", IntExpr.Add(IntExpr.Literal(23), IntExpr.Literal(89)))
    // println(assignmentExpre.toString())
    // println(assignmentAdd.toString())

//    if (x > 10){
//       y = 18
//     }else{
//    y = -1
//}
    val condition = BoolExpr.GreaterThan(IntExpr.Var("x"), (IntExpr.Literal(10)))
    val thenStmt = Stmt.Assign("y", IntExpr.Literal(18))
    val elseStmt = Stmt.Assign("y", IntExpr.Literal(-1))
    // val ifStmt = Stmt.If(condition, thenStmt, elseStmt)


    //println(ifStmt.toString(2))
    //    if (x > 10){
//            if( y == 18){
//    z = 90
//}
//     }else{
//    y = -1
//}
    val nestedCondition = BoolExpr.Equals(IntExpr.Var("y"), (IntExpr.Literal(18)))
    val nestedThenStmt = Stmt.Assign("z", IntExpr.Literal(90))
    val nestedIfStmt = Stmt.If(nestedCondition, nestedThenStmt)
    val ifStmt = Stmt.If(condition, nestedIfStmt, elseStmt)
    // println(ifStmt.toString(2))

//    while(x>10){
//    z = 90
//}
    val body = Stmt.Assign("z", IntExpr.Literal(90))
    val whileStmt = Stmt.While(condition,body)
    println(whileStmt.toString())

}

