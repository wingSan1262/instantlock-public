package com.risyan.quickshutdownphone.base.base_class

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity

open class BaseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }


    private fun handleIntent(intent: Intent?) {
        val link = intent?.extras?.getString("link")

        if (!link.isNullOrBlank()) {
            try {
                val uri = Uri.parse(link)
                val browserIntent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(browserIntent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Invalid link format", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d("HandleIntent", "No link found in intent extras.")
        }
    }
}

