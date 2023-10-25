package com.example.mymusic

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SplashActivity : AppCompatActivity() {
    private val permissionRequestCode = 123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)



        // Add a delay before checking permissions
        Handler(Looper.getMainLooper()).postDelayed({
            if (checkPermissions()) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                requestPermissions()
            }
        }, 2000) // Delay for 2 seconds (adjust as needed)

    }
    private fun checkPermissions(): Boolean {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            val readStoragePermission = android.Manifest.permission.READ_MEDIA_AUDIO
            val granted = PackageManager.PERMISSION_GRANTED
            return ContextCompat.checkSelfPermission(this, readStoragePermission) == granted
        } else {
            // Handle the condition for other Android versions
            // You might want to perform a different check or action here.
            // For example, check for WRITE_EXTERNAL_STORAGE permission.
            // Return true or false as needed for the specific condition.
            val readStoragePermission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            val granted = PackageManager.PERMISSION_GRANTED
            return ContextCompat.checkSelfPermission(this, readStoragePermission) == granted
        }

    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO),
                permissionRequestCode
            )
        } else {
            // Handle the request for other Android versions
            // You might want to request a different permission or perform a different action.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                permissionRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == permissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, show the RecyclerView
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied, handle this case as needed
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


}