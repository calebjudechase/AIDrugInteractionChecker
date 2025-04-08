package com.example.aidruginteractionchecker

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture

class BarcodeScannerPage : AppCompatActivity() {

    lateinit var previewView: PreviewView
    lateinit var cameraSelect: CameraSelector
    lateinit var cameraFutureProvider: ListenableFuture<ProcessCameraProvider>
    lateinit var cameraProviderProcess: ProcessCameraProvider
    lateinit var cameraPreview: Preview

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
        cameraPreview.setSurfaceProvider(previewView.getSurfaceProvider()) //sets the preview
        cameraProviderProcess.bindToLifecycle(this, cameraSelect, cameraPreview) //binds preview to lifecycle
    }

    companion object {
        fun goToScanner(context: Context, onScan: ()-> Unit) {
            Intent(context, BarcodeScannerPage::class.java).also { //sets up intent to go to this activity
                context.startActivity(it) //does it
            }
        }
    }
}