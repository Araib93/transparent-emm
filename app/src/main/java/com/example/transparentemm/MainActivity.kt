package com.example.transparentemm

import android.Manifest
import android.app.WallpaperManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.GridLayoutManager
import com.example.transparentemm.misc.ApkInfoExtractor
import com.example.transparentemm.misc.FileUtils
import com.example.transparentemm.recyclerView.AppsAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val REQUEST_READ_STORAGE = 100
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
        val apkInfoExtractor =
            ApkInfoExtractor(this)
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
