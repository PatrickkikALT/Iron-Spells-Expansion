package com.otherpatrick.ironspellsexpansion;

import com.otherpatrick.ironspellsexpansion.spells.*;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CustomSpellRegistry {
    public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(io.redspace.ironsspellbooks.api.registry.SpellRegistry.SPELL_REGISTRY_KEY, IronSpellsExpansion.MODID);

    public static void register(IEventBus eventBus) {
        SPELLS.register(eventBus);
    }

    public static DeferredHolder<AbstractSpell, AbstractSpell> registerSpell(AbstractSpell spell) {
        return SPELLS.register(spell.getSpellName(), () -> spell);
    }

    public static final DeferredHolder<AbstractSpell, AbstractSpell> LAUNCH_SPELL = registerSpell(new LaunchSpell());
    public static final DeferredHolder<AbstractSpell, AbstractSpell> COW_SPELL = registerSpell(new SummonCowSpell());
    public static final DeferredHolder<AbstractSpell, AbstractSpell> SMELT_SPELL = registerSpell(new SmeltSpell());
    public static final DeferredHolder<AbstractSpell, AbstractSpell> REPAIR_SPELL = registerSpell(new RepairSpell());
    public static final DeferredHolder<AbstractSpell, AbstractSpell> SCALE_SPELL = registerSpell(new ShrinkSpell());
    public static final DeferredHolder<AbstractSpell, AbstractSpell> COOKIE_SPELL = registerSpell(new CookieSpell());
    public static final DeferredHolder<AbstractSpell, AbstractSpell> POKE_HEAL_SPELL = registerSpell(new PokeHealSpell());

}
