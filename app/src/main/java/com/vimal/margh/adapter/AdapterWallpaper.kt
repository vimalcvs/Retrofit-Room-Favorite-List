package com.vimal.margh.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.vimal.margh.R
import com.vimal.margh.databinding.ItemListBinding
import com.vimal.margh.databinding.ItemLoadMoreBinding
import com.vimal.margh.db.Repository
import com.vimal.margh.models.ModelWallpaper
import com.vimal.margh.util.Constant
import com.vimal.margh.util.Utils.downloadImage
import com.vimal.margh.util.Utils.shareImage

class AdapterWallpaper(
    private val context: Context,
    view: RecyclerView,
    private val modelLists: MutableList<ModelWallpaper?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_ITEM = 1
    private val repository: Repository by lazy { Repository.getInstance(context)!! }
    var scrolling: Boolean = false
    private var loading = false
    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var mOnItemClickListener: OnItemClickListener? = null


    init {
        lastItemViewDetector(view)

        view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    scrolling = true
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    scrolling = false
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }


    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        this.mOnItemClickListener = mItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_ITEM) {
            OriginalViewHolder(
                ItemListBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        } else {
            ProgressViewHolder(
                ItemLoadMoreBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is OriginalViewHolder) {
            val modelList = modelLists[position]
            holder.binding.pbLoading.visibility = View.VISIBLE

            holder.binding.ivDownload.setOnClickListener(object : View.OnClickListener {
                private var isDownloading = false

                override fun onClick(v: View) {
                    Toast.makeText(context, "Downloading image... ", Toast.LENGTH_SHORT).show()
                    if (!isDownloading) {
                        isDownloading = true
                        Toast.makeText(context, "Already downloading...", Toast.LENGTH_SHORT).show()
                        downloadImage(context, modelList!!.largeImageURL)
                    }
                }
            })

            Glide.with(context)
                .load(modelList!!.webformatURL)
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
                shareImage(
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
        } else {
            (holder as ProgressViewHolder).binding.progressBar1.isIndeterminate = true
        }
    }

    override fun getItemCount(): Int {
        return modelLists.size
    }

    override fun getItemViewType(position: Int): Int {
        val modelList = modelLists[position]
        if (modelList != null) {
            modelList.previewURL
            if (modelList.previewURL.isEmpty()) {
                return 2
            }
            return VIEW_ITEM
        } else {
            return 0
        }
    }

    fun insertData(items: List<ModelWallpaper?>) {
        setLoaded()
        val positionStart = itemCount
        val itemCount = items.size
        modelLists.addAll(items)
        notifyItemRangeInserted(positionStart, itemCount)
    }

    fun setLoaded() {
        loading = false
        for (i in 0 until itemCount) {
            if (modelLists[i] == null) {
                modelLists.removeAt(i)
                notifyItemRemoved(i)
            }
        }
    }

    fun setLoading() {
        if (itemCount != 0) {
            modelLists.add(null)
            notifyItemInserted(itemCount - 1)
            loading = true
        }
    }

    fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener?) {
        this.onLoadMoreListener = onLoadMoreListener
    }

    private fun lastItemViewDetector(recyclerView: RecyclerView) {
        if (recyclerView.layoutManager is LinearLayoutManager) {
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPos: Int = layoutManager.findLastVisibleItemPosition()
                    if (!loading && lastPos == itemCount - 1 && onLoadMoreListener != null) {
                        val page = itemCount / (Constant.LOAD_MORE)
                        onLoadMoreListener!!.onLoadMore(page)
                        loading = true
                    }
                }
            })
        }
    }

    fun clearData() {
        modelLists.clear()
        notifyDataSetChanged()
    }

    interface OnLoadMoreListener {
        fun onLoadMore(page: Int)
    }

    interface OnItemClickListener {
        fun onItemClick(modelWallpaper: ModelWallpaper?)
        fun onItemDelete(modelWallpaper: ModelWallpaper?)
    }

    class OriginalViewHolder(val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root)

    class ProgressViewHolder(val binding: ItemLoadMoreBinding) :
        RecyclerView.ViewHolder(binding.root)
}