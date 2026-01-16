package com.otherpatrick.ironspellsexpansion.mixin;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import org.spongepowered. asm.mixin. Mixin;
import org.spongepowered.asm. mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered. asm.mixin.injection. callback.CallbackInfo;

@Mixin(PokemonBattle.class)
public class PokemonBattleMixin {
    @Inject(method = "writeShowdownAction", at = @At("HEAD"))
    private void onWriteShowdownAction(String[] messages, CallbackInfo ci) {
        System.out.println("===== SHOWDOWN ACTION =====");
        for (String message : messages) {
            System.out.println(message);
        }
        System.out.println("===========================");
    }
}