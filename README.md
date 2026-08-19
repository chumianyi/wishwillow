# 许愿柳 Wish Willow

一款基于 Three.js 3D WebGL 渲染的安卓许愿游戏，由 Android WebView 包裹，Neumorphism 新拟态 UI 设计。

## 游戏玩法

打开应用直接进入许愿界面，输入愿望后点击「许愿」按钮：

- 3D 柳树伸出一只手，抓住柳枝并做掰断动作
- **白色光（40%）**：许愿成功，无代价，白色柔光绽放
- **红色光（35%）**：许愿成功，需付出代价（随机抽取：三年寿命/一段记忆/一次好运等）
- **没掰断（25%）**：柳枝弯曲但未断，弹回原状，许愿失败

## 技术栈

- **3D 引擎**：Three.js r160（WebGL 渲染）
- **后期处理**：EffectComposer + UnrealBloomPass 泛光
- **粒子系统**：Points + BufferGeometry 自定义粒子
- **UI 设计**：Neumorphism 新拟态（HTML/CSS 浮层）
- **安卓壳**：Kotlin + Jetpack + Fullscreen WebView
- **构建**：GitHub Actions 云端构建 release APK

## 应用信息

- 应用名：许愿柳
- 包名：com.chumian.wishwillow
- 版本：v1.0.0
- 最低 SDK：Android 7.0（API 24）
- 架构：arm64-v8a

## 构建

```bash
./gradlew assembleRelease
```

APK 输出路径：`app/build/outputs/apk/release/`
