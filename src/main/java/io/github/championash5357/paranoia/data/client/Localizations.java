/*
 * Paranoia
 * Copyright (C) 2020 ChampionAsh5357
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation version 3.0 of the License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.championash5357.paranoia.data.client;

import io.github.championash5357.paranoia.api.util.DamageSources;
import io.github.championash5357.paranoia.common.Paranoia;
import io.github.championash5357.paranoia.common.util.LocalizationStrings;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.data.LanguageProvider;

public class Localizations extends LanguageProvider {

	public Localizations(DataGenerator gen, String locale) {
		super(gen, Paranoia.ID, locale);
	}

	@Override
	protected void addTranslations() {
		String locale = this.getName().replace("Languages: ", "");
		switch(locale) {
		case "en_us":
			add(LocalizationStrings.COMMAND_SANITY_SANITY, "Sanity");
			add(LocalizationStrings.COMMAND_SANITY_MAX_SANITY, "Max Sanity");
			add(DamageSources.PARANOIA, "%1$s went insane", "%1$s was too paranoid of %2$s");
			break;
		default:
			break;
		}
	}
	
	protected void add(DamageSource source, String regular, String player) {
		add("death.attack."  + source.damageType, regular);
		add("death.attack."  + source.damageType + ".player", player);
	}
}
