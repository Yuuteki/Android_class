package com.example.test01

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test01.ui.theme.Test01Theme
import kotlin.random.Random

private const val VALID_USERNAME = "admin"
private const val VALID_PASSWORD = "123456"
private const val EXTRA_USERNAME = "extra_username"
private const val EXTRA_PASSWORD = "extra_password"
private const val EXTRA_LEFT = "extra_left"
private const val EXTRA_RIGHT = "extra_right"
private const val EXTRA_OPERATOR = "extra_operator"
private const val EXTRA_RESULT = "extra_result"
private const val EXTRA_MESSAGE = "extra_message"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Test01Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        LoginAndCalculatorScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun LoginAndCalculatorScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var question by remember { mutableStateOf(generateQuestion()) }
    var answer by remember { mutableStateOf("?") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        if (result.resultCode == RESULT_OK) {
            answer = data?.getStringExtra(EXTRA_RESULT) ?: "?"
            Toast.makeText(context, "计算结束", Toast.LENGTH_SHORT).show()
        } else {
            val message = data?.getStringExtra(EXTRA_MESSAGE) ?: "用户名和密码错误"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF8E1), Color(0xFFFFE0B2))
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "LoginAndCalculater",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "随机题目",
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = question.format(answer),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("用户名") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("密码") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
                Text(
                    text = "示例账号：admin    示例密码：123456",
                    color = Color(0xFF6D4C41)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(context, CalculatorActivity::class.java)
                                .putExtra(EXTRA_USERNAME, username.trim())
                                .putExtra(EXTRA_PASSWORD, password)
                                .putExtra(EXTRA_LEFT, question.left)
                                .putExtra(EXTRA_RIGHT, question.right)
                                .putExtra(EXTRA_OPERATOR, question.operator.toString())
                            launcher.launch(intent)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("登录并计算")
                    }
                    Button(
                        onClick = {
                            question = generateQuestion()
                            answer = "?"
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D6E63))
                    ) {
                        Text("重新出题")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginAndCalculatorPreview() {
    Test01Theme {
        LoginAndCalculatorScreen()
    }
}

class CalculatorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val username = intent.getStringExtra(EXTRA_USERNAME).orEmpty()
        val password = intent.getStringExtra(EXTRA_PASSWORD).orEmpty()

        if (!isValidCredentials(username, password)) {
            setResult(
                RESULT_CANCELED,
                Intent().putExtra(EXTRA_MESSAGE, "用户名和密码错误")
            )
            finish()
            return
        }

        val left = intent.getIntExtra(EXTRA_LEFT, 0)
        val right = intent.getIntExtra(EXTRA_RIGHT, 0)
        val operator = intent.getStringExtra(EXTRA_OPERATOR)?.firstOrNull() ?: '+'
        val initialExpression = "$left$operator$right"

        setContent {
            Test01Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        CalculatorScreen(
                            questionText = "$left $operator $right = ?",
                            initialExpression = initialExpression,
                            onSubmit = { result ->
                                setResult(
                                    RESULT_OK,
                                    Intent().putExtra(EXTRA_RESULT, result)
                                )
                                finish()
                            },
                            onCancel = {
                                setResult(
                                    RESULT_CANCELED,
                                    Intent().putExtra(EXTRA_MESSAGE, "已取消计算")
                                )
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen(
    questionText: String,
    initialExpression: String,
    onSubmit: (String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var expression by remember { mutableStateOf(initialExpression) }
    var statusText by remember { mutableStateOf("点击 = 计算，Submit 提交结果。") }

    fun calculateAndUpdate() {
        val result = resolveExpression(expression)
        if (result == null) {
            Toast.makeText(context, "表达式格式错误", Toast.LENGTH_SHORT).show()
        } else {
            expression = result
            statusText = "当前结果：$result"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9))
                )
            )
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Calculator",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "目标题目：$questionText",
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedTextField(
                    value = expression,
                    onValueChange = { expression = it.filterCalculatorInput() },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("表达式 / 结果") },
                    singleLine = true
                )
                Text(
                    text = statusText,
                    color = Color(0xFF1B5E20)
                )
                KeypadRow(labels = listOf("7", "8", "9", "/")) { key ->
                    expression = expression.handleInput(key)
                }
                KeypadRow(labels = listOf("4", "5", "6", "*")) { key ->
                    expression = expression.handleInput(key)
                }
                KeypadRow(labels = listOf("1", "2", "3", "-")) { key ->
                    expression = expression.handleInput(key)
                }
                KeypadRow(labels = listOf("C", "0", "DEL", "+")) { key ->
                    expression = when (key) {
                        "C" -> ""
                        "DEL" -> expression.dropLast(1)
                        else -> expression.handleInput(key)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { calculateAndUpdate() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("=")
                    }
                    Button(
                        onClick = {
                            val result = resolveExpression(expression)
                            if (result == null) {
                                Toast.makeText(context, "请先得到有效结果", Toast.LENGTH_SHORT).show()
                            } else {
                                onSubmit(result)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                    ) {
                        Text("Submit")
                    }
                }
                Button(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D6E63))
                ) {
                    Text("返回上一级")
                }
            }
        }
    }
}

@Composable
fun KeypadRow(
    labels: List<String>,
    onKeyClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        labels.forEach { label ->
            Button(
                onClick = { onKeyClick(label) },
                modifier = Modifier
                    .weight(1f)
                    .sizeIn(minHeight = 56.dp)
            ) {
                Text(
                    text = label,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

data class MathQuestion(
    val left: Int,
    val right: Int,
    val operator: Char
) {
    fun format(answer: String): String = "$left $operator $right = $answer"
}

private fun generateQuestion(): MathQuestion {
    return when (Random.nextInt(4)) {
        0 -> MathQuestion(Random.nextInt(10, 100), Random.nextInt(10, 100), '+')
        1 -> {
            val right = Random.nextInt(10, 50)
            val left = Random.nextInt(right, right + 50)
            MathQuestion(left, right, '-')
        }
        2 -> MathQuestion(Random.nextInt(2, 20), Random.nextInt(2, 20), '*')
        else -> {
            val right = Random.nextInt(2, 10)
            val quotient = Random.nextInt(2, 15)
            MathQuestion(right * quotient, right, '/')
        }
    }
}

private fun isValidCredentials(username: String, password: String): Boolean {
    return username == VALID_USERNAME && password == VALID_PASSWORD
}

private fun String.filterCalculatorInput(): String {
    return filter { it.isDigit() || it == '+' || it == '-' || it == '*' || it == '/' }
}

private fun String.handleInput(key: String): String {
    return (this + key).filterCalculatorInput()
}

private fun resolveExpression(expression: String): String? {
    val plainNumber = expression.toLongOrNull()
    if (plainNumber != null) {
        return plainNumber.toString()
    }

    val match = Regex("""^\s*(-?\d+)\s*([+\-*/])\s*(-?\d+)\s*$""").matchEntire(expression)
        ?: return null

    val left = match.groupValues[1].toLong()
    val operator = match.groupValues[2].first()
    val right = match.groupValues[3].toLong()

    val result = when (operator) {
        '+' -> left + right
        '-' -> left - right
        '*' -> left * right
        '/' -> if (right == 0L) return null else left / right
        else -> return null
    }
    return result.toString()
}
