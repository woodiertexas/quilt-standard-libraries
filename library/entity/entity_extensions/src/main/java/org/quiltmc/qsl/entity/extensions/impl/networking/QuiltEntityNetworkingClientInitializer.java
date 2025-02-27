/*
 * Copyright 2023 The Quilt Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.qsl.entity.extensions.impl.networking;

import com.mojang.logging.LogUtils;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.entity.extensions.api.networking.QuiltExtendedSpawnDataEntity;
import org.slf4j.Logger;

import net.minecraft.registry.Registries;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

@ApiStatus.Internal
public final class QuiltEntityNetworkingClientInitializer implements ClientModInitializer {
	private static final Logger logger = LogUtils.getLogger();

	@Override
	public void onInitializeClient(ModContainer mod) {
		ClientPlayNetworking.registerGlobalReceiver(
				ExtendedEntitySpawnPayload.ID,
				(client, handler, payload, sender) -> {
					client.execute(() -> {
						try {
							var entity = client.world.getEntityById(payload.entityId());
							if (entity instanceof QuiltExtendedSpawnDataEntity extended) {
								extended.readAdditionalSpawnData(payload.data());
							} else {
								var id = entity == null
										? "null"
										: Registries.ENTITY_TYPE.getId(entity.getType()).toString();
								logger.error(
										"[Quilt] invalid entity received for extended spawn packet: entity ["
										+ id + "] does not implement QuiltCustomSpawnDataEntity!"
								);
							}
						} finally { // make sure the buffer is released after
							payload.data().release();
						}
					});
				}
		);
	}
}
