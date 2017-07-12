package edu.northwestern.langlearn

import android.os.Bundle
import android.util.Log
import android.support.v7.app.AppCompatActivity
// import android.support.v7.widget.Toolbar
// import android.widget.TextView

// import kotlinx.android.synthetic.main.activity_message.toolbar
import kotlinx.android.synthetic.main.content_message.errorMessage

class MessageActivity : AppCompatActivity() {
    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        // setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val msg = intent.getStringExtra(SleepMode.MESSAGE_INTENT_EXTRA)

        Log.d(TAG, "Msg: $msg")
        errorMessage.text = msg
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
