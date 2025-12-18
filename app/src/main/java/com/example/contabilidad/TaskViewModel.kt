package com.example.contabilidad

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.UUID

/**
 * Define la estructura de datos para una Tarea. Esta es la capa "MODELO" en la arquitectura MVVM.
 * Es una clase de datos simple que contiene las propiedades de una tarea.
 *
 * @property id El identificador único de la tarea, generado automáticamente.
 * @property name El nombre o descripción de la tarea.
 * @property isCompleted Un booleano que indica si la tarea ha sido completada.
 * @property dueDate Una marca de tiempo opcional (en milisegundos) para la fecha de vencimiento.
 */
data class Task(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    var isCompleted: Boolean = false,
    var dueDate: Long? = null
)

/**
 * Enum para representar los diferentes estados de filtro de la lista de tareas.
 */
enum class TaskFilter { ALL, PENDING, COMPLETED }

/**
 * Gestiona el estado de la interfaz de usuario y la lógica de negocio. Esta es la capa "VIEWMODEL" en la arquitectura MVVM.
 * Expone los datos a la "Vista" y maneja las acciones del usuario (añadir, borrar, etc.) sin tener conocimiento directo de la interfaz de usuario.
 */
class TaskViewModel : ViewModel() {

    /**
     * La lista maestra de todas las tareas. Es privada para evitar modificaciones directas desde la Vista.
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

    // Almacena temporalmente una tarea eliminada para la función "Deshacer".
    private var recentlyDeletedTask: Task? = null

    /**
     * Una lista derivada y reactiva que solo contiene las tareas pendientes.
     * Se recalcula automáticamente cuando `allTasks` cambia.
     */
    val pendingTasks by derivedStateOf { allTasks.filter { !it.isCompleted } }

    /**
     * Una lista derivada y reactiva que solo contiene las tareas completadas.
     * Se recalcula automáticamente cuando `allTasks` cambia.
     */
    val completedTasks by derivedStateOf { allTasks.filter { it.isCompleted } }

    /**
     * El nombre de la nueva tarea que el usuario está escribiendo en el campo de texto.
     */
    var newTaskName by mutableStateOf("")

    /**
     * Actualiza el valor de `newTaskName`. Es llamado por la Vista cada vez que el usuario escribe.
     */
    fun onNewTaskNameChange(name: String) {
        newTaskName = name
    }

    /**
     * Añade una nueva tarea a la lista `allTasks` si el nombre no está vacío.
     */
    fun addTask() {
        if (newTaskName.isNotBlank()) {
            allTasks = allTasks + Task(name = newTaskName)
            newTaskName = "" // Limpia el campo de texto después de añadir
        }
    }

    /**
     * Cambia el estado de `isCompleted` de una tarea específica.
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
     * Actualiza la fecha de vencimiento `dueDate` de una tarea específica.
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
     * Elimina una tarea de la lista principal y la guarda temporalmente por si el usuario quiere deshacer la acción.
     */
    fun deleteTask(task: Task) {
        recentlyDeletedTask = task
        allTasks = allTasks.filterNot { it.id == task.id }
    }

    /**
     * Restaura la tarea eliminada recientemente a la lista principal.
     */
    fun undoDelete() {
        recentlyDeletedTask?.let { task ->
            allTasks = allTasks + task
            recentlyDeletedTask = null
        }
    }
}