# devlog.md — 开发日志（跨会话外部记忆）

> 用法：**每次开工**先让 AI 读本文件；**每次收工**更新以下小节。
> 目的：把"需要记住的事"从对话上下文搬到文件里，AI 按需读取，省 token、防遗忘。

## 项目状态
- 当前阶段：会话7 完成（冶炼炉多方块结构系统：seared 方块 + 多块检测 + 炉体逻辑 + GUI）
- 最后更新：2026-08-06
- 旧源码路径：`./TinkersAntique-1.12/`（已解压，匠魂怀古 1.12.2 源码）

## 待办（按优先级）
- [x] 分析匠魂怀古源码模块结构，列出要移植的子系统清单
- [x] 创建 NeoForge 1.21.1 MDK 工程（版本对齐：NeoForge 21.1.248 / Java 21 / Gradle 8.14.2）
- [x] 会话2：材料与矿物系统（材料定义 + 钴/阿迪特矿 + 锭粒 + 世界生成 BiomeModifier + TCon 创造标签页）
- [x] 配置 DataGen 并跑通 `runData`（100 个数据文件已生成）
- [x] 会话3：工具部件系统（ToolPart + PartMaterialType + 部件数据 DataComponent + 模具 Pattern/Cast）
- [x] 会话3.5：RecipeMatch→ItemTag 材料-物品关联体系（ItemTagMatch TagKey 匹配 + 40 材料绑定 + 代表物品）
- [x] 会话4.5：附属扩展 API（公开注册表 + 事件钩子）
- [x] 会话4：工具组装系统（21 工具注册 + ToolData 公式 + 组装配方）+ 修饰符系统（26 修饰符）+ Trait 系统（53 特质）
- [x] 会话4.5b：ranged 完整化（自定义弹射物实体/弩装填/拉弓动画）+ 完整修复机制（材料修复配方 + 磨刀石）+ 工具站/锻造厂 GUI（简化版，随工具组装）
- [ ] 箭袋：旧版匠魂怀古无此物（全库 grep 确认），1:1 原则跳过；如后续用户要求可自创
- [ ] 工具站 GUI 增强：Shift 快速移动/拆解/修饰符按钮/修复按钮（当前最小版：5 部件槽+结果槽）
- [x] 会话5：金属流体系统（26 流体注册 + 温度系统 + 合金配方 + c: tag）——熔炼/浇铸配方类型与冶炼炉事件触发点待冶炼炉会话
- [x] 会话6：全部自定义配方类型——熔炼/浇铸/桶浇铸/部件制作（Recipe+Serializer+注册+DataGen 561 条）+ 8 流体补注册 + 铸造形状 5 个 + JEI 4 分类（compileOnly 软依赖）
- [x] 熔炼/浇铸配方类型（MeltingRecipe/CastingRecipe 数据驱动版，冶炼炉会话接入触发点）✓ 类型已完成；MeltingEvent 触发点已接入（会话7），CastingEvent 待浇铸台会话
- [x] 冶炼炉多方块（会话7）：seared 方块 12 变体 + 玻璃/储罐/控制器 + 多块检测 + 炉体逻辑（熔炼/合金/部件熔炼/实体熔炼/燃料）+ MeltingEvent 触发点 + GUI 最小版
- [ ] 浇铸系统（后续会话）：浇铸台/盆（BlockCasting + TileCasting + CastingEvent 触发点）、龙头/沟槽/排液口（faucet/channel/drain）、seared 楼梯/台阶
- [ ] 冶炼炉 GUI 增强：温度/进度条显示、燃料液体贴图渲染（当前 tint 色块占位）、Shift 快速移动
- [ ] needs_cobalt_tool 工具侧接线完善（数据驱动 level 判定已可用）+ sharpening_kit 部件注册
- [ ] TConConfig 矿石生成开关接线（BiomeModifier 数据驱动限制）或移除
- [ ] 确定 Mod 核心玩法（1:1 还原匠魂怀古，后续再调整/新增）
- [ ] 定义注册清单：物品 / 方块 / 流体 / 实体 / 配方类型等

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
| 2026-08-06 | RecipeMatch→ItemTag：util 包 `ItemTagMatch`（TagKey<Item> + 价值 amount）替代旧版 Mantle RecipeMatch；`Material.addItem(TagKey,value)/addItemIngot/addCommonItems(金属路径)/matches/getMatchValue/hasItems` 落位；代表物品支持 tag 式（运行时从 tag 内容取首个，旧版 representativeOre）与 Item/ItemStack 式 | 1.21.1 无 OreDictionary，TagKey 是唯一匹配路径；`setRepresentativeItem` 取消旧版"必须已关联"校验（材料绑定在 Mod 构造器，早于数据包 tag 加载，无法校验） |
| 2026-08-06 | 旧版 oredict 名映射规则：有 c: 约定直连（ingotCobalt→c:ingots/cobalt、stickWood→c:rods/wooden、gemPrismarine→c:gems/prismarine、bone→c:bones、string→c:strings、feather→c:feathers、blaze_rod→c:rods/blaze、sugar_cane→c:crops/sugar_cane、cobblestone→c:cobblestones、stone→c:stones、obsidian→c:obsidians、netherrack→c:netherracks、endstone→c:end_stones）；无 c: 对应项 → mod 命名空间 tag（flint/cactus/bonemeal/paper/sponges/vines/packed_ice/end_rods/storage_blocks/prismarine(+bricks/dark)），DataGen 加入原版物品；plankWood/logWood/treeLeaves → minecraft:planks/logs/leaves；金属合金（pigiron 等 9 种）→ c:ingots|nuggets|storage_blocks/<path> 约定 tag（现为空，附属/物品注册后自动生效） | 500mod 兼容：其他 mod 物品带同 tag 即自动关联材料；仅关联本 mod 未注册物品的旧条目（firewood/slimecrystal*/boneBloodied/slimevine*/slimeleaf*）留待物品注册会话 |
| 2026-08-06 | 弹射物实体移植：继承 AbstractArrow 复用物理/碰撞/拾取协议，1:1 覆盖伤害公式（(弹射物攻击+弓基础×power+bonusDamage)×damageModifier×power）、反弹/消失、重力（Bolt 0.065/Shuriken 动态）、阻力（tick 后按 getSlowdown 差值缩放，0.99 硬编码无覆写点）；命中用背包实际弹药栈结算（AmmoHelper 语义）+ 损坏弹药伤害 1 | 1.21.1 无 IEntityAdditionalSpawnData，pickupItemStack 自动同步；旧版 EntityProjectileBase 630 行核心逻辑逐段对齐 |
| 2026-08-06 | 弹射物弹药模型 1:1：durabilityPerAmmo=10（耐久/10=弹药），useAmmo→damageTool(10)，getProjectileStack（copy+setAmmo(1)+非消耗满耐久+unbreak），耐久条按弹药显示，tooltip 弹药/精准度；弓每发扣 1 耐久 | 与"万物皆可熔"同源：箭消耗自身耐久而非数量，拾取一单位弹药防正反馈（reinforced 等防损特质） |
| 2026-08-06 | 弓发射 1:1：power=(progress²+2progress)/3×progress×baseSpeed×range；findAmmo 主副手→快捷栏→背包且弹药>0；创造无弹药兜底原版箭；弩 loaded 装填（拉满→loaded→右键发射） | 旧版 BowCore 默认 baseInaccuracy=0/baseProjectileSpeed=3（本项目曾误 1/3.5 已修，影响弩不准度 0） |
| 2026-08-06 | 修复机制 1:1：磨刀石（cost=288，HEAD 材料）+ 工作台 RepairRecipe（工具+磨刀石，全消耗校验）+ TinkersItem.repair（144 换算、多材料×(1+(n-1)/9)、修饰符惩罚 0.95/0.9/0.85、修复递减下限 0.5） | 旧版工作台修复输入仅磨刀石（锭类材料走工具站修复按钮，后续会话） |
| 2026-08-06 | 工具站/锻造厂 GUI 最小可用版：BlockToolTable + ToolTableBlockEntity（SimpleContainer 5 槽）+ TinkerStationMenu（5 部件槽+结果槽实时预览、取走即消耗）+ Screen（旧版 generic.png 背景）；锻造厂同逻辑 | ContainerToolStation 510 行完整版（Shift/拆解/修饰符/修复按钮）留待增强会话；客户端 Menu 空容器重建，槽内容服务端广播同步 |
| 2026-08-06 | 箭袋不移植（旧版无此物，全库 grep 确认），devlog 待办标注；Bolt 伤害简化（旧版 Rapier.dealHybridDamage 混合伤害，本项目统一基类伤害） | 1:1 原则；差异记录防回归 |
| 2026-08-06 | 流体注册：NeoForge 21.1.248 无 ForgeFlowingFluid/FluidBlock（1.20 类），改用 `BaseFlowingFluid`（Source/Flowing 嵌套类）+ 原版 `LiquidBlock`；注册顺序 FLUID_TYPES→FLUIDS→ITEMS(桶)→BLOCKS(方块)（BucketItem/LiquidBlock 构造需 Fluid 实例）；条目互相引用经 DeferredHolder 延迟解析，静态初始化顺序无关 | 1.21.1 Fluid API 全变；javap 反编译 neoforge-21.1.248-universal.jar 实证 |
| 2026-08-06 | 流体 tag 命名 **c:<name>**（c:molten_iron 等，用户确认），非任务字面的 c:fluid/ 前缀：NeoForge 1.21.1 Tags.Fluids 全家族即 c:water/c:lava 风格，文件落 data/c/tags/fluid/*.json，500mod 互认最佳 | 实证：neoforge jar 内 data/c/tags/fluid/beetroot_soup.json ↔ Tags.Fluids.BEETROOT_SOUP="c:beetroot_soup"（tag path 不含 fluid/ 前缀，registry 目录自动加） |
| 2026-08-06 | 合金配方 = 自定义 RecipeType（`tconstruct_nirvana:alloy`）+ RecipeSerializer（codec+streamCodec），输入用 `SizedFluidIngredient`（FLAT_CODEC，JSON `{"fluid":..,"amount":..}`），输出 `FluidStack`；匹配逻辑 1:1 旧版 AlloyRecipe.matches（ratio 次数）；isSpecial 防配方书渲染；10 条 1:1 自 registerAlloys（旧版集成条件默认满足全注册；obsidianAlloy 配置开关旧版默认 true，本版无条件注册） | 1.21.1 无流体 Ingredient（1.20），NeoForge 21.1 新增 FluidIngredient 体系 |
| 2026-08-06 | 温度系统：熔点 1:1 自旧版 `Fluid.setTemperature` 写入 `FluidType.Properties.temperature`（iron 769/gold 532/cobalt 950/manyullyn 1000 等）；附属经 `FluidRegistry.getTemperature(fluidId)` 查询（default 方法，遵守稳定 API 承诺） | 匠魂 1.12 无显式流体温度概念，只有配方 temp 字段 + 加热结构；1.21.1 FluidType 自带 temperature 属性，正合熔点 |
| 2026-08-06 | 材料↔流体关联 1:1 自旧版 MaterialIntegration.integrateFluid：14 金属材料（iron/pigiron/cobalt/ardite/manyullyn/knightslime/alubrass/alumite/copper/bronze/lead/silver/electrum/steel）经 Material.setFluid 关联；gold/tin/zinc/nickel/brass/aluminum 无本 mod 材料（旧版同） | 关联后材料可浇铸、流体可熔炼对应物品（冶炼炉会话接入） |
| 2026-08-06 | 流体渲染：IClientFluidTypeExtensions（RegisterClientExtensionsEvent.registerFluidType）+ 旧版贴图 1:1（molten_metal/liquid_stone/liquid/liquid_slime 各 still+flow）+ FluidColored 染色（alpha 缺失补 0xFF）；流体方块模型 = 无 elements 占位（particle=类别贴图），表面由 LiquidBlockRenderer 按渲染属性绘制；桶模型用 `neoforge:fluid_container` loader（DynamicFluidContainerModelBuilder） | 21.1 无 "neoforge:fluid" 方块模型 loader（1.20 有）；桶 1:1 旧版 addBucketForFluid：1 堆叠 + craftRemainder 空桶 |

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
- bolt_core 为简化单材料版（旧版双材料：核心+头，Bolt 组装时头部复用核心材料），完整双材料逻辑后续补
- 弹射物 trait 钩子（旧版 IProjectileTrait.onLaunch/onProjectileUpdate/onMovement/afterHit）未接入，弹射物修饰符交互留待后续会话
- 弩 loaded 状态经物品 DataComponent 同步（同旧版 NBT 同步方式）：装填完成瞬间 1 tick 内立即右键可能触发客户端蓄力分支动画（服务端正常发射），功能不受影响
- Bolt 伤害为基类统一公式（旧版 Rapier.dealHybridDamage 混合伤害），差异已记录
- 工具站 GUI 为最小可用版：无 Shift 快速移动/拆解/修饰符/修复按钮；破坏方块不掉落容器内容
- 弹射物渲染为物品模型占位（2D 纸片），完整 3D 弹射物模型后续会话
- 材料↔物品关联已实现（ItemTagMatch）；遗留：仅关联本 mod 未注册物品的旧条目（firewood、slimecrystal*/slimeleaf*/slimevine*、boneBloodied）未绑定，待物品注册会话；合金金属（pigiron/bronze/lead/silver/electrum/steel/alubrass/alumite/manyullyn/knightslime）的 c: tag 现为空（无 mod 提供物品），附属注册后自动生效

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
- NeoForge 1.21.1 `DeferredRegister.Blocks.registerBlock` **不自动注册 BlockItem** → 须在 ModItems `registerSimpleBlockItem` 手动注册；漏注册时 `Block.asItem()` 返回 `minecraft:air`，创造标签页 `accept(Block)` 直接崩 `IllegalArgumentException: The stack count must be 1`（NeoForge 对 count≠1 校验）
- 1.21.1 `MenuScreens.register` 已 private + @Deprecated → 改 `RegisterMenuScreensEvent`（MOD bus，`event.register(MenuType, ScreenConstructor)`）
- 1.21.1 `BaseEntityBlock` 无泛型参数（1.20 是 `BaseEntityBlock<T extends BlockEntity>`），且须实现抽象 `codec()`（返回 `MapCodec<? extends BaseEntityBlock>`，用 `simpleCodec(MyBlock::new)`）；`Block.use` 交互方法已下沉到 `BlockBehaviour`（`useWithoutItem` 签名无 InteractionHand）
- 1.21.1 `MenuType` 构造是 `(MenuSupplier, FeatureFlagSet)` 双参（`FeatureFlags.REGISTRY.allFlags()`）；方法引用有多个构造时无法推断（TinkerStationMenu 有 5 参/2 参构造 → 用 2 参构造方法引用 `TinkerStationMenu::new` 仍可解析，但报"无法推断类型参数"时检查是否漏 FeatureFlagSet 参数）
- `ContainerHelper.saveAllItems/loadAllItems` 1.21.1 参数顺序是 `(CompoundTag, NonNullList, HolderLookup.Provider)`（provider 在最后，1.20 习惯在前会编译错）
- 创造标签页 `output.accept(Block)` 在游戏内运行时才构建（runClient 才暴露），编译/DataGen 无法发现漏 BlockItem → GUI 会话后必须跑 runClient 冒烟
- 1.21.1 `EntityType.Builder.build(String)` 需显式 id；`AbstractArrow.addAdditionalSaveData/readAdditionalSaveData` 是 public 非 protected；实体渲染器注册无 `RegisterRenderersEvent`（那是 1.20 的），用原版静态 `EntityRenderers.register` + FMLClientSetupEvent
- 1.21.1 `TooltipContext` 是 `Item` 的内部类（子类可裸引用，不能写 `net.minecraft.world.item.TooltipContext`）；`com.mojang.math.Axis`（非 RotationAxis，那是 1.21.3+）
- NeoForge 21.1.248 无 `ForgeFlowingFluid`/`FluidBlock`（1.20 类）→ 用 `net.neoforged.neoforge.fluids.BaseFlowingFluid`（Source/Flowing 嵌套类，Properties 构造 `(Supplier<FluidType>, Supplier<Fluid> still, Supplier<Fluid> flowing)` + `.bucket/.block/.explosionResistance`）+ 原版 `LiquidBlock`；`LiquidBlock` 构造签名是 `(FlowingFluid, Properties)` 非 `(Fluid, Properties)`，须强转
- `FluidTagsProvider` 在 **`net.minecraft.data.tags`**（vanilla 包，NeoForge 只 patch 了 4 参构造），不在 `net.neoforged.neoforge.common.data`（那是 BlockTagsProvider 等）
- 流体 common tag 的 TagKey **path 不带 "fluid/" 前缀**（如 `c:molten_iron`）：TagsProvider 自动按注册表目录（tags/fluid/）落文件；若写 `c:fluid/molten_iron` 会生成 `data/c/tags/fluid/fluid/molten_iron.json`（路径多一层）
- `BucketItem` 1.21.1 只有 `(Fluid, Item.Properties)` 构造（无 Supplier 版）→ 桶必须在 FLUIDS 注册之后注册；`LanguageProvider.addItem` 只接受 `Supplier<? extends Item>`（传 DeferredItem，不能 `.get()`）
- `BlockBehaviour` 在 `net.minecraft.world.level.block.state` 包（写错成 `block.BlockBehaviour` 编译报"程序包不存在"）
- FluidType 默认翻译 key = `fluid_type.<modid>.<名>`（`Util.makeDescriptionId("fluid_type", key)`）；1.21.1 FluidType 渲染属性经 `RegisterClientExtensionsEvent.registerFluidType(IClientFluidTypeExtensions, FluidType...)`（MOD bus，仅客户端）
- FluidIngredient 体系（1.21.1 新增）：`SizedFluidIngredient.FLAT_CODEC` JSON 为 `{"fluid":..,"amount":..}`/`{"tag":..,"amount":..}`；`FluidStack.CODEC` 输出 `{"id":..,"amount":..}`

## 会话记录
### 2026-08-06 会话7：冶炼炉多方块结构系统（完成）
- 方块（block/）：`BlockSeared` 12 变体（stone/cobble/paver/brick/cracked/fancy/square/triangle/small/road/tile/creeper，独立注册替代旧版 1 方块+meta，硬度 3/抗爆 20/金属音 1:1）、`BlockSearedGlass`（0.3 硬度透明玻璃，旧版 CTM 简化）、`BlockTank`（4000mb 储罐 + 比较器 + 液体亮度）、`BlockMultiblockController` 抽象基类（FACING+ACTIVE 状态、放置即检测、未成型不可开 GUI、禁旋转）、`BlockSmelteryController`（成型喷火粒子）
- 多块检测（新包 `multiblock/`，去 Mantle）：`IMasterLogic/IServantLogic` 接口 + `MultiblockDetection`（MultiblockStructure/assignMultiBlock/isAreaLoaded 适配）+ `MultiblockCuboid`（地板→逐层墙→天花板，hasFrame/hasCeiling 可选）+ `MultiblockTinker`（isValidSlave：已归属其他主机的 servant 拒绝）+ `MultiblockSmeltery`（有地板无顶、地板仅 seared、墙体 validSmelteryBlocks={seared,tank,glass}、必须含至少 1 个 tank）
- BE 链：`TileMultiblock`（active/minPos/maxPos/checkMultiblockStructure/NBT/网络包）→ `TileHeatingStructure`（TIME_FACTOR=8 加热、fuel/temperature/needsFuel、itemTemperatures 数组、SimpleContainer 组合替代旧版 TileInventory）→ `TileHeatingStructureFuelTank`（searchForFuel/consumeFuel 从 tank 抽 50mb 岩浆、结构尺寸调整物品栏、掉出多余物品）→ `TileSmeltery`（每秒结构检测+4tick 加热+15s 全检+炉内阻塞检测、熔炼/合金/实体熔炼/部件熔炼、MeltingEvent 触发、MenuProvider）；`TileSmelteryComponent`（servant 基类：master 位置 NBT）；`TileTank`（FluidTank 4000mb + RegisterCapabilitiesEvent 注册 FLUID_HANDLER capability）
- 炉内逻辑：熔炼 = `RecipeManager.getAllRecipesFor(MELTING_TYPE)`（1.21.1 返回 RecipeHolder 需 .value()）+ ItemTagMatch 匹配；部件熔炼 = TOOL_PARTS tag + PART_MATERIAL → Material.getFluidId → 熔出 cost mb（**差异**：旧版仅 stone 部件注册熔炼，本版扩展为全部带流体关联的材料，金属部件可熔回）；合金 = AlloyRecipe.matchesAmount + SizedFluidIngredient.test 匹配 drain（每 tick 上限 10mb）；实体熔炼 = ItemEntity 可熔即拾取 + 活体生物有燃料时 lava 伤害 2 + blood 20mb；燃料 = SmelteryFuels 注册表（lava 50mb→100 tick，1:1 旧版 registerSmelteryFuel；不足 50mb 按 in²/50 折算——旧版 bug 公式 1:1 保留）
- 事件：`MeltingEvent` 触发点接入 onItemFinishedHeating（NeoForge.EVENT_BUS.post，可取消/改流体/温度/量）；SmelteryEvent 基类生效；CastingEvent 待浇铸台会话
- GUI 最小版：`ContainerSmeltery`（27 可见槽侧栏 + clickMenuButton 0/1 滚动 + 2+idx 点击液体装桶）+ `ScreenSmeltery`（旧版 smeltery.png 背景、液体 tint 色块分层渲染 + tooltip、滚轮滚动、点击液体消耗背包空桶装桶，1:1 旧版 handleTankClick）；燃料区指示；smeltery 菜单注册 RegisterMenuScreensEvent
- 资源：旧版 smeltery 贴图 18 张复制（seared 12 变体 + tank side/top + window side/top + smeltery active/inactive）+ gui/smeltery.png；DataGen：15 blockstate + 15 loot + 14 pickaxe tag + lang（12 变体中英文名 + GUI 提示）
- **验证**：`./gradlew check` BUILD SUCCESSFUL（3 轮编译修复 56→16→0 错误）；`./gradlew runData` 通过（数据文件核对：controller 8 变体/15 loot/14 tags）；`./gradlew runClient` 冒烟 ×2（initialized → ResourceManager 加载 → 无 ERROR）
- 踩坑修复：静态初始化 get() 崩溃（VALID_SMELTERY_BLOCKS 静态 Set 含 SEARED.get() → 改懒初始化方法）；BlockStateProperties.ACTIVE 不存在 → BooleanProperty.create("active")；1.21.1 无 (BlockPos,BlockPos) isAreaLoaded → hasChunk 两角检查；AABB.offset(int) → move；getAllRecipesFor 返回 RecipeHolder；IFluidHandler 需 isFluidValid；FluidStack 1.21.1 无 getTintColor → IClientFluidTypeExtensions.of(type).getTintColor(fluid)；mouseScrolled 4 参；NbtUtils.readBlockPos(CompoundTag) 不存在 → putLongArray/asLong
- 遗留：浇铸台/盆/龙头/沟槽/排液口（CastingEvent 触发点）留待下会话；GUI 温度/进度条与燃料贴图渲染未做（tint 色块占位）；游戏内实际建结构/熔矿流程未自动化验证（runClient 仅到主菜单）；炉内液体渲染模型（BE 内液体 3D 渲染）未做（GUI 可见）
- review 修复（security 复查后）：ContainerSmeltery 客户端 NPE（tile 空占位防护）+ 液体/燃料改 DataSlot 同步（broadcastChanges 自动下发，客户端 setData 回填，液体层数上限 16）；fillBucketFromTank 抽指定层（drain(FluidStack) 非 drain(int) 底层）；TileHeatingStructure NBT 保存/恢复 inventorySize（加载后炉内物品不丢）；getValidSmelteryBlocks/isFloorBlock 覆盖全部 12 seared 变体；合金扣液先 SIMULATE 验证再 EXECUTE（防扣了输入填不满输出）；ACTIVE 状态 setBlock 落盘（1.21.1 无 getActualState，纹理切换生效）；getProgress 除零保护；MultiblockCuboid 死代码清理。旧版"结构失效弹出全部物品"行为 1:1 保留（与旧版一致，devlog 记录）
- review 二轮修复：SmelteryDataSlot 客户端回填生效（客户端 setData→set() 存 clientValue→get() 返回；服务端 get() 读 BE 实时值，MC 基类 checkAndClearUpdateFlag 自动推送）；合金 EXECUTE 逐输入扣量校验 + drainedBack 回滚（同层多输入场景零损失）；review 终审 pass 可交付
- 下一步：浇铸系统（浇铸台/盆 + faucet/channel/drain + seared 楼梯/台阶）或工具站 GUI 增强

### 2026-08-06 会话6：全部自定义配方类型 + 流体/模具补全 + JEI 预留（完成）
- 配方类型（recipe/ 包，全部 Recipe+Serializer+注册到 ModRecipeTypes）：
  - MeltingRecipe（物品 tag → 流体 + 熔点；温度自动 calcTemperature 1:1：300 + (amount/1296)^log9(2) × (流体温-300)，可显式覆盖；getUsableTemperature=max(1,temp-300)）
  - CastingRecipe（模具+流体 → 物品；模具三态：形状 ID（部件注册名或铸造形状）/ 物品 tag（染色陶瓦/沙，消耗）/ null（铸造盆）；输出二态：静态物品 / 动态 c: tag 首选（1:1 旧版 PreferenceCastingRecipe——任何 mod 的锭/粒/块可铸出）；calcCooldownTime 1:1：24+(temp-300)×amount/1600；withTime 覆盖）
  - BucketCastingRecipe（通用规则：c:buckets/empty 空桶 + 任意流体 → FluidUtil.getFilledBucket 满桶；时间 5/量 1000）
  - PartRecipe（模具+材料 → 部件+余料，1:1 旧版 ToolBuilder.tryBuildToolPart：遍历 craftable 材料、getMatchValue 多槽求和 ≥ cost、canUseMaterial 过滤、leftover=(value-cost)/72 碎块；29 条 DataGen）
  - AlloyRecipe 会话5 已完整（核对无缺）；ToolBuildRecipe/RepairRecipe 会话4/4.5b 已完整（核对无缺）
- 8 流体补注册（ModFluids，1:1 旧版 TinkerFluids 温度/颜色/贴图类）：molten_emerald(999,金属)、molten_diamond(999,金属)、molten_glass(625,金属)、notmilk(800,石头类——注册名 1:1 旧版,lang 显示"钙")、venom(336,水基+毒贴图)、milk(320,水基+奶贴图)、greenslime/blueslime(370,史莱姆)；贴图 8 个从旧版复制（liquid_poison/liquid_poison_flow/milk/milk_flow ×2 mcmeta）
- 铸造形状（ModPatterns.CAST_SHAPES，1:1 旧版 CastCustom 5 meta）：ingot/nugget/gem/plate/gear（含 cost 表 144/16/666/144/576）；PatternItem.getName 非部件形状 fallback（cast.<shape> lang key）；PatternRegistryImpl.isKnownShape 同步；创造页 +5 形状 cast
- DataGen（TConRecipeProvider 扩展，全部输入 c: Tag，输出动态 tag）：
  - melting 171 条：31 具体（冰/雪/雪球→水、腐肉→血、蛛眼/河豚→毒液、史莱姆球→绿史莱姆、骨粉/骨/骨块→钙、圆石/石→熔融石、黑曜石、铁轨×3/金铁轨、马铠×2、粘土×2、绿宝石/钻石 family×6、沙/玻璃块/玻璃板→玻璃）+ 20 金属 × 7（nugget/ingot/block/ore/plate/gear/dust）
  - casting 351 条：11 具体（骨/骨块/绿宝石×2/钻石×2/玻璃板/黑曜石块/硬化粘土×2/红沙）+ 20 金属 × 5 动态输出（锭/粒/块/板/齿轮）+ 20 金属 × 4 形状 × 3 铸模流体（金 288/黄铜 144/铝黄铜 144，switchOutputs）
  - part 29 条（ModToolParts.getAllParts 遍历）、bucket_casting 1 条、合金 10 条保留
  - 输入 tag：金属 c:ores|ingots|nuggets|storage_blocks|plates|gears|dusts/<path>（path 同材料关联 pig_iron 等）；原版物品输入 14 个 mod 命名空间 tag（TConTags 新增 + TConItemTagsProvider 加入物品）；复用 c:bones/c:obsidians/c:stones/c:cobblestones/c:sands/c:glass_blocks/c:glass_panes/c:slime_balls/c:gems|c:ores|c:storage_blocks/*(emerald/diamond)
- JEI 预留：build.gradle compileOnly `jei-1.21.1-common-api/neoforge-api:19.21.0.247`（腾讯镜像可拉取，gradle.properties 加 jei_version）；client/jei 包 TConJeiPlugin（@JeiPlugin，createFromVanilla 4 类型）+ 4 分类（MeltingRecipeCategory 物品→流体+熔点 tooltip；CastingRecipeCategory 模具三态+输出；PartRecipeCategory 模具+材料；AlloyRecipeCategory 多输入流体→输出）；lang 9 条
- **验证**：`./gradlew build` BUILD SUCCESSFUL（多轮编译修复）；`./gradlew runData` 通过（836 文件：melting 171/casting 351/part 29/alloy 10 + tag/lang/模型）；配方 JSON 逐项抽查（温度 534=1/9 价值→1/2 温度系数 ✓ 数学 1:1）；`./gradlew runClient` 冒烟（initialized → 数据包加载 0 ERROR → 主菜单，新流体贴图/创造页无异常）
- 踩坑修复：1.21.1 无 TagKey.streamCodec（RL.STREAM_CODEC.map 自定义）；无 StreamCodec.either（匿名布尔标记实现）；StreamCodec.unit 泛型推断失败（匿名类）；ResourceLocation.STREAM_CODEC/ByteBufCodecs.VAR_INT 是 ByteBuf 版——StreamCodec.composite 泛型 B 推断冲突（全匿名手写 encode/decode）；RecipeOutput 无 save（output.accept(id, recipe, null)）；ExtraCodecs.strictOptionalField 签名不同（optionalFieldOf(name, default)）；RecipeIngredientRole 在 mezz.jei.api.recipe 包（非 constants）；SizedFluidIngredient.getFluids() 取匹配流体；registerMelting(Item,Fluid,int) 第三参是**量**（非温度，温度自动算）
- 遗留（冶炼炉会话）：seared 系列方块/灼热石流体（stone 熔炼已指向本 mod molten_stone）、泥砖/grout、史莱姆球/凝滞史莱姆块物品、钢浇铸（blazingBlood 未注册）、lavawood/clear glass、实体熔炼（EntityMeltingRecipe）；部件熔炼/浇铸为炉内逻辑（Tag 无法表达"带材料部件"输入，冶炼炉会话用 tool_parts tag+组件匹配实现）；oreNether/oreDense/orePoor/oreNugget 熔炼条目跳过（1.21.1 c: tag 无对应）；PartRecipe 消耗/副产物接入需部件加工台 GUI 会话；JEI alloy 输入为 SizedFluidIngredient 匹配流体快照（tag 未展开全部）
- 下一步：冶炼炉多方块（含配方接入+事件触发点）或工具站 GUI 增强
### 2026-08-06 会话5：金属流体 + 温度系统 + 合金系统（完成）
- 流体注册（fluid/ModFluids 重写）：26 种流体全注册——20 熔融金属（iron/gold/pigiron/cobalt/ardite/manyullyn/knightslime/alubrass/alumite/brass/copper/tin/bronze/zinc/lead/nickel/silver/electrum/steel/aluminum）+ 4 石头类（molten_stone/obsidian/clay/dirt）+ blood + purpleslime（合金输入支撑）；每流体 = FluidType（属性 1:1 旧版 TinkerFluids：金属 density 2000/viscosity 10000/light 10 + 各自温度/稀有度）+ BaseFlowingFluid.Source/Flowing + LiquidBlock + BucketItem；FluidEntry record 汇总全部条目（type/still/flowing/block/bucket/贴图/tint）；注册顺序 FLUID_TYPES→FLUIDS→ITEMS→BLOCKS
- 温度系统：熔点 1:1 自旧版 setTemperature 写入 FluidType.Properties.temperature；附属 API `FluidRegistry.getTemperature(fluidId)`（default 方法，未注册返回 -1）
- 合金系统（recipe/ 包）：AlloyRecipeInput（RecipeInput 包装流体列表）+ AlloyRecipe（matchesAmount 1:1 旧版 ratio 次数匹配，isSpecial）+ AlloyRecipeSerializer（codec=RecordCodecBuilder(SizedFluidIngredient.FLAT_CODEC.listOf + FluidStack.CODEC)，streamCodec 同构）+ AlloyRecipeBuilder（DataGen）；ModRecipeTypes 注册 alloy 类型/序列化器；TConRecipeProvider 生成 10 条 1:1 合金（obsidian/clay/knightslime/pigiron/manyullyn/bronze/electrum/alubrass/brass/alumite，含水+岩浆输入）
- 流体 tag：TConTags.fluidTag + TConFluidTagsProvider（vanilla FluidTagsProvider），26 个 `c:molten_iron` 风格 tag（用户确认 NeoForge 标准）；DataGen：流体方块 blockstate+占位模型（particle=类别贴图）、桶模型（neoforge:fluid_container）、lang 26 流体+26 桶（en/zh）、创造页 26 桶
- 材料关联：ModMaterials.registerFluidAssociations 14 金属材料 ↔ 熔融流体（1:1 旧版 MaterialIntegration.integrateFluid）
- 客户端：TConFluidRenderProperties（IClientFluidTypeExtensions：still/flow 贴图 + tint）+ ModClientEvents RegisterClientExtensionsEvent 注册；旧版贴图 12 个复制（molten_metal/liquid/liquid_stone/liquid_slime 各 still+flow+mcmeta）
- **验证**：`./gradlew check` BUILD SUCCESSFUL（3 轮编译修复）；`./gradlew runData` 通过（236 文件，合金 JSON/tag/模型/lang 逐项核对）；`./gradlew runClient` 冒烟（initialized → 资源加载 → 主菜单，无 ERROR）
- 踩坑修复：LanguageProvider static 块插入破坏结构；BlockBehaviour 包路径（block.state）；LiquidBlock 需 FlowingFluid 强转；FluidTagsProvider 在 vanilla data.tags 包；tag path 多一层 fluid/（c:fluid/ 前缀错误，改 c:<name>）；addItem 只收 Supplier
- 遗留：熔炼/浇铸配方类型（MeltingRecipe/CastingRecipe）与冶炼事件触发点（SmelteryEvent/MeltingEvent/CastingEvent API 已发布）待冶炼炉会话；emerald/diamond/glass/calcium/venom/milk/slime 系列流体未注册（非合金输入，冶炼炉会话补）；obsidianAlloy 配置开关未做（旧版默认 true，无条件注册）；合金 c:ingots tag 仍空（附属/物品注册后自动生效）
- 下一步：冶炼炉多方块（含熔炼/浇铸配方接入）或工具站 GUI 增强
### 2026-08-06 会话4.5b：ranged 完整化 + 修复机制 + 工具站/锻造厂 GUI（完成）
- 弹射物实体：`entity/` 包新建 `ModEntities`（arrow/bolt/shuriken 三实体 DeferredRegister，1:1 尺寸 0.5×0.5/0.3×0.1）+ `TinkerProjectileBase extends AbstractArrow`（1:1 伤害公式=(弹射物攻击+弓基础×power+bonusDamage)×damageModifier×power；反弹/消失；重力 Bolt 0.065/Shuriken 动态(tickCount/10×0.04)；阻力按旧版 getSlowdown 差值在 super.tick 后缩放；背包实际弹药栈结算+损坏伤害 1；NBT power/launching）+ TinkerArrow/TinkerBolt/TinkerShuriken
- 客户端：`client/` 包新建，`TinkerProjectileRenderer`（物品模型渲染+ArrowRenderer 朝向+手里剑自旋），FMLClientSetupEvent 注册（1.21.1 无 RegisterRenderersEvent）；`ModClientEvents` 统一客户端注册
- 发射接线：BowToolItem 1:1（power=(p²+2p)/3×p×baseSpeed×range、findAmmo 主副手→快捷栏→背包且弹药>0、consumeAmmo、getProjectileEntity、拉满暴击、每发扣 1 耐久）；ProjectileToolItem 弹药模型 1:1（durabilityPerAmmo=10、useAmmo→damageTool(10)、getProjectileStack、弹药耐久条、弹药/精准度 tooltip）；Arrow/Bolt getProjectile（精准度修正 inaccuracy -= (1-1/acc)×speed/2）；Shuriken 投掷（cooldown 4、speed 2.1）；CrossBow loaded 装填（CROSSBOW_LOADED 组件+拉满装填+右键发射+UseAnim.NONE）；修正 BowCore 默认 baseInaccuracy 0f/baseProjectileSpeed 3f（弩不准度 0）
- 修复机制：SharpeningKit 部件（cost=288、HEAD 材料、进模具/创造页/tag）+ TinkerToolItem.repair/calculateRepairAmount/calculateRepair（144 换算、多材料×(1+(n-1)/9)、修饰符惩罚、修复递减下限 0.5、全消耗校验）+ RepairRecipe/Serializer（crafting_special）+ DataGen（配方/lang/tag/贴图）
- 工具站/锻造厂 GUI 最小版：BlockToolTable（工具站木/锻造厂金属）+ ToolTableBlockEntity（SimpleContainer 5 槽+NBT）+ TinkerStationMenu（5 部件槽+结果槽预览+取走即消耗+背包 36 槽）+ TinkerStationScreen（旧版 generic.png）+ RegisterMenuScreensEvent 注册；DataGen blockstate/模型/loot/lang/mineable tag
- **验证**：`./gradlew check` 多次 BUILD SUCCESSFUL；`./gradlew runData` 通过（repair.json/模型/loot/lang/tag 产物核对）；`./gradlew runClient` 冒烟通过（修复创造标签页崩溃后：mod 加载→资源加载→进入单人世界，无 ERROR）
- 踩坑修复：创造标签页崩溃（BlockItem 未注册→asItem=air）→ ModItems 手动 registerSimpleBlockItem；MenuScreens.register private→RegisterMenuScreensEvent；BaseEntityBlock 无泛型+codec()；MenuType 双参构造；ContainerHelper 参数序；TooltipContext 内部类
- 遗留：箭袋旧版无此物跳过（devlog 待办标注）；工具站 GUI 增强（Shift/拆解/修饰符/修复按钮）；弹射物 trait 钩子；Bolt 混合伤害；3D 弹射物模型；CrossBow preventSlowDown 减速
- 下一步：流体系统（熔融钴/阿迪特，冶炼炉前置）或工具站 GUI 增强
### 2026-08-06 会话3.5：RecipeMatch→ItemTag 材料-物品关联体系（完成）
- util 包新建 `ItemTagMatch`（record：TagKey<Item> + amount 价值），替代旧版 Mantle RecipeMatch（matches = stack.is(tag)，空栈恒 false）
- Material 新增：`addItem(TagKey, value)`（旧版 addItem(oredict,1,amount)，needed 恒 1 已省略）/ `addItemIngot` / `addCommonItems(金属路径)`（c:ingots|nuggets|storage_blocks/<path> 三件套）/ `matches` / `getMatchValue` / `hasItems` / `getItemMatches`；代表物品三式：setRepresentativeItem(TagKey)（运行时 BuiltInRegistries.ITEM.getTag 取首个，对应旧版 representativeOre）/ (Item) / (ItemStack)
- 迁移差异：setRepresentativeItem 取消旧版"必须已关联否则 warn"校验——材料绑定在 Mod 构造器，早于数据包 tag 加载，运行期校验不可行（javadoc 注明）
- 40 材料绑定（ModMaterials.registerItemAssociations，1:1 自 TinkerMaterials.setupMaterials）：wood(rods/planks/logs)、stone(cobblestones+stones)、flint/cactus/bonemeal/paper/sponges/vines/packed_ice/end_rods/prismarine 系列走 mod 命名空间 tag；骨/obsidian/netherrack/endstone/string/feather/blaze/reed 走 c: tag；leaf→minecraft:leaves；14 金属/合金 addCommonItems（cobalt/ardite 复用会话2 已有 c: tag）
- TConTags 新增 11 个 mod 命名空间 tag 常量；TConItemTagsProvider DataGen 加入原版物品（flint、cactus、prismarine×3、bone_meal、paper、sponge、vine、packed_ice、end_rod）
- **验证全通过**：`./gradlew build`（BUILD SUCCESSFUL，1 次修复 HolderSet.Named 无 isEmpty() → 改 size()）、`./gradlew runData`（+11 文件，tag JSON 内容核对无误）、二次 build 确认
- 遗留：firewood/slimecrystal*/boneBloodied/slimevine*/slimeleaf* 关联待物品注册会话；合金 c: tag 现空待附属填充；匹配体系暂无调用方（冶炼/部件加工台会话接入）
- 下一步：流体系统（熔融钴/阿迪特）或会话4.5b（ranged/修复/GUI）
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
