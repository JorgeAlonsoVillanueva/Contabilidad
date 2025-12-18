package com.example.contabilidad

import androidx.compose.runtime.derivedStateOf
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
 * @property dueDate La fecha de vencimiento de la tarea en milisegundos.
 */
data class Task(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    var isCompleted: Boolean = false,
    var dueDate: Long? = null
)

/**
 * Enum para los diferentes estados de filtro.
 */
enum class TaskFilter { ALL, PENDING, COMPLETED }

/**
 * Gestiona el estado y la lógica de negocio de la pantalla de la lista de tareas.
 * Esta es la capa "ViewModel" en el patrón MVVM.
 */
class TaskViewModel : ViewModel() {

    /**
     * La lista maestra de todas las tareas. Es privada para la escritura, pero pública para la lectura.
     */
    var allTasks by mutableStateOf(
        listOf(
            Task(name = "Enviar un informe sobre gastos", isCompleted = true),
            Task(name = "Programar una reunión revisión del proyecto"),
            Task(name = "Crear diapositivas para revisión del proyecto"),
            Task(name = "Crear diapositivas para demostración"),
            Task(name = "Escribir el resumen ejecutivo demostración")
        )
    )
        private set

    /**
     * Una lista derivada que solo contiene las tareas pendientes.
     */
    val pendingTasks by derivedStateOf { allTasks.filter { !it.isCompleted } }

    /**
     * Una lista derivada que solo contiene las tareas completadas.
     */
    val completedTasks by derivedStateOf { allTasks.filter { it.isCompleted } }

    /**
     * El filtro actualmente seleccionado.
     */
    var filter by mutableStateOf(TaskFilter.PENDING)
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
     * Cambia el filtro actual.
     * @param newFilter El nuevo filtro a aplicar.
     */
    fun onFilterChange(newFilter: TaskFilter) {
        filter = newFilter
    }

    /**
     * Añade una nueva tarea a la lista.
     */
    fun addTask() {
        if (newTaskName.isNotBlank()) {
            allTasks = allTasks + Task(name = newTaskName)
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
        allTasks = allTasks.map {
            if (it.id == task.id) {
                it.copy(isCompleted = isChecked)
            } else {
                it
            }
        }
    }

    /**
     * Actualiza la fecha de vencimiento de una tarea.
     *
     * @param task La tarea que se va a actualizar.
     * @param dueDate La nueva fecha de vencimiento.
     */
    fun onDueDateChange(task: Task, dueDate: Long?) {
        allTasks = allTasks.map {
            if (it.id == task.id) {
                it.copy(dueDate = dueDate)
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
        allTasks = allTasks.filterNot { it.id == task.id }
    }
}