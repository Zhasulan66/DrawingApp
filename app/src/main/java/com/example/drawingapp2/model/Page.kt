package com.example.drawingapp2.model

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

data class Page(
    /**
     * Paths that are added, this is required to have paths with different options and paths
     *  ith erase to keep over each other
     */
    var paths: SnapshotStateList<Pair<Path, PathProperties>>,

    /**
     * Paths that are undone via button. These paths are restored if user pushes
     * redo button if there is no new path drawn.
     *
     * If new path is drawn after this list is cleared to not break paths after undoing previous
     * ones.
     */
    var pathsUndone: SnapshotStateList<Pair<Path, PathProperties>>,

    //list for all figures
    var figures: List<Figure>,

    var myTable: Table,
    var bgType: Int,
    var pageBackground: Color,

    //for image
    var imageUri: Uri?,
    var bitmap: Bitmap?,
    val launcher: ManagedActivityResultLauncher<String, Uri?>,

    // Calculate offsets for centering the table
    var centerX: Float = 0f,
    var centerY: Float = 0f


)
