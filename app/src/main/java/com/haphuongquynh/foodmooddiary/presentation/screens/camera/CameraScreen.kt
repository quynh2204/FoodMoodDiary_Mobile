package com.haphuongquynh.foodmooddiary.presentation.screens.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import android.media.ExifInterface
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import androidx.compose.material.icons.filled.PhotoLibrary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onPhotoCaptured: (File, Bitmap) -> Unit,
    onDismiss: () -> Unit,
    onGalleryClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    // Default to front camera, will fallback to back if not available
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_FRONT) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    val previewView = remember { PreviewView(context) }
    var cameraReady by remember { mutableStateOf(false) }

    LaunchedEffect(lensFacing) {
        try {
            val cameraProvider = context.getCameraProvider()
            cameraProvider.unbindAll()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()

            // Check if the requested camera is available
            val hasRequestedCamera = when (lensFacing) {
                CameraSelector.LENS_FACING_FRONT -> cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
                CameraSelector.LENS_FACING_BACK -> cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
                else -> false
            }

            // If front camera not available, fallback to back camera
            val actualLensFacing = if (hasRequestedCamera) {
                lensFacing
            } else {
                android.util.Log.w("CameraScreen", "Requested camera not available, trying alternative")
                if (lensFacing == CameraSelector.LENS_FACING_FRONT && 
                    cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                    CameraSelector.LENS_FACING_BACK
                } else if (lensFacing == CameraSelector.LENS_FACING_BACK && 
                    cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
                    CameraSelector.LENS_FACING_FRONT
                } else {
                    lensFacing
                }
            }

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(actualLensFacing)
                .build()

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
            cameraReady = true
            android.util.Log.d("CameraScreen", "Camera bound successfully with lens facing: $actualLensFacing")
        } catch (e: Exception) {
            android.util.Log.e("CameraScreen", "Camera binding failed: ${e.message}", e)
            cameraReady = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Take Photo") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK)
                            CameraSelector.LENS_FACING_FRONT
                        else
                            CameraSelector.LENS_FACING_BACK
                    }) {
                        Icon(Icons.Default.FlipCameraAndroid, "Flip Camera")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Camera Preview
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            // Bottom buttons row
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery Button
                if (onGalleryClick != null) {
                    FloatingActionButton(
                        onClick = onGalleryClick,
                        modifier = Modifier.size(56.dp),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Icon(
                            Icons.Default.PhotoLibrary,
                            contentDescription = "Gallery",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(56.dp))
                }

                // Capture Button
                FloatingActionButton(
                    onClick = {
                        imageCapture?.let { capture ->
                            takePicture(context, capture) { file, bitmap ->
                                onPhotoCaptured(file, bitmap)
                            }
                        }
                    },
                    modifier = Modifier.size(70.dp)
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Capture",
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // Spacer for balance
                Spacer(modifier = Modifier.size(56.dp))
            }
        }
    }
}

/**
 * Extension function to get CameraProvider
 */
private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { future ->
            future.addListener({
                continuation.resume(future.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

/**
 * Take picture with ImageCapture
 */
private fun takePicture(
    context: Context,
    imageCapture: ImageCapture,
    onPhotoCaptured: (File, Bitmap) -> Unit
) {
    val photoFile = File(
        context.cacheDir,
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                // Load bitmap and fix orientation based on EXIF
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                val rotatedBitmap = fixBitmapOrientation(photoFile.absolutePath, bitmap)
                
                // Save the rotated bitmap back to the file
                saveBitmapToFile(rotatedBitmap, photoFile)
                
                onPhotoCaptured(photoFile, rotatedBitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                android.util.Log.e("CameraScreen", "Photo capture failed", exception)
            }
        }
    )
}

/**
 * Fix bitmap orientation based on EXIF data
 */
private fun fixBitmapOrientation(filePath: String, bitmap: Bitmap): Bitmap {
    return try {
        val exif = ExifInterface(filePath)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        
        val rotation = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> 0f // Will handle flip separately
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> 0f
            else -> 0f
        }
        
        val matrix = Matrix()
        
        // Handle flip for front camera
        if (orientation == ExifInterface.ORIENTATION_FLIP_HORIZONTAL) {
            matrix.preScale(-1f, 1f)
        } else if (orientation == ExifInterface.ORIENTATION_FLIP_VERTICAL) {
            matrix.preScale(1f, -1f)
        }
        
        if (rotation != 0f) {
            matrix.postRotate(rotation)
        }
        
        if (rotation != 0f || orientation == ExifInterface.ORIENTATION_FLIP_HORIZONTAL || orientation == ExifInterface.ORIENTATION_FLIP_VERTICAL) {
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }
    } catch (e: Exception) {
        android.util.Log.e("CameraScreen", "Error fixing orientation", e)
        bitmap
    }
}

/**
 * Save bitmap to file
 */
private fun saveBitmapToFile(bitmap: Bitmap, file: java.io.File) {
    try {
        java.io.FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
        }
    } catch (e: Exception) {
        android.util.Log.e("CameraScreen", "Error saving bitmap", e)
    }
}

/**
 * Rotate bitmap if needed
 */
private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    if (degrees == 0f) return bitmap
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

/**
 * Load image from Gallery URI
 */
private fun loadImageFromUri(context: Context, uri: Uri): Pair<File, Bitmap>? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        
        // Save bitmap to cache file
        val photoFile = File(
            context.cacheDir,
            SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )
        
        FileOutputStream(photoFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        
        Pair(photoFile, bitmap)
    } catch (e: Exception) {
        android.util.Log.e("CameraScreen", "Failed to load image from gallery", e)
        null
    }
}
