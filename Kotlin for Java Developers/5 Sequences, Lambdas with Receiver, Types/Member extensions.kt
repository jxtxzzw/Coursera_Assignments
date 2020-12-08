class Words {
    private val list = mutableListOf<String>()

    fun String.record() {
    	list += this
    }

    fun initialize() {
    	"first".record()
    }

    operator fun String.unaryPlus() {
    	record()
    }

    override fun toString() = list.toString()
}

fun Words.addFoo() {
	"foo".record()
}

fun main(args: Array<String>) {
    val words = Words()
    with(words) {
        // The following two lines should compile:
        "one".record()
        +"two"
    }
    words.toString() eq "[one, two]"
}