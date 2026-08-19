package com.wishwillow.engine.render
import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder
class Texture {
    var textureId = 0; private set
    fun create(pixels: ByteArray, w: Int, h: Int) {
        val ids = IntArray(1); GLES30.glGenTextures(1, ids, 0); textureId = ids[0]
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
        val buf = ByteBuffer.allocateDirect(pixels.size).order(ByteOrder.nativeOrder()).put(pixels); buf.position(0)
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, w, h, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, buf)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D); GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
    }
    fun bind(unit: Int = 0) { GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + unit); GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId) }
    companion object {
        fun bark(): Texture {
            val w=128; val h=128; val p=ByteArray(w*h*4); val rnd=java.util.Random(42)
            for(y in 0 until h) for(x in 0 until w){val i=(y*w+x)*4;val n=(rnd.nextFloat()*30f-15f).toInt();val v=(kotlin.math.sin(x*0.3f)*15f).toInt();p[i]=(107+n+v).toByte();p[i+1]=(68+n/2+v/2).toByte();p[i+2]=(35+n/3).toByte();p[i+3]=255.toByte()}
            return Texture().apply{create(p,w,h)}
        }
        fun particle(): Texture {
            val w=32;val h=32;val p=ByteArray(w*h*4)
            for(y in 0 until h) for(x in 0 until w){val i=(y*w+x)*4;val dx=x-w/2f;val dy=y-h/2f;val d=kotlin.math.sqrt(dx*dx+dy*dy)/(w/2f);val a=((1f-d.coerceIn(0f,1f))*255f).toInt();p[i]=255.toByte();p[i+1]=255.toByte();p[i+2]=255.toByte();p[i+3]=a.toByte()}
            return Texture().apply{create(p,w,h)}
        }
    }
}
