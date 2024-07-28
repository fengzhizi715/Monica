# Monica
Monica 是一款跨平台的桌面图像编辑软件，使用 Kotlin Compose Desktop 作为 UI 框架。

由于应用层是由 Kotlin 编写的，Monica 基于 mvvm 架构，使用 koin 作为依赖注入框架。

部分图像处理算法使用 OpenCV C++ 或调用深度学习的模型。

# 一. 简介
Monica 目前还处于开发阶段，当前版本的功能包括：

* 支持加载本地图片、网络图片。
* 支持对图片进行局部模糊、打马赛克。
* 支持对图片进行涂鸦，并保存涂鸦的结果。
* 支持图片取色功能。
* 支持图像的翻转、旋转、缩放。
* 支持各种形状的裁剪，并保存裁剪的结果。
* 调整图片的饱和度、色相、亮度。
* 支持图像增强。
* 提供 20 多款滤镜，大多数滤镜可以单独调整参数。
* 保存修改的图像。


# 二. 功能
截图较多，功能跳转到单独的页面。[功能详见](FUNCTION.md)

# 三. 本地的 CV 算法
OpenCV 使用的算法在：
https://github.com/fengzhizi715/MonicaImageProcess

目前在 Mac、Windows 环境下编译的了相关的算法库，Kotlin 通过 jni 来调用该算法库。

其中

| 库名        | 描述                                      |
|-----------|-----------------------------------------|
| libMonicaImageProcess.dylib | 基于 intel 芯片的 Mac OS 下编译好的算法库            |
| libMonicaImageProcess_aarch64.dylib | 基于 m 芯片的 Mac OS 下编译好的算法库                |
| MonicaImageProcess.dll | Windows 下编译好的算法库                        |
| opencv_world481.dll | Windows 下基于 OpenCV 4.8.1 源码编译的 OpenCV 库 |

# 四. 更多详情

Monica 还有很多有意思的功能没有来得及介绍，欢迎体验和使用，更欢迎提意见。

目前，暂时还不提供安装包不过可自行编译，稍后会提供各个平台的安装包。

之前介绍的文章：

https://juejin.cn/post/7393189744330014758

https://juejin.cn/post/7381652250815086643

https://juejin.cn/post/7374238968685920282

https://juejin.cn/post/7365711904159612954

# 五. TODO List：

* 增加对图像添加文字的功能。
* 考虑增加人脸美颜的算法。
* 考虑多种格式的导入。