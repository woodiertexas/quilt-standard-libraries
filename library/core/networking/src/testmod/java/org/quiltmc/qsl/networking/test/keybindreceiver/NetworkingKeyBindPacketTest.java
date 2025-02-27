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

package org.quiltmc.qsl.networking.test.keybindreceiver;

import net.minecraft.network.packet.payload.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.server.ServerPlayConnectionEvents;
import org.quiltmc.qsl.networking.api.server.ServerPlayNetworking;
import org.quiltmc.qsl.networking.test.NetworkingTestMods;

// Listens for a packet from the client which is sent to the server when a keybinding is pressed.
// In response the server will send a message containing the keybind text letting the client know it pressed that key.
public final class NetworkingKeyBindPacketTest implements ModInitializer {
	public static final CustomPayload.Id<CustomPayload> KEYBINDING_PACKET_ID = NetworkingTestMods.id("keybind_press_test");

	private static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, CustomPayload buf, PacketSender<CustomPayload> responseSender) {
		// TODO: Can we send chat off the server thread?
		server.execute(() -> {
			player.sendMessage(Text.literal("You pressed ").append(Text.keyBind("quilt_networking_keybind_testmod").formatted(Formatting.BLUE)), false);
		});
	}

	@Override
	public void onInitialize(ModContainer mod) {
		ServerPlayConnectionEvents.INIT.register((handler, server) -> {
			ServerPlayNetworking.registerReceiver(handler, KEYBINDING_PACKET_ID, NetworkingKeyBindPacketTest::receive);
		});
	}
}
