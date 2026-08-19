package com.wishwillow.engine.math
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan
class Mat4 private constructor(val m: FloatArray) {
    constructor() : this(FloatArray(16).also { it[0]=1f; it[5]=1f; it[10]=1f; it[15]=1f })
    fun multiply(o: Mat4): Mat4 { val a=m; val b=o.m; val r=FloatArray(16); for(i in 0..3) for(j in 0..3){var s=0f; for(k in 0..3) s+=a[i*4+k]*b[k*4+j]; r[i*4+j]=s}; return Mat4(r) }
    fun transpose(): Mat4 { val r=FloatArray(16); for(i in 0..3) for(j in 0..3) r[i*4+j]=m[j*4+i]; return Mat4(r) }
    companion object {
        fun identity()=Mat4()
        fun translation(x:Float,y:Float,z:Float)=Mat4().apply{m[3]=x;m[7]=y;m[11]=z}
        fun scaling(x:Float,y:Float,z:Float)=Mat4().apply{m[0]=x;m[5]=y;m[10]=z}
        fun rotation(angle:Float,axis:Vec3):Mat4{val c=cos(angle);val s=sin(angle);val t=1f-c;val ax=axis.normalized();val x=ax.x;val y=ax.y;val z=ax.z;return Mat4(FloatArray(16).apply{this[0]=t*x*x+c;this[1]=t*x*y-s*z;this[2]=t*x*z+s*y;this[4]=t*x*y+s*z;this[5]=t*y*y+c;this[6]=t*y*z-s*x;this[8]=t*x*z-s*y;this[9]=t*y*z+s*x;this[10]=t*z*z+c;this[15]=1f})}
        fun perspective(fovY:Float,aspect:Float,near:Float,far:Float):Mat4{val f=1f/tan(fovY/2f);val nf=1f/(near-far);return Mat4(FloatArray(16).apply{this[0]=f/aspect;this[5]=f;this[10]=(far+near)*nf;this[11]=2f*far*near*nf;this[14]=-1f})}
        fun lookAt(eye:Vec3,center:Vec3,up:Vec3):Mat4{val f=(center-eye).normalize();val s=f.cross(up).normalize();val u=s.cross(f);return Mat4(FloatArray(16).apply{this[0]=s.x;this[1]=s.y;this[2]=s.z;this[3]=-s.dot(eye);this[4]=u.x;this[5]=u.y;this[6]=u.z;this[7]=-u.dot(eye);this[8]=-f.x;this[9]=-f.y;this[10]=-f.z;this[11]=f.dot(eye);this[15]=1f})}
    }
}
