package com.vimal.margh.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.vimal.margh.R
import com.vimal.margh.databinding.ItemListBinding
import com.vimal.margh.db.Repository
import com.vimal.margh.models.ModelWallpaper
import com.vimal.margh.util.Utils

@SuppressLint("NotifyDataSetChanged")
class AdapterFavorite(val context: Context, private var list: List<ModelWallpaper>) :
    RecyclerView.Adapter<AdapterFavorite.ViewHolder>() {
    private var mOnItemClickListener: OnItemClickListener? = null
    private val repository: Repository by lazy { Repository.getInstance(context)!! }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        this.mOnItemClickListener = mItemClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modelList = list[position]

        holder.binding.pbLoading.visibility = View.VISIBLE

        holder.binding.ivDownload.setOnClickListener(object : View.OnClickListener {
            private var isDownloading = false

            override fun onClick(v: View) {
                Toast.makeText(context, "Downloading image... ", Toast.LENGTH_SHORT).show()
                if (!isDownloading) {
                    isDownloading = true
                    Toast.makeText(context, "Already downloading...", Toast.LENGTH_SHORT).show()
                    Utils.downloadImage(context, modelList.largeImageURL)
                }
            }
        })

        Glide.with(context)
            .load(modelList.webformatURL)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.binding.pbLoading.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.binding.pbLoading.visibility = View.GONE
                    return false
                }
            }).into(holder.binding.ivImage)

        holder.binding.ivShare.setOnClickListener {
            Utils.shareImage(
                context, modelList.largeImageURL
            )
        }

        if (repository.isFavorite(modelList.id)) {
            holder.binding.ivFavorite.setImageResource(R.drawable.ic_btm_nav_favorite_active)
        } else {
            holder.binding.ivFavorite.setImageResource(R.drawable.ic_btm_nav_favorite_normal)
        }

        holder.binding.cvCard.setOnClickListener {
            if (mOnItemClickListener != null) {
                mOnItemClickListener!!.onItemClick(modelList)
            }
        }

        holder.binding.ivSave.setOnClickListener {
            if (repository.isFavorite(modelList.id)) {
                repository.deleteFavorite(modelList)
                holder.binding.ivFavorite.setImageResource(R.drawable.ic_btm_nav_favorite_normal)
                Toast.makeText(context, "Remove to Favorite", Toast.LENGTH_SHORT).show()
            } else {
                repository.insertFavorite(modelList)
                holder.binding.ivFavorite.setImageResource(R.drawable.ic_btm_nav_favorite_active)
                Toast.makeText(context, "Added to Favorite", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun updateData(productList: List<ModelWallpaper>) {
        this.list = productList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onItemClick(modelWallpaper: ModelWallpaper?)
    }


    class ViewHolder(val binding: ItemListBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}