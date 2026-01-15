package com.otherpatrick.ironspellsexpansion.spells;

import com.otherpatrick.ironspellsexpansion.IronSpellsExpansion;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public class SmeltSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.tryBuild(IronSpellsExpansion.MODID, "smelt_spell");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.ironspellsexpansion.smelt_spell", Utils.stringTruncation(getSpellPower(spellLevel, caster), 1))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
            .setMaxLevel(4)
            .setCooldownSeconds(5)
            .build();

    public SmeltSpell() {
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 2;
        this.baseManaCost = 5;
        this.castTime = 0;
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
        return CastType.INSTANT;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.FIRE_CAST.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
//        if (entity instanceof Player player) {
//            player.setDeltaMovement(
//                    player.getDeltaMovement().x,
//                    computeLaunchVelocity(spellLevel, entity),
//                    player.getDeltaMovement().z
//            );
//            player.hurtMarked = true;
//            player.fallDistance = 0;
//        }
//        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
        List<Entity> entities = level.getEntities(entity, entity.getBoundingBox().inflate(spellLevel), e ->
                e instanceof LivingEntity || e instanceof ItemEntity
        );
        ServerLevel serverLevel = (ServerLevel)level;
        for (Entity target : entities) {
            if (target instanceof LivingEntity living) {
                serverLevel.sendParticles(
                        ParticleHelper.FIRE_EMITTER,
                        target.getX(),
                        target.getY(),
                        target.getZ(),
                        32,
                        0.3, 0.5, 0.3,
                        0.06);
                living.setRemainingFireTicks(20 * 3);
            }
            else if (target instanceof ItemEntity item) {
                ItemStack stack = item.getItem();
                SingleRecipeInput container = new SingleRecipeInput(stack);
                Optional<RecipeHolder<SmeltingRecipe>> recipe =
                        level.getRecipeManager()
                                .getRecipeFor(RecipeType.SMELTING, container, level);
                if (recipe.isPresent()) {
                    serverLevel.sendParticles(
                            ParticleHelper.FIRE_EMITTER,
                            item.getX(),
                            item.getY(),
                            item.getZ(),
                            32,
                            0.3, 0.5, 0.3,
                            0.06);
                    ItemStack result = recipe.get().value().assemble(container, level.registryAccess());
                    result.setCount(stack.getCount());
                    item.setItem(result);
                }
            }
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);

    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.SELF_CAST_ANIMATION;
    }
}
