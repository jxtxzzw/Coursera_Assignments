package board

import board.Direction.*

open class SquareBoardImpl(final override val width: Int) : SquareBoard {

    var cells: Array<Array<Cell>> = Array(width) {
        i -> Array(width) {
            j -> Cell(i + 1, j + 1)
        }
    }

    private fun valid(i: Int, j: Int): Boolean = (i in 1..width && j in 1..width)

    override fun getCellOrNull(i: Int, j: Int): Cell? {
        return if (!valid(i, j)) {
            null
        } else {
            return cells[i - 1][j - 1]
        }
    }

    override fun getCell(i: Int, j: Int): Cell {
        if (!valid(i, j)) {
            throw IllegalArgumentException()
        } else {
            return  cells[i - 1][j - 1]
        }
    }

    override fun getAllCells(): Collection<Cell> {
        val lst = ArrayList<Cell>()
        for (i in 1..width) {
            for (j in 1..width) {
                lst.add( cells[i - 1][j - 1])
            }
        }
        return lst
    }

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
        val lst = ArrayList<Cell>()
        val trueJRange: IntProgression = if (jRange.step == 1) {
            1.coerceAtLeast(jRange.first)..width.coerceAtMost(jRange.last)
        } else {
            width.coerceAtMost(jRange.first) downTo 1.coerceAtLeast(jRange.last)
        }

        for (j in trueJRange) {
            lst.add(cells[i - 1][j - 1])
        }
        return lst
    }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {
        val lst = ArrayList<Cell>()
        val trueIRange: IntProgression = if (iRange.step == 1) {
            1.coerceAtLeast(iRange.first)..width.coerceAtMost(iRange.last)
        } else {
            width.coerceAtMost(iRange.first) downTo 1.coerceAtLeast(iRange.last)
        }
        for (i in trueIRange) {
            lst.add(cells[i - 1][j - 1])
        }
        return lst
    }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
        return when (direction) {
            UP -> getCellOrNull(i - 1, j)
            LEFT -> getCellOrNull(i, j - 1)
            DOWN -> getCellOrNull(i + 1, j)
            RIGHT -> getCellOrNull(i, j + 1)
        }
    }

}

fun createSquareBoard(width: Int): SquareBoard = SquareBoardImpl(width)

class GameBoardImpl<T>(width: Int) : SquareBoardImpl(width), GameBoard<T> {

    private var map = mutableMapOf<Cell, T?>()

    init {
        cells.forEach { row -> row.forEach { cell -> map[cell] = null } }
    }

    override fun get(cell: Cell): T? {
        return map[cell]
    }

    override fun set(cell: Cell, value: T?) {
        map[cell] = value
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> {
        return map.filter { (_, value) -> predicate(value) }.keys
    }

    override fun find(predicate: (T?) -> Boolean): Cell? {
        return map.filter { (_, value) -> predicate(value) }.keys.firstOrNull()
    }

    override fun any(predicate: (T?) -> Boolean): Boolean {
        return map.filter { (_, value) -> predicate(value) }.isNotEmpty()
    }

    override fun all(predicate: (T?) -> Boolean): Boolean {
        return map.filter { (_, value) -> !predicate(value) }.isEmpty()
    }

}

fun <T> createGameBoard(width: Int): GameBoard<T> = GameBoardImpl(width)

