package com.otherpatrick.ironspellsexpansion.spells;

import com.otherpatrick.ironspellsexpansion.IronSpellsExpansion;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

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
        return Optional.of(SoundType.METAL.getPlaceSound());
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
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        var blockHitResult = Utils.getTargetBlock(level, entity, ClipContext.Fluid.NONE, 8);
        if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            if (entity instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("ui.irons_spellbooks.cast_error_target_block").withStyle(ChatFormatting.RED)));
            }
            return false;
        }
        return true;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        var blockhit = Utils.getTargetBlock(level, entity, ClipContext.Fluid.NONE, 8);
        var pos = blockhit.getBlockPos();
        var state = level.getBlockState(pos);
        var random = new Random();
        if (state.is(Blocks.DAMAGED_ANVIL)) {
            Player player = (Player)entity;
            ItemStack stack = checkForItem(Items.IRON_INGOT, player);
            if (stack != null) {
                stack.shrink(1);

                state.getBlock().destroy(level, pos, state);
                Block block = spellLevel == 2 ? random.nextInt(2) == 0 ? Blocks.CHIPPED_ANVIL : Blocks.ANVIL : Blocks.CHIPPED_ANVIL;
                level.setBlockAndUpdate(pos, block.defaultBlockState());
            }
            else {
                player.displayClientMessage(Component.literal("You need to have an iron ingot to use this spell!"), false);
            }
        } else if (state.is(Blocks.CHIPPED_ANVIL)) {
            Player player = (Player)entity;
            ItemStack stack = checkForItem(Items.IRON_INGOT, player);
            if (stack != null) {
                stack.shrink(1);
                state.getBlock().destroy(level, pos, state);
                level.setBlockAndUpdate(pos, Blocks.ANVIL.defaultBlockState());
            }
        }
        else {
            ((Player)entity).displayClientMessage(Component.literal("You need to target an anvil!"), false);
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);

    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.THROW_SINGLE_ITEM;
    }
}

