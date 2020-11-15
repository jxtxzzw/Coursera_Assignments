package nicestring

fun String.isNice(): Boolean {
    val notBad = !contains(Regex("b[uae]"))
    val threeVowels = count { e -> e in listOf('a', 'e', 'i', 'o', 'u') } >= 3
    val doubled = contains(Regex("(.)\\1{1}"))

    return listOf(notBad, threeVowels, doubled).filter { it }.count() >= 2

}