# 番茄钟 Android 应用

一个简洁优雅的番茄钟（Pomodoro Timer）Android 应用，帮助你提高工作和学习效率。

## 功能特性

- **三种计时模式**：
  - 工作模式：25分钟专注时间
  - 短休息：5分钟休息时间
  - 长休息：15分钟休息时间（每完成4个番茄钟后）

- **直观的界面**：
  - 圆形进度条显示剩余时间
  - 大字体显示计时器
  - 不同模式使用不同颜色区分

- **提醒功能**：
  - 计时结束时播放系统提示音
  - 振动提醒（需要设备支持）
  - 自动切换到下一个模式

- **统计功能**：
  - 记录已完成的番茄钟数量
  - 数据持久化保存

- **简单易用**：
  - 开始/暂停/重置按钮
  - 手动切换模式
  - 深色主题保护眼睛

## 系统要求

- Android 7.0 (API 24) 或更高版本
- 需要振动权限
- 建议屏幕尺寸：5英寸及以上

## 安装方法

### 方法一：使用 Android Studio

1. 下载并安装 [Android Studio](https://developer.android.com/studio)
2. 打开 Android Studio，选择 "Open an Existing Project"
3. 选择 `PomodoroTimer` 文件夹
4. 等待 Gradle 同步完成
5. 连接 Android 设备或启动模拟器
6. 点击 "Run" 按钮或按 `Shift + F10`

### 方法二：使用命令行

```bash
# 进入项目目录
cd PomodoroTimer

# 构建 APK
./gradlew assembleDebug

# 安装到连接的设备
./gradlew installDebug
```

生成的 APK 文件位于：`app/build/outputs/apk/debug/app-debug.apk`

## 使用说明

1. **开始计时**：点击"开始"按钮开始倒计时
2. **暂停计时**：点击"暂停"按钮暂停计时器
3. **重置计时**：点击"重置"按钮将计时器重置到当前模式的初始时间
4. **切换模式**：点击"切换模式"按钮手动切换工作/休息模式

### 工作流程

1. 开始 25 分钟的工作时间
2. 工作完成后，应用会自动切换到休息模式
3. 完成 4 个番茄钟后，会获得 15 分钟的长休息
4. 循环重复，保持高效工作状态

## 项目结构

```
PomodoroTimer/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/pomodorotimer/
│   │       │   └── MainActivity.java          # 主活动
│   │       ├── res/
│   │       │   ├── drawable/                  # 图形资源
│   │       │   ├── layout/
│   │       │   │   └── activity_main.xml      # 主界面布局
│   │       │   └── values/
│   │       │       ├── strings.xml            # 字符串资源
│   │       │       ├── colors.xml             # 颜色资源
│   │       │       └── themes.xml             # 主题样式
│   │       └── AndroidManifest.xml            # 应用清单
│   └── build.gradle                           # 应用级构建配置
├── build.gradle                               # 项目级构建配置
├── settings.gradle                            # Gradle 设置
└── README.md                                  # 项目说明
```

## 技术栈

- **语言**：Java
- **最低 SDK**：API 24 (Android 7.0)
- **目标 SDK**：API 34 (Android 14)
- **UI 框架**：XML 布局 + Material Design
- **依赖库**：
  - AndroidX AppCompat
  - Material Design Components
  - ConstraintLayout

## 自定义配置

如需修改时间设置，可以在 `MainActivity.java` 中修改以下常量：

```java
private static final int WORK_TIME = 25 * 60;      // 工作时间（秒）
private static final int SHORT_BREAK = 5 * 60;     // 短休息（秒）
private static final int LONG_BREAK = 15 * 60;     // 长休息（秒）
private static final int POMODOROS_UNTIL_LONG_BREAK = 4; // 长休息间隔
```

如需修改颜色，可以在 `app/src/main/res/values/colors.xml` 中修改：

```xml
<color name="workColor">#FF6B6B</color>          <!-- 工作模式颜色 -->
<color name="shortBreakColor">#4ECDC4</color>     <!-- 短休息颜色 -->
<color name="longBreakColor">#95E1D3</color>      <!-- 长休息颜色 -->
```

## 常见问题

### Q: 应用无法安装？
A: 请确保你的设备 Android 版本为 7.0 或更高，并且已允许安装来自未知来源的应用。

### Q: 没有声音提示？
A: 请检查设备音量设置，确保系统提示音已开启。

### Q: 没有振动提醒？
A: 某些设备可能不支持振动，或者应用没有获得振动权限。

### Q: 如何关闭振动？
A: 可以在设备设置中管理应用权限，或者在代码中注释掉振动相关代码。

## 开发者信息

本应用遵循番茄工作法（Pomodoro Technique），由 Francesco Cirillo 在 1980 年代创立。

## 许可证

本项目仅供学习和个人使用。

## 更新日志

### v1.0 (2024)
- 初始版本发布
- 实现基本番茄钟功能
- 添加三种计时模式
- 实现声音和振动提醒
- 添加番茄钟统计功能
