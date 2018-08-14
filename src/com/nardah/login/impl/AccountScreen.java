package com.nardah.login.impl;


import com.nardah.*;
import com.nardah.login.LoginComponent;
import com.nardah.login.ScreenType;

/**
 * Handles the account screen of login.
 *
 * @author Daniel
 */
public class AccountScreen extends LoginComponent {

	@Override
	public void render(Client client) {
		int centerX = getX();
		int centerY = getY();
		refresh(client);
		load(client, 13);
		AccountData account = client.lastAccount;
		int moveY = 20;

		Sprite avatar = Client.spriteCache.get(account.avatar);

          /* Background */
		Client.spriteCache.get(678).drawTransparentSprite((Client.frameWidth / 2) - (Client.spriteCache.get(57).width / 2), (Client.frameHeight / 2) - (Client.spriteCache.get(57).height / 2), client.loginTick);

        /* Box */
		Raster.fillRectangle(175, 175 + moveY, 425, 150, 0x120f0a, 150);
		Raster.drawRectangle(175, 175 + moveY, 425, 150, 0x3d3427);

        /* Avatar */
		if (client.mouseInRegion(centerX - 181, centerY - 32, centerX - 84, centerY + 66)) {
			Raster.fillRectangle(200, 203 + moveY, 80, 80, 0x1F1D19, 85);
			avatar.drawTransparentSprite(212, 233, 85);
//			addTooltip("Select avatar");
		} else {
			Raster.fillRectangle(200, 203 + moveY, 80, 80, 0x1F1D19, 150);
			avatar.drawTransparentSprite(212, 233, 255);
		}
		Raster.drawRectangle(200, 203 + moveY, 80, 80, 0x3d3427);

        /* Messages */
		client.boldText.drawCenteredText(0xff9040, centerX + 5, "Nardah", centerY - 105, true);
		client.regularText.drawCenteredText(0xB7B7B7, centerX + 5, "Account Manager", centerY - 85, true);
		client.newBoldFont.drawCenteredString(account.username == null ? "" : Utility.formatName(account.username), 385, 210 + moveY, 0xff9040, 0);
		client.newBoldFont.drawBasicString("Created:", 320, 235 + moveY, 0xff9040, 0);
		client.newBoldFont.drawBasicString(account.created, 385, 235 + moveY, 0xffffff, 0);
		client.newBoldFont.drawBasicString("Rank:", 320, 265 + moveY, 0xff9040, 0);
		int rank = (account.rank - 1);
		client.newBoldFont.drawBasicString("<img=" + rank + "> " + Utility.getRank(rank), 385, 265 + moveY, 0xffffff, 0);
		client.newBoldFont.drawBasicString("Uses:", 320, 295 + moveY, 0xff9040, 0);
		client.newBoldFont.drawBasicString(account.uses + "", 385, 295 + moveY, 0xffffff, 0);
		client.smallFont.drawCenteredText(0xFFFFFF, centerX + 5, "Click on your avatar to open the avatar menu", centerY + 88, true);
		client.boldText.drawCenteredText(0xFFFFFF, centerX + 5, "[ Click anywhere to return to the main screen ]", centerY + 150, true);

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

        /* Bubble */
		settingButton(client);

        /* Avatar */
		if (client.lastMetaModifier == 1 && client.mouseInRegion(centerX - 181, centerY - 32, centerX - 84, centerY + 66)) {
			client.loginMessage1 = "";
			client.loginMessage2 = "";
			client.loginRenderer.setScreen(new AvatarScreen());
			return;
		}

		if (client.lastMetaModifier == 1 && client.mouseInRegion(centerX - 381, centerY - 249, centerX + 381, centerY + 245)) {
			client.loginMessage1 = "";
			client.loginMessage2 = "";
			client.loginRenderer.setScreen(new MainScreen());
		}
	}

	@Override
	public ScreenType type() {
		return ScreenType.ACCOUNT;
	}
}
