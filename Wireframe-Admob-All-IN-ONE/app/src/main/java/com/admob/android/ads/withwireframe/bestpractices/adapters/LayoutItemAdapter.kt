package com.admob.android.ads.withwireframe.bestpractices.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admob.android.ads.withwireframe.bestpractices.activity.AboutActivity
import com.admob.android.ads.withwireframe.bestpractices.activity.HomeActivity
import com.admob.android.ads.withwireframe.bestpractices.databinding.LayoutItemBinding
import com.admob.android.ads.withwireframe.bestpractices.model.LayoutItem

class LayoutItemAdapter(private val itemCount: Int = 10, val callback: (() -> Unit)) : RecyclerView.Adapter<LayoutItemAdapter.LayoutItemViewHolder>() {
    private var items: List<LayoutItem> = listOf(
        LayoutItem("Layout 1", "Description for layout 1")
    )

    init {
        for (i: Int in 2..itemCount) {
            items += LayoutItem("Layout $i", "Description for layout $i")
        }
    }

    inner class LayoutItemViewHolder(val binding: LayoutItemBinding) :
        RecyclerView.ViewHolder(binding.root){
            init {
                binding.root.setOnClickListener {
                    callback.invoke()
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayoutItemViewHolder {
        val binding = LayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LayoutItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LayoutItemViewHolder, position: Int) {

    }

    override fun getItemCount(): Int = items.size
}
