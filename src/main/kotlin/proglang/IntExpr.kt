package proglang

sealed interface IntExpr {
    class Add(val lhs: IntExpr, val rhs: IntExpr) : IntExpr{
        override fun toString(): String {
            return "$lhs + $rhs"
        }
    }
    class Sub(val lhs: IntExpr, val rhs: IntExpr): IntExpr{
        override fun toString(): String {
            return "$lhs - $rhs"
        }
    }
    class Literal(val value: Int): IntExpr {
        override fun toString(): String {
            return "$value"
        }
    }
    class Var(val name: String): IntExpr {
        override fun toString(): String {
            return name
        }
    }
    class Mul(val lhs: IntExpr, val rhs: IntExpr): IntExpr {
        override fun toString(): String {
            return "$lhs * $rhs"
        }
    }
    class Div(val lhs: IntExpr, val rhs: IntExpr): IntExpr{
        override fun toString(): String {
            return "$lhs / $rhs"
        }
    }
    class Fact(val expr: IntExpr): IntExpr {
        override fun toString(): String {
            return "$expr!"
        }
    }
    class Paren(val expr: IntExpr): IntExpr{
        override fun toString(): String {
            return "($expr)"
        }
    }
}

fun IntExpr.eval(store: Map<String, Int>): Int = when (this) {
    is IntExpr.Add -> lhs.eval(store) + rhs.eval(store)
    is IntExpr.Sub -> lhs.eval(store) - rhs.eval(store)
    is IntExpr.Literal -> value
    is IntExpr.Var     -> if (store[name] == null) {
        throw UndefinedBehaviourException("store does not provide value for name")
    }
    else{
        store[name]!!
    }
    is IntExpr.Mul -> lhs.eval(store) * rhs.eval(store)
    is IntExpr.Div -> if (rhs.eval(store) == 0) {
        throw UndefinedBehaviourException("right hand side evaluates to 0")
    } else {
        lhs.eval(store) / rhs.eval(store)
    }
    is IntExpr.Fact ->
    { fun fact(n: Int): Int {
        if (n<0) {
            throw UndefinedBehaviourException("result is negative")
        }
        else {
            if (n<=1) return 1
        }
        return n* fact(n-1)
    }
        fact(expr.eval(store)) }

    is IntExpr.Paren -> expr.eval(store)

}
