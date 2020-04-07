package com.example.transparentemm

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.template_app.view.*

class AppsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun setAppIcon(icon: Uri?) {
        itemView.iv_icon.setImageURI(icon)
    }

    fun setAppName(appName: String) {
        itemView.tv_name.text = appName
    }
}