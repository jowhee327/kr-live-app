# Korean TV Live Streaming Android App

## 项目概述
构建一个韩国电视台直播 Android APP，同时支持手机和 Android TV 盒子。

## 技术要求
- **语言:** Kotlin
- **最低 API:** 21 (Android 5.0)
- **播放器:** ExoPlayer / Media3
- **UI:** Jetpack Compose (手机端) + Leanback (TV 端)
- **构建:** Gradle (Kotlin DSL)
- **直播源:** 远程 JSON 配置，支持热更新

## 核心功能 (MVP)
1. 频道列表页 - 显示所有频道，带台标和名称
2. 播放页 - 全屏播放直播流 (HLS m3u8)
3. 手机端 / TV 端双适配 (运行时检测 UI_MODE_TYPE_TELEVISION)
4. 远程直播源配置 (从 URL 加载 JSON 频道列表)
5. 基本播放控制 (暂停/播放、音量、进度)
6. 频道收藏功能
7. 多源备用切换 (一个源挂了自动尝试下一个)

## 双端适配策略
- 一个 APK，运行时判断
- TV 端: 大卡片网格布局，D-Pad 焦点导航，Leanback 风格
- 手机端: Material Design 列表布局，触屏操作，支持横竖屏

## 直播源 JSON 格式
```json
{
  "version": 1,
  "updated": "2026-03-20",
  "channels": [
    {
      "id": "arirang_tv",
      "name": "Arirang TV",
      "category": "综合",
      "logo": "https://i.imgur.com/Asu5pE9.png",
      "streams": [
        {
          "url": "http://amdlive-ch01.ctnd.com.edgesuite.net/arirang_1ch/smil:arirang_1ch.smil/playlist.m3u8",
          "label": "1080p",
          "priority": 1
        }
      ]
    },
    {
      "id": "arirang_radio",
      "name": "Arirang Radio",
      "category": "综合",
      "logo": "https://i.imgur.com/dtfiG9k.png",
      "streams": [
        {
          "url": "http://amdlive-ch01.ctnd.com.edgesuite.net/arirang_3ch/smil:arirang_3ch.smil/playlist.m3u8",
          "label": "720p",
          "priority": 1
        }
      ]
    },
    {
      "id": "kbs_world",
      "name": "KBS World",
      "category": "综合",
      "logo": "https://i.imgur.com/5VF8mLq.png",
      "streams": [
        {
          "url": "http://worldlive.kbs.co.kr/worldtvlive_h.m3u8",
          "label": "720p",
          "priority": 1
        }
      ]
    },
    {
      "id": "ktv",
      "name": "Korea TV (KTV)",
      "category": "新闻",
      "logo": "https://i.ibb.co/WkrLDJW/Screenshot-20230714-135036-removebg-preview.png",
      "streams": [
        {
          "url": "https://hlive.ktv.go.kr/live/klive_h.stream/playlist.m3u8",
          "label": "1080p",
          "priority": 1
        }
      ]
    },
    {
      "id": "ebs1",
      "name": "EBS 1",
      "category": "教育",
      "logo": "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e2/EBS_1TV_Logo.svg/960px-EBS_1TV_Logo.svg.png",
      "streams": [
        {
          "url": "https://ebsonair.ebs.co.kr/ebs1familypc/familypc1m/playlist.m3u8",
          "label": "400p",
          "priority": 1
        }
      ]
    },
    {
      "id": "ebs2",
      "name": "EBS 2",
      "category": "教育",
      "logo": "https://upload.wikimedia.org/wikipedia/commons/thumb/d/db/EBS_2TV_Logo.svg/960px-EBS_2TV_Logo.svg.png",
      "streams": [
        {
          "url": "https://ebsonair.ebs.co.kr/ebs2familypc/familypc1m/playlist.m3u8",
          "label": "400p",
          "priority": 1
        }
      ]
    },
    {
      "id": "mbc_chuncheon",
      "name": "MBC 春川",
      "category": "综合",
      "logo": "https://i.imgur.com/lthyx2P.png",
      "streams": [
        {
          "url": "https://stream.chmbc.co.kr/TV/myStream/playlist.m3u8",
          "label": "480p",
          "priority": 1
        }
      ]
    },
    {
      "id": "mbc_daejeon",
      "name": "MBC 大田",
      "category": "综合",
      "logo": "https://i.imgur.com/HCbvAiM.png",
      "streams": [
        {
          "url": "https://ns1.tjmbc.co.kr/live/myStream.sdp/playlist.m3u8",
          "label": "720p",
          "priority": 1
        }
      ]
    },
    {
      "id": "abn",
      "name": "ABN TV",
      "category": "综合",
      "logo": "https://www.abn.co.kr/resources/images/logo.png",
      "streams": [
        {
          "url": "https://vod2.abn.co.kr/IPHONE/abn.m3u8",
          "label": "720p",
          "priority": 1
        }
      ]
    },
    {
      "id": "bbs",
      "name": "BBS 佛教放送",
      "category": "宗教",
      "logo": "https://i.imgur.com/01UmXRe.png",
      "streams": [
        {
          "url": "http://bbstv.clouducs.com:1935/bbstv-live/livestream/playlist.m3u8",
          "label": "1080p",
          "priority": 1
        }
      ]
    },
    {
      "id": "gugak",
      "name": "国乐放送",
      "category": "音乐",
      "logo": "https://i.imgur.com/Ey7Htm8.png",
      "streams": [
        {
          "url": "https://mgugaklive.nowcdn.co.kr/gugakvideo/gugakvideo.stream/playlist.m3u8",
          "label": "1080p",
          "priority": 1
        }
      ]
    },
    {
      "id": "job_plus",
      "name": "Job Plus TV",
      "category": "综合",
      "logo": "https://i.imgur.com/Y5Zg5SR.png",
      "streams": [
        {
          "url": "https://live.jobplustv.or.kr/live/wowtvlive1.sdp/playlist.m3u8",
          "label": "480p",
          "priority": 1
        }
      ]
    }
  ]
}
```

## 项目结构建议
```
app/
├── src/main/
│   ├── java/com/koreatv/live/
│   │   ├── KoreaTVApp.kt              # Application class
│   │   ├── MainActivity.kt             # 入口 Activity
│   │   ├── ui/
│   │   │   ├── mobile/                 # 手机端 UI
│   │   │   │   ├── MobileChannelList.kt
│   │   │   │   └── MobilePlayerScreen.kt
│   │   │   ├── tv/                     # TV 端 UI
│   │   │   │   ├── TvChannelGrid.kt
│   │   │   │   └── TvPlayerScreen.kt
│   │   │   └── common/                 # 共用组件
│   │   │       ├── PlayerControls.kt
│   │   │       └── ChannelCard.kt
│   │   ├── data/
│   │   │   ├── Channel.kt             # 数据模型
│   │   │   ├── ChannelRepository.kt   # 数据源管理
│   │   │   └── RemoteConfigLoader.kt  # 远程配置加载
│   │   ├── player/
│   │   │   └── StreamPlayer.kt        # ExoPlayer 封装
│   │   └── util/
│   │       └── DeviceUtils.kt         # 设备类型检测
│   └── res/
│       ├── layout/
│       ├── values/
│       └── drawable/
├── build.gradle.kts
└── settings.gradle.kts
```

## 注意事项
- APP 名称: 韩流直播 / Korea TV Live
- 包名: com.koreatv.live
- 所有频道都用 HLS (m3u8) 格式
- 内置默认频道列表作为 fallback
- 支持从远程 URL 更新频道列表
- UI 用中文
- 配色: 深色主题为主 (适合看电视)
