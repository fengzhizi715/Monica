**Monica** is a cross-platform desktop image editor.
It supports a wide range of image formats (including camera RAW), integrates both traditional image processing and deep learning–based image enhancement, and provides an extensible, developer-friendly editing experience.

# 🧪 Tech Stack

* **UI Framework**: Kotlin Compose Multiplatform (Desktop)
* **Image Processing**: OpenCV
* **Deep Learning Inference**: ONNX Runtime
* **Backend Languages**: Kotlin / C++
* **Build Tools**: Gradle / CMake

# ✨ Features
## 📷 Image Editing

* Import: JPG, PNG, WebP, SVG, HDR, HEIC
* Import camera RAW files: CR2, CR3, etc.
* Export: JPG, PNG, WebP
* Zoom and preview
* Local blur & mosaic
* Freehand drawing, shapes, and text annotations
* Color picker
* Geometric transforms: flip, rotate, scale, shear
* Cropping with multiple shapes
* Adjustments: contrast, hue, saturation, brightness, temperature, highlights, shadows
* 50+ adjustable filters
* Multi-image → GIF creation
* Quick validation of OpenCV algorithms with parameter tuning

## 🤖 AI-powered Enhancements

* Face detection (face, gender, age)
* Sketch generation from photos
* Face replacement
* Cartoonization with multiple styles

# 📦 Installation & Usage
## Run from Source

Use IntelliJ IDEA / IntelliJ IDEA CE

```
git clone https://github.com/fengzhizi715/Monica.git
```

## 🍎 macOS Packages

### Intel Chip:
Monica-x64-1.1.3.dmg

Download Link: https://pan.baidu.com/s/1_vSjT4NBLJpxzzJ-kfrNmQ?pwd=hyaa

### Apple Silicon (M Series):
Monica-arm64-1.1.3.dmg

Download Link: https://pan.baidu.com/s/1vPOKW_QAiT1nRa30SX33Ag?pwd=np4g

## 🖥 Windows Package

Monica-1.0.9.exe (latest version will be provided later, no Windows machine available now)

Download Link: https://pan.baidu.com/s/1jL0bL17Omxtc2rqOBn9yWg?pwd=5dii

## 🐧 CentOS Package

Coming soon

# 📸 Screenshots
## ✨ New UI Preview

Support for **English UI + Multiple Themes**

English UI examples:

![](images/screenshot-en1.png)

![](images/screenshot-en2.png)

Theme switching:
![](images/ui-theme-settings.png)

Dark Theme:


Purple Theme:


📷 Classic Features










More screenshots 👉 Feature Overview

Articles 👉 Juejin Column

# 📁 CV & AI Services
⚙️ CV Algorithms

Code repo 👉 https://github.com/fengzhizi715/MonicaImageProcess

Currently, prebuilt algorithm libraries are available for macOS and Windows. Kotlin calls them via JNI.

Library Name	Version	Description	Notes
libMonicaImageProcess.dylib	0.2.3	Prebuilt for macOS	Built with CLion
libopencv_world.4.10.0.dylib	–	OpenCV 4.10.0 prebuilt for macOS	Built with CMake
MonicaImageProcess.dll	0.2.1	Prebuilt for Windows, depends on opencv_world481.dll	Built with Visual Studio 2022
opencv_world481.dll	–	OpenCV 4.8.1 prebuilt for Windows	Built with Visual Studio 2022
☁️ Deep Learning Services

Monica communicates with deep learning inference services via HTTP.
You need to set the Algorithm Service URL in General Settings.

Source code & models 👉 https://github.com/fengzhizi715/MonicaImageProcessHttpServer

No online deployment provided. Feel free to build and run locally.

# 💻 Roadmap

* - [x] Multi-format import/export
* - [x] Core image editing features
* - [x] AI module integration
* - [ ] Plugin system support
* - [ ] More AI features (face retouching, background removal, style transfer, etc.)

Upcoming TODO:

* Unified error handling
* Improved configuration management
* Enhanced cropping tools
* Face retouching
* Image compression
* Upgrade Kotlin Compose Desktop & third-party libraries

# 🤝 Contributing

Contributions of all kinds are welcome: new features, bug fixes, docs, or feedback.

# 📄 License

Apache License 2.0

# 📝 Changelog

See [CHANGELOG](CHANGELOG.md)

# 📬 Contact

WeChat: fengzhizi715

Email: fengzhizi715@126.com

# 📈 Star History

[![Star History Chart](https://api.star-history.com/svg?repos=fengzhizi715/Monica&type=Date)](https://star-history.com/#fengzhizi715/Monica&Date)