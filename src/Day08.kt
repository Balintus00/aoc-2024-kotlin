fun main() {
    val input = readInput("Day08")

    val antennasByFrequency = mutableMapOf<Char, MutableSet<Pair<Int, Int>>>()

    val clearLocationMarker = '.'

    input.forEachIndexed { rowIndex, row ->
        row.forEachIndexed { columnIndex, antennaCandidate ->
            if (antennaCandidate != clearLocationMarker) {
                antennasByFrequency.getOrPut(antennaCandidate) { mutableSetOf() }
                    .add(rowIndex to columnIndex)
            }
        }
    }

    val antiNodeLocations1: MutableSet<Pair<Int, Int>> = mutableSetOf()
    val antiNodeLocations2: MutableSet<Pair<Int, Int>> = mutableSetOf()

    antennasByFrequency.keys.forEach { frequency ->
        val currentTypedAntennaLocations = antennasByFrequency.getValue(frequency).toList()

        currentTypedAntennaLocations
            .forEachIndexed { combinationFirstMemberIndex, combinationFirstMember ->
                for (
                combinationSecondMemberIndex in
                combinationFirstMemberIndex + 1 until currentTypedAntennaLocations.size
                ) {
                    antiNodeLocations1.addAll(
                        getAntiNodeLocationsTask1(
                            combinationFirstMember,
                            currentTypedAntennaLocations[combinationSecondMemberIndex],
                        )
                            .toList()
                            .filter {
                                it.first in input.indices && it.second in input.first().indices
                            }
                    )

                    antiNodeLocations2.addAll(
                        getAntiNodeLocationsTask2(
                            firstAntennaLocation = combinationFirstMember,
                            secondAntennaLocation =
                            currentTypedAntennaLocations[combinationSecondMemberIndex],
                            firstPositionRange = input.indices,
                            secondPositionRange = input.first().indices,
                        )
                    )
                }
            }
    }

    println("Number of unique antinode locations task 1: ${antiNodeLocations1.size}")
    println("Number of unique antinode locations task 2: ${antiNodeLocations2.size}")
}

private fun getAntiNodeLocationsTask1(
    firstAntennaLocation: Pair<Int, Int>,
    secondAntennaLocation: Pair<Int, Int>,
): Pair<Pair<Int, Int>, Pair<Int, Int>> {
    val vector = firstAntennaLocation - secondAntennaLocation

    return firstAntennaLocation + vector to secondAntennaLocation - vector
}

private operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>): Pair<Int, Int> =
    first - other.first to second - other.second

private operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> =
    first + other.first to second + other.second

private fun getAntiNodeLocationsTask2(
    firstAntennaLocation: Pair<Int, Int>,
    secondAntennaLocation: Pair<Int, Int>,
    firstPositionRange: IntRange,
    secondPositionRange: IntRange,
): Set<Pair<Int, Int>> {
    val vector = firstAntennaLocation - secondAntennaLocation

    val antiNodeLocations = mutableSetOf(firstAntennaLocation, secondAntennaLocation)

    var positiveDirectionAntiNodeCandidate = firstAntennaLocation + vector
    var isPositiveDirectionOutOfBounds =
        positiveDirectionAntiNodeCandidate.first !in firstPositionRange
                || positiveDirectionAntiNodeCandidate.second !in secondPositionRange

    while (!isPositiveDirectionOutOfBounds) {
        antiNodeLocations.add(positiveDirectionAntiNodeCandidate)

        positiveDirectionAntiNodeCandidate += vector

        isPositiveDirectionOutOfBounds =
            positiveDirectionAntiNodeCandidate.first !in firstPositionRange
                    || positiveDirectionAntiNodeCandidate.second !in secondPositionRange
    }

    var negativeDirectionAntiNodeCandidate = secondAntennaLocation + vector
    var negativeVectorDirectionOutOfBounds =
        negativeDirectionAntiNodeCandidate.first !in firstPositionRange
                || negativeDirectionAntiNodeCandidate.second !in secondPositionRange

    while (!negativeVectorDirectionOutOfBounds) {
        antiNodeLocations.add(negativeDirectionAntiNodeCandidate)

        negativeDirectionAntiNodeCandidate -= vector

        negativeVectorDirectionOutOfBounds =
            negativeDirectionAntiNodeCandidate.first !in firstPositionRange
                    || negativeDirectionAntiNodeCandidate.second !in secondPositionRange
    }

    return antiNodeLocations
}
