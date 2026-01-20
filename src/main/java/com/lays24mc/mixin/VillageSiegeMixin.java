package com.lays24mc.mixin;

import com.lays24mc.SiegeStopperFabric;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.VillageSiege;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(VillageSiege.class)
public class VillageSiegeMixin {
	// Tracks whether a siege has already been prevented to avoid duplicate logs
	private boolean siegePrevented = false;

	@Inject(
			method = "tick",
			at = @At("HEAD"),
			cancellable = true
	)
	private void cancelActiveSiege(ServerLevel serverLevel, boolean bl, CallbackInfo ci) {
		try {

			Field stateField = VillageSiege.class.getDeclaredField("siegeState");
			stateField.setAccessible(true);
			Object currentState = stateField.get(this);

			// Find the Enum constant corresponding to SIEGE_DONE
			Object siegeDone = null;
			Object[] constants = stateField.getType().getEnumConstants();
			for (Object c : constants) {
				if (c.toString().equals("SIEGE_DONE")) {
					siegeDone = c;
					break;
				}
			}

			// Fail-safe if Enum is not found
			if (siegeDone == null) return;

			// Only cancel the siege if it is currently active
			if (!currentState.equals(siegeDone)) {
				// Set the siege state to SIEGE_DONE to stop the siege
				stateField.set(this, siegeDone);

				// Log the cancellation only once per active siege
				if (!siegePrevented) {
					SiegeStopperFabric.LOGGER.info("[SiegeStopper] An active village siege was successfully prevented!");
					siegePrevented = true;
				}

				// Cancel the tick to prevent any zombies from spawning
				ci.cancel();
			} else {
				siegePrevented = false;
			}

		} catch (Exception e) {
			SiegeStopperFabric.LOGGER.error("[SiegeStopper] Failed to prevent village siege:", e);
		}
	}
}