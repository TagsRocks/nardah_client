package com.nardah.login.impl;


import com.nardah.*;
import com.nardah.login.LoginComponent;
import com.nardah.login.ScreenType;

/**
 * Handles the setting screen of login.
 *
 * @author Daniel
 */
public class SettingScreen extends LoginComponent {

	@Override
	public void render(Client client) {
		int centerX = getX();
		int centerY = getY();
		refresh(client);
		load(client, 13);

          /* Background */
		Client.spriteCache.get(678).drawTransparentSprite((Client.frameWidth / 2) - (Client.spriteCache.get(57).width / 2), (Client.frameHeight / 2) - (Client.spriteCache.get(57).height / 2), client.loginTick);

		client.boldText.drawCenteredText(0xff9040, centerX + 5, "Nardah", centerY - 105, true);
		client.regularText.drawCenteredText(0xB7B7B7, centerX + 5, "Settings", centerY - 85, true);

		//announcement
		client.regularText.drawCenteredText(0xFFFFFF, centerX + 5, "Announcement:", centerY - 50, true);
		if (client.mouseInRegion(centerX - 47, centerY - 37, centerX + 51, centerY - 9)) {
			Raster.fillRectangle(336, 215, 100, 30, 0x1F1D19, 105);
			Raster.drawRectangle(336, 215, 100, 30, 0x3d3427);
		} else {
//			Raster.fillRectangle(336, 215, 100, 30, 0x2D1F1F, 105);
			Raster.drawRectangle(336, 215, 100, 30, 0x3d3427);
		}
		client.smallFont.drawCenteredText(Utility.getPrefix(Settings.DRAW_ANNOUNCEMENT), centerX + 5, (Settings.DRAW_ANNOUNCEMENT ? "Enabled" : "Disabled"), centerY - 16, true);

		//bubbles
		client.regularText.drawCenteredText(0xFFFFFF, centerX + 5, "Bubbles:", centerY + 20, true);
		if (client.mouseInRegion(centerX - 47, centerY + 24, centerX + 58, centerY + 64)) {
			Raster.fillRectangle(336, 280, 100, 30, 0x1F1D19, 105);
			Raster.drawRectangle(336, 280, 100, 30, 0x3d3427);
		} else {
//			Raster.fillRectangle(336, 280, 100, 30, 0x2D1F1F, 105);
			Raster.drawRectangle(336, 280, 100, 30, 0x3d3427);
		}
		client.smallFont.drawCenteredText(Utility.getPrefix(Settings.DRAW_BUBBLE), centerX + 5, (Settings.DRAW_BUBBLE ? "Enabled" : "Disabled"), centerY + 49, true);

        /* Announcement */
		announcement(client);

        /* Bubble */
		drawSetting(client);

        /* Other */
		client.smallFont.drawCenteredText(0xFFFFFF, centerX + 330, "Client Build: " + Configuration.GAME_VERSION, centerY + 245, true);
		if (Configuration.DEBUG_MODE) {
			client.smallFont.drawCenteredText(0xFFFFFF, centerX + 300, "MouseX: " + (client.mouseX - (centerX)) + " Mouse Y: " + (client.mouseY - (centerY)), centerY - 225, true);
		}

        /* Drawing */
		Client.loginScreenIP.drawGraphics(client.graphics, 0, 0);
		Raster.reset();
	}

	@Override
	public void click(Client client) {
		int centerX = getX();
		int centerY = getY();

		if (client.lastMetaModifier == 1 && client.mouseInRegion(centerX - 47, centerY - 37, centerX + 51, centerY - 9)) {
			Settings.DRAW_ANNOUNCEMENT = !Settings.DRAW_ANNOUNCEMENT;
		}

		if (client.lastMetaModifier == 1 && client.mouseInRegion(centerX - 47, centerY + 24, centerX + 58, centerY + 64)) {
			Settings.DRAW_BUBBLE = !Settings.DRAW_BUBBLE;
		}

        /* Bubble */
		settingButton(client);
	}

	@Override
	public ScreenType type() {
		return ScreenType.SETTING;
	}
}
