package games.gameOfFifteen

import board.Direction
import board.createGameBoard
import games.game.Game

/*
 * Implement the Game of Fifteen (https://en.wikipedia.org/wiki/15_puzzle).
 * When you finish, you can play the game by executing 'PlayGameOfFifteen'.
 */
fun newGameOfFifteen(initializer: GameOfFifteenInitializer = RandomGameInitializer()): Game =
        GameOfFifteen(initializer)

class GameOfFifteen(private val initializer: GameOfFifteenInitializer) : Game {
    private val board = createGameBoard<Int?>(4)

    override fun initialize() {
        val initialPermutation = initializer.initialPermutation
        for (i in 1..board.width) {
            for (j in 1..board.width) {
                val index = (i - 1) * board.width + (j - 1)
                val cell = board.getCell(i, j)
                board[cell] = if (index < board.width * board.width - 1) {
                    initialPermutation[index]
                } else {
                    null
                }
            }
        }
    }

    override fun canMove(): Boolean = true

    override fun hasWon(): Boolean {
        var won = true
        for (i in 1..board.width) {
            for (j in 1..board.width) {
                val index = (i - 1) * board.width + (j - 1) + 1
                val cell = board.getCell(i, j)
                won = won && (if (index < board.width * board.width) {
                    board[cell] == index
                } else {
                    board[cell] == null
                })
            }
        }
        return won
    }

    override fun processMove(direction: Direction) {
        val empty = board.find { it == null } ?: return
        board.apply {
            val operator = when (direction) {
                Direction.DOWN -> {
                    empty.getNeighbour(Direction.UP)
                }
                Direction.UP -> {
                    empty.getNeighbour(Direction.DOWN)
                }
                Direction.RIGHT -> {
                    empty.getNeighbour(Direction.LEFT)
                }
                Direction.LEFT -> {
                    empty.getNeighbour(Direction.RIGHT)
                }
            } ?: return
            val tmp = board[empty]
            board[empty] = board[operator]
            board[operator] = tmp
        }

    }

    override fun get(i: Int, j: Int): Int? {
        val cell = board.getCell(i, j)
        return board[cell]
    }


}

