package com.example.transparentemm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AppsAdapter(private var apps: ArrayList<App> = arrayListOf()) :
    RecyclerView.Adapter<AppsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.template_app, parent, false)
        return AppsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onBindViewHolder(holder: AppsViewHolder, position: Int) {
        val app = apps[position]
        holder.setAppName(app.appName)
        holder.setAppIcon(app.icon)
    }

    fun addApps(apps: List<App>) {
        this.apps.clear()
        this.apps.addAll(apps)
        notifyDataSetChanged()
    }
}