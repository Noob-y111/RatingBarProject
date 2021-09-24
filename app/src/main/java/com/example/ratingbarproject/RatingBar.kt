package com.example.ratingbarproject

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import kotlin.math.*

class RatingBar : View {

    /**
     * <declare-styleable name="RatingBar">
     *  <attr name="starSize" format="dimension"/>
     *  <attr name="starLightColor" format="color"/>
     *  <attr name="starDarkColor" format="color"/>
     *  <attr name="starSelectedColor" format="color"/>
     *  </declare-styleable>
     */

    data class Point(var x: Float, var y: Float)
    interface TouchOver {
        fun touchOver(percent: Float)
    }

    //attrs
    private var defaultWidth = dpToPx(100f)
    private var starSize = dpToPx(20f)
    private var starCount = 1

    @ColorInt
    private var starLightColor = Color.YELLOW

    @ColorInt
    private var starDarkColor = Color.GRAY

    private val path = Path()
    private val darkPaint = Paint().also {
        it.style = Paint.Style.FILL
        it.strokeWidth = dpToPx(2f)
        it.isAntiAlias = true
    }
    private val lightPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val mode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

    private var currentX = 0f
    private var touchListener: TouchOver? = null


    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        getAttrs(context!!, attrs)
    }

    private fun getAttrs(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.RatingBar).apply {
            starSize = getDimension(R.styleable.RatingBar_starSize, starSize)
            starLightColor = getColor(R.styleable.RatingBar_starLightColor, starLightColor)
            starDarkColor = getColor(R.styleable.RatingBar_starDarkColor, starDarkColor)
            starCount = getInt(R.styleable.RatingBar_starCount, starCount)
            recycle()
        }
        initPaint()
    }

    private fun initPaint() {
        darkPaint.color = starDarkColor
        lightPaint.color = starLightColor
    }

    private fun getRadian(angle: Float): Float {
        return (angle / 180f * PI).toFloat()
    }

    private fun initPathPoint() {
        //radius == starSize / 2
        val radius = (starSize) / 2
        val x1 = 0f
        val y1 = -radius
        val point1 = Point(x1, y1)

        val x2 = sin(getRadian(36f)) * (radius / 2)
        val y2 = -cos(getRadian(36f)) * (radius / 2)
        val point2 = Point(x2, y2)

        val x3 = cos(getRadian(18f)) * radius
        val y3 = -sin(getRadian(18f)) * radius
        val point3 = Point(x3, y3)

        val x4 = cos(getRadian(18f)) * (radius / 2)
        val y4 = sin(getRadian(18f)) * (radius / 2)
        val point4 = Point(x4, y4)

        val x5 = sin(getRadian(36f)) * radius
        val y5 = cos(getRadian(36f)) * radius
        val point5 = Point(x5, y5)

        val x6 = 0f
        val y6 = (radius / 2f)
        val point6 = Point(x6, y6)
        val point7 = Point(-point5.x, point5.y)
        val point8 = Point(-point4.x, point4.y)
        val point9 = Point(-point3.x, point3.y)
        val point10 = Point(-point2.x, point2.y)

        moveTo(point1)
        lineTo(point2)
        lineTo(point3)
        lineTo(point4)
        lineTo(point5)
        lineTo(point6)
        lineTo(point7)
        lineTo(point8)
        lineTo(point9)
        lineTo(point10)
        close()
    }

    private fun moveTo(point: Point) {
        path.moveTo(point.x, point.y)
    }

    private fun lineTo(point: Point) {
        path.lineTo(point.x, point.y)
    }

    private fun close() {
        path.close()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var width: Int
        var height: Int
        width = MeasureSpec.getSize(widthMeasureSpec)
        height = MeasureSpec.getSize(heightMeasureSpec)

        if (widthMode == MeasureSpec.AT_MOST) {
            width = (starCount * starSize).toInt()
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            height = starSize.toInt()
        }

        val targetWidth = max(width.toFloat(), defaultWidth)
        initPathPoint()
        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(targetWidth.toInt(), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            val layer =
                it.saveLayer(
                    0f,
                    0f,
                    measuredWidth.toFloat(), measuredHeight.toFloat(), null
                )
            it.save()
            it.translate(starSize / 2, (measuredHeight / 2).toFloat())
            repeat(starCount) { _ ->
                it.drawPath(path, darkPaint)
                it.translate(starSize, 0f)
            }
            it.restore()

            lightPaint.xfermode = mode
            it.drawRect(0f, 0f, currentX, measuredHeight.toFloat(), lightPaint)
            lightPaint.xfermode = null
            it.restoreToCount(layer)
        }
    }

    private fun handleEvent(event: MotionEvent?) {
        event?.let {
            if (it.x <= starCount * starSize) {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {

                    }

                    MotionEvent.ACTION_UP -> {
                        currentX = it.x
                    }

                    MotionEvent.ACTION_MOVE -> {
                        currentX = it.x
                    }
                }
                touchListener?.touchOver(it.x / (starSize * starCount))
                invalidate()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        handleEvent(event)
        performClick()
        return true
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    fun setTouchOverListener(listener: TouchOver) {
        this.touchListener = listener
    }

    fun setLightColor(@ColorInt color: Int) {
        this.starLightColor = color
        lightPaint.color = starLightColor
        invalidate()
    }

    fun setDarkColor(@ColorInt color: Int) {
        this.starDarkColor = color
        darkPaint.color = starDarkColor
        invalidate()
    }

//    fun setStarCount(count: Int) {
//        this.starCount = count
//        measure(
//            MeasureSpec.makeMeasureSpec((starSize * starCount).toInt(), MeasureSpec.EXACTLY),
//            MeasureSpec.makeMeasureSpec(starSize.toInt(), MeasureSpec.EXACTLY)
//        )
//        invalidate()
//    }
//
//    fun setStarSize(dpOfStarSize: Float) {
//        this.starSize = dpToPx(dpOfStarSize)
//        measure(
//            MeasureSpec.makeMeasureSpec((starSize * starCount).toInt(), MeasureSpec.EXACTLY),
//            MeasureSpec.makeMeasureSpec(starSize.toInt(), MeasureSpec.EXACTLY)
//        )
//        invalidate()
//    }
}