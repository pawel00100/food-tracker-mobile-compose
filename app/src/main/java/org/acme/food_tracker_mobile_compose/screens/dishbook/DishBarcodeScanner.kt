package org.acme.food_tracker_mobile_compose.screens.dishbook

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import mu.KotlinLogging
import org.acme.food_tracker_mobile_compose.barcodeScanner.BarcodeAnalyser
import org.acme.food_tracker_mobile_compose.screens.menu.BackButton
import org.acme.food_tracker_mobile_compose.viewmodel.DishBarcodeScannerViewModel
import org.acme.food_tracker_mobile_compose.viewmodel.PermissionStatus
import java.util.concurrent.Executors

val logger = KotlinLogging.logger {}

@Composable
fun DishBarcodeScanner(
    navController: NavController,
    padding: PaddingValues,
    onCapturedBarcode: (Long) -> Unit = {}
) {
    val viewModel = DishBarcodeScannerViewModel()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding), color = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 30.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            BackButton(navController)
            Spacer(modifier = Modifier.height(16.dp))
            PreviewViewComposable(viewModel) { barcode ->
                onCapturedBarcode(barcode)
                navController.popBackStack()
            }
        }
    }
}

@Composable
fun cameraPermissionResultLauncher(viewModel: DishBarcodeScannerViewModel) =
    rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.cameraPermissionGranted =
                if (isGranted) PermissionStatus.GRANTED else PermissionStatus.REJECTED
        },
    )

@Composable
private fun PreviewViewComposable(
    viewModel: DishBarcodeScannerViewModel,
    onCapturedBarcode: (Long) -> Unit
) {
    val x = cameraPermissionResultLauncher(viewModel)
    SideEffect {
        x.launch(Manifest.permission.CAMERA)
    }

    CameraViewport(onCapturedBarcode)

    if (viewModel.cameraPermissionGranted == PermissionStatus.REJECTED) {
        CameraPermissionRequest(x)
    }
}

@Composable
private fun CameraPermissionRequest(x: ManagedActivityResultLauncher<String, Boolean>) {
    Column {
        Text(text = "Need camera permission for barcode scanning")
        Button(onClick = { x.launch(Manifest.permission.CAMERA) }) {
            Text(text = "Grant permission")
        }
    }
}

@Composable
private fun CameraViewport(onCapturedBarcode: (Long) -> Unit) {
    AndroidView(
        { context ->
            val cameraExecutor = Executors.newSingleThreadExecutor()
            val previewView = PreviewView(context).also {
                it.scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalyzer = ImageAnalysis.Builder().build().also {
                    it.setAnalyzer(
                        cameraExecutor,
                        BarcodeAnalyser { barcode ->
                            cameraProvider.unbindAll()
                            onCapturedBarcode(barcode.rawValue?.toLong()!!)
                        }
                    )
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()

                    cameraProvider.bindToLifecycle(
                        context as ComponentActivity,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )

                } catch (e: Exception) {
                    logger.debug("Use case binding failed", e)
                }
            }, ContextCompat.getMainExecutor(context))
            previewView
        },
        modifier = Modifier.fillMaxWidth()
    )
}
