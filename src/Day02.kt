import kotlin.io.path.Path
import kotlin.io.path.forEachLine
import kotlin.math.absoluteValue

fun main() {
    var safeCounter = 0
    var dampenedSafeCounter = 0
    Path("src/Day02.txt").forEachLine { report ->
        val levels = report.split(' ').map { it.toInt() }

        checkSafeReportTask1(levels = levels, onSafeReportAction = { safeCounter += 1 })

        checkDampenedSafeReportWithDynamicProgrammingTask2(
            levels = levels,
            onSafeReportAction = { dampenedSafeCounter += 1 },
        )
    }

    println("Task 1 safe report count: $safeCounter")
    println("Task 2 Dynamic Programming dampened safe report count: $dampenedSafeCounter")
}

private fun checkSafeReportTask1(levels: List<Int>, onSafeReportAction: () -> Unit) {
    val firstLevel = levels[0]
    val secondLevel = levels[1]
    val firstDifference = firstLevel - secondLevel

    val isIncreasing = if (!isDifferenceBetweenBounds(firstDifference)) {
        return
    } else {
        firstDifference < 0
    }

    val remainingLevels = levels.drop(1)

    remainingLevels.forEachIndexed { index, level ->
        remainingLevels.getOrNull(index + 1)?.let { nextLevel ->
            if (isDifferenceInvalid(level - nextLevel, isIncreasing)) {
                return
            }
        }
    }

    onSafeReportAction()
}

private fun isDifferenceBetweenBounds(difference: Int) = difference.absoluteValue.let {
    val minimumDistance = 1
    val maximumDistance = 3

    it in minimumDistance..maximumDistance
}

private fun isDifferenceInvalid(difference: Int, isIncreasing: Boolean): Boolean =
    !isDifferenceBetweenBounds(difference)
            || (isIncreasing && difference > 0)
            || (!isIncreasing && difference < 0)

private fun checkDampenedSafeReportWithDynamicProgrammingTask2(
    levels: List<Int>,
    onSafeReportAction: () -> Unit,
) {
    var isReportSafe = false

    checkSafeReportTask1(levels = levels, onSafeReportAction = { isReportSafe = true })

    if (!isReportSafe) {
        var isAnyDampenedReportSafe = false

        for (index in levels.indices) {
            checkSafeReportTask1(
                levels = levels.toMutableList().apply { removeAt(index) },
                onSafeReportAction = { isAnyDampenedReportSafe = true },
            )
        }

        if (!isAnyDampenedReportSafe) return
    }

    onSafeReportAction()
}
