package com.nardah.login;


import com.nardah.*;
import com.nardah.login.impl.MainScreen;
import com.nardah.login.impl.SettingScreen;

/**
 * The login screen class.
 *
 * @author Daniel
 */
public abstract class LoginComponent {

	/**
	 * Renders the login screen component.
	 */
	public abstract void render(Client client);

	/**
	 * Handles clicking buttons on the login screen components.
	 */
	public abstract void click(Client client);

	public abstract ScreenType type();

	/**
	 * Handles refreshing the login screen components.
	 */
	protected void refresh(Client client) {
		if (Client.loginScreenIP == null) {
			Client.loginScreenIP = new GraphicsBuffer(765, 503, client.getGameComponent());
			Raster.reset();
		}
	}

	/**
	 * Handles loading the login screen components.
	 */
	public void load(Client client, int rate) {
		client.loginTick += rate;
		if (client.loginTick > 255) {
			client.loginTick = 255;
			client.loginLoaded = true;
		}
	}

	/**
	 * Draws the announcement bar
	 */
	protected void announcement(Client client) {
		if (Settings.DRAW_ANNOUNCEMENT) {
			client.announcementMovement--;
			client.announcementFade++;
			if (client.announcementMovement < -Configuration.ANNOUNCEMENT[client.announcementTicks].length() - 10) {
				client.announcementMovement = Client.frameWidth + 2;
				client.announcementTicks++;
				if (client.announcementTicks >= Configuration.ANNOUNCEMENT.length) {
					client.announcementTicks = 0;
				}
			}
			int color1 = 0xc98502;//c98502
			int color2 = 0x905a08;
			Raster.drawAlphaGradient(0, 0, Client.frameWidth, 25, color1, color2, 205 - (int) (50 * Math.sin(client.announcementFade / 20.0)));
			client.smallFont.drawText(true, client.announcementMovement, 0xffffff, Configuration.ANNOUNCEMENT[client.announcementTicks], 17);
		}
	}

	/**
	 * Draws the bubbles on the login screen.
	 */
	protected void drawSetting(Client client) {
		int centerX = getX();
		int centerY = getY();

//		if (Settings.DRAW_BUBBLE)
//			for (Bubble bubble : Client.bubbles)
//				bubble.draw(Bubble.BOUNCING_BALLS);

//		if (client.mouseInRegion(centerX - 58, centerY + 132, centerX + 27, centerY + 158)) {
//			Client.spriteCache.get(879).drawSprite(324, 386, 0);
//			addTooltip("Toggle world");
//		} else {
//			Client.spriteCache.get(879).drawSprite(670, 450, 0);
//		}

		if (client.mouseInRegion(37, 452, 83, 480)) {
			Client.spriteCache.get(879).drawSprite(centerX - 345, centerY + 200, 0);
			addTooltip("Manage login screen settings");
		} else {
			Client.spriteCache.get(879).drawTransparentSprite(centerX - 345, centerY + 200, 90);
		}



//		String string = client.loginRenderer.getScreen(ScreenType.SETTING) ? "Back" : "Settings";
//		client.boldText.drawCenteredText(0xFFFFFF, centerX - 312, string, centerY + 221, true);
	}

	/**
	 * Draws the bubble toggle on the login screen.
	 */
	protected void settingButton(Client client) {
		int centerX = getX();
		int centerY = getY();
		if (client.lastMetaModifier == 1 && client.mouseInRegion(37, 452, 83, 480)) {
			if (client.loginRenderer.getScreen(ScreenType.SETTING)) {
				client.loginRenderer.setScreen(new MainScreen());
			} else {
				client.loginRenderer.setScreen(new SettingScreen());
			}
		}
	}

	/**
	 * Draws a tooltip hover to the login screen.
	 */
	protected void addTooltip(String message) {
		int x = Client.instance.mouseX;
		int y = Client.instance.mouseY;
		int boxWith = Client.instance.newSmallFont.getTextWidth(message) + 30;

		if (x > 638)
			x = 638;
		if (y > 467)
			y = 467;

		Raster.drawTransparentBox(x + 15, y + 10, boxWith, 22, 0x534a40, 150);
		Raster.drawRectangle(x + 15, y + 10, boxWith, 22, 0x383023);
		Client.instance.newSmallFont.drawBasicString(message, x + 30, y + 27, 16777215, 1);
	}

	/**
	 * Gets the x position of the mouse.
	 */
	public int getX() {
		return Client.frameWidth / 2;
	}

	/**
	 * Gets the y position of the mouse.
	 */
	public int getY() {
		return Client.frameHeight / 2;
	}
}
