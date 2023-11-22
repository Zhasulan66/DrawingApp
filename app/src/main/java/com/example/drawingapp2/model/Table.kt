package com.example.drawingapp2.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap


data class Table(
    var rowAmount: Int = 0,
    var columnAmount: Int = 0,
    var color: Color = Color.Black,
    var tableScale: Float = 100f,
    var strokeWidth: Dp = 4.dp,

) /*{


    fun DrawTable() {

        //var tableLines by remember { mutableStateOf(listOf<Pair<Float, Float>>()) }

        Canvas(modifier = Modifier) {
            // Calculate offsets for centering the table
            val centerX = (size.width) / 2 - (columnAmount / 2 * tableScale)
            val centerY = (size.height) / 2 - (rowAmount / 2 * tableScale)

            for (i in 0 until rowAmount + 1) {
                val y = i * tableScale + centerY
                tableLines = tableLines + Pair(centerX, y)
            }

            for (i in 0 until columnAmount + 1) {
                val x = i * tableScale + centerX
                tableLines = tableLines + Pair(x, centerY)
            }

            if (rowAmount != 0 && columnAmount != 0) {
                tableLines.forEach { (x, y) ->
                    drawLine(
                        color = color,
                        start = Offset(x, y),
                        end = if (x == centerX) Offset(
                            centerX + columnAmount * tableScale, y)
                        else Offset(x, centerY + rowAmount * tableScale),
                        strokeWidth = strokeWidth.toPx(),
                        cap = StrokeCap.Square
                    )
                }
            }
        }
    }*/
