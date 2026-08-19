package com.wishwillow.ui
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatButton
class NeumorphismButton @JvmOverloads constructor(c: Context, a: AttributeSet?=null, s: Int=0):AppCompatButton(c,a,s){
    private val bg=Color.parseColor("#E8ECEF"); private val dark=Color.parseColor("#A3B1C2"); private val light=Color.parseColor("#FFFFFF")
    private val paint=Paint(Paint.ANTI_ALIAS_FLAG); private val rect=RectF(); private var pressed=false; private val radius=24f
    var accent=Color.parseColor("#4A90D9"); set(v){field=v;invalidate()}
    init{setBackgroundColor(Color.TRANSPARENT);setPadding(48,28,48,28);setTextColor(Color.parseColor("#2D3748"));textSize=16f}
    override fun onDraw(canvas:Canvas){val w=width.toFloat();val h=height.toFloat();rect.set(8f,8f,w-8f,h-8f)
        if(!pressed){paint.color=dark;paint.setShadowLayer(12f,6f,6f,dark);canvas.drawRoundRect(rect,radius,radius,paint);paint.color=light;paint.setShadowLayer(12f,-6f,-6f,light);canvas.drawRoundRect(rect,radius,radius,paint)}
        paint.color=bg;paint.setShadowLayer(0f,0f,0f,Color.TRANSPARENT);canvas.drawRoundRect(rect,radius,radius,paint)
        paint.color=accent;paint.textSize=textSize;paint.isFakeBoldText=true;paint.textAlign=Paint.Align.CENTER;canvas.drawText(text.toString(),w/2f,h/2f-(paint.descent()+paint.ascent())/2f,paint)}
    override fun onTouchEvent(e:MotionEvent):Boolean{when(e.action){MotionEvent.ACTION_DOWN->{pressed=true;invalidate()}MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL->{pressed=false;invalidate()}};return super.onTouchEvent(e)}
}
