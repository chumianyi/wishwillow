package com.wishwillow.game
import android.opengl.GLES30
import com.wishwillow.engine.math.Mat4
import com.wishwillow.engine.math.Vec3
import com.wishwillow.engine.render.GLRenderer
import com.wishwillow.engine.render.MeshGenerator
import com.wishwillow.engine.render.ParticleSystem
import kotlin.math.sin
enum class GameState { IDLE, WISHING, REACHING, BREAKING, RESULT }
enum class WishResult { SUCCESS_WHITE, SUCCESS_RED, FAILED }
class GameScene : GLRenderer.Renderable {
    lateinit var willow: WillowTree; lateinit var hand: Mesh; lateinit var particles: ParticleSystem; lateinit var glow: ParticleSystem
    var state=GameState.IDLE; var result=WishResult.SUCCESS_WHITE; var wishText=""; var stateTime=0f
    var selected: WillowBranch?=null; var handPos=Vec3(0f,-2f,3f); var handTarget=Vec3(); var handRot=0f
    var resultGlow=0f; var camAngle=0f; var shake=0f; var breakForce=0f; var breakThresh=0.7f
    var onResult: ((WishResult,String)->Unit)? = null
    override fun init(r: GLRenderer) { willow=WillowTree(); willow.generate(); hand=MeshGenerator.hand(); hand.upload(); particles=ParticleSystem(800); glow=ParticleSystem(300); r.camera.position.set(0f,3.5f,7f); r.camera.target.set(0f,2f,0f) }
    override fun update(dt: Float, r: GLRenderer) {
        stateTime+=dt; willow.update(dt); particles.update(dt); glow.update(dt)
        camAngle+=dt*0.15f; val cr=7f+sin(stateTime*0.3f)*0.3f; r.camera.position.x=sin(camAngle)*cr; r.camera.position.z=kotlin.math.cos(camAngle)*cr; r.camera.position.y=3.5f+sin(stateTime*0.5f)*0.2f+shake; r.camera.target.y=2f+shake*0.5f; shake*=0.9f
        when(state){
            GameState.IDLE-> handPos.y=-2f+sin(stateTime*2f)*0.1f
            GameState.REACHING-> selected?.let{ b-> handTarget=b.tipPos(); handTarget.y-=0.3f; val t=(stateTime/1.5f).coerceAtMost(1f); val e=t*t*(3f-2f*t); handPos=Vec3(0f,-2f,3f).lerp(handTarget,e); handRot=sin(stateTime*5f)*0.2f; if(t>=1f){state=GameState.BREAKING;stateTime=0f;breakForce=0f} }
            GameState.BREAKING-> selected?.let{ b-> breakForce+=dt*0.8f; b.bendAmount=breakForce*0.8f; shake=sin(stateTime*40f)*0.05f*breakForce; handRot=sin(stateTime*8f)*0.3f
                if(breakForce>breakThresh){b.broken=true;val roll=Math.random();result=when{roll<0.4f->WishResult.SUCCESS_WHITE;roll<0.75f->WishResult.SUCCESS_RED;else->WishResult.FAILED};if(result!=WishResult.FAILED)spawnParticles(b.tipPos());state=GameState.RESULT;stateTime=0f;onResult?.invoke(result,wishText)}
                if(stateTime>4f&&breakForce<=breakThresh){result=WishResult.FAILED;state=GameState.RESULT;stateTime=0f;onResult?.invoke(result,wishText)} }
            GameState.RESULT-> { resultGlow=(stateTime/1f).coerceAtMost(1f); if(result!=WishResult.FAILED&&stateTime<3f&&stateTime.toInt()!=(stateTime-dt).toInt()) glow.emit(if(result==WishResult.SUCCESS_WHITE)20 else 15,Vec3(0f,2f,0f),Vec3(0f,2f,0f),if(result==WishResult.SUCCESS_WHITE)Vec3(1f,1f,0.95f)else Vec3(1f,0.2f,0.15f),2f,0.15f) }
            else->{}
        }
        particles.build(); glow.build()
    }
    private fun spawnParticles(pos: Vec3) { val c=when(result){WishResult.SUCCESS_WHITE->Vec3(1f,1f,0.95f);WishResult.SUCCESS_RED->Vec3(1f,0.3f,0.2f);WishResult.FAILED->Vec3(0.5f,0.5f,0.5f)}; particles.emit(100,pos,Vec3(0f,3f,0f),c,3f,0.12f); particles.spread=3f }
    fun submitWish(text: String) { wishText=text; state=GameState.REACHING; stateTime=0f; selected=willow.randomBranch(); selected?.selected=true; breakThresh=0.5f+Math.random().toFloat()*0.4f }
    fun reset() { state=GameState.IDLE; stateTime=0f; wishText=""; resultGlow=0f; selected?.let{it.broken=false;it.bendAmount=0f;it.selected=false}; selected=null; particles.reset(); glow.reset(); handPos.set(0f,-2f,3f) }
    override fun render(r: GLRenderer) {
        r.bindPhong(Mat4.identity(),Vec3(0.55f,0.7f,0.45f),1f,0); willow.ground.draw()
        r.bindPhong(Mat4.translation(0f,2.25f,0f),Vec3(0.42f,0.27f,0.14f),1f,1); willow.trunk.draw()
        for(b in willow.branches){ val m=Mat4.translation(b.basePos.x,b.basePos.y,b.basePos.z); val tint=if(b.selected)Vec3(1.2f,1.1f,0.9f)else Vec3(0.45f,0.3f,0.16f); r.bindPhong(m,tint,1f,1); b.mesh.draw() }
        if(state!=GameState.RESULT||result!=WishResult.FAILED){ val hm=Mat4.translation(handPos.x,handPos.y,handPos.z).multiply(Mat4.rotation(handRot,Vec3(0f,0f,1f))).multiply(Mat4.rotation(-0.3f,Vec3(1f,0f,0f))).multiply(Mat4.scaling(1.5f,1.5f,1.5f)); r.bindPhong(hm,Vec3(1f,1f,1f),1f,0); hand.draw() }
        GLES30.glDisable(GLES30.GL_CULL_FACE); GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA,GLES30.GL_ONE); r.particle.use(); r.particle.setMat4("uView",r.camera.getView().toFloatArray()); r.particle.setMat4("uProjection",r.camera.getProj().toFloatArray()); particles.draw(); glow.draw(); GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA,GLES30.GL_ONE_MINUS_SRC_ALPHA); GLES30.glEnable(GLES30.GL_CULL_FACE)
    }
}
