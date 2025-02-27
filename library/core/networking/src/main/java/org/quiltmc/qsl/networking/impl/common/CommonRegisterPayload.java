/*
 * Copyright 2016, 2017, 2018, 2019 FabricMC
 * Copyright 2024 The Quilt Project
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

package org.quiltmc.qsl.networking.impl.common;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.payload.CustomPayload;
import net.minecraft.util.Identifier;

public record CommonRegisterPayload(int version, String phase, Set<Id<?>> channels) implements CustomPayload {
	public static final PacketCodec<PacketByteBuf, CommonRegisterPayload> CODEC = CustomPayload.create(CommonRegisterPayload::write, CommonRegisterPayload::new);
	public static final Id<CommonRegisterPayload> PACKET_ID = new Id<>(Identifier.of("c", "register"));

	public static final String PLAY_PHASE = "play";
	public static final String CONFIGURATION_PHASE = "configuration";

	public CommonRegisterPayload(PacketByteBuf buf) {
		this(
				buf.readVarInt(),
				buf.readString(),
				buf.readCollection(HashSet::new, PacketByteBuf::readIdentifier).stream().map(Id::new).collect(Collectors.toSet())
		);
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.version);
		buf.writeString(this.phase);
		buf.writeCollection(this.channels.stream().map(Id::id).collect(Collectors.toSet()), PacketByteBuf::writeIdentifier);
	}

	@Override
	public Id<CommonRegisterPayload> getId() {
		return PACKET_ID;
	}
}
