(* Dan Grossman, Coursera PL, HW2 Provided Code *)

(* if you use this function to compare two strings (returns true if the same
   string), then you avoid several of the functions in problem 1 having
   polymorphic types that may be confusing *)
fun same_string(s1 : string, s2 : string) =
    s1 = s2

(* put your solutions for problem 1 here *)

fun all_except_option(s: string, lst: string list) =
  case lst of
      [] => NONE (* No remaining strings, so the list does not contains s *)
    | s'::lst' => if
		     same_string(s', s)
		 then
		     SOME lst' (* s appears at most once, so return the rest list *)
		 else
		     case all_except_option(s, lst') of
			   (* Note that the return types should agree: The function returns only NONE or SOME list *)
			   NONE => NONE
			 | SOME some => SOME (s'::some); 
		      	   
fun get_substitutions1(lst: string list list, s: string) =
  case lst of
      [] => []
    | l::lst' => case all_except_option(s, l) of
		     NONE => get_substitutions1(lst', s)
		   | SOME some => some @ get_substitutions1(lst', s);

		     
fun get_substitutions2(lst: string list list, s: string) =
  let
      fun tail_recursion_helper(lst: string list list, acc: string list) =
	case lst of
	    [] => acc (* Every list has been checked. Return the accumulator*)
	  | l::lst' => case all_except_option(s, l) of
			   NONE => tail_recursion_helper(lst', acc)
			 | SOME some => tail_recursion_helper(lst', some@acc)
  in
      tail_recursion_helper(lst, [])
  end;

type NAME = {first: string, middle: string, last: string};

fun similar_names(lst: string list list, name: NAME) =
  let
      val firstName: string = case name of
				  {first=x,middle=_,last=_} => x;
      (* The pool of all strings that can substitute #first *)
      val pool: string list = get_substitutions2(lst, firstName);
      (* Go through the pool, and replace the first name with el in pool, using accumulator *)
      fun go_through(pool: string list, acc: NAME list) =
      case pool of
	  [] => acc (* Processed all strings *)
	| s::pool' => case name of
			  {first=_, middle=y, last=z} => go_through(pool', {first=s, middle=y, last=z}::acc)
  in
      go_through(pool, name::[])
  end;
  
  
(* you may assume that Num is always used with values 2, 3, ..., 10
   though it will not really come up *)
datatype suit = Clubs | Diamonds | Hearts | Spades
datatype rank = Jack | Queen | King | Ace | Num of int 
type card = suit * rank

datatype color = Red | Black
datatype move = Discard of card | Draw 

exception IllegalMove

fun card_color(c: card) =
  case c of
      (Spades, _) => Black
    | (Hearts, _) => Red
    | (Clubs, _) => Black
    | (Diamonds, _) => Red;

fun card_value(c: card) =
  case c of
      (_, Ace) => 11
    | (_, Num y) => y
    | _ => 10;

fun remove_card(cs: card list, c: card, e: exn) =
  case cs of
      [] => raise e
    | cc::cs' => if
		   cc = c
	       then
		   cs'
	       else
		   cc::remove_card(cs', c, e);

(* A elegant solution is very similar to one of the functions using nested pattern mathcing in the lecture*)
(*
fun nondecreasing intlist =
  case intlist of
      [] => true
    | _::[] => true
    | head::(neck::rest) => (head <= neck andalso nondecreasing (neck::rest))
*)
fun all_same_color(cs: card list) =
  case cs of
      [] => true
    | _::[] => true
    | head::(neck::cs') => card_color(head) = card_color(neck) andalso all_same_color(neck::cs');


fun sum_cards(cs: card list) =
  let
      fun sum(cs: card list, acc: int) =
	case cs of
	    [] => acc
	  | c::cs' => sum(cs', acc + card_value(c))
  in
      sum(cs, 0)
  end;

fun score(cs: card list, goal: int) =
  let
      val sum = sum_cards(cs);
      val preliminary_score = if sum > goal then 3 * (sum - goal) else goal - sum
  in
      if all_same_color(cs) then preliminary_score div 2 else preliminary_score
  end;

fun officiate(cs: card list, moves: move list, goal: int) =
  let
      fun action(remains: card list, moves: move list, hands: card list) =
	case moves of
	    (* The move list is empty. The player chose to stop. *)
	    [] => score(hands, goal)
	  | mv::moves' => case mv of (* Chose to move, so there are 2 choices *)
	    Discard c => action(remains, moves', remove_card(hands, c, IllegalMove)) (* Remove card from held cards, the card list stays unchanged, and continue with the rest move list *)
			   | Draw => case remains of
					 [] => score(hands, goal) (* If the player draws and the card-list is (already) empty, the game is over. *)
				       | cc::remains'  => let
					   val larger_hands = cc::hands
				       in
					   if sum_cards(larger_hands) > goal
					   then
					       score(larger_hands, goal)
					   else
					       action(remains', moves', larger_hands)
				       end
  in
      action(cs, moves, [])
  end;

fun score_challenge(cs: card list, goal: int) =
  let
      fun count_Aces(lst: card list) =
	case lst of
	    [] => 0
	  | card::lst' => case card of
			      (_, Ace) => 1 + count_Aces(lst')
			    | _ => count_Aces(lst');
      val sum = sum_cards(cs); (* If Ace = 11*)
      val min_sum = sum - count_Aces(cs) * 10; (* If all Aces take 1 *)
      (* If min_sum > goal, there is no way to be smaller, so the result is min_goal - sum; If sum < goal, there is no way to be larger, so the result is goal - sum; Otherwise, we can find a min_sum < sum_x < sum such that the sum_x equals to goal, and the final result is 0 *)
      val preliminary_score = if min_sum > goal
			      then 3 * (min_sum - goal)
			      else if sum < goal
			      then goal - sum
			      else 0
  in
      if all_same_color(cs) then preliminary_score div 2 else preliminary_score
  end;

fun officiate_challenge(cs: card list, moves: move list, goal: int) =
  officiate(cs, moves, goal);
(* No need to change this function if we modified the score function in such way. *)


fun careful_player(cs: card list, goal: int) =
  (* A lot of help functions are needed. *)
  let
      (* Look ahead to the next card. Return the value of the card. 0 if empty. *)
      fun look_ahead(lst: card list) =
	case lst of
	    [] => 0
	  | head::lst' => card_value(head);
      (* Known as tl, raise exception if is empty. *)

      fun rest_list(lst: card list) =
	case lst of
	    [] => raise IllegalMove
	  | l::lst' => lst'; 
      (* If I should draw. *)

      fun should_draw(lst: card list, hands: card list) =
	let
	    val current_sum = sum_cards(hands);
	    val next = look_ahead(lst);
	in
	    if goal - current_sum > 10
	    then true
(* A card is drawn whenever the goal is more than 10 greater than the value of the held cards. *) (* Even if no cards remain in the card-list. *)
	    else current_sum + next <= goal
	end;
      (* Get a card with value v, NONE if there is no such card. *)

      fun get_card(lst: card list, v: int) =
	case lst of
	    [] => NONE
	  | c::lst' => if v = card_value(c)
		       then SOME c
		       else get_card(lst', v);
      
      (* If I should take a Discard move. *)
      fun should_discard(lst: card list, hands: card list) =
	let
	    val next = look_ahead(lst);
	    val current_sum = sum_cards(hands);
	    val diff = current_sum + next - goal; (* Which card should I discard. *)
	in
	    get_card(lst, diff)
	end;

      fun go(lst: card list, hands: card list, acc: move list) =
	let
	    val current_sum = sum_cards(hands);
	    val possible_card = should_discard(lst, hands);
	in
	    if current_sum = goal
	    then acc (* A score of 0 is reached, there must be no more moves. *)
	    else
		if should_draw(lst, hands)
		then
		    case lst of
			[] => Draw::acc
		      | c::lst' => go(lst', c::hands, Draw::acc)
		else
		    case possible_card of
			NONE => acc
		     | SOME card => go(rest_list(lst), hands, Discard card::acc) 
	end;
      
  in
      go(cs, [], [])
  end;
  
      

(* put your solutions for problem 2 here *)
