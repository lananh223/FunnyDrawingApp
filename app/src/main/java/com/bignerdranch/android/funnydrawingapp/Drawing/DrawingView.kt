package com.bignerdranch.android.funnydrawingapp.Drawing

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attributes: AttributeSet) : View(context, attributes) {

    private var drawPath: CustomPath? = null
    private var canvasBitmap: Bitmap? = null
    private var drawPaint: Paint? = null
    private var canvasPaint: Paint? = null
    private var brushSize: Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas: Canvas? = null
    private var paths = ArrayList<CustomPath>()
    private var undoPaths = ArrayList<CustomPath>()

    init {
        setUpDrawing()
    }

    private fun setUpDrawing() {
        drawPaint = Paint()
        drawPath = CustomPath(color, brushSize)
        canvasPaint = Paint(Paint.DITHER_FLAG)
        drawPaint?.apply {
            this.color = color
            this.style = Paint.Style.STROKE
            this.strokeJoin = Paint.Join.ROUND
            this.strokeCap = Paint.Cap.ROUND
        }
    }

    // set up inflated screen everytime size changes
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(canvasBitmap!!)
    }

    // write or draw sth on on canvas
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(canvasBitmap!!, 0f, 0f, canvasPaint)

        for (path in paths) {
            drawPaint!!.strokeWidth = path.brushThickness
            drawPaint!!.color = path.color
            canvas.drawPath(path, drawPaint!!)
        }

        if (drawPath!!.isEmpty) {
            drawPaint!!.strokeWidth = drawPath!!.brushThickness
            drawPaint!!.color = drawPath!!.color
            canvas.drawPath(drawPath!!, drawPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                drawPath!!.color = color
                drawPath!!.brushThickness = brushSize
                drawPath!!.reset()
                drawPath!!.moveTo(touchX!!, touchY!!)
            }
            MotionEvent.ACTION_MOVE -> {
                drawPath!!.lineTo(touchX!!, touchY!!)
            }
            MotionEvent.ACTION_UP -> {
                paths.add(drawPath!!)
                drawPath = CustomPath(color, brushSize)
            }
            // do nothing
            else -> return false
        }
        invalidate()
        return true
    }

    // swap size dimension with different screen
    fun setSizeForBrush(newSize: Float) {
        brushSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            newSize, resources.displayMetrics
        )
        drawPaint!!.strokeWidth = brushSize
    }

    fun setColor(newColor: String) {
        color = Color.parseColor(newColor)
        drawPaint!!.color = color
    }

    fun reset() {
        if (paths.size > 0) {
            paths = ArrayList()
            invalidate()
        }
        val sharedPreferences =
            context.getSharedPreferences("image link", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("image link", "")
            apply()
        }
        DrawingAndGallery().updateBackgroundImage()
    }

    fun onClickUndo() {
        if (paths.size > 0) {
            undoPaths.add(paths.removeAt(paths.size - 1))
            //redraw, call onDraw
            invalidate()
        }
    }

    internal inner class CustomPath(var color: Int, var brushThickness: Float) : Path() {

    }
}