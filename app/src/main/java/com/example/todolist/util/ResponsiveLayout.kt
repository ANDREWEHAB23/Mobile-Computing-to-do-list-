package com.example.todolist.util


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** True on tablets and landscape-wide screens (width >= 600dp). */
fun WindowSizeClass.isWide(): Boolean = widthSizeClass != WindowWidthSizeClass.Compact

/** Keeps content readable on wide screens by capping and centering its width. */
@Composable
fun ResponsiveContainer(
    modifier: Modifier = Modifier,
    maxWidth: Dp = 600.dp,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Box(modifier = Modifier.widthIn(max = maxWidth).fillMaxWidth()) {
            content()
        }
    }
}

/** Shows list and detail side by side on wide screens, only the list otherwise. */
@Composable
fun TwoPaneLayout(
    isWide: Boolean,
    list: @Composable () -> Unit,
    detail: @Composable () -> Unit
) {
    if (isWide) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) { list() }
            Box(modifier = Modifier.weight(1f)) { detail() }
        }
    } else {
        list()
    }
}