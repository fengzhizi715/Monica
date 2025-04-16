package cn.netdiscovery.monica.ui.controlpanel.cartoon

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.*
import cn.netdiscovery.monica.utils.chooseImage
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.cartoon.CartoonView
 * @author: Tony Shen
 * @date: 2025/4/16 17:32
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun cartoon(state: ApplicationState) {

    val viewModel: CartoonViewModel = koinInject()

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Row (
            modifier = Modifier.fillMaxSize().padding(bottom = 160.dp, end = 400.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxSize().padding(10.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp,
                onClick = {
                    chooseImage(state) { file ->
                        state.rawImage = BufferedImages.load(file)
                        state.currentImage = state.rawImage
                        state.rawImageFile = file
                    }
                },
                enabled = state.currentImage == null
            ) {
                if (state.currentImage == null) {
                    Text(
                        modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
                        text = "请点击选择图像",
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp
                    )
                } else {
                    Image(
                        painter = state.currentImage!!.toPainter(),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(start = 20.dp, bottom = 20.dp, top = 160.dp).align(Alignment.BottomStart)) {
            subTitle(text = "请选择下列动漫风格", modifier = Modifier.padding(start = 10.dp), fontWeight = FontWeight.Bold)

            desktopLazyRow(modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(100.dp)) {
                Card(
                    elevation = 16.dp,
                    modifier = Modifier.fillMaxSize().padding(start = 5.dp).clickable{

                    }
                ) {
                    Text(
                        text = "宫崎骏风格", fontSize = 22.sp,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.width(200.dp).wrapContentSize(Alignment.Center)
                    )
                }

                Card(
                    elevation = 16.dp,
                    modifier = Modifier.fillMaxSize().padding(start = 5.dp).clickable{

                    }
                ) {
                    Text(
                        text = "日系人像风格", fontSize = 22.sp,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.width(200.dp).wrapContentSize(Alignment.Center)
                    )
                }

                Card(
                    elevation = 16.dp,
                    modifier = Modifier.fillMaxSize().padding(start = 5.dp).clickable{

                    }
                ) {
                    Text(
                        text = "黑白线稿", fontSize = 22.sp,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.width(200.dp).wrapContentSize(Alignment.Center)
                    )
                }

                Card(
                    elevation = 16.dp,
                    modifier = Modifier.fillMaxSize().padding(start = 5.dp).clickable{

                    }
                ) {
                    Text(
                        text = "新海诚风格", fontSize = 22.sp,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.width(200.dp).wrapContentSize(Alignment.Center)
                    )
                }

                Card(
                    elevation = 16.dp,
                    modifier = Modifier.fillMaxSize().padding(start = 5.dp).clickable{

                    }
                ) {
                    Text(
                        text = "可爱风格", fontSize = 22.sp,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.width(200.dp).wrapContentSize(Alignment.Center)
                    )
                }
            }
        }

        rightSideMenuBar(modifier = Modifier.width(400.dp).height(450.dp).align(Alignment.CenterEnd), backgroundColor = Color.White, percent = 3) {

//            Column {
//                if (filterSelectedIndex.value>=0) {
//                    subTitle(text = "${filterNames[filterSelectedIndex.value]} 滤镜", modifier = Modifier.padding(start =10.dp, bottom = 10.dp), fontWeight = FontWeight.Bold)
//                    generateFilterParams(filterSelectedIndex.value)
//                    generateFilterRemark(filterSelectedIndex.value)
//                } else {
//                    subTitle(text = "请先选择一款滤镜", modifier = Modifier.padding(start = 10.dp), fontWeight = FontWeight.Bold)
//                }
//            }
//
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.BottomCenter // 将内容对齐到底部中心
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(10.dp),
//                    horizontalArrangement = Arrangement.SpaceEvenly // 按钮水平分布
//                ) {
//                    toolTipButton(text = "预览效果",
//                        enable = { state.currentImage != null && filterSelectedIndex.value >= 0 },
//                        painter = painterResource("images/filters/preview.png"),
//                        onClick = {
//                            viewModel.applyFilter(state, filterSelectedIndex.value, filterTempMap)
//                        })
//
//                    toolTipButton(text = "上一步",
//                        painter = painterResource("images/doodle/previous_step.png"),
//                        onClick = {
//                            state.getLastImage()?.let {
//                                state.currentImage = it
//                            }
//                        })
//
//                    toolTipButton(text = "取消滤镜操作",
//                        painter = painterResource("images/filters/cancel.png"),
//                        onClick = {
//                            viewModel.job?.cancel()
//                            loadingDisplay = false
//                        })
//
//                    toolTipButton(text = "保存",
//                        painter = painterResource("images/doodle/save.png"),
//                        onClick = {
//                            viewModel.clear()
//                            state.closePreviewWindow()
//                        })
//
//                    toolTipButton(text = "删除原图",
//                        painter = painterResource("images/preview/delete.png"),
//                        onClick = {
//                            state.clearImage()
//                        })
//                }
//            }
        }

//        if (loadingDisplay) {
//            showLoading()
//        }
    }
}