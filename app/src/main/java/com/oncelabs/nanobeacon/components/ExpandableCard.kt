
package com.oncelabs.nanobeacon.components

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.oncelabs.nanobeacon.ui.theme.logModalItemBackgroundColor

@Composable
fun ExpandableCard(
    expanded: Boolean,
    content: @Composable () -> Unit
) {
    val animationDuration = 500
    val transitionState = remember {
        MutableTransitionState(expanded).apply {
            targetState = !expanded
        }
    }
    val transition = updateTransition(transitionState, label = "")

    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = animationDuration)
    }, label = "") {
        if (it) 0f else 180f
    }

    Card(
        backgroundColor = logModalItemBackgroundColor,
        contentColor = Color.White,
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Column {
            ExpandableContent(visible = expanded, content = content)
        }
    }
}

@Composable
fun ExpandableContent(
    visible: Boolean = true,
    content: @Composable () -> Unit
) {
    val transitionDuration = 500

    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(transitionDuration)
        ) + fadeIn(
            initialAlpha = 0.3f,
            animationSpec = tween(transitionDuration)
        )
    }
    val exitTransition = remember {
        shrinkVertically(
            // Expand from the top.
            shrinkTowards = Alignment.Top,
            animationSpec = tween(transitionDuration)
        ) + fadeOut(
            // Fade in with the initial alpha of 0.3f.
            animationSpec = tween(transitionDuration)
        )
    }
    AnimatedVisibility(
        visible = visible,
        enter = enterTransition,
        exit = exitTransition
    ) {
        content()
    }
}