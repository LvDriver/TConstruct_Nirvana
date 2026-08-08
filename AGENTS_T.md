# AGENTS_T.md — 审查阶段 AI 约定

> 本文件是人工审查阶段的 **自包含** AI 约定，无需读取 AGENTS.md 或 devlog.md。
> 审查发现的问题记录在 `debuglog_T.md`。

## 审查阶段说明
- 阶段：代码开发完成，进入人工审查 + 问题修复阶段
- AI 角色：修复用户在游戏中发现的问题，包括 Bug 修复和未实现功能的补全
- 工作方式：用户逐节点审查 → 粘贴该节点的审查结果 → AI 修复 → 用户验证 → 下一个节点
- 用户按节点控制节奏，AI **不要自行扩大修复范围**，修完当前节点等用户确认

## 项目路径
| 用途 | 路径 | 权限 |
|------|------|------|
| 主项目（代码+资源） | `D:\Lv\TConstruct_Nirvana1.21.1v0.0.1\` | 可读写 |
| debuglog_T.md（审查日志） | `D:\Lv\TConstruct_Nirvana1.21.1v0.0.1\debuglog_T.md` | 可读写 |
| 旧版匠魂怀古源码 | `D:\Lv\TConstruct_Nirvana1.21.1v0.0.1\TinkersAntique-1.12\` | 只读参考 |

## 审查节点规划
| 会话 | 优先级 | 节点 |
|------|--------|------|
| T0 | P0 | P0-1 GUI显示 / P0-2 物品图标 / P0-3 材料颜色 / P0-4 动画&模版 / P0-5 冶炼炉tooltip / P0-6 物品模型 / P0-7 JEI&Jade |
| T1 | P1 | P1-1 工具组装 / P1-2 属性&修饰符 / P1-3 熔融&合金 / P1-4 浇筑&桶 / P1-5 强化 / P1-6 弹射物&弓 |
| T2 | P3 | P3-1 矿物生成 / P3-2 史莱姆岛 / P3-3 匠魂宝典 / P3-4 性能优化 |
| T3A | P3A | Mek / EIO / AE2 / DE |
| T3B | P3B | 实用拓展 / 无尽贪婪 / Botania / 等价交换 / 暮色 / 天境 / 冰火 / 豆腐 |
| T3C | P3C | IF / IE / RFT / KER / Psi |
| T3D | P3D | 灾变 / 神化 / ISS / 神秘学 / 新生魔艺 / 魔法师 |

> 优先级仅用于排审查顺序，不是 Bug 等级。

## 修复原则
1. **按节点修**：用户每次给一个节点的审查结果，只修这个节点的问题，不要碰其他节点的代码
2. **最小 diff**：只改问题相关代码，不顺手重构
3. **修完即验**：每修完一个问题跑 `./gradlew build`，编译通过再继续
4. **记录修复**：在 `debuglog_T.md` 记录修复结果（改了什么文件、怎么改的）
5. **遇到未实现功能**：评估工作量，小改动直接做，大改动先告诉用户需要多少行代码、涉及哪些文件，让用户决定是否在本节点做
6. **解释简短**：结论在前，不输出长篇背景分析
7. **加密软件注意**：公司加密软件可能破坏 java 源文件（read 报 NUL 字节），如遇此情况让用户粘贴代码重建

## 代码约定（本文件自包含，无需读 AGENTS.md）
- Mod ID：`tconstruct_nirvana`，主包名：`com.lvdriver.tconstruct_nirvana`
- 目标：Minecraft 1.21.1 · NeoForge 21.1.x · Java 21
- 注册一律用 `DeferredRegister`，集中在 `ModItems` / `ModBlocks` 等类
- 物品数据用 `DataComponents.*` / 自定义 `DataComponentType`，**禁止 NBT tag**
- 事件总线：注册类→`NeoForge.MOD_EVENT_BUS`，运行期→`NeoForge.EVENT_BUS`
- `@EventBusSubscriber` 务必指明 `bus` 参数
- 配方输入一律用 Tag（`#c:ores/iron` 等），禁止用具体物品 ID
- 数据驱动内容用 DataGen 生成，不手写 JSON
- 客户端逻辑放 `client` 子包，与服务端分端
- 包结构：`item / block / entity / fluid / data / recipe / event / client / gui / util / world / api / impl`
- Mappings：Official (Mojang)
- 参考旧版时从 `TinkersAntique-1.12/` 对应路径单文件读取，旧代码的 import/API 一律视为待重写

## 构建命令
- 编译验证：`./gradlew build`
- 运行客户端：`./gradlew runClient`
- 数据生成：`./gradlew runData`

## 已踩过的坑
- 1.21.1 类名与 1.20 不同：`BootstrapContext`（非 BootstapContext）、`RegistrySetBuilder` 在 `net.minecraft.core`、`BlockLootSubProvider` 在 `net.minecraft.data.loot`
- `BlockLootSubProvider.getKnownBlocks()` 必须 override 只返回本 mod 方块
- 物品模型贴图按注册名查找，旧版命名不匹配会报 Texture does not exist
- 1.21.1 移除 `Block#isBeaconBase`，信标基座改 `minecraft:beacon_base_blocks` tag
- NeoForge 21.1 无 ForgeFlowingFluid/FluidBlock（1.20 类），改用 `BaseFlowingFluid` + `LiquidBlock`
- 流体 tag 命名用 `c:<name>`（如 `c:molten_iron`），不是 `c:fluid/<name>`

## 联动兼容测试方法
1. 将目标 mod 加入 `build.gradle`（`runtimeOnly`）
2. 启动客户端确认不崩溃
3. 测试万物皆可熔：目标 mod 矿石/锭能否被冶炼炉熔化（验证 `c:` Tag 互通）
4. 测试工具采掘：目标 mod 方块能否被匠魂工具正确采掘
5. 如有 Tag 不通：在 `src/main/resources/data/c/tags/` 下补充 Tag JSON
6. 如有崩溃：截取 crash report 关键段给 AI
7. 软依赖原则：缺失依赖时不能崩溃，用反射或 Optional
