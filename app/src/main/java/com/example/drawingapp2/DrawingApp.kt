package com.example.drawingapp2

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawingapp2.gesture.MotionEvent
import com.example.drawingapp2.gesture.dragMotionEvent
import com.example.drawingapp2.menu.DrawingPropertiesMenu
import com.example.drawingapp2.model.Circle
import com.example.drawingapp2.model.HalfCircle
import com.example.drawingapp2.model.Line
import com.example.drawingapp2.model.Octagon
import com.example.drawingapp2.model.PathProperties
import com.example.drawingapp2.model.Rectangle
import com.example.drawingapp2.model.Table
import com.example.drawingapp2.model.Triangle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
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

    //dashed Line drawing
    var dashedLines by rememberSaveable { mutableStateOf(emptyList<Line>()) }
    var temporaryDashedLine by mutableStateOf<Line?>(null)

    //line with arrow drawing
    var arrowLines by rememberSaveable { mutableStateOf(emptyList<Line>()) }
    var temporaryArrowLine by mutableStateOf<Line?>(null)

    //dashed line with arrow drawing
    var dashedArrowLines by rememberSaveable { mutableStateOf(emptyList<Line>()) }
    var temporaryDashedArrowLine by mutableStateOf<Line?>(null)

    //triangle drawing
    var triangles by remember { mutableStateOf(emptyList<Triangle>()) }
    var temporaryTriangle by remember { mutableStateOf<Triangle?>(null) }

    //rect drawing
    var rectangles by rememberSaveable { mutableStateOf(emptyList<Rectangle>()) }
    var temporaryRectangle by mutableStateOf<Rectangle?>(null)

    // round rect drawing
    var roundRectangles by rememberSaveable { mutableStateOf(emptyList<Rectangle>()) }
    var temporaryRoundRectangle by mutableStateOf<Rectangle?>(null)

    //circle drawing
    var circles by rememberSaveable { mutableStateOf(emptyList<Circle>()) }
    var temporaryCircle by mutableStateOf<Circle?>(null)

    // Half circle drawing
    var halfCircles by remember { mutableStateOf(emptyList<HalfCircle>()) }
    var temporaryHalfCircle by remember { mutableStateOf<HalfCircle?>(null) }

    var octagons by remember { mutableStateOf(emptyList<Octagon>()) }
    var temporaryOctagon by remember { mutableStateOf<Octagon?>(null) }

    //field background color
    var currentBackgroundColor by remember { mutableStateOf(Color.White) }
    var bgType by remember { mutableStateOf(0) }
    val myTable by remember { mutableStateOf(Table()) }

    //for image
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }

    // Calculate offsets for centering the table
    var centerX by remember { mutableStateOf(0f) }
    var centerY by remember { mutableStateOf(0f) }

    val canvasText = remember { StringBuilder() }
    val paint = remember {
        Paint().apply {
            textSize = 40f
            color = Color.Black.toArgb()
        }
    }

    var scale by remember { mutableStateOf(1f) }
    var myRotation by remember { mutableStateOf(0f) }

    var initialTouchPoint by mutableStateOf(Offset.Zero)




    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)

    ) {

        var bg_lines by remember { mutableStateOf(listOf<Pair<Float, Float>>()) }
        var tableLines by remember { mutableStateOf(listOf<Pair<Float, Float>>()) }

        val distanceBetweenLines = 30.dp

        //canvas for bg lines
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

                        /*val updatedTableLines = tableLines.map { (x, y) ->
                            Pair(x + currentPosition.x, y + currentPosition.y)
                        }

                        tableLines = emptyList()
                        tableLines = updatedTableLines*/
                        // Calculate the desired number of lines based on the current scale
                        val desiredLineCount = myTable.rowAmount / 2 // + myTable.columnAmount
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
                        centerX += change.x
                        centerY += change.y

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
                    scale = 1f
                    if (drawMode == DrawMode.Touch) {
                        val scaleFactor = 1.0f + (zoomChange - 1.0f) * 0.2f
                        scale *= scaleFactor
                        myTable.tableZoom *= scaleFactor
                        //myRotation += rotationChange * scaleFactor

                        Log.d("zoom", "zoom: $scale")

                        paths.forEachIndexed { index, entry ->
                            val originalPath = entry.first
                            val pathProperties = entry.second

                            // Apply the zoom transformation to the new path
                            originalPath.zoom(scale)
                            //originalPath.rotate(myRotation)
                            if (scale > 1) {
                                pathProperties.strokeWidth += scale * 3
                            } else {
                                pathProperties.strokeWidth -= scale * 3
                            }
                        }

                        // Calculate the desired number of lines based on the current scale
                        val desiredLineCount =
                            myTable.rowAmount + myTable.columnAmount - 10 // extra lines while dragging to delete
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

                        if (myTable.tableZoom > 1) {
                            myTable.tableScale = 100f + myTable.tableZoom * 10
                            myTable.strokeWidth = 4.dp + (myTable.tableZoom).toInt().dp
                            //tableLines[tableLines.size - 2]
                            //tableStrokeWidth += (scale).dp
                        } else {
                            myTable.tableScale = 100f - myTable.tableZoom * 10
                            //myTable.strokeWidth = 4.dp - (scale).toInt().dp
                        }
                        //scale = 1f
                    }
                }
            )



        Canvas(
            modifier = drawModifier
        ) {

            //motion events

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

                    if (drawMode == DrawMode.DashLineDraw) {
                        temporaryDashedLine = Line(
                            PointF(currentPosition.x, currentPosition.y),
                            PointF(currentPosition.x, currentPosition.y),
                            color = currentPathProperty.color
                        )
                    }

                    if (drawMode == DrawMode.ArrowLineDraw) {
                        temporaryArrowLine = Line(
                            PointF(currentPosition.x, currentPosition.y),
                            PointF(currentPosition.x, currentPosition.y),
                            color = currentPathProperty.color
                        )
                    }

                    if (drawMode == DrawMode.DashArrowLineDraw) {
                        temporaryDashedArrowLine = Line(
                            PointF(currentPosition.x, currentPosition.y),
                            PointF(currentPosition.x, currentPosition.y),
                            color = currentPathProperty.color
                        )
                    }

                    if (drawMode == DrawMode.TriangleDraw) {
                        temporaryTriangle = Triangle(currentPosition, currentPosition, currentPosition, currentPathProperty.color)
                    }

                    if (drawMode == DrawMode.RectDraw) {
                        // Start of the rectangle
                        initialTouchPoint = currentPosition
                        temporaryRectangle = Rectangle(
                            PointF(currentPosition.x, currentPosition.y),
                            0f,
                            0f,
                            currentPathProperty.color
                        )
                    }

                    if (drawMode == DrawMode.RoundRectDraw) {
                        initialTouchPoint = currentPosition
                        temporaryRoundRectangle = Rectangle(
                            PointF(currentPosition.x, currentPosition.y),
                            0f,
                            0f,
                            currentPathProperty.color
                        )
                    }

                    if (drawMode == DrawMode.CircleDraw) {
                        // Start of the circle
                        val center = PointF(currentPosition.x, currentPosition.y)
                        temporaryCircle = Circle(center, 0f, currentPathProperty.color)
                    }

                    if (drawMode == DrawMode.HalfCircleDraw) {
                        initialTouchPoint = currentPosition
                        temporaryHalfCircle = HalfCircle(currentPosition.x, currentPosition.y, 0f, currentPathProperty.color, true)
                    }

                    if (drawMode == DrawMode.OctagonDraw) {
                        temporaryOctagon = Octagon(List(8) { currentPosition }.toMutableList(), currentPathProperty.color)
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
                        temporaryLine?.end?.set(currentPosition.x, currentPosition.y)
                    }

                    if (drawMode == DrawMode.DashLineDraw) {
                        temporaryDashedLine?.end?.set(currentPosition.x, currentPosition.y)
                    }

                    if (drawMode == DrawMode.ArrowLineDraw) {
                        temporaryArrowLine?.end?.set(currentPosition.x, currentPosition.y)
                    }

                    if (drawMode == DrawMode.DashArrowLineDraw) {
                        temporaryDashedArrowLine?.end?.set(currentPosition.x, currentPosition.y)
                    }

                    if (drawMode == DrawMode.TriangleDraw) {
                        temporaryTriangle?.let { triangle ->
                            triangle.point2 = currentPosition
                            triangle.point3 = Offset(currentPosition.x, triangle.point1.y)
                        }
                    }

                    if (drawMode == DrawMode.RectDraw) {
                        // Update the size of the temporary rectangle as the user drags
                        val left = min(initialTouchPoint.x, currentPosition.x)
                        val top = min(initialTouchPoint.y, currentPosition.y)
                        val right = max(initialTouchPoint.x, currentPosition.x)
                        val bottom = max(initialTouchPoint.y, currentPosition.y)

                        temporaryRectangle?.let { rectangle ->
                            rectangle.leftTop.x = left
                            rectangle.leftTop.y = top
                            rectangle.width = right - left
                            rectangle.height = bottom - top
                        }
                    }

                    if (drawMode == DrawMode.RoundRectDraw) {
                        val left = min(initialTouchPoint.x, currentPosition.x)
                        val top = min(initialTouchPoint.y, currentPosition.y)
                        val right = max(initialTouchPoint.x, currentPosition.x)
                        val bottom = max(initialTouchPoint.y, currentPosition.y)

                        temporaryRoundRectangle?.let { rectangle ->
                            rectangle.leftTop.x = left
                            rectangle.leftTop.y = top
                            rectangle.width = right - left
                            rectangle.height = bottom - top
                        }
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

                    if (drawMode == DrawMode.HalfCircleDraw) {
                        temporaryHalfCircle?.let { halfCircle ->
                            val deltaY = currentPosition.y - initialTouchPoint.y
                            val radius = abs(deltaY)
                            halfCircle.radius = radius

                            // Determine if the touch is going towards the top or bottom
                            halfCircle.isTop = currentPosition.y < initialTouchPoint.y

                        }
                    }

                    if (drawMode == DrawMode.OctagonDraw) {
                        temporaryOctagon?.let { octagon ->
                            octagon.points.forEachIndexed { index, _ ->
                                octagon.points[index] = calculateOctagonPoint(currentPosition, index)
                            }
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

                    if (drawMode == DrawMode.DashLineDraw) {

                        // Drawing dashed lines
                        temporaryDashedLine?.let { dashedLine ->
                            dashedLines = dashedLines + dashedLine
                        }

                    }

                    if (drawMode == DrawMode.ArrowLineDraw) {

                        // Drawing arrow lines
                        temporaryArrowLine?.let { arrowLine ->
                            arrowLines = arrowLines + arrowLine
                        }

                    }

                    if (drawMode == DrawMode.DashArrowLineDraw) {

                        // Drawing arrow lines
                        temporaryDashedArrowLine?.let { dashedArrowLine ->
                            dashedArrowLines = dashedArrowLines + dashedArrowLine
                        }

                    }

                    if (drawMode == DrawMode.TriangleDraw) {
                        temporaryTriangle?.let { triangle ->
                            triangles = triangles + triangle
                        }
                    }

                    if (drawMode == DrawMode.RectDraw) {
                        // Touch released, add the final version of the rectangle to the list
                        temporaryRectangle?.let { rectangle ->
                            rectangles = rectangles + rectangle
                        }
                    }

                    if (drawMode == DrawMode.RoundRectDraw) {
                        // Touch released, add the final version of the rectangle to the list
                        temporaryRoundRectangle?.let { rectangle ->
                            roundRectangles = roundRectangles + rectangle
                        }
                    }

                    if (drawMode == DrawMode.CircleDraw) {
                        // Touch released, add the final version of the circle to the list
                        temporaryCircle?.let { circle ->
                            circles = circles + circle
                        }
                    }

                    if (drawMode == DrawMode.HalfCircleDraw) {
                        temporaryHalfCircle?.let { halfCircle ->
                            halfCircles = halfCircles + halfCircle
                        }
                    }

                    if (drawMode == DrawMode.OctagonDraw) {
                        temporaryOctagon?.let { octagon ->
                            octagons = octagons + octagon
                        }
                    }

                    temporaryLine = null
                    temporaryDashedLine = null
                    temporaryArrowLine = null
                    temporaryDashedArrowLine = null
                    temporaryTriangle = null
                    temporaryRectangle = null
                    temporaryRoundRectangle = null
                    temporaryCircle = null
                    temporaryHalfCircle = null
                    temporaryOctagon = null

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

            for (i in 0 until myTable.rowAmount + 1) {
                val y = i * myTable.tableScale + centerY
                tableLines = tableLines + Pair(centerX, y)
            }

            for (i in 0 until myTable.columnAmount + 1) {
                val x = i * myTable.tableScale + centerX
                tableLines = tableLines + Pair(x - 0.1f, centerY)
            }



            with(drawContext.canvas.nativeCanvas) {

                imageUri?.let {
                    if (Build.VERSION.SDK_INT < 28) {  //28
                        bitmap.value = MediaStore.Images
                            .Media.getBitmap(context.contentResolver, it)
                    } else {
                        val source = ImageDecoder.createSource(context.contentResolver, it)
                        bitmap.value = ImageDecoder.decodeBitmap(source)
                    }

                    bitmap.value?.let { btm ->
                        drawImage(
                            image = btm.asImageBitmap(),
                            dstOffset = IntOffset(x = centerX.toInt(), y = centerY.toInt()),
                            dstSize = IntSize(btm.width, btm.height),
                        )
                    }
                }

                if (myTable.rowAmount != 0 && myTable.columnAmount != 0) {
                    tableLines.forEach { (x, y) ->
                        drawLine(
                            color = myTable.color,
                            start = Offset(x, y),
                            end = if (x == centerX) Offset(
                                centerX + myTable.columnAmount * myTable.tableScale, y
                            )
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
                            color = Color.Gray,
                            start = Offset(line.start.x, line.start.y),
                            end = Offset(line.end.x, line.end.y),
                            strokeWidth = 10f
                        )
                    }
                }

                lines.forEach { line ->
                    drawLine(
                        color = line.color,
                        start = Offset(line.start.x, line.start.y),
                        end = Offset(line.end.x, line.end.y),
                        strokeWidth = 10f
                    )
                }

                // Drawing dashed lines
                if (drawMode == DrawMode.DashLineDraw) {
                    temporaryDashedLine?.let { dashedLine ->
                        drawLine(
                            color = Color.Gray,
                            start = Offset(dashedLine.start.x, dashedLine.start.y),
                            end = Offset(dashedLine.end.x, dashedLine.end.y),
                            strokeWidth = 10f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                        )
                    }
                }

                dashedLines.forEach { dashedLine ->
                    drawLine(
                        color = dashedLine.color,
                        start = Offset(dashedLine.start.x, dashedLine.start.y),
                        end = Offset(dashedLine.end.x, dashedLine.end.y),
                        strokeWidth = 10f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                    )
                }

                // Drawing Arrow lines
                if (drawMode == DrawMode.ArrowLineDraw) {
                    temporaryArrowLine?.let { arrowLine ->
                        val path = Path()
                        path.moveTo(arrowLine.start.x, arrowLine.start.y)
                        path.lineTo(arrowLine.end.x, arrowLine.end.y)

                        drawPath(path, color = arrowLine.color, style = Stroke(width = 10f))

                        // Calculate arrowhead points
                        val angle = atan2(
                            arrowLine.end.y - arrowLine.start.y,
                            arrowLine.end.x - arrowLine.start.x
                        )
                        val arrowPoints = calculateArrowheadPoints(
                            Offset(arrowLine.end.x, arrowLine.end.y),
                            angle
                        )

                        // Draw the arrowhead
                        drawPath(arrowPoints, color = arrowLine.color, style = Stroke(width = 10f))
                    }
                }

                arrowLines.forEach { arrowLine ->

                    val path = Path()
                    path.moveTo(arrowLine.start.x, arrowLine.start.y)
                    path.lineTo(arrowLine.end.x, arrowLine.end.y)

                    drawPath(path, color = arrowLine.color, style = Stroke(width = 10f))

                    // Calculate arrowhead points
                    val angle = atan2(
                        arrowLine.end.y - arrowLine.start.y,
                        arrowLine.end.x - arrowLine.start.x
                    )
                    val arrowPoints =
                        calculateArrowheadPoints(Offset(arrowLine.end.x, arrowLine.end.y), angle)

                    // Draw the arrowhead
                    drawPath(arrowPoints, color = arrowLine.color, style = Stroke(width = 10f))
                }

                // Drawing Dashed Arrow lines
                if (drawMode == DrawMode.DashArrowLineDraw) {
                    temporaryDashedArrowLine?.let { dashedArrowLine ->
                        val path = Path()
                        path.moveTo(dashedArrowLine.start.x, dashedArrowLine.start.y)
                        path.lineTo(dashedArrowLine.end.x, dashedArrowLine.end.y)

                        drawPath(
                            path,
                            color = dashedArrowLine.color,
                            style = Stroke(
                                width = 10f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                            ),
                        )

                        // Calculate arrowhead points
                        val angle = atan2(
                            dashedArrowLine.end.y - dashedArrowLine.start.y,
                            dashedArrowLine.end.x - dashedArrowLine.start.x
                        )
                        val arrowPoints = calculateArrowheadPoints(
                            Offset(
                                dashedArrowLine.end.x,
                                dashedArrowLine.end.y
                            ), angle
                        )

                        // Draw the arrowhead
                        drawPath(
                            arrowPoints,
                            color = dashedArrowLine.color,
                            style = Stroke(width = 10f)
                        )
                    }
                }

                dashedArrowLines.forEach { dashedArrowLine ->

                    val path = Path()
                    path.moveTo(dashedArrowLine.start.x, dashedArrowLine.start.y)
                    path.lineTo(dashedArrowLine.end.x, dashedArrowLine.end.y)

                    drawPath(
                        path, color = dashedArrowLine.color,
                        style = Stroke(
                            width = 10f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                        )
                    ) // Modify stroke width as needed

                    // Calculate arrowhead points
                    val angle = atan2(
                        dashedArrowLine.end.y - dashedArrowLine.start.y,
                        dashedArrowLine.end.x - dashedArrowLine.start.x
                    )
                    val arrowPoints = calculateArrowheadPoints(
                        Offset(
                            dashedArrowLine.end.x,
                            dashedArrowLine.end.y
                        ), angle
                    )

                    // Draw the arrowhead
                    drawPath(
                        arrowPoints,
                        color = dashedArrowLine.color,
                        style = Stroke(width = 10f)
                    )
                }

                // draw triangle
                if (drawMode == DrawMode.TriangleDraw){
                    temporaryTriangle?.let { triangle ->
                        drawLine(color = Color.Gray, start = triangle.point1, end = triangle.point2, strokeWidth = 10f)
                        drawLine(color = Color.Gray, start = triangle.point2, end = triangle.point3, strokeWidth = 10f)
                        drawLine(color = Color.Gray, start = triangle.point3, end = triangle.point1, strokeWidth = 10f)
                    }
                }

                triangles.forEach { triangle ->
                    drawLine(color = triangle.color, start = triangle.point1, end = triangle.point2, strokeWidth = 10f)
                    drawLine(color = triangle.color, start = triangle.point2, end = triangle.point3, strokeWidth = 10f)
                    drawLine(color = triangle.color, start = triangle.point3, end = triangle.point1, strokeWidth = 10f)
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

                // draw Round rectangles
                if (drawMode == DrawMode.RoundRectDraw) {
                    temporaryRoundRectangle?.let { rectangle ->
                        drawRoundRect(
                            color = Color.Gray,
                            topLeft = Offset(rectangle.leftTop.x, rectangle.leftTop.y),
                            size = Size(rectangle.width, rectangle.height),
                            style = Stroke(10f),
                            cornerRadius = CornerRadius(16f, 16f)
                        )
                    }
                }

                roundRectangles.forEach { rectangle ->
                    drawRoundRect(
                        color = rectangle.color,
                        topLeft = Offset(rectangle.leftTop.x, rectangle.leftTop.y),
                        size = Size(rectangle.width, rectangle.height),
                        style = Stroke(10f),
                        cornerRadius = CornerRadius(20f, 20f)
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

                // Draw Half circles
                if (drawMode == DrawMode.HalfCircleDraw) {
                    temporaryHalfCircle?.let { halfCircle ->
                        val startAngle = if (halfCircle.isTop) 180f else 0f
                        val sweepAngle = 180f

                        drawArc(
                            color = halfCircle.color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = 5f),
                            topLeft = Offset(halfCircle.centerX - halfCircle.radius, halfCircle.centerY - halfCircle.radius),
                            size = Size(halfCircle.radius * 2, halfCircle.radius * 2)
                        )

                        // Draw the bottom line for the temporary half circle
                        val startX = halfCircle.centerX - halfCircle.radius
                        val startY = halfCircle.centerY
                        val endX = halfCircle.centerX + halfCircle.radius
                        val endY = startY

                        drawLine(
                            color = halfCircle.color,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = 5f
                        )
                    }
                }

                halfCircles.forEach { halfCircle ->
                    val startAngle = if (halfCircle.isTop) 180f else 0f
                    val sweepAngle = 180f

                    drawArc(
                        color = halfCircle.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 5f),
                        topLeft = Offset(halfCircle.centerX - halfCircle.radius, halfCircle.centerY - halfCircle.radius),
                        size = Size(halfCircle.radius * 2, halfCircle.radius * 2)
                    )

                    // Draw the bottom line
                    val startX = halfCircle.centerX - halfCircle.radius
                    val startY = halfCircle.centerY
                    val endX = halfCircle.centerX + halfCircle.radius
                    val endY = startY

                    drawLine(
                        color = halfCircle.color,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 5f
                    )
                }

                // Draw Octagon
                if (drawMode == DrawMode.OctagonDraw){
                    temporaryOctagon?.let { octagon ->
                        for (i in 0 until octagon.points.size) {
                            val startPoint = octagon.points[i]
                            val endPoint = octagon.points[(i + 1) % octagon.points.size]
                            drawLine(color = Color.Gray, start = startPoint, end = endPoint, strokeWidth = 10f)
                        }
                    }
                }

                octagons.forEach { octagon ->
                    for (i in 0 until octagon.points.size) {
                        val startPoint = octagon.points[i]
                        val endPoint = octagon.points[(i + 1) % octagon.points.size]
                        drawLine(color = octagon.color, start = startPoint, end = endPoint, strokeWidth = 10f)
                    }
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
                // delete all tableLines
                tableLines = emptyList()
                myTable.tableZoom = 1f
                myTable.rowAmount = 0
                myTable.columnAmount = 0
                //clear figures
                lines = emptyList()
                dashedLines = emptyList()
                arrowLines = emptyList()
                dashedArrowLines = emptyList()
                triangles = emptyList()
                rectangles = emptyList()
                circles = emptyList()
                halfCircles = emptyList()
                octagons = emptyList()

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
                myTable.tableZoom = 1f
                myTable.rowAmount = rowInt
                myTable.columnAmount = columnInt
            },
            myLauncher = launcher

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

private fun calculateArrowheadPoints(endPoint: Offset, angle: Float): Path {
    val arrowPath = Path()
    val arrowLength = 30f // Length of the arrowhead
    val arrowAngle = 150 // Angle of the arrowhead

    // Calculate the adjusted angle for the arrowhead


    val x1 = endPoint.x + arrowLength * cos(angle - Math.toRadians(arrowAngle.toDouble())).toFloat()
    val y1 = endPoint.y + arrowLength * sin(angle - Math.toRadians(arrowAngle.toDouble())).toFloat()

    val x2 = endPoint.x + arrowLength * cos(angle + Math.toRadians(arrowAngle.toDouble())).toFloat()
    val y2 = endPoint.y + arrowLength * sin(angle + Math.toRadians(arrowAngle.toDouble())).toFloat()

    arrowPath.moveTo(endPoint.x, endPoint.y)
    arrowPath.lineTo(x1, y1)
    arrowPath.moveTo(endPoint.x, endPoint.y)
    arrowPath.lineTo(x2, y2)

    return arrowPath
}

// Function to calculate octagon points based on the initial touch point and index
private fun calculateOctagonPoint(initialPoint: Offset, index: Int): Offset {
    val angle = index * (360 / 8) // Angle between each point
    val radius = 100f // Adjust the radius of the octagon as needed

    val x = initialPoint.x + radius * cos(Math.toRadians(angle.toDouble())) // X-coordinate calculation
    val y = initialPoint.y + radius * sin(Math.toRadians(angle.toDouble())) // Y-coordinate calculation

    return Offset(x.toFloat(), y.toFloat())
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
