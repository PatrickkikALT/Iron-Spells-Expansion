package com.otherpatrick.ironspellsexpansion.spells;

import com.otherpatrick.ironspellsexpansion.IronSpellsExpansion;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RepairSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.tryBuild(IronSpellsExpansion.MODID, "repair_spell");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.ironspellsexpansion.repair_spell", Utils.stringTruncation(getSpellPower(spellLevel, caster), 1))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(2)
            .setCooldownSeconds(20)
            .build();

    public RepairSpell() {
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 1;
        this.baseManaCost = 5;
        this.castTime = 0;
        this.spellPowerPerLevel = 1;
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

    public ItemStack checkForItem(Item item, Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.is(item)) {
                return stack;
            }
        }
        return null;
    }
    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        AABB box = entity.getBoundingBox().inflate(getSpellPower(spellLevel, entity));
        Random random = new Random();
        if (entity instanceof Player player) {
            BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
            BlockPos max = BlockPos.containing(box.maxX, box.maxY, box.maxZ);
            for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
                BlockState state = level.getBlockState(pos);
                if (state.is(Blocks.DAMAGED_ANVIL)) {
                    ItemStack stack = checkForItem(Items.IRON_INGOT, player);
                    if (stack != null) {
                        stack.shrink(1);
                        state.getBlock().destroy(level, pos, state);
                        Block block = spellLevel == 2 ? random.nextInt(2) == 0 ? Blocks.CHIPPED_ANVIL : Blocks.ANVIL : Blocks.CHIPPED_ANVIL;
                        level.setBlockAndUpdate(pos, block.defaultBlockState());
                    }
                    else {
                        player.displayClientMessage(Component.literal("You need an Iron Ingot to cast!"), true);
                    }
                    return;
                }
                else if (state.is(Blocks.CHIPPED_ANVIL)) {
                    ItemStack stack = checkForItem(Items.IRON_INGOT, player);
                    if (stack != null) {
                        stack.shrink(1);
                        state.getBlock().destroy(level, pos, state);
                        level.setBlockAndUpdate(pos, Blocks.ANVIL.defaultBlockState());
                    }
                    else {
                        player.displayClientMessage(Component.literal("You need an Iron Ingot to cast!"), true);
                    }
                    return;
                }
            }
            player.displayClientMessage(Component.literal("You're not nearby an anvil!"), true);
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);

    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.THROW_SINGLE_ITEM;
    }
}

