/*
 * Copyright 2022 The Quilt Project
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

package org.quiltmc.qsl.networking.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.ConnectedClientData;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.listener.AbstractServerPacketHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;

import org.quiltmc.qsl.networking.impl.DisconnectPacketSource;
import org.quiltmc.qsl.networking.impl.NetworkHandlerExtensions;
import org.quiltmc.qsl.networking.impl.server.ServerPlayNetworkAddon;

// We want to apply a bit earlier than other mods which may not use us in order to prevent refCount issues
@Mixin(value = ServerPlayNetworkHandler.class, priority = 999)
abstract class ServerPlayNetworkHandlerMixin extends AbstractServerPacketHandler implements NetworkHandlerExtensions, DisconnectPacketSource {
	@Unique
	private ServerPlayNetworkAddon addon;

	ServerPlayNetworkHandlerMixin(MinecraftServer server, ClientConnection connection, ConnectedClientData connectedClientData) {
		super(server, connection, connectedClientData);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void initAddon(CallbackInfo ci) {
		this.addon = new ServerPlayNetworkAddon((ServerPlayNetworkHandler) (Object) this, this.server);
		// A bit of a hack but it allows the field above to be set in case someone registers handlers during INIT event which refers to said field
		this.addon.lateInit();
	}

	@Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
	private void handleCustomPayloadReceivedAsync(CustomPayloadC2SPacket packet, CallbackInfo ci) {
		if (this.getAddon().handle(packet.payload())) {
			ci.cancel();
		}
	}

	@Inject(method = "onDisconnected", at = @At("HEAD"))
	private void handleDisconnection(DisconnectionDetails details, CallbackInfo ci) {
		this.addon.handleDisconnect();
	}

	@Override
	public ServerPlayNetworkAddon getAddon() {
		return this.addon;
	}

	@Override
	public Packet<?> createDisconnectPacket(Text message) {
		return new DisconnectS2CPacket(message);
	}
}
