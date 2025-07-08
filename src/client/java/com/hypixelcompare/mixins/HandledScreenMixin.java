package com.hypixelcompare.mixins;

import com.hypixelcompare.ItemComparator;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {
    
    @Inject(method = "onMouseClick", at = @At("HEAD"), cancellable = true)
    private void onMouseClick(Slot slot, int slotId, int button, CallbackInfoReturnable<Boolean> cir) {
        if (ItemComparator.isComparisonMode() && slot != null && slot.hasStack()) {
            ItemComparator.selectItem(slot.getStack());
            cir.setReturnValue(true);
        }
    }
}