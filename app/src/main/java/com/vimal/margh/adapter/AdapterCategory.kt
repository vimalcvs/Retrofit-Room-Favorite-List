package com.vimal.margh.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.vimal.margh.databinding.ItemCategoryBinding
import com.vimal.margh.models.ModelCategory

class AdapterCategory(private val context: Context, private val categories: List<ModelCategory>) :
    RecyclerView.Adapter<AdapterCategory.ViewHolder>() {
    private var mOnItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        this.mOnItemClickListener = mItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.tvTitle.text = category.title

        Glide.with(context)
            .load(category.image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.binding.ivImage)

        holder.binding.clItem.setOnClickListener {
            if (mOnItemClickListener != null) {
                mOnItemClickListener!!.onItemClick(category)
            }
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }


    interface OnItemClickListener {
        fun onItemClick(category: ModelCategory?)
    }

    class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}