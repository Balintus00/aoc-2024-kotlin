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

    val solvableEquations = equations.filterSolvableEquations()

    println("Task 1 solvable equations' results' sum: ${solvableEquations.sumOf { it.first }}")
}

private fun List<Pair<Long, List<Long>>>.filterSolvableEquations(): List<Pair<Long, List<Long>>> {
    val solvableEquations = mutableListOf<Pair<Long, List<Long>>>()
    val operators = listOf<(Long, Long) -> Long>(Long::plus, Long::times)

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
