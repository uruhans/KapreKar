@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.urh.kaprekar

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.urh.kaprekar.ui.theme.KapreKarTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MainScreen(
    fabColor: Color,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onFabClick: (Int) -> Unit,
    viewModel: NumberViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val number by remember {
        derivedStateOf {
            state.code.joinToString("").toInt()
        }
    }

    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

   Scaffold(
       floatingActionButton = {
           AnimatedVisibility(
               visible = state.code.none { it == null } &&
                       (state.code.distinct().size > 1) &&
                       (state.code.get(0) != 0),
               enter = scaleIn(tween(durationMillis = 400, delayMillis = 400)) ,// fadeIn(),
               exit = scaleOut(tween(durationMillis = 400))
           ) {
               FloatingActionButton(
                   onClick = { if (state.code.all { it != null }) onFabClick(number) },
                   containerColor = fabColor,
                   modifier = Modifier
                       .sharedBounds(
                           sharedContentState = rememberSharedContentState(
                               key = FAB_EXPLODE_BOUNDS_KEY
                           ),
                           animatedVisibilityScope = animatedVisibilityScope
                       )
                       .padding(end = 12.dp, bottom = 12.dp)
               ) {
                   Icon(
                       imageVector = KapreKarTheme.icons.ThumbUp,
                       tint = Color.Unspecified,
                       contentDescription = "Add item"
                   )
               }
           }

       }
   ) { innerPadding ->
       if(!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
           Box(
               modifier = Modifier
                   .fillMaxSize(),
               contentAlignment = Alignment.Center
           ) {
               Button(onClick = {
                   permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
               }) {
                   Text(text = "Request permission")
               }
           }
       } else {
           val focusRequesters = remember {
               List(4) { FocusRequester() }
           }
           val focusManager = LocalFocusManager.current
           val keyboardManager = LocalSoftwareKeyboardController.current

           LaunchedEffect(state.focusedIndex) {
               state.focusedIndex?.let { index ->
                   focusRequesters.getOrNull(index)?.requestFocus()
               }
           }

           LaunchedEffect(state.code, keyboardManager) {
               val allNumbersEntered = state.code.none { it == null }
               if(allNumbersEntered) {
                   focusRequesters.forEach {
                       it.freeFocus()
                   }
                   focusManager.clearFocus()
                   keyboardManager?.hide()
               }
           }

           NumberScreen(
               state = state,
               focusRequesters = focusRequesters,
               onAction = { action ->
                   when(action) {
                       is NumberAction.OnEnterNumber -> {
                           if(action.number != null) {
                               focusRequesters[action.index].freeFocus()
                           }
                       }
                       else -> Unit
                   }
                   viewModel.onAction(action)
               },
               onDelete = {
                   viewModel.onDelete()
               },
               modifier = Modifier
                   .padding(innerPadding)
                   .consumeWindowInsets(innerPadding)
           )
       }
   }
}