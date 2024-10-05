package cn.netdiscovery.monica.ui.controlpanel.crop.cropimage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.crop.CropViewModel
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.OutlineType
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.RectCropShape
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.setting.*
import cn.netdiscovery.monica.ui.widget.rightSideMenuBar
import cn.netdiscovery.monica.ui.widget.toolTipButton
import cn.netdiscovery.monica.utils.OnCropPropertiesChange
import org.koin.compose.koinInject

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.CropImageView
 * @author: Tony Shen
 * @date: 2024/5/27 14:00
 * @version: V1.0 <描述当前版本功能>
 */
val cropTypes = mutableListOf(CropType.Dynamic, CropType.Static)
var cropTypesIndex = mutableStateOf(0)

val contentScales = listOf("None", "Fit", "Crop", "FillBounds", "FillWidth", "FillHeight", "Inside")
var contentScalesIndex = mutableStateOf(1)

@Composable
fun cropImage(state: ApplicationState) {

    val cropViewModel: CropViewModel = koinInject()

    val handleSize: Float = LocalDensity.current.run { 20.dp.toPx() }
    var croppedImage by remember { mutableStateOf<ImageBitmap?>(null) }
    var crop by remember { mutableStateOf(false) }

    var showSettingDialog by remember { mutableStateOf(false) }
    var showCropDialog by remember { mutableStateOf(false) }
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

    val imageBitmap = state.currentImage!!.toComposeImageBitmap()

    val cropFrameFactory = remember {
        CropFrameFactory(
            listOf(imageBitmap)
        )
    }

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
                imageBitmap = imageBitmap,
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
                    showCropDialog = true
                }
            )
        }

        rightSideMenuBar(modifier = Modifier.align(Alignment.CenterEnd)) {
            toolTipButton(text = "settings",
                painter = painterResource("images/cropimage/settings.png"),
                onClick = {
                    showSettingDialog = true
                })

            toolTipButton(text = "crop",
                painter = painterResource("images/cropimage/crop.png"),
                onClick = {
                    crop = true
                })
        }
    }

    if (showSettingDialog) {
        showCroppedImageSettingDialog(
            cropProperties, cropFrameFactory,
            onConfirm = {
                cropProperties = it

                showSettingDialog = false
            },
            onDismiss = {
                showSettingDialog = false
            }
        )
    }

    if (showCropDialog) {
        croppedImage?.let {
            showCroppedImageDialog(imageBitmap = it,
            onConfirm = {
                showCropDialog = !showCropDialog
                croppedImage = null

                cropViewModel.clearCropImageView()

                state.addQueue(state.currentImage!!)
                state.currentImage = it.toAwtImage()
                state.resetCurrentStatus()
                state.togglePreviewWindow(false)
            }, onDismiss = {
                showCropDialog = !showCropDialog
                croppedImage = null
            })
        }
    }
}

@Composable
private fun showCroppedImageSettingDialog(cropProperties:CropProperties,
                                          cropFrameFactory:CropFrameFactory,
                                          onConfirm: OnCropPropertiesChange,
                                          onDismiss: () -> Unit) {

    var tempProperties:CropProperties = cropProperties

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Crop Properties Settings",
                    color = MaterialTheme.colors.primary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                divider()

                cropTypeSelect(tempProperties) {
                    tempProperties = it
                }

                divider()

                contentScaleSelect(tempProperties) {
                    tempProperties = it
                }

                divider()

                aspectRatioScrollableRow(tempProperties) {
                    tempProperties = it
                }

                divider()

                cropFrameScrollableRow(tempProperties,cropFrameFactory) {
                    tempProperties = it
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(tempProperties)
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

@Composable
private fun divider() {
    Spacer(modifier = Modifier.padding(top = 15.dp, bottom = 15.dp))
}