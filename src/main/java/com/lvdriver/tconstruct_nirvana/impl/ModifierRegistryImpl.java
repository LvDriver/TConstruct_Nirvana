package com.lvdriver.tconstruct_nirvana.impl;

import com.lvdriver.tconstruct_nirvana.api.registry.ModifierRegistry;
import com.lvdriver.tconstruct_nirvana.modifier.Modifier;
import com.lvdriver.tconstruct_nirvana.modifier.Modifiers;

import java.util.Collection;

/**
 * {@link ModifierRegistry} 实现：委托 {@link Modifiers} 静态注册表。
 * 附属 mod 不应直接引用本类（实现细节，可能变更），请经
 * {@code TConstructNirvanaAPI.modifiers()} 获取。
 */
public final class ModifierRegistryImpl implements ModifierRegistry {

    @Override
    public void register(Modifier modifier) {
        Modifiers.register(modifier);
    }

    @Override
    public Modifier get(String identifier) {
        return Modifiers.get(identifier);
    }

    @Override
    public Collection<Modifier> getAll() {
        return Modifiers.getAll();
    }
}
