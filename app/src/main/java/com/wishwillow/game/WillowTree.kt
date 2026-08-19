package com.wishwillow.game
import com.wishwillow.engine.animation.VertexAnimation
import com.wishwillow.engine.math.Vec3
import com.wishwillow.engine.render.Mesh
import com.wishwillow.engine.render.MeshGenerator
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
class WillowBranch(val basePos: Vec3, val length: Float, val angle: Float, val seed: Int) {
    lateinit var mesh: Mesh; lateinit var anim: VertexAnimation
    var bendAmount=0f; var bendDir=Vec3(1f,0f,0f); var swayOffset=0f; var broken=false; var selected=false
    fun generate() {
        val seg=12; val stacks=16; val v=mutableListOf<Float>();val n=mutableListOf<Float>();val t=mutableListOf<Float>();val idx=mutableListOf<Int>();val c=mutableListOf<Float>()
        val dx=cos(angle); val dz=sin(angle); val rnd=Random(seed)
        for(s in 0..stacks){val tt=s.toFloat()/stacks;val y=-tt*length;val r=0.04f*(1f-tt*0.7f);val sx=sin(tt*3f+seed)*0.15f*tt;val sz=cos(tt*2.5f+seed*0.7f)*0.1f*tt;val cx=dx*sx;val cz=dz*sz
            for(i in 0..seg){val a=i.toFloat()/seg*2f*Math.PI.toFloat();v.add(cx+cos(a)*r);v.add(y);v.add(cz+sin(a)*r);n.add(cos(a));n.add(0f);n.add(sin(a));t.add(i.toFloat()/seg);t.add(tt);val bt=0.4f+rnd.nextFloat()*0.2f;c.add(bt*1.1f);c.add(bt*0.7f);c.add(bt*0.4f);c.add(1f)}}
        for(s in 0 until stacks) for(i in 0 until seg){val i0=s*(seg+1)+i;val i1=i0+1;val i2=(s+1)*(seg+1)+i;val i3=i2+1;idx.add(i0);idx.add(i2);idx.add(i1);idx.add(i1);idx.add(i2);idx.add(i3)}
        mesh=Mesh().apply{vertices=v.toFloatArray();normals=n.toFloatArray();texCoords=t.toFloatArray();indices=idx.toIntArray();colors=c.toFloatArray()}
        anim=VertexAnimation(mesh); anim.setBase(mesh.vertices); anim.amp=0.08f; anim.freq=1.5f; mesh.upload()
    }
    fun update(dt: Float, gt: Float, wind: Float) {
        val sway=sin(gt*1.2f+swayOffset)*wind; anim.update(dt, bendAmount+sway*0.3f, bendDir)
    }
    fun tipPos(): Vec3 { val t=1f; val y=-t*length; val sx=sin(t*3f+seed)*0.15f*t; val sz=cos(t*2.5f+seed*0.7f)*0.1f*t; val dx=cos(angle); val dz=sin(angle); val b=bendAmount*t*t; return Vec3(basePos.x+dx*sx+bendDir.x*b, basePos.y+y, basePos.z+dz*sz+bendDir.z*b) }
}
class WillowTree {
    lateinit var trunk: Mesh; lateinit var ground: Mesh
    val branches=mutableListOf<WillowBranch>(); var wind=0.5f; var gt=0f
    fun generate() {
        trunk=MeshGenerator.cylinder(0.35f,4.5f,16); trunk.upload()
        ground=MeshGenerator.plane(20f,20f,20); ground.upload()
        val rnd=Random(12345)
        for(i in 0 until 24){val a=i.toFloat()/24*2f*Math.PI.toFloat()+rnd.nextFloat()*0.3f;val h=3.5f+rnd.nextFloat()*0.8f;val rad=0.3f+rnd.nextFloat()*0.15f;val bp=Vec3(cos(a)*rad,h,sin(a)*rad);val len=2.5f+rnd.nextFloat()*2f;val b=WillowBranch(bp,len,a,rnd.nextInt());b.swayOffset=rnd.nextFloat()*6.28f;b.bendDir=Vec3(cos(a),0f,sin(a)).normalize();b.generate();branches.add(b)}
    }
    fun update(dt: Float) { gt+=dt; for(b in branches) b.update(dt,gt,wind) }
    fun randomBranch(): WillowBranch = branches.filter{!it.broken}.random()
}
