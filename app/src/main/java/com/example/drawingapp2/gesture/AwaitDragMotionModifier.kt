package com.example.drawingapp2.gesture

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput

suspend fun AwaitPointerEventScope.awaitDragMotionEvent(
    onTouchEvent: (MotionEvent, PointerInputChange) -> Unit
) {
    // Wait for at least one pointer to press down, and set first contact position
    val down: PointerInputChange = awaitFirstDown()
    onTouchEvent(MotionEvent.Down, down)

    var pointer = down

    // 🔥 Waits for drag threshold to be passed by pointer
    // or it returns null if up event is triggered
    val change: PointerInputChange? =
        awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->
            // 🔥🔥 If consumePositionChange() is not consumed drag does not
            // function properly.
            // Consuming position change causes change.positionChanged() to return false.
            change.consumePositionChange()
        }

    if (change != null) {
        // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
        drag(change.id) { pointerInputChange: PointerInputChange ->
            pointer = pointerInputChange
            onTouchEvent(MotionEvent.Move, pointer)
        }

        // All of the pointers are up
        onTouchEvent(MotionEvent.Up, pointer)
    } else {
        // Drag threshold is not passed and last pointer is up
        onTouchEvent(MotionEvent.Up, pointer)
    }
}

fun Modifier.dragMotionEvent(onTouchEvent: (MotionEvent, PointerInputChange) -> Unit) = this.then(
    Modifier.pointerInput(Unit) {
        forEachGesture {
            awaitPointerEventScope {
                awaitDragMotionEvent(onTouchEvent)
            }
        }
    }
)


suspend fun AwaitPointerEventScope.awaitDragMotionEvent(
    onDragStart: (PointerInputChange) -> Unit = {},
    onDrag: (PointerInputChange) -> Unit = {},
    onDragEnd: (PointerInputChange) -> Unit = {}
) {
    // Wait for at least one pointer to press down, and set first contact position
    val down: PointerInputChange = awaitFirstDown()
    onDragStart(down)

    var pointer = down

    // 🔥 Waits for drag threshold to be passed by pointer
    // or it returns null if up event is triggered
    val change: PointerInputChange? =
        awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->
            // 🔥🔥 If consumePositionChange() is not consumed drag does not
            // function properly.
            // Consuming position change causes change.positionChanged() to return false.
            change.consumePositionChange()
        }



    if (change != null) {
        // 🔥 Calls  awaitDragOrCancellation(pointer) in a while loop
        drag(change.id) { pointerInputChange: PointerInputChange ->
            pointer = pointerInputChange
            onDrag(pointer)
        }

        // All of the pointers are up
        onDragEnd(pointer)
    } else {
        // Drag threshold is not passed and last pointer is up
        onDragEnd(pointer)
    }
}

fun Modifier.dragMotionEvent(
    onDragStart: (PointerInputChange) -> Unit = {},
    onDrag: (PointerInputChange) -> Unit = {},
    onDragEnd: (PointerInputChange) -> Unit = {},
    //onTranslate: (zoom: Float, rotation: Float) -> Unit
) = this.then(
    Modifier.pointerInput(Unit) {
        forEachGesture {
            awaitPointerEventScope {
                awaitDragMotionEvent(onDragStart, onDrag, onDragEnd)
            }
        }


    }
)

suspend fun AwaitPointerEventScope.awaitMultiTouchDrag(
    onMultiTouchDragStart: (List<PointerInputChange>) -> Unit = {},
    onMultiTouchDrag: (List<PointerInputChange>) -> Unit = {},
    onMultiTouchDragEnd: (List<PointerInputChange>) -> Unit = {}
) {
    // Wait for at least one pointer to press down, and set the first contact position
    val initialEvent: PointerEvent = awaitPointerEvent(PointerEventPass.Initial)
    var activePointers: List<PointerInputChange> = listOf(initialEvent.changes.first())

    onMultiTouchDragStart(activePointers)

    while (true) {
        val event: PointerEvent = awaitPointerEvent()

        // Handle different types of pointer events
        when (event.type) {
            PointerEventType.Move -> {
                // Update the list of active pointers
                activePointers = event.changes.toList()
                onMultiTouchDrag(activePointers)
            }
            PointerEventType.Release -> {
                // Update the list of active pointers
                activePointers = event.changes.toList()
                onMultiTouchDragEnd(activePointers)
                break
            }
            // Handle other pointer events if needed
            // PointerEventType.Cancel, PointerEventType.Enter, PointerEventType.Leave, etc.
        }
    }
}

fun Modifier.multiTouchDragGesture(
    onMultiTouchDragStart: (List<PointerInputChange>) -> Unit = {},
    onMultiTouchDrag: (List<PointerInputChange>) -> Unit = {},
    onMultiTouchDragEnd: (List<PointerInputChange>) -> Unit = {},
) = this.then(
    Modifier.pointerInput(Unit) {
        forEachGesture {
            awaitPointerEventScope {
                awaitMultiTouchDrag(
                    onMultiTouchDragStart = onMultiTouchDragStart,
                    onMultiTouchDrag = onMultiTouchDrag,
                    onMultiTouchDragEnd = onMultiTouchDragEnd
                )
            }
        }
    }
)