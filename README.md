# 方案 B：独立安卓 App（可侧载，可选）

把「生产线体管理看板」的部署链接封装成一个**真正的 Android 应用**：

- 安装后是桌面独立图标「线体看板」，点开即**全屏加载看板**，体验等同原生 App。
- 数据保存在 App 自身 WebView 的 `localStorage` 中（与浏览器隔离、本机持久）。
- 看板本身仍是单一 HTML + `localStorage`，**前端零改动**；App 只是个全屏浏览器壳。

> 当前看板地址（在 `app/src/main/res/values/strings.xml` 的 `kanban_url` 维护）：
> `https://peter-d6giligzmb0ebb036-1468375977.tcloudbaseapp.com/`
>
> 该地址为 CloudBase 静态托管域名（已加入安全域名白名单），看板已开启**账号权限 + 联网多人同步**。App 壳本身零改动，只是全屏加载这个网页。

---

## 一、零安装云端构建（推荐，最省事）

你**不需要**在本机装 Android Studio / JDK / SDK。把仓库推到 GitHub，由 GitHub Actions 免费云端编译 APK：

1. 把本项目（含 `android-app/` 目录）推送到一个 GitHub 仓库。
2. 打开仓库 `Actions` 标签 → 找 **Build Kanban APK** 工作流 → 点 `Run workflow`。
   （之后只要改动 `android-app/**`，也会自动触发构建。）
3. 等约 3–5 分钟显示绿色 ✓，展开 `Artifacts`：
   - `app-debug-apk` → `app-debug.apk`（**直接用，侧载首选**）
   - `app-release-apk` → `app-release-unsigned.apk`（未签名 release，需自行对齐/签名后才能上架商店；侧载同样可用）

> 工作流定义在 `android-app/.github/workflows/build-apk.yml`：
> 用 `setup-java@17` + `gradle/gradle-build-action`（自带 Gradle 8.9），
> 因此**不依赖本地 `gradle-wrapper.jar`**，仓库保持轻量。

---

## 二、本地构建（已有 Android 环境时）

### 方式 A：Android Studio
1. `File → Open` 选择本 `android-app/` 目录，等待 Gradle 同步。
2. `Build → Build Bundle(s) / APK(s) → Build APK(s)`。
3. 产物：`app/build/outputs/apk/debug/app-debug.apk`。

### 方式 B：命令行（需本地有 JDK17 + Android SDK）
```bash
cd android-app
./gradlew assembleDebug        # Linux/Mac
gradlew.bat assembleDebug      # Windows
```
首次运行会自动下载 Gradle 8.9 与 AGP 依赖。产物同上。

---

## 三、侧载安装到平板

1. 把 `app-debug.apk` 拷到安卓平板（微信 / 数据线 / U 盘均可）。
2. 平板「设置 → 安全 → 安装未知应用」，允许你用来打开 APK 的那个 App（如「文件管理」/「浏览器」）。
3. 点击 APK，按提示安装。桌面出现「线体看板」图标，点开即全屏看板。
4. 首次打开需联网加载看板；加载失败会显示「重新加载」按钮。

> 已启用「保持屏幕常亮」「禁止双指缩放」「横屏全屏」，观感更像车间专用终端。

---

## 四、如何切换看板地址

只改一处即可，无需动 Java：

```
android-app/app/src/main/res/values/strings.xml
    → <string name="kanban_url">https://你的新地址</string>
```

重新构建即可。若日后看板迁到 CloudBase 真后台（多端数据互通），地址同样只改这一行。

## 五、技术要点

- `minSdk 26`（Android 8.0）、`targetSdk/compileSdk 34`，覆盖绝大多数平板。
- 明文流量已禁用（`usesCleartextTraffic=false`），看板必须是 **HTTPS**（CloudStudio 分享链接已是 HTTPS，符合）。
- WebView 已启用 JS、`DomStorageEnabled`（localStorage 持久化）、硬件加速。
- 自适应图标（foreground vector + 蓝色背景），无需另行准备多尺寸 PNG。
