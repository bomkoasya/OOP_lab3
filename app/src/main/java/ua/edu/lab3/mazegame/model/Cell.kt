package ua.edu.lab3.mazegame.model

data class Cell(
    val row: Int,
    val col: Int,
    var topWall: Boolean = true,
    var bottomWall: Boolean = true,
    var leftWall: Boolean = true,
    var rightWall: Boolean = true,
    var visited: Boolean = false
)
