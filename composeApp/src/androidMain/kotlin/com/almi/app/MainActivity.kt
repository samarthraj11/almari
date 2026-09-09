package com.almi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.almi.shared.data.initializeHomeStateStorage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeHomeStateStorage(this)
        enableEdgeToEdge()
        setContent { App() }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppPreview() {
    App()
}
