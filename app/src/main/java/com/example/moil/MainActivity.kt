package com.example.moil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.moil.navigation.MoilAppNavigation
import com.example.moil.ui.theme.MoilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoilTheme(darkTheme = false) {
                MoilAppNavigation()
            }
        }
    }
}
