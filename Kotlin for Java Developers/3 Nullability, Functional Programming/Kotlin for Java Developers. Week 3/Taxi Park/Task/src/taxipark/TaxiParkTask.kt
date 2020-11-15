package taxipark

/*
 * Task #1. Find all the drivers who performed no trips.
 */
fun TaxiPark.findFakeDrivers(): Set<Driver> =
        allDrivers.filter { d ->
            trips.none { it.driver == d }
        }.toSet()

/*
 * Task #2. Find all the clients who completed at least the given number of trips.
 */
fun TaxiPark.findFaithfulPassengers(minTrips: Int): Set<Passenger> =
        allPassengers.filter { p ->
            trips.count { p in it.passengers } >= minTrips
        }.toSet()

/*
 * Task #3. Find all the passengers, who were taken by a given driver more than once.
 */
fun TaxiPark.findFrequentPassengers(driver: Driver): Set<Passenger> =
        allPassengers.filter { p ->
            trips.count { it.driver == driver && p in it.passengers } > 1
        }.toSet()

/*
 * Task #4. Find the passengers who had a discount for majority of their trips.
 */
fun TaxiPark.findSmartPassengers(): Set<Passenger> {
    val (withDiscount, withoutDiscount) = trips.partition {
        it.discount is Double
    }
    return allPassengers.filter { p ->
        withDiscount.count { p in it.passengers } > withoutDiscount.count { p in it.passengers }
    }.toSet()
}

/*
 * Task #5. Find the most frequent trip duration among minute periods 0..9, 10..19, 20..29, and so on.
 * Return any period if many are the most frequent, return `null` if there're no trips.
 */
fun TaxiPark.findTheMostFrequentTripDurationPeriod(): IntRange? {
    val timeGroup =
            trips
                    .groupBy {
                        val start = it.duration / 10 * 10
                        val end = start + 9
                        start..end
                    }
    return timeGroup.toList().maxBy { (_, list) -> list.size }?.first
    // maxBy() is deprecated, so we should use maxByOrNull()
    // but it will cause unresolved reference: maxByOrNull, on Coursera because of the Kotlin version
}

/*
 * Task #6.
 * Check whether 20% of the drivers contribute 80% of the income.
 */
fun TaxiPark.checkParetoPrinciple(): Boolean {
    val totalIncome = trips.sumByDouble { it.cost }

    if (totalIncome.equals(0.0)) {
        return false
    }

    val topDriversIncome =
            trips.groupBy { it.driver }
                    .map { (_, list) ->
                        list.sumByDouble { it.cost }
                    }
                    .sortedDescending()

    val top20 = (allDrivers.size * 0.2).toInt()

    return topDriversIncome.take(top20).sum() >= totalIncome * 0.8
}