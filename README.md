**Monica** 是一款跨平台的桌面图像编辑软件。它不仅支持多种图像格式（包括相机 RAW），还集成了传统图像处理和基于深度学习的图像增强功能，提供可扩展、可二次开发的图像编辑体验。

# 🧪 技术栈
* **UI 框架**：Kotlin Compose Multiplatform (Desktop)
* **图像处理**：OpenCV
* **深度学习推理**：ONNX Runtime
* **后端语言**：Kotlin / C++
* **构建工具**：Gradle / CMake

# ✨ 功能列表

## 图像处理功能
* 支持导入：JPG、PNG、WebP、SVG、HDR、HEIC
* 支持相机 RAW 文件导入：CR2、CR3 等
* 支持导出：JPG、PNG、WebP
* 图像放大预览
* 局部模糊、马赛克处理
* 涂鸦、绘制形状、添加文字
* 图像取色
* 图像几何变换：翻转、旋转、缩放、错切
* 支持各种形状的裁剪
* 调整参数：对比度、色调、饱和度、亮度、色温、高光、阴影
* 50+ 可调节滤镜
* 多图合成 GIF
* 快速验证 OpenCV 算法，支持简单算法的调参

## 深度学习增强功能
* 人脸检测(人脸、性别、年龄)
* 图像生成素描画
* 替换人脸
* 多种风格的漫画生成

# 📦 安装与运行

## 运行方式
可以通过源码进行编译和运行，使用的 IDE:  IntelliJ IDEA、IntelliJ IDEA CE

```
git clone https://github.com/fengzhizi715/Monica.git
```

## macOS 安装包
### Intel 芯片：
Monica-x64-1.1.0.dmg
链接: https://pan.baidu.com/s/19TPhrbhCrKgv30KlYVvXGw?pwd=kdub 

### M 芯片：
Monica-arm64-1.1.0.dmg
链接: https://pan.baidu.com/s/1wPgG6jJFwMYrDted8O-Jpw?pwd=ykh1

## CentOS 安装包
稍后提供

## Windows 安装包
Monica-1.0.9.exe 
链接: https://pan.baidu.com/s/1jL0bL17Omxtc2rqOBn9yWg?pwd=5dii

# 📸 项目截图
![](images/screenshot.png)

![](images/screenshot-version.png)

![](images/4-2.png)

![](images/5-2.png)

![](images/7-2.png)

更多截图可以访问: [详细功能介绍](FUNCTION.md)

也可以访问相关的专栏: https://juejin.cn/column/7396157773312065574

# 📁 CV 算法 && 深度学习的服务

## CV 算法

CV 算法的地址：
https://github.com/fengzhizi715/MonicaImageProcess

目前在 Mac、Windows、Linux 环境下编译的了相关的算法库，Kotlin 通过 jni 来调用该算法库。


| 库名                                  | 版本号   | 描述                                       | 备注                       |
|-------------------------------------|-------|------------------------------------------|--------------------------|
| libMonicaImageProcess.dylib         | 0.2.1 | macOS 下编译好的算法库                           | 使用 CLion 编译              |
| libopencv_world.4.10.0.dylib        |       | macOS 下基于 OpenCV 4.10.0 源码编译的 OpenCV 库   | 使用 cmake 编译              |
| MonicaImageProcess.dll              | 0.2.1 | Windows 下编译好的算法库需要依赖 opencv_world481.dll | 使用 Visual Studio 2022 编译 |
| opencv_world481.dll                 |       | Windows 下基于 OpenCV 4.8.1 源码编译的 OpenCV 库  | 使用 Visual Studio 2022 编译 |


## 深度学习的服务

深度学习的服务部署在云端， Monica 需要在“通用设置”中设置“算法服务的url”才能调用相关的服务。(Monica 通过 http 协议来调用算法服务，实现推理)

深度学习相关服务的源码和模型：https://github.com/fengzhizi715/MonicaImageProcessHttpServer

> 我并没有来得及在云端部署相关服务，感兴趣可以下载在本地编译和部署


# 💻 项目计划：
* - [x] 多格式导入导出支持
* - [x] 图像基础编辑功能
* - [x] 深度学习模块集成
* - [ ] 支持插件机制
* - [ ] 添加更多 AI 功能（如人脸美颜、去背景、风格化等）

近期的 TODO : 

* 增加人脸美颜的功能
* 重构形状绘制模块
* 升级 Kotlin Compose desktop、第三方库的版本
* 优化 UI
* 国际化


# 🤝 贡献方式
欢迎任何形式的贡献，包括但不限于功能开发、Bug 修复、文档完善和使用反馈。


# 📄 开源协议
本项目基于 Apache License 2.0 开源。


# 📝 更新日志

请查看 [CHANGELOG](CHANGELOG.md) 文件


# 📬 联系方式：

wechat：fengzhizi715

Email：fengzhizi715@126.com


# 📈 Star History

[![Star History Chart](https://api.star-history.com/svg?repos=fengzhizi715/Monica&type=Date)](https://star-history.com/#fengzhizi715/Monica&Date)