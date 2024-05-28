package cn.netdiscovery.monica.ui.controlpanel.crop.cropimage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.rxcache.rxCache
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.OutlineType
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.RectCropShape
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.setting.CropDefaults
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.setting.CropOutlineProperty

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.crop.CropImageView
 * @author: Tony Shen
 * @date: 2024/5/27 14:00
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun cropImage(state: ApplicationState) {
    val handleSize: Float = LocalDensity.current.run { 20.dp.toPx() }
    var croppedImage by remember { mutableStateOf<ImageBitmap?>(null) }
    var crop by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var isCropping by remember { mutableStateOf(false) }

    var cropProperties by remember {
        mutableStateOf(
            CropDefaults.properties(
                cropOutlineProperty = CropOutlineProperty(
                    OutlineType.Rect,
                    RectCropShape(0, "Rect")
                ),
                handleSize = handleSize
            )
        )
    }
    var cropStyle by remember { mutableStateOf(CropDefaults.style()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            ImageCropper(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                imageBitmap = state.currentImage!!.toComposeImageBitmap(),
                contentDescription = "Image Cropper",
                cropStyle = cropStyle,
                cropProperties = cropProperties,
                crop = crop,
                onCropStart = {
                    isCropping = true
                },
                onCropSuccess = {
                    croppedImage = it
                    isCropping = false
                    crop = false
                    showDialog = true
                }
            )
        }

        Row(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 10.dp)) {

            Column(
                Modifier.padding(end = 10.dp),
                verticalArrangement = Arrangement.Center
            ) {

                OutlinedButton(
                    modifier = Modifier.width(80.dp),
                    onClick = {
                        crop = true
                    },
                ) {
                    Text("crop")
                }
            }
        }
    }

    if (showDialog) {
        croppedImage?.let {
            showCroppedImageDialog(imageBitmap = it, onConfirm = {
                showDialog = !showDialog
                croppedImage = null

                cropFlag.set(false)
                rxCache.remove("crop-first")
                state.addQueue(state.currentImage!!)
                state.currentImage = it.toAwtImage()
                state.isCropSize = false
                state.togglePreviewWindow(false)
            }, onDismiss = {
                showDialog = !showDialog
                croppedImage = null
            })
        }
    }
}

@Composable
private fun showCroppedImageDialog(imageBitmap: ImageBitmap,
                                   onConfirm: () -> Unit,
                                   onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit,
                bitmap = imageBitmap,
                contentDescription = "result"
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}