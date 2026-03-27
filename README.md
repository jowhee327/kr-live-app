# 韩流直播 (Korea TV Live)

韩国电视直播 Android 应用，支持手机和 Android TV 双平台。单 APK 根据设备类型自动切换 UI，提供 HLS (m3u8) 直播流播放、频道收藏、远程频道源配置等功能。

## 技术栈

| 类别 | 技术 |
|------|------|
| 语言 | Kotlin 2.1.0 |
| 构建工具 | Gradle 8.11.1 / AGP 8.7.3 (Kotlin DSL) |
| UI 框架 | Jetpack Compose + Material 3 |
| TV 支持 | Android TV (Compose 实现，Leanback 仅 manifest 声明) |
| 播放器 | ExoPlayer (Media3 1.5.1)，支持 HLS |
| 网络 | OkHttp 4.12.0 |
| 序列化 | Kotlinx Serialization 1.7.3 |
| 图片加载 | Coil 2.7.0 |
| 最低 SDK | 21 (Android 5.0) |
| 目标 SDK | 35 (Android 15) |

## 构建与运行

```bash
# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK（启用 ProGuard 混淆）
./gradlew assembleRelease

# 完整构建（Debug + Release）
./gradlew build

# 安装到已连接的设备
./gradlew installDebug
```

构建产物位于 `app/build/outputs/apk/`。

## 项目结构

```
app/src/main/
├── AndroidManifest.xml
├── kotlin/com/koreatv/live/
│   ├── KoreaTvApp.kt                     # Application 入口
│   ├── data/
│   │   ├── model/Channel.kt              # 数据模型（频道、流地址）
│   │   ├── local/PreferencesManager.kt   # SharedPreferences 本地存储
│   │   └── repository/ChannelRepository.kt  # 频道数据源（远程→缓存→内置三级回退）
│   ├── ui/
│   │   ├── mobile/                        # 手机端 UI
│   │   │   ├── MainActivity.kt
│   │   │   ├── MobileChannelListScreen.kt
│   │   │   └── MobilePlayerScreen.kt
│   │   ├── tv/                            # Android TV 端 UI（D-Pad 导航）
│   │   │   ├── TvActivity.kt
│   │   │   ├── TvChannelGridScreen.kt
│   │   │   └── TvPlayerScreen.kt
│   │   ├── player/PlayerViewModel.kt      # 共享播放状态管理 (MVVM)
│   │   ├── settings/SettingsScreen.kt     # 频道源 URL 设置
│   │   └── theme/Theme.kt                # 深色主题配置
│   └── util/DeviceUtils.kt               # 设备类型检测
└── res/                                   # 资源文件（颜色、字符串、图标）
```

### 架构要点

- **单 APK 双平台**：运行时通过 `UiModeManager` 检测设备类型，自动路由到手机或 TV 界面
- **MVVM 架构**：`PlayerViewModel` 管理播放状态，通过 Compose StateFlow 驱动 UI
- **三级频道加载**：优先从远程 URL 获取频道列表，失败时回退到本地缓存，最后使用 12 个内置频道
- **流地址容错**：每个频道支持多个流地址，按优先级排序，播放失败自动切换下一个
