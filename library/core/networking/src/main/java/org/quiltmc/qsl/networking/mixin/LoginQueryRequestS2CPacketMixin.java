/*
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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.payload.CustomQueryPayload;
import net.minecraft.util.Identifier;

import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.impl.payload.PacketByteBufLoginQueryRequestPayload;

@Mixin(LoginQueryRequestS2CPacket.class)
public class LoginQueryRequestS2CPacketMixin {
	@Inject(method = "readPayload", at = @At("HEAD"), cancellable = true)
	private static void read(Identifier id, PacketByteBuf buf, CallbackInfoReturnable<CustomQueryPayload> cir) {
		PacketByteBuf newBuf = PacketByteBufs.read(buf);
		cir.setReturnValue(new PacketByteBufLoginQueryRequestPayload(id, newBuf));
	}
}
