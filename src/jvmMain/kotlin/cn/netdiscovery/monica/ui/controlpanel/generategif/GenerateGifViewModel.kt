package cn.netdiscovery.monica.ui.controlpanel.generategif

import cn.netdiscovery.monica.utils.currentTime
import com.madgag.gif.fmsware.AnimatedGifEncoder
import java.io.File
import java.io.FileOutputStream
import javax.imageio.ImageIO

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.generategif.GenerateGifViewModel
 * @author: Tony Shen
 * @date: 2025/3/4 14:06
 * @version: V1.0 <描述当前版本功能>
 */
class GenerateGifViewModel {

    fun generateGif(images: List<File>, width: Int, height: Int, frameDelay: Int, loopEnabled: Boolean) {

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
    }
}