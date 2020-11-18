(* Coursera Programming Languages, Homework 3, Provided Code *)

exception NoAnswer

datatype pattern = Wildcard
		 | Variable of string
		 | UnitP
		 | ConstP of int
		 | TupleP of pattern list
		 | ConstructorP of string * pattern

datatype valu = Const of int
	      | Unit
	      | Tuple of valu list
	      | Constructor of string * valu

fun g f1 f2 p =
    let 
	val r = g f1 f2 
    in
	case p of
	    Wildcard          => f1 ()
	  | Variable x        => f2 x
	  | TupleP ps         => List.foldl (fn (p,i) => (r p) + i) 0 ps
	  | ConstructorP(_,p) => r p
	  | _                 => 0
    end

(**** for the challenge problem only ****)

datatype typ = Anything
	     | UnitT
	     | IntT
	     | TupleT of typ list
	     | Datatype of string

(**** you can put all your code here ****)

(* 1 *)
fun is_capital s =
  Char.isUpper(String.sub(s, 0));
(* List.filter takes 2 parameters, not a tuple *)
fun only_capitals slist =
  List.filter is_capital slist;

(* 2 *)
(* helper function takes a tuple with 2 parameters *)
fun keep_longer1 (x, y) =
  if String.size(x) > String.size(y)
  then
      x
  else
      y;
(* List.foldl expects a 'a * 'b -> 'b function, not a triple *)
fun longest_string1 slist =
  List.foldl keep_longer1 "" slist;

(* 3 *)
fun keep_longer2 (x, y) =
  if String.size(x) >= String.size(y)
  then
      x
  else
      y;
fun longest_string2 slist =
  List.foldl keep_longer2 "" slist;

(* 4 *)
fun longest_string_helper f slist =
  let
      fun keep_longer (x, y) =
	if f(String.size(x), String.size(y))
	then
	    x
	else
	    y;
  in
      List.foldl keep_longer "" slist
  end;
(* defined with val-bindings and partial applications *)
val longest_string3 =
    longest_string_helper (fn(x, y) => x > y);
val longest_string4 =
    longest_string_helper (fn(x, y) => x >= y);

(* 5 *)
fun longest_capitalized slist =
  longest_string1(only_capitals(slist));

(* 6 *)
(* use () to determine the priority, otherwise explode(s) will be done first *)
fun rev_string s =
  (implode o rev o explode) s;
(* or, define with val-binding: val rev_string = (implode o rev o explode) *)

(* 7 *)
fun first_answer f lst =
  case lst of
      [] => raise NoAnswer
    | x::xs => case f(x) of
		   NONE => first_answer f xs
		 | SOME v => v;

(* 8 *)
fun all_answers f lst =
  let
      fun helper(l, f, acc) =
	case l of
	    [] => SOME acc
	  | x::xs => case f(x) of
			 NONE => NONE
		       | SOME v => helper(xs, f, v @ acc)
  in
      helper(lst, f, [])
end;

(* 9 *)
fun void _ = 0

val count_wildcards =
    g (fn _ => 1) void;

val count_wild_and_variable_lengths =
    g (fn _ => 1) (fn x => String.size x);

fun count_some_var (s, p) =
    g void (fn x => if x = s then 1 else 0) p;

(* 10 *)

fun check_pat p =
  let
      fun get_type pat =
	case pat of
	    Variable x => [x]
	  | TupleP ps => List.concat (List.map get_type ps)
	  | ConstructorP(_,p) =>  get_type p
	  | _ => [];
      fun find_duplicate lst =
	case lst of
	    [] => false
	  | x::xs => case List.exists (fn y => x = y) xs of
			 true => true
		      | false => find_duplicate xs 
  in
      not (find_duplicate (get_type p))
  end;

(* 11 *)
fun match vp =
  case vp of
      (_, Wildcard) => SOME []
    | (v, Variable s) => SOME [(s, v)]
    | (Unit, UnitP) => SOME []
    | (Const x, ConstP y) =>
      if x = y then
	  SOME []
      else
	  NONE
    | (Tuple vs, TupleP ps) =>
      if List.length(vs) = List.length(ps) then
	  all_answers match (ListPair.zip (vs, ps))
      else
	  NONE
    | (Constructor(s2, v), ConstructorP(s1, p)) =>
      if s1 = s2 then
	  match (v, p)
      else
	  NONE
    | _ => NONE;

(* 12 *)
fun first_match v lst =
  SOME (first_answer (fn p => match(v, p)) lst)
  handle NoAnswer => NONE;
