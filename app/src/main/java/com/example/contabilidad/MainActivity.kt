package com.example.contabilidad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.contabilidad.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ContabilidadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = LightGrayBackground
                ) {
                    TaskListScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TaskListScreen(taskViewModel: TaskViewModel = viewModel()) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showError by remember { mutableStateOf(false) }

    // State for the dialogs
    var showDatePicker by remember { mutableStateOf(false) }
    var taskToSetDate: Task? by remember { mutableStateOf(null) }
    var showUncheckDialog by remember { mutableStateOf(false) }
    var taskToUncheck: Task? by remember { mutableStateOf(null) }

    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })

    val addTaskAction = {
        if (taskViewModel.newTaskName.isNotBlank()) {
            taskViewModel.addTask()
            scope.launch {
                snackbarHostState.showSnackbar("Tarea añadida correctamente")
            }
            keyboardController?.hide()
            showError = false
        } else {
            showError = true
        }
    }

    // Date Picker Dialog
    if (showDatePicker && taskToSetDate != null) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    taskViewModel.onDueDateChange(taskToSetDate!!, datePickerState.selectedDateMillis)
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Uncheck Task Dialog
    if (showUncheckDialog && taskToUncheck != null) {
        AlertDialog(
            onDismissRequest = { showUncheckDialog = false },
            title = { Text("Tarea Desmarcada") },
            text = { Text("¿Deseas mover la tarea a 'Pendientes' o borrarla permanentemente?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskViewModel.onTaskCheckedChanged(taskToUncheck!!, false)
                        showUncheckDialog = false
                    }
                ) {
                    Text("MOVER A PENDIENTES")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        taskViewModel.deleteTask(taskToUncheck!!)
                        showUncheckDialog = false
                    }
                ) {
                    Text("BORRAR", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de tareas", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Blue)
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = CompletedGreen,
                    contentColor = Color.White
                )
            }
        },
        containerColor = LightGrayBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Input Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = taskViewModel.newTaskName,
                    onValueChange = {
                        taskViewModel.onNewTaskNameChange(it)
                        if (showError) showError = false
                    },
                    label = { Text("Añadir una tarea...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Blue,
                        unfocusedBorderColor = Color.Gray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Blue
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { addTaskAction() }),
                    singleLine = true,
                    isError = showError
                )
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = { addTaskAction() },
                    containerColor = Blue,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            }

            if (showError) {
                Text(
                    text = "La tarea no puede estar vacía",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Tabs
            TaskFilterTabs(
                selectedTabIndex = pagerState.currentPage,
                onTabSelected = {
                    scope.launch { pagerState.animateScrollToPage(it) }
                }
            )

            // Pager with Task Lists
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize().padding(top = 16.dp)
            ) { page ->
                val tasks = when (page) {
                    0 -> taskViewModel.allTasks
                    1 -> taskViewModel.pendingTasks
                    2 -> taskViewModel.completedTasks
                    else -> emptyList()
                }
                TaskList(
                    tasks = tasks,
                    onTaskCheckedChanged = { task, isChecked ->
                        if (!isChecked && task.isCompleted) {
                            taskToUncheck = task
                            showUncheckDialog = true
                        } else {
                            taskViewModel.onTaskCheckedChanged(task, isChecked)
                        }
                    },
                    onDeleteTask = { taskViewModel.deleteTask(it) },
                    onDateClick = {
                        taskToSetDate = it
                        showDatePicker = true
                    }
                )
            }
        }
    }
}

@Composable
fun TaskFilterTabs(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    val filters = listOf("Todas", "Pendientes", "Completadas")

    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = LightGrayBackground,
        contentColor = Blue,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = Blue
            )
        }
    ) {
        filters.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = { Text(title) },
                selectedContentColor = Blue,
                unselectedContentColor = Color.Gray
            )
        }
    }
}

@Composable
fun TaskList(
    tasks: List<Task>,
    onTaskCheckedChanged: (Task, Boolean) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onDateClick: (Task) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(tasks, key = { it.id }) { task ->
            TaskItem(
                task = task,
                onTaskCheckedChanged = onTaskCheckedChanged,
                onDeleteTask = onDeleteTask,
                onDateClick = onDateClick
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onTaskCheckedChanged: (Task, Boolean) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onDateClick: (Task) -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TaskItemGray)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomCheckbox(checked = task.isCompleted, onCheckedChange = { onTaskCheckedChanged(task, it) })

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.name,
                color = Color.White,
                textAlign = TextAlign.Start,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
            )
            if (task.dueDate != null) {
                Text(
                    text = dateFormatter.format(Date(task.dueDate!!)),
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        IconButton(onClick = { onDateClick(task) }) {
            Icon(Icons.Default.DateRange, contentDescription = "Set Date", tint = Color.White)
        }

        IconButton(onClick = { onDeleteTask(task) }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Task", tint = Color.White)
        }
    }
}

@Composable
fun CustomCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(if (checked) CompletedGreen else Color.Transparent)
            .border(2.dp, if (checked) CompletedGreen else Color.White, CircleShape)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(Icons.Default.Check, contentDescription = "Checked", tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ContabilidadTheme {
        TaskListScreen()
    }
}
