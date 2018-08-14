package com.nardah;

import java.util.*;

class ParticleAttachment {
	private static final Map<Integer, int[][]> attachments = new HashMap<>();

	static int[][] getAttachments(int model) {
		return attachments.get(model);
	}

	static {
		attachments.put(29616, new int[][]{{272, 0}, {269, 0}, {49, 0}, {45, 0}, {37, 0}, {16, 0}, {17, 0}, {5, 0}, {41, 0}, {283, 0}, {310, 0}, {315, 0}});
		attachments.put(29624, new int[][]{{49, 0}, {45, 0}, {37, 0}, {16, 0}, {17, 0}, {5, 0}, {41, 0}, {283, 0}, {310, 0}, {315, 0}});
	}
}
