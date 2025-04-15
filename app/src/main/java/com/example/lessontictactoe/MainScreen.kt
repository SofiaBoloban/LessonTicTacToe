package com.example.lessontictactoe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var gridSize by remember { mutableStateOf(3) }
    var currentPlayer by remember { mutableStateOf("X") }
    var board by remember { mutableStateOf(List(gridSize) { MutableList(gridSize) { "" } }) }
    var winner by remember { mutableStateOf<String?>(null) }
    var playerXScore by remember { mutableStateOf(0) }
    var playerOScore by remember { mutableStateOf(0) }
    var timeRemaining by remember { mutableStateOf(5) }
    var scoreUpdated by remember { mutableStateOf(false) }
    var lastMoveMade by remember { mutableStateOf(false) }
    var isBotEnabled by remember { mutableStateOf(false) }
    var showScore by remember { mutableStateOf(false) }

    // Таймер
    LaunchedEffect(currentPlayer, winner) {
        if (winner == null) {
            timeRemaining = if (gridSize == 3) 5 else 10
            lastMoveMade = false
            while (timeRemaining > 0 && !lastMoveMade) {
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
        LaunchedEffect(currentPlayer) {
            delay(500) // Затримка для "відтворення" ходу бота
            makeBotMove(board)
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
            Button(onClick = { isBotEnabled = !isBotEnabled }) {
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

        // Обгортаємо поле гри в Box для скролінгу та вирівнюємо по центру
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Забезпечує, щоб поле гри займало решту місця
                .verticalScroll(rememberScrollState()) // Додаємо вертикальний скролінг
                .padding(16.dp), // Відступи навколо гри
            contentAlignment = Alignment.Center
        ) {
            BoardGrid(board, gridSize, onClick = { row, col ->
                if (board[row][col] == "" && winner == null) {
                    board[row][col] = currentPlayer
                    lastMoveMade = true
                    if (checkWinner(board, currentPlayer)) {
                        winner = currentPlayer
                    } else if (checkDraw(board)) {
                        winner = "Нічия"
                    } else {
                        currentPlayer = if (currentPlayer == "X") "O" else "X"
                        lastMoveMade = false // Дозволяємо зробити хід наступному гравцю
                    }
                }
            })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Використовуємо Column для вертикального вирівнювання кнопок
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp) // Відстань між кнопками
        ) {
            Button(onClick = {
                board = List(gridSize) { MutableList(gridSize) { "" } }
                winner = null
                timeRemaining = if (gridSize == 3) 5 else 10
                scoreUpdated = false
                lastMoveMade = false
            }) {
                Text("Скинути раунд")
            }

            Button(onClick = {
                gridSize = 3
                board = List(3) { MutableList(3) { "" } }
                currentPlayer = "X"
                winner = null
                playerXScore = 0
                playerOScore = 0
                timeRemaining = 5
                scoreUpdated = false
                lastMoveMade = false
            }) {
                Text("Нова гра")
            }

            Button(onClick = {
                showScore = !showScore // Перемикач для показу рахунку
            }) {
                Text("Показати рахунок")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Показуємо рахунок, якщо showScore == true
        if (showScore) {
            Text("Рахунок - X: $playerXScore, O: $playerOScore", fontSize = 20.sp)
        }
    }
}

// Функція для виконання ходу бота
fun makeBotMove(board: List<MutableList<String>>) {
    val emptyCells = mutableListOf<Pair<Int, Int>>()
    for (i in board.indices) {
        for (j in board[i].indices) {
            if (board[i][j].isEmpty()) {
                emptyCells.add(Pair(i, j))
            }
        }
    }

    if (emptyCells.isNotEmpty()) {
        val move = emptyCells[Random.nextInt(emptyCells.size)]
        board[move.first][move.second] = "O"
    }
}

@Composable
fun BoardGrid(
    board: List<List<String>>,
    gridSize: Int,
    onClick: (Int, Int) -> Unit
) {
    val cellSize = when (gridSize) {
        3 -> 80.dp // Для 3x3 клітинки залишаються великими
        4 -> 60.dp // Для 4x4 клітинки зменшуються
        5 -> 50.dp // Для 5x5 клітинки ще менші
        else -> 80.dp
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (i in 0 until gridSize) {
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                for (j in 0 until gridSize) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(cellSize)
                            .padding(4.dp)
                            .background(Color.LightGray)
                            .clickable { onClick(i, j) }
                    ) {
                        Text(text = board[i][j], fontSize = 32.sp)
                    }
                }
            }
        }
    }
}

// Функція для перевірки нічії
fun checkDraw(board: List<List<String>>): Boolean {
    return board.all { row -> row.all { it.isNotEmpty() } }
}

fun checkWinner(board: List<List<String>>, player: String): Boolean {
    val size = board.size

    for (i in 0 until size) {
        if ((0 until size).all { board[i][it] == player }) return true
        if ((0 until size).all { board[it][i] == player }) return true
    }

    if ((0 until size).all { board[it][it] == player }) return true
    if ((0 until size).all { board[it][size - 1 - it] == player }) return true

    return false
}
