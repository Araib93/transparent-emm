package com.example.transparentemm

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.util.Log
import java.util.*

class ApkInfoExtractor(private var context: Context) {

    private val TAG = "ApkInfoExtractor"

    fun getInstalledApps(): List<String> {
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            //flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        }

        val packageNameList = ArrayList<String>()
        val resolveInfoList = context.packageManager.queryIntentActivities(intent, 0)
        for (resolveInfo in resolveInfoList) {
            val activityInfo = resolveInfo.activityInfo
            //if (resolveInfo.isSystemPackage) {
            packageNameList.add(activityInfo.applicationInfo.packageName)
            //}
        }
        return packageNameList
    }

    fun ResolveInfo.isSystemPackage(): Boolean {
        return activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    fun getAppIconByPackageName(packageName: String): Drawable {
        val drawable = with(context.packageManager) {
            try {
                getApplicationIcon(packageName)
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e(TAG, e.localizedMessage ?: "Unable to fetch icon")
                getApplicationIcon(context.packageName)
            }
        }
        return drawable
    }

    fun getAppNameByPackageName(packageName: String): String {
        val name = try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo) as String
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, e.localizedMessage ?: "Unable to fetch name")
            ""
        }
        return name
    }
}
