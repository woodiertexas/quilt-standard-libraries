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

package org.quiltmc.qsl.networking.api.server;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.util.Identifier;

import org.quiltmc.qsl.base.api.event.Event;
import org.quiltmc.qsl.base.api.event.EventAwareListener;
import org.quiltmc.qsl.networking.api.LoginPacketSender;
import org.quiltmc.qsl.networking.api.PacketSender;

/**
 * Offers access to events related to the connection to a client on a logical server while a client is logging in.
 */
public final class ServerLoginConnectionEvents {
	/**
	 * Event indicating a connection entered the LOGIN state, ready for registering query response handlers.
	 *
	 * @see ServerLoginNetworking#registerReceiver(ServerLoginNetworkHandler, Identifier, ServerLoginNetworking.QueryResponseReceiver)
	 */
	public static final Event<Init> INIT = Event.create(Init.class, callbacks -> (handler, server) -> {
		for (Init callback : callbacks) {
			callback.onLoginInit(handler, server);
		}
	});

	/**
	 * An event for the start of login queries of the server login network handler.
	 * This event may be used to register {@link ServerLoginNetworking.QueryResponseReceiver login query response handlers}
	 * using {@link ServerLoginNetworking#registerReceiver(ServerLoginNetworkHandler, Identifier, ServerLoginNetworking.QueryResponseReceiver)}
	 * since this event is fired just before the first login query response is processed.
	 * <p>
	 * You may send login queries to the connected client using the provided {@link PacketSender}.
	 */
	public static final Event<QueryStart> QUERY_START = Event.create(QueryStart.class, callbacks -> (handler, server, sender, synchronizer) -> {
		for (QueryStart callback : callbacks) {
			callback.onLoginStart(handler, server, sender, synchronizer);
		}
	});

	/**
	 * An event for the disconnection of the server login network handler.
	 * <p>
	 * No packets should be sent when this event is invoked.
	 */
	public static final Event<Disconnect> DISCONNECT = Event.create(Disconnect.class, callbacks -> (handler, server) -> {
		for (Disconnect callback : callbacks) {
			callback.onLoginDisconnect(handler, server);
		}
	});

	private ServerLoginConnectionEvents() {
	}

	/**
	 * @see ServerLoginConnectionEvents#INIT
	 */
	@FunctionalInterface
	public interface Init extends EventAwareListener {
		void onLoginInit(ServerLoginNetworkHandler handler, MinecraftServer server);
	}

	/**
	 * @see ServerLoginConnectionEvents#QUERY_START
	 */
	@FunctionalInterface
	public interface QueryStart extends EventAwareListener {
		void onLoginStart(ServerLoginNetworkHandler handler, MinecraftServer server, LoginPacketSender sender, ServerLoginNetworking.LoginSynchronizer synchronizer);
	}

	/**
	 * @see ServerLoginConnectionEvents#DISCONNECT
	 */
	@FunctionalInterface
	public interface Disconnect extends EventAwareListener {
		void onLoginDisconnect(ServerLoginNetworkHandler handler, MinecraftServer server);
	}
}
