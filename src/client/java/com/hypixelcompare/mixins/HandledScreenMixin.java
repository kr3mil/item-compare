package com.hypixelcompare.mixins;

import com.hypixelcompare.HypixelCompare;
import com.hypixelcompare.ItemComparator;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {
    
    @Inject(method = "keyPressed(III)Z", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (keyCode == GLFW.GLFW_KEY_M) {
            HypixelCompare.LOGGER.info("M key pressed in inventory! Screen: " + 
                this.getClass().getSimpleName());
            ItemComparator.selectHoveredItem();
            cir.setReturnValue(true); // Consume the event
        }
    }
}