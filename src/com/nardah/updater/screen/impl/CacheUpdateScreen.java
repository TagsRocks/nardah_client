package com.nardah.updater.screen.impl;

import java.awt.*;

import com.nardah.Client;
import com.nardah.Configuration;
import com.nardah.updater.screen.UpdateComponent;

public class CacheUpdateScreen extends UpdateComponent {
	private int progress, ticks;

	private int arcLength, arcAngle;

	/**
	 * The colors.
	 */
	private int startColor, endColor;

	@Override
	public void setup(Client client) {
		super.setup(client);
		Graphics2D g2d = (Graphics2D) getGraphics();
		g2d.setStroke(new BasicStroke(5));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		getGraphics().setFont(new Font("Tahoma", Font.PLAIN, 25));
		setLabel("Requesting cache files");
	}

	@Override
	public void render(Client client) {
		getGraphics().setFont(new Font("Tahoma", Font.PLAIN, 20));
		drawProgressBar();

		getGraphics().setFont(new Font("Tahoma", Font.PLAIN, 25));
		drawWaitingLabel(Color.WHITE);
	}

	@Override
	public void process() {
		super.process();
		if (startColor == endColor) {
			int minimum = 127;
			int r = (int) (Math.random() * (255 - minimum)) + minimum;
			int g = (int) (Math.random() * (255 - minimum)) + minimum;
			int b = (int) (Math.random() * (255 - minimum)) + minimum;
			endColor = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
		}
		double amplitude = ticks++ / 45.0;
		double offset = Math.sin(amplitude) + Math.cos(amplitude);
		arcAngle = (int) (180 * offset);
		arcLength = (int) (45 * offset);
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}


	private void drawProgressBar() {
		int messageWidth = getGraphics().getFontMetrics().stringWidth("Requesting cache files...");
		messageWidth += messageWidth / 2 + 5;
		int x = (getWidth() - messageWidth) / 2;
		int y = (getHeight() - messageWidth) / 2;

		getGraphics().setFont(new Font("Tahoma", Font.PLAIN, 15));
		getGraphics().drawString("v" + Configuration.GAME_VERSION, 5, 496);

		drawArc(x, y, messageWidth, 0, arcAngle);
		drawArc(x, y, messageWidth, 0, 180 + arcAngle);
		drawArc(x, y, messageWidth, 20, 180 - arcAngle);
		drawArc(x, y, messageWidth, 20, 360 - arcAngle);

		getGraphics().setFont(new Font("Tahoma", Font.PLAIN, 25));
		messageWidth = getGraphics().getFontMetrics().stringWidth(progress + "%");
		getGraphics().setColor(Color.WHITE);
		getGraphics().drawString(progress + "%", (getWidth() - messageWidth) / 2, getHeight() / 2 + 50);
	}

	private void drawArc(int x, int y, int size, int offSet, int angle) {
		Graphics2D g2d = (Graphics2D) getGraphics();

		g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		getGraphics().setColor(new Color(getRed(), getGreen(), getBlue()));
		getGraphics().drawArc(x - offSet / 2, y - offSet / 2, size + offSet, size + offSet, angle, 90 - arcLength);

		g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		getGraphics().setColor(new Color(getRed(), getGreen(), getBlue(), 75));
		getGraphics().drawArc(x - offSet / 2, y - offSet / 2, size + offSet, size + offSet, angle, 90 - arcLength);

		g2d.setStroke(new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		getGraphics().setColor(new Color(getRed(), getGreen(), getBlue(), 25));
		getGraphics().drawArc(x - offSet / 2, y - offSet / 2, size + offSet, size + offSet, angle, 90 - arcLength);

	}

	private int getRed() {
		int start = (startColor >> 16) & 0xFF;
		int end = (endColor >> 16) & 0xFF;
		if (start > end)
			start = (start - 1) & 0xFF;
		else if (start < end)
			start = (start + 1) & 0xFF;
		startColor = (startColor & 0x00FFFF) | (start << 16);
		return start;
	}

	private int getGreen() {
		int start = (startColor >> 8) & 0xFF;
		int end = (endColor >> 8) & 0xFF;
		if (start > end)
			start = (start - 1) & 0xFF;
		else if (start < end)
			start = (start + 1) & 0xFF;
		startColor = (startColor & 0xFF00FF) | (start << 8);
		return start;
	}

	private int getBlue() {
		int start = startColor & 0xFF;
		int end = endColor & 0xFF;
		if (start > end)
			start = (start - 1) & 0xFF;
		else if (start < end)
			start = (start + 1) & 0xFF;
		startColor = (startColor & 0xFFFF00) | start;
		return start;
	}
}
