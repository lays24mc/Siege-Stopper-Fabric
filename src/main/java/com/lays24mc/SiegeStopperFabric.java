package com.lays24mc;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiegeStopperFabric implements ModInitializer {
	public static final String MOD_ID = "siege-stopper-fabric";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("[SiegeStopper] Fabric mod loaded – village sieges are disabled.");
	}
}