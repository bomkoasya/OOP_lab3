package ua.edu.lab3.mazegame

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ua.edu.lab3.mazegame.model.MazeGrid

class MazeGridTest {

    @Test
    fun `maze generates all cells as visited`() {
        val grid = MazeGrid(8, 8)
        for (r in 0 until grid.rows) {
            for (c in 0 until grid.cols) {
                assertTrue("Cell ($r, $c) should be visited after DFS generation", grid.cells[r][c].visited)
            }
        }
    }

    @Test
    fun `player starts at top left and exit is at bottom right`() {
        val grid = MazeGrid(10, 10)
        assertEquals(0, grid.playerRow)
        assertEquals(0, grid.playerCol)
        assertEquals(9, grid.exitRow)
        assertEquals(9, grid.exitCol)
        assertFalse(grid.isExitReached())
    }
}
