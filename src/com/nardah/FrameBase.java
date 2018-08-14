package com.nardah;

public final class FrameBase {

	public final int length;
	final int[] transformationType;
	final int[][] skinList;

	FrameBase(Buffer buffer) {
		length = buffer.readUShort();
		transformationType = new int[length];
		skinList = new int[length][];

		for (int i = 0; i < length; i++) {
			transformationType[i] = buffer.readUShort();
		}

		for (int i = 0; i < length; i++) {
			skinList[i] = new int[buffer.readUShort()];
		}

		for (int i = 0; i < length; i++) {
			for (int j = 0; j < skinList[i].length; j++) {
				skinList[i][j] = buffer.readUShort();
			}
		}
	}
}