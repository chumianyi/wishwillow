package com.wishwillow.engine.math
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
data class Quat(var x:Float=0f,var y:Float=0f,var z:Float=0f,var w:Float=1f){
    fun normalize():Quat{val l=sqrt(x*x+y*y+z*z+w*w);if(l>1e-6f){x/=l;y/=l;z/=l;w/=l};return this}
    fun toMat4():Mat4{val xx=x*x;val yy=y*y;val zz=z*z;val xy=x*y;val xz=x*z;val yz=y*z;val wx=w*x;val wy=w*y;val wz=w*z;return Mat4(FloatArray(16).apply{this[0]=1f-2f*(yy+zz);this[1]=2f*(xy-wz);this[2]=2f*(xz+wy);this[4]=2f*(xy+wz);this[5]=1f-2f*(xx+zz);this[6]=2f*(yz-wx);this[8]=2f*(xz-wy);this[9]=2f*(yz+wx);this[10]=1f-2f*(xx+yy);this[15]=1f})}
    companion object{fun fromAxisAngle(axis:Vec3,angle:Float):Quat{val h=angle/2f;val s=sin(h);val a=axis.normalized();return Quat(a.x*s,a.y*s,a.z*s,cos(h))}}
}
