package com.portfolio.pixelglow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.portfolio.pixelglow.ui.screens.AnimatedSplashScreen
import com.portfolio.pixelglow.ui.screens.MainEditorScreen
import com.portfolio.pixelglow.ui.theme.PixelGlowTheme
import com.portfolio.pixelglow.ui.viewmodel.EditorViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PixelGlowTheme {
                val viewModel: EditorViewModel = viewModel(
                    factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                )

                val isSplashLoading by viewModel.isSplashLoading.collectAsState()

                Crossfade(
                    targetState = isSplashLoading,
                    animationSpec = tween(durationMillis = 600),
                    label = "AppLaunchTransition"
                ) { loading ->
                    if (loading) {
                        AnimatedSplashScreen()
                    } else {
                        // 🌟 'finish()' एक्शन को लैम्ब्डा के रूप में पास करना ताकि होम स्क्रीन पर बैक दबाने से ऐप बंद हो
                        MainEditorScreen(
                            viewModel = viewModel,
                            onSystemCloseApp = { finish() }
                        )
                    }
                }
            }
        }
    }
}