package rationals

import java.lang.StringBuilder
import java.math.BigInteger

class Rational(private var numerator: BigInteger, private var denominator: BigInteger): Comparable<Rational> {

    operator fun plus(other: Rational): Rational = run {
        val newNumerator = numerator * other.denominator + denominator * other.numerator
        val newDenominator = denominator * other.denominator
        return Rational(newNumerator, newDenominator)
    }

    operator fun minus(other: Rational): Rational = run {
        return this + (-other)
    }

    operator fun times(other: Rational): Rational = run {
        val newNumerator = numerator *  other.numerator
        val newDenominator = denominator * other.denominator
        return Rational(newNumerator, newDenominator)
    }

    operator fun div(other: Rational): Rational = run {
        return this * other.reciprocal()
    }

    operator fun unaryMinus(): Rational {
        return Rational(-numerator, denominator)
    }

    private fun reciprocal(): Rational {
        return Rational(denominator, numerator)
    }

    private fun gcd(a: BigInteger, b: BigInteger): BigInteger = run {
        return if (b == BigInteger.ZERO) {
            a
        } else {
            gcd(b, a.mod(b))
        }
    }

    override fun equals(other: Any?): Boolean {
        return toString() == other.toString()
    }

    override fun toString(): String {
        val sb = StringBuilder()
        val g = gcd(numerator.abs(), denominator.abs())
        if (denominator < BigInteger.ZERO) {
            denominator *= BigInteger("-1")
            numerator *= BigInteger("-1")
        }
        sb.append((numerator / g).toString())
        if (denominator / g != BigInteger.ONE) {
            sb.append("/")
            sb.append((denominator / g).toString())
        }
        return sb.toString()
    }

    override operator fun compareTo(other: Rational): Int {
        val numeratorA = numerator * other.denominator
        val numeratorB = other.numerator * denominator
        return numeratorA.compareTo(numeratorB)
    }

}

infix fun Int.divBy(i: Int): Rational {
    return Rational(toBigInteger(), i.toBigInteger())
}

infix fun Long.divBy(l: Long): Rational {
    return Rational(toBigInteger(), l.toBigInteger())
}

infix fun BigInteger.divBy(b: BigInteger): Rational {
    return Rational(this, b)
}

fun String.toRational(): Rational = run {
    val splitIndex = this.indexOf('/')
    return if (splitIndex > 0) {
        val numerator = this.substring(0, splitIndex).toBigInteger()
        val denominator = this.substring(splitIndex + 1).toBigInteger()
        Rational(numerator, denominator)
    } else {
        val numerator = this.toBigInteger()
        Rational(numerator, BigInteger.ONE)
    }
}

fun main() {
    val half = 1 divBy 2
    val third = 1 divBy 3

    val sum: Rational = half + third
    println(5 divBy 6 == sum)

    val difference: Rational = half - third
    println(1 divBy 6 == difference)

    val product: Rational = half * third
    println(1 divBy 6 == product)

    val quotient: Rational = half / third
    println(3 divBy 2 == quotient)

    val negation: Rational = -half
    println(-1 divBy 2 == negation)

    println((2 divBy 1).toString() == "2")
    println((-2 divBy 4).toString() == "-1/2")
    println("117/1098".toRational().toString() == "13/122")

    val twoThirds = 2 divBy 3
    println(half < twoThirds)

    println(half in third..twoThirds)

    println(2000000000L divBy 4000000000L == 1 divBy 2)

    println("912016490186296920119201192141970416029".toBigInteger() divBy
            "1824032980372593840238402384283940832058".toBigInteger() == 1 divBy 2)
}
