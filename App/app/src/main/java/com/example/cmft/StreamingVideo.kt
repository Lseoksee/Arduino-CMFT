package com.example.cmft

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.view.SurfaceHolder
import java.nio.ByteBuffer


class StreamingVideo(private val holder: SurfaceHolder)  {

    fun refreshFrame(byteBuffer: ByteBuffer) {
        val bitmap = BitmapFactory.decodeByteArray(byteBuffer.array(), 0, byteBuffer.limit())
        val canvas: Canvas = holder.lockCanvas()
        canvas.drawBitmap(bitmap, 0f, 0f, Paint())
        holder.unlockCanvasAndPost(canvas)
    }
}