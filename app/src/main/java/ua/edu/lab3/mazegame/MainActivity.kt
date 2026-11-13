package ua.edu.lab3.mazegame

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import ua.edu.lab3.mazegame.model.Direction
import ua.edu.lab3.mazegame.model.MazeGrid
import ua.edu.lab3.mazegame.ui.MazeView
import ua.edu.lab3.mazegame.util.AppLogger

class MainActivity : AppCompatActivity() {

    private lateinit var mazeView: MazeView
    private lateinit var tvLevel: TextView
    private lateinit var tvMoves: TextView

    private lateinit var mazeGrid: MazeGrid
    private var currentLevel = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentLevel = savedInstanceState?.getInt(KEY_LEVEL, 1) ?: 1
        AppLogger.info("MainActivity created. Current level=$currentLevel")

        mazeView = findViewById(R.id.mazeView)
        tvLevel = findViewById(R.id.tvLevel)
        tvMoves = findViewById(R.id.tvMoves)

        findViewById<Button>(R.id.btnUp).setOnClickListener { handleMove(Direction.UP) }
        findViewById<Button>(R.id.btnDown).setOnClickListener { handleMove(Direction.DOWN) }
        findViewById<Button>(R.id.btnLeft).setOnClickListener { handleMove(Direction.LEFT) }
        findViewById<Button>(R.id.btnRight).setOnClickListener { handleMove(Direction.RIGHT) }

        findViewById<Button>(R.id.btnNewMaze).setOnClickListener {
            AppLogger.info("User requested manual maze regeneration at level $currentLevel")
            startLevel(currentLevel)
        }

        findViewById<Button>(R.id.btnLanguage).setOnClickListener {
            toggleLanguage()
        }

        mazeView.onSwipeMoveListener = { direction ->
            handleMove(direction)
        }

        startLevel(currentLevel)
    }

    private fun startLevel(level: Int) {
        val dimension = (6 + level * 2).coerceAtMost(20)
        mazeGrid = MazeGrid(dimension, dimension)
        mazeView.mazeGrid = mazeGrid
        updateScoreboard()
    }

    private fun handleMove(direction: Direction) {
        if (mazeGrid.movePlayer(direction)) {
            mazeView.invalidate()
            updateScoreboard()

            if (mazeGrid.isExitReached()) {
                AppLogger.info("Level $currentLevel completed in ${mazeGrid.movesCount} moves!")
                showWinDialog()
            }
        }
    }

    private fun updateScoreboard() {
        tvLevel.text = getString(R.string.level_format, currentLevel, mazeGrid.rows, mazeGrid.cols)
        tvMoves.text = getString(R.string.moves_format, mazeGrid.movesCount)
    }

    private fun showWinDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.win_title)
            .setMessage(getString(R.string.win_message, mazeGrid.movesCount))
            .setCancelable(false)
            .setPositiveButton(R.string.win_next_level) { _, _ ->
                currentLevel++
                AppLogger.info("Advancing to level $currentLevel")
                startLevel(currentLevel)
            }
            .show()
    }

    /**
     * Перемикання локалі між українською (uk) та англійською (en) за допомогою AndroidX i18n API.
     */
    private fun toggleLanguage() {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        val currentTag = if (!currentLocales.isEmpty) {
            currentLocales[0]?.language ?: "uk"
        } else {
            resources.configuration.locales[0].language
        }

        val nextLang = if (currentTag == "uk") "en" else "uk"
        AppLogger.info("Switching application language from '$currentTag' to '$nextLang'")
        val appLocale = LocaleListCompat.forLanguageTags(nextLang)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_LEVEL, currentLevel)
    }

    companion object {
        private const val KEY_LEVEL = "key_current_level"
    }
}
