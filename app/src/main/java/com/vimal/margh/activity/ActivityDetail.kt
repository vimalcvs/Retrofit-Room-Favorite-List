package com.vimal.margh.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.vimal.margh.databinding.ActivityDetailBinding
import com.vimal.margh.models.ModelWallpaper
import com.vimal.margh.util.Constant.EXTRA_KEY


class ActivityDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener { finish() }

        @Suppress("DEPRECATION")
        val modelList = intent.getSerializableExtra(EXTRA_KEY) as ModelWallpaper?
        if (modelList != null) {
            Glide.with(this)
                .load(modelList.largeImageURL)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.pbLoading.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.pbLoading.visibility = View.GONE
                        return false
                    }
                }).into(binding.ivImage)

        } else {
            Toast.makeText(this, "No data available", Toast.LENGTH_SHORT).show()
        }
    }


}