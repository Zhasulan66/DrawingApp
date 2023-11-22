package com.example.drawingapp2

import android.annotation.SuppressLint
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawingapp2.gesture.MotionEvent
import com.example.drawingapp2.gesture.dragMotionEvent
import com.example.drawingapp2.menu.DrawingPropertiesMenu
import com.example.drawingapp2.model.Circle
import com.example.drawingapp2.model.Line
import com.example.drawingapp2.model.PathProperties
import com.example.drawingapp2.model.Rectangle
import com.example.drawingapp2.model.Table
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.max
import kotlin.math.sqrt

@SuppressLint("UnrememberedMutableState")
@Composable
fun DrawingApp() {

    val context = LocalContext.current

    /**
     * Paths that are added, this is required to have paths with different options and paths
     *  ith erase to keep over each other
     */
    var paths = remember { mutableStateListOf<Pair<Path, PathProperties>>() }

    /**
     * Paths that are undone via button. These paths are restored if user pushes
     * redo button if there is no new path drawn.
     *
     * If new path is drawn after this list is cleared to not break paths after undoing previous
     * ones.
     */
    var pathsUndone = remember { mutableStateListOf<Pair<Path, PathProperties>>() }

    /**
     * Canvas touch state. [MotionEvent.Idle] by default, [MotionEvent.Down] at first contact,
     * [MotionEvent.Move] while dragging and [MotionEvent.Up] when first pointer is up
     */
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }

    /**
     * Current position of the pointer that is pressed or being moved
     */
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }

    /**
     * Previous motion event before next touch is saved into this current position.
     */
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

    /**
     * Draw mode, erase mode or touch mode to
     */
    var drawMode by remember { mutableStateOf(DrawMode.Draw) }

    /**
     * Path that is being drawn between [MotionEvent.Down] and [MotionEvent.Up]. When
     * pointer is up this path is saved to **paths** and new instance is created
     */
    var currentPath by remember { mutableStateOf(Path()) }

    /**
     * Properties of path that is currently being drawn between
     * [MotionEvent.Down] and [MotionEvent.Up].
     */
    var currentPathProperty by remember { mutableStateOf(PathProperties()) }

    //line drawing
    var lines by rememberSaveable { mutableStateOf(emptyList<Line>()) }
    var temporaryLine by mutableStateOf<Line?>(null)

    //circle drawing
    var circles by rememberSaveable { mutableStateOf(emptyList<Circle>()) }
    var temporaryCircle by mutableStateOf<Circle?>(null)

    //rect drawing
    var rectangles by rememberSaveable { mutableStateOf(emptyList<Rectangle>()) }
    var temporaryRectangle by mutableStateOf<Rectangle?>(null)

    //field background color
    var currentBackgroundColor by remember { mutableStateOf(Color.White) }
    var bgType by remember { mutableStateOf(0) }
    /*var tableRowSize by remember { mutableStateOf(0) }
    var tableColumnSize by remember { mutableStateOf(0) }

    var tableScale by remember { mutableStateOf(100f) }
    var tableStrokeWidth by remember { mutableStateOf(4.dp) }*/
    val myTable by remember { mutableStateOf(Table()) }

    val canvasText = remember { StringBuilder() }
    val paint = remember {
        Paint().apply {
            textSize = 40f
            color = Color.Black.toArgb()
        }
    }

    var scale by remember { mutableStateOf(1f) }
    var myRotation by remember { mutableStateOf(0f) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)

    ) {

        var bg_lines by remember { mutableStateOf(listOf<Pair<Float, Float>>()) }
        var tableLines by remember { mutableStateOf(listOf<Pair<Float, Float>>()) }

        val distanceBetweenLines = 30.dp

        Canvas(
            modifier = Modifier
                .background(currentBackgroundColor)
                .fillMaxSize()
        ) {
            val screenSize = size
            val numberOfHorizontalLines =
                (screenSize.height / distanceBetweenLines.toPx()).toInt()
            val numberOfVerticalLines = (screenSize.width / distanceBetweenLines.toPx()).toInt()


            when (bgType) {
                1 -> {
                    bg_lines = emptyList()
                    // Draw horizontal lines
                    for (i in 0 until numberOfHorizontalLines) {
                        val y = i * distanceBetweenLines.toPx()
                        bg_lines = bg_lines + Pair(0f, y)
                    }
                }

                2 -> {
                    bg_lines = emptyList()
                    // Draw horizontal lines
                    for (i in 0 until numberOfHorizontalLines) {
                        val y = i * distanceBetweenLines.toPx()
                        bg_lines = bg_lines + Pair(0f, y)
                    }
                    // Draw vertical lines
                    for (i in 0 until numberOfVerticalLines) {
                        val x = i * distanceBetweenLines.toPx()
                        bg_lines = bg_lines + Pair(x, 0f)
                    }
                }

                else -> {
                    // Clear all lines
                    bg_lines = emptyList()
                }
            }

            bg_lines.forEach { (x, y) ->
                drawLine(
                    color = Color.Gray,
                    start = Offset(x, y),
                    end = if (x == 0f) Offset(screenSize.width, y) else Offset(
                        x,
                        screenSize.height
                    ),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Square
                )
            }
        }

        val drawModifier = Modifier

            .fillMaxSize()
            //.weight(1f)
            .background(Color.Transparent)
//            .background(getRandomColor())
            .dragMotionEvent(
                onDragStart = { pointerInputChange ->
                    motionEvent = MotionEvent.Down
                    currentPosition = pointerInputChange.position
                    pointerInputChange.consumeDownChange()

                },
                onDrag = { pointerInputChange ->
                    motionEvent = MotionEvent.Move
                    currentPosition = pointerInputChange.position

                    if (drawMode == DrawMode.Touch) {
                        val change = pointerInputChange.positionChange()
                        println("DRAG: $change")
                        paths.forEach { entry ->
                            val path: Path = entry.first
                            path.translate(change)
                        }
                        currentPath.translate(change)

                    }
                    pointerInputChange.consumePositionChange()

                },
                onDragEnd = { pointerInputChange ->
                    motionEvent = MotionEvent.Up
                    pointerInputChange.consumeDownChange()
                },

                )
            .transformable(
                state = rememberTransformableState { zoomChange, panChange, rotationChange ->
                    if (drawMode == DrawMode.Touch) {
                        val scaleFactor = 1.0f + (zoomChange - 1.0f) * 0.2f
                        scale *= scaleFactor
                        //myRotation += rotationChange * scaleFactor

                        Log.d("zoom", "zoom: $scale")

                        paths.forEachIndexed { index, entry ->
                            val originalPath = entry.first
                            val pathProperties = entry.second

                            // Apply the zoom transformation to the new path
                            originalPath.zoom(scale)
                            //originalPath.rotate(myRotation)
                            if(scale > 1) {
                                pathProperties.strokeWidth += scale * 3
                            }
                            else {
                                pathProperties.strokeWidth -= scale * 3
                            }
                        }

                        // Calculate the desired number of lines based on the current scale
                        val desiredLineCount = myTable.rowAmount + myTable.columnAmount + 4// Change this to the desired number of lines
                        val currentLineCount = tableLines.size

                        // Remove lines if needed
                        if (currentLineCount > desiredLineCount) {
                            val linesToRemove = currentLineCount - desiredLineCount
                            tableLines = tableLines.drop(linesToRemove)

                            // Trigger recomposition
                            Modifier.onGloballyPositioned { coordinates ->
                                // Do nothing, just trigger recomposition
                            }
                        }

                        if(scale > 1) {
                            myTable.tableScale = 100f + scale * 10
                            myTable.strokeWidth = 4.dp + (scale).toInt().dp
                            //tableLines[tableLines.size - 2]
                            //tableStrokeWidth += (scale).dp
                        } else {
                            myTable.tableScale = 100f - scale * 10
                            //myTable.strokeWidth = 4.dp - (scale).toInt().dp
                        }

                    }
                }
            )



        Canvas(
            modifier = drawModifier
            /*.then(Modifier
                .graphicsLayer {
                    scaleX = scale; scaleY = scale
                    rotationZ = myRotation
                })*/
        ) {

            when (motionEvent) {

                MotionEvent.Down -> {
                    if (drawMode == DrawMode.Draw || drawMode == DrawMode.Erase) {
                        currentPath.moveTo(currentPosition.x, currentPosition.y)
                    }

                    if (drawMode == DrawMode.LineDraw) {
                        temporaryLine = Line(
                            PointF(currentPosition.x, currentPosition.y),
                            PointF(currentPosition.x, currentPosition.y),
                            color = currentPathProperty.color
                        )
                    }

                    if (drawMode == DrawMode.CircleDraw) {
                        // Start of the circle
                        val center = PointF(currentPosition.x, currentPosition.y)
                        temporaryCircle = Circle(center, 0f, currentPathProperty.color)
                    }

                    if (drawMode == DrawMode.RectDraw) {
                        // Start of the rectangle
                        val leftTop = PointF(currentPosition.x, currentPosition.y)
                        temporaryRectangle = Rectangle(leftTop, 0f, 0f, currentPathProperty.color)
                    }

                    previousPosition = currentPosition

                }

                MotionEvent.Move -> {

                    if (drawMode == DrawMode.Draw || drawMode == DrawMode.Erase) {
                        currentPath.quadraticBezierTo(
                            previousPosition.x,
                            previousPosition.y,
                            (previousPosition.x + currentPosition.x) / 2,
                            (previousPosition.y + currentPosition.y) / 2

                        )
                    }

                    if (drawMode == DrawMode.LineDraw) {
                        temporaryLine?.endPoint?.set(currentPosition.x, currentPosition.y)
                    }

                    if (drawMode == DrawMode.CircleDraw) {
                        // Update the radius of the temporary circle as the user drags
                        temporaryCircle?.let { circle ->
                            val dx = currentPosition.x - circle.center.x
                            val dy = currentPosition.y - circle.center.y
                            circle.radius =
                                max(0f, sqrt(dx * dx + dy * dy)) // Prevent negative radius
                        }
                    }

                    if (drawMode == DrawMode.RectDraw) {
                        // Update the size of the temporary rectangle as the user drags
                        temporaryRectangle?.let { rectangle ->
                            rectangle.width = max(0f, currentPosition.x - rectangle.leftTop.x)
                            rectangle.height = max(0f, currentPosition.y - rectangle.leftTop.y)
                        }
                    }


                    previousPosition = currentPosition
                }

                MotionEvent.Up -> {
                    if (drawMode == DrawMode.Draw || drawMode == DrawMode.Erase) {
                        currentPath.lineTo(currentPosition.x, currentPosition.y)

                        // Pointer is up save current path
//                        paths[currentPath] = currentPathProperty
                        paths.add(Pair(currentPath, currentPathProperty))

                        // Since paths are keys for map, use new one for each key
                        // and have separate path for each down-move-up gesture cycle
                        currentPath = Path()

                        // Create new instance of path properties to have new path and properties
                        // only for the one currently being drawn
                        currentPathProperty = PathProperties(
                            strokeWidth = currentPathProperty.strokeWidth,
                            color = currentPathProperty.color,
                            strokeCap = currentPathProperty.strokeCap,
                            strokeJoin = currentPathProperty.strokeJoin,
                            eraseMode = currentPathProperty.eraseMode
                        )
                    }

                    if (drawMode == DrawMode.LineDraw) {
                        // Touch released, add the line to the list
                        temporaryLine?.let { line ->
                            lines = lines + line
                        }

                    }

                    if (drawMode == DrawMode.CircleDraw) {
                        // Touch released, add the final version of the circle to the list
                        temporaryCircle?.let { circle ->
                            circles = circles + circle
                        }
                    }

                    if (drawMode == DrawMode.RectDraw) {
                        // Touch released, add the final version of the rectangle to the list
                        temporaryRectangle?.let { rectangle ->
                            rectangles = rectangles + rectangle
                        }
                    }

                    temporaryLine = null
                    temporaryCircle = null
                    temporaryRectangle = null

                    // Since new path is drawn no need to store paths to undone
                    pathsUndone.clear()

                    // If we leave this state at MotionEvent.Up it causes current path to draw
                    // line from (0,0) if this composable recomposes when draw mode is changed
                    currentPosition = Offset.Unspecified
                    previousPosition = currentPosition
                    motionEvent = MotionEvent.Idle
                }

                else -> Unit
            }

            // Calculate offsets for centering the table
            val centerX = (size.width) / 2 - (myTable.columnAmount / 2 * myTable.tableScale)
            val centerY = (size.height) / 2 - (myTable.rowAmount / 2 * myTable.tableScale)

            for (i in 0 until myTable.rowAmount + 1) {
                val y = i * myTable.tableScale + centerY
                tableLines = tableLines + Pair(centerX, y)
            }

            for (i in 0 until myTable.columnAmount + 1) {
                val x = i * myTable.tableScale + centerX
                tableLines = tableLines + Pair(x - 0.1f, centerY)
            }


            with(drawContext.canvas.nativeCanvas) {
                if (myTable.rowAmount != 0 && myTable.columnAmount != 0) {
                    tableLines.forEach { (x, y) ->
                        drawLine(
                            color = myTable.color,
                            start = Offset(x, y),
                            end = if (x == centerX) Offset(
                                centerX + myTable.columnAmount * myTable.tableScale, y)
                            else Offset(x, centerY + myTable.rowAmount * myTable.tableScale),
                            strokeWidth = myTable.strokeWidth.toPx(),
                            cap = StrokeCap.Square
                        )
                    }


                }

                val checkPoint = saveLayer(null, null)

                //draw lines
                if (drawMode == DrawMode.LineDraw) {
                    temporaryLine?.let { line ->
                        drawLine(
                            color = Color.Gray, // Adjust color for temporary line
                            start = Offset(line.startPoint.x, line.startPoint.y),
                            end = Offset(line.endPoint.x, line.endPoint.y),
                            strokeWidth = 10f
                        )
                    }
                }

                lines.forEach { line ->
                    drawLine(
                        color = line.color,
                        start = Offset(line.startPoint.x, line.startPoint.y),
                        end = Offset(line.endPoint.x, line.endPoint.y),
                        strokeWidth = 10f
                    )
                }

                // draw circles
                if (drawMode == DrawMode.CircleDraw) {
                    temporaryCircle?.let { circle ->
                        drawCircle(
                            color = Color.Gray, // Adjust color for temporary circle
                            center = Offset(circle.center.x, circle.center.y),
                            radius = circle.radius,
                            style = Stroke(10f)
                        )
                    }
                }

                circles.forEach { circle ->
                    drawCircle(
                        color = circle.color,
                        center = Offset(circle.center.x, circle.center.y),
                        radius = circle.radius,
                        style = Stroke(10f)
                    )
                }

                // draw rectangles
                if (drawMode == DrawMode.RectDraw) {
                    temporaryRectangle?.let { rectangle ->
                        drawRect(
                            color = Color.Gray, // Adjust color for temporary rectangle
                            topLeft = Offset(rectangle.leftTop.x, rectangle.leftTop.y),
                            size = Size(rectangle.width, rectangle.height),
                            style = Stroke(10f)
                        )
                    }
                }

                rectangles.forEach { rectangle ->
                    drawRect(
                        color = rectangle.color,
                        topLeft = Offset(rectangle.leftTop.x, rectangle.leftTop.y),
                        size = Size(rectangle.width, rectangle.height),
                        style = Stroke(10f)
                    )
                }

                paths.forEach {

                    val path = it.first
                    val property = it.second

                    if (!property.eraseMode) {
                        drawPath(
                            color = property.color,
                            path = path,
                            style = Stroke(
                                width = property.strokeWidth,
                                cap = property.strokeCap,
                                join = property.strokeJoin
                            )
                        )
                    } else {

                        // Source
                        drawPath(
                            color = Color.Transparent,
                            path = path,
                            style = Stroke(
                                width = currentPathProperty.strokeWidth,
                                cap = currentPathProperty.strokeCap,
                                join = currentPathProperty.strokeJoin
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                }




                if (motionEvent != MotionEvent.Idle) {

                    if (!currentPathProperty.eraseMode) {
                        drawPath(
                            color = currentPathProperty.color,
                            path = currentPath,
                            style = Stroke(
                                width = currentPathProperty.strokeWidth,
                                cap = currentPathProperty.strokeCap,
                                join = currentPathProperty.strokeJoin
                            )
                        )
                    } else {
                        drawPath(
                            color = Color.Transparent,
                            path = currentPath,
                            style = Stroke(
                                width = currentPathProperty.strokeWidth,
                                cap = currentPathProperty.strokeCap,
                                join = currentPathProperty.strokeJoin
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                }
                restoreToCount(checkPoint)
            }

            // 🔥🔥 This is for debugging
//            canvasText.clear()
//
//            paths.forEach {
//                val path = it.first
//                val property = it.second
//
//                canvasText.append(
//                    "pHash: ${path.hashCode()}, " +
//                            "propHash: ${property.hashCode()}, " +
//                            "Mode: ${property.eraseMode}\n"
//                )
//            }
//
//            canvasText.append(
//                "🔥 pHash: ${currentPath.hashCode()}, " +
//                        "propHash: ${currentPathProperty.hashCode()}, " +
//                        "Mode: ${currentPathProperty.eraseMode}\n"
//            )
//
//            drawText(text = canvasText.toString(), x = 0f, y = 60f, paint)
        }


        Clock(
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
                .padding(20.dp)
        )

        DrawingPropertiesMenu(
            modifier = Modifier
                .background(currentBackgroundColor)
                .align(alignment = Alignment.BottomCenter),
            pathProperties = currentPathProperty,
            drawMode = drawMode,
            currentBackgroundColor = currentBackgroundColor,
            onUndo = {
                if (paths.isNotEmpty()) {

                    val lastItem = paths.last()
                    val lastPath = lastItem.first
                    val lastPathProperty = lastItem.second
                    paths.remove(lastItem)

                    pathsUndone.add(Pair(lastPath, lastPathProperty))

                }
            },
            onRedo = {
                if (pathsUndone.isNotEmpty()) {

                    val lastPath = pathsUndone.last().first
                    val lastPathProperty = pathsUndone.last().second
                    pathsUndone.removeLast()
                    paths.add(Pair(lastPath, lastPathProperty))
                }
            },
            onClearAll = {
                paths.clear()
                pathsUndone.clear()
                // delete all lines2
                tableLines = emptyList()
                myTable.rowAmount = 0
                myTable.columnAmount = 0
                //clear figures
                lines = emptyList()
                circles = emptyList()
                rectangles = emptyList()
            },
            onPathPropertiesChange = {
                motionEvent = MotionEvent.Idle
            },
            onDrawModeChanged = {
                motionEvent = MotionEvent.Idle
                drawMode = it
                currentPathProperty.eraseMode = (drawMode == DrawMode.Erase)
                /*Toast.makeText(
                context, "pathProperty: ${currentPathProperty.hashCode()}, " +
                        "Erase Mode: ${currentPathProperty.eraseMode}", Toast.LENGTH_SHORT
            ).show()*/
            },
            onBgChanged = { color, type ->
                currentBackgroundColor = color
                bgType = type
            },
            onDrawTable = { rowInt, columnInt ->
                tableLines = emptyList()
                myTable.rowAmount = rowInt
                myTable.columnAmount = columnInt
            }

        )
    }


}

private fun Path.zoom(zoom: Float) {
    val bounds = RectF(
        this.getBounds().left,
        this.getBounds().top,
        this.getBounds().right,
        this.getBounds().bottom
    )
    //this.asAndroidPath().computeBounds(bounds, true)

    val pivotX = bounds.centerX()
    val pivotY = bounds.centerY()

    val matrix = Matrix()

    // Translate the path so that the pivot point is at the origin
    matrix.preTranslate(-pivotX, -pivotY)

    // Scale the path uniformly based on the larger dimension

    matrix.postScale(zoom, zoom)

    // Translate the path back to its original position
    matrix.postTranslate(pivotX, pivotY)

    this.asAndroidPath().transform(matrix)
}

private fun Path.rotate(rotationDegrees: Float) {
    val bounds = RectF(
        this.getBounds().left,
        this.getBounds().top,
        this.getBounds().right,
        this.getBounds().bottom
    )

    val pivotX = bounds.centerX()
    val pivotY = bounds.centerY()

    val matrix = Matrix()

    // Translate the path so that the pivot point is at the origin
    matrix.preTranslate(-pivotX, -pivotY)

    // Rotate the path
    matrix.postRotate(rotationDegrees)

    // Translate the path back to its original position
    matrix.postTranslate(pivotX, pivotY)

    this.asAndroidPath().transform(matrix)
}


private fun DrawScope.drawText(text: String, x: Float, y: Float, paint: Paint) {

    val lines = text.split("\n")
    // 🔥🔥 There is not a built-in function as of 1.0.0
    // for drawing text so we get the native canvas to draw text and use a Paint object
    val nativeCanvas = drawContext.canvas.nativeCanvas

    lines.indices.withIndex().forEach { (posY, i) ->
        nativeCanvas.drawText(lines[i], x, posY * 40 + y, paint)
    }
}

class ClockViewModel : ViewModel() {
    private val _currentTime = mutableStateOf(getCurrentTime())
    val currentTime: State<String> get() = _currentTime

    init {
        // Use a coroutine to update the time every second
        viewModelScope.launch {
            while (true) {
                _currentTime.value = getCurrentTime()
                delay(1000)
            }
        }
    }

    private fun getCurrentTime(): String {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return dateFormat.format(currentTime)
    }
}

@Composable
fun Clock(
    modifier: Modifier
) {
    val viewModel: ClockViewModel = ClockViewModel()
    val currentTime by viewModel.currentTime

    Box(
        modifier = modifier
    ) {
        Text(text = currentTime, fontSize = 16.sp)
    }
}
