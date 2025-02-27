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

package org.quiltmc.qsl.entity.extensions.mixin.networking;

import org.quiltmc.qsl.entity.extensions.api.networking.QuiltExtendedSpawnDataEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.world.World;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Shadow
	public abstract World getWorld();

	@Inject(method = "createSpawnPacket", at = @At("RETURN"), cancellable = true)
	private void quilt$createExtendedSpawnPacket(EntityTrackerEntry entry, CallbackInfoReturnable<Packet<ClientPlayPacketListener>> cir) {
		if (this instanceof QuiltExtendedSpawnDataEntity extended) {
			Packet<ClientPlayPacketListener> basePacket = cir.getReturnValue();
			Packet<ClientPlayPacketListener> extendedPacket = QuiltExtendedSpawnDataEntity.createExtendedPacket(extended, basePacket, this.getWorld().getRegistryManager());
			cir.setReturnValue(extendedPacket);
		}
	}
}
