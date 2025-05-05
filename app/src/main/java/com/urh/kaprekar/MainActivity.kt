@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.urh.kaprekar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.urh.kaprekar.imageshare.BitmapComposer
import com.urh.kaprekar.imageshare.ImageInfo
import com.urh.kaprekar.imageshare.ShareImage
import com.urh.kaprekar.ui.theme.KapreKarTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


const val FAB_EXPLODE_BOUNDS_KEY = "FAB_EXPLODE_BOUNDS_KEY"

class MainActivity : ComponentActivity() {

    private lateinit var bitmapComposer: BitmapComposer
    private lateinit var shareImage: ShareImage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        bitmapComposer = BitmapComposer(mainScope)
        shareImage = ShareImage(this)

        enableEdgeToEdge()
        setContent {
            val scope: CoroutineScope = rememberCoroutineScope()
            KapreKarTheme {
                val navController = rememberNavController()
                val fabColor = KapreKarTheme.colorScheme.surface
                SharedTransitionLayout {
                    NavHost(
                        navController = navController,
                        startDestination = MainRoute,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        composable<MainRoute> {
                            MainScreen(
                                fabColor = fabColor,
                                animatedVisibilityScope = this,
                                onFabClick = {
                                    navController.navigate(route = CalculationRoute(movieId = it))
                                }
                            )
                        }

                        composable<CalculationRoute> { backStackEntry ->
                            val calculationRoute: CalculationRoute = backStackEntry.toRoute()
                            val id = calculationRoute.movieId
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(fabColor.copy(alpha = 0.8f))
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState(
                                            key = FAB_EXPLODE_BOUNDS_KEY
                                        ),
                                        animatedVisibilityScope = this
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                CalculationScreen(id = id, onShareBitmap = { screenshot ->
                                    scope.launch {
                                        createAndShareBitmap(imageInfo = screenshot)
                                    }
                                })
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun createAndShareBitmap(imageInfo: ImageInfo) {
        val bitmap = bitmapComposer.composableToBitmap(
            imageInfo.currentActivity,
            width = 600.dp,
            screenDensity = imageInfo.screenDensity,
            content = imageInfo.composableView
        )

        val shareImageIntent = shareImage.initiateWith(bitmap.asImageBitmap())
        shareImageIntent?.let {
            startActivity(it)
        }
    }
}