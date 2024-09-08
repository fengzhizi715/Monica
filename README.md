![](images/screenshot.png)

# Monica
Monica 是一款跨平台的桌面图像编辑软件，使用 Kotlin Compose Desktop 作为 UI 框架。应用层使用 Kotlin 编写，基于 mvvm 架构，使用 koin 作为依赖注入框架。

大部分算法也是用 Kotlin 编写，少部分通过 jni 调用 OpenCV C++ 实现的图像处理或者调用深度学习的模型。

# 一. 简介
Monica 目前还处于开发阶段，当前提供的基础功能包括：

* 支持加载本地图片、网络图片。
* 支持对图片进行局部模糊、打马赛克。
* 支持对图片进行涂鸦，并保存涂鸦的结果。
* 支持图片取色功能。
* 支持图像的翻转、旋转、缩放、错切。
* 支持各种形状的裁剪，并保存裁剪的结果。
* 支持调整图片的饱和度、色相、亮度。
* 支持图像增强。
* 提供 20 多款滤镜，大多数滤镜可以单独调整参数。
* 保存修改的图像。

深度学习相关的功能包括：

* 人脸检测(人脸、性别、年龄)
* 生成素描画
* 替换人脸

# 二. 功能
截图较多，详细的功能跳转到单独的页面。[功能详见](FUNCTION.md)

# 三. CV 算法 && 调用深度学习的模型
本地的算法库：
https://github.com/fengzhizi715/MonicaImageProcess

目前在 Mac、Windows 环境下编译的了相关的算法库，Kotlin 通过 jni 来调用该算法库。(在开发过程中 MacOS 的算法库会比较新，因为我的开发环境是在 MacOS 下)

其中

| 库名        | 描述                                      |
|-----------|-----------------------------------------|
| libMonicaImageProcess.dylib | 基于 intel 芯片的 Mac OS 下编译好的算法库 |
| libMonicaImageProcess_aarch64.dylib | 基于 m 芯片的 Mac OS 下编译好的算法库 |
| MonicaImageProcess.dll | Windows 下编译好的算法库需要依赖 opencv_world481.dll |
| opencv_world481.dll | Windows 下基于 OpenCV 4.8.1 源码编译的 OpenCV 库 |


# 四. 更多详情

Monica 还有很多有意思的功能没有来得及介绍，欢迎体验、使用、提意见。

暂时还不提供安装包不过可自行编译，稍后会提供 Windows、Linux、MasOS 各个平台对应的安装包。

相关文章的介绍，在如下的专栏：

https://juejin.cn/column/7396157773312065574


# 五. TODO List：

* 重新规划架构
* Kotlin 版本升级到 2.0.0+
* 增加 OpenCV 算法的调试模块，方便快速验证算法
* 增加人脸美颜的算法
* 增加对图像添加文字的功能


# 六. Star History

[![Star History Chart](https://api.star-history.com/svg?repos=fengzhizi715/Monica&type=Date)](https://star-history.com/#fengzhizi715/Monica&Date)