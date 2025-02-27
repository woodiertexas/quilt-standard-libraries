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

package org.quiltmc.qsl.resource.loader.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.widget.list.pack.PackEntryListWidget;

import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
@Mixin(PackScreen.class)
public interface PackScreenAccessor {
	@Accessor("availablePackList")
	PackEntryListWidget getAvailablePackList();

	@Accessor("selectedPackList")
	PackEntryListWidget getSelectedPackList();
}
