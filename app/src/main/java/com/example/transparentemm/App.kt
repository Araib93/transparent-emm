package com.example.transparentemm

import android.net.Uri

data class App(
    val appName: String,
    val packageName: String,
    val icon: Uri?
)