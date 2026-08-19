package com.wishwillow.ui
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
class NeumorphismEditText @JvmOverloads constructor(c: Context, a: AttributeSet?=null, s: Int=0):AppCompatEditText(c,a,s){
    private val bg=Color.parseColor("#E8ECEF"); private val dark=Color.parseColor("#A3B1C2"); private val light=Color.parseColor("#FFFFFF")
    private val paint=Paint(Paint.ANTI_ALIAS_FLAG); private val rect=RectF(); private val radius=20f
    init{setBackgroundColor(Color.TRANSPARENT);setPadding(40,32,40,32);setTextColor(Color.parseColor("#2D3748"));setHintTextColor(Color.parseColor("#718096"));textSize=16f;background=null}
    override fun onDraw(canvas:Canvas){val w=width.toFloat();val h=height.toFloat();rect.set(6f,6f,w-6f,h-6f)
        paint.color=dark;paint.setShadowLayer(8f,4f,4f,dark);canvas.drawRoundRect(rect,radius,radius,paint)
        paint.color=light;paint.setShadowLayer(8f,-4f,-4f,light);canvas.drawRoundRect(rect,radius,radius,paint)
        paint.color=bg;paint.setShadowLayer(0f,0f,0f,Color.TRANSPARENT);canvas.drawRoundRect(rect,radius,radius,paint)
        canvas.save();canvas.translate(0f,4f);super.onDraw(canvas);canvas.restore()}
}
