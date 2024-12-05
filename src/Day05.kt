import kotlin.io.path.Path
import kotlin.io.path.forEachLine

fun main() {
    val mutablePrecedenceRules: MutableMap<Int, List<Int>> = mutableMapOf()
    val mutableUpdates: MutableList<List<Int>> = mutableListOf()

    readInput(mutablePrecedenceRules, mutableUpdates)

    val updates: List<List<Int>> = mutableUpdates

    var sumOfMiddleElementsInValidUpdatesTask1 = 0
    var sumOfMiddleElementsInCorrectedInvalidUpdatesTask2 = 0

    updates.forEach { currentUpdate ->
        if (currentUpdate.getFirstInvalidElementIndex(mutablePrecedenceRules) == null) {
            sumOfMiddleElementsInValidUpdatesTask1 += currentUpdate.getMiddleUpdateElement()
        } else {
            sumOfMiddleElementsInCorrectedInvalidUpdatesTask2 +=
                currentUpdate.getFixedUpdate(mutablePrecedenceRules).getMiddleUpdateElement()
        }
    }

    println("Task 1 result = $sumOfMiddleElementsInValidUpdatesTask1")
    println("Task 2 result = $sumOfMiddleElementsInCorrectedInvalidUpdatesTask2")
}

private fun readInput(
    mutablePrecedenceRules: MutableMap<Int, List<Int>>,
    mutableUpdates: MutableList<List<Int>>,
) {
    var isUpdatesPartReached = false
    val precedenceRuleDelimiter = "|"
    val updateElementDelimiter = ","

    Path("src/Day05.txt").forEachLine { line ->
        when {
            !isUpdatesPartReached && line == "" -> {
                isUpdatesPartReached = true
            }

            !isUpdatesPartReached -> {
                val currentRule = line.split(precedenceRuleDelimiter).map { it.toInt() }
                val rulesForEarlierPageOfCurrentRule = mutablePrecedenceRules[currentRule.first()]

                if (rulesForEarlierPageOfCurrentRule == null) {
                    mutablePrecedenceRules[currentRule.first()] = listOf(currentRule[1])
                } else {
                    mutablePrecedenceRules[currentRule.first()] =
                        rulesForEarlierPageOfCurrentRule + currentRule[1]
                }
            }

            else -> {
                mutableUpdates.add(line.split(updateElementDelimiter).map { it.toInt() })
            }
        }
    }
}

private fun List<Int>.getFirstInvalidElementIndex(
    precedentRules: Map<Int, List<Int>>,
): Int? {
    forEachIndexed { currentUpdateElementIndex, currentUpdateElement ->
        val currentRules = precedentRules[currentUpdateElement]

        if (
            currentRules != null && subList(0, currentUpdateElementIndex).any { it in currentRules }
        ) {
            return currentUpdateElementIndex
        }
    }

    return null
}

private fun List<Int>.getMiddleUpdateElement(): Int = get(lastIndex / 2)

private fun List<Int>.getFixedUpdate(precedentRules: Map<Int, List<Int>>): List<Int> {
    var fixedUpdate = this.toMutableList()
    var invalidIndex =  getFirstInvalidElementIndex(precedentRules)

    while (invalidIndex != null) {
        val indexToMoveInvalidElement =  fixedUpdate.indexOfFirst {
            it in precedentRules[get(invalidIndex!!)]!!
        }

        fixedUpdate = fixedUpdate.apply {
            add(indexToMoveInvalidElement, fixedUpdate[invalidIndex!!])
            removeAt(invalidIndex!! + 1)
        }

        invalidIndex =  fixedUpdate.getFirstInvalidElementIndex(precedentRules)
    }

    return fixedUpdate
}
