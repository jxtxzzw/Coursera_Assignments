(* Homework3 Simple Test*)
(* These are basic test cases. Passing these tests does not guarantee that your code will pass the actual homework grader *)
(* To run the test, add a new line to the top of this file: use "homeworkname.sml"; *)
(* All the tests should evaluate to true. For example, the REPL should say: val test1 = true : bool *)

val test1 = only_capitals ["A","B","C"] = ["A","B","C"]

val test2 = longest_string1 ["A","bc","C"] = "bc"

val test3 = longest_string2 ["A","bc","C"] = "bc"

val test4a = longest_string3 ["A","bc","C"] = "bc"

val test4b = longest_string4 ["A","B","C"] = "C"

val test5 = longest_capitalized ["A","bc","C"] = "A"

val test5_1 = longest_capitalized ["A","bc","CA"] = "CA"

val test6 = rev_string "abc" = "cba"

val test7 = first_answer (fn x => if x > 3 then SOME x else NONE) [1,2,3,4,5] = 4

val test8 = all_answers (fn x => if x = 1 then SOME [x] else NONE) [2,3,4,5,6,7] = NONE

val test8_1 = all_answers (fn x => if x > 1 then SOME [x] else NONE) [3] = SOME [3]

val test8_2 = all_answers (fn x => if x > 1 then SOME [x] else NONE) [3,1] = NONE

val test8_3 = all_answers (fn x => if x > 1 then SOME [x] else NONE) [2,3,4] = SOME [4,3,2]

val test9a = count_wildcards Wildcard = 1

val test9a_1 = count_wildcards (Variable("a")) = 0

val test9b = count_wild_and_variable_lengths (Variable("a")) = 1

val test9b_1 = count_wild_and_variable_lengths (TupleP([Wildcard, Variable("abc")])) = 4

val test9c = count_some_var ("x", Variable("x")) = 1

val test9c_1 = count_some_var ("x", Variable("y")) = 0

val test9c_2 = count_some_var ("hello", TupleP([Variable("x"), Wildcard, Variable("hello"), Variable("hello")])) = 2


val test10 = check_pat (Variable("x")) = true

val test10_1 = check_pat (TupleP([Variable("x"), Wildcard])) = true

val test10_2 = check_pat (TupleP([Variable("x"), Wildcard, Variable("hello"), Variable("hello")])) = false

val test11 = match (Const(1), UnitP) = NONE

val test11_1 = match (Const(1), Wildcard) = SOME []

val test11_2 = match (Const(1), Variable("a")) = SOME [("a", Const(1))]

val test11_3 = match (Unit, UnitP) = SOME []

val test11_4 = match (Tuple([Const(1)]), UnitP) = NONE

val test11_5 = match (Const(1), ConstP(1)) = SOME []

val test11_6 = match (Const(1), ConstP(2)) = NONE

val test11_7 = match (Tuple([Const(1), Const(2)]), TupleP([Wildcard, ConstP(2)])) = SOME []

val test11_8 = match (Tuple([Const(1)]), TupleP([Wildcard, ConstP(2)])) = NONE

val test11_9 = match (Tuple([Const(1), Const(2)]), TupleP([ConstP(3), ConstP(2)])) = NONE

val test11_10 = match (Tuple([Const(1)]), TupleP([Variable("a")])) = SOME [("a", Const(1))]

val test11_11 = match (Tuple([Const(1), Const(2)]), TupleP([Variable("a"), ConstP(2)])) = SOME [("a", Const(1))]

val test11_12 = match (Tuple([Const(1), Const(2)]), TupleP([Variable("a"), Variable("b")])) = SOME [("b", Const(2)), ("a", Const(1))]

val test11_13 = match (Constructor("a", Const(1)), ConstructorP("b", ConstP(1))) = NONE

val test11_14 = match (Constructor("a", Const(1)), ConstructorP("a", ConstP(1))) = SOME []

val test11_15 = match (Constructor("a", Const(1)), ConstructorP("a", ConstP(2))) = NONE
val test12 = first_match Unit [UnitP] = SOME []

val test12_1 = first_match Unit [Variable("a"), UnitP] = SOME [("a", Unit)]

val test12_2 = first_match (Const(1)) [(ConstP(2)), UnitP] = NONE
