package com.nardah;

import java.awt.*;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.image.ColorModel;

import javax.swing.ImageIcon;

public final class Sprite extends Raster {
	
	
	private static final ColorModel COLOR_MODEL = new DirectColorModel(32, 0xff0000, 0xff00, 0xff);

	public static Graphics2D createGraphics(int[] pixels, int width, int height) {
		return new java.awt.image.BufferedImage(
			COLOR_MODEL, 
				java.awt.image.Raster.createWritableRaster(COLOR_MODEL.createCompatibleSampleModel(width, height), 
					new java.awt.image.DataBufferInt(pixels, width * height), null), 
						false, new java.util.Hashtable<Object, Object>()).createGraphics();
	}
	
	
	

	private int idenfier;

	private String name;

	public int raster[];

	public int width;
	public int height;
	int offsetX;
	int offsetY;
	public int resizeWidth;
	public int resizeHeight;

	public Sprite() {
	}

	public int getId() {
		return idenfier;
	}

	public void setId(int id) {
		this.idenfier = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int[] getRaster() {
		return raster;
	}

	public void setRaster(int[] raster) {
		this.raster = raster;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	public Sprite(int width, int height) {
		raster = new int[width * height];
		this.width = resizeWidth = width;
		this.height = resizeHeight = height;
		offsetX = offsetY = 0;
	}

	private static Image getImageFromArray(int[] pixels, int width, int height) {
		MemoryImageSource source = new MemoryImageSource(width, height, pixels, 0, width);
		Toolkit kit = Toolkit.getDefaultToolkit();
		return kit.createImage(source);
	}

	public static Sprite resizeSprite(Sprite s, int width, int height) {
		try {
			Sprite sprite = new Sprite();
			Image image = getImageFromArray(s.raster, s.width, s.height);
			sprite.width = width;
			sprite.height = height;
			sprite.resizeWidth = sprite.width;
			sprite.resizeHeight = sprite.height;
			sprite.raster = new int[sprite.width * sprite.height];
			PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, sprite.width, sprite.height, sprite.raster, 0, sprite.width);
			pixelgrabber.grabPixels();
			image = null;
			return sprite;
		} catch (Exception _ex) {
			return null;
		}
	}
	

	public String location = Utility.findcachedir() + "Sprites/";

	public Sprite(byte abyte0[], Component component) {
		try {
			Image image = Toolkit.getDefaultToolkit().createImage(abyte0);
			MediaTracker mediatracker = new MediaTracker(component);
			mediatracker.addImage(image, 0);
			mediatracker.waitForAll();
			width = image.getWidth(component);
			height = image.getHeight(component);
			resizeWidth = width;
			resizeHeight = height;
			offsetX = 0;
			offsetY = 0;
			raster = new int[width * height];
			PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, width, height, raster, 0, width);
			pixelgrabber.grabPixels();
		} catch (Exception _ex) {
			System.out.println("Error converting jpg");
		}
	}

	public Sprite(Sprite other, int x, int y, int width, int height) {
		this.width = resizeWidth = width;
		this.height = resizeHeight = height;
		offsetX = offsetY = 0;
		raster = new int[width * height];

		for (int y1 = 0; y1 < height; y1++) {
			for (int x1 = 0; x1 < width; x1++) {
				int index = ((x + x1) + (y + y1) * other.width);
				raster[x1 + (y1 * width)] = other.raster[index];
			}
		}
	}

	public Sprite(int width, int height, int offsetX, int offsetY, int[] pixels) {
		this.width = width;
		this.height = height;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.raster = pixels;

		Color color = Color.MAGENTA;
		setTransparency(color.getRed(), color.getGreen(), color.getBlue());
	}

	public Sprite(String img, int width, int height) {
		try {
			Image image = Toolkit.getDefaultToolkit().createImage(FileUtility.readFile(img));
			this.width = width;
			this.height = height;
			resizeWidth = width;
			resizeHeight = height;
			offsetX = 0;
			offsetY = 0;
			raster = new int[width * height];
			PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, width, height, raster, 0, width);
			pixelgrabber.grabPixels();
			image = null;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Sprite(String img) {
		try {
			Image image = Toolkit.getDefaultToolkit().getImage(location + img + ".png");
			ImageIcon sprite = new ImageIcon(image);
			width = sprite.getIconWidth();
			height = sprite.getIconHeight();
			resizeWidth = width;
			resizeHeight = height;
			offsetX = 0;
			offsetY = 0;
			raster = new int[width * height];
			PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, width, height, raster, 0, width);
			pixelgrabber.grabPixels();
			image = null;
			setTransparency(255, 0, 255);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Sprite(Sprite target) {
		this(target, 0, 0, target.width, target.height);
	}

	public void drawHoverSprite(int x, int y, int offsetX, int offsetY, Sprite hover) {
		this.drawSprite(x, y);
		if (Client.instance.mouseX >= offsetX + x && Client.instance.mouseX <= offsetX + x + this.width && Client.instance.mouseY >= offsetY + y && Client.instance.mouseY <= offsetY + y + this.height) {
			hover.drawSprite(x, y);
		}
	}


	public void draw24BitSprite(int x, int y) {
		int alpha = 256;
		x += this.offsetX;// offsetX
		y += this.offsetY;// offsetY
		int destOffset = x + y * Raster.width;
		int srcOffset = 0;
		int height = this.height;
		int width = this.width;
		int destStep = Raster.width - width;
		int srcStep = 0;
		if (y < topY) {
			int trimHeight = topY - y;
			height -= trimHeight;
			y = topY;
			srcOffset += trimHeight * width;
			destOffset += trimHeight * Raster.width;
		}
		if (y + height > bottomY) {
			height -= (y + height) - bottomY;
		}
		if (x < topX) {
			int trimLeft = topX - x;
			width -= trimLeft;
			x = topX;
			srcOffset += trimLeft;
			destOffset += trimLeft;
			srcStep += trimLeft;
			destStep += trimLeft;
		}
		if (x + width > bottomX) {
			int trimRight = (x + width) - bottomX;
			width -= trimRight;
			srcStep += trimRight;
			destStep += trimRight;
		}
		if (!((width <= 0) || (height <= 0))) {
			set24BitPixels(width, height, pixels, raster, alpha, destOffset, srcOffset, destStep, srcStep);
		}
	}

	public void drawTransparentSprite(int i, int j, int opacity) {
		int k = opacity;// was parameter
		i += offsetX;
		j += offsetY;
		int i1 = i + j * Raster.width;
		int j1 = 0;
		int k1 = height;
		int l1 = width;
		int i2 = Raster.width - l1;
		int j2 = 0;
		if (j < topY) {
			int k2 = topY - j;
			k1 -= k2;
			j = topY;
			j1 += k2 * l1;
			i1 += k2 * Raster.width;
		}
		if (j + k1 > bottomY)
			k1 -= (j + k1) - bottomY;
		if (i < topX) {
			int l2 = topX - i;
			l1 -= l2;
			i = topX;
			j1 += l2;
			i1 += l2;
			j2 += l2;
			i2 += l2;
		}
		if (i + l1 > bottomX) {
			int i3 = (i + l1) - bottomX;
			l1 -= i3;
			j2 += i3;
			i2 += i3;
		}
		if (!(l1 <= 0 || k1 <= 0)) {
			method351(j1, l1, pixels, raster, j2, k1, i2, k, i1);
		}
	}

	private void set24BitPixels(int width, int height, int destPixels[], int srcPixels[], int srcAlpha, int destOffset, int srcOffset, int destStep, int srcStep) {
		int srcColor;
		int destAlpha;
		for (int loop = -height; loop < 0; loop++) {
			for (int loop2 = -width; loop2 < 0; loop2++) {
				srcAlpha = ((this.raster[srcOffset] >> 24) & 255);
				destAlpha = 256 - srcAlpha;
				srcColor = srcPixels[srcOffset++];
				if (srcColor != 0 && srcColor != 0xffffff) {
					int destColor = destPixels[destOffset];
					destPixels[destOffset++] = ((srcColor & 0xff00ff) * srcAlpha + (destColor & 0xff00ff) * destAlpha & 0xff00ff00) + ((srcColor & 0xff00) * srcAlpha + (destColor & 0xff00) * destAlpha & 0xff0000) >> 8;
				} else {
					destOffset++;
				}
			}
			destOffset += destStep;
			srcOffset += srcStep;
		}
	}

	public void setTransparency(int transRed, int transGreen, int transBlue) {
		for (int index = 0; index < raster.length; index++)
			if (((raster[index] >> 16) & 255) == transRed && ((raster[index] >> 8) & 255) == transGreen && (raster[index] & 255) == transBlue)
				raster[index] = 0;
	}

	public Sprite(StreamLoader streamLoader, String s, int i) {
		Buffer stream = new Buffer(streamLoader.getDataForName(s + ".dat"));
		Buffer stream_1 = new Buffer(streamLoader.getDataForName("index.dat"));
		stream_1.currentOffset = stream.readUShort();
		resizeWidth = stream_1.readUShort();
		resizeHeight = stream_1.readUShort();
		int j = stream_1.readUByte();
		int ai[] = new int[j];
		for (int k = 0; k < j - 1; k++) {
			ai[k + 1] = stream_1.read24Int();
			if (ai[k + 1] == 0)
				ai[k + 1] = 1;
		}

		for (int l = 0; l < i; l++) {
			stream_1.currentOffset += 2;
			stream.currentOffset += stream_1.readUShort() * stream_1.readUShort();
			stream_1.currentOffset++;
		}

		offsetX = stream_1.readUByte();
		offsetY = stream_1.readUByte();
		width = stream_1.readUShort();
		height = stream_1.readUShort();
		int i1 = stream_1.readUByte();
		int j1 = width * height;
		raster = new int[j1];
		if (i1 == 0) {
			for (int k1 = 0; k1 < j1; k1++)
				raster[k1] = ai[stream.readUByte()];
			setTransparency(255, 0, 255);
			return;
		}
		if (i1 == 1) {
			for (int l1 = 0; l1 < width; l1++) {
				for (int i2 = 0; i2 < height; i2++)
					raster[l1 + i2 * width] = ai[stream.readUByte()];
			}

		}
		setTransparency(255, 0, 255);
	}

	public void method343() {
		initDrawingArea(raster, width, height);
	}

	public void method344(int i, int j, int k) {
		for (int i1 = 0; i1 < raster.length; i1++) {
			int j1 = raster[i1];
			if (j1 != 0) {
				int k1 = j1 >> 16 & 0xff;
				k1 += i;
				if (k1 < 1)
					k1 = 1;
				else if (k1 > 255)
					k1 = 255;
				int l1 = j1 >> 8 & 0xff;
				l1 += j;
				if (l1 < 1)
					l1 = 1;
				else if (l1 > 255)
					l1 = 255;
				int i2 = j1 & 0xff;
				i2 += k;
				if (i2 < 1)
					i2 = 1;
				else if (i2 > 255)
					i2 = 255;
				raster[i1] = (k1 << 16) + (l1 << 8) + i2;
			}
		}

	}

	public void method345() {
		int ai[] = new int[resizeWidth * resizeHeight];
		for (int j = 0; j < height; j++) {
			System.arraycopy(raster, j * width, ai, j + offsetY * resizeWidth + offsetX, width);
		}

		raster = ai;
		width = resizeWidth;
		height = resizeHeight;
		offsetX = 0;
		offsetY = 0;
	}

	public void method346(int i, int j) {
		i += offsetX;
		j += offsetY;
		int l = i + j * Raster.width;
		int i1 = 0;
		int j1 = height;
		int k1 = width;
		int l1 = Raster.width - k1;
		int i2 = 0;
		if (j < topY) {
			int j2 = topY - j;
			j1 -= j2;
			j = topY;
			i1 += j2 * k1;
			l += j2 * Raster.width;
		}
		if (j + j1 > bottomY)
			j1 -= (j + j1) - bottomY;
		if (i < topX) {
			int k2 = topX - i;
			k1 -= k2;
			i = topX;
			i1 += k2;
			l += k2;
			i2 += k2;
			l1 += k2;
		}
		if (i + k1 > bottomX) {
			int l2 = (i + k1) - bottomX;
			k1 -= l2;
			i2 += l2;
			l1 += l2;
		}
		if (k1 <= 0 || j1 <= 0) {
		} else {
			method347(l, k1, j1, i2, i1, l1, raster, pixels);
		}
	}

	private void method347(int i, int j, int k, int l, int i1, int k1, int ai[], int ai1[]) {
		int l1 = -(j >> 2);
		j = -(j & 3);
		for (int i2 = -k; i2 < 0; i2++) {
			for (int j2 = l1; j2 < 0; j2++) {
				ai1[i++] = ai[i1++];
				ai1[i++] = ai[i1++];
				ai1[i++] = ai[i1++];
				ai1[i++] = ai[i1++];
			}

			for (int k2 = j; k2 < 0; k2++)
				ai1[i++] = ai[i1++];

			i += k1;
			i1 += l;
		}
	}

	public void drawSprite1(int i, int j) {
		drawSprite1(i, j, 128);
	}

	public void drawSprite1(int i, int j, int k, boolean one) {
		i += offsetX;
		j += offsetY;
		int i1 = i + j * Raster.width;
		int j1 = 0;
		int k1 = height;
		int l1 = width;
		int i2 = Raster.width - l1;
		int j2 = 0;
		if (!(one && j > 0) && j < topY) {
			int k2 = topY - j;
			k1 -= k2;
			j = topY;
			j1 += k2 * l1;
			i1 += k2 * Raster.width;
		}
		if (j + k1 > bottomY)
			k1 -= (j + k1) - bottomY;
		if (!(one && j > 0) && i < topX) {
			int l2 = topX - i;
			l1 -= l2;
			i = topX;
			j1 += l2;
			i1 += l2;
			j2 += l2;
			i2 += l2;
		}
		if (i + l1 > bottomX) {
			int i3 = (i + l1) - bottomX;
			l1 -= i3;
			j2 += i3;
			i2 += i3;
		}
		if (!(l1 <= 0 || k1 <= 0)) {
			method351(j1, l1, pixels, raster, j2, k1, i2, k, i1);
		}
	}

	public void drawSprite1(int i, int j, int k) {
		i += offsetX;
		j += offsetY;
		int i1 = i + j * Raster.width;
		int j1 = 0;
		int k1 = height;
		int l1 = width;
		int i2 = Raster.width - l1;
		int j2 = 0;
		if (j < topY) {
			int k2 = topY - j;
			k1 -= k2;
			j = topY;
			j1 += k2 * l1;
			i1 += k2 * Raster.width;
		}
		if (j + k1 > bottomY)
			k1 -= (j + k1) - bottomY;
		if (i < topX) {
			int l2 = topX - i;
			l1 -= l2;
			i = topX;
			j1 += l2;
			i1 += l2;
			j2 += l2;
			i2 += l2;
		}
		if (i + l1 > bottomX) {
			int i3 = (i + l1) - bottomX;
			l1 -= i3;
			j2 += i3;
			i2 += i3;
		}
		if (!(l1 <= 0 || k1 <= 0)) {
			method351(j1, l1, pixels, raster, j2, k1, i2, k, i1);
		}
	}

	public void drawSprite(int i, int k) {
		i += offsetX;
		k += offsetY;
		int l = i + k * Raster.width;
		int i1 = 0;
		int j1 = height;
		int k1 = width;
		int l1 = Raster.width - k1;
		int i2 = 0;
		if (k < topY) {
			int j2 = topY - k;
			j1 -= j2;
			k = topY;
			i1 += j2 * k1;
			l += j2 * Raster.width;
		}
		if (k + j1 > bottomY)
			j1 -= (k + j1) - bottomY;
		if (i < topX) {
			int k2 = topX - i;
			k1 -= k2;
			i = topX;
			i1 += k2;
			l += k2;
			i2 += k2;
			l1 += k2;
		}
		if (i + k1 > bottomX) {
			int l2 = (i + k1) - bottomX;
			k1 -= l2;
			i2 += l2;
			l1 += l2;
		}
		if (!(k1 <= 0 || j1 <= 0)) {
			method349(pixels, raster, i1, l, k1, j1, l1, i2); // draws sprites
		}
	}

	public void drawSprite(int i, int k, int color) {
		int tempWidth = width + 2;
		int tempHeight = height + 2;
		int[] tempArray = new int[tempWidth * tempHeight];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (raster[x + y * width] != 0)
					tempArray[(x + 1) + (y + 1) * tempWidth] = raster[x + y * width];
			}
		}
		for (int x2 = 0; x2 < tempWidth; x2++) {
			for (int y = 0; y < tempHeight; y++) {
				if (tempArray[(x2) + (y) * tempWidth] == 0) {
					if (x2 < tempWidth - 1 && tempArray[(x2 + 1) + ((y) * tempWidth)] != 0 && tempArray[(x2 + 1) + ((y) * tempWidth)] != 0xffffff) {
						tempArray[(x2) + (y) * tempWidth] = color;
					}
					if (x2 > 0 && tempArray[(x2 - 1) + ((y) * tempWidth)] != 0 && tempArray[(x2 - 1) + ((y) * tempWidth)] != 0xffffff) {
						tempArray[(x2) + (y) * tempWidth] = color;
					}
					if (y < tempHeight - 1 && tempArray[(x2) + ((y + 1) * tempWidth)] != 0 && tempArray[(x2) + ((y + 1) * tempWidth)] != 0xffffff) {
						tempArray[(x2) + (y) * tempWidth] = color;
					}
					if (y > 0 && tempArray[(x2) + ((y - 1) * tempWidth)] != 0 && tempArray[(x2) + ((y - 1) * tempWidth)] != 0xffffff) {
						tempArray[(x2) + (y) * tempWidth] = color;
					}
				}
			}
		}
		i--;
		k--;
		i += offsetX;
		k += offsetY;
		int l = i + k * Raster.width;
		int i1 = 0;
		int j1 = tempHeight;
		int k1 = tempWidth;
		int l1 = Raster.width - k1;
		int i2 = 0;
		if (k < topY) {
			int j2 = topY - k;
			j1 -= j2;
			k = topY;
			i1 += j2 * k1;
			l += j2 * Raster.width;
		}
		if (k + j1 > bottomY) {
			j1 -= (k + j1) - bottomY;
		}
		if (i < topX) {
			int k2 = topX - i;
			k1 -= k2;
			i = topX;
			i1 += k2;
			l += k2;
			i2 += k2;
			l1 += k2;
		}
		if (i + k1 > bottomX) {
			int l2 = (i + k1) - bottomX;
			k1 -= l2;
			i2 += l2;
			l1 += l2;
		}
		if (!(k1 < 0 || j1 < 0)) {
			method349(pixels, tempArray, i1, l, k1, j1, l1, i2); // magic sprite outline
		}
	}

	public void drawSprite2(int i, int j) {
		int k = 225;// was parameter
		i += offsetX;
		j += offsetY;
		int i1 = i + j * Raster.width;
		int j1 = 0;
		int k1 = height;
		int l1 = width;
		int i2 = Raster.width - l1;
		int j2 = 0;
		if (j < topY) {
			int k2 = topY - j;
			k1 -= k2;
			j = topY;
			j1 += k2 * l1;
			i1 += k2 * Raster.width;
		}
		if (j + k1 > bottomY)
			k1 -= (j + k1) - bottomY;
		if (i < topX) {
			int l2 = topX - i;
			l1 -= l2;
			i = topX;
			j1 += l2;
			i1 += l2;
			j2 += l2;
			i2 += l2;
		}
		if (i + l1 > bottomX) {
			int i3 = (i + l1) - bottomX;
			l1 -= i3;
			j2 += i3;
			i2 += i3;
		}
		if (!(l1 <= 0 || k1 <= 0)) {
			method351(j1, l1, pixels, raster, j2, k1, i2, k, i1);
		}
	}

	private void method349(int ai[], int ai1[], int j, int k, int l, int i1, int j1, int k1) {
		int i;// was parameter
		int l1 = -(l >> 2);
		l = -(l & 3);
		for (int i2 = -i1; i2 < 0; i2++) {
			for (int j2 = l1; j2 < 0; j2++) {
				i = ai1[j++];
				if (i != 0 && i != -1) {
					ai[k++] = i;
				} else {
					k++;
				}
				i = ai1[j++];
				if (i != 0 && i != -1) {
					ai[k++] = i;
				} else {
					k++;
				}
				i = ai1[j++];
				if (i != 0 && i != -1) {
					ai[k++] = i;
				} else {
					k++;
				}
				i = ai1[j++];
				if (i != 0 && i != -1) {
					ai[k++] = i;
				} else {
					k++;
				}
			}

			for (int k2 = l; k2 < 0; k2++) {
				i = ai1[j++];
				if (i != 0 && i != -1) {
					ai[k++] = i;
				} else {
					k++;
				}
			}
			k += j1;
			j += k1;
		}
	}

	private void method351(int i, int j, int ai[], int ai1[], int l, int i1, int j1, int k1, int l1) {
		int k;// was parameter
		int j2 = 256 - k1;
		for (int k2 = -i1; k2 < 0; k2++) {
			for (int l2 = -j; l2 < 0; l2++) {
				k = ai1[i++];
				if (k != 0) {
					int i3 = ai[l1];
					ai[l1++] = ((k & 0xff00ff) * k1 + (i3 & 0xff00ff) * j2 & 0xff00ff00) + ((k & 0xff00) * k1 + (i3 & 0xff00) * j2 & 0xff0000) >> 8;
				} else {
					l1++;
				}
			}

			l1 += j1;
			i += l;
		}
	}

	public void method352(int i, int j, int ai[], int k, int ai1[], int i1, int j1, int k1, int l1, int i2) {
		try {
			int j2 = -l1 / 2;
			int k2 = -i / 2;
			int l2 = (int) (Math.sin((double) j / 326.11000000000001D) * 65536D);
			int i3 = (int) (Math.cos((double) j / 326.11000000000001D) * 65536D);
			l2 = l2 * k >> 8;
			i3 = i3 * k >> 8;
			int j3 = (i2 << 16) + (k2 * l2 + j2 * i3);
			int k3 = (i1 << 16) + (k2 * i3 - j2 * l2);
			int l3 = k1 + j1 * Raster.width;
			for (j1 = 0; j1 < i; j1++) {
				int i4 = ai1[j1];
				int j4 = l3 + i4;
				int k4 = j3 + i3 * i4;
				int l4 = k3 - l2 * i4;
				for (k1 = -ai[j1]; k1 < 0; k1++) {
					int x1 = k4 >> 16;
					int y1 = l4 >> 16;
					int x2 = x1 + 1;
					int y2 = y1 + 1;
					int c1 = raster[x1 + y1 * width];
					int c2 = raster[x2 + y1 * width];
					int c3 = raster[x1 + y2 * width];
					int c4 = raster[x2 + y2 * width];
					int u1 = (k4 >> 8) - (x1 << 8);
					int v1 = (l4 >> 8) - (y1 << 8);
					int u2 = (x2 << 8) - (k4 >> 8);
					int v2 = (y2 << 8) - (l4 >> 8);
					int a1 = u2 * v2;
					int a2 = u1 * v2;
					int a3 = u2 * v1;
					int a4 = u1 * v1;
					int r = (c1 >> 16 & 0xff) * a1 + (c2 >> 16 & 0xff) * a2 + (c3 >> 16 & 0xff) * a3 + (c4 >> 16 & 0xff) * a4 & 0xff0000;
					int g = (c1 >> 8 & 0xff) * a1 + (c2 >> 8 & 0xff) * a2 + (c3 >> 8 & 0xff) * a3 + (c4 >> 8 & 0xff) * a4 >> 8 & 0xff00;
					int b = (c1 & 0xff) * a1 + (c2 & 0xff) * a2 + (c3 & 0xff) * a3 + (c4 & 0xff) * a4 >> 16;
					pixels[j4++] = r | g | b;
					k4 += i3;
					l4 -= l2;
				}

				j3 += l2;
				k3 += i3;
				l3 += Raster.width;
			}

		} catch (Exception _ex) {
		}
	}

	public void method353(int i, double d, int l1) {
		// all of the following were parameters
		int j = 15;
		int k = 20;
		int l = 15;
		int j1 = 256;
		int k1 = 20;
		// all of the previous were parameters
		try {
			int i2 = -k / 2;
			int j2 = -k1 / 2;
			int k2 = (int) (Math.sin(d) * 65536D);
			int l2 = (int) (Math.cos(d) * 65536D);
			k2 = k2 * j1 >> 8;
			l2 = l2 * j1 >> 8;
			int i3 = (l << 16) + (j2 * k2 + i2 * l2);
			int j3 = (j << 16) + (j2 * l2 - i2 * k2);
			int k3 = l1 + i * Raster.width;
			for (i = 0; i < k1; i++) {
				int l3 = k3;
				int i4 = i3;
				int j4 = j3;
				for (l1 = -k; l1 < 0; l1++) {
					int k4 = raster[(i4 >> 16) + (j4 >> 16) * width];
					if (k4 != 0)
						pixels[l3++] = k4;
					else
						l3++;
					i4 += l2;
					j4 -= k2;
				}

				i3 += k2;
				j3 += l2;
				k3 += Raster.width;
			}

		} catch (Exception _ex) {
		}
	}

	public Sprite(byte spriteData[]) {
		try {
			Image image = Toolkit.getDefaultToolkit().createImage(spriteData);
			ImageIcon sprite = new ImageIcon(image);
			width = sprite.getIconWidth();
			height = sprite.getIconHeight();
			resizeWidth = width;
			resizeHeight = height;
			offsetX = 0;
			offsetY = 0;
			raster = new int[width * height];
			PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, width, height, raster, 0, width);
			pixelgrabber.grabPixels();
			image = null;
			setTransparency(255, 0, 255);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void method354(Background background, int i, int j) {
		j += offsetX;
		i += offsetY;
		int k = j + i * Raster.width;
		int l = 0;
		int i1 = height;
		int j1 = width;
		int k1 = Raster.width - j1;
		int l1 = 0;
		if (i < topY) {
			int i2 = topY - i;
			i1 -= i2;
			i = topY;
			l += i2 * j1;
			k += i2 * Raster.width;
		}
		if (i + i1 > bottomY)
			i1 -= (i + i1) - bottomY;
		if (j < topX) {
			int j2 = topX - j;
			j1 -= j2;
			j = topX;
			l += j2;
			k += j2;
			l1 += j2;
			k1 += j2;
		}
		if (j + j1 > bottomX) {
			int k2 = (j + j1) - bottomX;
			j1 -= k2;
			l1 += k2;
			k1 += k2;
		}
		if (!(j1 <= 0 || i1 <= 0)) {
			method355(raster, j1, background.aByteArray1450, i1, pixels, 0, k1, k, l1, l);
		}
	}

	public void drawARGBSprite(int xPos, int yPos) {
		drawARGBSprite(xPos, yPos, 256);
	}

	public void drawARGBSprite(int xPos, int yPos, int alpha) {
		int alphaValue = alpha;
		xPos += offsetX;
		yPos += offsetY;
		int i1 = xPos + yPos * Raster.width;
		int j1 = 0;
		int spriteHeight = height;
		int spriteWidth = width;
		int i2 = Raster.width - spriteWidth;
		int j2 = 0;
		if (yPos < topY) {
			int k2 = topY - yPos;
			spriteHeight -= k2;
			yPos = topY;
			j1 += k2 * spriteWidth;
			i1 += k2 * Raster.width;
		}
		if (yPos + spriteHeight > bottomY)
			spriteHeight -= (yPos + spriteHeight) - bottomY;
		if (xPos < topX) {
			int l2 = topX - xPos;
			spriteWidth -= l2;
			xPos = topX;
			j1 += l2;
			i1 += l2;
			j2 += l2;
			i2 += l2;
		}
		if (xPos + spriteWidth > bottomX) {
			int i3 = (xPos + spriteWidth) - bottomX;
			spriteWidth -= i3;
			j2 += i3;
			i2 += i3;
		}
		if (!(spriteWidth <= 0 || spriteHeight <= 0)) {
			renderARGBPixels(spriteWidth, spriteHeight, raster, pixels, i1, alphaValue, j1, j2, i2);
		}
	}

	private void renderARGBPixels(int spriteWidth, int spriteHeight, int spritePixels[], int renderAreaPixels[], int pixel, int alphaValue, int i, int l, int j1) {
		int pixelColor;
		int alphaLevel;
		int alpha = alphaValue;
		for (int height = -spriteHeight; height < 0; height++) {
			for (int width = -spriteWidth; width < 0; width++) {
				alphaValue = ((raster[i] >> 24) & (alpha - 1));
				alphaLevel = 256 - alphaValue;
				if (alphaLevel > 256) {
					alphaValue = 0;
				}
				if (alpha == 0) {
					alphaLevel = 256;
					alphaValue = 0;
				}
				pixelColor = spritePixels[i++];
				if (pixelColor != 0) {
					int pixelValue = renderAreaPixels[pixel];
					renderAreaPixels[pixel++] = ((pixelColor & 0xff00ff) * alphaValue + (pixelValue & 0xff00ff) * alphaLevel & 0xff00ff00) + ((pixelColor & 0xff00) * alphaValue + (pixelValue & 0xff00) * alphaLevel & 0xff0000) >> 8;
				} else {
					pixel++;
				}
			}
			pixel += j1;
			i += l;
		}
	}

	private void method355(int ai[], int i, byte abyte0[], int j, int ai1[], int k, int l, int i1, int j1, int k1) {
		int l1 = -(i >> 2);
		i = -(i & 3);
		for (int j2 = -j; j2 < 0; j2++) {
			for (int k2 = l1; k2 < 0; k2++) {
				k = ai[k1++];
				if (k != 0 && abyte0[i1] == 0)
					ai1[i1++] = k;
				else
					i1++;
				k = ai[k1++];
				if (k != 0 && abyte0[i1] == 0)
					ai1[i1++] = k;
				else
					i1++;
				k = ai[k1++];
				if (k != 0 && abyte0[i1] == 0)
					ai1[i1++] = k;
				else
					i1++;
				k = ai[k1++];
				if (k != 0 && abyte0[i1] == 0)
					ai1[i1++] = k;
				else
					i1++;
			}

			for (int l2 = i; l2 < 0; l2++) {
				k = ai[k1++];
				if (k != 0 && abyte0[i1] == 0)
					ai1[i1++] = k;
				else
					i1++;
			}

			i1 += l;
			k1 += j1;
		}

	}

	public void outline(int color) {
		int[] raster = new int[width * height];
		int start = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int outline = pixels[start];
				if (outline == 0) {
					if (x > 0 && pixels[start - 1] != 0) {
						outline = color;
					} else if (y > 0 && pixels[start - width] != 0) {
						outline = color;
					} else if (x < width - 1 && pixels[start + 1] != 0) {
						outline = color;
					} else if (y < width - 1 && pixels[start + width] != 0) {
						outline = color;
					}
				}
				raster[start++] = outline;
			}
		}
		pixels = raster;
	}

	public void shadow(int color) {
		for (int y = height - 1; y > 0; y--) {
			int pos = y * width;
			for (int x = width - 1; x > 0; x--) {
				if (pixels[x + pos] == 0 && pixels[x + pos - 1 - width] != 0) {
					pixels[x + pos] = color;
				}
			}
		}
	}

}
