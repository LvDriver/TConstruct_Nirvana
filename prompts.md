# 提示词手册 — TConstruct: Nirvana 开发

> 使用方式：每个会话复制对应提示词粘贴到 Reasonix 即可。
> 纪律：**一个会话只做一个子系统**，做完 → build 通过 → 更新 devlog → 关闭会话。
> 所有提示词默认在项目根目录 `D:\Lv\TConstruct_Nirvana1.21.1v0.0.1\` 下运行 `reasonix`。

---

## 通用开场（每个会话开头都要发）

```
先读取 devlog.md 了解当前项目进度，然后读取 AGENTS.md 确认项目约定。读完后用一句话告诉我：当前进度到哪了、本次会话要做什么。不要开始写代码，等我确认。
```

---

## 会话0：旧代码结构分析（只读不写，第一个会话）

```
本次会话只分析旧代码，不写任何项目代码。

任务：分析 ./TinkersAntique-1.12/src/main/java/ 下的匠魂怀古源码结构。

步骤：
1. 列出顶层包结构（只看目录，不逐文件读）
2. 逐个子系统读取关键入口类（每个子系统只读 1-2 个核心文件），识别以下子系统：
   - 材料系统（Material 定义、属性）
   - 矿物与金属（ ores、ingots、nuggets）
   - 工具部件（ToolPart、部件类型）
   - 模具（Pattern、Cast）
   - 工具组装（ToolBuilder、工具构造逻辑）
   - 修饰符（Modifier、Trait）
   - 冶炼炉（Smeltery、多方块结构）
   - 流体（Fluids、温度、合金）
   - 配方系统（Recipe 类型）
   - GUI（Container/Gui 类）
   - 世界生成（OreGen、宝箱战利品）
   - Mantle 依赖（列出被引用的 slimeknights.mantle.* 类）
3. 对每个子系统输出：核心类路径、功能简述、Mantle 依赖情况、迁移难度评估

最后把分析结果写入 devlog.md 的"子系统清单"小节，格式：
### 子系统清单
| 子系统 | 核心类路径 | 功能 | Mantle依赖 | 迁移难度 | 优先级 |

不要写代码，不要改项目文件，只更新 devlog.md。
```

---

## 会话1：脚手架搭建

```
本次会话：创建 NeoForge 1.21.1 MDK 工程脚手架。

前提：先读 devlog.md 确认子系统清单已分析完成。

步骤：
1. 生成 build.gradle（NeoForge 21.1.x 最新稳定版、Java 21、Gradle 8.x）
2. 生成 settings.gradle、gradle.properties（mod_id=tconstruct_nirvana、mod_name=Tinkers' Construct: Nirvana、mod_version=0.0.1）
3. 生成 neoforge.mods.toml（依赖 neoforge 21.1.x，可选依赖 jei）
4. 创建主类 TConstructNirvana（@Mod("tconstruct_nirvana")，构造器里注册 DeferredRegister）
5. 创建注册类骨架（空壳即可，后续会话填充）：
   - ModItems（DeferredRegister<Item>）
   - ModBlocks（DeferredRegister<Block>）
   - ModBlockEntities（DeferredRegister<BlockEntityType<?>>）
   - ModFluids（DeferredRegister<FluidType>）
   - ModRecipeTypes（后续注册自定义配方类型）
   - ModDataComponents（后续注册 DataComponentType）
   - ModCreativeTabs（DeferredRegister<CreativeModeTab>）
6. 创建配置类 TConConfig（ModConfigSpec，预留矿物生成开关等配置项）
7. 创建 DataGen 入口类 TConDataGen（implements GatherDataEvent helper）

验证：./gradlew build 必须通过，./gradlew runClient 必须能启动到游戏主菜单（空 mod 无内容即可）。

完成后更新 devlog.md：
- 项目状态改为"脚手架已搭建"
- 待办里勾选 MDK 工程项
- 会话记录补充本会话成果
```

---

## 会话2：材料与矿物系统

```
本次会话：移植材料定义与矿物/金属物品。

先读 devlog.md 确认脚手架已完成。参考旧代码路径：
- ./TinkersAntique-1.12/src/main/java/slimeknights/tconstruct/library/materials/
- ./TinkersAntique-1.12/src/main/java/slimeknights/tconstruct/world/（矿物方块/物品）

任务：
1. 材料系统：
   - 读取旧代码 Material 类和材料属性定义（耐久、采掘等级、攻击力、开采速度等）
   - 用 1.21.1 方式重建材料定义：enum 或 record + 静态注册
   - 材料属性用 DataComponent 或自定义数据结构存储
2. 矿物方块与物品：
   - 钴矿（Cobalt Ore）、阿迪特矿（Ardite Ore）的方块和物品
   - 深板岩变体（如有）
   - 锭（ingot）、粒（nugget）物品
   - 全部用 DeferredRegister 注册到 ModItems/ModBlocks
3. 矿物世界生成：
   - 用 DataGen 生成矿石生成配置（BiomeModifier JSON）
   - 读取旧代码的矿脉参数（Y轴范围、矿脉大小、生成率）1:1 还原
4. 创造模式标签页：注册一个 TCon 创造标签页，放入当前所有物品

注意事项：
- 材料属性数据必须和匠魂怀古完全一致（1:1 还原）
- 矿物生成用 1.21.1 的 BiomeModifier 机制，不用旧版 IWorldGenerator
- 矿物词典用 Tag（ItemTags / BlockTags），不用旧版 OreDictionary

验证：./gradlew build 通过，runData 生成矿石配置 JSON，runClient 能看到物品在创造标签页。

完成后更新 devlog.md。
```

---

## 会话3：工具部件与模具

```
本次会话：移植工具部件物品与模具系统。

先读 devlog.md。参考旧代码路径：
- ./TinkersAntique-1.12/src/main/java/slimeknights/tconstruct/tools/parts/
- ./TinkersAntique-1.12/src/main/java/slimeknights/tconstruct/library/tools/parts/
- ./TinkersAntique-1.12/src/main/java/slimeknights/tconstruct/library/casts/

任务：
1. 工具部件物品：
   - 读取旧代码列出所有部件类型（镐头、斧刃、剑刃、手柄、绑定等）
   - 每个部件用 DeferredRegister 注册为 Item
   - 部件存储材料信息：用 DataComponentType 记录部件所用的 Material
   - 部件属性从材料属性计算（耐久、采掘等级等，和旧版公式一致）
2. 模具系统：
   - 模具物品（Pattern：空白模具）
   - 浇铸模具（Cast：有部件形状的模具）
   - 模具用 DataComponent 记录形状信息
3. 部件与模具的关联：
   - 每个部件类型对应一个模具形状
   - 用 Tag 或注册表关联部件和模具

注意事项：
- 部件材料数据必须用 DataComponent，不用 NBT
- 部件属性计算公式和匠魂怀古完全一致
- 模具的形状标识用 ResourceLocation，不用旧版的字符串 ID

验证：./gradlew build 通过，runClient 能注册所有部件和模具物品。

完成后更新 devlog.md。
```

---

## 会话4：工具组装与修饰符

```
本次会话：移植工具组装逻辑与修饰符系统。

先读 devlog.md。参考旧代码路径：
- ./TinkersAntique-1.12/src/main/java/slimeknights/tconstruct/library/tools/
- ./TinkersAntique-1.12/src/main/java/slimeknights/tconstruct/library/modifiers/
- ./TinkersAntique-1.12/src/main/java/slimeknights/tconstruct/library/traits/

任务：
1. 工具组装系统：
   - 读取旧代码 ToolBuilder / ToolConstruction 逻辑
   - 工具物品（镐、斧、剑、铲、锄等）注册到 ModItems
   - 工具用 DataComponent 存储组成信息（哪些部件、什么材料）
   - 工具属性从部件属性计算（和旧版公式一致）
   - 工具组装逻辑：输入部件 → 输出工具（在工作台或专用方块中完成）
2. 修饰符系统：
   - 读取旧代码 Modifier 类和所有修饰符定义
   - 修饰符用注册表注册（DeferredRegister<Modifier> 或自定义注册表）
   - 修饰符效果 1:1 还原（挖掘加速、自动修复、击退等）
   - 修饰符存储在工具的 DataComponent 中
3. 材料特性（Trait）：
   - 读取旧代码 Trait 系统
   - 每种材料的特性作为特殊修饰符实现
   - 特性触发时机和效果与旧版一致

注意事项：
- 工具数据全部用 DataComponent，禁止 NBT
- 修饰符的触发机制用 NeoForge 事件（EVENT_BUS），不用旧版 Hook
- 工具属性计算必须和匠魂怀古公式一致，先列公式给我确认再写

验证：./gradlew build 通过，runClient 能组装出工具、修饰符生效。

完成后更新 devlog.md。
```

---

## 会话5：冶炼流体与温度

```
本次会话：移植金属流体、温度系统与合金系统（不含多方块冶炼炉结构）。

先读 devlog.md。参考旧代码路径：
- ./TinkersAntique-1.12/src/main/java/slimeknights/tconstruct/smeltery/
- ./TinkersAntique-1.12/src/main/java/slimeknights/tconstruct/library/fluid/

任务：
1. 金属流体注册：
   - 列出旧代码所有流体（熔融铁、熔融金、熔融铜、熔融钴等）
   - 用 NeoForge 1.21.1 FluidType + DeferredRegister 注册流体
   - 每种流体对应方块和物品（FluidBlock、BucketItem）
   - 流体属性（粘度、温度、光照）和旧版一致
2. 温度系统：
   - 每种金属的熔点温度和旧版一致
   - 温度数据存储在流体的 FluidType 属性中
3. 合金系统：
   - 读取旧代码合金配方（如熔融铁+熔融镍=熔融殷钢）
   - 用自定义配方类型实现合金（输入流体→输出流体）
   - 合金配方用 DataGen 生成

注意事项：
- 流体用 NeoForge 1.21.1 的 FluidType 体系，不用旧版 BlockFluidBase
- 流体渲染用 DataGen 生成流体模型 JSON
- 合金配方存储为自定义 RecipeType

验证：./gradlew build 通过，runData 生成流体相关 JSON，runClient 能放置流体方块。

完成后更新 devlog.md。
```

---

## 会话6：配方系统

```
本次会话：移植所有自定义配方类型（冶炼、浇铸、部件制作、合金等）。

先读 devlog.md。参考旧代码路径：
- ./TinkersAntique-1.12/src/main/java/slimeknights/tconstruct/smeltery/recipe/
- ./TinkersAntique-1.12/src/main/java/slimeknights/tconstruct/tools/recipe/

任务：
1. 注册自定义 RecipeType：
   - 冶炼配方（MeltingRecipe：物品→流体+温度+时间）
   - 浇铸配方（CastingRecipe：模具+流体→物品+冷却时间）
   - 合金配方（AlloyRecipe：多流体→流体）
   - 部件制作配方（PartRecipe：材料+模具→部件+成本）
   - 工具组装配方（如旧版有特殊组装配方）
2. 每个 RecipeType 实现：
   - Recipe 接口（匹配逻辑、结果计算）
   - RecipeSerializer（网络同步 + JSON 序列化）
   - 注册到 ModRecipeTypes
3. DataGen：
   - 生成所有配方 JSON（从旧代码的配方定义迁移）
   - 配方数据和匠魂怀古完全一致
4. 配方查看兼容：
   - 为 JEI 提供配方分类（RecipeCategory）
   - JEI 集成代码放 client 子包

注意事项：
- RecipeSerializer 用 1.21.1 的 StreamCodec 网络同步
- 配方 JSON 格式遵循 1.21.1 vanilla 风格
- JEI 兼容用 Optional 依赖，不硬引用 JEI 类

验证：./gradlew build 通过，runData 生成配方 JSON，JEI 能显示配方。

完成后更新 devlog.md。
```

---

## 会话7：GUI 系统

```
本次会话：移植工作台与冶炼炉的 GUI（Menu + Screen）。

先读 devlog.md。参考旧代码路径：
- ./TinkersAntique-1.12/src/main/java/slimeknights/tconstruct/common/gui/
- ./TinkersAntique-1.12/src/main/java/slimeknights/tconstruct/tools/client/gui/
- ./TinkersAntique-1.12/src/main/java/slimeknights/tconstruct/smeltery/client/gui/

任务：
1. 工作台方块与方块实体：
   - 模具台（PartBuilder）、工具组装台（ToolStation）、工具锻造台（ToolForge）
   - 注册方块 + BlockEntity，BlockEntity 持有物品栏
2. Menu 实现：
   - 每个 GUI 一个 MenuType（DeferredRegister 注册）
   - AbstractContainerMenu 子类实现物品栏同步
   - ContainerData 同步进度数据（如冶炼进度）
3. Screen 实现：
   - 放 client 子包，AbstractContainerScreen 子类
   - 贴图先用旧版 GUI 贴图
   - 按钮和槽位布局和匠魂怀古一致
4. 方块实体注册与渲染：
   - BlockEntityType 注册到 ModBlockEntities
   - 方块模型用 DataGen 生成

注意事项：
- Menu 和 Screen 严格分端，Screen 只在 client 子包
- GUI 贴图路径放 assets/tconstruct_nirvana/textures/gui/
- 槽位逻辑必须和匠魂怀古一致（哪个槽放什么）

验证：./gradlew build 通过，runClient 能打开各工作台 GUI 并正常交互。

完成后更新 devlog.md。
```

---

## 会话8：冶炼炉多方块（后期）

```
本次会话：移植冶炼炉多方块结构系统。

先读 devlog.md 确认冶炼流体、温度、配方系统已完成。参考旧代码路径：
- ./TinkersAntique-1.12/src/main/java/slimeknights/tconstruct/smeltery/
- ./TinkersAntique-1.12/src/main/java/slimeknights/tconstruct/smeltery/tileentity/

任务：
1. 冶炼炉多方块结构：
   - 控制器方块（SmelteryController）：检测多方块是否成型
   - 结构方块：冶炼炉砖、冶炼炉控制器、排液口、浇铸盆
   - 多方块验证逻辑（读取旧代码的 MultiblockStructure / SmelteryDetector）
2. 冶炼炉方块实体：
   - SmelteryTileEntity → BlockEntity：持有物品栏 + 流体罐
   - 熔炼逻辑：消费燃料/温度 → 物品熔化 → 流体存储
   - 合金逻辑：多流体混合 → 合金配方匹配 → 输出流体
   - 排液逻辑：排液口 → 浇铸盆/浇铸台
3. 流体存储：
   - 用 NeoForge 1.21.1 的 FluidStorage / IFluidHandler
   - 冶炼炉容量和旧版一致
4. 冶炼炉 GUI：
   - 显示当前温度、熔融流体列表、燃料进度
   - 先用旧版贴图

注意事项：
- 多方块检测逻辑必须和匠魂怀古一致（哪些方块算有效结构）
- 流体罐用 NeoForge Capability 体系
- 冶炼炉内部逻辑放服务端，渲染放 client 子包
- 这是最复杂的子系统，分步做：先结构检测，再熔炼逻辑，再 GUI

验证：./gradlew build 通过，runClient 能搭建冶炼炉、放入物品熔化、浇铸出部件。

完成后更新 devlog.md。
```

---

## 会话9：世界生成与战利品

```
本次会话：完善世界生成（矿物）与宝箱战利品。

先读 devlog.md。参考旧代码路径：
- ./TinkersAntique-1.12/src/main/java/slimeknights/tconstruct/world/
- ./TinkersAntique-1.12/src/main/java/slimeknights/tconstruct/world/worldgen/

任务：
1. 矿物生成（如会话2未完成的部分）：
   - 检查钴矿/阿迪特矿的 BiomeModifier 是否已生成
   - 补充下界矿物生成（如旧版有下界矿物）
   - 矿脉参数和匠魂怀古一致
2. 宝箱战利品：
   - 读取旧代码的 LootTable 注入（地牢、神殿、村庄等）
   - 用 DataGen 生成 LootModifier JSON
   - 战利品内容（模具、锭、部件）和旧版一致
3. 可选：匠魂小屋结构（如旧版有）：
   - 用 StructurePool / Jigsaw 实现
   - 如复杂度过高可跳过，后续单独会话做

注意事项：
- 世界生成全部用 DataGen（BiomeModifier + Datapack）
- 战利品用 LootModifier，不用旧版 LootTablePool 注入
- 矿物生成的 Y 轴范围必须和匠魂怀古一致

验证：./gradlew build 通过，runData 生成 JSON，runClient 新建世界能看到矿物生成。

完成后更新 devlog.md。
```

---

## 会话10：兼容性

```
本次会话：实现与其他 mod 的兼容性（500mod 整合包场景）。

先读 devlog.md。参考旧代码的兼容模块（如有）。

任务：
1. JEI 兼容：
   - 冶炼配方 JEI 分类（ MeltingRecipeCategory）
   - 浇铸配方 JEI 分类（CastingRecipeCategory）
   - 合金配方 JEI 分类（AlloyRecipeCategory）
   - 部件制作 JEI 分类（PartRecipeCategory）
   - JEI 代码放 client 子包，用 Optional 依赖 + @Mod.EventBusSubscriber
2. 矿物词典 / Tag 兼容：
   - 所有锭、粒、矿物用统一 Tag（forge:ingots/iron 等）
   - 确保其他 mod 的冶炼配方能识别本 mod 的矿物
   - 流体 Tag：forge:fluid/molten_iron 等
3. Curios 饰品栏兼容（如有饰品）：
   - 如果匠魂怀古有戒指/护身符等饰品，用 Curios API 注册
   - 可选依赖，不硬引用
4. 配置项：
   - 矿物生成开关（允许整合包作者关闭本 mod 矿物）
   - 冶炼炉容量配置
   - 默认值和匠魂怀古一致

注意事项：
- JEI 兼容用 compileOnly 依赖，runtime 用 Optional
- Tag 命名遵循 forge: 前缀规范
- 所有兼容代码用反射或 Optional，避免缺失依赖时崩溃

验证：./gradlew build 通过，带 JEI 启动 runClient 能看到配方分类。

完成后更新 devlog.md。
```

---

## 会话11：资源与本地化

```
本次会话：完善本地化、模型与贴图引用。

先读 devlog.md。

任务：
1. 本地化（lang）：
   - 用 DataGen 生成 en_us.json 和 zh_cn.json
   - 所有物品、方块、流体、修饰符、GUI 文本的翻译 key
   - 翻译文本参考匠魂怀古的本地化文件：
     ./TinkersAntique-1.12/src/main/resources/assets/tconstruct/lang/
2. 模型 JSON：
   - 用 DataGen 生成所有物品/方块的模型 JSON
   - 基础物品用 item/generated，工具用自定义模型
3. 贴图引用：
   - 从旧版资源目录复制贴图到 src/main/resources/assets/tconstruct_nirvana/textures/
   - 贴图路径映射：旧版 tconstruct: → 新版 tconstruct_nirvana:
   - 后续你会手动重画，本次只确保引用正确
4.方块状态 JSON：
   - 用 DataGen 生成所有方块的 blockstate 和 model

注意事项：
- 翻译 key 格式统一：item.tconstruct_nirvana.xxx / block.tconstruct_nirvana.xxx
- 贴图先搬旧版，文件名保持一致方便后续替换
- 模型 JSON 不要手写，全部 DataGen

验证：./gradlew build 通过，runData 生成所有 JSON，runClient 所有物品有正确名称和贴图。

完成后更新 devlog.md。
```

---

## 通用收工提示（每个会话结束前发）

```
本次会话即将结束，请执行收工流程：

1. 确认 ./gradlew build 已通过
2. 更新 devlog.md：
   - 项目状态：当前阶段更新
   - 最后更新：改为今天日期
   - 待办：勾选已完成项，补充新发现的待办
   - 已知 Bug：记录本次发现但未解决的 bug
   - 会话记录：补充本会话日期 + 做了什么 + 下一步建议
3. 列出本会话新建/修改的文件清单
4. 告诉我下一个会话应该做什么

不要修改 AGENTS.md（除非有需要固化的新约定）。
```

---

## 调试专用会话提示（遇到 bug 时单独开会话）

```
本次会话：调试一个 bug，不开发新功能。

先读 devlog.md 了解项目状态。

Bug 描述：[粘贴崩溃日志或 bug 现象]

步骤：
1. 分析崩溃日志/现象，定位问题文件
2. 读取相关源文件（只读相关的，不要整库扫描）
3. 如果需要参考旧版实现，读取 ./TinkersAntique-1.12/ 对应文件
4. 修复 bug，保持最小 diff
5. ./gradlew build 验证

修复后更新 devlog.md 的"已知 Bug"小节：标注已修复 + 修复方式。
```

---

## 使用节奏建议

| 会话 | 内容 | 预计轮次 | 备注 |
|------|------|----------|------|
| 0 | 旧代码分析 | 10-15 | 只读不写，轻量 |
| 1 | 脚手架 | 5-10 | 建工程+编译验证 |
| 2 | 材料与矿物 | 20-30 | 第一个内容会话 |
| 3 | 工具部件与模具 | 15-25 | |
| 4 | 工具组装与修饰符 | 30-40 | 最复杂的逻辑 |
| 5 | 冶炼流体与温度 | 15-25 | |
| 6 | 配方系统 | 20-30 | |
| 7 | GUI 系统 | 20-30 | |
| 8 | 冶炼炉多方块 | 40-50 | 最复杂的子系统，后期做 |
| 9 | 世界生成与战利品 | 10-15 | |
| 10 | 兼容性 | 15-20 | |
| 11 | 资源与本地化 | 10-15 | 收尾 |

总计约 210-300 轮。按之前估算，缓存命中 80%+ 的话，总花费约 150-300 元。

> **省钱纪律**：每个会话做完就关，别"顺便"做下一个子系统。新会话开头先读 devlog.md，前缀短且稳定，缓存命中才高。
