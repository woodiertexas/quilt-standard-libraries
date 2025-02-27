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

package org.quiltmc.qsl.networking.mixin;

import java.util.List;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.payload.CustomPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;

import org.quiltmc.qsl.networking.impl.QuiltCustomPayloadPacketCodec;
import org.quiltmc.qsl.networking.impl.PayloadTypeRegistryImpl;

@Mixin(CustomPayloadS2CPacket.class)
public class CustomPayloadS2CPacketMixin {
	@WrapOperation(
			method = "<clinit>",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/network/packet/payload/CustomPayload;create(Lnet/minecraft/network/packet/payload/CustomPayload$CodecFactory;Ljava/util/List;)Lnet/minecraft/network/codec/PacketCodec;",
				ordinal = 0
			)
	)
	private static PacketCodec<RegistryByteBuf, CustomPayload> wrapPlayCodec(CustomPayload.CodecFactory<RegistryByteBuf> unknownCodecFactory, List<CustomPayload.Type<RegistryByteBuf, ?>> types, Operation<PacketCodec<RegistryByteBuf, CustomPayload>> original) {
		PacketCodec<RegistryByteBuf, CustomPayload> codec = original.call(unknownCodecFactory, types);
		QuiltCustomPayloadPacketCodec<RegistryByteBuf> fabricCodec = (QuiltCustomPayloadPacketCodec<RegistryByteBuf>) codec;
		fabricCodec.setPacketCodecProvider((packetByteBuf, identifier) -> PayloadTypeRegistryImpl.PLAY_S2C.get(identifier));
		return codec;
	}

	@WrapOperation(
			method = "<clinit>",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/network/packet/payload/CustomPayload;create(Lnet/minecraft/network/packet/payload/CustomPayload$CodecFactory;Ljava/util/List;)Lnet/minecraft/network/codec/PacketCodec;",
				ordinal = 1
			)
	)
	private static PacketCodec<PacketByteBuf, CustomPayload> wrapConfigCodec(CustomPayload.CodecFactory<PacketByteBuf> unknownCodecFactory, List<CustomPayload.Type<PacketByteBuf, ?>> types, Operation<PacketCodec<PacketByteBuf, CustomPayload>> original) {
		PacketCodec<PacketByteBuf, CustomPayload> codec = original.call(unknownCodecFactory, types);
		QuiltCustomPayloadPacketCodec<PacketByteBuf> fabricCodec = (QuiltCustomPayloadPacketCodec<PacketByteBuf>) codec;
		fabricCodec.setPacketCodecProvider((packetByteBuf, identifier) -> PayloadTypeRegistryImpl.CONFIGURATION_S2C.get(identifier));
		return codec;
	}
}
