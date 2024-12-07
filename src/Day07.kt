import kotlin.io.path.Path
import kotlin.io.path.forEachLine
import kotlin.math.pow
import kotlin.math.roundToLong

fun main() {
    val equations: MutableList<Pair<Long, List<Long>>> = mutableListOf()

    val resultAndOperandsDelimiter = ": "
    val operandDelimiter = ' '

    Path("src/Day07.txt").forEachLine { line ->
        val resultWithOperands = line.split(resultAndOperandsDelimiter)

        equations.add(
            resultWithOperands.first().toLong() to
                    resultWithOperands[1].split(operandDelimiter).map { it.toLong() }
        )
    }

    val task1Operators = listOf<(Long, Long) -> Long>(Long::plus, Long::times)

    val solvableEquationsTask1 = equations.filterSolvableEquations(operators = task1Operators)

    val solvableEquationsTask2 = equations.filterSolvableEquations(
        operators = task1Operators + Long::concatenate,
    )

    println("Task 1 solvable equations' results' sum: ${solvableEquationsTask1.sumOf { it.first }}")
    println("Task 2 solvable equations' results' sum: ${solvableEquationsTask2.sumOf { it.first }}")
}

private fun List<Pair<Long, List<Long>>>.filterSolvableEquations(
    operators: List<(Long, Long) -> Long>,
): List<Pair<Long, List<Long>>> {
    val solvableEquations = mutableListOf<Pair<Long, List<Long>>>()

    forEach { solvableEquationCandidate ->
        if (
            isEquationSolvable(
                result = solvableEquationCandidate.first,
                operands = solvableEquationCandidate.second,
                operators = operators,
            )
        ) {
            solvableEquations.add(solvableEquationCandidate)
        }
    }

    return solvableEquations
}

private fun isEquationSolvable(
    result: Long,
    operands: List<Long>,
    operators: List<(Long, Long) -> Long>,
): Boolean = sequence<List<(Long, Long) -> Long>> {
    val operatorCountInEquation = (operands.size - 1)
    val numberOfOperators = operators.size

    for (possibilityIndex in 0 until numberOfOperators.pow(operatorCountInEquation)) {
        val currentOperatorVariation = mutableListOf<(Long, Long) -> Long>()

        var possibilityIndexCounter = possibilityIndex

        for (operationPositionIndex in operatorCountInEquation downTo 0) {
            val minimalHighestLocalValue = numberOfOperators.pow(operationPositionIndex)
            val numberOfMinimalHighestValueInCounter =
                possibilityIndexCounter / minimalHighestLocalValue

            currentOperatorVariation.add(operators[numberOfMinimalHighestValueInCounter.toInt()])

            possibilityIndexCounter -=
                numberOfMinimalHighestValueInCounter * minimalHighestLocalValue
        }

        yield(currentOperatorVariation)
    }
}.firstOrNull { operatorCombination ->
    result == operands.reduceIndexed { operandIndex, acc, operand ->
        operatorCombination[operandIndex](acc, operand)
    }
} != null

private fun Int.pow(n: Int): Long = toFloat().pow(n).roundToLong()

private fun Long.concatenate(other: Long): Long = (toString() + other.toString()).toLong()
