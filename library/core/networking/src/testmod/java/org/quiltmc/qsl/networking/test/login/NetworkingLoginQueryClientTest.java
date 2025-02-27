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

package org.quiltmc.qsl.networking.test.login;

import java.util.concurrent.CompletableFuture;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientLoginConnectionEvents;
import org.quiltmc.qsl.networking.api.client.ClientLoginNetworking;
import org.quiltmc.qsl.networking.test.NetworkingTestMods;

@ClientOnly
public final class NetworkingLoginQueryClientTest implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		// Send a dummy response to the server in return, by registering here we essentially say we understood the server's query
		ClientLoginNetworking.registerGlobalReceiver(NetworkingLoginQueryTest.TEST_CHANNEL_GLOBAL, (client, handler, buf, listenerAdder) -> {
			NetworkingTestMods.LOGGER.info("Received TEST_CHANNEL_GLOBAL on the client.");
			return CompletableFuture.completedFuture(PacketByteBufs.empty());
		});

		ClientLoginConnectionEvents.QUERY_START.register((handler, client) -> {
			ClientLoginNetworking.registerReceiver(NetworkingLoginQueryTest.TEST_CHANNEL, (_client, _handler, buf, listenerAdder) -> {
				NetworkingTestMods.LOGGER.info("Received TEST_CHANNEL on the client.");
				return CompletableFuture.completedFuture(PacketByteBufs.empty());
			});
		});
	}
}
