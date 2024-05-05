@file:Suppress("DEPRECATION")

package com.vimal.margh.util

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.ByteArrayOutputStream

object Utils {


    fun getErrors(e: Exception?) {
        println("TAG :: " + Log.getStackTraceString(e))
    }


    fun shareImage(context: Context, imageUrl: String?) {
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>,
                    isFirstResource: Boolean
                ): Boolean {
                    Toast.makeText(context, "Failed to load image for sharing", Toast.LENGTH_SHORT)
                        .show()
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    model: Any,
                    target: Target<Bitmap>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.setType("image/*")
                    val bytes = ByteArrayOutputStream()
                    resource.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                    val path = MediaStore.Images.Media.insertImage(
                        context.contentResolver,
                        resource,
                        "Image",
                        null
                    )
                    val uri = Uri.parse(path)
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                    context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
                    return false
                }
            })
            .submit()
    }

    fun downloadImage(context: Context, url: String?) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle("Image Download")
        request.setDescription("Downloading image...")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "image.jpg")

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }


    fun shareApp(context: Context) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        val shareBody =
            "Check out this awesome app!" + "https://play.google.com/store/apps/details?id=" + context.packageName
        val shareSubject = "Share App"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject)
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        context.startActivity(Intent.createChooser(shareIntent, "Share using"))
    }

    fun contactApp(context: Context) {
        val message = "Hello, this is a direct message from my app!"
        val uri = Uri.parse("smsto:" + "+918882683887")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.setPackage("com.whatsapp")
        context.startActivity(Intent.createChooser(intent, ""))
    }
}
