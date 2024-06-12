package com.example.cmft

import android.graphics.BitmapFactory
import android.widget.ImageView
import java.io.InputStream


class StreamingVideo(private val view: ImageView)  {

    fun refreshFrame(inputStream: InputStream) {
        val bitmap = BitmapFactory.decodeStream(inputStream);
        view.setImageBitmap(bitmap)
    }
}