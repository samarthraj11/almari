package com.almari.app

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.almari.shared.data.initializeHomeStateStorage
import com.almari.shared.data.handleGoogleAuthRedirect

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeHomeStateStorage(this)
        intent.dataString?.let(::handleGoogleAuthRedirect)
        enableEdgeToEdge()
        setContent { App() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.dataString?.let(::handleGoogleAuthRedirect)
    }
}

@Preview(showBackground = true)
@Composable
private fun AppPreview() {
    App()
}
