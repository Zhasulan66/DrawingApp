package com.example.drawingapp2.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class Parallelogram(
    var points: MutableList<Offset>,
    val color: Color
)
