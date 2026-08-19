package com.wishwillow.engine.render
import android.opengl.GLES30
import android.util.Log
class Shader(private val vs: String, private val fs: String) {
    var programId = 0; private set
    fun compile() {
        val v = compile(GLES30.GL_VERTEX_SHADER, vs)
        val f = compile(GLES30.GL_FRAGMENT_SHADER, fs)
        programId = GLES30.glCreateProgram()
        GLES30.glAttachShader(programId, v); GLES30.glAttachShader(programId, f)
        GLES30.glLinkProgram(programId)
        val s = IntArray(1); GLES30.glGetProgramiv(programId, GLES30.GL_LINK_STATUS, s, 0)
        if (s[0] != GLES30.GL_TRUE) Log.e("Shader", "Link: ${GLES30.glGetProgramInfoLog(programId)}")
        GLES30.glDeleteShader(v); GLES30.glDeleteShader(f)
    }
    private fun compile(type: Int, src: String): Int {
        val sh = GLES30.glCreateShader(type); GLES30.glShaderSource(sh, src); GLES30.glCompileShader(sh)
        val s = IntArray(1); GLES30.glGetShaderiv(sh, GLES30.GL_COMPILE_STATUS, s, 0)
        if (s[0] != GLES30.GL_TRUE) Log.e("Shader", "Compile($type): ${GLES30.glGetShaderInfoLog(sh)}")
        return sh
    }
    fun use() = GLES30.glUseProgram(programId)
    fun uloc(n: String) = GLES30.glGetUniformLocation(programId, n)
    fun setMat4(n: String, v: FloatArray) = GLES30.glUniformMatrix4fv(uloc(n), 1, false, v, 0)
    fun setVec3(n: String, x: Float, y: Float, z: Float) = GLES30.glUniform3f(uloc(n), x, y, z)
    fun setFloat(n: String, v: Float) = GLES30.glUniform1f(uloc(n), v)
    fun setInt(n: String, v: Int) = GLES30.glUniform1i(uloc(n), v)
}
