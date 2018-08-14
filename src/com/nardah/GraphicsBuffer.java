package com.nardah;

import java.awt.*;
import java.awt.image.*;

public final class GraphicsBuffer implements ImageProducer, ImageObserver {

	public final int[] raster;
	public final int width;
	public final int height;
	private final ColorModel model;
	private ImageConsumer consumer;
	private final Image image;

	public GraphicsBuffer(int width, int height, int[] raster, Component component) {
		this.width = width;
		this.height = height;
		this.raster = raster;
		model = new DirectColorModel(32, 0xff0000, 65280, 255);
		image = component.createImage(this);
		setPixels();
		component.prepareImage(image, this);
		setPixels();
		component.prepareImage(image, this);
		setPixels();
		component.prepareImage(image, this);
		initDrawingArea();
	}

	public GraphicsBuffer(int width, int height, Component component) {
		this.width = width;
		this.height = height;
		this.raster = new int[width * height];
		this.model = new DirectColorModel(32, 0xff0000, 65280, 255);
		this.image = component.createImage(this);
		setPixels();
		component.prepareImage(image, this);
		setPixels();
		component.prepareImage(image, this);
		setPixels();
		component.prepareImage(image, this);
		initDrawingArea();
	}

	@Override
	public synchronized void addConsumer(ImageConsumer consumer) {
		this.consumer = consumer;
		consumer.setDimensions(width, height);
		consumer.setProperties(null);
		consumer.setColorModel(model);
		consumer.setHints(14);
	}

	public void drawGraphics(Graphics g, int x, int y) {
		setPixels();
		g.drawImage(image, x, y, this);
	}

	@Override
	public boolean imageUpdate(Image image, int bit, int x, int y, int width, int height) {
		return true;
	}

	public void initDrawingArea() {
		Raster.initDrawingArea(raster, width, height);
	}

	@Override
	public synchronized boolean isConsumer(ImageConsumer consumer) {
		return this.consumer == consumer;
	}

	private synchronized void setPixels() {
		if (consumer != null) {
			consumer.setPixels(0, 0, width, height, model, raster, 0, width);
			consumer.imageComplete(2);
		}
	}

	@Override
	public synchronized void removeConsumer(ImageConsumer consumer) {
		if (this.consumer == consumer) {
			consumer = null;
		}
	}

	@Override
	public void requestTopDownLeftRightResend(ImageConsumer consumer) {
		System.out.println("TDLR");
	}

	@Override
	public void startProduction(ImageConsumer consumer) {
		addConsumer(consumer);
	}

	@Override
	public String toString() {
		return "Width: " + width + " Height: " + height;
	}
}