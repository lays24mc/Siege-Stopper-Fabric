package com.lays24mc.mixin;

import com.lays24mc.SiegeStopperFabric;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.village.VillageSiege;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VillageSiege.class)
public class VillageSiegeMixin {

	/**
	 * Intercepts vanilla's nightly 1-in-10 roll that decides whether a siege starts tonight.
	 * We only log/prevent when that roll actually hits, so the message fires exactly when a
	 * siege would otherwise have happened - not on every night cycle.
	 */
	@Redirect(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"
			)
	)
	private int preventSiegeRoll(RandomSource random, int bound) {
		int roll = random.nextInt(bound);
		if (roll == 0) {
			SiegeStopperFabric.LOGGER.info("[SiegeStopper] An active village siege was successfully prevented!");
			// Force a non-zero result so vanilla resolves the siege state to SIEGE_DONE instead of starting one.
			return 1;
		}
		return roll;
	}
}
