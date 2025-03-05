package cn.netdiscovery.monica.ui.controlpanel.generategif

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.*
import cn.netdiscovery.monica.utils.chooseImage
import cn.netdiscovery.monica.utils.getValidateField
import org.koin.compose.koinInject
import java.io.File
import loadingDisplay

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.generategif.GenerateGifView
 * @author: Tony Shen
 * @date:  2025/2/23 16:16
 * @version: V1.0 <描述当前版本功能>
 */
private var showVerifyToast by mutableStateOf(false)
private var verifyToastMessage by mutableStateOf("")
private val height = 600.dp // 上传图片的区域

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun generateGif(state: ApplicationState) {
    val viewModel: GenerateGifViewModel = koinInject()

    var selectedImages by remember { mutableStateOf<List<File>>(emptyList()) }

    var widthText by remember { mutableStateOf("400") }
    var heightText by remember { mutableStateOf("400") }
    var frameDelayText by remember { mutableStateOf("500") }
    var loopEnabled by remember { mutableStateOf(false) }

    fun clear() {
        widthText = "400"
        heightText = "400"
        frameDelayText = "500"
        loopEnabled = false
        selectedImages = emptyList()
    }

    @Composable
    fun addImageCard(state:ApplicationState) {
        Card(onClick = {
            chooseImage(state) {imageFile ->
                selectedImages += imageFile
            }},
            modifier = Modifier.padding(10.dp).width(300.dp).height(150.dp), shape = RoundedCornerShape(8.dp))  {
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("请先添加图片")
            }
        }
    }

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedImages.isNotEmpty()) {
                subTitle(text = "选择下列图片", color = Color.Black)

                Box(modifier = Modifier.height(height).fillMaxWidth()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val emptyFile = File("")
                        itemsIndexed(selectedImages + emptyFile) { index, imageFile ->

                            if (index < selectedImages.size) {
                                Card(modifier = Modifier.padding(10.dp), shape = RoundedCornerShape(8.dp)) {

                                    Column(modifier = Modifier.padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        val bitmap = remember(imageFile) { BufferedImages.load(imageFile).toComposeImageBitmap() }

                                        Image(painter = BitmapPainter(bitmap), contentDescription = imageFile.name, modifier = Modifier.size(100.dp))

                                        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                            Button(onClick = {
                                                selectedImages = selectedImages.toMutableList().apply { removeAt(index) }
                                            }) {
                                                Text("Delete")
                                            }

                                            if (index > 0) {
                                                Button(onClick = {
                                                    selectedImages = selectedImages.toMutableList().apply {
                                                        add(index - 1, removeAt(index))
                                                    }
                                                }) {
                                                    Text("Up")
                                                }
                                            }

                                            if (index < selectedImages.size - 1) {
                                                Button(onClick = {
                                                    selectedImages = selectedImages.toMutableList().apply {
                                                        add(index + 1, removeAt(index))
                                                    }
                                                }) {
                                                    Text("Down")
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                addImageCard(state)
                            }
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.height(height).fillMaxWidth()) {
                    addImageCard(state)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            subTitleWithDivider(text = "gif 生成策略", color = Color.Black)

            Row {
                basicTextFieldWithTitle(titleText = "gif 宽", widthText, Modifier.padding(end = 20.dp)) { str ->
                    widthText = str
                }

                basicTextFieldWithTitle(titleText = "gif 高", heightText, Modifier.padding(end = 20.dp)) { str ->
                    heightText = str
                }
            }

            Row(modifier = Modifier.padding(top = 20.dp)) {
                basicTextFieldWithTitle(titleText = "每一帧间隔 (ms)", frameDelayText) { str ->
                    frameDelayText = str
                }
            }

            Row(modifier = Modifier.padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("是否循环播放")
                Checkbox(checked = loopEnabled, onCheckedChange = { loopEnabled = it })
            }

            Spacer(modifier = Modifier.height(16.dp))

            confirmButton(
                enabled = selectedImages.isNotEmpty(),
                text = "生成 gif",
                onClick = {
                    val width = getValidateField(block = { widthText.toInt() } , failed = { showGenerateGifVerifyToast("width 需要 int 类型") }) ?: return@confirmButton
                    val height = getValidateField(block = { heightText.toInt() } , failed = { showGenerateGifVerifyToast("height 需要 int 类型") }) ?: return@confirmButton
                    val frameDelay = getValidateField(block = { frameDelayText.toInt() } , failed = { showGenerateGifVerifyToast("frameDelay 需要 int 类型") }) ?: return@confirmButton

                    viewModel.generateGif(state, selectedImages, width, height, frameDelay, loopEnabled) {
                        showGenerateGifVerifyToast("gif 已生成")
                        clear()
                    }
                })
        }

        if (loadingDisplay) {
            showLoading()
        }

        if (showVerifyToast) {
            centerToast(message = verifyToastMessage) {
                showVerifyToast = false
            }
        }
    }
}

private fun showGenerateGifVerifyToast(message: String) {
    verifyToastMessage = message
    showVerifyToast = true
}