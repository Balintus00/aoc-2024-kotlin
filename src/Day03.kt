fun main() {
    var sumOfMultiplicationInstructions = 0

    val inputLines = readInput("Day03")

    inputLines.forEach { line ->
        sumOfMultiplicationInstructions += getSumOfMultiplicationsInLineTask1(line)
    }

    val sumOfConditionalMultiplicationInstructions =
        getSumOfConditionalMultiplicationInstructionsTask2(inputLines)

    println("Sum of multiplication instructions Task 1: $sumOfMultiplicationInstructions")
    println(
        "Sum of conditional multiplication instructions Task 2: " +
                "$sumOfConditionalMultiplicationInstructions"
    )
}

private fun getSumOfMultiplicationsInLineTask1(line: String): Int {
    var sumOfMultiplicationInstructions = 0

    val validMultiplicationInstructionRegex = Regex("mul\\(\\d{1,3},\\d{1,3}\\)")
    val multiplicationSeparator = ","

    val validMultiplicationInstructions = validMultiplicationInstructionRegex.findAll(line)
        .map { it.value }
    validMultiplicationInstructions.forEach { instruction ->
        sumOfMultiplicationInstructions += instruction.split(multiplicationSeparator).map {
            it.filter { instructionCharacter -> instructionCharacter.isDigit() }.toInt()
        }.reduce { acc, i -> acc * i }
    }

    return sumOfMultiplicationInstructions
}

private fun getSumOfConditionalMultiplicationInstructionsTask2(lines: List<String>): Int {
    val disablerInstruction = "don't()"
    val enablerInstruction = "do()"
    val explicitlyEnabledPartRegex = Regex("do\\(\\).+?don't\\(\\)")

    val mergedLines = lines.joinToString(separator = "")

    val startingImplicitlyEnabledPart = mergedLines.split(disablerInstruction).first()
    val explicitlyEnabledParts = explicitlyEnabledPartRegex.findAll(mergedLines).map { it.value }
    // Note that in this case this will always be null because of the task's specific input
    val endingExplicitlyEnabledPart = if (
        mergedLines.lastIndexOf(enablerInstruction) > mergedLines.lastIndexOf(disablerInstruction)
    ) {
        mergedLines.split(enablerInstruction).last()
    } else {
        null
    }

    return getSumOfMultiplicationsInLineTask1(startingImplicitlyEnabledPart) +
            explicitlyEnabledParts.fold(0) { acc, part ->
                acc + getSumOfMultiplicationsInLineTask1(part)
            } + (endingExplicitlyEnabledPart?.let { getSumOfMultiplicationsInLineTask1(it) } ?: 0)
}
