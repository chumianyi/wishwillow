package com.wishwillow.engine.render
import com.wishwillow.engine.math.Vec3
import kotlin.math.cos
import kotlin.math.sin
object MeshGenerator {
    fun cylinder(radius: Float, height: Float, seg: Int): Mesh {
        val v=mutableListOf<Float>();val n=mutableListOf<Float>();val t=mutableListOf<Float>();val idx=mutableListOf<Int>();val c=mutableListOf<Float>()
        for(i in 0..seg){val a=i.toFloat()/seg*2f*Math.PI.toFloat();val x=cos(a)*radius;val z=sin(a)*radius;val nx=cos(a);val nz=sin(a)
            v.add(x);v.add(-height/2);v.add(z);n.add(nx);n.add(0f);n.add(nz);t.add(i.toFloat()/seg);t.add(0f);c.add(1f);c.add(1f);c.add(1f);c.add(1f)
            v.add(x);v.add(height/2);v.add(z);n.add(nx);n.add(0f);n.add(nz);t.add(i.toFloat()/seg);t.add(1f);c.add(1f);c.add(1f);c.add(1f);c.add(1f)}
        for(i in 0 until seg){val i0=i*2;val i1=i*2+1;val i2=(i+1)*2;val i3=(i+1)*2+1;idx.add(i0);idx.add(i2);idx.add(i1);idx.add(i1);idx.add(i2);idx.add(i3)}
        val ci=v.size/3;v.add(0f);v.add(height/2);v.add(0f);n.add(0f);n.add(1f);n.add(0f);t.add(0.5f);t.add(0.5f);c.add(1f);c.add(1f);c.add(1f);c.add(1f)
        for(i in 0..seg){val a=i.toFloat()/seg*2f*Math.PI.toFloat();v.add(cos(a)*radius);v.add(height/2);v.add(sin(a)*radius);n.add(0f);n.add(1f);n.add(0f);t.add(0.5f);t.add(0.5f);c.add(1f);c.add(1f);c.add(1f);c.add(1f)}
        for(i in 0 until seg){idx.add(ci);idx.add(ci+1+i);idx.add(ci+1+i+1)}
        return Mesh().apply{vertices=v.toFloatArray();normals=n.toFloatArray();texCoords=t.toFloatArray();indices=idx.toIntArray();colors=c.toFloatArray()}
    }
    fun plane(w: Float, d: Float, seg: Int): Mesh {
        val v=mutableListOf<Float>();val n=mutableListOf<Float>();val t=mutableListOf<Float>();val idx=mutableListOf<Int>();val c=mutableListOf<Float>()
        for(z in 0..seg) for(x in 0..seg){v.add((x.toFloat()/seg-0.5f)*w);v.add(0f);v.add((z.toFloat()/seg-0.5f)*d);n.add(0f);n.add(1f);n.add(0f);t.add(x.toFloat()/seg*4f);t.add(z.toFloat()/seg*4f);c.add(0.55f);c.add(0.7f);c.add(0.45f);c.add(1f)}
        for(z in 0 until seg) for(x in 0 until seg){val i0=z*(seg+1)+x;val i1=i0+1;val i2=(z+1)*(seg+1)+x;val i3=i2+1;idx.add(i0);idx.add(i2);idx.add(i1);idx.add(i1);idx.add(i2);idx.add(i3)}
        return Mesh().apply{vertices=v.toFloatArray();normals=n.toFloatArray();texCoords=t.toFloatArray();indices=idx.toIntArray();colors=c.toFloatArray()}
    }
    fun skybox(): Mesh {
        val s=1f;val verts=floatArrayOf(-s,-s,-s,s,-s,-s,s,s,-s,-s,s,-s,-s,-s,s,s,-s,s,s,s,s,-s,s,s)
        val idx=intArrayOf(0,1,2,0,2,3,4,6,5,4,7,6,0,4,5,0,5,1,3,2,6,3,6,7,0,3,7,0,7,4,1,5,6,1,6,2)
        return Mesh().apply{vertices=verts;normals=FloatArray(verts.size);texCoords=FloatArray(verts.size/3*2);indices=idx;colors=FloatArray(verts.size/3*4){1f}}
    }
    fun hand(): Mesh {
        val v=mutableListOf<Float>();val n=mutableListOf<Float>();val t=mutableListOf<Float>();val idx=mutableListOf<Int>();val c=mutableListOf<Float>()
        addBox(v,n,t,idx,c,0f,0f,0f,0.3f,0.35f,0.12f,Vec3(0.95f,0.78f,0.65f))
        for(i in 0 until 4) addBox(v,n,t,idx,c,-0.12f+i*0.08f,0.27f,0f,0.05f,0.2f,0.05f,Vec3(0.95f,0.78f,0.65f))
        addBox(v,n,t,idx,c,0.18f,0.05f,0f,0.06f,0.12f,0.05f,Vec3(0.95f,0.78f,0.65f))
        return Mesh().apply{vertices=v.toFloatArray();normals=n.toFloatArray();texCoords=t.toFloatArray();indices=idx.toIntArray();colors=c.toFloatArray()}
    }
    private fun addBox(v:MutableList<Float>,n:MutableList<Float>,t:MutableList<Float>,idx:MutableList<Int>,c:MutableList<Float>,cx:Float,cy:Float,cz:Float,w:Float,h:Float,d:Float,col:Vec3){
        val base=v.size/3;val hw=w/2;val hh=h/2;val hd=d/2
        val vt=arrayOf(floatArrayOf(cx-hw,cy-hh,cz-hd),floatArrayOf(cx+hw,cy-hh,cz-hd),floatArrayOf(cx+hw,cy+hh,cz-hd),floatArrayOf(cx-hw,cy+hh,cz-hd),floatArrayOf(cx-hw,cy-hh,cz+hd),floatArrayOf(cx+hw,cy-hh,cz+hd),floatArrayOf(cx+hw,cy+hh,cz+hd),floatArrayOf(cx-hw,cy+hh,cz+hd))
        val nm=arrayOf(floatArrayOf(0f,0f,-1f),floatArrayOf(0f,0f,1f),floatArrayOf(-1f,0f,0f),floatArrayOf(1f,0f,0f),floatArrayOf(0f,-1f,0f),floatArrayOf(0f,1f,0f))
        val fc=arrayOf(intArrayOf(0,1,2,3),intArrayOf(5,4,7,6),intArrayOf(4,0,3,7),intArrayOf(1,5,6,2),intArrayOf(4,5,1,0),intArrayOf(3,2,6,7))
        for(f in fc){for(vi in f){v.add(vt[vi][0]);v.add(vt[vi][1]);v.add(vt[vi][2]);val fi=fc.indexOf(f);n.add(nm[fi][0]);n.add(nm[fi][1]);n.add(nm[fi][2]);t.add(0f);t.add(0f);c.add(col.x);c.add(col.y);c.add(col.z);c.add(1f)};idx.add(base);idx.add(base+1);idx.add(base+2);idx.add(base);idx.add(base+2);idx.add(base+3)}
    }
}
