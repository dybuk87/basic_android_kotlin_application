package pl.dybuk.posttest.ui.welcome

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_welcome.view.*
import pl.dybuk.health.core.BuildConfig
import pl.dybuk.posttest.R
import pl.dybuk.posttest.ui.base.BaseActivity
import pl.dybuk.posttest.ui.dagger
import pl.dybuk.posttest.ui.posts.PostListActivity

class WelcomeActivity : BaseActivity() {

    private val TAG = WelcomeActivity::class.java.name

    val version : TextView by lazy { findViewById<TextView>(R.id.activity_welcome_version) }

    val handler : Handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dagger().inject(this)

        setContentView(R.layout.activity_welcome)

        version.text = BuildConfig.CORE_VERSION

        Log.i(TAG, BuildConfig.CORE_VERSION)

        handler.postDelayed({
            val intent = Intent(this, PostListActivity::class.java)
            startActivity(intent)
            finish()
        },  1200)
    }
}