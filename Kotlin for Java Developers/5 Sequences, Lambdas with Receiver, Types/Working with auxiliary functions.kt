interface X {
    var first: Int
    var second: Int
    var third: Int
}

interface Y {
    fun start()
    fun finish()
}

interface Z {
    fun init()
}

fun foo(x: X, y: Y?, z: Z) {
    with(x) {
        first = 1
        second = 2
        third = 3
    }
    y?.run {
        start()
        finish()
    }
    val result = z.apply {
        init()
    }
}

fun foo_todo(x: X, y: Y?, z: Z) {
    x.let {
        it.first = 1
        it.second = 2
        it.third = 3
    }
    y?.let {
        with(it) {
            start()
            finish()
        }
    }
    val result = with(z) {
        init()
        this
    }
}