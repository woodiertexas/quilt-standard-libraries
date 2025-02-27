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

package org.quiltmc.qsl.resource.loader.test.client;

import java.util.Optional;
import java.util.Random;

import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.texture.NativeImage;

import net.minecraft.SharedConstants;
import net.minecraft.resource.PackPosition;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.pack.PackLocationInfo;
import net.minecraft.resource.pack.PackProfile;
import net.minecraft.resource.pack.PackSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.resource.loader.api.InMemoryPack;
import org.quiltmc.qsl.resource.loader.api.QuiltPackProfile;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;

public class ResourcePackProfileProviderTestMod implements ClientModInitializer {
	private static final String PACK_NAME = "Visible Test Virtual Pack";

	@Override
	public void onInitializeClient(ModContainer mod) {
		ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerPackProfileProvider((profileAdder) -> {
			var pack = new TestPack();
			profileAdder.accept(PackProfile.of(
					pack.getLocationInfo(),
					QuiltPackProfile.wrapToFactory(pack),
					ResourceType.CLIENT_RESOURCES,
					new PackPosition(
						true,
						PackProfile.InsertionPosition.TOP,
						true
					)));
		});
	}

	static class TestPack extends InMemoryPack {
		private static final Identifier DIRT_IDENTIFIER = Identifier.ofDefault("textures/block/dirt.png");
		private final Random random = new Random();

		TestPack() {
			this.putText("pack.mcmeta", String.format("""
							{"pack":{"pack_format":%d,"description":"Just testing."}}
							""",
					SharedConstants.getGameVersion().getResourceVersion(ResourceType.CLIENT_RESOURCES)));
			this.putImage("pack.png", this::createRandomImage);
			this.putImage(DIRT_IDENTIFIER, this::createRandomImage);
		}

		private NativeImage createRandomImage() {
			var image = new NativeImage(16, 16, true);

			boolean t = this.random.nextBoolean();
			for (int y = 0; y < 16; y++) {
				int color = 0xff << 24;
				color |= this.random.nextInt(256) << 16;
				color |= this.random.nextInt(256) << 8;
				color |= this.random.nextInt(256);
				for (int x = 0; x < 16; x++) {
					image.setPixelColor(t ? x : y, t ? y : x, color);
				}
			}

			return image;
		}

		@Override
		public PackLocationInfo getLocationInfo() {
			return new PackLocationInfo(
				PACK_NAME,
				this.getDisplayName(),
				PackSource.PACK_SOURCE_BUILTIN,
				Optional.empty()
			);
		}

		@Override
		public String getName() {
			return PACK_NAME;
		}

		@Override
		public @NotNull Text getDisplayName() {
			return Text.of(this.getName());
		}
	}
}
