package Conexion


import Modelo.Usuario.Usuario
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
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
                    nombre, correo, roles, isActivo, edad, genero, foto, isNuevo
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



    suspend fun obtenerUsuarioPorNombre(nombreUser: String): Usuario? {
        val firestore = FirebaseFirestore.getInstance()

        try {
            val querySnapshot = firestore.collection("Usuarios")
                .whereEqualTo("Nombre", nombreUser)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents[0]

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
                        nombre, correo, roles, isActivo, edad, genero, foto, isNuevo
                    )

                    Log.e("FirebaseLogin", "Usuario obtenido correctamente -> $usuario")
                    return usuario
                } else {
                    Log.d("Firebase", "El documento no existe en Firestore")
                }
            } else {
                Log.d("Firebase", "No se encontró ningún usuario con el nombre especificado")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }


    suspend fun obtenerUsuarios(): QuerySnapshot? {

        return db.collection("Usuarios").get().await()

    }

    suspend fun actualizarPreferencias(correoUsuario: String, nuevasPreferencias: ArrayList<String>) {
        val db = FirebaseFirestore.getInstance()
        val usuarioRef = db.collection("Usuarios").document(correoUsuario)

        try {
            // Convertir el ArrayList de Preferencia a un formato que Firestore pueda almacenar


            // Actualizar el documento con las nuevas preferencias
            usuarioRef.update("Preferencias", nuevasPreferencias).await()
            println("Preferencias actualizadas correctamente.")
        } catch (e: Exception) {
            println("Error al actualizar las preferencias: ${e.message}")
        }
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

    suspend fun dejarDeSerNuevo(correo: String) {
        val tareaRef = db.collection("Usuarios").document(correo)

        try {
            // Actualizar el campo "asignada" de la tarea a true
            tareaRef.update("Es nuevo", false).await()
            Log.d("Firebase", "El usuario ya no es nuevo")
        } catch (e: Exception) {
            Log.e("Firebase", "Error al dejar de ser nuevo: $e")
            throw e
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

    suspend fun enviarSolicitudDeAmistad(correoUsuarioObjetivo: String, nombreUsuarioSolicitante: String): Boolean {
        return suspendCoroutine { continuation ->
            val firestore = FirebaseFirestore.getInstance()
            val usuarioRef = firestore.collection("Usuarios").document(correoUsuarioObjetivo)

            usuarioRef.update("Solicitudes De Amistad", FieldValue.arrayUnion(nombreUsuarioSolicitante))
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener { e ->
                    Log.w("SolicitudAmistad", "Error al enviar la solicitud de amistad", e)
                    continuation.resume(false)
                }
        }
    }

    fun verificarSolicitudDeAmistad(userIdDestino: String, nombreUsuarioEnviaSolicitud: String, callback: (Boolean) -> Unit) {
        // Validación básica
        if (userIdDestino.isEmpty() || nombreUsuarioEnviaSolicitud.isEmpty()) {
            Log.e("Solicitudes", "Error: El ID del usuario destino y el nombre del usuario remitente no pueden estar vacíos")
            callback(false)
            return
        }

        val db = FirebaseFirestore.getInstance()

        db.collection("Usuarios")
            .document(userIdDestino)
            .get()
            .addOnSuccessListener { document ->

                Log.e("Solicitudes", "Verificando solicitud de amistad")

                if (document.exists()) {
                    val solicitudesDeAmistad = document.get("Solicitudes De Amistad") as? List<String>
                    if (solicitudesDeAmistad != null && solicitudesDeAmistad.contains(nombreUsuarioEnviaSolicitud)) {
                        callback(true)
                    } else {
                        callback(false)
                    }
                } else {
                    Log.e("Solicitudes", "El documento del usuario destino no existe")
                    callback(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Solicitudes", "Error al verificar la solicitud de amistad: ${e.message}")
                callback(false)
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

    suspend fun obtenerSolicitudesDeAmistad(correoUsuario: String): ArrayList<String> {
        val firestore = FirebaseFirestore.getInstance()

        return try {
            val documento = firestore.collection("Usuarios").document(correoUsuario).get().await()
            if (documento.exists()) {

                Log.e("Solicitudes", "Aqui si entra")

                // Obtener el campo 'solicitudesDeAmistad' como una lista de cadenas
                val solicitudes = documento.get("Solicitudes De Amistad") as? ArrayList<String> ?: arrayListOf()

                Log.e("Solicitudes", solicitudes.toString())

                solicitudes



            } else {
                // Si el documento no existe, devolvemos una lista vacía

                Log.e("Solicitudes", "Aqui no deberia entrar")

                arrayListOf()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // En caso de error, devolvemos una lista vacía
            arrayListOf()
        }
    }

    suspend fun agregarAmigoAUsuarios(correoUsuario1: String, nombreUsuario1: String, nombreUsuario2: String) {
        try {
            val db = FirebaseFirestore.getInstance()
            val usuariosRef = db.collection("Usuarios")

            // Buscar el usuario 1 por correo y actualizar su campo Amigos
            val queryUsuario1 = usuariosRef.whereEqualTo("Correo", correoUsuario1).limit(1).get().await()
            if (!queryUsuario1.isEmpty) {
                val usuario1Doc = queryUsuario1.documents.first()
                val amigosUsuario1 = usuario1Doc.get("Amigos") as? ArrayList<String> ?: arrayListOf()
                if (!amigosUsuario1.contains(nombreUsuario2)) {
                    amigosUsuario1.add(nombreUsuario2)
                    usuario1Doc.reference.update("Amigos", amigosUsuario1).await()
                    println("Nombre de usuario $nombreUsuario2 agregado a Amigos de $nombreUsuario1")
                } else {
                    println("$nombreUsuario2 ya es amigo de $nombreUsuario1")
                }
            } else {
                println("Usuario 1 no encontrado con correo $correoUsuario1")
            }

            // Buscar el usuario 2 por nombre y actualizar su campo Amigos
            val queryUsuario2 = usuariosRef.whereEqualTo("Nombre", nombreUsuario2).limit(1).get().await()
            if (!queryUsuario2.isEmpty) {
                val usuario2Doc = queryUsuario2.documents.first()
                val amigosUsuario2 = usuario2Doc.get("Amigos") as? ArrayList<String> ?: arrayListOf()
                if (!amigosUsuario2.contains(nombreUsuario1)) {
                    amigosUsuario2.add(nombreUsuario1)
                    usuario2Doc.reference.update("Amigos", amigosUsuario2).await()
                    println("Nombre de usuario $nombreUsuario1 agregado a Amigos de $nombreUsuario2")
                } else {
                    println("$nombreUsuario1 ya es amigo de $nombreUsuario2")
                }
            } else {
                println("Usuario 2 no encontrado con nombre $nombreUsuario2")
            }

        } catch (e: Exception) {
            println("Error al agregar amigo a usuarios: ${e.message}")
            // Manejo de errores según sea necesario
        }
    }

    suspend fun eliminarSolicitud(correoUsuario: String, nombreSolicitud: String) {
        try {
            val db = FirebaseFirestore.getInstance()
            val usuariosRef = db.collection("Usuarios")

            // Consultar el documento del usuario por su correo
            val query = usuariosRef.whereEqualTo("Correo", correoUsuario).limit(1).get().await()

            if (!query.isEmpty) {
                val usuarioDoc = query.documents.first()
                val solicitudes = usuarioDoc.get("Solicitudes De Amistad") as? ArrayList<String> ?: arrayListOf()

                // Eliminar el nombre de la solicitud del array de solicitudes
                if (solicitudes.contains(nombreSolicitud)) {
                    solicitudes.remove(nombreSolicitud)
                    usuarioDoc.reference.update("Solicitudes De Amistad", solicitudes).await()
                    println("Solicitud de amistad de $nombreSolicitud Eliminada correctamente para $correoUsuario")
                } else {
                    println("$nombreSolicitud no está en las solicitudes de $correoUsuario")
                }
            } else {
                println("Usuario con correo $correoUsuario no encontrado")
            }

        } catch (e: Exception) {
            println("Error al eliminar solicitud: ${e.message}")
            // Manejo de errores según sea necesario
        }
    }

    suspend fun obtenerAmigos(correoUsuario: String): ArrayList<String> {
        val db = FirebaseFirestore.getInstance()
        val usuariosRef = db.collection("Usuarios")

        return try {
            val query = usuariosRef.whereEqualTo("Correo", correoUsuario).limit(1).get().await()
            if (!query.isEmpty) {
                val usuarioDoc = query.documents.first()
                val amigos = usuarioDoc.get("Amigos") as? ArrayList<String> ?: arrayListOf()
                amigos
            } else {
                println("Usuario con correo $correoUsuario no encontrado")
                arrayListOf()
            }
        } catch (e: Exception) {
            println("Error al obtener amigos: ${e.message}")
            arrayListOf()
        }
    }

    suspend fun eliminarAmigo(correoUsuario1: String, nombreUsuario2: String, nombreUsuario1: String) {
        val db = FirebaseFirestore.getInstance()

        try {
            // Iniciar la transacción
            db.runTransaction { transaction ->
                // Referencia al documento del primer usuario
                val usuario1Ref = db.collection("Usuarios").document(correoUsuario1)
                val usuario1Snapshot = transaction.get(usuario1Ref)
                val amigosUsuario1 = usuario1Snapshot.get("Amigos") as? MutableList<String>

                // Eliminar el nombre del segundo usuario del array de amigos del primer usuario
                if (amigosUsuario1 != null && amigosUsuario1.contains(nombreUsuario2)) {
                    amigosUsuario1.remove(nombreUsuario2)
                    transaction.update(usuario1Ref, "Amigos", amigosUsuario1)
                }

                // Buscar el documento del segundo usuario por su nombre
                val usuario2Query = db.collection("Usuarios").whereEqualTo("Nombre", nombreUsuario2).get()
                val usuario2Snapshot = Tasks.await(usuario2Query).documents.firstOrNull()

                if (usuario2Snapshot != null) {
                    val correoUsuario2 = usuario2Snapshot.id
                    val usuario2Ref = db.collection("Usuarios").document(correoUsuario2)
                    val amigosUsuario2 = usuario2Snapshot.get("Amigos") as? MutableList<String>

                    // Eliminar el nombre del primer usuario del array de amigos del segundo usuario
                    if (amigosUsuario2 != null && amigosUsuario2.contains(nombreUsuario1)) {
                        amigosUsuario2.remove(nombreUsuario1)
                        transaction.update(usuario2Ref, "Amigos", amigosUsuario2)
                    }
                }
            }.await()

            println("Amigos eliminados exitosamente")
        } catch (e: Exception) {
            println("Error eliminando amigos: ${e.message}")
        }
    }

    suspend fun enviarLikeMatch(nombreUserMeGusta: String, correoUser: String): Boolean {
        return suspendCoroutine { continuation ->
            val firestore = FirebaseFirestore.getInstance()
            val usuarioRef = firestore.collection("Usuarios").document(correoUser)

            usuarioRef.update("Me gustas", FieldValue.arrayUnion(nombreUserMeGusta))
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener { e ->
                    Log.w("Me gustas", "Error al añadir el like", e)
                    continuation.resume(false)
                }
        }
    }

    suspend fun obtenerMeGustas(correo: String): List<String> {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("Usuarios").document(correo)

        return try {
            val snapshot = documentRef.get().await()
            if (snapshot.exists()) {
                val meGustas = snapshot.get("Me gustas") as? List<String> ?: emptyList()

                meGustas
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            // Manejar errores aquí según sea necesario
            throw e
        }
    }


    suspend fun eliminarMeGusta(correo: String, nombreUsuario: String): Boolean {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("Usuarios").document(correo)

        return try {
            val snapshot = documentRef.get().await()
            if (snapshot.exists()) {
                val meGustas = snapshot.get("Me gustas") as? MutableList<String> ?: mutableListOf()
                if (meGustas.contains(nombreUsuario)) {
                    meGustas.remove(nombreUsuario)
                    documentRef.update("Me gustas", meGustas).await()
                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            // Manejar errores aquí según sea necesario
            throw e
        }
    }

    fun buscarNombreUsuarioEnAmigos(correoDocumento: String, nombreUsuario: String, callback: (Boolean) -> Unit) {
        // Validación básica
        if (correoDocumento.isEmpty() || nombreUsuario.isEmpty()) {
            Log.e("BuscarUsuarioEnAmigos", "Error: El correo del documento y el nombre de usuario no pueden estar vacíos")
            callback(false)
            return
        }

        val db = FirebaseFirestore.getInstance()

        db.collection("Usuarios")
            .document(correoDocumento)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val amigos = documentSnapshot.get("Amigos") as? List<String>
                    if (amigos != null && amigos.contains(nombreUsuario)) {
                        callback(true)
                    } else {
                        callback(false)
                    }
                } else {
                    Log.e("BuscarUsuarioEnAmigos", "El documento con correo $correoDocumento no existe")
                    callback(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("BuscarUsuarioEnAmigos", "Error al buscar usuario en amigos: ${e.message}")
                callback(false)
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


    suspend fun obtenerEventos(): QuerySnapshot? {

        return db.collection("Eventos").get().await()

    }

    fun agregarParticipante(eventoId: String, correo: String, callback: (Boolean) -> Unit) {
        val eventoRef = db.collection("Eventos").document(eventoId)

        // Obtener el documento del evento
        eventoRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Agregar el correo al campo "Participantes"
                eventoRef.update("Participantes", FieldValue.arrayUnion(correo))
                    .addOnSuccessListener {
                        callback(true)
                    }
                    .addOnFailureListener { e ->
                        callback(false)
                        Log.e("Firebase", "Error al agregar participante: ${e.message}")
                    }
            } else {
                Log.e("Firebase", "El evento no existe")
                callback(false)
            }
        }.addOnFailureListener { e ->
            callback(false)
            Log.e("Firebase", "Error al obtener el evento: ${e.message}")
        }
    }


    fun eliminarParticipante(eventoId: String, correo: String, callback: (Boolean) -> Unit){
        val eventoRef = db.collection("Eventos").document(eventoId)

        // Obtener el documento del evento
        eventoRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Eliminar el correo del campo "Participantes"
                eventoRef.update("Participantes", FieldValue.arrayRemove(correo))
                    .addOnSuccessListener {
                        callback(true)
                    }
                    .addOnFailureListener { e ->
                        callback(false)
                        Log.e("Firebase", "Error al eliminar participante: ${e.message}")
                    }
            } else {
                Log.e("Firebase", "El evento no existe")
                callback(false)
            }
        }.addOnFailureListener { e ->
            callback(false)
            Log.e("Firebase", "Error al obtener el evento: ${e.message}")
        }
    }
}