(* Note that a date_A is older than date_B, refers that date_A comes before date_B *)
fun is_older(one_date : int * int * int, other_date: int * int * int) =
  let
      val one_year = #1 one_date;
      val other_year = #1 other_date;
      val one_month = #2 one_date;
      val other_month = #2 other_date;
      val one_day = #3 one_date;
      val other_day = #3 other_date;
  in
      if one_year <> other_year
      then
	  one_year < other_year
      else
	  if one_month <> other_month
	  then
	      one_month < other_month
	  else
	      one_day < other_day
  end
;


fun number_in_month(dates : (int * int * int) list, month : int) =
		    if null dates
		    then
			0
		    else
			let
			    val date = hd(dates)
			    val a_month = #2 date
			in
			    if a_month = month
			    then
				1 + number_in_month(tl(dates), month)
			    else
				number_in_month(tl(dates), month)
			end
;

  fun number_in_months(dates : (int * int * int) list, months : (int) list) =
    if null months
  then
      0
  else
      let
	  val month = hd(months)
      in
	  number_in_month(dates, month) + number_in_months(dates, tl(months))
      end
;

  fun dates_in_month(dates : (int * int * int) list, month : int) =
    if null dates
    then
	[]
    else
	let
	    val date = hd(dates)
	    val a_month = #2 date
	in
	    if a_month = month
	    then
		date :: dates_in_month(tl(dates), month)
	    else
		dates_in_month(tl(dates), month)
	end
;

  fun dates_in_months(dates : (int * int * int) list, months : (int) list) =
    if null months
    then
	[]
    else
	let
	    val month = hd(months)
	in
	    dates_in_month(dates, month) @ dates_in_months(dates, tl(months))
	end
;

  fun get_nth(strings : string list, n : int) =
    if n = 1
    then
	hd(strings)
    else
	get_nth(tl(strings), n - 1)
;
  fun date_to_string(date : (int * int * int)) =
    let
	val months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"]
	val year = Int.toString(#1 date)
	val month = get_nth(months, #2 date)
	val day = Int.toString(#3 date)
    in
	month ^ " " ^ day ^ ", " ^ year
    end
;

  fun number_before_reaching_sum(sum : int, numbers : (int) list) =
    let
	val num = hd(numbers)
    in
	if sum > num
	then
	    number_before_reaching_sum(sum - num, tl(numbers)) + 1
	else
	    0
    end
;
  
  fun what_month(sum : int) =
    let
	val days = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
    in
	1 + number_before_reaching_sum(sum, days)
    end
;

  fun month_range(day1: int, day2: int) =
    if day1 > day2
    then
	[]
    else
	what_month(day1) :: month_range(day1 + 1, day2)
;

  fun oldest(dates : (int * int * int) list) =
    if null dates
    then
	NONE
    else
	let
	    fun oldest_nonempty(dates : (int * int * int) list) =
	    if null (tl(dates)) (* Note that 'null tl date' or 'null tl(dates)' is wrong, it should be 'null (tl(dates))' or 'null (tl dates)' *)
	    then
		hd(dates)
	    else
		let
		    val hd_date = hd(dates);
		    val tl_date = oldest_nonempty(tl(dates))
		in
		    if is_older(hd_date, tl_date)
		    then
			hd_date
		    else
			tl_date
		end
	in
	    SOME(oldest_nonempty(dates))
	end
;
  
  fun number_in_months_challenge(dates: (int * int * int) list, months: int list) =
    let
	(* To check if `arr` contains `a`  *)
	fun exists(arr : int list, a: int) =
	  if null arr
	  then
	      false
	  else
	      if hd(arr) = a
	      then
		  true
	      else
		  exists(tl(arr), a);
	(* Remove duplicates, then use previous work. *)
	fun remove_duplicates(months: int list) =
	  if null months
	  then
	      []
	  else
	      if null (tl(months))
	      then
		  hd(months) :: []
	      else
		  let
		      val hd_months = hd(months)
		      val tl_months = tl(months)
		  in
		      if exists(tl_months, hd_months)
		      then
			  remove_duplicates(tl_months)
		      else
			  hd_months :: remove_duplicates(tl_months)
		  end
    in	
	number_in_months(dates, remove_duplicates(months))
    end
;

  fun dates_in_months_challenge(dates: (int * int * int) list, months: int list) =
    let
	(* To check if `arr` contains `a`  *)
	fun exists(arr : int list, a: int) =
	  if null arr
	  then
	      false
	  else
	      if hd(arr) = a
	      then
		  true
	      else
		  exists(tl(arr), a);
	(* Remove duplicates, then use previous work. *)
	fun remove_duplicates(months: int list) =
	  if null months
	  then
	      []
	  else
	      if null (tl(months))
	      then
		  hd(months) :: []
	      else
		  let
		      val hd_months = hd(months)
		      val tl_months = tl(months)
		  in
		      if exists(tl_months, hd_months)
		      then
			  remove_duplicates(tl_months)
		      else
			  hd_months :: remove_duplicates(tl_months)
		  end
    in	
	dates_in_months(dates, remove_duplicates(months))
    end
;

  fun reasonable_date(date: (int * int * int)) = 
    let
	val year = #1 date;
	val month = #2 date;
	val day = #3 date;
	val month_days = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
	val month_days_leap = [31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
	fun is_leap(year : int) =
	  (year mod 400 = 0) orelse (year mod 4 = 0 andalso year mod 100 <> 0)
	;
	  fun get_month_day(month_days: int list, month : int) =
	    if month = 1
	    then
		hd(month_days)
	    else
		get_month_day(tl(month_days), month - 1)	    
    in
	if year <= 0
	then
	    false
	else
	    if month < 1 orelse month > 12
	    then
		false
	    else
		let
		    val leap = is_leap(year);
		in
		    if leap
		    then
			day > 0 andalso day <= get_month_day(month_days_leap, month)
		    else
			day > 0 andalso day <= get_month_day(month_days, month)
		end
    end
	

				   
				      
 
	
