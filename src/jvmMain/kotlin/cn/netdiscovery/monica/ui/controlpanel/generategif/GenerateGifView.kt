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
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.basicTextFieldWithTitle
import cn.netdiscovery.monica.ui.widget.centerToast
import cn.netdiscovery.monica.ui.widget.confirmButton
import cn.netdiscovery.monica.utils.chooseImage
import cn.netdiscovery.monica.utils.currentTime
import cn.netdiscovery.monica.utils.getValidateField
import com.madgag.gif.fmsware.AnimatedGifEncoder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import javax.imageio.ImageIO

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.generategif.GenerateGifView
 * @author: Tony Shen
 * @date:  2025/2/23 16:16
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

private var showVerifyToast by mutableStateOf(false)
private var verifyToastMessage by mutableStateOf("")

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun generateGif(state: ApplicationState) {
    var selectedImages by remember { mutableStateOf<List<File>>(emptyList()) }

    var widthText by remember { mutableStateOf("400") }
    var heightText by remember { mutableStateOf("400") }
    var frameDelayText by remember { mutableStateOf("500") }
    var loopEnabled by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Button(onClick = {
            chooseImage(state) {imageFile ->
                selectedImages += imageFile
            }
        }) {
            Text("添加图片")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedImages.isNotEmpty()) {
            Text("Selected Images:")
            Box(modifier = Modifier.height(600.dp).fillMaxWidth()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(selectedImages) { index, imageFile ->

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
                    }
                }
            }
        } else {
            Column(modifier = Modifier.height(600.dp).fillMaxWidth()) {
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
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            basicTextFieldWithTitle(titleText = "gif 宽", widthText, Modifier.padding(end = 20.dp)) { str ->
                widthText = str
            }

            basicTextFieldWithTitle(titleText = "gif 高", heightText, Modifier.padding(end = 20.dp)) { str ->
                heightText = str
            }

            basicTextFieldWithTitle(titleText = "每一帧间隔 (ms)", frameDelayText) { str ->
                frameDelayText = str
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {

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

                generateGif(selectedImages, width, height, frameDelay, loopEnabled)
        })
    }

    if (showVerifyToast) {
        centerToast(message = verifyToastMessage) {
            showVerifyToast = false
        }
    }
}

private fun showGenerateGifVerifyToast(message: String) {
    verifyToastMessage = message
    showVerifyToast = true
}

private fun generateGif(images: List<File>, width: Int, height: Int, frameDelay: Int, loopEnabled: Boolean) {
    logger.info("start to generate gif")
    val gifEncoder = AnimatedGifEncoder()
    gifEncoder.setSize(width, height)
    gifEncoder.start(FileOutputStream("output_${currentTime()}.gif"))

    gifEncoder.setDelay(frameDelay)
    gifEncoder.setRepeat(if (loopEnabled) 0 else 1) // Set loop option

    images.forEach { imageFile ->
        val image = ImageIO.read(imageFile)
        gifEncoder.addFrame(image)
    }

    gifEncoder.finish()

    showGenerateGifVerifyToast("gif 生成成功")
    logger.info("gif generated successfully!")
}