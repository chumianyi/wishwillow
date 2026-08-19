package com.wishwillow
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wishwillow.engine.render.GLRenderer
import com.wishwillow.game.GameScene
import com.wishwillow.game.WishResult
import com.wishwillow.ui.NeumorphismButton
class MainActivity : AppCompatActivity() {
    private lateinit var glv: GLSurfaceView; private lateinit var renderer: GLRenderer; private lateinit var scene: GameScene
    private lateinit var wishEt: EditText; private lateinit var submitBtn: NeumorphismButton; private lateinit var againBtn: NeumorphismButton
    private lateinit var inputLayout: View; private lateinit var resultLayout: View; private lateinit var resultTitle: TextView; private lateinit var resultWish: TextView; private lateinit var hint: TextView
    private val h=Handler(Looper.getMainLooper())
    override fun onCreate(s: Bundle?){super.onCreate(s);setContentView(R.layout.activity_main)
        glv=findViewById(R.id.glSurfaceView); glv.setEGLContextClientVersion(3); glv.setEGLConfigChooser(8,8,8,8,16,0)
        scene=GameScene(); renderer=GLRenderer(this); renderer.scene=scene; glv.setRenderer(renderer); glv.renderMode=GLSurfaceView.RENDERMODE_CONTINUOUSLY
        wishEt=findViewById(R.id.wishEditText); submitBtn=findViewById(R.id.submitWishButton); againBtn=findViewById(R.id.tryAgainButton)
        inputLayout=findViewById(R.id.wishInputLayout); resultLayout=findViewById(R.id.resultLayout); resultTitle=findViewById(R.id.resultTitle); resultWish=findViewById(R.id.resultWishText); hint=findViewById(R.id.hintText)
        scene.onResult={r,w->h.post{showResult(r,w)}}
        submitBtn.setOnClickListener{val w=wishEt.text.toString().trim();if(w.isNotEmpty()){hideKb();inputLayout.visibility=View.GONE;hint.visibility=View.GONE;glv.queueEvent{scene.submitWish(w)}}}
        againBtn.setOnClickListener{resultLayout.visibility=View.GONE;inputLayout.visibility=View.VISIBLE;hint.visibility=View.VISIBLE;wishEt.text.clear();glv.queueEvent{scene.reset()}}
        wishEt.addTextChangedListener(object:TextWatcher{override fun beforeTextChanged(s:CharSequence?,st:Int,c:Int,a:Int){}override fun onTextChanged(s:CharSequence?,st:Int,b:Int,c:Int){submitBtn.alpha=if(s?.isNotEmpty()==true)1f else 0.5f}override fun afterTextChanged(s:Editable?){}})
        submitBtn.alpha=0.5f
    }
    private fun showResult(r:WishResult,w:String){resultLayout.visibility=View.VISIBLE;resultLayout.startAnimation(AnimationUtils.loadAnimation(this,android.R.anim.fade_in));resultWish.text=getString(R.string.your_wish)+"："+w
        when(r){WishResult.SUCCESS_WHITE->{resultTitle.text=getString(R.string.wish_success);resultTitle.setTextColor(getColor(R.color.accent_blue));againBtn.accent=getColor(R.color.accent_blue)}
            WishResult.SUCCESS_RED->{resultTitle.text=getString(R.string.wish_success_cost);resultTitle.setTextColor(getColor(R.color.cost_red));againBtn.accent=getColor(R.color.cost_red)}
            WishResult.FAILED->{resultTitle.text=getString(R.string.wish_failed);resultTitle.setTextColor(getColor(R.color.neu_text_sub));againBtn.accent=getColor(R.color.neu_text_sub)}}}
    private fun hideKb(){val imm=getSystemService(INPUT_METHOD_SERVICE)as InputMethodManager;imm.hideSoftInputFromWindow(wishEt.windowToken,0)}
    override fun onResume(){super.onResume();glv.onResume()}
    override fun onPause(){super.onPause();glv.onPause()}
}
