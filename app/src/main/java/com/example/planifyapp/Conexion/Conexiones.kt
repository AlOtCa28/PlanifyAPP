package Conexion


import Modelo.TareasYLogros.Logro
import Modelo.TareasYLogros.TareaGeneral
import Modelo.Usuario.Usuario
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object Conexiones {

    @SuppressLint("StaticFieldLeak")
    val db = Firebase.firestore
    val storage = Firebase.storage
    var storageRef = storage.reference

    suspend fun registrarUsuario(
        mail: String,
        rol: ArrayList<Long>,
        edad: Long,
        foto: String,
        nombre: String,
        genero: Long
    ): Boolean = withContext(Dispatchers.IO) {

        val user = hashMapOf(
            "Nombre" to nombre,
            "Correo" to mail,
            "Roles" to rol,
            "Esta activo" to false,
            "Edad" to edad,
            "Genero" to genero,
            "Foto" to foto,
            "Es nuevo" to true
        )

        try {
            val document = db.collection("Usuarios").document(mail)
            document.set(user).await() // Espera a que la operación de escritura termine
            return@withContext true // Si la escritura es exitosa, devuelve true
        } catch (e: Exception) {
            Log.e("Firebase", "Error al registrar el usuario")
            return@withContext false
        }
    }


    suspend fun obtenerUsuario(mail: String): Usuario? {
        val firestore = FirebaseFirestore.getInstance()

        try {
            val document = firestore.collection("Usuarios")
                .document(mail)
                .get()
                .await()

            if (document.exists()) {
                val nombre = document.getString("Nombre") ?: ""
                val correo = document.getString("Correo") ?: ""
                val rolesFromFirestore = document.get("Roles")
                val roles = when (rolesFromFirestore) {
                    is ArrayList<*> -> {
                        @Suppress("UNCHECKED_CAST")
                        rolesFromFirestore as ArrayList<Long>
                    }
                    is Long -> {
                        arrayListOf(rolesFromFirestore)
                    }
                    else -> {
                        arrayListOf()
                    }
                }
                val isActivo = document.getBoolean("Esta activo") ?: false
                val edad = document.getLong("Edad") ?: 0L
                val genero = document.getLong("Genero") ?: 0L
                val foto = document.getString("Foto") ?: ""



                val isNuevo = document.getBoolean("Es nuevo") ?: false

                val usuario = Usuario(
                    nombre, correo, roles, isActivo, edad, genero, foto
                )

                Log.e("FirebaseLogin", "Usuario obtenido correctamente -> $usuario")
                return usuario
            } else {
                Log.d("Firebase", "El documento no existe en Firestore para el usuario con correo: $mail")
            }
        } catch (e: Exception) {
            Log.e("Firebase", "Error al obtener el usuario de Firestore: ${e.message}", e)
        }
        return null
    }


    suspend fun obtenerUsuarios(): QuerySnapshot? {

        return db.collection("Usuarios").get().await()

    }


    suspend fun activar_desactivarUsuario(correo: String, valor : Boolean) {
        val tareaRef = db.collection("Usuarios").document(correo)

        try {
            // Actualizar el campo "asignada" de la tarea a true
            tareaRef.update("Esta activo", valor).await()
            Log.d("Firebase", "Tarea marcada como asignada correctamente")
        } catch (e: Exception) {
            Log.e("Firebase", "Error al marcar tarea como asignada: $e")
            throw e
        }
    }

    suspend fun addRoleToUser(mail: String, newRole: Long) {
        val db = Firebase.firestore

        try {
            // Obtén la referencia del documento
            val userDocRef = db.collection("Usuarios").document(mail)

            // Usa arrayUnion para agregar el nuevo rol al array "Roles"
            userDocRef.update("Roles", com.google.firebase.firestore.FieldValue.arrayUnion(newRole)).await()

        } catch (e: Exception) {
            Log.e("FirebaseRol", e.message.toString())
        }
    }

    suspend fun removeRoleFromUser(mail: String, roleToRemove: Long) {
        val db = Firebase.firestore

        try {
            // Obtén la referencia del documento
            val userDocRef = db.collection("Usuarios").document(mail)

            // Usa arrayRemove para eliminar el rol del array "Roles"
            userDocRef.update("Roles", com.google.firebase.firestore.FieldValue.arrayRemove(roleToRemove)).await()

            println("Rol eliminado exitosamente.")
        } catch (e: Exception) {
            println("Error eliminando rol: ${e.message}")
        }
    }



    suspend fun subirImagenAlStorageSuspend(bitmap: Bitmap, nombreArchivo: String) {
        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.reference.child("imagenes/$nombreArchivo")

        // Convertir el Bitmap a un ByteArray
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        suspendCoroutine<Unit> { continuation ->
            val uploadTask = storageRef.putBytes(data)

            uploadTask.addOnSuccessListener {
                // La imagen se subió exitosamente
                continuation.resume(Unit)
            }.addOnFailureListener { exception ->
                // Ocurrió un error al subir la imagen
                continuation.resumeWithException(exception)
            }
        }
    }

    suspend fun downloadImageFromUri(uriAdjunto: Uri): Bitmap? {
        return try {
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uriAdjunto.toString())
            val bytes = storageRef.getBytes(Long.MAX_VALUE).await()
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun eliminarUsuarioPorNombre(nombreUsuario: String, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        // Consulta para buscar el documento con el nombre de usuario específico
        db.collection("Usuarios")
            .whereEqualTo("Nombre", nombreUsuario)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Verifica si se encontró algún documento
                if (!querySnapshot.isEmpty) {
                    // Solo se eliminará el primer documento encontrado (asumiendo que hay solo uno con ese nombre)
                    val documento = querySnapshot.documents[0]
                    documento.reference.delete()
                        .addOnSuccessListener {
                            Log.d("EliminarUsuario", "Documento con nombre $nombreUsuario eliminado correctamente")
                            callback(true)
                        }
                        .addOnFailureListener { e ->
                            Log.e("EliminarUsuario", "Error al eliminar documento con nombre $nombreUsuario: ${e.message}")
                            callback(false)
                        }
                } else {
                    Log.d("EliminarUsuario", "No se encontró ningún usuario con nombre $nombreUsuario")
                    callback(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("EliminarUsuario", "Error al buscar documento con nombre $nombreUsuario: ${e.message}")
                callback(false)
            }
    }

    fun obtenerLogrosGenerales(): Task<QuerySnapshot> {
        return FirebaseFirestore.getInstance()
            .collection("LogrosGenerales")
            .get()
    }


    fun eliminarLogroPorId(id: String): Task<Void> {
        return FirebaseFirestore.getInstance()
            .collection("LogrosGenerales")
            .document(id)
            .delete()
    }

    fun obtenerTareasGenerales() = FirebaseFirestore.getInstance()
        .collection("TareasGenerales")
        .get()


    fun eliminarTareaGeneral(id: String): Task<Void> {
        return FirebaseFirestore.getInstance()
            .collection("TareasGenerales")
            .document(id)
            .delete()
    }

}