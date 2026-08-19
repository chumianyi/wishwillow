package com.wishwillow.engine.render
import android.opengl.GLES30
import com.wishwillow.engine.math.Vec3
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.random.Random
class ParticleSystem(val max: Int = 500) {
    data class P(var pos: Vec3=Vec3(),var vel:Vec3=Vec3(),var col:Vec3=Vec3(1f,1f,1f),var life:Float=0f,var maxLife:Float=1f,var size:Float=0.1f,var active:Boolean=false)
    val particles=Array(max){P()}
    var gravity=-2f; var spread=1f
    private var posData=FloatArray(max*3);private var colData=FloatArray(max*4);private var sizeData=FloatArray(max)
    private var vao=IntArray(1);private var vbo=IntArray(3);private var init=false;private var activeCount=0
    private val rnd=Random(System.currentTimeMillis())
    fun emit(count:Int,basePos:Vec3,baseVel:Vec3,color:Vec3,life:Float,size:Float){var e=0;for(p in particles){if(e>=count)break;if(!p.active){p.active=true;p.pos.set(basePos);p.vel.set(baseVel.x+(rnd.nextFloat()-0.5f)*spread,baseVel.y+rnd.nextFloat()*spread*0.5f,baseVel.z+(rnd.nextFloat()-0.5f)*spread);p.col.set(color);p.life=life;p.maxLife=life;p.size=size*(0.7f+rnd.nextFloat()*0.6f);e++}}}
    fun update(dt:Float){for(p in particles){if(!p.active)continue;p.life-=dt;if(p.life<=0f){p.active=false;continue};p.vel.y+=gravity*dt;p.pos.add(p.vel*dt)}}
    fun build(){var cnt=0;for(p in particles){if(!p.active)continue;val a=(p.life/p.maxLife).coerceIn(0f,1f);posData[cnt*3]=p.pos.x;posData[cnt*3+1]=p.pos.y;posData[cnt*3+2]=p.pos.z;colData[cnt*4]=p.col.x;colData[cnt*4+1]=p.col.y;colData[cnt*4+2]=p.col.z;colData[cnt*4+3]=a;sizeData[cnt]=p.size;cnt++}
        if(!init){GLES30.glGenVertexArrays(1,vao,0);GLES30.glBindVertexArray(vao[0]);GLES30.glGenBuffers(3,vbo,0);for(i in 0..2){GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,vbo[i]);val sz=when(i){0->max*3*4;1->max*4*4;else->max*4};GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,sz.toLong(),null,GLES30.GL_DYNAMIC_DRAW);val comp=when(i){0->3;1->4;else->1};GLES30.glEnableVertexAttribArray(i);GLES30.glVertexAttribPointer(i,comp,GLES30.GL_FLOAT,false,0,0)};GLES30.glBindVertexArray(0);init=true}
        updBuf(0,posData,cnt*3);updBuf(1,colData,cnt*4);updBuf(2,sizeData,cnt);activeCount=cnt}
    private fun updBuf(i:Int,d:FloatArray,cnt:Int){if(cnt<=0)return;GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,vbo[i]);val b=ByteBuffer.allocateDirect(cnt*4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(d,0,cnt);b.position(0);GLES30.glBufferSubData(GLES30.GL_ARRAY_BUFFER,0,cnt*4L,b)}
    fun draw(){if(activeCount<=0)return;GLES30.glBindVertexArray(vao[0]);GLES30.glDrawArrays(GLES30.GL_POINTS,0,activeCount);GLES30.glBindVertexArray(0)}
    fun reset(){for(p in particles)p.active=false}
}
