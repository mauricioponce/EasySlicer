package com.eme.easyslicer

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class DrawView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : View(context, attrs, defStyleAttr) {

    private val margin = 120 // this must depend on @dimen/margin_pizza
    private var radius: Float = 0f
    private var slices = 2

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 4f
        pathEffect = DashPathEffect(floatArrayOf(15f, 10f), 0f)
    }

    var center = PointF()
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.apply {
            val targetWidth = width - margin
            radius = (targetWidth / 2).toFloat()
            drawSlices(this)
            //drawEdge(this)
        }
    }

    private fun drawEdge(canvas: Canvas) {
        val bitmapOrg = BitmapFactory.decodeResource(resources, R.drawable.pizza)
        val matrix = Matrix()
        matrix.postScale(1f, 1f)
        //canvas.drawBitmap(bitmapOrg, matrix, paint)

        var margin = 10

        var targetWidth: Int = width - margin
        var targetHeight: Int = height - margin
        val bmp = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)

        var newMatrix = Matrix()
        matrix.mapRect(RectF(0f, 0f, bitmapOrg.width.toFloat(), bitmapOrg.height.toFloat()))
        var resizedBitmap =
            Bitmap.createBitmap(bmp, 0, 0, targetWidth, targetHeight, newMatrix, true)

        canvas.drawBitmap(
            resizedBitmap,
            (width - resizedBitmap.width) / 2.toFloat(),
            (height - resizedBitmap.height) / 2.toFloat(),
            paint
        )
    }

    private fun drawSlices(canvas: Canvas) {
        val coords = getCoords(center, slices, radius)
        coords.map {
            canvas.drawLine(center.x, center.y, it.x, it.y, paint)
        }
    }

    fun updateSlices(progress: Int) {
        invalidate()
        slices = progress
    }
}