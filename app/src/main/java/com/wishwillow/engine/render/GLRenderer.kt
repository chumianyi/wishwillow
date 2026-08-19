package com.wishwillow.engine.render
import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.wishwillow.engine.math.Mat4
import com.wishwillow.engine.math.Vec3
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
class GLRenderer(private val ctx: Context) : GLSurfaceView.Renderer {
    lateinit var phong: Shader; lateinit var particle: Shader; lateinit var sky: Shader
    lateinit var camera: Camera; lateinit var light: Light; lateinit var barkTex: Texture; lateinit var skyMesh: Mesh
    var scene: Renderable? = null; var time = 0f; private var last = 0L
    interface Renderable { fun init(r: GLRenderer); fun update(dt: Float, r: GLRenderer); fun render(r: GLRenderer) }
    override fun onSurfaceCreated(gl: GL10?, cfg: EGLConfig?) {
        GLES30.glClearColor(0.72f,0.83f,0.91f,1f); GLES30.glEnable(GLES30.GL_DEPTH_TEST); GLES30.glEnable(GLES30.GL_BLEND); GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA,GLES30.GL_ONE_MINUS_SRC_ALPHA); GLES30.glEnable(GLES30.GL_CULL_FACE); GLES30.glCullFace(GLES30.GL_BACK)
        phong=Shader(load("shaders/phong.vert"),load("shaders/phong.frag")); phong.compile()
        particle=Shader(load("shaders/particle.vert"),load("shaders/particle.frag")); particle.compile()
        sky=Shader(load("shaders/sky.vert"),load("shaders/sky.frag")); sky.compile()
        camera=Camera(); light=Light(); barkTex=Texture.bark(); skyMesh=MeshGenerator.skybox(); skyMesh.upload()
        scene?.init(this); last=System.nanoTime()
    }
    override fun onSurfaceChanged(gl: GL10?, w: Int, h: Int) { GLES30.glViewport(0,0,w,h); camera.aspect=w.toFloat()/h.toFloat() }
    override fun onDrawFrame(gl: GL10?) {
        val now=System.nanoTime(); val dt=((now-last)/1e9f).coerceAtMost(0.05f); last=now; time+=dt
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT); camera.update()
        scene?.update(dt,this); renderSky(); scene?.render(this)
    }
    private fun renderSky() {
        GLES30.glDisable(GLES30.GL_DEPTH_TEST); GLES30.glDisable(GLES30.GL_CULL_FACE)
        sky.use(); sky.setMat4("uProjection",camera.getProj().toFloatArray()); sky.setMat4("uView",camera.getView().toFloatArray())
        sky.setVec3("uTopColor",0.72f,0.83f,0.91f); sky.setVec3("uBottomColor",0.91f,0.94f,0.91f); sky.setVec3("uSunDir",-0.5f,1f,-0.3f)
        skyMesh.draw(); GLES30.glEnable(GLES30.GL_DEPTH_TEST); GLES30.glEnable(GLES30.GL_CULL_FACE)
    }
    fun bindPhong(model: Mat4, tint: Vec3=Vec3(1f,1f,1f), alpha: Float=1f, useTex: Int=0) {
        phong.use(); phong.setMat4("uModel",model.toFloatArray()); phong.setMat4("uView",camera.getView().toFloatArray()); phong.setMat4("uProjection",camera.getProj().toFloatArray())
        val nm=floatArrayOf(model.m[0],model.m[1],model.m[2],model.m[4],model.m[5],model.m[6],model.m[8],model.m[9],model.m[10])
        GLES30.glUniformMatrix3fv(phong.uloc("uNormalMatrix"),1,false,nm,0)
        phong.setVec3("uLightDir",light.direction.x,light.direction.y,light.direction.z)
        phong.setVec3("uAmbient",light.ambient.x,light.ambient.y,light.ambient.z)
        phong.setVec3("uDiffuse",light.diffuse.x,light.diffuse.y,light.diffuse.z)
        phong.setVec3("uSpecular",light.specular.x,light.specular.y,light.specular.z)
        phong.setFloat("uShininess",light.shininess); phong.setVec3("uCameraPos",camera.position.x,camera.position.y,camera.position.z)
        phong.setVec3("uTint",tint.x,tint.y,tint.z); phong.setFloat("uAlpha",alpha); phong.setInt("uUseTexture",useTex)
        if(useTex==1){barkTex.bind(0); phong.setInt("uTexture",0)}
    }
    private fun load(p: String): String { val s=ctx.assets.open(p); val r=BufferedReader(InputStreamReader(s)); val sb=StringBuilder(); var l:String?; while(r.readLine().also{l=it}!=null) sb.append(l).append("\n"); r.close(); return sb.toString() }
}
