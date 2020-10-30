package mastermind

data class Evaluation(val rightPosition: Int, val wrongPosition: Int)

fun evaluateGuess(secret: String, guess: String): Evaluation {
    fun String.count(x: Char): Int = run {
        var count = 0
        for (c in this) {
            if (x == c) {
                count++
            }
        }
        return count
    }

    var right = 0
    var wrong = 0
    var newSecret = ""
    var newGuess = ""

    for (i in 0 until 4) {
        if (guess[i] == secret[i]) {
            right++
        } else {
            newSecret += secret[i]
            newGuess += guess[i]
        }
    }

    for (c in 'A' .. 'F') {
        wrong += newSecret.count(c).coerceAtMost(newGuess.count(c))
    }

    return Evaluation(right, wrong)
}
