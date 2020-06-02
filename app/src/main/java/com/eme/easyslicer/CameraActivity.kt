package com.eme.easyslicer

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class CameraActivity : AppCompatActivity() {
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private val requestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initLog()
        managePermission()
    }

    private fun showView() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))

        initViews()
    }

    private fun initLog() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("Logging enabled")
        }
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder().build()

        cameraView.preferredImplementationMode = PreviewView.ImplementationMode.SURFACE_VIEW

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        // The use case is bound to an Android Lifecycle with the following code
        val camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)

        preview.setSurfaceProvider(cameraView.createSurfaceProvider(camera.cameraInfo))
    }

    private fun initViews() {
        // Pizza
        val bitmap = AppCompatResources.getDrawable(this, R.drawable.tpizza)
        imageView.setImageDrawable(bitmap)

        // Slices
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        // point to the center to display the circle
        drawView.center = PointF((width / 2).toFloat(), (height / 2).toFloat())

        // Seekbar
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                drawView.updateSlices(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun managePermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                showView()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                showEducationalUI()
            }
            else -> {
                // You can directly ask for the permission.
                requestPermissions()
            }
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            requestCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            this.requestCode -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    // Permission is granted. Continue the action or workflow in your app.
                    showView()
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    showEducationalUI()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun showEducationalUI() {
        seekbar.visibility = View.GONE

        val bitmap = AppCompatResources.getDrawable(this, R.drawable.ic_pizza_24dp)
        imageView.setImageDrawable(bitmap)

        Snackbar.make(cameraView, R.string.item_removed_message, Snackbar.LENGTH_INDEFINITE).show();
    }
}
