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

package io.github.championash5357.paranoia.mixin;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.HEALTH;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import io.github.championash5357.paranoia.client.ClientReference;
import io.github.championash5357.paranoia.common.Paranoia;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.gui.ForgeIngameGui;

@Mixin(ForgeIngameGui.class)
public abstract class ForgeIngameGuiMixin extends IngameGui {

	private static final ResourceLocation SANITY_GUI_ICONS_LOCATION = new ResourceLocation(Paranoia.ID, "textures/gui/icons.png");
	
	public ForgeIngameGuiMixin(Minecraft mcIn) {
		super(mcIn);
	}

	@Shadow(remap = false)
	public static int left_height;
	
	@Shadow(remap = false)
	protected abstract void bind(ResourceLocation res);
	
	@Shadow(remap = false)
	protected abstract boolean pre(ElementType type, MatrixStack mStack);
	
	@Shadow(remap = false)
	protected abstract void post(ElementType type, MatrixStack mStack);
	
	public void renderHealth(int width, int height, MatrixStack mStack) {
		bind(GUI_ICONS_LOCATION);
        if (pre(HEALTH, mStack)) return;
        mc.getProfiler().startSection("health");
        RenderSystem.enableBlend();

        PlayerEntity player = (PlayerEntity)this.mc.getRenderViewEntity();
        int health = MathHelper.ceil(player.getHealth());
        boolean highlight = healthUpdateCounter > (long)ticks && (healthUpdateCounter - (long)ticks) / 3L %2L == 1L;

        if (health < this.playerHealth && player.hurtResistantTime > 0)
        {
            this.lastSystemTime = Util.milliTime();
            this.healthUpdateCounter = (long)(this.ticks + 20);
        }
        else if (health > this.playerHealth && player.hurtResistantTime > 0)
        {
            this.lastSystemTime = Util.milliTime();
            this.healthUpdateCounter = (long)(this.ticks + 10);
        }

        if (Util.milliTime() - this.lastSystemTime > 1000L)
        {
            this.playerHealth = health;
            this.lastPlayerHealth = health;
            this.lastSystemTime = Util.milliTime();
        }

        this.playerHealth = health;
        int healthLast = this.lastPlayerHealth;

        ModifiableAttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        float healthMax = (float)attrMaxHealth.getValue();
        float absorb = MathHelper.ceil(player.getAbsorptionAmount());

        int healthRows = MathHelper.ceil((healthMax + absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);

        this.rand.setSeed((long)(ticks * 312871));

        int left = width / 2 - 91;
        int top = height - left_height;
        left_height += (healthRows * rowHeight);
        if (rowHeight != 10) left_height += 10 - rowHeight;

        int regen = -1;
        if (player.isPotionActive(Effects.REGENERATION))
        {
            regen = ticks % 25;
        }

        final int TOP =  9 * (mc.world.getWorldInfo().isHardcore() ? 5 : 0);
        final int BACKGROUND = (highlight ? 25 : 16);
        int MARGIN = 16;
        boolean enable = false;
        if (ClientReference.getInstance().hasSanityOverlay()) enable = true;
        else if (player.isPotionActive(Effects.POISON)) MARGIN += 36;
        else if (player.isPotionActive(Effects.WITHER)) MARGIN += 72;
        float absorbRemaining = absorb;

        for (int i = ClientReference.getInstance().isMissingHealth() ? 2 : MathHelper.ceil((healthMax + absorb) / 2.0F) - 1; i >= 0; --i)
        {
            //int b0 = (highlight ? 1 : 0);
            int row = MathHelper.ceil((float)(i + 1) / 10.0F) - 1;
            int x = left + i % 10 * 8;
            int y = top - row * rowHeight;

            if (health <= 4) y += rand.nextInt(2);
            if (i == regen) y -= 2;

            blit(mStack, x, y, BACKGROUND, TOP, 9, 9);
            if (enable) bind(SANITY_GUI_ICONS_LOCATION);
            if (highlight)
            {
                if (i * 2 + 1 < healthLast)
                    blit(mStack, x, y, MARGIN + 54, TOP, 9, 9); //6
                else if (i * 2 + 1 == healthLast)
                    blit(mStack, x, y, MARGIN + 63, TOP, 9, 9); //7
            }

            if (absorbRemaining > 0.0F)
            {
                if (absorbRemaining == absorb && absorb % 2.0F == 1.0F)
                {
                    blit(mStack, x, y, MARGIN + 153, TOP, 9, 9); //17
                    absorbRemaining -= 1.0F;
                }
                else
                {
                    blit(mStack, x, y, MARGIN + 144, TOP, 9, 9); //16
                    absorbRemaining -= 2.0F;
                }
            }
            else
            {
                if (i * 2 + 1 < health)
                    blit(mStack, x, y, MARGIN + 36, TOP, 9, 9); //4
                else if (i * 2 + 1 == health)
                    blit(mStack, x, y, MARGIN + 45, TOP, 9, 9); //5
            }
            if (enable) bind(GUI_ICONS_LOCATION);
        }

        RenderSystem.disableBlend();
        mc.getProfiler().endSection();
        post(HEALTH, mStack);
	}
}
