import kotlin.io.path.Path
import kotlin.io.path.forEachLine

fun main() {
    val startingLaboratoryMap: MutableList<MutableList<Char>> = mutableListOf()

    Path("src/Day06.txt").forEachLine { line ->
        startingLaboratoryMap.add(line.toCharArray().toMutableList())
    }

    val obstructionMarker = '#'
    val guardVisitedPositionMarker = 'X'

    val laboratoryMapWithGuardMovement = startingLaboratoryMap.deepCopy().apply {
        simulateGuardMovement(
            obstructionMarker = obstructionMarker,
            guardVisitedPositionMarker = guardVisitedPositionMarker,
        )
    }

    val guardVisitedPositions = mutableSetOf<Pair<Int, Int>>().apply {
        laboratoryMapWithGuardMovement.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { positionIndex, position ->
                if (position == guardVisitedPositionMarker) add(rowIndex to positionIndex)
            }
        }
    }

    val guardStartingPosition: Pair<Int, Int> = requireNotNull(
        startingLaboratoryMap.getGuardPosition()
    ) {
        "No guard was found on the initial laboratory map!\n" +
                startingLaboratoryMap.toDebugTextMap()
    }

    var loopCauserObstructionPositionCounter = 0
    guardVisitedPositions.forEach {
        if (
            it != guardStartingPosition
            && startingLaboratoryMap.deepCopy().apply {
                get(it.first)[it.second] = obstructionMarker
            }.isGuardLooping(obstructionMarker = obstructionMarker)
        ) {
            loopCauserObstructionPositionCounter += 1
        }
    }

    println("Task 1 guard distinct position before leaving are: ${guardVisitedPositions.size}")
    println(
        "Task 2 loop causer obstruction position possibility count: " +
                "$loopCauserObstructionPositionCounter"
    )
}

private fun List<List<Char>>.getGuardPosition(): Pair<Int, Int>? {
    forEachIndexed { rowIndex, row ->
        row.forEachIndexed { positionIndex, position ->
            if (position in GuardDirection.entries.map { it.marker }.toSet()) {
                return rowIndex to positionIndex
            }
        }
    }

    return null
}

private enum class GuardDirection {
    UP {
        override val marker: Char = '^'

        override val nextDirection: GuardDirection
            get() = RIGHT

        override val step: Pair<Int, Int> = -1 to 0
    },
    RIGHT {
        override val marker: Char = '>'

        override val nextDirection: GuardDirection
            get() = DOWN

        override val step: Pair<Int, Int> = 0 to 1
    },
    DOWN {
        override val marker: Char = 'V'

        override val nextDirection: GuardDirection
            get() = LEFT

        override val step: Pair<Int, Int> = 1 to 0
    },
    LEFT {
        override val marker: Char = '<'

        override val nextDirection: GuardDirection
            get() = UP

        override val step: Pair<Int, Int> = 0 to -1
    };

    abstract val marker: Char

    abstract val nextDirection: GuardDirection

    abstract val step: Pair<Int, Int>
}

private fun List<List<Char>>.deepCopy(): MutableList<MutableList<Char>> = map {
    it.toMutableList()
}.toMutableList()

private fun MutableList<MutableList<Char>>.simulateGuardMovement(
    obstructionMarker: Char,
    guardVisitedPositionMarker: Char,
) {
    var isGuardOut = false
    while (!isGuardOut) {
        getGuardPosition()?.let { guardPosition ->
            step(
                guardPosition = guardPosition,
                obstructionMarker = obstructionMarker,
                guardVisitedPositionMarker = guardVisitedPositionMarker,
            )
        } ?: run {
            isGuardOut = true
        }
    }
}

private fun MutableList<MutableList<Char>>.step(
    guardPosition: Pair<Int, Int>,
    obstructionMarker: Char,
    guardVisitedPositionMarker: Char,
    guardStuckExceptionMessage: String = "Guard stuck!",
) {
    var guardDirection = getGuardDirection(guardPosition)

    get(guardPosition.first)[guardPosition.second] = guardVisitedPositionMarker

    var isStepPerformed = false
    var turnCounter = 0

    while (!isStepPerformed) {
        when {
            turnCounter == GuardDirection.entries.size -> {
                throw IllegalStateException(guardStuckExceptionMessage)
            }

            getOrNull(guardPosition.first + guardDirection.step.first)
                ?.getOrNull(guardPosition.second + guardDirection.step.second) == null -> {
                isStepPerformed = true
            }

            getOrNull(guardPosition.first + guardDirection.step.first)
                ?.getOrNull(guardPosition.second + guardDirection.step.second)
                    == obstructionMarker -> {
                guardDirection = guardDirection.nextDirection
                turnCounter += 1
            }

            else -> {
                get(guardPosition.first + guardDirection.step.first)[
                    guardPosition.second + guardDirection.step.second
                ] = guardDirection.marker
                isStepPerformed = true
            }
        }
    }
}

private fun List<List<Char>>.getGuardDirection(guardPosition: Pair<Int, Int>): GuardDirection {
    val guardMarker = get(guardPosition.first)[guardPosition.second]

    return requireNotNull(
        GuardDirection.entries.firstOrNull { it.marker == guardMarker }
    ) {
        "No guard was found at the given position: " +
                "(${guardPosition.first},${guardPosition.second}) in map:\n${toDebugTextMap()}"
    }
}

private fun List<List<Char>>.toDebugTextMap(): String = fold("") { rowAcc, rowNext ->
    rowAcc + rowNext.fold("") { acc, next -> acc + next } + '\n'
}

private fun List<List<Char>>.isGuardLooping(obstructionMarker: Char): Boolean {
    val performedSteps: MutableSet<Pair<Pair<Int, Int>, Pair<Int, Int>>> = mutableSetOf()
    var guardPosition = getGuardPosition()
    var guardDirection = guardPosition?.let { getGuardDirection(it) } ?: return false

    while (guardPosition != null) {
        var isNextStepPerformed = false
        var currentGuardTurnCounter = 0

        while (!isNextStepPerformed && guardPosition != null && currentGuardTurnCounter < 4) {
            val nextGuardPositionCandidate = guardPosition.first + guardDirection.step.first to
                    guardPosition.second + guardDirection.step.second
            when (
                getOrNull(nextGuardPositionCandidate.first)?.getOrNull(
                    nextGuardPositionCandidate.second
                )
            ) {
                null -> {
                    guardPosition = null
                }

                obstructionMarker -> {
                    currentGuardTurnCounter += 1
                    guardDirection = guardDirection.nextDirection
                }

                else -> {
                    val currentPositionChange = guardPosition to nextGuardPositionCandidate

                    if (performedSteps.contains(currentPositionChange)) {
                        return  true
                    } else {
                        performedSteps.add(currentPositionChange)
                        guardPosition = nextGuardPositionCandidate
                        isNextStepPerformed = true
                    }
                }
            }
        }

        if (currentGuardTurnCounter == 4) {
            return true
        }
    }

    return false
}
