package com.nardah.updater.screen.impl;

import java.awt.*;
import java.util.concurrent.TimeUnit;

import com.nardah.Client;
import com.nardah.Stopwatch;
import com.nardah.updater.UpdateState;
import com.nardah.updater.Updater;
import com.nardah.updater.screen.UpdateComponent;

public class WelcomeUpdateScreen extends UpdateComponent {

	private final Stopwatch stopwatch = new Stopwatch();

	private int alpha;

	private int stage;

	@Override
	public void setup(Client client) {
		super.setup(client);
		Graphics2D g2d = (Graphics2D) getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		getGraphics().setFont(new Font("Tahoma", Font.PLAIN, 25));
		stopwatch.reset(2000);
	}

	@Override
	public void render(Client client) {
		Color color = new Color(255, 255, 255, alpha);
		if (stage <= 5)
			drawLabel(color);
	}

	@Override
	public void process() {
		if (stage == 0 || stage == 1)
			setLabel("Hello");

		else if (stage == 2 || stage == 3)
			setLabel("Welcome to Nardah");

		else if (stage == 4 || stage == 5)
			setLabel("Please wait as we set up your files");

		else
			return;

		int rate = 5;

		if (stage % 2 == 0)
			alpha += rate;

		else if (stage % 2 == 1)
			alpha -= rate;

		if (alpha > 255)
			alpha = 255;
		if (alpha < 0)
			alpha = 0;

		if (stopwatch.hasElapsed(2, TimeUnit.SECONDS)) {
			stage++;
			if (stage == 6)
				Updater.get().state = UpdateState.DEFAULT_SCREEN;
			stopwatch.reset();
		}
	}

}
