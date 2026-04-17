package com.appclimb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.appclimb.navigation.AppNavigation
import com.appclimb.ui.theme.AppClimbTheme
import com.appclimb.util.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 저장된 토큰 확인해서 로그인 여부 결정
        val isLoggedIn = runBlocking { tokenManager.token.firstOrNull() != null }

        setContent {
            AppClimbTheme {
                AppNavigation(isLoggedIn = isLoggedIn)
            }
        }
    }
}
