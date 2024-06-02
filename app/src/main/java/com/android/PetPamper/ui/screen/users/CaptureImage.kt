package com.android.PetPamper.ui.screen.users

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.viewbinding.BuildConfig
import coil.annotation.ExperimentalCoilApi
import java.util.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import android.Manifest
import androidx.core.net.toUri
import com.android.PetPamper.database.FirebaseConnection
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat


@OptIn(ExperimentalCoilApi::class)
@Composable
fun CaptureCamera(email: String) {

    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        "com.android.PetPamper" + ".provider", file
    )

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            capturedImageUri = uri
        }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            val permissionCheckResult =
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                cameraLauncher.launch(uri)
            } else {
                // Request a permission
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }) {
            Text(text = "Capture Image From Camera")
        }
    }

    if (capturedImageUri.path?.isNotEmpty() == true) {
        val firebaseConnection = FirebaseConnection()
        var uri = capturedImageUri
        var imageUri = capturedImageUri.path
        if (uri != null) {

            // Get a reference to the storage service
            val storageRef = FirebaseStorage.getInstance().reference

            val fileRef = storageRef.child("images/${uri.lastPathSegment}")
            val uploadTask = fileRef.putFile(uri)

            uploadTask
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri
                        ->
                        imageUri = downloadUri.toString() // Store download URI instead of local URI
                        //viewModel.profilePicture = downloadUri.toString()
                        firebaseConnection.changeUserImage(email, downloadUri.toString())
                    }
                }
                .addOnFailureListener {
                    // Handle unsuccessful uploads
                    Toast.makeText(context, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            imageUri = null
            //viewModel.profilePicture = imageUri.toString()
        }
        Image(
            modifier = Modifier
                .padding(16.dp, 8.dp),
            painter = rememberImagePainter(capturedImageUri),
            contentDescription = null
        )
    }


}

fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    return image
}