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
import cn.netdiscovery.monica.utils.chooseImage
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

@Composable
fun generateGif(state: ApplicationState) {
    var selectedImages by remember { mutableStateOf<List<File>>(emptyList()) }
    var frameDelay by remember { mutableStateOf(500) }
    var loopEnabled by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Spacer(modifier = Modifier.height(16.dp))

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
                Card(modifier = Modifier.padding(10.dp).width(300.dp).height(150.dp), shape = RoundedCornerShape(8.dp))  {
                    Row(horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("请先添加图片")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Frame Delay (ms): $frameDelay")
        Slider(
            value = frameDelay.toFloat(),
            onValueChange = { frameDelay = it.toInt() },
            valueRange = 100f..2000f,
            steps = 19
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = loopEnabled, onCheckedChange = { loopEnabled = it })
            Text("Loop GIF")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            generateGif(selectedImages, frameDelay, loopEnabled)
        }) {
            Text("生成 gif")
        }
    }
}

private fun generateGif(images: List<File>, frameDelay: Int, loopEnabled: Boolean) {
    logger.info("start to generate gif")
    val gifEncoder = AnimatedGifEncoder()
    gifEncoder.setSize(900, 1000)
    gifEncoder.start(FileOutputStream("output.gif"))

    gifEncoder.setDelay(frameDelay)
    gifEncoder.setRepeat(if (loopEnabled) 0 else 1) // Set loop option

    images.forEach { imageFile ->
        val image = ImageIO.read(imageFile)
        gifEncoder.addFrame(image)
    }

    gifEncoder.finish()

    logger.info("GIF generated successfully!")
}