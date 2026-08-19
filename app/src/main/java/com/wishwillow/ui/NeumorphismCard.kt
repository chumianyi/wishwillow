package com.wishwillow.ui
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout
class NeumorphismCard @JvmOverloads constructor(c: Context, a: AttributeSet?=null, s: Int=0):FrameLayout(c,a,s){
    private val bg=Color.parseColor("#E8ECEF"); private val dark=Color.parseColor("#A3B1C2"); private val light=Color.parseColor("#FFFFFF")
    private val paint=Paint(Paint.ANTI_ALIAS_FLAG); private val rect=RectF(); private val radius=32f
    init{setBackgroundColor(Color.TRANSPARENT);setPadding(40,40,40,40)}
    override fun onDraw(canvas:Canvas){val w=width.toFloat();val h=height.toFloat();rect.set(12f,12f,w-12f,h-12f)
        paint.color=dark;paint.setShadowLayer(16f,8f,8f,dark);canvas.drawRoundRect(rect,radius,radius,paint)
        paint.color=light;paint.setShadowLayer(16f,-8f,-8f,light);canvas.drawRoundRect(rect,radius,radius,paint)
        paint.color=bg;paint.setShadowLayer(0f,0f,0f,Color.TRANSPARENT);canvas.drawRoundRect(rect,radius,radius,paint);super.onDraw(canvas)}
}
