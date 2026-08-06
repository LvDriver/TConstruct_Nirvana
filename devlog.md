# devlog.md — 开发日志（跨会话外部记忆）

> 用法：**每次开工**先让 AI 读本文件；**每次收工**更新以下小节。
> 目的：把"需要记住的事"从对话上下文搬到文件里，AI 按需读取，省 token、防遗忘。

## 项目状态
- 当前阶段：会话4.5 完成（附属扩展 API：公开注册表 + 事件钩子）
- 最后更新：2026-08-06
- 旧源码路径：`./TinkersAntique-1.12/`（已解压，匠魂怀古 1.12.2 源码）

## 待办（按优先级）
- [x] 分析匠魂怀古源码模块结构，列出要移植的子系统清单
- [x] 创建 NeoForge 1.21.1 MDK 工程（版本对齐：NeoForge 21.1.248 / Java 21 / Gradle 8.14.2）
- [x] 会话2：材料与矿物系统（材料定义 + 钴/阿迪特矿 + 锭粒 + 世界生成 BiomeModifier + TCon 创造标签页）
- [x] 配置 DataGen 并跑通 `runData`（100 个数据文件已生成）
- [x] 会话3：工具部件系统（ToolPart + PartMaterialType + 部件数据 DataComponent + 模具 Pattern/Cast）
- [ ] 会话3.5：RecipeMatch→ItemTag 材料-物品关联体系（材料↔物品 addItem/addCommonItems 仍为遗留；部件已用 DataComponent 直存材料标识绕过）
- [x] 会话4.5：附属扩展 API（公开注册表 + 事件钩子）
- [x] 会话4：工具组装系统（21 工具注册 + ToolData 公式 + 组装配方）+ 修饰符系统（26 修饰符）+ Trait 系统（53 特质）
- [ ] 会话4.5b：ranged 完整化（自定义弹射物实体/拉弓 GUI/弩装填/箭袋）+ 完整修复机制（材料修复配方）+ 工具站/锻造厂 GUI（随工具组装）
- [ ] 流体系统（熔融钴/阿迪特等，冶炼炉前置）
- [ ] needs_cobalt_tool 工具侧接线完善（数据驱动 level 判定已可用）+ sharpening_kit 部件注册
- [ ] TConConfig 矿石生成开关接线（BiomeModifier 数据驱动限制）或移除
- [ ] 确定 Mod 核心玩法（1:1 还原匠魂怀古，后续再调整/新增）
- [ ] 定义注册清单：物品 / 方块 / 流体 / 实体 / 配方类型等
- [ ] 冶炼炉多方块（后期）

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
| 2026-08-05 | 按子系统拆分 Reasonix 会话，串行推进 | 避免上下文膨胀导致缓存失效和代码不一致 |
| 2026-08-05 | 脚手架版本：NeoForge 21.1.248 / moddev 2.0.91 / Gradle 8.14.2 / Parchment 2024.11.17 / JDK 21（Temurin） | 官方 MDK 21.1-mdg 分支模板；NeoForge 取 21.1.x 最新稳定版 |
| 2026-08-05 | 网络环境特殊：services.gradle.org、plugins.gradle.org、mavenCentral 均不可达；Gradle 发行版走腾讯镜像，maven 仓库走腾讯 nexus maven-public 兜底 | 国内网络；本地已有 JDK 21，故移除官方 MDK 的 foojay toolchain 插件 |
| 2026-08-05 | Parchment 官方仓库 maven.parchmentmc.org 已加入 build.gradle repositories（重定向到 ldtteam.jfrog.io/GCS，不稳定） | 兜底下载源 |
| 2026-08-06 | 材料系统落在新包 `material/`（Material + 8 类 stats record + ModMaterials 静态注册），不建 registry | 材料是纯数据定义，无需注册表；部件/物品才需要 DeferredRegister |
| 2026-08-06 | 材料属性数据 1:1 移植自 `tools/TinkerMaterials.java`（40 材料全量）；特质先以字符串标识占位（如 "momentum"/"magnetic1"），Trait 实现留待修饰符会话 | 旧版 addTrait 依赖 ITrait 类（60+ 类），本会话不阻塞，数据先落 |
| 2026-08-06 | 材料↔物品关联（addItem/addCommonItems/representativeItem，旧 RecipeMatch 体系）暂不移植，留待 RecipeMatch→ItemTag 会话 | 依赖 Mantle RecipeMatch，需按 1.21 Tag 体系重写 |
| 2026-08-06 | 钴/阿迪特矿 1:1 还原：硬度 10、采掘等级 4（COBALT）→ 自定义 tag `tconstruct_nirvana:needs_cobalt_tool`；金属块硬度 5、任意镐可采 | 1.21 采掘等级由 tag 体系表达；needs_cobalt_tool 的工具侧接线在工具会话 |
| 2026-08-06 | 矿物词典映射：oreCobalt→c:ores/cobalt、ingotCobalt→c:ingots/cobalt、blockCobalt→c:storage_blocks/cobalt 等（c: 前缀 common tag） | 500mod 整合包兼容（JEI/其他 mod 矿物词典互通） |
| 2026-08-06 | 信标基座用 `minecraft:beacon_base_blocks` tag（1.21.1 已移除 Block#isBeaconBase） | 1.21 信标基座判定改为 tag（BeaconBlockEntity 硬编码检查） |
| 2026-08-06 | 世界生成 1:1：每区块 20 矿脉（旧 rate=20）、size 5、替换 netherrack、Y 0~128（旧两段随机合并近似均匀）、BiomeModifier `add_features` + `#minecraft:is_nether` + underground_ores | BiomeModifier JSON 手写 provider（1.21.1 无 BiomeModifiersBuilder） |
| 2026-08-06 | TConConfig 的 generateCobaltOre/generateArditeOre 开关暂不接线（BiomeModifier 是数据驱动，无法读运行时 config） | 待后续会话用数据条件/删除处理 |
| 2026-08-06 | 配方输入一律用 Tag，禁止用具体物品 ID | 匠魂核心价值是"万物皆可熔"，用 Tag 才能熔其他 mod 的矿物 |
| 2026-08-06 | 新增会话4.5：附属扩展 API | 公开材料/修饰符/部件/模具注册表 + 事件钩子，让附属 mod 能扩展 |
| 2026-08-06 | AGENTS.md 新增"兼容性与附属生态"整节 | 每个会话都必须考虑 Tag 兼容、附属 API、软依赖，不单独提醒 |
| 2026-08-06 | 部件↔属性类型映射直接挂在 ToolPart 上（statTypes 列表），不学旧版遍历工具查询 hasUseForStat | 工具尚未实现；映射来源 1:1 旧版工具定义（pick_head→HEAD、tool_rod→HANDLE、bow_limb→BOW+HEAD 等），工具会话可直接复用 |
| 2026-08-06 | 部件材料用 PART_MATERIAL DataComponent 存材料 identifier 字符串（非 ResourceLocation） | 材料系统无注册表（ModMaterials 静态注册），identifier 即唯一键；模具形状才用 ResourceLocation（部件注册名） |
| 2026-08-06 | 模具形状 ID = 部件物品注册名（如 tconstruct_nirvana:pick_head），Pattern/Cast 共用一套形状；关联走 ModToolParts.PARTS 静态注册表 + `tconstruct_nirvana:tool_parts` ItemTag | 1:1 旧版 Pattern TAG_PARTTYPE 存部件注册名；注册表查询 + Tag 供配方/附属使用 |
| 2026-08-06 | bolt_core / sharpening_kit 部件本会话不注册 | BoltCore 双材料逻辑、SharpeningKit 属工具修饰子系统，留待对应会话，devlog 已记录 |
| 2026-08-06 | 部件创造标签页只显示第一个可用材料变体（1:1 旧版 listAllPartMaterials=false 行为） | 40 材料 × 27 部件全列出会淹没标签页；完整材料变体后续加开关 |
| 2026-08-06 | 工具属性合成 1:1 落 ToolData record（head→extra→handle 顺序，公式见用户确认）；MAX_DAMAGE 组件同步耐久、ATTRIBUTE_MODIFIERS 组件承载攻击/攻速（1.21.1 Item.getDefaultAttributeModifiers() 无参，无法按栈动态，改组件方案） | 用户确认的公式检查点；1.21.1 动态属性标准做法 |
| 2026-08-06 | 攻击链 1.21.1 适配：不拦截原版攻击（无 PlayerAttackEvent），改 LivingIncomingDamageEvent 做 trait.damage 链 + cuttoff + modifyDamage 钩子；afterHit/耐久损耗在 hurtEnemy 覆写 | NeoForge 21.1 移除 PlayerAttackEvent/LivingHurtEvent/HarvestDropsEvent，对应替换为 LivingIncomingDamageEvent/BlockDropsEvent |
| 2026-08-06 | 挖掘速度走 PlayerEvent.BreakSpeed、采掘判定走 PlayerEvent.HarvestCheck（needs_* tag → HarvestLevels 映射，Mattock 按斧/铲分等级）；AOE 扩展挂 BreakEvent；物品 tick 用 Item.inventoryTick（1.21.1 恢复） | 1.21.1 移除 getStrVsBlock/tool class 体系，mineable+needs tag 是唯一判定路径 |
| 2026-08-06 | 修饰符/Trait 用静态注册表（Modifiers/Traits），不建 Registry（同材料先例）；identifier 即唯一键，材料挂载字符串直接匹配 | 纯数据/逻辑定义无需注册表，附属 API 会话再公开 accessor |
| 2026-08-06 | 工具行为钩子（onToolDamage/afterHit/miningSpeed 等 16 个）提升到 Modifier 基类，Trait 继承；事件分发统一遍历 getActiveModifiers（trait + 修饰符实例去重） | necrotic/fiery 等是 Modifier 非 Trait，必须同一触发链 |
| 2026-08-06 | 工具组装用特殊 CraftingRecipe（crafting_special，工作台按序摆部件→出工具，DataGen 生成），不做 GUI | 用户确认方案；工具站 GUI 后续会话 |
| 2026-08-06 | 弓/箭等 ranged：注册 + 属性计算 1:1（LauncherData/ACCURACY 组件），发射为简化版（原版箭实体）；自定义弹射物实体/弩装填/箭袋留待会话4.5b | ranged 完整系统依赖实体系统，量级大 |
| 2026-08-06 | BoltCore 简化为单材料部件（旧版双材料：核心+头），Bolt 组装时头部复用核心材料 | 双材料部件逻辑后续补 |
| 2026-08-06 | 工具模型暂用代表部件贴图占位（生成器 singleTexture），完整部件组合渲染模型后续会话 | 旧版无整工具贴图（tmat 部件组合模型） |
| 2026-08-06 | AOE 额外方块破坏前：采掘等级校验（requiredHarvestLevel 提取到 ToolHelper 复用）+ 手动派发 BreakEvent 尊重领地保护；`aoeInProgress` 实例标志防 BreakEvent 重入递归（security_review 发现并修复） | 低级锤不能 AOE 挖高级矿；额外方块须让领地 mod 可拦截；重入由 try/finally 复位 |
| 2026-08-06 | security_review 遗留（后续会话）：ModCreative 无限耐久/无限槽须在应用入口接入时加创造/命令限定；silktouch 与 autosmelt/blasting 互斥 aspect；战牌格挡任意伤害源减半无冷却 | 现均不可达/低危，记录待办防回归 |
| 2026-08-06 | AOE 1:1 细化（review 三轮闭环）：强度比过滤 `额外硬度/主硬度>10` 拒绝（防钴锤瞬破黑曜石）；俯视分轴（俯角>45° → width×height 水平面 + Y 向 depth 层，含 origin 层）；`damageTool` 早退（amount<=0||broken）+ maxDamage clamp | 旧版 canBreakExtraBlock/calcAOEBlocks 语义逐行对齐 |
| 2026-08-06 | afterHit 钩子改由 `LivingDamageEvent.Post` 驱动（damageDealt = newDamage - blockedDamage），hurtEnemy 只留耐久/饥饿 | 1.21.1 无攻击结算后回调可拿实际伤害；necrotic 吸血因此拿到真实值；横扫副目标也触发 afterHit（与旧版差异已记录） |
| 2026-08-06 | 附属 API 分层：`api` 包只放接口/事件类/门面入口（TConstructNirvanaAPI 返回 5 个 Registry 接口），实现类放 `impl` 包，附属只依赖 api 类型；Modifiers.register 改 public 开放 | 稳定 API：接口发布后不改签名，实现可自由重构；附属 compileOnly 引用完整 jar（impl 类同 jar 内） |
| 2026-08-06 | 附属部件注册走 DeferredItem：`registerPart(ResourceLocation, DeferredItem<? extends ToolPart>)`，PARTS 结构不动；DeferredItem.createItem 在 21.1 无 supplier 版（仅按 key 懒查找）故不适用 | 附属用自己 DeferredRegister 注册物品天然返回 DeferredItem；getAllParts 的 List<DeferredItem> 类型被 DataGen/创造标签页依赖，不能改 |
| 2026-08-06 | 事件钩子：ToolBuildEvent（buildItem 组装时触发，可改 ToolData/取消）+ ModifierTriggerEvent（ATTACK/BLOCK_BREAK，只读监听）+ SmelteryEvent/MeltingEvent/CastingEvent（冶炼炉会话接入触发点，API 先行锁定形状） | 全部挂 NeoForge.EVENT_BUS，附属可用 EventPriority；冶炼炉未实现故事件暂无触发点，javadoc 已注明 |
| 2026-08-06 | 模具形状扩展 = 注册部件即自动可用（形状即部件注册名），PatternRegistry 只提供物品 accessor + isKnownShape 查询 | 1:1 旧版 Pattern 形状来自 part 注册表，无需独立模具注册表 |
| 2026-08-06 | 附属 API 验证：模拟附属源码（仅引用 api 公共类型）编译通过后删除；src/test 无 Minecraft classpath（moddev 不给 test 源集挂 mc 依赖） | 验收"API 可被外部引用"；冒烟验证不留测试类，保持最小 diff |

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
- （无功能性 Bug；以下为已知限制/遗留，详见会话记录与关键决策）
- 冶炼事件类（SmelteryEvent/MeltingEvent/CastingEvent）已发布但无触发点，待冶炼炉会话接入
- `needs_cobalt_tool`：钴/阿迪特矿需钴级工具采掘（1:1 还原旧版采掘等级 4），当前尚无钴工具 → 矿石暂无法正常采掘掉落，待工具会话接线
- TConConfig 的 generateCobaltOre / generateArditeOre 开关未接线（BiomeModifier 为数据驱动，无法读运行时 config）
- 材料特质为字符串占位（如 "momentum"），无实际游戏效果，待修饰符会话实现 Trait 类
- bolt_core / sharpening_kit 部件未注册（BoltCore 双材料、SharpeningKit 属工具修饰子系统），待对应会话
- 材料↔物品关联（旧 RecipeMatch addItem/addCommonItems）未实现：部件经 DataComponent 直存材料标识可用，但"钴锭→钴材料"等自动关联待 RecipeMatch→ItemTag 会话

## 已踩过的坑（随开发补充）
- `.m2` 本地 Maven 仓库（`C:\Users\<user>\.m2\repository`）中曾有损坏的 parchment zip（25KB，正常 889KB），因 `mavenLocal()` 排仓库首位被优先选中，导致 jst 报 `ZipException: zip END header not found`、`createMinecraftArtifacts` 失败 → 删掉 `.m2/repository/org/parchmentmc` 后恢复（gradle modules-2 缓存有完好副本）
- services.gradle.org 不可达时，`gradle/wrapper/gradle-wrapper.properties` 的 `distributionUrl` 已指向腾讯镜像（`mirrors.cloud.tencent.com/gradle/`），`networkTimeout` 已调至 60000
- 1.21.1 类名/包与 1.20 不同：`BootstrapContext`（1.20 误拼 BootstapContext）、`RegistrySetBuilder` 在 `net.minecraft.core`、`BlockLootSubProvider` 在 `net.minecraft.data.loot`（不在 packs）、无 `BiomeModifiersBuilder`、`BlockTags` 无 NETHERRACK 常量（用 `TagKey.create(Registries.BLOCK, mc:netherrack)`）
- `BlockLootSubProvider` 的 `getKnownBlocks()` 默认遍历全注册表（含 vanilla 方块）会报 `Missing loottable 'minecraft:blocks/stone'` → 必须 override 只返回本 mod 方块
- 物品模型贴图按注册名查找（`item/cobalt_ingot` → `textures/item/cobalt_ingot.png`），旧版 `ingot_cobalt.png` 命名不匹配会报 `Texture ... does not exist` → 贴图按注册名重命名
- 1.21.1 移除 `Block#isBeaconBase`，信标基座改 `minecraft:beacon_base_blocks` tag
- 旧版下界矿石贴图是 `nether_ore_cobalt/ardite`（非 ore_cobalt），1:1 沿用
- 1.21.1 Item API 大改：`use` 返回 `InteractionResultHolder<ItemStack>`；`getDefaultAttributeModifiers()` 无参（动态属性写 `ATTRIBUTE_MODIFIERS` 组件）；无 `onLeftClickEntity/onBlockDestroyed/onBlockStartBreak/setDamage/getMaxDamage` 覆写点（改 `hurtEnemy/mineBlock/inventoryTick` + MAX_DAMAGE 组件）；`AttributeModifier` 构造为 `(ResourceLocation, double, Operation)`
- NeoForge 21.1 移除 `PlayerAttackEvent/LivingHurtEvent/BlockEvent.HarvestDropsEvent` → 用 `LivingIncomingDamageEvent`（含 getOriginalAmount）/ `BlockDropsEvent`；`PlayerTickEvent` 在 `net.neoforged.neoforge.event.tick` 包（有 Pre/Post）；配方 `RecipeSerializer` 的 `codec()` 返回 `MapCodec` 且须实现 `streamCodec()`
- 1.21.1 `RecipeManager.byType` 是 private → 用 `getAllRecipesFor`；`SpecialRecipeBuilder` 在 `net.minecraft.data.recipes` 且 `special()` 接收 `Function<CraftingBookCategory, Recipe>`；`save()` 不带命名空间时默认 `minecraft:`（须显式 `modid:name`）
- 1.21.1 `Biome` 无公开温度/降雨 getter（TraitAridiculous 改 `#minecraft:is_hot` tag 判定）；`DamageTypeTags` 在 `net.minecraft.tags` 包；`Entity.getRandom()` 不存在（用 `level().getRandom()`）；`getMobType()` 移除（改 `EntityTypeTags.UNDEAD`）；`spawnSweepParticles` 移除（改 `broadcastEntityEvent(31)`）
- DeferredHolder 直接传 `ItemStack.getOrDefault` 有泛型推断坑（CAP#1）→ fallback 显式泛型（`Map.<String,Integer>of()`）或组件加强类型 accessor（`ModDataComponents.traitLevelsType()`）
- 静态初始化顺序坑：`ModToolParts.PARTS` 声明在 `part()` 调用之后时，`PARTS.put` 报 NPE（PARTS 尚未初始化）→ 被方法间接引用的静态字段必须声明在调用之前
- `DeferredItem.get()` 在注册事件触发前调用会抛异常 → 构造器中"确保类加载"只能访问字段/调用静态方法，不能 `get()`
- 静态块中前向引用后声明的静态字段是编译错误（非法前向引用）→ 把被引用的 Map 声明移到类顶部

## 会话记录
### 2026-08-06 会话4.5：附属扩展 API（完成）
- api 包（只放接口/事件/门面）：`TConstructNirvanaAPI` 门面 + 5 个 Registry 接口（MaterialRegistry / ModifierRegistry / ToolPartRegistry / PatternRegistry / FluidRegistry）+ 5 个事件类（ToolBuildEvent / ModifierTriggerEvent / SmelteryEvent 基类 + MeltingEvent / CastingEvent）
- impl 包：5 个 RegistryImpl 实现类（委托 ModMaterials / Modifiers / ModToolParts / ModPatterns / ModFluids），附属不直接引用
- 开放入口：ModMaterials.registerMaterial（去重替换）、Modifiers.register（改 public）、ModToolParts.registerPart ×2（本 mod 注册表注册 / 登记附属 DeferredItem）
- 事件触发点接入：TinkerToolItem.buildItem → ToolBuildEvent（可改 ToolData/取消）；hurtEnemy → ModifierTriggerEvent(ATTACK)；afterBlockBreak → ModifierTriggerEvent(BLOCK_BREAK)；冶炼事件 API 先行无触发点（冶炼炉会话接入）
- build.gradle：java-library 已启用（api configuration 可用），publishing 加 artifactId=mod_id；附属 compileOnly 引用 maven-publish 产物
- **验证全通过**：`./gradlew check`（BUILD SUCCESSFUL）、`./gradlew build`（BUILD SUCCESSFUL，jar 含 api/* 11 类 + impl/* 5 类）、模拟附属源码编译通过（临时类已删）
- 踩坑：DeferredItem.createItem 在 21.1 无 supplier 版（仅按 key 懒查找）；src/test 无 Minecraft classpath（冒烟验证改放 main 临时类）
- 遗留：冶炼事件触发点待冶炼炉会话；附属扩展的 DataGen（lang/tag）附属自理
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

### 2026-08-06 会话2：材料定义与矿物/金属物品（完成）
- 材料系统：新包 `material/`，Material 类（VALUE 常量/颜色/craftable+castable/属性 Map/特质字符串占位）+ 8 类 MaterialStats record（Head/Handle/Extra/Bow/BowString/ArrowShaft/Fletching/Projectile）+ MaterialTypes + ModMaterials 静态注册 40 材料（属性数值 1:1 自 tools/TinkerMaterials.java，含弓/箭杆/箭羽数据）；TConStats 便捷方法 + util/HarvestLevels 常量
- 矿物与金属：cobalt_ore/ardite_ore（硬度 10、采掘等级 4→needs_cobalt_tool tag）+ cobalt_block/ardite_block（硬度 5、信标基座 tag）+ 钴/阿迪特锭粒 4 件，全部 DeferredRegister；矿物词典等价 tag（c:ores/*、c:ingots/*、c:nuggets/*、c:storage_blocks/*）DataGen 生成
- 世界生成：configured feature（size 5、替换 #minecraft:netherrack）+ placed feature（count 20、in_square、uniform Y0-128、biome）+ BiomeModifier（add_features、#minecraft:is_nether、underground_ores）全部 DataGen 输出 JSON，参数 1:1 自 NetherOreGenerator/Config
- 创造标签页：TCon 主标签页（icon=钴锭）放入 8 个物品
- 资源：旧版贴图 9 张复制（nether_ore_cobalt/ardite、block_cobalt/ardite(+top)、锭粒贴图重命名为注册名）；模型/blockstate/loot/lang（en_us+zh_cn）全部 DataGen
- **验证全通过**：`./gradlew build`（BUILD SUCCESSFUL）、`./gradlew runData`（生成 46 个数据文件，含 worldgen/loot/tags/lang）、`./gradlew runClient`（tconstruct_nirvana initialized → 资源加载无错误 → 主菜单）
- 遗留：材料↔物品关联（RecipeMatch→ItemTag）与 Trait 类留待后续会话；TConConfig 矿石开关未接线（BiomeModifier 数据驱动）；needs_cobalt_tool 工具侧接线待工具会话
- 下一步：会话3（待定：RecipeMatch 体系 / 工具部件 / 流体）

### 2026-08-06 会话2 收工
- `./gradlew build` 确认通过（BUILD SUCCESSFUL）
- devlog 收工更新：待办补充会话3 及后续待办；已知 Bug 记录 3 条遗留限制
- Git 存档并 push 至 GitHub（origin: https://github.com/LvDriver/TConstruct_Nirvana.git）
- 下一会话建议：会话3 = RecipeMatch→ItemTag 材料-物品关联体系 + 工具部件系统（材料/修饰符全局地基，先铺）

### 2026-08-06 会话3：工具部件物品 + 模具系统（完成）
- DataComponent：ModDataComponents 填充 PART_MATERIAL（Codec.STRING，材料 identifier）+ PATTERN_SHAPE（ResourceLocation.CODEC，模具形状 = 部件注册名，null=空白）
- 部件系统（item/part/）：ToolPart（cost + statTypes 属性类型 + 材料 DataComponent 读写 + 1:1 属性 tooltip：head 耐久/采掘等级/采掘速度/攻击、handle 系数/耐久、extra 耐久、bow 拉弓速度/射程/伤害、bowstring 系数、shaft 系数/弹药、fletching 精准度/系数）+ Shard（cost=72，canUseMaterial 特殊）+ PartMaterialType（head/handle/extra/bow/bowstring/arrowHead/arrowShaft/fletching/crossbow 工厂）+ ModToolParts 注册 27 部件（26 ToolPart + shard，cost/属性类型 1:1 自 TinkerTools.registerToolParts；boltCore 未注册）
- 模具系统（item/pattern/）：PatternItem + CastItem（形状 DataComponent，getName 空白/带形状，cost tooltip）+ ModPatterns 注册 pattern/cast
- 部件↔模具关联：形状 ID = 部件注册名 → ModToolParts.PARTS 静态注册表查询；`tconstruct_nirvana:tool_parts` ItemTag 生成（27 条目）
- 创造标签页：模具（空白 + 27 形状 × pattern/cast）+ 部件（每部件第一个可用材料变体）
- 资源：29 张贴图从旧版复制并按注册名重命名（parts/*.png 15 张 + 工具目录 head/guard/limb 等 14 张，映射自旧版 tmat 模型 layer0）；模型/lang（en_us+zh_cn：27 部件名 + pattern/cast + stat.* + ui.mininglevel.*）/tag 全部 DataGen
- **验证全通过**：`./gradlew check`（BUILD SUCCESSFUL）、`./gradlew build`（BUILD SUCCESSFUL）、`./gradlew runData`（73 个数据文件，32 新写）、`./gradlew runClient`（tconstruct_nirvana initialized → ResourceManager 加载完成 → 0 ERROR/Exception → 主菜单）
- 踩坑修复：ModToolParts 静态初始化顺序（PARTS 声明在 part() 调用后 → NPE；移到类顶部）；DeferredItem.get() 注册前抛异常（主类只触发类加载不取值）
- 遗留：bolt_core/sharpening_kit 未注册（ranged/工具修饰会话）；材料↔物品关联 RecipeMatch→ItemTag 未做（部件已用 DataComponent 绕过）
- 下一步：RecipeMatch→ItemTag 材料-物品关联体系（或直接进工具组装/模具 GUI 会话）
