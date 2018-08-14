package com.nardah.updater.screen;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import com.nardah.Client;
import com.nardah.Stopwatch;

/**
 * The Update component class.
 *
 * @author Daniel
 * @author Michael | Chex
 */
public abstract class UpdateComponent {

	/**
	 * The label string.
	 */
	private String label;

	/**
	 * Ticks for updating.
	 */
	private int ticks;

	/**
	 * The stopwatch.
	 */
	private final Stopwatch stopwatch = new Stopwatch();

	/**
	 * The buffered image.
	 */
	private BufferedImage image;

	/**
	 * The graphics object.
	 */
	private Graphics graphics;

	/**
	 * Renders the updater screen component.
	 *
	 * @param client the client instance
	 */
	public void setup(Client client) {
		image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		graphics = image.createGraphics();
	}

	/**
	 * Renders the updater screen component.
	 *
	 * @param client the client instance
	 */
	public abstract void render(Client client);

	public void setLabel(String label) {
		this.label = label;
	}

	protected final void drawLabel(Color color) {
		int labelWidth = getGraphics().getFontMetrics().stringWidth(label);
		int x = (getWidth() - labelWidth) / 2;
		int y = getHeight() / 2;
		getGraphics().setColor(new Color(0, 0, 0, color.getAlpha()));
		getGraphics().drawString(label, x + 1, y + 1);
		getGraphics().setColor(color);
		getGraphics().drawString(label, x, y);
	}

	protected final void drawWaitingLabel(Color color) {
		int labelWidth = getGraphics().getFontMetrics().stringWidth(label);
		int x = (getWidth() - labelWidth) / 2;
		int y = getHeight() / 2;

		if (ticks % 4 > 0) {
			StringBuilder message = new StringBuilder(label);
			for (int index = 0; index <= ticks % 4; index++) {
				if (index > 0) {
					message.append('.');
				}
			}
			getGraphics().setColor(Color.BLACK);
			getGraphics().drawString(message.toString(), x + 1, y + 1);
			getGraphics().setColor(color);
			getGraphics().drawString(message.toString(), x, y);
		} else {
			getGraphics().setColor(Color.BLACK);
			getGraphics().drawString(label, x + 1, y + 1);
			getGraphics().setColor(color);
			getGraphics().drawString(label, x, y);
		}
	}

	public void process() {
		if (stopwatch.hasElapsed(500, TimeUnit.MILLISECONDS)) {
			ticks++;
			stopwatch.reset();
		}
	}

	/**
	 * Gets the client width.
	 *
	 * @return the width
	 */
	public int getWidth() {
		return Client.frameWidth;
	}

	/**
	 * Gets the client height.
	 *
	 * @return the length
	 */
	public int getHeight() {
		return Client.frameHeight;
	}

	/**
	 * Gets the image.
	 *
	 * @return the image
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Gets the client instance's graphic object.
	 *
	 * @return the graphic object
	 */
	public Graphics getGraphics() {
		return graphics;
	}

}
