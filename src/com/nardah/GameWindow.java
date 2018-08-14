package com.nardah;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


public final class GameWindow extends JFrame {

	private final GameApplet applet;
	private Toolkit toolkit = Toolkit.getDefaultToolkit();
	private Dimension screenSize = toolkit.getScreenSize();
	public int screenWidth = (int) screenSize.getWidth();
	public int screenHeight = (int) screenSize.getHeight();
	private final Insets insets;
	private static final long serialVersionUID = 1L;

	GameWindow(GameApplet applet, int width, int height, boolean resizable, boolean fullscreen) {
		this.applet = applet;
		setTitle(Configuration.PREFIX + " " + Configuration.NAME);
		setResizable(resizable);
		setUndecorated(fullscreen);
		setVisible(true);
		insets = getInsets();
		if (resizable) {
			setMinimumSize(new Dimension(766 + insets.left + insets.right, 536 + insets.top + insets.bottom));
		}
		setSize(width + insets.left + insets.right, height + insets.top + insets.bottom);
		setLocationRelativeTo(null);
		setBackground(Color.BLACK);
		requestFocus();
		setFocusTraversalKeysEnabled(false);
		toFront();
	}

	public static Image getImageFromArray(int[] pixels, int width, int height) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < pixels.length; i++) {
			int x = i % width;
			int y = i / height;

			image.setRGB(x, y, pixels[i]);
		}
		return image;
	}

	public Graphics getGraphics() {
		final Graphics graphics = super.getGraphics();
		Insets insets = this.getInsets();
		graphics.fillRect(0, 0, getWidth(), getHeight());
		graphics.translate(insets != null ? insets.left : 0, insets != null ? insets.top : 0);
		return graphics;
	}

	public int getFrameWidth() {
		Insets insets = this.getInsets();
		return getWidth() - (insets.left + insets.right);
	}

	public int getFrameHeight() {
		Insets insets = this.getInsets();
		return getHeight() - (insets.top + insets.bottom);
	}

	public void update(Graphics graphics) {
		applet.update(graphics);
	}

	public void paint(Graphics graphics) {
		applet.paint(graphics);
	}
}