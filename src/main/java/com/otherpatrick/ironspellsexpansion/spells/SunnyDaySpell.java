package com.otherpatrick.ironspellsexpansion.spells;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.otherpatrick.ironspellsexpansion.IronSpellsExpansion;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class SunnyDaySpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.tryBuild(IronSpellsExpansion.MODID, "sunny_spell");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.ironspellsexpansion.sunny_spell", Utils.stringTruncation(getSpellPower(spellLevel, caster), 1))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.NATURE_RESOURCE)
            .setMaxLevel(10)
            .setCooldownSeconds(20)
            .build();

    public SunnyDaySpell() {
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 10;
        this.baseManaCost = 5;
        this.castTime = 0;
        this.spellPowerPerLevel = 5;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    private double computeLaunchVelocity(int spellLevel, Entity caster) {
        double power = getSpellPower(spellLevel, caster);
        return 1.0 + (power / 60.0);
    }


    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        PokemonBattle battle = BattleRegistry.getBattleByParticipatingPlayer(player);
        if (battle == null) {
            player.displayClientMessage(Component.literal("You need to be in a Pokémon Battle to use this!"), true);
            return;
        }

        battle.writeShowdownAction(">weather sunnyday");

    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.SELF_CAST_ANIMATION;
    }
}
