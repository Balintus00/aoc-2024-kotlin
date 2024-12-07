import kotlin.io.path.Path
import kotlin.io.path.forEachLine

fun main() {
    val laboratoryMap: MutableList<MutableList<Char>> = mutableListOf()

    Path("src/Day06.txt").forEachLine { line ->
        laboratoryMap.add(line.toCharArray().toMutableList())
    }

    val obstructionMarker = '#'
    val guardVisitedPositionMarker = 'X'

    var isGuardOut = false

    while (!isGuardOut) {
        laboratoryMap.getGuardPosition()?.let { guardPosition ->
            laboratoryMap.step(
                guardPosition = guardPosition,
                obstructionMarker = obstructionMarker,
                guardVisitedPositionMarker = guardVisitedPositionMarker,
            )
        } ?: run {
            isGuardOut = true
        }
    }

    val guardUniquePositionCount = laboratoryMap.sumOf { row ->
        row.count { it == guardVisitedPositionMarker }
    }

    println("Task 1 guard distinct position before leaving are: $guardUniquePositionCount")
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
