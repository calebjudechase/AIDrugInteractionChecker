package com.example.aidruginteractionchecker

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class BarcodeScannerPage : AppCompatActivity() {

    lateinit var previewView: PreviewView
    lateinit var cameraSelect: CameraSelector
    lateinit var cameraFutureProvider: ListenableFuture<ProcessCameraProvider>
    lateinit var cameraProviderProcess: ProcessCameraProvider
    lateinit var cameraPreview: Preview
    lateinit var analyzeImage: ImageAnalysis

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanner_page)

        previewView = findViewById<PreviewView>(R.id.previewView) //initializes previewView

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) { //if permission isn't granted for camera
            val cameraPermissionRequester = //sets up camera permission requester
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted -> //requests permission
                    if (!isGranted) { //if not granted sends back to main page
                        val mainPageSkip = Intent(this, MainActivity::class.java)
                        mainPageSkip.putExtra("fragment", 1)
                        startActivity(mainPageSkip) //goes to main page
                    }
                }
            cameraPermissionRequester.launch(android.Manifest.permission.CAMERA) //runs camera permission requester
        }

        cameraSelect = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build() //selects camera
        cameraFutureProvider = ProcessCameraProvider.getInstance(this) //gets instance
        cameraFutureProvider.addListener({ //adds listener
            cameraProviderProcess = cameraFutureProvider.get() //gets the future provider for teh process
            cameraPreviewBind() //runs binding function
            bindAnalysis() //runs analysis function
        }, ContextCompat.getMainExecutor(this))

        val backBtn = findViewById<Button>(R.id.backBtn)
        backBtn.setOnClickListener{
            val mainPageSkip = Intent(this, MainActivity::class.java)
            mainPageSkip.putExtra("fragment", 1)
            startActivity(mainPageSkip) //goes to main page
        }
    }

    private fun cameraPreviewBind() {
        cameraPreview = Preview.Builder().build() //initializes the preview
        cameraPreview.surfaceProvider = previewView.getSurfaceProvider() //sets the preview
        cameraProviderProcess.bindToLifecycle(this, cameraSelect, cameraPreview) //binds preview to lifecycle
    }

    private fun bindAnalysis() {
        val scanner: BarcodeScanner = BarcodeScanning.getClient(BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_UPC_A).build()) //initializes barcode scanner var
        val camExecution = Executors.newSingleThreadExecutor() //initializes camera execution var
        if (previewView.display != null) { //if the previewView display is not null (in order to prevent null pointer error)
            analyzeImage = ImageAnalysis.Builder().setTargetRotation(previewView.display.rotation).build() //sets analyze image
            analyzeImage.setAnalyzer(camExecution){imageProxy -> //sets analyzer
                imageProxyProcessor(scanner, imageProxy) //runs image proxy process function
            }
            cameraProviderProcess.bindToLifecycle(this, cameraSelect, analyzeImage) //binds to lifecycle
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun imageProxyProcessor(barcodeScanner: BarcodeScanner, imageProxy: ImageProxy) {
        val imageInput = InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees) //sets input image
        barcodeScanner.process(imageInput).addOnSuccessListener { barcodes -> //if it succeeds
            if(barcodes.isNotEmpty()) { //checks if barcode list is empty, if its not
                onScan?.invoke(barcodes) //invokes barcodes
                onScan = null //sets on scan to null
                finish() //finishes activity, returning barcodes to previous context
            }
        }.addOnFailureListener { //if it fails
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show() //tells em an error occurred
        }.addOnCompleteListener {
            imageProxy.close() //closes image proxy
        }
    }

    companion object {
        private var onScan: ((barcodes: List<Barcode>) -> Unit)? = null //sets on scan to null
        fun goToScanner(context: Context, onScan: (barcodes: List<Barcode>)-> Unit) { //takes previous context and returns barcodes
            this.onScan = onScan //sets the function onScan to variable onScan
            Intent(context, BarcodeScannerPage::class.java).also { //sets up intent to go to this activity
                context.startActivity(it) //does it
            }
        }
    }
}


