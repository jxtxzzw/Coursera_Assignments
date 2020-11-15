# Kotlin for Java Developers

> Coursera 课程 Kotlin for Java Developers（由 JetBrains 提供）的学习笔记

## From Java to Kotlin

Java 和 Kotlin 代码可以相互转化

```java
public class Person {
    private final String name;
    private final int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() {
        return name;
    }
    
    public int getAge() {
        return age;
    }
}
```

```kotlin
class Person(val name: String, val age: Int)
```

Kotlin 被编译为 Java 字节码，所以从 Java 代码的层面看，这两者是一样的，都有一个 Constructor 和两个 Getter

也可以加上 `data` 修饰符，表示自动生成 `equals`、`hashCode` 和 `toString` 这三个函数

```kotlin
data class Person(val name: String, val age: Int)
```

多个变量可以以 Pair 的形式赋值

```kotlin
val (description: String, color: Color) = Pair("hot", RED)
```

如果数据的类型在上下文中可以很明确地被推导出来，那么可以不用声明变量的类型

```kotlin
val (description: String, color: Color)
val (description, color)
```

多于 2 个 if … else … 时可以使用 when 关键字，类似于 switch，但又有细微区别

```kotlin
val (description, color) = when {
    degrees < 10 -> Pair("cold", BLUE)
    degrees < 25 -> Pair("mild", ORANGE)
    else -> Pair("hot", RED)
}
```

## 基本语法

Kotlin 的代码也是从 main 函数开始

```kotlin
package intro
fun main() {
    val name = "Kotlin"
    println("Hello, $name!")
}
```

从 Kotlin 1.3 开始

```kotlin
fun main(args: Array<String>)
```

可以只写

```kotlin
fun main()
```

### 变量、常量与字符串模板

字符串模板 `$variable`，`${args.getOrNull(0)}`

“变量”分为 `val` 和 `var`，`val` 是只读的

Kotlin 是静态类型的语言，每一个变量都会有自己的类型，但是我们可以在代码中省略基本类型，编译器会自动推断

```kotlin
var s = "abc" // var s: String = "abc"
var v = 123 // var v: Int = 123
```

我们不能给一个类型的变量赋值另一个类型的数据，例如：字符串常量赋值给一个 Int 类型的变量 `string`，这是一个编译时错误

```kotlin
var string = 1
string = "abc" // NOT ALLOWED是不允许的，我们不能把
```

`val` 不对数据做任何强加的限制，仍然可以改变其引用的数据，例如通过 `list.add()` 去修改一个被 `val` 修饰的列表，只要这个列表本身是允许被修改的

```kotlin
val list = mutableListOf("Java") // list.add() 可以往 List 中加东西
val list = listOf("Java") // list.add() 是不存在的
```

### 函数

```kotlin
fun max(a: Int, b: Int): Int {
    return if (a > b) a else b
}
```

如果只有一句话（function wilth expression body），可以写成

```kotlin
fun max(a: Int, b: Int) = if (a > b) a else b
```

`void` 类型的函数在 Kotlin 中会以 `Unit` 的形式返回

Kotlin 的函数可以定义在任何地方：顶层、类的成员、函数中定义另一个函数

调用顶层函数相当于 Java 中的 static 函数

```kotlin
// MyFile.kt
package intro
fun foo() = 0
```

```java
//UsingFoo.java
package other;
import intro.MyFileKt;
public class UsingFoo {
    public static void main(String[] args) {
        MyFileKt.foo();
    }
}
```

为变量提供默认值，不再需要重载各种函数

```kotlin
fun displaySeparatpr(character: Char = '*', size: Int = 10) {
    repeat(size) {
        print(character)
    }
}

displaySeparator() // **********
displaySeparator(size = 5) // *****
displaySeparator(3, '5') // WON'T COMPILE
displaySeparator(size = 3, character = '5') // 555
```

### 分支

在 Kotlin 中，`if` 是表达式

```kotlin
val max = if (a > b) a else b
```

没有三元表达式

```
(a > b) ? a : b
```

注意与 Python 的区别

```python
max = a if a > b else b
```

在 Kotlin 中，`when` 可以当作 `switch` 使用，不需要 `break`

```java
switch (color) {
	case BLUE:
		System.out.println("cold")
		break;
	case ORANGE:
		System.out.println("mild")
		break;
	default:
		System.out.println("hot")
}
```

```kotlin
when (color) {
    BLUE -> println("cold")
    ORANGE -> println("mild")
    else -> println("hot")
}
```

可以使用任何类型，可以用来判断多个条件

```kotlin
fun response(input: String) = when (input) {
	"y", "yes" -> "Agree"
	"n", "no" -> "Sorry"
	else -> "Not Understand"
}
```

可以做类型检查

```java
if (pet instanceof Cat) {
    ((Cat) pet).meow();
} else if (pet instanceof Dog) {
	Dog dog = (Dog) pet;
	dog.woof();
}
```

```kotlin
when (pet) {
    is Cat -> pet.meow()
    is Dog -> pet.woof()
}
```

可以不需要参数

```kotlin
fun updateWeather(degrees: Int) {
	val (desc, color) = when {
        degrees < 5 -> "cold" to BLUE
        degrees < 23 -> "mild" to ORANGE
        else -> "hot" to RED
	}
}
```

### 循环

```kotlin
val map = mapOf(1 to "one", 2 to "two", 3 to "three")
for ((key, value) in map) {
    println("$key = $value")
}
```

```kotlin
val list = listOf("a", "b", "c")
for ((index, element) in list.withIndex()) {
    println("$index: $element")
}
```

```kotlin
for (i in 1..9) // 1 2 3 4 5 6 7 8 9

for (i in 1 until 9) // 1 2 3 4 5 6 7 8
```

```kotlin
for (ch in "abc")
```

```kotlin
for (i in 9 downTo 1 step 2) // 9 7 5 3 1
```

### 拓展函数

```kotlin
fun String.lastChar() = get(length - 1)
val c: Char = "abc".lastChar()
```

也可以直接在 Java 中使用 Kotlin 中定义的拓展函数

```java
import lastChar from ExtensionFunctions.kt;
char c = lastChar("abc");
```

常用的有 `withIndex`、`getOrNull`、`joinToString`、`until`、`to` 、`eq`、`toInt` 等等

```kotlin
val set = hashSetOf(1, 7, 53)
println(set.javaClass) // class java.util.HashSet
```

```kotlin
fun main(args: Array<String>) {
    println("Hello, ${args.getOrNull(0)}!")
}
```

```kotlin
val regex = """\d{2}\.\d{2}\.\d{4}""".toRegex()
regex.matches("15.02.2016") // true
regex.matches("15.02.16") // false
```

特别的，`until` 和 `to` 这种，本身是需要通过 `.` 和 `()` 调用的

```kotlin
1.until(10)
"Answer".to(42)
```

但是因为原型声明的时候允许 `infix`

```kotlin
infix fun Int.until(to: Int) = IntRange
infix fun <A, B> A.to(that: B) = pair(this, that)
```

所以可以省略 `.` 和 `()`

```kotlin
1 until 10
"Answer" to 42
```

成员函数比拓展函数的优先级高，例如下例会输出 `1`，并得到一个警告，说 entension is shadowed by a member

```kotlin
class A {
    fun foo() = 1
}

fun A.foo() = 2 // Warning: Extension is shadowed by a member

A().foo() // 1
```

但是我们可以重载一个拓展函数

```kotlin
class A {
    fun foo() = "member"
}

fun A.foo(i: Int) = "extension($i)"

A().foo(2) // extension(2)
```

### 标准库

Kotlin 的标准库包括 Java 标准库和一些常用的拓展函数

没有所谓的 Kotlin SDK，只有 Java 的 JDK 和一些 extensions

## Nullability

现代的编程语言应该把 Null Pointer Exception 变成编译时错误，而不是运行时错误

```kotlin
val s1: String = "always not null" // 不可以 = null
val s2: String? = null // 或者 = "a string"
```

对于一个可能为 null 的变量，我们应该始终用 if 语句检查

```java
if (s != null) {
    s.length
}
```

在 Kotlin 中。可以使用 `?` 来简化访问，如果对象为 null，则运行结果为 null，返回类型是 nullable 的基本类型

```kotlin
val length: Int? = s?.length
```

如果只想要基本类型，可以使用 elvis 运算符（ elvis 来自 Groove）

```kotlin
val length: Int = s?.length ?: 0
```

可以使用 `!!` 强制抛出 NPE

elvis 的优先级比加减法低

```kotlin
val x: Int? = 1
val y: Int = 2
val z1 = x ?: 0 + y // 1
val z2 = x ?: (0 + y) // 1
val z3 = (x ?: 0) + y // 3
```

`?` 的位置不同会决定具体什么东西不可以为 null：`List<Int?>` 和 `List<Int>?`

Kotlin 中使用 `as` 进行类型转换，同样可以对 `as` 进行 `?` 修饰

```kotlin
if (any is String) {
    any.toUpperCase()
}

(any as? String)?.toUpperCase()
```

## 函数式编程

### Lambda

与匿名类类似，在现代语言（例如 Kotlin）和 Java 8 中，都支持了 Lambda 使得语法更简单

Kotlin 中的 Lambda 用 `{}` 包围，为了与正则表达式区分，Lambda 的 `{}` 常加粗

```kotlin
list.any({i: Int -> i > 0})
```

+ 当 Lambda 是括号中最后一个参数时，我们可以把 Lambda 从括号中移出

+ 当括号为空时，可以省略空括号

+ 当类型可以被推断时，可以省略类型

+ 当只有一个参数时，可以只用 `it` 而无需声明参数

于是可以简化为

```kotlin
list.any { it > 0 }
```

多行的 Lambda 的最后一个表达式为 Lambda 结果

```kotlin
list.any {
    println("processing $it")
    it > 0
}
```

可以使用解构声明简化 Lambda 表达式

对于没有使用的参数，可以用 `_` 替代

```kotlin
map.mapValues {entry -> "${entry.key} -> ${entry.value}!" }
map.mapValues {(key, value) -> "${key} -> ${value}!" }
map.mapValues {(_, value) -> "${value}!" }
```

### 常用的集合操作

+ filter 只保留满足谓词条件的元素
+ map 将每一个元素按指定规则变换
+ any 判断列表中是否有满足谓词条件的元素
+ all 判断列表中是否所有元素都满足谓词条件
+ find 找第一个满足谓词条件的元素，如果不存在则为 null，等价于将谓词条件作为参数的 first 或者 firstOrNull
+ count 计算列表中满足谓词条件的元素个数
+ partition 按是否满足谓词条件，将列表分裂为 2 个列表
+ groupBy 按照指定字段将元素分类为若干个列表（例如按照 it.age 分类）
+ associatedBy 会将重复字段删除
+ zip 将 2 个列表合并为一个列表，其中每一个元素分别由两个列表各自对应位置元素组合，如果列表长度不同，则合并后的元素个数是较短列表的长度，其余部分将被忽略
+ flatten 将嵌套的列表展开
+ flatMap 是 map 和 flatten 的组合
+ distinct 保留列表中互不相同的元素
+ maxBy 查找列表中给定字段最大的元素，如果列表为空则返回 null

组合这些操作，我们可以很容易进行复杂的运算，例如找年龄的众数

```kotlin
val mapByAge: Map<Int, List<Hero>> = heros.groupBy {it.age }
val (age, group) = mapByAge.maxBy { (_, group) -> group.size }!!
println(age) // 找众数
```

### 函数类型

Lambda 表达式是有函数类型的

```kotlin
val isEven: (Int) -> Boolean = { i: Int -> i % 2 == 0 }
val result: Boolean = isEven(2) // true
```

对于没有参数的函数，使用 `()` 调用看起来很奇怪，所以经常使用 `run`

```kotlin
{ println("hey!") }() // possible, but looks strange
run { println("hey!") }
```

`() -> Int?` 表示返回值可以为 null，而 `(() -> Int)?` 表示表达式可以为 null

### 成员引用

可以往变量中存储 Lambda 表达式，但是不可以存储一个函数，在 Kotlin 中，函数和 Lambda 是两回事，如果一定要把函数保存到变量中，可以使用函数引用

```kotlin
val isEven: (Int) -> Boolean = { i % 2 == 0 } // OK

fun isEven(i: Int): Boolean = i % 2 == 0
val predicate = isEven // COMPILE ERROR

fun isEven(i: Int): Boolean = i % 2 == 0
val predicate = ::isEven // OK
list.any(::isEven) // OK
```

可以将函数绑定到特定的实例，也可以不绑定

```kotlin
class Person(val name: String, val age: Int) {
    fun isOlder(ageLimit: Int) = age > ageLimit
}
val alice = Person("alice", 29)

val agePredicate = alice::isOlder
agePredicate(21) // true

val agePredicate: (Person, Int) -> Boolean = { person, ageLimit -> person.isOlder(ageLimit) }
agePredicate(alice, 21) // true
```

下面这个例子是 Bound Reference，Person 被存储在了实例的内部，所以函数类型是 `(Int) -> Boolean` 而不是 `(Person, Int) -> Boolean`

```kotlin
class Person(val name: String, val age: Int) {
    fun isOlder(ageLimit: Int) = age > ageLimit
    fun getAgePredicate() = ::isOlder // this::isOlder
}
```

### 函数返回值

`return` 只会返回到函数 `fun`，而不会到返回 Lambda

```kotlin
fun containsZero(list: List<Int>): Boolean {
    list.forEach {
        if (it == 0) return true
    }
    return false
}
// 这个 forEach 接受了一个 Lambda 表达式，但是 return 是返回到 fun containsZero 的

fun duplicateNonZero(list: List<Int>): List<Int> {
    return list.flatMap {
        if (it == 0) return listOf()
        listOf(it, it)
    }
}
duplicateNonZero(listOf(3, 0, 5)) // 输出 []
// 因为 return 只会返回到 fun duplicateNonZero，而不是先返回给 flatMap 接受的 Lambda 再经由 flatMap 返回
```

为了避免这种情况，我们应该避免使用 `return` 语句，利用 Lambda 将最后一行作为返回值的特性来实现 Lambda 中的返回

```kotlin
fun duplicateNonZero(list: List<Int>): List<Int> {
    return list.flatMap {
        if (it == 0) {
            listOf()
        } else {
            listOf(it, it)
        }
    }
}
```

如果确实需要将结果返回到 Lambda，可以使用 `return@` 返回到指定的标签

```kotlin
list.flatMap {
    if (it == 0) return@flatMap listOf<Int>()
    listOf(it, it)
}

list.flatMap l@ { // 自定义标签 l
    if (it == 0) return@l listOf<Int>()
    listOf(it, it)
}
```

另外的解决方案是使用本地函数或者匿名函数

```kotlin
fun duplicateNonZeroLocalFunction(list: List<Int>): List<Int> {
    fun duplicateNonZeroElement(e: Int): List<Int> {
        if (e == 0) return listOf()
        return listOf(e, e)
    }
    return list.flatMap(::duplicateNonZeroElement)
}

fun duplicateNonZero(list: List<Int>): List<Int> {
    return list.flatMap(fun (e): List<Int> {
        if (e == 0) return listOf()
        return listOf(e, e)
    })
}
```

`forEach` 中的 `return` 不会像 `break` 一样响应

```kotlin
fun foo(list: List<Int>) {
    list.forEach {
        if (it == 0) return
        print(it)
    }
    print("###") // 如果 list 包含 0，则不会输出 ###
}

fun bar(list: List<Int>) {
    for (element in list) {
        if (element == 0) break
        print(element)
    }
    print("###") // 始终会输出 ###
}
```

