package com.example.contabilidad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.contabilidad.ui.theme.Blue
import com.example.contabilidad.ui.theme.ContabilidadTheme
import com.example.contabilidad.ui.theme.LightBlue

/**
 * MainActivity es la actividad principal de la aplicación.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ContabilidadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskListScreen()
                }
            }
        }
    }
}

/**
 * La pantalla principal que muestra la lista de tareas, el campo de entrada y el botón para añadir nuevas tareas.
 * Esta es la "Vista" en el patrón MVVM.
 *
 * @param taskViewModel El ViewModel que gestiona el estado de la lista de tareas.
 */
@Composable
fun TaskListScreen(taskViewModel: TaskViewModel = viewModel()) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blue)
            .padding(16.dp)
    ) {
        Text(
            text = "Lista de tareas",
            color = Color.White,
            fontSize = 36.sp,
            modifier = Modifier.padding(bottom = 60.dp, start = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = taskViewModel.newTaskName,
                onValueChange = {
                    taskViewModel.onNewTaskNameChange(it)
                    showError = false
                },
                label = { Text("Añadir una tarea") },
                modifier = Modifier.weight(0.75f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LightBlue,
                    unfocusedBorderColor = LightBlue,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (taskViewModel.newTaskName.isNotBlank()) {
                        taskViewModel.addTask()
                        keyboardController?.hide()
                    } else {
                        showError = true
                    }
                }),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(20.dp))

            Button(
                onClick = {
                    if (taskViewModel.newTaskName.isNotBlank()) {
                        taskViewModel.addTask()
                        keyboardController?.hide()
                    } else {
                        showError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Blue)
            ) {
                Text(text = "Añadir")
            }
        }
        if (showError) {
            Text(
                "El nombre de la tarea no puede estar vacío",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(taskViewModel.tasks) { task ->
                TaskItem(
                    task = task,
                    onTaskCheckedChanged = { task, isChecked ->
                        taskViewModel.onTaskCheckedChanged(task, isChecked)
                    },
                    onDeleteTask = { taskToDelete ->
                        taskViewModel.deleteTask(taskToDelete)
                    }
                )
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

/**
 * Un elemento individual de la lista de tareas.
 *
 * @param task El objeto de la tarea a mostrar.
 * @param onTaskCheckedChanged Lambda que se invoca cuando se marca o desmarca la casilla de verificación.
 * @param onDeleteTask Lambda que se invoca cuando se pulsa el botón de eliminar.
 */
@Composable
fun TaskItem(
    task: Task,
    onTaskCheckedChanged: (Task, Boolean) -> Unit,
    onDeleteTask: (Task) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { isChecked ->
                onTaskCheckedChanged(task, isChecked)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = LightBlue,
                uncheckedColor = Color.White
            )
        )
        Text(
            text = task.name,
            color = Color.White,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { onDeleteTask(task) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Task",
                tint = Color.White
            )
        }
    }
}

/**
 * Una vista previa de la pantalla de la lista de tareas.
 */
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ContabilidadTheme {
        TaskListScreen()
    }
}
