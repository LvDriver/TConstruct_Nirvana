# devlog.md — 开发日志（跨会话外部记忆）

> 用法：**每次开工**先让 AI 读本文件；**每次收工**更新以下小节。
> 目的：把"需要记住的事"从对话上下文搬到文件里，AI 按需读取，省 token、防遗忘。

## 项目状态
- 当前阶段：脚手架已搭建（NeoForge 21.1.248 MDK 工程，build/runData/runClient 均验证通过）
- 最后更新：2026-08-05
- 旧源码路径：`./TinkersAntique-1.12/`（已解压，匠魂怀古 1.12.2 源码）

## 待办（按优先级）
- [x] 分析匠魂怀古源码模块结构，列出要移植的子系统清单
- [x] 创建 NeoForge 1.21.1 MDK 工程（版本对齐：NeoForge 21.1.248 / Java 21 / Gradle 8.14.2）
- [ ] 确定 Mod 核心玩法（1:1 还原匠魂怀古，后续再调整/新增）
- [ ] 会话2：材料与矿物系统（材料定义 + 钴/阿迪特矿 + 锭粒 + 世界生成 BiomeModifier + TCon 创造标签页）
- [ ] 定义注册清单：物品 / 方块 / 流体 / 实体 / 配方类型等
- [ ] 配置 DataGen 并跑通 `runData`

## 关键决策
| 日期 | 决策 | 原因 |
|------|------|------|
| 2026-08-05 | Mod ID 定为 `tconstruct_nirvana`（全小写 [a-z0-9_]），版本独立为 0.0.1 | 符合 NeoForge modid 规则，版本号不混入 ID；短 ID 便于命令/日志/数据包引用 |
| 2026-08-05 | 主包名定为 `com.lvdriver.tconstruct_nirvana` | Java 包名不能含 `'`，保留 lvdriver 域名 |
| 2026-08-05 | 接入 DeepSeek V4 Flash + Reasonix 做长期开发 | 成本与代码能力平衡 |
| 2026-08-05 | 注册用 DeferredRegister、数据用 DataGen | 官方推荐，减少样板代码与 token 消耗 |
| 2026-08-05 | AGENTS.md 保持精简稳定 + devlog.md 承载可变信息 | 最大化缓存命中，降低长期成本 |
| 2026-08-05 | 每模块完成即 `./gradlew build` 验证 | 小步验证，避免大段返工烧 token |
| 2026-08-05 | 基于匠魂怀古（1.12.2）移植，不参考匠魂3 | 匠魂3 玩法与匠魂2 差异过大，移植目标就是保留匠魂2 玩法 |
| 2026-08-05 | 物品数据用 DataComponent，禁止 NBT tag | 1.21.1 已用 DataComponent 替代 NBT，旧写法无法运行 |
| 2026-08-05 | 事件总线区分 MOD_BUS / EVENT_BUS | 挂错总线事件不触发，是迁移高频坑 |
| 2026-08-05 | Mappings 固定 Official (Mojang) | 切换 mapping 会使全部缓存与 API 引用失效 |
| 2026-08-05 | Mantle 不移植，工具类按需重写到 util 包 | Mantle 无 1.21.1 版本；1.20 版为匠魂3 设计，与怀古不匹配；按需重写工作量可控 |
| 2026-08-05 | 全套移植，1:1 还原匠魂怀古玩法 | 后续再新增和调整 |
| 2026-08-05 | 资源先用旧版，后续手动重画 | 避免等待资源阻塞开发 |
| 2026-08-05 | 必须考虑 500mod 整合包兼容（JEI/矿物词典等） | 目标就是整合包使用 |
| 2026-08-05 | 冶炼炉多方块系统放到后期 | 最复杂的子系统，先铺基础设施 |
| 2026-08-05 | 脚手架版本：NeoForge 21.1.248 / moddev 2.0.91 / Gradle 8.14.2 / Parchment 2024.11.17 / JDK 21（Temurin） | 官方 MDK 21.1-mdg 分支模板；NeoForge 取 21.1.x 最新稳定版 |
| 2026-08-05 | 网络环境特殊：services.gradle.org、plugins.gradle.org、mavenCentral 均不可达；Gradle 发行版走腾讯镜像，maven 仓库走腾讯 nexus maven-public 兜底 | 国内网络；本地已有 JDK 21，故移除官方 MDK 的 foojay toolchain 插件 |
| 2026-08-05 | Parchment 官方仓库 maven.parchmentmc.org 已加入 build.gradle repositories（重定向到 ldtteam.jfrog.io/GCS，不稳定） | 兜底下载源 |
| 2026-08-05 | 按子系统拆分 Reasonix 会话，串行推进 | 避免上下文膨胀导致缓存失效和代码不一致 |

## 子系统清单
> 2026-08-05 首次分析 `./TinkersAntique-1.12/src/main/java/slimeknights/tconstruct/`，顶层 8 个子包：common / library / tools / smeltery / world / shared / gadgets / plugin

| 子系统 | 核心类路径 | 功能 | Mantle依赖 | 迁移难度 | 优先级 |
|---|---|---|---|---|---|
| 材料系统 | `library/materials/`（Material、IMaterialStats、Head/Handle/Extra/Bow/BowString/ArrowShaft/Fletching/Projectile MaterialStats、MaterialTypes） | 材料标识/颜色/流体关联/craftable+castable/8 类属性/特质挂载；物品价值常量体系（Ingot=144、Nugget=16、Shard=72、Gem=666） | RecipeMatchRegistry（继承）、RecipeMatch | 中高 | 1（基础，先做） |
| 矿物与金属 | `shared/block/`（BlockOre、BlockMetal、BlockCommonOre、BlockCommonMetal）、`shared/TinkerCommons`、`common/TinkerOredict` | 钴/阿迪特矿石与金属块；锭/粒/块经矿物词典（ingotCobalt 等）与 Material 关联 | EnumBlock、ItemBlockMeta | 中 | 1 |
| 工具部件 | `library/tools/ToolPart`、`library/tinkering/MaterialItem`、`IToolPart` | 部件 = 材料 + cost（材质存 NBT Tags.PART_MATERIAL）；经 PartMaterialType 校验与工具绑定 | LocUtils（tooltip 换行） | 中高 | 1 |
| 模具 | `library/tools/Pattern`（部件模具）、`library/smeltery/Cast` + `ICast`（铸造模具） | Pattern 单 Item 多 PartType（NBT 存）；Cast 分空白/带部件、金/石材质 | 无 | 中 | 2 |
| 工具组装 | `library/utils/ToolBuilder`、`library/tinkering/TinkersItem`（+ ToolCore/SwordCore/AoeToolCore） | tryBuildTool 按部件槽组装工具并校验；基类管部件槽/分类/耐久/命名/物品实体 | RecipeMatch、ItemStackList | 高 | 1 |
| 修饰符 | `library/modifiers/`（Modifier、IModifier、ModifierAspect、ModifierNBT）+ `tools/modifiers/`（25 个）+ `tools/traits/`（InfiTool、ToolGrowth、40+ Trait） | 修饰符 = 增强 + aspect 约束 + 冲突检测；Trait 随材料/部件自动附带 | RecipeMatchRegistry、RecipeMatch | 高（60+ 类逐个移植） | 2 |
| 冶炼炉 | `smeltery/`（TinkerSmeltery、block/、tileentity/、multiblock/、inventory/、network/）+ `library/smeltery/SmelteryTank` | 多方块冶炼炉/焦黑熔炉/铸造盆台/排液口/通道；TileSmeltery + MultiblockSmeltery 控制 | MultiServantLogic、IMasterLogic、TileInventory、NetworkWrapper | 极高 | 5（已定后期） |
| 流体 | `library/fluid/`（FluidMolten、FluidTankBase、FluidTankAnimated 等）+ `shared/TinkerFluids` | 熔融金属流体注册、储罐/通道、流速动画；与材料、配方关联 | 无（纯 Forge Fluid API） | 中高（1.21.1 Fluid API 全变） | 3（冶炼炉前置） |
| 配方系统 | `library/smeltery/`（MeltingRecipe、CastingRecipe、AlloyRecipe、OreCastingRecipe、BucketCastingRecipe）+ `tools/common/RepairRecipe`、`TableRecipeFactory` | 冶炼/铸造/合金/桶铸配方（含熔点 temp 字段）；工具修复、部件合成配方 | RecipeMatch、RecipeMatchRegistry | 中高 | 3 |
| GUI | `tools/common/inventory/`（14 个 Container）+ `tools/common/client/`（11 个 Gui）+ `smeltery/client/`（5 个 Gui） | 工具站/工具锻造厂/部件加工台/模具桌/合成站/熔炉 GUI，含合成与槽位逻辑 | BaseContainer、ContainerMultiModule、GuiMultiModule、GuiModule、GuiElement、IInventoryGui | 高（Mantle GUI 框架全重写） | 2（随工具组装） |
| 世界生成 | `world/worldgen/`（7 个史莱姆岛/池/湖生成器）+ `shared/worldgen/`（NetherOreGenerator、OverworldOreGenerator）+ `world/village/loot/VillageLoot` | 史莱姆岛/树/矿池、钴/阿迪特矿石生成；村庄宝箱战利品（workshop_patterns / workshop_parts） | 无（纯 Forge） | 中高（1.21.1 改数据驱动 worldgen） | 4 |

### Mantle 依赖汇总（全库 grep 统计：约 60 个类、380 处引用）
- pulsar（Pulse 模块系统，TinkerIntegration/TinkerSmeltery 等入口）→ NeoForge 模块化替代
- RecipeMatch / RecipeMatchRegistry（材料与修饰符的物品匹配核心，最关键依赖）→ 重写到 `util` 包（ItemStack/TagKey 匹配）
- network（AbstractPacket、NetworkWrapper）→ NeoForge payload 网络
- inventory/gui（BaseContainer、ContainerMultiModule、GuiMultiModule、GuiElement、GuiElementScalable 等）→ NeoForge Menu/AbstractContainerScreen 重写
- block/item（EnumBlock、EnumBlockSlab、ItemBlockMeta、BlockInventory、ItemMetaDynamic）→ 原生 API 重写
- tileentity（TileInventory、MantleTileEntity）→ 原生 BlockEntity
- multiblock（MultiServantLogic、IMasterLogic、IServantLogic）→ 重写到 `smeltery` 子包（仅冶炼炉用）
- client book/model（BookData、GuiBook、TRSRBakedModel、BakedWrapper、CustomTextureCreator）→ 匠魂宝典后期再议；模型用原版 BakedModel 体系

### 分析要点（迁移时参考）
- 注册中枢：`library/TinkerRegistry`（静态注册表）+ `TinkerIntegration`（Pulse 模式汇总材料/流体/矿物词典）→ 1.21.1 改 DeferredRegister + ModItems/ModBlocks/ModFluids 集中类
- 工具全部数据存 NBT（Tags.PART_MATERIAL、ToolNBT、ModifierNBT、Pattern TAG_PARTTYPE）→ 全部改 DataComponent
- 匠魂 1.12 无显式"流体温度"概念：只有配方 temp 字段（熔点），炉温由加热结构（TileHeatingStructure）实现
- 材料与修饰符的 RecipeMatch 体系是全局地基（Material 和 Modifier 都继承它），优先重写

## 已知 Bug
- （无）

## 已踩过的坑（随开发补充）
- `.m2` 本地 Maven 仓库（`C:\Users\<user>\.m2\repository`）中曾有损坏的 parchment zip（25KB，正常 889KB），因 `mavenLocal()` 排仓库首位被优先选中，导致 jst 报 `ZipException: zip END header not found`、`createMinecraftArtifacts` 失败 → 删掉 `.m2/repository/org/parchmentmc` 后恢复（gradle modules-2 缓存有完好副本）
- services.gradle.org 不可达时，`gradle/wrapper/gradle-wrapper.properties` 的 `distributionUrl` 已指向腾讯镜像（`mirrors.cloud.tencent.com/gradle/`），`networkTimeout` 已调至 60000

## 会话记录
### 2026-08-06 会话1：脚手架搭建（完成）
- 按官方 MDK `archive/1.21-mdg` 分支模板创建工程：build.gradle / settings.gradle / gradle.properties / src/main/templates/META-INF/neoforge.mods.toml（含 JEI 可选依赖）/ pack.mcmeta / .gitignore
- 主类 `TConstructNirvana`（@Mod），构造器挂接 7 个 DeferredRegister 注册类 + 注册 COMMON 配置
- 注册类骨架：ModItems / ModBlocks / ModBlockEntities / ModFluids（FluidType+Fluid 双注册表）/ ModRecipeTypes / ModDataComponents / ModCreativeTabs
- 配置类 TConConfig（预留 worldgen 两个开关）；DataGen 入口 TConDataGen（GatherDataEvent，空骨架）
- Gradle Wrapper 8.14.2 安装完毕，distributionUrl 改腾讯镜像
- **验证全通过**：`./gradlew build`（BUILD SUCCESSFUL）、`./gradlew runData`（DataGen 管线正常，0 provider）、`./gradlew runClient`（启动→加载 mod→配置文件生成→进入单人世界→正常退出）
- 已知告警：NeoForge 21.1.248 中 `EventBusSubscriber.Bus` 被标记过时（forRemoval，21.1.x 内可用），`bus = Bus.MOD` 仍是官方标准写法，暂保留
- 下一步：会话2 材料与矿物系统
- 确立长期协作约定：AGENTS.md（稳定前缀）+ devlog.md（外部记忆）
- 补充 1.21.1 迁移核心约定：DataComponent / 事件总线分挂 / mappings 固定 / 旧代码引用方式
- 明确移植来源：匠魂怀古（1.12.2），不参考匠魂3
- 确认 Mantle 不移植，工具类按需重写
- 确认全套移植、1:1 还原、500mod 兼容、冶炼炉后期
- 确认按子系统拆分会话串行开发
- 分析匠魂怀古源码顶层结构：8 个子包（common / library / tools / smeltery / world / shared / gadgets / plugin）
- 逐子系统识别 11 个迁移子系统，写入"子系统清单"小节（含核心类路径、功能、Mantle 依赖、迁移难度、优先级）
- 全库 grep 统计 Mantle 依赖：约 60 个类、380 处引用，核心是 pulsar / RecipeMatch / network / inventory-gui / block / tileentity / multiblock / client book-model
- 关键结论：RecipeMatch 体系是全局地基（Material、Modifier 都继承它），优先重写；工具数据全存 NBT 需全改 DataComponent；1.12 无显式流体温度概念，只有配方 temp 熔点字段
- 下一步：创建 NeoForge 1.21.1 MDK 工程
