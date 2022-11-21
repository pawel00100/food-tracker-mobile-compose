package org.acme.food_tracker_mobile_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.acme.food_tracker_mobile_compose.ui.theme.FoodtrackermobilecomposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodtrackermobilecomposeTheme {
                SetSystemNavbarColor()

                Navigation()
            }
        }
    }
}

@Composable
private fun SetSystemNavbarColor() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight
    val color = MaterialTheme.colors.background
    SideEffect {
        systemUiController.setNavigationBarColor(
            color = color, //Your color
            darkIcons = useDarkIcons
        )
    }
}