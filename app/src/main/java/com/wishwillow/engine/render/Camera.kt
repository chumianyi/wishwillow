package com.wishwillow.engine.render
import com.wishwillow.engine.math.Mat4
import com.wishwillow.engine.math.Vec3
import kotlin.math.cos
import kotlin.math.sin
class Camera {
    var position = Vec3(0f, 3.5f, 7f)
    var target = Vec3(0f, 2f, 0f)
    var up = Vec3(0f, 1f, 0f)
    var fov = 60f; var near = 0.1f; var far = 100f; var aspect = 1f
    private var view = Mat4.identity(); private var proj = Mat4.identity(); private var vp = Mat4.identity()
    fun update() { view = Mat4.lookAt(position, target, up); proj = Mat4.perspective(fov*Math.PI.toFloat()/180f, aspect, near, far); vp = proj.multiply(view) }
    fun getView() = view; fun getProj() = proj; fun getVP() = vp
    fun orbit(a: Float, r: Float, h: Float) { position.x = target.x + sin(a)*r; position.z = target.z + cos(a)*r; position.y = target.y + h }
}
