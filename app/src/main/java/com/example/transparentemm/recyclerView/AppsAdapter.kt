package com.example.transparentemm.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.transparentemm.App
import com.example.transparentemm.R

class AppsAdapter(
    private var mApps: ArrayList<App> = arrayListOf(),
    private var onAppClick: (app: App) -> Unit
) :
    RecyclerView.Adapter<AppsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.template_app, parent, false)
        return AppsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mApps.size
    }

    override fun onBindViewHolder(holder: AppsViewHolder, position: Int) {
        val app = mApps[position]
        holder.apply {
            setAppName(app.appName)
            setAppIcon(app.icon)
            setOnClickListener { onAppClick(app) }
        }
    }

    fun addApps(apps: List<App>) {
        mApps.apply {
            clear()
            addAll(apps)
        }
        notifyDataSetChanged()
    }
}