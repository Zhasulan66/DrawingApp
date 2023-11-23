package com.example.drawingapp2.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


data class Table(
    var rowAmount: Int = 0,
    var columnAmount: Int = 0,
    var color: Color = Color.Black,
    var tableScale: Float = 100f,
    var strokeWidth: Dp = 4.dp,
    var tableZoom: Float = 1f

)
