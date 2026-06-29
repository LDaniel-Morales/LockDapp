package com.lockdapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.lockdapp.domain.model.AppTheme
import com.lockdapp.ui.theme.LockAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LockAppTheme(theme = AppTheme.MASTIL) {
                Surface(modifier = Modifier.fillMaxSize()) {}
            }
        }
    }
}
