# Monica
Monica 是一款跨平台的桌面图像编辑软件(早期是为了验证一些算法而产生的)。

其技术栈如下：
* Kotlin 编写 UI(Kotlin Compose Desktop 作为 UI 框架)
* 基于 mvvm 模式，依赖注入使用 koin，编译使用 JDK 17
* 部分算法使用 Kotlin 实现
* 其余的算法使用 OpenCV C++ 来实现，Kotlin 通过 jni 来调用。
* Monica 所使用的模型，主要使用 ONNXRuntime 进行部署和推理
* 其余少部分模型使用 OpenCV DNN 进行部署和推理。
* 本地的算法库使用 C++ 17 编译

![](images/screenshot.png)
![](images/screenshot-version.png)

# 一. 简介
Monica 目前还处于开发阶段，当前提供的基础功能：

* 支持图像放大预览。
* 支持对图像进行局部模糊、打马赛克。
* 支持对图像进行涂鸦，并保存涂鸦的结果。
* 支持对图像绘制各种形状和动态添加文字，并保存结果。
* 支持图像取色功能。
* 支持图像的翻转、旋转、缩放、错切。
* 支持各种形状的裁剪，并保存裁剪的结果。
* 支持调整图像的对比度、色调、饱和度、亮度、色温、高光、阴影等。
* 提供 20 多款滤镜，大多数滤镜可以单独调整参数。
* 提供快速验证 OpenCV 算法的功能(目前只能提供一些简单算法的调参)

深度学习相关的功能：

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


> 目前，有三个模型没有提交到 github，主要是太大了，每个都超过了 100 M。我把他们放到百度网盘
> 
> 链接: https://pan.baidu.com/s/15XhVHKi-vPGjB2hYa33v_A?pwd=d9mm
> 
> 链接: https://pan.baidu.com/s/1cZvMSuOGxl8CuyHdJM27kw?pwd=9mun
> 
> 链接: https://pan.baidu.com/s/10bIlCT08XPtUd_GBOGH6cw?pwd=xu4f
> 
> 模型下载下来，存放的目录是在工程的 /resources/common/ 目录下


# 四. 更多详情
Monica 还有很多有意思的功能没有来得及介绍，欢迎体验、使用、提意见(提需求)、参与开发。

暂时还不提供安装包。不过可自行编译，后续版本会提供 Windows、Linux、MasOS 各个平台对应的安装包。

Windows 用户的体验安装包: 
> 链接: https://pan.baidu.com/s/1QrauiGLKUtozkKKB4JKLiQ?pwd=u9jn
> 
> 由于是体验版本，少了一些深度学习相关的功能。如果加上这些功能的话，打包超过了 1G，主要是模型太大。后续的方向是把模型部署到云端。


相关文章的介绍，在如下的专栏：

https://juejin.cn/column/7396157773312065574


# 五. TODO List：

* 完善 CV 调试模块
* 部分模型部署到云端
* 增加滤镜
* UI 层性能优化
* 拆分成多模块的应用
* 绘制完的图形，支持可以拖动
* 增加人脸美颜的算法


# 六. 更新日志
请查看 [CHANGELOG](CHANGELOG.md) 文件


# 七. 联系方式：

wechat：fengzhizi715

Email：fengzhizi715@126.com


# 八. Star History

[![Star History Chart](https://api.star-history.com/svg?repos=fengzhizi715/Monica&type=Date)](https://star-history.com/#fengzhizi715/Monica&Date)