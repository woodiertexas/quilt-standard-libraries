/*
 * Copyright 2021 The Quilt Project
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

package org.quiltmc.qsl.registry.attachment.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ConstantDefaultRegistryEntryAttachmentImpl<R, V> extends RegistryEntryAttachmentImpl<R, V> {
	private final @Nullable V defaultValue;

	public ConstantDefaultRegistryEntryAttachmentImpl(Registry<R> registry, Identifier id, Class<V> valueClass,
													  Codec<V> codec, PacketCodec<RegistryByteBuf, V> packetCodec, Side side, @Nullable V defaultValue) {
		super(registry, id, valueClass, codec, packetCodec, side);

		if (defaultValue != null) {
			CodecUtils.assertValid(this.codec(), defaultValue, "Default value");
		}

		this.defaultValue = defaultValue;
	}

	@Override
	protected @Nullable V getDefaultValue(R entry) {
		return this.defaultValue;
	}
}
