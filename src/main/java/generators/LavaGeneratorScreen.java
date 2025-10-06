/*package Generators;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class LavaGeneratorScreen extends AbstractContainerScreen<LavaGeneratorMenu> {
    // Pekar på din PNG i resources
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("generators", "textures/gui/lava_generator.png");

    public LavaGeneratorScreen(LavaGeneratorMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = 176; // GUI bredd
        this.imageHeight = 166; // GUI höjd
        this.inventoryLabelY = this.imageHeight - 94; // Adjust inventory label position
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // Rita bakgrund (hela PNG:n)
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // Rita lava-bar (exempel: vertikal fyllning)
        int lavaAmount = this.menu.getLavaAmount();
        int maxLava = this.menu.getMaxLava();

        if (maxLava > 0 && lavaAmount > 0) {
            int barHeight = 50; // höjden på baren i din texture
            int filled = (lavaAmount * barHeight) / maxLava;

            // Här antar jag att din lava-bar sitter på (x+150, y+20)
            // Justera siffrorna efter var i din PNG den är placerad
            guiGraphics.fill(x + 150, y + 20 + (barHeight - filled),
                    x + 150 + 16, y + 20 + barHeight,
                    0xFFFF0000); // röd färg
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Rita titel
        guiGraphics.drawString(this.font, this.title, 8, 6, 4210752, false);

        // Spelarens inventory label
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}*/
package generators;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class LavaGeneratorScreen extends AbstractContainerScreen<LavaGeneratorMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("generators", "textures/gui/lava_generator.png");

    // GUI dimensions - should match your texture size
    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 166;

    public LavaGeneratorScreen(LavaGeneratorMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);

        // Center the GUI on screen
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // FIX: Use the scaled blit method to ensure proper sizing
        // This explicitly tells Minecraft the texture size matches the GUI size
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        // Draw lava progress bar (right side)
        int lavaAmount = this.menu.getLavaAmount();
        int maxLava = this.menu.getMaxLava();

        if (maxLava > 0 && lavaAmount > 0) {
            int lavaHeight = 50;
            int filledHeight = (lavaAmount * lavaHeight) / maxLava;

            int lavaX = x + 128;
            int lavaY = y + 17;

            guiGraphics.fill(lavaX, lavaY + (lavaHeight - filledHeight),
                    lavaX + 16, lavaY + lavaHeight,
                    0xFFFFA500); // Orange for lava
        }

        // Draw energy progress bar (next to lava bar)
        int energy = this.menu.getEnergy();
        int maxEnergy = this.menu.getMaxEnergy();

        if (maxEnergy > 0 && energy > 0) {
            int energyHeight = 50;
            int filledEnergy = (energy * energyHeight) / maxEnergy;

            int energyX = x + 152; // Next to lava bar (128 - 24)
            int energyY = y + 17;

            guiGraphics.fill(energyX, energyY + (energyHeight - filledEnergy),
                    energyX + 16, energyY + energyHeight,
                    0xFFFFFF00); // Yellow for energy
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Draw title
        guiGraphics.drawString(this.font, this.title,
                (this.imageWidth - this.font.width(this.title)) / 2, 6, 0x404040, false);

        // Draw player inventory label
        guiGraphics.drawString(this.font, this.playerInventoryTitle,
                8, this.imageHeight - 94, 0x404040, false);

        // Draw stats
        int lavaAmount = this.menu.getLavaAmount();
        int energy = this.menu.getEnergy();

        /* LAVA */
        guiGraphics.drawString(this.font, "Lava",
                80, 18, 0x404040, false);

        guiGraphics.drawString(this.font, lavaAmount + "/4" + " MB",
                80, 28, 0x404040, false);

        /* ENERGY */
        guiGraphics.drawString(this.font, "Energy",
                80, 60, 0x404040, false);

        guiGraphics.drawString(this.font, energy +  " FE",
                80, 70, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}