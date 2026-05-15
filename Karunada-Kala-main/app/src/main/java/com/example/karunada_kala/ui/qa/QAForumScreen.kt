package com.example.karunada_kala.ui.qa

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.karunada_kala.domain.model.Question
import com.example.karunada_kala.ui.auth.AuthViewModel

private val KRed = Color(0xFFC1272D)
private val KYellow = Color(0xFFF9E106)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QAForumScreen(
    viewModel: QAForumViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val questions by viewModel.questions.collectAsState()
    val isPosting by viewModel.isPosting.collectAsState()
    val userRole by authViewModel.userRole.collectAsState()
    val isStudio = userRole == "STUDIO"

    var showAskDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = com.example.karunada_kala.R.string.qa_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KRed,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (!isStudio) {
                FloatingActionButton(
                    onClick = { showAskDialog = true },
                    containerColor = KRed,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Ask Question")
                }
            }
        }
    ) { paddingValues ->
        if (questions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No questions yet", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                    if (!isStudio) {
                        Spacer(Modifier.height(8.dp))
                        Text("Be the first to ask a Guru!", fontSize = 13.sp, color = Color.LightGray)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(questions, key = { it.id }) { question ->
                    QuestionCard(
                        question = question,
                        isStudio = isStudio,
                        onAnswer = { answer ->
                            viewModel.answerQuestion(question.id, answer)
                        }
                    )
                }
            }
        }

        if (isPosting) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = KRed)
            }
        }
    }

    // Ask Question Dialog (USER side)
    if (showAskDialog) {
        AskQuestionDialog(
            onDismiss = { showAskDialog = false },
            onSubmit = { text, artFormId ->
                viewModel.askQuestion(text, artFormId)
                showAskDialog = false
            }
        )
    }
}

@Composable
fun QuestionCard(
    question: Question,
    isStudio: Boolean,
    onAnswer: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showAnswerField by remember { mutableStateOf(false) }
    var answerText by remember { mutableStateOf("") }
    val hasAnswer = !question.answerText.isNullOrBlank()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Question text
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(KYellow, shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Q", fontWeight = FontWeight.Bold, color = KRed, fontSize = 14.sp)
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = question.questionText,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { expanded = !expanded }, modifier = Modifier.size(24.dp)) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle",
                        tint = Color.Gray
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded || hasAnswer,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.height(12.dp))

                    if (hasAnswer) {
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(KRed, shape = RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("A", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = question.answerText ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        Surface(
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                stringResource(id = com.example.karunada_kala.R.string.awaiting_answer),
                                modifier = Modifier.padding(10.dp),
                                fontSize = 13.sp,
                                fontStyle = FontStyle.Italic,
                                color = Color(0xFFE65100)
                            )
                        }
                    }

                    // Studio: answer box
                    if (isStudio && !hasAnswer) {
                        Spacer(Modifier.height(12.dp))
                        AnimatedVisibility(visible = showAnswerField) {
                            Column {
                                OutlinedTextField(
                                    value = answerText,
                                    onValueChange = { answerText = it },
                                    label = { Text(stringResource(id = com.example.karunada_kala.R.string.your_answer)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3,
                                    maxLines = 6,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        if (answerText.isNotBlank()) {
                                            onAnswer(answerText)
                                            answerText = ""
                                            showAnswerField = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = KRed),
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                    Spacer(Modifier.width(4.dp))
                                    Text(stringResource(id = com.example.karunada_kala.R.string.submit_answer))
                                }
                            }
                        }
                        if (!showAnswerField) {
                            OutlinedButton(
                                onClick = { showAnswerField = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = KRed),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("✍️ " + stringResource(id = com.example.karunada_kala.R.string.answer_this))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AskQuestionDialog(onDismiss: () -> Unit, onSubmit: (String, String) -> Unit) {
    var text by remember { mutableStateOf("") }
    var artFormId by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(stringResource(id = com.example.karunada_kala.R.string.ask_guru), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Your question") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = artFormId,
                    onValueChange = { artFormId = it },
                    label = { Text("Art Form (e.g. Yakshagana)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) { Text(stringResource(id = com.example.karunada_kala.R.string.cancel)) }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { if (text.isNotBlank()) onSubmit(text, artFormId) },
                        colors = ButtonDefaults.buttonColors(containerColor = KRed)
                    ) { Text(stringResource(id = com.example.karunada_kala.R.string.submit), color = Color.White) }
                }
            }
        }
    }
}
