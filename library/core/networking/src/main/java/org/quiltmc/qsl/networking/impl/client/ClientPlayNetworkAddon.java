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

package org.quiltmc.qsl.networking.impl.client;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.NetworkPhase;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.payload.CustomPayload;

import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.networking.api.client.C2SPlayChannelEvents;
import org.quiltmc.qsl.networking.api.client.ClientPlayConnectionEvents;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;
import org.quiltmc.qsl.networking.impl.AbstractChanneledNetworkAddon;
import org.quiltmc.qsl.networking.impl.ChannelInfoHolder;
import org.quiltmc.qsl.networking.impl.NetworkingImpl;
import org.quiltmc.qsl.networking.impl.payload.ChannelPayload;

@ApiStatus.Internal
@ClientOnly
public final class ClientPlayNetworkAddon extends AbstractChanneledNetworkAddon<ClientPlayNetworking.CustomChannelReceiver<?>> {
	private final ClientPlayNetworkHandler handler;
	private final MinecraftClient client;
	private boolean sentInitialRegisterPacket;

	public ClientPlayNetworkAddon(ClientPlayNetworkHandler handler, MinecraftClient client) {
		super(ClientNetworkingImpl.PLAY, handler.getConnection(), "ClientPlayNetworkAddon for " + handler.getProfile().getName());
		this.handler = handler;
		this.client = client;

		// Must register pending channels via lateinit
		this.registerPendingChannels((ChannelInfoHolder) this.connection, NetworkPhase.PLAY);

		// Register global receivers and attach to session
		this.receiver.startSession(this);
	}

	@Override
	public void lateInit() {
		for (Map.Entry<CustomPayload.Id<?>, ClientPlayNetworking.CustomChannelReceiver<?>> entry : this.receiver.getReceivers().entrySet()) {
			this.registerChannel(entry.getKey(), entry.getValue());
		}

		ClientPlayConnectionEvents.INIT.invoker().onPlayInit(this.handler, this.client);
	}

	public void onServerReady() {
		ClientPlayConnectionEvents.JOIN.invoker().onPlayReady(this.handler, this, this.client);

		// The client cannot send any packets, including `minecraft:register` until after GameJoinS2CPacket is received.
		this.sendInitialChannelRegistrationPacket();
		this.sentInitialRegisterPacket = true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T extends CustomPayload> void receive(ClientPlayNetworking.CustomChannelReceiver<?> handler, T buf) {
		((ClientPlayNetworking.CustomChannelReceiver<T>) handler).receive(this.client, this.handler, buf, this);
	}

	// impl details

	@Override
	protected void schedule(Runnable task) {
		MinecraftClient.getInstance().execute(task);
	}

	@Override
	public Packet<?> createPacket(CustomPayload payload) {
		return ClientNetworkingImpl.createC2SPacket(payload);
	}

	@Override
	protected void invokeRegisterEvent(List<CustomPayload.Id<?>> ids) {
		C2SPlayChannelEvents.REGISTER.invoker().onChannelRegister(this.handler, this, this.client, ids);
	}

	@Override
	protected void invokeUnregisterEvent(List<CustomPayload.Id<?>> ids) {
		C2SPlayChannelEvents.UNREGISTER.invoker().onChannelUnregister(this.handler, this, this.client, ids);
	}

	@Override
	protected void handleRegistration(CustomPayload.Id<?> channelName) {
		// If we can already send packets, immediately send the register packet for this channel
		if (this.sentInitialRegisterPacket) {
			final ChannelPayload payload = this.createRegistrationPacket(List.of(channelName), true);

			if (payload != null) {
				this.sendPacket(new CustomPayloadC2SPacket(payload));
			}
		}
	}

	@Override
	protected void handleUnregistration(CustomPayload.Id<?> channelName) {
		// If we can already send packets, immediately send the unregister packet for this channel
		if (this.sentInitialRegisterPacket) {
			final ChannelPayload payload = this.createRegistrationPacket(List.of(channelName), true);

			if (payload != null) {
				this.sendPacket(new CustomPayloadC2SPacket(payload));
			}
		}
	}

	@Override
	protected void invokeDisconnectEvent() {
		ClientPlayConnectionEvents.DISCONNECT.invoker().onPlayDisconnect(this.handler, this.client);
		this.receiver.endSession(this);
	}

	@Override
	protected boolean isReservedChannel(CustomPayload.Id<?> channelName) {
		return NetworkingImpl.isReservedCommonChannel(channelName);
	}
}
