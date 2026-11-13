package ua.edu.lab3.mazegame.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import ua.edu.lab3.mazegame.model.Direction
import ua.edu.lab3.mazegame.model.MazeGrid
import ua.edu.lab3.mazegame.util.AppLogger
import kotlin.math.abs
import kotlin.math.min

/**
 * Кастомний View-компонент для візуалізації лабіринту та обробки свайпів.
 */
class MazeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var mazeGrid: MazeGrid? = null
        set(value) {
            field = value
            invalidate()
        }

    var onSwipeMoveListener: ((Direction) -> Unit)? = null

    private val wallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CBD5E1")
        strokeWidth = 6f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val playerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F59E0B")
        style = Paint.Style.FILL
    }

    private val exitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#10B981")
        style = Paint.Style.FILL
    }

    private val boardBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1E293B")
        style = Paint.Style.FILL
    }

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean = true

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 == null) return false
            val diffX = e2.x - e1.x
            val diffY = e2.y - e1.y

            if (abs(diffX) > abs(diffY)) {
                if (abs(diffX) > 40) {
                    val dir = if (diffX > 0) Direction.RIGHT else Direction.LEFT
                    AppLogger.debug("Swipe gesture detected: $dir")
                    onSwipeMoveListener?.invoke(dir)
                    return true
                }
            } else {
                if (abs(diffY) > 40) {
                    val dir = if (diffY > 0) Direction.DOWN else Direction.UP
                    AppLogger.debug("Swipe gesture detected: $dir")
                    onSwipeMoveListener?.invoke(dir)
                    return true
                }
            }
            return false
        }
    })

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val grid = mazeGrid ?: return

        val padding = 24f
        val availableWidth = width - padding * 2
        val availableHeight = height - padding * 2

        val cellSize = min(availableWidth / grid.cols, availableHeight / grid.rows)
        val totalMazeWidth = cellSize * grid.cols
        val totalMazeHeight = cellSize * grid.rows

        val offsetX = (width - totalMazeWidth) / 2f
        val offsetY = (height - totalMazeHeight) / 2f

        // Фон лабіринту
        canvas.drawRoundRect(
            offsetX - 8f,
            offsetY - 8f,
            offsetX + totalMazeWidth + 8f,
            offsetY + totalMazeHeight + 8f,
            16f,
            16f,
            boardBgPaint
        )

        // Малювання клітинки виходу
        val exitLeft = offsetX + grid.exitCol * cellSize + cellSize * 0.2f
        val exitTop = offsetY + grid.exitRow * cellSize + cellSize * 0.2f
        canvas.drawRoundRect(
            exitLeft,
            exitTop,
            exitLeft + cellSize * 0.6f,
            exitTop + cellSize * 0.6f,
            10f,
            10f,
            exitPaint
        )

        // Малювання стін лабіринту
        for (r in 0 until grid.rows) {
            for (c in 0 until grid.cols) {
                val cell = grid.cells[r][c]
                val x1 = offsetX + c * cellSize
                val y1 = offsetY + r * cellSize
                val x2 = x1 + cellSize
                val y2 = y1 + cellSize

                if (cell.topWall) canvas.drawLine(x1, y1, x2, y1, wallPaint)
                if (cell.bottomWall) canvas.drawLine(x1, y2, x2, y2, wallPaint)
                if (cell.leftWall) canvas.drawLine(x1, y1, x1, y2, wallPaint)
                if (cell.rightWall) canvas.drawLine(x2, y1, x2, y2, wallPaint)
            }
        }

        // Малювання гравця (об'єкта, який виводимо з лабіринту)
        val playerCenterX = offsetX + (grid.playerCol + 0.5f) * cellSize
        val playerCenterY = offsetY + (grid.playerRow + 0.5f) * cellSize
        canvas.drawCircle(playerCenterX, playerCenterY, cellSize * 0.32f, playerPaint)
    }
}
