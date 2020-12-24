package com.example.mytestbanner.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.mytestbanner.R
import com.example.mytestbanner.utill.dp
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.tan

/**
 *  author : Gary
 *  e-mail : 2509908478@qq.com
 *  time   : 2020/12/21 13:49
 *  desc   : 自定义三角到八角分布器
 */
class PolygonDistributionView
    : View {
    private var paint = Paint()
    //多边形层数
    private var floorCount = 3
    //多边形边数（属性个数）
    private var angleCount = 3
    //标题文本长度
    private var textLength = 2
    //属性标题列表
    private var title = listOf("血量", "蓝量", "攻击", "防御", "移速", "耐力", "氧气", "重量")
    //各属性分布层数，不大于总层数
    private var scaleList = listOf(3, 1, 2, 2, 1, 2, 3, 1, 3)
    //覆盖区域颜色
    private var areaColor = resources.getColor(R.color.trared)
    //边框与连线颜色
    private var lineColor = resources.getColor(R.color.black)
    //属性文本颜色
    private var textColor = resources.getColor(R.color.black)
    //默认文本与边框距离
    private var defaultTextPadding = 20f
    //属性文本大小（px）
    private var textSize = 18f.dp
    //是否显示各层连线 默认显示
    private var isShowConnect = true
    //是否显示边框 默认显示
    private var isShowLine = true
    //画笔粗细
    private var paintSize = 5f
    private var mWidth = 0
    private var mHeight = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : this(context, attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.PolygonDistributionView)
        if (typedArray != null) {
            //MyView_line_color线条颜色
            lineColor = typedArray.getColor(R.styleable.PolygonDistributionView_line_color, Color.BLACK)
            //MyView_line_size线条宽度
            paintSize = typedArray.getDimension(R.styleable.PolygonDistributionView_line_size, 5f)
            //MyView_floor_count绘制层数（不得小于各元素分配的比例）
            floorCount = typedArray.getInt(R.styleable.PolygonDistributionView_floor_count, 3)
            //MyView_angle_count绘制元素个数
            angleCount = typedArray.getInt(R.styleable.PolygonDistributionView_angle_count, 3)
            //MyView_text_color文本颜色
            textColor = typedArray.getColor(R.styleable.PolygonDistributionView_text_color, Color.BLACK)
            //MyView_text_size文本大小
            textSize = typedArray.getDimension(R.styleable.PolygonDistributionView_text_size, 18f.dp)
            //MyView_area_color覆盖区域颜色
            areaColor = typedArray.getColor(R.styleable.PolygonDistributionView_area_color, ContextCompat.getColor(context, R.color.trared))
            //MyView_default_text_padding默认文本与图形间距
            defaultTextPadding = typedArray.getDimension(R.styleable.PolygonDistributionView_default_text_padding, 20f)
        }
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attributeSet, defStyleAttr, defStyleRes)

    /**
     * 设置元素标题（不得少于元素个数）
     */
    fun setTitle(titles: List<String>): PolygonDistributionView {
        this.title = titles
        return this
    }

    /**
     * 设置各元素对应比例（不得少于元素个数，不得低于层数）
     */
    fun setScaleList(list: List<Int>): PolygonDistributionView {
        this.scaleList = list
        return this
    }

    /**
     * 设置层数
     */
    fun setFloorCount(i: Int): PolygonDistributionView {
        this.floorCount = i
        return this
    }

    /**
     * 设置属性个数
     */
    fun setAngleCount(i: Int): PolygonDistributionView {
        this.angleCount = i
        return this
    }

    /**
     * 设置是否显示连接线
     */
    fun setIsShowConnect(isShow: Boolean): PolygonDistributionView {
        this.isShowConnect = isShow
        return this
    }

    /**
     * 设置是否显示边框
     */
    fun setIsShowLine(isShow: Boolean): PolygonDistributionView {
        this.isShowLine = isShow
        return this
    }

    private fun init() {
        paint.color = lineColor
        paint.strokeWidth = paintSize
        paint.style = Paint.Style.STROKE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)


        //设置warp_content的默认宽高
        val width = 400
        val height = 400
        //AT_MOST对应wrap_content；EXACTLY对应match_parent
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, height)
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, heightSize)
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, height)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        init()
        mWidth = width - paddingLeft - paddingRight
        mHeight = height - paddingBottom - paddingTop
        mWidth = mWidth.coerceAtMost(mHeight)
        mHeight = mWidth
        for (i in title)
            textLength = max(i.length, textLength)
        when (angleCount) {
            3 -> {
                drawThreeAngle(canvas)
            }
            4 -> {
                drawFourAngle(canvas)
            }
            5 -> {
                drawFiveAngle(canvas)
            }
            6 -> {
                drawSixAngle(canvas)
            }
            7 -> {
                drawSevenAngle(canvas)
            }
            8 -> {
                drawEightAngle(canvas)
            }
        }
    }

    /**
     * 三角分布器  等边三角形
     */
    private fun drawThreeAngle(canvas: Canvas) {
        //三角形宽度，边长
        val ww = mWidth - defaultTextPadding * 2 - textLength * textSize
        //中心点各对角线的夹角
        val angle = (Math.PI * 2 / angleCount).toFloat()
        //三角形高度，顶点到底边的垂直线
        var hh = ww * sin(angle / 2)
        val pathLine = Path()
        //中心点到各角的距离
        val r = ww / 2 / sin(angle / 2)

        /**
         *                      图形中心点
         *   各正多边形的中心点横坐标X都可以是整个控件的横向中心位置
         *   但是由于三角形，正五边形，正七边形比较特殊
         *   所以它们的中心点纵坐标Y并不在整个控件的中心位置
         *   这个时候就需要根据实际情况做一下小运算
         *   比如说三角形，首先算得三角形的宽度作为底边长
         *   因为中心点到各角的连线夹角都是相等的
         *   所以根据角度和边长就可算得中心点到各夹角的连线长度
         *   然后根据这个长度就可已算出纵坐标Y的位置
         *   之后的图形绘制都需要根据中心点和中心点到各夹角的长度来计算
         */
        val centerX = width / 2
        //图形中心点y
        val centerY = r + paddingTop + textSize + defaultTextPadding
        //绘制边框
        if (isShowLine) {
            for (i in 1..floorCount) {
                val ur = r * i / floorCount
                pathLine.moveTo(centerX + 0f, centerY - ur)
                pathLine.lineTo(centerX + ur * sin(angle / 2), centerY + ur * cos(angle / 2))
                pathLine.lineTo(centerX - ur * sin(angle / 2), centerY + ur * cos(angle / 2))
                pathLine.close()
            }
            canvas.drawPath(pathLine, paint)
        }
        //绘制连接线
        if (isShowConnect) {
            pathLine.reset()
            pathLine.moveTo(centerX.toFloat(), centerY)
            pathLine.lineTo(centerX.toFloat(), centerY - r)
            pathLine.moveTo(centerX.toFloat(), centerY)
            pathLine.lineTo(centerX + r * sin(angle / 2), centerY + r * cos(angle / 2))
            pathLine.moveTo(centerX.toFloat(), centerY)
            pathLine.lineTo(centerX - r * sin(angle / 2), centerY + r * cos(angle / 2))
            pathLine.close()
            canvas.drawPath(pathLine, paint)
        }

        //绘制文本
        paint.color = textColor
        paint.textSize = textSize
        paint.style = Paint.Style.FILL
        canvas.drawText(title[0], centerX - textSize, centerY - r - defaultTextPadding, paint)
        canvas.drawText(title[1], centerX + ww / 2 - textSize, centerY + r * cos(angle / 2) + textSize + defaultTextPadding, paint)
        canvas.drawText(title[2], centerX - ww / 2 - textSize, centerY + r * cos(angle / 2) + textSize + defaultTextPadding, paint)

        //绘制覆盖分布区域图
        pathLine.reset()
        pathLine.moveTo(centerX.toFloat(), centerY - r * scaleList[0] / floorCount)
        pathLine.lineTo(centerX + r * scaleList[1] / floorCount * sin(angle / 2), centerY + r * scaleList[1] / floorCount * cos(angle / 2))
        pathLine.lineTo(centerX - r * scaleList[2] / floorCount * sin(angle / 2), centerY + r * scaleList[2] / floorCount * cos(angle / 2))
        pathLine.close()
        paint.color = areaColor
        canvas.drawPath(pathLine, paint)
    }

    /**
     * 四角分布器   正方形
     */
    private fun drawFourAngle(canvas: Canvas) {
        //四边形宽度，高度，边长
        val ww = mWidth - 2 * defaultTextPadding - textLength * textSize
        var hh = ww
        //中心点到各角的连线夹角
        val angle = (Math.PI * 2 / angleCount).toFloat()
        val pathLine = Path()
        //中心点X
        val centerX = width / 2
        //中心点Y
        val centerY = height / 2
        //中心点到各角的距离
        val r = ww / (2 * cos(angle / 2))
        //绘制边框
        if (isShowLine) {
            for (i in 1..floorCount) {
                val ur = r * i / floorCount
                pathLine.moveTo(centerX - ur * cos(angle / 2), centerY - ur * cos(angle / 2))
                pathLine.lineTo(centerX + ur * cos(angle / 2), centerY - ur * cos(angle / 2))
                pathLine.lineTo(centerX + ur * cos(angle / 2), centerY + ur * cos(angle / 2))
                pathLine.lineTo(centerX - ur * cos(angle / 2), centerY + ur * cos(angle / 2))
                pathLine.close()
            }
            canvas.drawPath(pathLine, paint)
        }
        //绘制线条
        if (isShowConnect) {
            pathLine.reset()
            pathLine.moveTo(centerX.toFloat(), centerY.toFloat())
            pathLine.lineTo(centerX - r * cos(angle / 2), centerY - r * cos(angle / 2))
            pathLine.moveTo(centerX.toFloat(), centerY.toFloat())
            pathLine.lineTo(centerX + r * cos(angle / 2), centerY - r * cos(angle / 2))
            pathLine.moveTo(centerX.toFloat(), centerY.toFloat())
            pathLine.lineTo(centerX + r * cos(angle / 2), centerY + r * cos(angle / 2))
            pathLine.moveTo(centerX.toFloat(), centerY.toFloat())
            pathLine.lineTo(centerX - r * cos(angle / 2), centerY + r * cos(angle / 2))
            canvas.drawPath(pathLine, paint)
        }
        //绘制文本
        paint.color = textColor
        paint.textSize = textSize
        paint.style = Paint.Style.FILL
        canvas.drawText(title[0], centerX - r * sin(angle / 2) - textSize, centerY - r * cos(angle / 2) - defaultTextPadding, paint)
        canvas.drawText(title[1], centerX + r * sin(angle / 2) - textSize, centerY - r * cos(angle / 2) - defaultTextPadding, paint)
        canvas.drawText(title[2], centerX + r * sin(angle / 2) - textSize, centerY + r * cos(angle / 2) + defaultTextPadding + textSize, paint)
        canvas.drawText(title[3], centerX - r * sin(angle / 2) - textSize, centerY + r * cos(angle / 2) + defaultTextPadding + textSize, paint)

        //绘制覆盖区域
        val pathArea = Path()
        pathArea.moveTo(centerX - r * scaleList[0] / floorCount * sin(angle / 2), centerY - r * scaleList[0] / floorCount * sin(angle / 2))
        pathArea.lineTo(centerX + r * scaleList[1] / floorCount * sin(angle / 2), centerY - r * scaleList[1] / floorCount * sin(angle / 2))
        pathArea.lineTo(centerX + r * scaleList[2] / floorCount * sin(angle / 2), centerY + r * scaleList[2] / floorCount * sin(angle / 2))
        pathArea.lineTo(centerX - r * scaleList[3] / floorCount * sin(angle / 2), centerY + r * scaleList[3] / floorCount * sin(angle / 2))
        pathArea.close()
        paint.color = areaColor
        canvas.drawPath(pathArea, paint)
    }

    /**
     * 五角分布器   正五边形
     */
    private fun drawFiveAngle(canvas: Canvas) {
        //五边形的宽高   顶点到底边的垂直距离
        val ww = mWidth - textLength * 2 * textSize - 2 * defaultTextPadding
        var hh = ww
        //中心点到各角的连线夹角
        val angle = (Math.PI * 2 / angleCount).toFloat()
        val pathLine = Path()
        //中心点X
        val centerX = width / 2
        //中心点Y
        val centerY = height / 2
        val r = ww / 2
        //画边框
        if (isShowLine) {
            for (i in 1..floorCount) {
                val ur = r * i / floorCount
                val x1 = centerX.toFloat()
                val y1 = centerY - ur
                val x2 = centerX + ur * sin(angle)
                val y2 = centerY - ur * cos(angle)
                val x3 = centerX + ur * sin(angle / 2)
                val y3 = centerY + ur * cos(angle / 2)
                val x4 = centerX - ur * sin(angle / 2)
                val y4 = centerY + ur * cos(angle / 2)
                val x5 = centerX - ur * sin(angle)
                val y5 = centerY - ur * cos(angle)
                pathLine.moveTo(x1, y1)
                pathLine.lineTo(x2, y2)
                pathLine.lineTo(x3, y3)
                pathLine.lineTo(x4, y4)
                pathLine.lineTo(x5, y5)
                pathLine.close()
            }
            canvas.drawPath(pathLine, paint)
        }
        //绘制连线
        if (isShowConnect) {
            pathLine.reset()
            pathLine.moveTo(centerX.toFloat(), centerY.toFloat())
            pathLine.lineTo(centerX.toFloat(), centerY - r)
            pathLine.moveTo(centerX.toFloat(), centerY.toFloat())
            pathLine.lineTo(centerX + r * sin(angle), centerY - r * cos(angle))
            pathLine.moveTo(centerX.toFloat(), centerY.toFloat())
            pathLine.lineTo(centerX + r * sin(angle / 2), centerY + r * cos(angle / 2))
            pathLine.moveTo(centerX.toFloat(), centerY.toFloat())
            pathLine.lineTo(centerX - r * sin(angle / 2), centerY + r * cos(angle / 2))
            pathLine.moveTo(centerX.toFloat(), centerY.toFloat())
            pathLine.lineTo(centerX - r * sin(angle), centerY - r * cos(angle))
            canvas.drawPath(pathLine, paint)
        }
        //画文本
        paint.color = textColor
        paint.textSize = textSize
        paint.style = Paint.Style.FILL
        canvas.drawText(title[0], centerX - textSize, centerY - r - defaultTextPadding, paint)
        canvas.drawText(title[1], centerX + r * sin(angle) + defaultTextPadding, centerY - cos(angle) * r + defaultTextPadding, paint)
        canvas.drawText(title[2], centerX + sin(angle / 2) * r - textSize, centerY + cos(angle / 2) * r + textSize + defaultTextPadding, paint)
        canvas.drawText(title[3], centerX - sin(angle / 2) * r - textSize, centerY + cos(angle / 2) * r + textSize + defaultTextPadding, paint)
        canvas.drawText(title[4], centerX - r * sin(angle) - defaultTextPadding - textSize * textLength, centerY - cos(angle) * r + defaultTextPadding, paint)

        //画区域
        val pathArea = Path()
        pathArea.moveTo(centerX + 0f, centerY - ww / 2 * scaleList[0] / floorCount)
        pathArea.lineTo(centerX + r * scaleList[1] / floorCount * sin(angle), centerY - r * scaleList[1] / floorCount * cos(angle))
        pathArea.lineTo(centerX + r * scaleList[2] / floorCount * sin(angle / 2), centerY + r * scaleList[2] / floorCount * cos(angle / 2))
        pathArea.lineTo(centerX - r * scaleList[3] / floorCount * sin(angle / 2), centerY + r * scaleList[3] / floorCount * cos(angle / 2))
        pathArea.lineTo(centerX - r * scaleList[4] / floorCount * sin(angle), centerY - r * scaleList[4] / floorCount * cos(angle))
        pathArea.close()
        paint.color = areaColor
        canvas.drawPath(pathArea, paint)
    }

    /**
     * 六角分布器  正六边形
     */
    private fun drawSixAngle(canvas: Canvas) {
        //六边形的宽   对角线的距离
        val ww = mWidth - textLength * 2 * textSize - 2 * defaultTextPadding
        //中心点到各角的连线夹角
        val angle = (Math.PI * 2 / angleCount).toFloat()
        //中心点到各角的距离
        val r = ww / 2
        //六边形的高   顶边到底边的距离
        var hh = 2 * r * sin(angle)
        val pathLine = Path()
        //中心点X
        val centerX = width / 2
        //中心点Y
        val centerY = height / 2
        //绘制边框
        if (isShowLine) {
            for (i in 1..floorCount) {
                val ur = r * i / floorCount
                pathLine.moveTo(centerX - ur * cos(angle), centerY - ur * sin(angle))
                pathLine.lineTo(centerX + ur * cos(angle), centerY - ur * sin(angle))
                pathLine.lineTo(centerX + ur, centerY.toFloat())
                pathLine.lineTo(centerX + ur * cos(angle), centerY + ur * sin(angle))
                pathLine.lineTo(centerX - ur * cos(angle), centerY + ur * sin(angle))
                pathLine.lineTo(centerX - ur, centerY.toFloat())
                pathLine.close()
            }
            canvas.drawPath(pathLine, paint)
        }
        //绘制连线
        if (isShowConnect) {
            pathLine.reset()
            pathLine.moveTo(centerX.toFloat(), centerY.toFloat())
            pathLine.lineTo(centerX - r * cos(angle), centerY - r * sin(angle))
            pathLine.moveTo(centerX.toFloat(), centerY.toFloat())
            pathLine.lineTo(centerX + r * cos(angle), centerY - r * sin(angle))
            pathLine.moveTo(centerX.toFloat(), centerY.toFloat())
            pathLine.lineTo(centerX + r, centerY.toFloat())
            pathLine.moveTo(centerX.toFloat(), centerY.toFloat())
            pathLine.lineTo(centerX + r * cos(angle), centerY + r * sin(angle))
            pathLine.moveTo(centerX.toFloat(), centerY.toFloat())
            pathLine.lineTo(centerX - r * cos(angle), centerY + r * sin(angle))
            pathLine.moveTo(centerX.toFloat(), centerY.toFloat())
            pathLine.lineTo(centerX - r, centerY.toFloat())
            canvas.drawPath(pathLine, paint)
        }
        //绘制文本
        paint.color = textColor
        paint.textSize = textSize
        paint.style = Paint.Style.FILL
        canvas.drawText(title[0], centerX - r * cos(angle) - textSize, centerY - r * sin(angle) - defaultTextPadding, paint)
        canvas.drawText(title[1], centerX + r * cos(angle) - textSize, centerY - r * sin(angle) - defaultTextPadding, paint)
        canvas.drawText(title[2], centerX + r + defaultTextPadding, centerY + textSize / 2, paint)
        canvas.drawText(title[3], centerX + r * cos(angle) - textSize, centerY + r * sin(angle) + textSize + defaultTextPadding, paint)
        canvas.drawText(title[4], centerX - r * cos(angle) - textSize, centerY + r * sin(angle) + textSize + defaultTextPadding, paint)
        canvas.drawText(title[5], centerX - r - textSize * textLength - defaultTextPadding, centerY + textSize / 2, paint)

        //绘制覆盖区域
        pathLine.reset()
        pathLine.moveTo(centerX - r * cos(angle) * scaleList[0] / floorCount, centerY - r * sin(angle) * scaleList[0] / floorCount)
        pathLine.lineTo(centerX + r * cos(angle) * scaleList[1] / floorCount, centerY - r * sin(angle) * scaleList[1] / floorCount)
        pathLine.lineTo(centerX + r * scaleList[2] / floorCount, centerY.toFloat())
        pathLine.lineTo(centerX + r * cos(angle) * scaleList[3] / floorCount, centerY + r * sin(angle) * scaleList[3] / floorCount)
        pathLine.lineTo(centerX - r * cos(angle) * scaleList[4] / floorCount, centerY + r * sin(angle) * scaleList[4] / floorCount)
        pathLine.lineTo(centerX - r * scaleList[5] / floorCount, centerY.toFloat())
        pathLine.close()
        paint.color = areaColor
        canvas.drawPath(pathLine, paint)
    }

    /**
     * 七角分布器   正七边形
     */
    private fun drawSevenAngle(canvas: Canvas) {
        //七边形的宽度
        val ww = mWidth - textLength * 2 * textSize - 2 * defaultTextPadding
        //七边形中心点链接各角的夹角
        val angle = (Math.PI * 2 / angleCount).toFloat()
        //七边形的边长
        val a = (ww / (1 + 2 * cos(Math.PI * 52 / 180))).toFloat()
        //七边形的顶角到底部的垂直距离
        var hh = ww * tan(Math.PI * 50 / 180) / 2 + a * sin(Math.PI * 52 / 180)
        //中心点到各角的距离
        val r = a / 2 / sin(angle / 2)
        //中心点X
        val centerX = (width / 2).toFloat()
        //中心点Y
        val ceterY = r + paddingTop + textSize + defaultTextPadding
        val pathLine = Path()
        //绘制边框
        if (isShowLine) {
            for (i in 1..floorCount) {
                val ur = r * i / floorCount
                pathLine.moveTo(centerX, ceterY - ur)
                pathLine.lineTo(centerX + ur * sin(angle), ceterY - ur * cos(angle))
                pathLine.lineTo(centerX + ur * sin(angle * 3 / 2), ceterY + ur * cos(angle * 3 / 2))
                pathLine.lineTo(centerX + ur * sin(angle / 2), ceterY + ur * cos(angle / 2))
                pathLine.lineTo(centerX - ur * sin(angle / 2), ceterY + ur * cos(angle / 2))
                pathLine.lineTo(centerX - ur * sin(angle * 3 / 2), ceterY + ur * cos(angle * 3 / 2))
                pathLine.lineTo(centerX - ur * sin(angle), ceterY - ur * cos(angle))
                pathLine.close()
            }
            canvas.drawPath(pathLine, paint)
        }
        //绘制连线
        if (isShowConnect) {
            pathLine.reset()
            pathLine.moveTo(centerX, ceterY)
            pathLine.lineTo(centerX, ceterY - r)
            pathLine.moveTo(centerX, ceterY)
            pathLine.lineTo(centerX + r * sin(angle), ceterY - r * cos(angle))
            pathLine.moveTo(centerX, ceterY)
            pathLine.lineTo(centerX + r * sin(angle * 3 / 2), ceterY + r * cos(angle * 3 / 2))
            pathLine.moveTo(centerX, ceterY)
            pathLine.lineTo(centerX + r * sin(angle / 2), ceterY + r * cos(angle / 2))
            pathLine.moveTo(centerX, ceterY)
            pathLine.lineTo(centerX - r * sin(angle / 2), ceterY + r * cos(angle / 2))
            pathLine.moveTo(centerX, ceterY)
            pathLine.lineTo(centerX - r * sin(angle * 3 / 2), ceterY + r * cos(angle * 3 / 2))
            pathLine.moveTo(centerX, ceterY)
            pathLine.lineTo(centerX - r * sin(angle), ceterY - r * cos(angle))
            canvas.drawPath(pathLine, paint)
        }
        //绘制文本
        paint.color = textColor
        paint.textSize = textSize
        paint.style = Paint.Style.FILL
        canvas.drawText(title[0], centerX - textSize, paddingTop + textSize, paint)
        canvas.drawText(title[1], centerX + r * sin(angle) + defaultTextPadding, ceterY - r * cos(angle) + textSize / 2, paint)
        canvas.drawText(title[2], centerX + r * sin(angle * 3 / 2) + defaultTextPadding, ceterY + r * cos(angle * 3 / 2) + textSize / 2, paint)
        canvas.drawText(title[3], centerX + r * sin(angle / 2) - textSize, ceterY + r * cos(angle / 2) + textSize + defaultTextPadding, paint)
        canvas.drawText(title[4], centerX - r * sin(angle / 2) - textSize, ceterY + r * cos(angle / 2) + textSize + defaultTextPadding, paint)
        canvas.drawText(title[5], centerX - r * sin(angle * 3 / 2) - textSize * textLength - defaultTextPadding, ceterY + r * cos(angle * 3 / 2) + textSize / 2, paint)
        canvas.drawText(title[6], centerX - r * sin(angle) - textSize * textLength - defaultTextPadding, ceterY - r * cos(angle) + textSize / 2, paint)

        //绘制区域
        pathLine.reset()
        pathLine.moveTo(centerX, ceterY - r * scaleList[0] / floorCount)
        pathLine.lineTo(centerX + r * sin(angle) * scaleList[1] / floorCount, ceterY - r * cos(angle) * scaleList[1] / floorCount)
        pathLine.lineTo(centerX + r * sin(angle * 3 / 2) * scaleList[2] / floorCount, ceterY + r * cos(angle * 3 / 2) * scaleList[2] / floorCount)
        pathLine.lineTo(centerX + r * sin(angle / 2) * scaleList[3] / floorCount, ceterY + r * cos(angle / 2) * scaleList[3] / floorCount)
        pathLine.lineTo(centerX - r * sin(angle / 2) * scaleList[4] / floorCount, ceterY + r * cos(angle / 2) * scaleList[4] / floorCount)
        pathLine.lineTo(centerX - r * sin(angle * 3 / 2) * scaleList[5] / floorCount, ceterY + r * cos(angle * 3 / 2) * scaleList[5] / floorCount)
        pathLine.lineTo(centerX - r * sin(angle) * scaleList[7] / floorCount, ceterY - r * cos(angle) * scaleList[7] / floorCount)
        pathLine.close()
        paint.color = areaColor
        canvas.drawPath(pathLine, paint)
    }

    /**
     * 八角分布器   正八边形
     */
    private fun drawEightAngle(canvas: Canvas) {
        //八边形的宽度
        val ww = mWidth - textLength * 2 * textSize - 2 * defaultTextPadding
        var hh = ww
        //八边形中心点链接各角的夹角
        val angle = (Math.PI * 2 / angleCount).toFloat()
        //中心点到各角的距离
        val r = ww / 2 / cos(angle / 2)
        //中心点x
        val centerX = (width / 2).toFloat()
        //中心点y
        val centerY = (height / 2).toFloat()
        val pathLine = Path()
        //绘制边框
        if (isShowLine) {
            for (i in 1..floorCount) {
                val ur = r * i / floorCount
                pathLine.moveTo(centerX - ur * sin(angle / 2), centerY - ur * cos(angle / 2))
                pathLine.lineTo(centerX + ur * sin(angle / 2), centerY - ur * cos(angle / 2))
                pathLine.lineTo(centerX + ur * cos(angle / 2), centerY - ur * sin(angle / 2))
                pathLine.lineTo(centerX + ur * cos(angle / 2), centerY + ur * sin(angle / 2))
                pathLine.lineTo(centerX + ur * sin(angle / 2), centerY + ur * cos(angle / 2))
                pathLine.lineTo(centerX - ur * sin(angle / 2), centerY + ur * cos(angle / 2))
                pathLine.lineTo(centerX - ur * cos(angle / 2), centerY + ur * sin(angle / 2))
                pathLine.lineTo(centerX - ur * cos(angle / 2), centerY - ur * sin(angle / 2))
                pathLine.close()
            }
            canvas.drawPath(pathLine, paint)
        }
        //绘制连线
        if (isShowConnect) {
            pathLine.reset()
            pathLine.moveTo(centerX, centerY)
            pathLine.lineTo(centerX - r * sin(angle / 2), centerY - r * cos(angle / 2))
            pathLine.moveTo(centerX, centerY)
            pathLine.lineTo(centerX + r * sin(angle / 2), centerY - r * cos(angle / 2))
            pathLine.moveTo(centerX, centerY)
            pathLine.lineTo(centerX + r * cos(angle / 2), centerY - r * sin(angle / 2))
            pathLine.moveTo(centerX, centerY)
            pathLine.lineTo(centerX + r * cos(angle / 2), centerY + r * sin(angle / 2))
            pathLine.moveTo(centerX, centerY)
            pathLine.lineTo(centerX + r * sin(angle / 2), centerY + r * cos(angle / 2))
            pathLine.moveTo(centerX, centerY)
            pathLine.lineTo(centerX - r * sin(angle / 2), centerY + r * cos(angle / 2))
            pathLine.moveTo(centerX, centerY)
            pathLine.lineTo(centerX - r * cos(angle / 2), centerY + r * sin(angle / 2))
            pathLine.moveTo(centerX, centerY)
            pathLine.lineTo(centerX - r * cos(angle / 2), centerY - r * sin(angle / 2))
            canvas.drawPath(pathLine, paint)
        }
        //绘制文本
        paint.color = textColor
        paint.textSize = textSize
        paint.style = Paint.Style.FILL
        canvas.drawText(title[0], centerX - r * sin(angle / 2) - textSize, centerY - r * cos(angle / 2) - defaultTextPadding, paint)
        canvas.drawText(title[1], centerX + r * sin(angle / 2) - textSize, centerY - r * cos(angle / 2) - defaultTextPadding, paint)
        canvas.drawText(title[2], centerX + r * cos(angle / 2) + defaultTextPadding, centerY - r * sin(angle / 2) + textSize / 2, paint)
        canvas.drawText(title[3], centerX + r * cos(angle / 2) + defaultTextPadding, centerY + r * sin(angle / 2) + textSize / 2, paint)
        canvas.drawText(title[4], centerX + r * sin(angle / 2) - textSize, centerY + r * cos(angle / 2) + defaultTextPadding + textSize, paint)
        canvas.drawText(title[5], centerX - r * sin(angle / 2) - textSize, centerY + r * cos(angle / 2) + defaultTextPadding + textSize, paint)
        canvas.drawText(title[6], centerX - r * cos(angle / 2) - textSize * textLength - defaultTextPadding, centerY + r * sin(angle / 2) + textSize / 2, paint)
        canvas.drawText(title[7], centerX - r * cos(angle / 2) - textSize * textLength - defaultTextPadding, centerY - r * sin(angle / 2) + textSize / 2, paint)

        //绘制区域
        pathLine.reset()
        pathLine.moveTo(centerX - r * sin(angle / 2) * scaleList[0] / floorCount, centerY - r * cos(angle / 2) * scaleList[0] / floorCount)
        pathLine.lineTo(centerX + r * sin(angle / 2) * scaleList[1] / floorCount, centerY - r * cos(angle / 2) * scaleList[1] / floorCount)
        pathLine.lineTo(centerX + r * cos(angle / 2) * scaleList[2] / floorCount, centerY - r * sin(angle / 2) * scaleList[2] / floorCount)
        pathLine.lineTo(centerX + r * cos(angle / 2) * scaleList[3] / floorCount, centerY + r * sin(angle / 2) * scaleList[3] / floorCount)
        pathLine.lineTo(centerX + r * sin(angle / 2) * scaleList[4] / floorCount, centerY + r * cos(angle / 2) * scaleList[4] / floorCount)
        pathLine.lineTo(centerX - r * sin(angle / 2) * scaleList[5] / floorCount, centerY + r * cos(angle / 2) * scaleList[5] / floorCount)
        pathLine.lineTo(centerX - r * cos(angle / 2) * scaleList[6] / floorCount, centerY + r * sin(angle / 2) * scaleList[6] / floorCount)
        pathLine.lineTo(centerX - r * cos(angle / 2) * scaleList[7] / floorCount, centerY - r * sin(angle / 2) * scaleList[7] / floorCount)
        pathLine.close()
        paint.color = areaColor
        canvas.drawPath(pathLine, paint)
    }
}