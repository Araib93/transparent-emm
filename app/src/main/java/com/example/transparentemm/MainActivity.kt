package com.example.transparentemm

import android.Manifest
import android.app.WallpaperManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val REQUEST_READ_STORAGE = 100

    private val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

    private val appsAdapter by lazy {
        AppsAdapter { app ->
            packageManager.getLaunchIntentForPackage(app.packageName)
                ?.let { intent ->
                    startActivity(intent)
                } ?: run {
                Toast.makeText(
                    this,
                    "${app.packageName} Error, Please Try Again.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Info: Disabled as we will not be needing this
/*
        window.addFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
*/

//        window.decorView.systemUiVisibility = flags
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkStoragePermission()?.let { setWallpaper() }
        showApps()
    }

    private fun showApps() {
        rv_apps.adapter = appsAdapter
        rv_apps.layoutManager = GridLayoutManager(this, 4)

        loadApps()
    }

    private fun loadApps() {
        val apkInfoExtractor = ApkInfoExtractor(this)
        val fileUtils = FileUtils(this)
        CoroutineScope(Dispatchers.IO).launch {
            val apps = apkInfoExtractor.getInstalledApps().map {
                App(
                    appName = apkInfoExtractor.getAppNameByPackageName(it),
                    packageName = it,
                    icon = fileUtils.saveBitmap(
                        it,
                        apkInfoExtractor.getAppIconByPackageName(it).toBitmap()
                    )
                )
            }

            withContext(Dispatchers.Main) {
                appsAdapter.addApps(apps)
            }
        }
    }

    private fun checkStoragePermission(): Any? {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) true else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_READ_STORAGE
            )
            null
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_READ_STORAGE -> {
                if (permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    setWallpaper()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun setWallpaper() {
        container.background = WallpaperManager.getInstance(this).drawable
    }

    override fun onBackPressed() {}
}
