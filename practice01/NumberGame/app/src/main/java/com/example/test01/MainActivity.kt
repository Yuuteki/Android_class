package com.example.test01

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test01.ui.theme.Test01Theme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Test01Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        NumberGameScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun NumberGameScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var leftNumber by remember { mutableIntStateOf(0) }
    var rightNumber by remember { mutableIntStateOf(0) }
    var points by remember { mutableIntStateOf(0) }

    fun nextRound() {
        leftNumber = Random.nextInt(100)
        do {
            rightNumber = Random.nextInt(100)
        } while (rightNumber == leftNumber)
    }

    fun submitAnswer(selectedNumber: Int) {
        val correctNumber = maxOf(leftNumber, rightNumber)
        val isCorrect = selectedNumber == correctNumber
        points += if (isCorrect) 1 else -1
        Toast.makeText(
            context,
            if (isCorrect) "Correct" else "Wrong",
            Toast.LENGTH_SHORT
        ).show()
        nextRound()
    }

    LaunchedEffect(Unit) {
        nextRound()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFF5F7FF), Color(0xFFE3F2FD))
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Number Game",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "点击较大的数字，答对加 1 分，答错减 1 分。",
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Points: $points",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0D47A1)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    NumberButton(
                        value = leftNumber,
                        buttonColor = Color(0xFF1976D2),
                        modifier = Modifier.weight(1f)
                    ) {
                        submitAnswer(leftNumber)
                    }
                    NumberButton(
                        value = rightNumber,
                        buttonColor = Color(0xFF00897B),
                        modifier = Modifier.weight(1f)
                    ) {
                        submitAnswer(rightNumber)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NumberGamePreview() {
    Test01Theme {
        NumberGameScreen()
    }
}

@Composable
fun NumberButton(
    value: Int,
    buttonColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.sizeIn(minHeight = 140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
    ) {
        Text(
            text = value.toString(),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
