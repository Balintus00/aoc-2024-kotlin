fun main() {
    var task1WordCounter = 0
    var task2PatternCounter = 0
    val task1SearchedWord = "XMAS"

    val inputLines = readInput("Day04")

    inputLines.forEachIndexed { lineIndex, line ->
        line.forEachIndexed { characterIndex, character ->
            when {
                character == task1SearchedWord.first() -> run {
                    task1WordCounter += inputLines.getTask1MatchingWordCount(
                        startIndexVertical = lineIndex,
                        startIndexHorizontal = characterIndex,
                        word = task1SearchedWord,
                    )
                }

                inputLines.isPositionTask2PatternCenter(
                    centerIndexVertical = lineIndex,
                    centerIndexHorizontal = characterIndex,
                ) -> run {
                    task2PatternCounter += 1
                }
            }
        }
    }

    println("Task 1 searched word count: $task1WordCounter")
    println("Task 2 searched pattern count: $task2PatternCounter")
}

private fun List<String>.getTask1MatchingWordCount(
    startIndexVertical: Int,
    startIndexHorizontal: Int,
    word: String,
): Int = run {
    var positionalCounter = 0

    (-1..1).forEach { verticalStep ->
        (-1..1).forEach horizontal@{ horizontalStep ->
            if (!(verticalStep == 0 && horizontalStep == 0) && isMatchingSearchedWord(
                    startIndexVertical = startIndexVertical,
                    startIndexHorizontal = startIndexHorizontal,
                    word = word,
                    verticalStep = verticalStep,
                    horizontalStep = horizontalStep,
                )
            ) {
                positionalCounter += 1
            }
        }
    }

    positionalCounter
}

private fun List<String>.isMatchingSearchedWord(
    startIndexVertical: Int,
    startIndexHorizontal: Int,
    word: String,
    verticalStep: Int,
    horizontalStep: Int,
): Boolean {
    val verticalEndingPosition = startIndexVertical + (word.length - 1) * verticalStep
    // Assuming every line has the same length
    val horizontalEndingPosition = startIndexHorizontal + (word.length - 1) * horizontalStep

    return verticalEndingPosition in indices && horizontalEndingPosition in 0..<get(
        startIndexVertical
    ).length && run {
        word.indices.fold("") { acc, i ->
            acc + get(startIndexVertical + i * verticalStep)[
                startIndexHorizontal + i * horizontalStep
            ]
        } == word
    }
}

private fun List<String>.isPositionTask2PatternCenter(
    centerIndexVertical: Int,
    centerIndexHorizontal: Int,
): Boolean {
    val centerCharacter = 'A'
    val sideCharacters = setOf('M', 'S')

    return getCharacterOrNull(
        verticalIndex = centerIndexVertical,
        horizontalIndex = centerIndexHorizontal,
    ) == centerCharacter
            && getCharacterOrNull(
        verticalIndex = centerIndexVertical - 1,
        horizontalIndex = centerIndexHorizontal - 1,
    ) in sideCharacters
            && getCharacterOrNull(
        verticalIndex = centerIndexVertical - 1,
        horizontalIndex = centerIndexHorizontal + 1,
    ) in sideCharacters
            && getCharacterOrNull(
        verticalIndex = centerIndexVertical + 1,
        horizontalIndex = centerIndexHorizontal - 1,
    ) in sideCharacters
            && getCharacterOrNull(
        verticalIndex = centerIndexVertical + 1,
        horizontalIndex = centerIndexHorizontal + 1,
    ) in sideCharacters
            && getCharacter(
        verticalIndex = centerIndexVertical - 1,
        horizontalIndex = centerIndexHorizontal - 1,
    ) != getCharacter(
        verticalIndex = centerIndexVertical + 1,
        horizontalIndex = centerIndexHorizontal + 1,
    )
            && getCharacter(
        verticalIndex = centerIndexVertical + 1,
        horizontalIndex = centerIndexHorizontal - 1,
    ) != getCharacter(
        verticalIndex = centerIndexVertical - 1,
        horizontalIndex = centerIndexHorizontal + 1,
    )
}

private fun List<String>.getCharacterOrNull(verticalIndex: Int, horizontalIndex: Int): Char? =
    getOrNull(verticalIndex)?.getOrNull(horizontalIndex)

private fun List<String>.getCharacter(verticalIndex: Int, horizontalIndex: Int): Char =
    get(verticalIndex)[horizontalIndex]
