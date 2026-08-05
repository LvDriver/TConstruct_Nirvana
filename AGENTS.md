# AGENTS.md — 项目约定

> 本文件会随每次请求注入，是缓存命中的核心前缀。**保持精简、稳定，不要频繁改动。**
> 需要更新的内容（进度、决策、Bug）请写进 devlog.md，而不是这里。

## 项目概况
- Mod ID：`tconstruct_nirvana`（全小写，仅 [a-z0-9_]，不含版本号）
- 显示名：Tinkers' Construct: Nirvana（displayName 可含特殊字符，仅作展示）
- 版本：0.0.1
- 目标：Minecraft 1.21.1 · NeoForge 21.1.x（该 MC 版本对应系列的最新稳定版）· Java 21
- 语言：Java（如改用 Kotlin 需在此注明）
- 主包名：`com.lvdriver.tconstruct_nirvana`
- 构建：Gradle + Gradle Wrapper
- Mappings：Official (Mojang)，全程不切换
- 移植来源：基于匠魂怀古（Tinkers' Antique，1.12.2 Forge）移植，**不参考匠魂3**（1.14+ 玩法改动过大）
- 兼容性目标：500mod 整合包，须考虑 JEI / 矿物词典 / 常见前置兼容

## 构建命令
- 运行客户端：`./gradlew runClient`
- 数据生成：`./gradlew runData`
- 编译打包：`./gradlew build`（产物在 `build/libs/*.jar`）

## 目录结构
- `src/main/java/com/lvdriver/tconstruct_nirvana/` — 主代码
- `src/main/resources/META-INF/neoforge.mods.toml` — Mod 元数据
- `src/generated/` — DataGen 输出目录，**勿手改**，改数据请重跑 `runData`
- `./TinkersAntique-1.12/` — 1.12.2 匠魂怀古源码（仅参考功能逻辑，不照搬 API；此目录非当前项目代码）

## 开发约定
- 注册一律使用 `DeferredRegister`，禁止逐类手动注册（物品/方块/实体/配方等）
- Registry 类组织：`ModItems` / `ModBlocks` / `ModEntities` / `ModFluids` / `ModRecipeTypes` 集中管理，每个子系统一个类，禁止散落注册
- 数据驱动内容用 DataGen 生成，不手写 JSON
- 客户端专用逻辑放 `client` 子包，与服务端严格分端（注意 `@OnlyIn`/事件分发）
- 包结构：`com.lvdriver.tconstruct_nirvana` → 子包 `item / block / entity / fluid / data / recipe / event / client / gui / util / world`
- 新 API 不确定时，以 NeoForge 官方文档与 MDK 模板为准，**不臆造 API 签名**

## 1.21.1 核心迁移约定（务必遵守）
- **DataComponent 优先**：物品数据一律用 `DataComponents.*` / 自定义 `DataComponentType` 注册 + `DataComponentMap`，禁止用 NBT tag / `getOrCreateTag` / `ItemStack#getTag`
- **事件总线分挂**：
  - 注册类事件 → `NeoForge.MOD_EVENT_BUS`
  - 运行期事件 → `NeoForge.EVENT_BUS`
  - 用 `@EventBusSubscriber` 时务必指明 `bus` 参数，不依赖默认值
- **旧代码引用**：参考匠魂怀古源码时，明确指定文件路径、单文件读取，禁止整目录扫描；旧代码的 import / API 调用一律视为"待重写"，不直接复制
- **Mantle 替代**：旧代码引用 `slimeknights.mantle.*` 的工具类时，用 1.21.1 NeoForge 原生 API 重写到 `util` 子包，不移植 Mantle

## 资源约定
- 贴图/模型：先用旧版资源，后续手动重画
- 本地化：lang 文件用 DataGen 生成
- 资源文件引用旧版时，从 `./TinkersAntique-1.12/` 对应路径读取，复制到 `src/main/resources/` 下

## 协作规则（省钱关键，必须遵守）
1. 只改任务相关的文件，保持最小 diff
2. 每完成一个模块跑一次 `./gradlew build`，编译通过再继续下一步
3. 解释简短：结论在前，不要输出长篇背景分析
4. 重要决策、进度、Bug 写进 `devlog.md`，不要重复输出历史细节
5. 沿用项目现有代码风格与命名
6. 需要上下文时优先读取 devlog.md 和相关源文件，不要整库扫描
7. 跨会话接力：每次开工先读 devlog.md 了解进度，收工前更新 devlog.md

## 已踩过的坑（随开发补充，每项一行）
- （暂无）
