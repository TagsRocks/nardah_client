package com.nardah;

public final class Varp {

	public static void unpackConfig(StreamLoader archive) {
		Buffer buffer = new Buffer(archive.getDataForName("varp.dat"));
		anInt702 = 0;
		int varpCount = buffer.readUShort();

		System.out.println(String.format("Loaded: %d varps", varpCount));

		if (varps == null) {
			varps = new Varp[varpCount];
		}

		if (anIntArray703 == null) {
			anIntArray703 = new int[varpCount];
		}
		for (int i = 0; i < varpCount; i++) {
			if (varps[i] == null) {
				varps[i] = new Varp();
			}

			varps[i].decode(buffer, i);
		}

		if (buffer.currentOffset != buffer.buffer.length) {
			System.out.println("varp mismatch!");
		}
	}

	private void decode(Buffer stream, int i) {
		while(true) {
			int opcode = stream.readUByte();
			if (opcode == 0) {
				return;
			} else if (opcode == 1) {
				stream.readUByte();
			} else if (opcode == 2) {
				stream.readUByte();
			} else if (opcode == 3) {
				anIntArray703[anInt702++] = i;
			} else if (opcode == 4) {

			} else if (opcode == 5) {
				parameter = stream.readUShort();
			} else if (opcode == 6) {

			} else if (opcode == 7) {
				stream.readInt();
			} else if (opcode == 8) {
				aBoolean713 = true;
			} else if (opcode == 10) {
				stream.readString();
			} else if (opcode == 11) {
				aBoolean713 = true;
			} else if (opcode == 12) {
				stream.readInt();
			} else if (opcode == 13) {
			} else {
				System.out.println("varp invalid opcode: " + opcode);
			}
		}
	}

	private Varp() {
		aBoolean713 = false;
	}

	public static Varp varps[];
	private static int anInt702;
	private static int[] anIntArray703;
	public int parameter;
	public boolean aBoolean713;

}
