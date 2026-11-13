package ua.edu.lab3.mazegame.model

import ua.edu.lab3.mazegame.util.AppLogger
import java.util.Stack
import kotlin.random.Random

/**
 * Генератор та модель лабіринту на основі алгоритму Recursive Backtracker (DFS).
 * Гарантує існування шляху від стартової клітинки (0, 0) до виходу (rows - 1, cols - 1).
 */
class MazeGrid(val rows: Int, val cols: Int, private val random: Random = Random.Default) {

    val cells: Array<Array<Cell>> = Array(rows) { r ->
        Array(cols) { c -> Cell(r, c) }
    }

    var playerRow: Int = 0
        private set
    var playerCol: Int = 0
        private set

    val exitRow: Int = rows - 1
    val exitCol: Int = cols - 1

    var movesCount: Int = 0
        private set

    init {
        generateMaze()
    }

    private fun generateMaze() {
        AppLogger.info("Generating new maze of size ${rows}x${cols} using DFS Backtracker...")
        val stack = Stack<Cell>()
        val startCell = cells[0][0]
        startCell.visited = true
        stack.push(startCell)

        while (stack.isNotEmpty()) {
            val current = stack.peek()
            val unvisitedNeighbors = getUnvisitedNeighbors(current)

            if (unvisitedNeighbors.isNotEmpty()) {
                val next = unvisitedNeighbors[random.nextInt(unvisitedNeighbors.size)]
                removeWallBetween(current, next)
                next.visited = true
                stack.push(next)
            } else {
                stack.pop()
            }
        }
        AppLogger.info("Maze generation finished. Start=(0,0), Exit=($exitRow,$exitCol)")
    }

    private fun getUnvisitedNeighbors(cell: Cell): List<Cell> {
        val neighbors = mutableListOf<Cell>()
        val r = cell.row
        val c = cell.col

        if (r > 0 && !cells[r - 1][c].visited) neighbors.add(cells[r - 1][c])
        if (r < rows - 1 && !cells[r + 1][c].visited) neighbors.add(cells[r + 1][c])
        if (c > 0 && !cells[r][c - 1].visited) neighbors.add(cells[r][c - 1])
        if (c < cols - 1 && !cells[r][c + 1].visited) neighbors.add(cells[r][c + 1])

        return neighbors
    }

    private fun removeWallBetween(current: Cell, next: Cell) {
        val dRow = next.row - current.row
        val dCol = next.col - current.col

        when {
            dRow == -1 -> {
                current.topWall = false
                next.bottomWall = false
            }
            dRow == 1 -> {
                current.bottomWall = false
                next.topWall = false
            }
            dCol == -1 -> {
                current.leftWall = false
                next.rightWall = false
            }
            dCol == 1 -> {
                current.rightWall = false
                next.leftWall = false
            }
        }
    }

    /**
     * Спроба перемістити гравця у заданому напрямку.
     * Повертає true, якщо хід відбувся, або false, якщо на шляху стіна чи межа поля.
     */
    fun movePlayer(direction: Direction): Boolean {
        val currentCell = cells[playerRow][playerCol]
        val blocked = when (direction) {
            Direction.UP -> currentCell.topWall
            Direction.DOWN -> currentCell.bottomWall
            Direction.LEFT -> currentCell.leftWall
            Direction.RIGHT -> currentCell.rightWall
        }

        if (blocked) {
            AppLogger.warn("Move blocked by wall: direction=$direction at pos=($playerRow, $playerCol)")
            return false
        }

        val targetRow = playerRow + direction.dRow
        val targetCol = playerCol + direction.dCol

        if (targetRow !in 0 until rows || targetCol !in 0 until cols) {
            AppLogger.warn("Move out of bounds prevented: target=($targetRow, $targetCol)")
            return false
        }

        playerRow = targetRow
        playerCol = targetCol
        movesCount++
        AppLogger.debug("Player moved $direction to ($playerRow, $playerCol). Total moves: $movesCount")
        return true
    }

    fun isExitReached(): Boolean = playerRow == exitRow && playerCol == exitCol
}
