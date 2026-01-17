package com.otherpatrick.ironspellsexpansion.spells;

import com.otherpatrick.ironspellsexpansion.IronSpellsExpansion;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class GrowSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.tryBuild(IronSpellsExpansion.MODID, "grow_spell");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.ironspellsexpansion.grow_spell", Utils.stringTruncation(getSpellPower(spellLevel, caster), 1))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(SchoolRegistry.NATURE_RESOURCE)
            .setMaxLevel(6)
            .setCooldownSeconds(5)
            .build();

    public GrowSpell() {
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 2;
        this.baseManaCost = 5;
        this.castTime = 2 * 20;
        this.spellPowerPerLevel = 4;
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
        return CastType.LONG;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.NATURE_CAST.get());
    }


    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundType.CROP.getPlaceSound());
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        AABB box = entity.getBoundingBox().inflate(getSpellPower(spellLevel, entity));
        ServerLevel serverLevel = (ServerLevel)level;
        if (entity instanceof Player player) {
            BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
            BlockPos max = BlockPos.containing(box.maxX, box.maxY, box.maxZ);
            for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
                BlockState state = level.getBlockState(pos);
                if (state.getBlock() instanceof CropBlock crop) {
                    if (!crop.isMaxAge(state)) {
                        crop.growCrops(level, pos, state);
                        serverLevel.sendParticles(
                                ParticleTypes.HAPPY_VILLAGER,
                                pos.getX(),
                                pos.getY(),
                                pos.getZ(),
                                12,
                                0, 0.5, 0,
                                0.06);
                    }
                }
            }
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);

    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.CHARGE_ANIMATION;
    }
}
