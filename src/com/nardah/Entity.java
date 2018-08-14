package com.nardah;
// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.

// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

public class Entity extends Renderable {

	public final void setPos(int i, int j, boolean flag) {
		if (emoteAnimation != -1 && Animation.animations[emoteAnimation].walkingPrecedence == 1)
			emoteAnimation = -1;
		if (!flag) {
			int k = i - smallX[0];
			int l = j - smallY[0];
			if (k >= -8 && k <= 8 && l >= -8 && l <= 8) {
				if (remainingPath < 9)
					remainingPath++;
				for (int i1 = remainingPath; i1 > 0; i1--) {
					smallX[i1] = smallX[i1 - 1];
					smallY[i1] = smallY[i1 - 1];
					aBooleanArray1553[i1] = aBooleanArray1553[i1 - 1];
				}

				smallX[0] = i;
				smallY[0] = j;
				aBooleanArray1553[0] = false;
				return;
			}
		}
		remainingPath = 0;
		anInt1542 = 0;
		anInt1503 = 0;
		smallX[0] = i;
		smallY[0] = j;
		x = smallX[0] * 128 + size * 64;
		y = smallY[0] * 128 + size * 64;
	}

	public final void resetPath() {
		remainingPath = 0;
		anInt1542 = 0;
	}

	public double[] hitmarkMove = new double[4];
	public int[] hitmarkTrans = new int[4];
	public int[] hitIcon = new int[4];

	public final void damage(int markType, int damage, int l, int icon) {
		int mark = markType;

		if (Settings.HITSPLAT == 0 || Settings.HITSPLAT == 1) {
			if (mark == 4) {
				mark = 1;
			}
		}

		for (int index = 0; index < 4; index++) {
			if (hitsLoopCycle[index] <= l) {
				hitIcon[index] = icon;
				hitmarkMove[index] = 5;
				hitmarkTrans[index] = 230;
				hitArray[index] = Settings.DAMAGE_MULTIPLIER ? damage * 10 : damage;
				if (damage > 0 && Settings.DAMAGE_MULTIPLIER) {
					hitArray[index] += new java.util.Random().nextInt(9);
				}
				hitMarkTypes[index] = mark;
				hitsLoopCycle[index] = l + 70;
				return;
			}
		}
	}

	public final void moveInDir(boolean flag, int i) {
		int j = smallX[0];
		int k = smallY[0];
		if (i == 0) {
			j--;
			k++;
		}
		if (i == 1)
			k++;
		if (i == 2) {
			j++;
			k++;
		}
		if (i == 3)
			j--;
		if (i == 4)
			j++;
		if (i == 5) {
			j--;
			k--;
		}
		if (i == 6)
			k--;
		if (i == 7) {
			j++;
			k--;
		}
		if (emoteAnimation != -1 && Animation.animations[emoteAnimation].walkingPrecedence == 1)
			emoteAnimation = -1;
		if (remainingPath < 9)
			remainingPath++;
		for (int l = remainingPath; l > 0; l--) {
			smallX[l] = smallX[l - 1];
			smallY[l] = smallY[l - 1];
			aBooleanArray1553[l] = aBooleanArray1553[l - 1];
		}
		smallX[0] = j;
		smallY[0] = k;
		aBooleanArray1553[0] = flag;
	}

	public int entScreenX;
	public int entScreenY;
	public final int index = -1;

	public boolean isVisible() {
		return false;
	}

	Entity() {
		smallX = new int[10];
		smallY = new int[10];
		interactingEntity = -1;
		rotation = 32;
		runAnimation = -1;
		height = 200;
		idleAnimation = -1;
		turnAnimation = -1;
		hitArray = new int[4];
		hitMarkTypes = new int[4];
		hitsLoopCycle = new int[4];
		anInt1517 = -1;
		graphic = -1;
		emoteAnimation = -1;
		cycleStatus = -1000;
		textCycle = 100;
		size = 1;
		aBoolean1541 = false;
		aBooleanArray1553 = new boolean[10];
		walkingAnimation = -1;
		halfTurnAnimation = -1;
		quarterClockwiseTurnAnimation = -1;
		quarterAnticlockwiseTurnAnimation = -1;
	}

	public final int[] smallX;
	public final int[] smallY;
	public int interactingEntity;
	int anInt1503;
	int rotation;
	int runAnimation;
	public String textSpoken;
	public int height;
	public int turnDirection;
	int idleAnimation;
	int turnAnimation;
	int textColor;
	final int[] hitArray;
	final int[] hitMarkTypes;
	final int[] hitsLoopCycle;
	int anInt1517;
	int anInt1518;
	int anInt1519;
	int graphic;
	int currentAnimation;
	int anInt1522;
	int graphicDelay;
	int graphicHeight;
	int remainingPath;
	public int emoteAnimation;
	int displayedEmoteFrames;
	int emoteTimeRemaining;
	int animationDelay;
	int currentAnimationLoops;
	int textEffect;
	public int cycleStatus;
	public int currentHealth;
	public int maximumHealth;
	int textCycle;
	int time;
	int faceX;
	int faceY;
	int size;
	boolean aBoolean1541;
	int anInt1542;
	int initialX;
	int destinationX;
	int initialY;
	int destinationY;
	int startForceMovement;
	int endForceMovement;
	int direction;
	public int x;
	public int y;
	int anInt1552;
	final boolean[] aBooleanArray1553;
	int walkingAnimation;
	int halfTurnAnimation;
	int quarterClockwiseTurnAnimation;
	int quarterAnticlockwiseTurnAnimation;

	public int nextAnimFrame;
	public int nextGraphicFrame;
	public int nextIdleFrame;
}
