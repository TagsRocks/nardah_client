package com.nardah;

public final class Frame {
	static Frame[][] animationlist;

	public static void load(int file, byte[] data) {
		try {
			final Buffer buffer = new Buffer(data);
			final FrameBase frame = new FrameBase(buffer);
			final int len = buffer.readUShort();
			animationlist[file] = new Frame[len * 3];
			final int[] array2 = new int[500];
			final int[] array3 = new int[500];
			final int[] array4 = new int[500];
			final int[] array5 = new int[500];
			for (int j = 0; j < len; ++j) {
				final int k = buffer.readUShort();
				final Frame[] array6 = animationlist[file];
				final int n2 = k;
				final Frame q = new Frame();
				array6[n2] = q;
				final Frame q2 = q;
				q.aClass18_637 = frame;
				final int f = buffer.readUByte();
				int c2 = 0;
				int n3 = -1;
				for (int l = 0; l < f; ++l) {
					final int f2;
					if ((f2 = buffer.readUByte()) > 0) {
						if (frame.transformationType[l] != 0) {
							for (int n4 = l - 1; n4 > n3; --n4) {
								if (frame.transformationType[n4] == 0) {
									array2[c2] = n4;
									array3[c2] = 0;
									array5[c2] = (array4[c2] = 0);
									++c2;
									break;
								}
							}
						}
						array2[c2] = l;
						int n4 = 0;
						if (frame.transformationType[l] == 3) {
							n4 = 128;
						}
						if ((f2 & 0x1) != 0x0) {
							array3[c2] = buffer.readShort2();
						} else {
							array3[c2] = n4;
						}
						if ((f2 & 0x2) != 0x0) {
							array4[c2] = buffer.readShort2();
						} else {
							array4[c2] = n4;
						}
						if ((f2 & 0x4) != 0x0) {
							array5[c2] = buffer.readShort2();
						} else {
							array5[c2] = n4;
						}
						n3 = l;
						++c2;
					}
				}
				q2.anInt638 = c2;
				q2.transformationIndices = new int[c2];
				q2.transformX = new int[c2];
				q2.transformY = new int[c2];
				q2.transformZ = new int[c2];
				for (int l = 0; l < c2; ++l) {
					q2.transformationIndices[l] = array2[l];
					q2.transformX[l] = array3[l];
					q2.transformY[l] = array4[l];
					q2.transformZ[l] = array5[l];
				}
			}
		} catch (Exception ex) {
		}
	}

	public static void load600Data(int file, byte[] array) {
		try {
			Buffer stream = new Buffer(array);
			FrameBase class18 = new FrameBase(stream);
			int k1 = stream.readUShort();
			animationlist[file] = new Frame[(int) (k1 * 3.0)];
			int ai[] = new int[500];
			int ai1[] = new int[500];
			int ai2[] = new int[500];
			int ai3[] = new int[500];
			for (int l1 = 0; l1 < k1; l1++) {
				int i2 = stream.readUShort();
				Frame class36 = animationlist[file][i2] = new Frame();
				class36.aClass18_637 = class18;
				int j2 = stream.readUByte();
				int l2 = 0;
				int k2 = -1;
				for (int i3 = 0; i3 < j2; i3++) {
					int j3 = stream.readUByte();
					if (j3 > 0) {
						if (class18.transformationType[i3] != 0) {
							for (int l3 = i3 - 1; l3 > k2; l3--) {
								if (class18.transformationType[l3] != 0)
									continue;
								ai[l2] = l3;
								ai1[l2] = 0;
								ai2[l2] = 0;
								ai3[l2] = 0;
								l2++;
								break;
							}
						}
						ai[l2] = i3;
						short c = 0;
						if (class18.transformationType[i3] == 3) {
							c = (short) 128;
						}
						if ((j3 & 1) != 0) {
							ai1[l2] = (short) stream.readShort2();
						} else {
							ai1[l2] = c;
						}
						if ((j3 & 2) != 0) {
							ai2[l2] = stream.readShort2();
						} else {
							ai2[l2] = c;
						}
						if ((j3 & 4) != 0) {
							ai3[l2] = stream.readShort2();
						} else {
							ai3[l2] = c;
						}
						k2 = i3;
						l2++;
					}
				}
				class36.anInt638 = l2;
				class36.transformationIndices = new int[l2];
				class36.transformX = new int[l2];
				class36.transformY = new int[l2];
				class36.transformZ = new int[l2];
				for (int k3 = 0; k3 < l2; k3++) {
					class36.transformationIndices[k3] = ai[k3];
					class36.transformX[k3] = ai1[k3];
					class36.transformY[k3] = ai2[k3];
					class36.transformZ[k3] = ai3[k3];
				}
			}
		} catch (Exception exception) {
		}
	}

	public static void loader(int file, byte[] abyte0) {
		try {
			Buffer stream = new Buffer(abyte0);
			FrameBase class18 = new FrameBase(stream);
			int k1 = stream.readUShort();
			animationlist[file] = new Frame[k1 * 3];
			int ai[] = new int[500];
			int ai1[] = new int[500];
			int ai2[] = new int[500];
			int ai3[] = new int[500];
			for (int l1 = 0; l1 < k1; l1++) {
				int i2 = stream.readUShort();
				Frame class36 = animationlist[file][i2] = new Frame();
				class36.aClass18_637 = class18;
				class36.aClass18_637 = class18;

				int j2 = stream.readUByte();
				int l2 = 0;
				int k2 = -1;
				for (int i3 = 0; i3 < j2; i3++) {
					int j3 = stream.readUByte();
					if (j3 > 0) {
						if (class18.transformationType[i3] != 0) {
							for (int l3 = i3 - 1; l3 > k2; l3--) {
								if (class18.transformationType[l3] != 0)
									continue;
								ai[l2] = l3;
								ai1[l2] = 0;
								ai2[l2] = 0;
								ai3[l2] = 0;
								l2++;
								break;
							}
						}
						ai[l2] = i3;
						short c = 0;
						if (class18.transformationType[i3] == 3)
							c = (short) 128;

						if ((j3 & 1) != 0)
							ai1[l2] = stream.readShort2();
						else
							ai1[l2] = c;
						if ((j3 & 2) != 0)
							ai2[l2] = stream.readShort2();
						else
							ai2[l2] = c;
						if ((j3 & 4) != 0)
							ai3[l2] = stream.readShort2();
						else
							ai3[l2] = c;
						k2 = i3;
						l2++;
					}
				}
				class36.anInt638 = l2;
				class36.transformationIndices = new int[l2];
				class36.transformX = new int[l2];
				class36.transformY = new int[l2];
				class36.transformZ = new int[l2];
				for (int k3 = 0; k3 < l2; k3++) {
					class36.transformationIndices[k3] = ai[k3];
					class36.transformX[k3] = ai1[k3];
					class36.transformY[k3] = ai2[k3];
					class36.transformZ[k3] = ai3[k3];
				}
			}
		} catch (Exception exception) {
		}
	}

	static void nullLoader() {
		animationlist = null;
	}

	static Frame method531(int int1) {
		try {
			int file = int1 >> 16;
			int frame = int1 & 0xFFFF;
			if (animationlist[file].length == 0) {
				Client.instance.onDemandFetcher.provide(1, file);
				return null;
			}
			if (animationlist[file][frame] == null) {
				return null;
			}
			return animationlist[file][frame];
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	static boolean method532(int i) {
		return i == -1;
	}

	public Frame() {
	}

	int anInt636;
	FrameBase aClass18_637;
	int anInt638;
	int transformationIndices[];
	int transformX[];
	int transformY[];
	int transformZ[];

}
