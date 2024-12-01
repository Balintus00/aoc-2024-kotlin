import kotlin.io.path.Path
import kotlin.io.path.forEachLine
import kotlin.math.absoluteValue

fun main() {
    val leftList = mutableListOf<Int>()
    val rightList = mutableListOf<Int>()
    val listDelimiter = "   "

    Path("src/Day01.txt").forEachLine { line ->
        val linePairedLocationIds = line.split(listDelimiter)
        leftList += linePairedLocationIds.first().toInt()
        rightList += linePairedLocationIds[1].toInt()
    }

    setOf(leftList, rightList).forEach { it.sort() }

    part1(sortedLeftList = leftList, sortedRightList = rightList)

    part2(leftList = leftList, rightList = rightList)
}

fun part1(sortedLeftList: List<Int>, sortedRightList: List<Int>) {
    var totalDistance = 0

    sortedLeftList.forEachIndexed { index, leftListElement ->
        totalDistance += (leftListElement - sortedRightList[index]).absoluteValue
    }

    println("Part1 total distance: $totalDistance")
}

fun part2(leftList: List<Int>, rightList: List<Int>) {
    var similarityScore = 0

    // Simple solution
//        sortedLeftList.forEach { leftListElement ->
//            similarityScore += sortedRightList.count { leftListElement == it } * leftListElement
//        }

    // A little more complex, optimized solution for the number of calculations, but not for memory
    val rightListElementWithCounts = rightList.fold(mutableMapOf<Int, Int>()) { acc, i ->
        acc.apply { acc[i] = acc[i]?.let { it + 1 } ?: 1 }
    }.toMap()
    leftList.forEach { similarityScore += it * (rightListElementWithCounts[it] ?: 0) }

    println("Part2 similarity score: $similarityScore")
}
