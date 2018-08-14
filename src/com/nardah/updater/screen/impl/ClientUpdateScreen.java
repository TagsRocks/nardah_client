package com.nardah.updater.screen.impl;

import java.awt.*;

import com.nardah.Client;
import com.nardah.updater.screen.UpdateComponent;

public class ClientUpdateScreen extends UpdateComponent {
	private int progress;

	@Override
	public void setup(Client client) {
		super.setup(client);
		Graphics2D g2d = (Graphics2D) getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		getGraphics().setFont(new Font("Tahoma", Font.PLAIN, 25));
		setLabel("Requesting client files");
	}

	@Override
	public void render(Client client) {
		getGraphics().setFont(new Font("Tahoma", Font.PLAIN, 25));
		drawWaitingLabel(Color.WHITE);

		getGraphics().setFont(new Font("Tahoma", Font.PLAIN, 20));
		drawProgressBar();

		if (progress == 100) {
			progress = 0;
		}
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	private void drawProgressBar() {
		int width = 200;
		int height = 30;
		int completed = width * progress / 100;

		getGraphics().setColor(Color.WHITE);
		getGraphics().drawRect((getWidth() - width) / 2, getHeight() / 2 + height, width, height);
		getGraphics().fillRect((getWidth() - width) / 2, getHeight() / 2 + height, completed, height);

		int messageWidth = getGraphics().getFontMetrics().stringWidth(progress + "%");
		getGraphics().setColor(Color.BLACK);
		getGraphics().drawString(progress + "%", (getWidth() - messageWidth) / 2 + 1, getHeight() / 2 + height + height - 6);
		getGraphics().setColor(Color.DARK_GRAY);
		getGraphics().drawString(progress + "%", (getWidth() - messageWidth) / 2, getHeight() / 2 + height + height - 7);
	}

}
