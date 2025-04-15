package com.example.lessontictactoe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

// Визначення кольорів
val SoftBlue = Color(0xFF5365A9)
val LightPurple = Color(0xFFE6E6FA)

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var gridSize by remember { mutableStateOf(3) }
    var currentPlayer by remember { mutableStateOf("X") }
    var board by remember { mutableStateOf(List(gridSize) { MutableList(gridSize) { "" } }) }
    var winner by remember { mutableStateOf<String?>(null) }
    var playerXScore by remember { mutableStateOf(0) }
    var playerOScore by remember { mutableStateOf(0) }
    var timeRemaining by remember { mutableStateOf(if (gridSize == 3) 5 else 10) }
    var scoreUpdated by remember { mutableStateOf(false) }
    var lastMoveMade by remember { mutableStateOf(false) }
    var isBotEnabled by remember { mutableStateOf(false) }
    var showScore by remember { mutableStateOf(false) }

    // Таймер
    LaunchedEffect(currentPlayer, winner, gridSize) {
        if (winner == null) {
            timeRemaining = if (gridSize == 3) 5 else 10
            lastMoveMade = false
            while (timeRemaining > 0 && !lastMoveMade && winner == null) {
                delay(1000L)
                timeRemaining -= 1
            }
            if (!lastMoveMade && winner == null) {
                currentPlayer = if (currentPlayer == "X") "O" else "X"
            }
        }
    }

    // Якщо бот увімкнений, на хід "O" він вибирає випадкову клітинку
    if (isBotEnabled && currentPlayer == "O" && winner == null) {
        LaunchedEffect(currentPlayer, board) {
            delay(500) // Затримка для "відтворення" ходу бота
            val nextBoard = makeBotMove(board)
            if (nextBoard != board) {
                board = nextBoard
                lastMoveMade = true
                if (checkWinner(board, "O")) {
                    winner = "O"
                } else if (checkDraw(board)) {
                    winner = "Нічия"
                } else {
                    currentPlayer = "X"
                    lastMoveMade = false
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Оберіть розмір поля", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            listOf(3, 4, 5).forEach { size ->
                Button(
                    onClick = {
                        gridSize = size
                        board = List(size) { MutableList(size) { "" } }
                        currentPlayer = "X"
                        winner = null
                        timeRemaining = if (size == 3) 5 else 10
                        scoreUpdated = false
                        lastMoveMade = false
                    },
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text("${size}x$size")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка для вибору гри з ботом
        Row {
            Button(
                onClick = { isBotEnabled = !isBotEnabled },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isBotEnabled) SoftBlue else LightPurple,
                    contentColor = Color.White
                )
            ) {
                Text(if (isBotEnabled) "Грати проти людини" else "Грати з ботом")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Рахунок - X: $playerXScore, O: $playerOScore", fontSize = 18.sp)

        Spacer(modifier = Modifier.height(16.dp))

        if (winner != null) {
            Text(
                text = if (winner == "Нічия") "Нічия!" else "Переміг: $winner",
                fontSize = 24.sp,
                color = if (winner == "Нічия") Color.Gray else Color.Green,
                fontWeight = FontWeight.Bold
            )

            if (!scoreUpdated) {
                if (winner == "X") playerXScore++
                else if (winner == "O") playerOScore++
                scoreUpdated = true
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                board = List(gridSize) { MutableList(gridSize) { "" } }
                currentPlayer = "X"
                winner = null
                timeRemaining = if (gridSize == 3) 5 else 10
                scoreUpdated = false
                lastMoveMade = false
            }) {
                Text("Наступний раунд")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Час на хід: $timeRemaining секунд", fontSize = 24.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Обчислюємо бажаний розмір сітки на основі розміру клітинки
            val cellSize = 65.dp // Зменшений розмір клітинки
            val boardSize = (cellSize * gridSize) + (4.dp * gridSize) // Враховуємо відступи між клітинками

            BoardGrid(
                board = board,
                gridSize = gridSize,
                modifier = Modifier.size(boardSize), // Задаємо явний розмір для BoardGrid
                onClick = { row, col ->
                    if (board[row][col] == "" && winner == null) {
                        val updatedBoard = board.map { it.toMutableList() }.toMutableList()
                        updatedBoard[row][col] = currentPlayer
                        board = updatedBoard
                        lastMoveMade = true
                        if (checkWinner(board, currentPlayer)) {
                            winner = currentPlayer
                        } else if (checkDraw(board)) {
                            winner = "Нічия"
                        } else {
                            currentPlayer = if (currentPlayer == "X") "O" else "X"
                            lastMoveMade = false
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround // Розміщуємо кнопки по центру з відступами
        ) {
            Button(
                onClick = {
                    board = List(gridSize) { MutableList(gridSize) { "" } }
                    winner = null
                    timeRemaining = if (gridSize == 3) 5 else 10
                    scoreUpdated = false
                    lastMoveMade = false
                },
                modifier = Modifier.weight(1f) // Розділяємо доступний простір між кнопками
            ) {
                Text("Скинути")
            }

            Button(
                onClick = {
                    gridSize = 3
                    board = List(3) { MutableList(3) { "" } }
                    currentPlayer = "X"
                    winner = null
                    playerXScore = 0
                    playerOScore = 0
                    timeRemaining = 5
                    scoreUpdated = false
                    lastMoveMade = false
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Нова гра")
            }

            Button(
                onClick = {
                    showScore = !showScore
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (showScore) "Приховати" else "Рахунок")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showScore) {
            Text("Рахунок - X: $playerXScore, O: $playerOScore", fontSize = 20.sp)
        }
    }
}

@Composable
fun BoardGrid(
    board: List<List<String>>,
    gridSize: Int,
    modifier: Modifier = Modifier, // Додаємо модифікатор для BoardGrid
    onClick: (Int, Int) -> Unit
) {
    Column(modifier = modifier) {
        for (i in 0 until gridSize) {
            Row {
                for (j in 0 until gridSize) {
                    Cell(value = board[i][j], onClick = { onClick(i, j) })
                }
            }
        }
    }
}

@Composable
fun Cell(value: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(65.dp) // Зменшений розмір клітинки
            .padding(4.dp),
        enabled = value.isEmpty(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.LightGray,
            contentColor = Color.Black
        )
    ) {
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold) // Зменшений розмір тексту
    }
}

fun checkWinner(board: List<List<String>>, player: String): Boolean {
    val n = board.size
    // Check rows
    for (i in 0 until n) {
        if (board[i].all { it == player }) return true
    }
    // Check columns
    for (j in 0 until n) {
        if ((0 until n).all { board[it][j] == player }) return true
    }
    // Check diagonals
    if ((0 until n).all { board[it][it] == player }) return true
    if ((0 until n).all { board[it][n - 1 - it] == player }) return true
    return false
}

fun checkDraw(board: List<List<String>>): Boolean {
    return board.all { row -> row.all { it.isNotEmpty() } }
}

fun makeBotMove(board: List<List<String>>): List<MutableList<String>> {
    val n = board.size
    val availableCells = mutableListOf<Pair<Int, Int>>()
    for (i in 0 until n) {
        for (j in 0 until n) {
            if (board[i][j] == "") {
                availableCells.add(Pair(i, j))
            }
        }
    }
    if (availableCells.isNotEmpty()) {
        val randomIndex = Random.nextInt(availableCells.size)
        val (row, col) = availableCells[randomIndex]
        return board.map { it.toMutableList() }.toMutableList().apply { this[row][col] = "O" }
    }
    return board.map { it.toMutableList() }.toMutableList()
}