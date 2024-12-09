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

    val antiNodeLocations: MutableSet<Pair<Int, Int>> = mutableSetOf()

    antennasByFrequency.keys.forEach { frequency ->
        val currentTypedAntennaLocations = antennasByFrequency.getValue(frequency).toList()

        currentTypedAntennaLocations
            .forEachIndexed { combinationFirstMemberIndex, combinationFirstMember ->
                for (
                combinationSecondMemberIndex in
                combinationFirstMemberIndex + 1 until currentTypedAntennaLocations.size
                ) {
                    antiNodeLocations.addAll(
                        getAntiNodeLocations(
                            combinationFirstMember,
                            currentTypedAntennaLocations[combinationSecondMemberIndex],
                        )
                            .toList()
                            .filter {
                                it.first in input.indices && it.second in input.first().indices
                            }
                    )
                }
            }
    }

    println("Number of unique antinode locations task 1: ${antiNodeLocations.size}")
}

private fun getAntiNodeLocations(
    firstAntennaLocation: Pair<Int, Int>,
    secondAntennaLocation: Pair<Int, Int>,
): Pair<Pair<Int, Int>, Pair<Int, Int>> {
    val vector = firstAntennaLocation - secondAntennaLocation

    return firstAntennaLocation + vector to secondAntennaLocation - vector
}

private operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> =
    first + other.first to second + other.second

private operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>): Pair<Int, Int> =
    first - other.first to second - other.second
