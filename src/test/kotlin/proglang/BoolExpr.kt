import proglang.IntExpr
import proglang.eval

interface BoolExpr {
    class LessThan(val lhs:IntExpr, val rhs: IntExpr): BoolExpr{
        override fun toString(): String {
            return "$lhs < $rhs"
        }
    }
    class GreaterThan (val lhs: IntExpr, val rhs: IntExpr): BoolExpr{
        override fun toString(): String {
            return "$lhs > $rhs"
        }
    }
    class Equals(val lhs: IntExpr, val rhs: IntExpr): BoolExpr {
        override fun toString(): String {
            return "$lhs == $rhs"
        }
    }
    class And(val lhs: BoolExpr, val rhs: BoolExpr): BoolExpr {
        override fun toString(): String {
            return "$lhs && $rhs"
        }

    }
    class Or(val lhs: BoolExpr, val rhs: BoolExpr): BoolExpr{
        override fun toString(): String {
            return "$lhs || $rhs"
        }
    }
    class Not(val target: BoolExpr): BoolExpr{
        override fun toString(): String {
            return "!$target"
        }
    }
    class Paren(val target: BoolExpr): BoolExpr{
        override fun toString(): String {
            return "($target)"
        }
    }

}

fun BoolExpr.eval(store: Map<String, Int>): Boolean = when (this) {
    is BoolExpr.LessThan ->  lhs.eval(store) < rhs.eval(store)
    is BoolExpr.GreaterThan -> lhs.eval(store) > rhs.eval(store)
    is BoolExpr.Equals -> lhs.eval(store) == rhs.eval(store)
    is BoolExpr.And -> lhs.eval(store) && rhs.eval(store)
    is BoolExpr.Or  -> lhs.eval(store) || rhs.eval(store)
    is BoolExpr.Not -> !target.eval(store)
    is BoolExpr.Paren -> target.eval(store)


    else -> throw UnsupportedOperationException("The above should account for all kinds of BoolExpr.")


}