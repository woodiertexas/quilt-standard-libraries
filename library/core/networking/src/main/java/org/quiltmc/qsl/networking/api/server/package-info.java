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

/**
 * <h2>The Networking API.</h2>
 * <p>
 * For login stage networking see {@link org.quiltmc.qsl.networking.api.server.ServerLoginNetworking}.
 * For configuration stage networking see {@link org.quiltmc.qsl.networking.api.server.ServerConfigurationNetworking}.
 * For play stage networking see {@link org.quiltmc.qsl.networking.api.server.ServerPlayNetworking}.
 * <p>
 * For events related to the connection to a client see:
 * <ul>
 * <li>{@link org.quiltmc.qsl.networking.api.server.ServerLoginConnectionEvents} for login stage. </li>
 * <li>{@link org.quiltmc.qsl.networking.api.server.ServerConfigurationConnectionEvents} for configuration stage. </li>
 * <li>{@link org.quiltmc.qsl.networking.api.server.ServerPlayConnectionEvents} for play stage. </li>
 * </ul>
 * <p>
 * For events related to the ability of a client to receive packets on a channel of a specific name see {@link org.quiltmc.qsl.networking.api.server.S2CPlayChannelEvents} or {@link org.quiltmc.qsl.networking.api.server.S2CConfigurationChannelEvents}.
 */

package org.quiltmc.qsl.networking.api.server;
