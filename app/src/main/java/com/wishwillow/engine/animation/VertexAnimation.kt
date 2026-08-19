package com.wishwillow.engine.animation
import com.wishwillow.engine.math.Vec3
import com.wishwillow.engine.render.Mesh
class VertexAnimation(val mesh: Mesh) {
    private var base = FloatArray(0); private var anim = FloatArray(0); var time = 0f; var amp = 0.1f; var freq = 2f
    fun setBase(v: FloatArray) { base = v.copyOf(); anim = v.copyOf() }
    fun update(dt: Float, bend: Float = 0f, bendDir: Vec3 = Vec3(1f,0f,0f)) {
        time += dt; if(base.isEmpty()) return
        val cnt = base.size / 3
        for(i in 0 until cnt) {
            val bx=base[i*3]; val by=base[i*3+1]; val bz=base[i*3+2]
            val hf = ((by+2f).coerceIn(0f,4f))/4f
            val wave = kotlin.math.sin(time*freq+by*2f)*amp*hf
            val b = bend*hf*hf
            anim[i*3]=bx+bendDir.x*b+wave*bendDir.x
            anim[i*3+1]=by
            anim[i*3+2]=bz+bendDir.z*b+wave*bendDir.z
        }
        mesh.updateVerts(anim)
    }
    fun reset() { if(base.isNotEmpty()) mesh.updateVerts(base) }
}
