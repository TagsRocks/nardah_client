package com.nardah.login.impl;


import com.nardah.Client;
import com.nardah.Raster;
import com.nardah.login.LoginComponent;
import com.nardah.login.ScreenType;

/**
 * Handles the message screen of login.
 *
 * @author Daniel
 */
public class MessageScreen extends LoginComponent {

	@Override
	public void render(Client client) {
		int centerX = getX();
		int centerY = getY();
		refresh(client);
		load(client, 13);

        /* Background */
		Client.spriteCache.get(678).drawTransparentSprite((Client.frameWidth / 2) - (Client.spriteCache.get(57).width / 2), (Client.frameHeight / 2) - (Client.spriteCache.get(57).height / 2), client.loginTick);

        /* Box */
		Raster.fillRectangle(175, 215, 425, 100, 0x1F1D19, 150);
		Raster.drawRectangle(175, 215, 425, 100, 0x3d3427);

        /* Messages */
		client.boldText.drawCenteredText(0xff9040, centerX + 5, "Nardah", centerY - 115, true);
		client.regularText.drawCenteredText(0xB7B7B7, centerX + 5, "Error Message", centerY - 95, true);
		if (client.loginMessage2.length() == 0) {
			client.regularText.drawCenteredText(0xE56161, centerX + 5, client.loginMessage1, centerY + 20, true);
		} else {
			client.boldText.drawCenteredText(0xE56161, centerX + 5, client.loginMessage1, centerY + 15, true);
			client.boldText.drawCenteredText(0xE56161, centerX + 5, client.loginMessage2, centerY + 35, true);
		}
		client.boldText.drawCenteredText(0xFFFFFF, centerX + 5, "[ Click anywhere to return to the main screen ]", centerY + 150, true);

        /* Announcement */
		announcement(client);

        /* Bubble */
		drawSetting(client);

        /* Drawing */
		Client.loginScreenIP.drawGraphics(client.graphics, 0, 0);
		Raster.reset();
	}

	@Override
	public void click(Client client) {
		int centerX = getX();
		int centerY = getY();

        /* Bubble */
		settingButton(client);

		if (client.lastMetaModifier == 1 && client.mouseInRegion(centerX - 381, centerY - 249, centerX + 381, centerY + 245)) {
			client.loginMessage1 = "";
			client.loginMessage2 = "";
			client.loginRenderer.setScreen(new MainScreen());
		}
	}

	@Override
	public ScreenType type() {
		return ScreenType.MESSAGE;
	}
}
