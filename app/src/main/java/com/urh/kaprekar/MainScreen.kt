@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.urh.kaprekar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.urh.kaprekar.ui.theme.KapreKarTheme
import org.koin.androidx.compose.koinViewModel

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

   Scaffold(
       floatingActionButton = {
           AnimatedVisibility(
               visible = state.code.none { it == null } &&
                       (state.code.distinct().size > 1) &&
                       (state.code.get(0) != 0),
               enter = fadeIn(),
               exit = fadeOut()
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
           modifier = Modifier
               .padding(innerPadding)
               .consumeWindowInsets(innerPadding)
       )
   }
}