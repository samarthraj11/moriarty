package com.moriartystudio.moriarty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.moriartystudio.moriarty.app.AppContainer
import com.moriartystudio.moriarty.app.navigation.MoriartyApp
import com.moriartystudio.moriarty.ui.theme.MoriartyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppContainer.initialize(applicationContext)
        enableEdgeToEdge()
        setContent {
            MoriartyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MoriartyApp()
                }
            }
        }
    }
}
