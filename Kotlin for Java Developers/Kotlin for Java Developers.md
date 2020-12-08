Kotlin for Java Developers

> Coursera 课程 Kotlin for Java Developers（由 JetBrains 提供）的学习笔记

[TOC]

# From Java to Kotlin

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

# 基本语法

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

## 变量、常量与字符串模板

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

## 函数

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

## 分支

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

## 循环

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

## 拓展函数

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

## 标准库

Kotlin 的标准库包括 Java 标准库和一些常用的拓展函数

没有所谓的 Kotlin SDK，只有 Java 的 JDK 和一些 extensions

# Nullability

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

# 函数式编程

## Lambda

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

## 常用的集合操作

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

## 函数类型

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

## 成员引用

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

## 函数返回值

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
    print("##") // 如果 list 包含 0，则不会输出 ##
}

fun bar(list: List<Int>) {
    for (element in list) {
        if (element == 0) break
        print(element)
    }
    print("##") // 始终会输出 ##
}
```

# 属性

## 属性和域成员变量

在 Kotlin 中，依然保持了 Java 中属性的概念，但是不再需要显式地声明 getter 和 setter

+ property = field + accessor
+ val = field + getter
+ var = filed + getter + setter

例如在 Kotlin 的这段代码中，如果将它转化为 Java 代码，则隐含了 3 个 accessor

```kotlin
class Person (val name: String, var age: Int)

// String getName()
// int getAge()
// void setAge(int newAge)
```

## 对属性的访问

下面这段代码中，“Calculati……” 会输出 3 次

对于 foo1 来说：

+ 代码中使用了 run，所以运行了 Lambda 并且把最后一行的表达式作为了结果，因此 foo1 获得了值 42，并在这个过程中输出了 “Calculating……” 的信息

+ Lambda 表达式的值只在赋值时被计算一次，之后就会使用 property 的值，所以 “Calculating……” 只会输出 1 次

对于 foo2 来说：

+ 我们写了一个自定义的 getter，所以当访问 foo2 时，会访问自定义的 getter，因此输出 2 次 “Calculating……”

```kotlin
val foo1 = run {
  println("Calculating the answer...")
  42
}

val foo2: Int 
  get() {
    println("Calculating the answer...")
    return 42 
  }

fun main(args: Array<String>) { 
  println("$foo1 $foo1 $foo2 $foo2")
}
```

  ```kotlin
class StateLogger {
    var state = false
    set(value) {
        println("state has changed: $field -> $value")
        field = value
    }
}

StateLogger.state = true // state has changed: false -> true
  ```

在 accessor（getter 和 setter）中，我们可以使用 `field` 来访问域成员变量，但是也仅能在 accessor 中通过这种方式来访问

如果重新定义了 accessor 但是没有使用 field，编译器会忽略并且不会生成对应的 accessor

如果没有为属性定义 accessor，那么会有默认的 getter 和 setter

在类的内部，`className.valueNale` 的代码将由编译器决定是否对齐进行优化，如果访问非常简单，那么编译器会替换为直接访问这个变量本身，注意这样的优化对于类外部的访问来说是不安全的，所以在类的外部，`className.valueNale` 会调用对应的 getter 作为字节码，而不是直接访问这个变量本身

使用 `private set` 来将一个成员变量设置为仅允许从内部被修改，而不会被外部的访问所修改

```kotlin
interface User {
  val nickname: String
}

class FacebookUser(val accountId: Int) : User { 
  override val nickname = getFacebookName(accountId)
}

class SubscribingUser(val email: String) : User { 
  override val nickname: String
    get() = email.substringBefore('@')
}
```

FacebookUser.nickname 会把值存在 filed 中，而 SubscribingUser.nickname 用的是一个自定义的 getter，所以每一次都会访问计算

## 接口中的属性

接口中的属性不是 final 的，它们可以被子类修改

如果任意一个子类中有自定义的 getter，那么不可以使用智能类型转换（即 `if (session.user is FacebookUser)` 会被编译器报错），因为自定义的 getter 可能每一次返回的是不同的值

可以通过引入一个本地变量来使用智能类型转换

```kotlin
fun analyzeUserSession(session: Session) {
    if (session.user is FacebookUser) { // 这里判断的时候得到了一个值
        println(session.user.accountId) // 下一次 getter 得到的未必是同一个
    }
}
// Compiler error: Smart cast to 'FacebookUser' is impossible, because 'session.user' is a property that has open or custom getter

fun analyzeUserSession(session: Session) {
    val user = session.user // 只会在这里有一次 getter
    if (user is FacebookUser) {
        println(user.accountId)
    }
}
// OK
```

同样的，可变数据类型（mutable variables）也不可以使用智能类型转换

## 属性拓展

可以拓展已有的属性

```kotlin
val String.lastIndex: Int
	get() = this.length - 1
val String.indices: IntRange
	get() = 0..lastIndex
```

拓展属性和拓展函数很类似，没有任何奇妙的优化，所以下面这段代码依然会输出 2 次 “Calculating……”

```kotlin
val String.medianChar 
  get(): Char? {
    println("Calculating...")
    return getOrNull(length / 2) 
  }

fun main(args: Array<String>) { 
  val s = "abc"
  println(s.medianChar) 
  println(s.medianChar)
}
```

## 延迟初始化

Lazy Initialization 或者叫 Late Initialization，以只在第一次被用到的时候才会计算

```kotlin
val lazyValue: String by lazy {
    println("Computed")
    "Hello"
}

fun main(args: Array<String>) {
    println(lazyValue)
    println(lazyValue)
}
// 只在声明的时候计算（输出）1 次 "Computed"，main 函数中的访问直接用的 property

fun main(args: Array<String>) {
    // no lazyValue usage
}
// 但是因为初始化是 lazy 的，所以只在第一次被用到的时候才会计算，于是不会输出 "Computed"
```

如果对于一个类的成员，我们在构造函数中没有办法知道它的初始值，那么只能将它初始化成了 null，之后就需要使用 `myData?.foo` 的形式来访问

但是如果我们能确保在初始化完成后这个成员不可能再是 null，例如我们在 onCreate 函数中（或者别的手段）对其进行了初始化，处理 null 就会显得冗余

就可以使用 `lateinit` 对其修饰，这样这个类型就不再需要是 `nullable` 的了

```kotlin
lateinit myData: MyData
// ...
myData.foo
```

如果因为某些原因，这个成员没有被正确初始化，我们会得到一个运行时错误，但是这个错误不会显示 `NullPointerException`，而是 `UninitializedPropertyAccessException`

注意 `lateinit` 修饰的只能是 `var `而不可以是 `val`，其类型不能是基本类型也不能是一个 `nullable`

可以个 `.isInitialized` 来判断一个延迟初始化的变量有没有被初始化

# 面向对象编程

## 访问级别

+ Kotlin 中默认级别是 public 和 final 的，如果需要不是 final 的需要显式说明 open
+ Java 中的默认级别是 package-level，同一个包内其他类可见，这个在 Kotlin 中叫做 internal
+ override 在 Kotlin 中是强制的，避免意外 override
+ protected 在 Java 中仍然对同一个包内的其他类可见，在 Kotlin 中只对子类可见
+ private 针对类来说就是私有类，对于 top-level declaration 是对同一个文件中的其他部分可见
+ internal 在 JVM 的层面 public + name mangled
+ Java 中每一个类需要是单独的类，而 Kotlin 中可以把多个类放在一个文件里
+ Kotlin 中的包名称不必遵循 `org.company.store` 的形式，但仍做如此推荐

## 构造器

Kotlin 中不需要使用 `new`，直接像访问函数一样就可以构造一个对象

```kotlin
class A
val a = A()
```

如果构造器足够简单，不需要像 Java 一样显式地写清楚 `this.val = val` 这样的构造器，Kotlin 会自动赋值

```kotlin
// Kotlin
class Person(val name: String, val age: Int)

// Java
class Person(String name, int age) {
    this.name = name;
    this.age = age;
}
```

如果需要更复杂的构造器，可以使用 `init`

```kotlin
class Person(name: String) {
    val name: String // property declaration
    
    init {
        this.name = name
        // do something else
    }
}
```

注意，只有加上 `var` 或者 `val` 才会自动赋值作为域成员，否则就只是普通的构造器的参数

可以修改构造器的访问级别

可以声明二级构造器，例如在矩形的类中声明一个二级的构造器（正方形），当接收一个参数（边长）时，由正方形调用 `this(side, side)`

```kotlin
class Rectangle(val height: Int, val width: Int) {
    constructor(side: Int): this(side, side) {
        // ...
    }
}
```

子类的构造器会先调用父类的构造器

```kotlin
open class Parent {
  init { print("parent ") } 
}

class Child : Parent() {
  init { print("child ") } 
}

fun main(args: Array<String>) {
  Child() 
}

// parent child
```

```kotlin
open class Parent {
    open val foo = 1
    init {
        println(foo)
    }
}

class Child: Parent() {
    override val foo = 2
}

fun main() {
    Child()
}

// 0
```

这段代码会输出 0

override 一个 property 其实是 override 了它的 getter，而不是 filed

父类（应该）拥有 `foo`，初始化为 1，并且有一个平凡的 getter，叫做 `getFoo()`，这个 getter 返回了（父类的） `foo`

子类（应该）拥有 `foo`，初始化为 2，并且有一个平凡的 getter，叫做 `getFoo()`，这个 getter 返回了（子类的） `foo`，注意这个 getter 会 override 父类的 getter

当新建一个子类的时候，首先调用了父类的构造器，父类的 `foo` 为 1，并且拥有一个返回了（父类的）`foo` 的 getter，然后调用 `init`，在 `init` 中，会调用 `getFoo`，由于这是一个子类，那么根据多态，应该调用子类的 `getFoo`，子类的 `getFoo` 会返回（子类的）`foo` 值，而此时子类还没有完成初始化，所以 `foo` 值为 0

因此，上面这段代码在 Java 中相当于

```java
public class A {
    private final String value;
    
    public A(String value) {
        this.value = value;
        getValue().length(); // call value_B.length() -> call null.length()
    }
    
    public String getValue() {
        retrun value;
    }
}

public class B extends A {
    private final String value; // mark it as value_B
    
    public B(String value) {
        super(value);
        this.value = value; // mark it as value_B
    }
    
    @Override
    public String getValue() {
        retrun value; // mark it as value_B
    }
}
```

## 类修饰符

`enum` 是一个类修饰符，而不是一个特殊的关键字

```kotlin
enum class Color {
    BLUE, ORANGE, RED
}

Color.BLUE

import mypackage.Color.*
BLUE
```

```kotlin
enum class Color(val r: Int, val g: Int, val b: Int) {
    BLUE(0, 0, 255), ORANGE(255, 165, 0), RED(255, 0, 0);
    fun rgb() = (r * 256 + g) * 256 + b
}

BLUE.r
BLUE.rgb()
```

`data` 有 `equals`、`copy`、`hashCode` 和 `toString` 等方法

```kotlin
data class Contact(val name: String, val address: String)
contact.copy(address = "new address")
```

在 Kotlin 中，`==` 默认比较它们的 `equals`，而 `===` 比较它们是不是同一个引用

在 Java 中，`==` 比较是否是同一个引用，需要使用 `equals` 来比较它们

```kotlin
class Foo(val first: Int, val second: Int)
data class Bar(val first: Int, val second: Int)

val f1 = Foo(1, 2) 
val f2 = Foo(1, 2) 
println(f1 == f2) // false

val b1 = Bar(1, 2) 
val b2 = Bar(1, 2) 
println(b1 == b2) // true
```

默认的实现都是比较引用的 `equals`，但是当类使用 `data` 修饰时，会自动实现一个比较域成员的 `equals`，于是就会得到 `true`

Kotlin 只会使用主构造器中的属性来实现 `equals`，不会使用类在其他部分定义的变量

当明确知道自己的类考虑了所有考虑的情况时，可以用 `sealed` 来避免冗余的代码，注意这个是类修饰符，不能用于接口

```kotlin
interface Expr
class Num(val value: Int): Expr
class Sum(val left: Expr, val: Right: Expr): Expr
fun eval(e: Expr): Int = when (e) {
    is Num -> e.value
    is Sum -> eval(e.left) + eval(e.right)
    else -> throw IllegalArgumentException("Unknown expression") // 要加上这句话，否则无法通过编译：when 必须完备
}

sealed class Expr
class Num(val value: Int): Expr
class Sum(val left: Expr, val: Right: Expr): Expr
fun eval(e: Expr): Int = when (e) {
    is Num -> e.value
    is Sum -> eval(e.left) + eval(e.right)
    // OK
}
```

在 Java 中，如果只写 `class A`，则作为一个内部类，会默认保存外部类的一个引用，而在 Kotlin 中， `class A`这种写法默认不会产生这样的引用，即相当于 Java 中的 `static class A`

如果需要这样一个对外部类的引用，可以使用 `inner class A`，并通过 `@` 标签访问

```kotlin
class A {
    class B
    inner class C {
        this@A
    }
}
```

类委托可以委托一个类来实现一个接口

```kotlin
interface Repository {
    fun getById(id: Int): Customer
    fun getAll(): List<Customer>
}
interface Logger {
    fun logAll()
}
```

```kotlin
// 原本的写法
class Controller(
	repository: Repository,
    logger: Logger
): Repository, Logger {
    override fun getById(id: Int) = repository.getById(id)
    override fun getAll(): List<Customer> = repository.getAll()
    override fun logAll() = logger.logAll()
}
```

```kotlin
// Class Delegation
class Controller(
	repository: Repository,
    logger: Logger
): Repository by repository, Logger by logger

fun use(controller: Controller) {
    controller.logAll()
}
```

## 对象

对象在 Kotlin 中，对象是单例的

```kotlin
object KSingleton {
    fun foo() {}
}

KSingleton.foo()
```

对象表达式代替了 Java 中的匿名类（如果只有简单的方法，可以直接使用 Lambda 表达式，如果需要多个方法，那可以使用对象表达式）

对象表达式不是单例的，每一次调用都会新建新的实例，因为有可能会需要使用外部的类传递进来的参数，使用每一次都要实例化

Kotlin 中没有 static 的方法，companion object 可以作为它的替代

Java 中的 static 方法不能重写接口的方法，在 Kotlin 中，companion object 可以重写接口的方法

```kotlin
class C {
  companion object {
    @JvmStatic fun foo() {}
    fun bar() {} 
  }
}

// Java
C.foo();           // OK
C.bar();           // NO，因为试图将其作为 static 方法来调用
C.Companion.foo(); // OK
C.Companion.bar(); // OK
```

`inner` 只能修饰类，不能修饰对象，因为 `object` 是单例

可以把 `object` 放在 class 内部作为嵌套

## 常量

`const` 用来定义基本类型或者 string，这个常量会在编译时被替换掉

```kotlin
const cal answer = 42
```

## 泛型

```kotlin
interface List<E> {
    fun get(index: Int): E
}

fun foo(ints: List<Int>) { ... }

fun <T> List<T>.filter(predicate: (T) -> Boolean): List<T>

fun <T> List<T>.firstOrNull(): T?
```

可以使用 `Any` 来确保元素不可以为 null

```kotlin
fun <T> foo (list: List<T>) {
    for (element in List) {
        
    }
}

foo(listOf(1, null)) // OK

fun <T: Any> foo (list: List<T>) {
    for (element in List) {
        
    }
}

foo(listOf(1, null)) // NO
```

可以使用 `where` 来进行多个 upper bounds

```kotlin
fun <T: Comparable<T>> max(first: T, second: T): T {
    return if (first > second) first else second
}

fun <T> ensureTrailingPeriod(seq: T)
	where T: CharSequence, T: Appendable {
        if (!seq.endsWith('.') {
            seq.append('.')
        })
    }
```

使用了泛型的函数，可以用 `JvmName` 来指定不同的泛型函数名称，这样就可以在 Java 中使用 averageOfDouble，因为字节码有这个函数了

```kotlin
fun List<Int>.average: Double { ... }

@JvmName("averageOfDouble")
fun List<Double>.average(): Double { ... }
```

# 编码约定

## 符号重载

使用 `a + b` 会自动调用 `a.plus(b)`

```kotlin
operator fun Point.plus(other: Point): Point {
    return Point(x + other.x, y + other.y)
}

Point(1, 2) + Point(2, 3)
```

重载的运算符左右两边的数据类型可以不一样

```kotlin
operator fun Point.times(scale: Int): Point {
    return Point(x * scale, y * scale)
}

Point(1, 2) * 3
```

单目运算符也可以重载，例如 `unaryMinus`、`not`、`inc`

注意对于 list 这样的类型，+= 的操作会新建一个新的 list，例如下面这段代码会输出 `[1, 2, 3, 4]` 和 `[1, 2, 3]`

```kotlin
val list1 = listOf(1, 2, 3) 
var list2 = list1
list2 += 4
println(list1) 
println(list2)
```

如果需要，可以把 `var` 和 `listOf` 换成 `val` 和 `mutableListOf`

在 Kotlin 中，可以使用 `<` 这些符号比较字符串之间的大小，会自动调用 `compareTo()` 并和 0 比较，也可以使用 `==` 比较相等，会调用 `equals()`

访问键值对也可以使用 `map[index]` 操作，会调用 `map.get(index)`

Java 的 String 没有实现 Iterable 接口，但是 Kotlin 中可以通过定义拓展函数的方法重载迭代运算符

```
operator fun CharSequence.iterator(): CharIterator
for (c in "abc") { ... }
```

解构式的定义，在本质上也是运算符的重载 `argument.component1()`

```kotlin
map.forEach { (key, value) -> { ... } }
```

list 也可以同时遍历下标和元素

```kotlin
for ((inex, element) in list.withIndex()) {
	println("$index $element")
}
```

不需要的参数可以用 `_` 跳过

如果一个类（例如 Point）实现了 Comparable 接口，那么在任何其他地方都可以使用 `<` `>` 来比较大小，也可以再定义一个 `private operator fun Point.compareTo`，这样就可以在自己的算法中用新的比较规则，这个规则在代码的其他部分是不可见的

# 内联函数

`run` 会运行一个 Lambda 代码段，并把最后一个表达式作为结果

`let` 可以检测一个参数是不是 `nulll`

```kotlin
fun getEmail(): Email?
val email = getEmail()

if (meial != null) sendEmailTo(email)

email?.let { e -> sendEmailTo(e) }
getEmail()?.let { sendEmailTo(it) }
```

如果任意一个子类中有自定义的 getter，那么不可以使用智能类型转换（即 `if (session.user is FacebookUser)` 会被编译器报错），因为自定义的 getter 可能每一次返回的是不同的值，可以通过引入一个本地变量来使用智能类型转换，而 `let` 可以简化这个写法

```kotlin
interface Session {
    val user: User
}
fun analyzeUserSession(session: Session) {
    val user = session.user
    if (user is FacebookUser) {
        println(user.accountId)
    }
}

(session.user as? FacebookUser)?.let {
    println(it.accountId)
}
```

`takeIf` 返回条件满足时的对象，否则 null

常与 `?.let` 连用

```kotlin
issue.takeIf { it.status == FIXED }
person.patronymicName.takeIf(String::isNotEmpty)
```

`takeUnless` 与 `takeIf` 相反

`repeat` 可以重复一个操作多次，注意这不是一个 built-in 的关键字，而是一个 inline function

```kotlin
repeat(10) {
    println("Hello")
}
```

```kotlin
inline fun repeat(times: Int, action (Int) -> Unit) {
    for (index in 0 until times) {
        action(index)
    }
}
```

没有内联的 Lambda 表达式会被当做一个类，会带来额外的性能开销，因为内联会把函数题替换掉，而不是调用函数

```kotlin
fun myRun(f: () -> Unit) = f()
fun main(args: Array<String>) {
    val name = "Kotlin"
    myRun { println("Hi, $name!") }
}
// class Examplekt$main$1
```

像 filter 这样的函数，都是内联的

但 inline 是 Kotlin 的特性，如果从 Java 调用，那不会有内联

# 序列

Lambda 是内联的，但是链式调用的中间过程的数据集合都会被产生

```kotlin
val list = listOf(1, 2, -3) 
val maxOddSquare = list // [1, 2, -3]
    .map { it * it }  // [1, 4, 9]
    .filter { it % 2 == 1 }  // [1, 9]
    .max() // 1
```

序列 `.asSequence()` 推迟了计算发生的时间，从而避免了中间过程中不断产生集合

```kotlin
val list = listOf(1, 2, -3) 
val maxOddSquare = list
	.asSequence()
    .map { it * it } 
    .filter { it % 2 == 1 } 
    .max()
```

![](https://img.jxtxzzw.com/2020/12/02/zh6dff.png)

Collections 中，每一次链式调用都会完成计算，因此得到 [m1, m2, m3, m4, f1, f2, f3, f4]

Sequences 中，每次对一个值完成全部的计算，因此得到 [m1, f1, m2, f2, m3, f3, m4, f4]

注意在 Sequences 中，除非需要这个值，否则不会计算

![](https://img.jxtxzzw.com/2020/12/02/zig69f.png)

另外，但 Sequences 发现前面的步骤已经不满足时，不会进行后面的步骤

![](https://img.jxtxzzw.com/2020/12/02/10tjj6g.png)

Collections 和 Sequences 的类不是父类子类关系

```kotlin
val seq = generateSequence {
    Random.nextInt(5).takeIf { it > 0 }
}
println(seq.toList())
```

Sequences 都是懒惰计算的，除非到了需要的时候，否则不会完成计算

例如下面这个例子，问的只是 `.first()`，而第一个元素已知，所以不会去计算后面的元素，因此输出 “Generating” 0 次

```kotlin
val numbers = generateSequence(3) { n ->
  println("Generating element...")
  (n + 1).takeIf { it < 7 } 
}
println(numbers.first()) // 3
```

`yield` 在 Kotlin 中不是语言特性、不是关键字，只是一个函数

但它是懒惰的，只在需要时被调用

```kotlin
val numbers = sequence {
    var x = 0
    while (true) {
        yield(x++)
    }
}
numbers.take(5).toList() // [0, 1, 2, 3, 4]
```

```kotlin
fun mySequence() = buildSequence { 
  println("yield one element") 
  yield(1)
  println("yield a range") 
  yieldAll(3..5)
  println("yield a list")
  yieldAll(listOf(7, 9)) 
}

println(mySequence() 
  .map { it * it }
  .filter { it > 10 } 
  .take(1))
// 不会输出任何一条 yield ...
// 因为 take() 不是最终操作

println(mySequence() 
  .map { it * it }
  .filter { it > 10 } 
  .first())
// 只会输出 "yield one element" 和 "yield a range"
// first() 是终端操作
// 首先计算 1，经过 map 得到 1，被过滤
// 然后计算 3，经过 map 得到 9，被过滤
// 再计算 4，经过 map 得到 16，找到答案，程序结束，不会继续后面的计算
```

# 带接收器的 Lambda

拓展函数和 Lambda 结合，可以看作带接收器的 Lambda，又叫拓展的 Lambda

```kotlin
val sb = StringBuilder()
sb.appendln("Alphabet: ")
for (c in 'a'..'z') {
    sb.append(c)
}
sb.toString()
```

这样的代码需要重复多次变量名，可以使用 `with` 简化

```kotlin
val sb = StringBuilder()
with (sb) {
    appendln("Alphabet: ")
    for (c in 'a'..'z') {
        append(c)
    }
    toString()
}
```

事实上，`with` 是一个函数，`sb` 作为第一个参数，而这个 Lambda 表达式是第二个参数，即

```kotlin
with (sb, { this.toString() } )
```

```kotlin
val isEven: (Int) -> Boolean = { it % 2 == 0 }
val isOdd: Int.() -> Boolean = { this % 2 == 1 }

isEven(0)
1.isOdd()
```

# 使用库函数简化一些计算

```kotlin
people.filter { it.age < 21 }.size


people,count { it.age < 21 }
```

```kotlin
people.sortedBy { it.age }.reversed()


people.sortedByDesending { it.age }
```

```kotlin
people
  .map { person ->
    person.takeIf { it.isPublicProfile }?.name 
  }
  .filterNotNull()


people.mapNotNull { person ->
    person.takeIf { it.isPublicProfile }?.name
}
```

```kotlin
if (person.age !in map) {
  map[person.age] = mutableListOf() 
}
map.getValue(person.age) += person


val group = map.getOrPut(person.age) { mutableListOf() }
group += person
```

```kotlin
val map = mutableMapOf<Int, MutableList<Person>>() 
for (person in people) {
  if (person.age !in map) {
    map[person.age] = mutableListOf() 
  }
  map.getValue(person.age) += person 
}


people.groupBy { it.age }
```

```kotlin
groupBy()
// Write the name of the function that performs groupBy for Sequences in a lazy way.


groupingBy()
```

```kotlin
eachCount() // counts elements in each group
```

# Kotlin 和 Java 中的数据类型

使用 `Int` 时，Kotlin 将其转换为 `int` 字节码，当使用 `Int?` 时，Kotlin将其转换为 `Integer` 字节码

`List<Int>` 仍然会被当成 `List<Integer>`

`Array<Int>` 是 `Integer[]`，`IntArray` 是 `int[]`

Kotlin 中的 `String` 就是 Java 中的 `String`，但隐藏了一些容易混淆的方法，例如 `replaceAll` 接收正则表达式

`Any` 是 `Object`，也是 Int 这些基本类型（在 Kotlin 中）的基类

 除非是内联的 Lambda 表达式，否则会被变成 `Function0`、`Function1` 这样，内联的表达式会直接替换

可以显式地在 Kotlin 中调用 invoke()

```kotlin
println(arrayOf(1, 2) == arrayOf(1, 2))
```

Kotin 中的数组和 Java 中的数组是一样的，没有魔法，所以上面的比较结果是 `false`，可以使用 `contentEquals` 来比较它们的内容

当只使用 Kotlin（而不需要从字节码层面被 Java 使用）时，那么没有理由使用 `Array`，应该始终使用 `List`

`Nothing` 是 Kotlin 中的底层类型，`Nothing` 可以看做是任何类型的子类，但在字节码层面，仍然会被转化为 `void`，因为 Java 中没有可以表示 `Nothing` 的类型

`Unit` 表示函数返回时没有有意义的返回值，用来替代 Java 的 `void`，其在字节码层面就是 `void`，完全等价

`Nothing` 表示函数永远不会返回，例如在 `fail()` 函数中抛出异常，这是一个永远不会执行完成的函数

Kotlin 中，`TODO()` 是一个内联的函数，可以接受一个参数 String 表示一些备注信息，它的类型也是 `Nothing`

直接使用 `return` 也可以获得 `Nothing` 类型

```kotlin
fun greetPerson(person: Person) {
    val name = person.name ?: return
    println("Hello, $name!")
}
```

```kotlin
val answer = if (timeHasPassed()) {
    42
} else {
    fail("Not ready")
}
fun fail(message: String) {
    throw IllegalStateException(message)
}
```

这里 `answer` 会被认为是 `Any`，因为当条件成立时，42 是一个 `Int`，而 `fail()` 是 `Unit`，这两个类型的公共父类是 `Any`，这与期望不合

```kotlin
val answer = if (timeHasPassed()) {
    42
} else {
    fail("Not ready")
}
fun fail(message: String): Nothing {
    throw IllegalStateException(message)
}
```

这里 `answer` 会被认为是 `Int`，因为当条件成立时，42 是一个 `Int`，而 `fail()` 是 `Nothing`，`Nothing` 可以看做是任何类型的子类

![](https://img.jxtxzzw.com/2020/12/04/vtolkc.png)

最简单的、也是唯一的一个 `Nothing?` 类型是 `null` 常量

类型后面加 `!` 例如 `String!` 往往只会出现在错误信息中，例如数据类型不匹配的错误，来表示这个类型是来自 Java 的

```kotlin
// Java
public class Session {
  public String getDescription() {
    return null; 
  }
}
// Kotlin
val session = Session()
val description = session.description // description 的类型是 "String!"
println(description.length) // NullPointerException
```

这样会使得 Kotlin 中的 Nullable 检查毫无用处，因为依然可能出现 Null Pointer Exception，而不需要明确地检查是不是为 null

这种情况可以在 Java 代码中增加注解 `@Nullable`、`@NonNull` 等，这样 Kotlin 就可以强制检查 Nullable 的数据

```kotlin
// Java
public class Session {
  @Nullable
  String getDescription() {
    return null;
  }
}
// Kotlin
val session = Session()
val description = session.description
println(description.length) // 无法通过编译
```

可以将 `@NotNull` 设置为默认（由 JSR-305 支持的 `@ParametersAreNonnullByDefault`、`@MyNonnullByDefault`），这样只需要注释 `@Nullable` 的类型即可

也可以根据自己的需要指定另一个默认值

但注意 Kotlin 将默认 `NotNull` 的数据类型、却接收了 `null` 这样的问题，只是看作警告，需要添加 `-Xjsr305=strict` 编译选项，Kotlin 才会把它们看作错误

预防 Null Pointer Exception，除了使用 Java 注解，还可以在 Kotlin 代码中明确数据类型，例如 `String?` 或者 `String`，而不要让编译器自己猜测

明确数据类型可以得到以下不同的结果：

```kotlin
// Java
public class Session {
  String getDescription() {
    return null;
  }
}
// Kotlin
val session = Session()
val description: String? = session.description // 这是 String? 类型
println(description?.length) // 输出 null
```

```kotlin
// Java
public class Session {
  String getDescription() {
    return null;
  }
}
// Kotlin
val session = Session()
val description: String = session.description // 这是 String 类型，不能为空
println(description.length) // 抛出 IllegalStateException，不是 NUllPointerException
```

`kotlin.List` 和 `java.util.List` 是一样的，`MutableList` 继承自 `List`

注意只读和不可变是不一样的，不能对 `List` 使用 `add`，因为它没有 mutating 方法，但可以通过 `MutableList` 来修改

```kotlin
val mutableList = mutableListOf(1, 2, 3)  //#1 
val list: List<Int> = mutableList         //#2 
mutableList.add(4)                        //#3 
println(list)                             //#4 
```

这依然会输出 `[1, 2, 3, 4]`

在底层，`kotlin.List` 有一个子类 `kotlin.MutableList`，而 `kotlin.MutableList` 会用 `java.util.ArrayList` 来实现

使用只读类型，例如 List，可以防止自己意外地调用 `.add()` 这样的方法，除非把它明确地交给 Mutable，那就可以修改

