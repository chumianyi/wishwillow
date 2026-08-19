package com.wishwillow.engine.math
import kotlin.math.sqrt
data class Vec3(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f) {
    fun set(x: Float, y: Float, z: Float) { this.x = x; this.y = y; this.z = z }
    fun set(v: Vec3) { x = v.x; y = v.y; z = v.z }
    operator fun plus(v: Vec3) = Vec3(x + v.x, y + v.y, z + v.z)
    operator fun minus(v: Vec3) = Vec3(x - v.x, y - v.y, z - v.z)
    operator fun times(s: Float) = Vec3(x * s, y * s, z * s)
    fun add(v: Vec3): Vec3 { x += v.x; y += v.y; z += v.z; return this }
    fun sub(v: Vec3): Vec3 { x -= v.x; y -= v.y; z -= v.z; return this }
    fun mul(s: Float): Vec3 { x *= s; y *= s; z *= s; return this }
    fun dot(v: Vec3) = x * v.x + y * v.y + z * v.z
    fun cross(v: Vec3) = Vec3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)
    fun length() = sqrt(x * x + y * y + z * z)
    fun normalize(): Vec3 { val l = length(); if (l > 1e-6f) { x /= l; y /= l; z /= l }; return this }
    fun normalized() = Vec3(this).normalize()
    fun lerp(target: Vec3, t: Float): Vec3 { val i = 1f - t; return Vec3(x * i + target.x * t, y * i + target.y * t, z * i + target.z * t) }
    companion object { val UP = Vec3(0f, 1f, 0f); val ZERO = Vec3(0f, 0f, 0f) }
}
