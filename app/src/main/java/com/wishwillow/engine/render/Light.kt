package com.wishwillow.engine.render
import com.wishwillow.engine.math.Vec3
class Light {
    var direction = Vec3(-0.5f, -1f, -0.3f).normalize()
    var ambient = Vec3(0.3f, 0.32f, 0.35f)
    var diffuse = Vec3(0.9f, 0.88f, 0.82f)
    var specular = Vec3(1f, 1f, 1f)
    var shininess = 32f
}
