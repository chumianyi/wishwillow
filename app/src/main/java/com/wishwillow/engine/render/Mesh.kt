package com.wishwillow.engine.render
import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder
class Mesh {
    var vertices = FloatArray(0); var normals = FloatArray(0); var texCoords = FloatArray(0); var indices = IntArray(0); var colors = FloatArray(0)
    private var vao = IntArray(1); private var vbo = IntArray(4); private var ibo = IntArray(1); private var count = 0; private var init = false
    fun upload() {
        if(init) delete()
        count = indices.size
        GLES30.glGenVertexArrays(1, vao, 0); GLES30.glBindVertexArray(vao[0]); GLES30.glGenBuffers(4, vbo, 0)
        uploadBuf(0, vertices, 3); uploadBuf(1, normals, 3); uploadBuf(2, texCoords, 2); uploadBuf(3, colors, 4)
        if(indices.isNotEmpty()){GLES30.glGenBuffers(1,ibo,0);GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,ibo[0]);val b=ByteBuffer.allocateDirect(indices.size*4).order(ByteOrder.nativeOrder()).asIntBuffer().put(indices);b.position(0);GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,indices.size*4L,b,GLES30.GL_STATIC_DRAW)}
        GLES30.glBindVertexArray(0); init = true
    }
    private fun uploadBuf(i: Int, d: FloatArray, s: Int) {
        if(d.isEmpty()) return
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[i])
        val b=ByteBuffer.allocateDirect(d.size*4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(d); b.position(0)
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, d.size*4L, b, GLES30.GL_DYNAMIC_DRAW)
        GLES30.glEnableVertexAttribArray(i); GLES30.glVertexAttribPointer(i, s, GLES30.GL_FLOAT, false, 0, 0)
    }
    fun updateVerts(d: FloatArray) { vertices=d; if(!init) return; GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,vbo[0]); val b=ByteBuffer.allocateDirect(d.size*4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(d); b.position(0); GLES30.glBufferSubData(GLES30.GL_ARRAY_BUFFER,0,d.size*4L,b) }
    fun draw() { if(!init) return; GLES30.glBindVertexArray(vao[0]); if(indices.isNotEmpty()) GLES30.glDrawElements(GLES30.GL_TRIANGLES,count,GLES30.GL_UNSIGNED_INT,0) else GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0,vertices.size/3); GLES30.glBindVertexArray(0) }
    fun delete() { if(init){GLES30.glDeleteVertexArrays(1,vao,0);GLES30.glDeleteBuffers(4,vbo,0);init=false} }
}
