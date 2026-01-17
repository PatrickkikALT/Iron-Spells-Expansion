package com.otherpatrick.ironspellsexpansion.mixin;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.ShowdownInterpreter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShowdownInterpreter.class)
public class ShowdownInterpreterMixin {
    @Inject(method="interpret", at = @At("HEAD"), remap = false)
    private static void onInterpret(PokemonBattle battle, String rawMessage, CallbackInfo info) {
        System.out.println("===== SHOWDOWN INCOMING =====");
        System.out. println(rawMessage);
        System.out.println("=============================");
    }
}
