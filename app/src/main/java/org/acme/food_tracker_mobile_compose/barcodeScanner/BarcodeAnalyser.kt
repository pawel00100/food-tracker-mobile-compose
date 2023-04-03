package org.acme.food_tracker_mobile_compose.barcodeScanner

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import mu.KotlinLogging

val logger = KotlinLogging.logger {}

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
class BarcodeAnalyser(
    val callback: (Barcode) -> Unit
) : ImageAnalysis.Analyzer {

    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_EAN_13)
        .build()

    val scanner = BarcodeScanning.getClient(options)

    override fun analyze(imageProxy: ImageProxy) {
        val image = InputImage.fromBitmap(
            imageProxy.toBitmap(),
            imageProxy.imageInfo.rotationDegrees
        )
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.size > 0) {
                    logger.info("Success")
                    callback(barcodes[0])
                }
            }

        imageProxy.close()
    }
}