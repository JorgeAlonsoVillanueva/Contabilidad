package com.example.contabilidad

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.UUID

/**
 * Define la estructura de una tarea. 
 * Esta es la capa "Modelo" en el patrón MVVM.
 *
 * @property id El identificador único de la tarea.
 * @property name El nombre o descripción de la tarea.
 * @property isCompleted Indica si la tarea se ha completado.
 */
data class Task(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    var isCompleted: Boolean = false
)

/**
 * Gestiona el estado y la lógica de negocio de la pantalla de la lista de tareas.
 * Esta es la capa "ViewModel" en el patrón MVVM.
 */
class TaskViewModel : ViewModel() {

    /**
     * La lista de tareas que se muestra en la interfaz de usuario.
     */
    var tasks by mutableStateOf(
        listOf(
            Task(name = "Enviar un informe sobre gastos"),
            Task(name = "Programar una reunión de revisión del proyecto"),
            Task(name = "Crear diapositivas para demostración"),
            Task(name = "Revisar el plan del proyecto"),
            Task(name = "Escribir el resumen ejecutivo")
        )
    )
        private set

    /**
     * El nombre de la nueva tarea que se está introduciendo.
     */
    var newTaskName by mutableStateOf("")

    /**
     * Actualiza el nombre de la nueva tarea.
     *
     * @param name El nuevo nombre de la tarea.
     */
    fun onNewTaskNameChange(name: String) {
        newTaskName = name
    }

    /**
     * Añade una nueva tarea a la lista.
     */
    fun addTask() {
        if (newTaskName.isNotBlank()) {
            tasks = tasks + Task(name = newTaskName)
            newTaskName = ""
        }
    }

    /**
     * Actualiza el estado de finalización de una tarea.
     *
     * @param task La tarea que se va a actualizar.
     * @param isChecked El nuevo estado de finalización.
     */
    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) {
        tasks = tasks.map {
            if (it.id == task.id) {
                it.copy(isCompleted = isChecked)
            } else {
                it
            }
        }
    }

    /**
     * Elimina una tarea de la lista.
     *
     * @param task La tarea que se va a eliminar.
     */
    fun deleteTask(task: Task) {
        tasks = tasks.filterNot { it.id == task.id }
    }
}