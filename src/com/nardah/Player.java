package com.nardah;

import static com.nardah.Model.modelIntArray3;

/**
 * The player class.
 *
 * @author Daniel
 */
public final class Player extends Entity {

	String title;
	String clanTag;
	String clanChannel;
	String cachedName;
	String clanTagColor;
	int titleColor;
	private long aLong1697;
	NpcDefinition desc;
	boolean aBoolean1699;
	final int[] appearanceColors;
	private int defaultParticleColor = 0xFFFFFF;
	private int maxCapeParticleColor = defaultParticleColor;
	int team;
	private int gender;
	public String name;
	static Cache mruNodes = new Cache(260);
	double combatLevel;
	int headIcon;
	int skullIcon;
	int bountyIcon;
	int hintIcon;
	int anInt1707;
	int anInt1708;
	int anInt1709;
	boolean visible;
	int anInt1711;
	int anInt1712;
	int anInt1713;
	Model aModel_1714;
	final int[] appearanceModels;
	private long appearanceHash;
	int anInt1719;
	int anInt1720;
	int anInt1721;
	int anInt1722;
	int skill;

	Player() {
		aLong1697 = -1L;
		aBoolean1699 = false;
		appearanceColors = new int[5];
		visible = false;
		appearanceModels = new int[12];
	}

	public Model getRotatedModel() {

		if (!visible) {
			return null;
		}

		Model animatedModel = method452();

		if (animatedModel == null) {
			return null;
		}

		super.height = animatedModel.modelHeight;
		animatedModel.aBoolean1659 = true;

		if (aBoolean1699) {
			return animatedModel;
		}

		if (super.graphic != -1 && super.currentAnimation != -1) {
			Graphic spotAnim = Graphic.cache[super.graphic];

			Model spotAnimationModel = spotAnim.getModel();

			/**
			 * MAKE SURE WE'VE LOADED THE GRAPHIC BEFORE ATTEMPTING TO DO IT.
			 * Fixes graphics flickering.
			 */
			if (Frame.animationlist[spotAnim.aAnimation_407.primaryFrames[0] >> 16].length == 0) {
				spotAnimationModel = null;
			}

			if (spotAnimationModel != null) {

				Model model_3 = new Model(true, Frame.method532(super.currentAnimation), false, spotAnimationModel);
				model_3.method475(0, -super.graphicHeight, 0);
				model_3.skin();
				model_3.method470(spotAnim.aAnimation_407.primaryFrames[super.currentAnimation]);
				model_3.anIntArrayArray1658 = null;
				model_3.anIntArrayArray1657 = null;
				if (spotAnim.resizeX != 128 || spotAnim.resizeY != 128)
					model_3.method478(spotAnim.resizeX, spotAnim.resizeX, spotAnim.resizeY);
				model_3.light(64 + spotAnim.ambience, 850 + spotAnim.contrast, -30, -50, -30, true);
				Model models[] = {animatedModel, model_3};
				animatedModel = new Model(models);
			}
		}

		if (aModel_1714 != null) {
			if (Client.tick >= anInt1708)
				aModel_1714 = null;
			if (Client.tick >= anInt1707 && Client.tick < anInt1708) {
				Model model_1 = aModel_1714;
				assert model_1 != null;
				model_1.method475(anInt1711 - super.x, anInt1712 - anInt1709, anInt1713 - super.y);
				if (super.turnDirection == 512) {
					model_1.method473();
					model_1.method473();
					model_1.method473();
				} else if (super.turnDirection == 1024) {
					model_1.method473();
					model_1.method473();
				} else if (super.turnDirection == 1536)
					model_1.method473();
				Model models[] = {animatedModel, model_1};
				animatedModel = new Model(models);
				if (super.turnDirection == 512)
					model_1.method473();
				else if (super.turnDirection == 1024) {
					model_1.method473();
					model_1.method473();
				} else if (super.turnDirection == 1536) {
					model_1.method473();
					model_1.method473();
					model_1.method473();
				}
				model_1.method475(super.x - anInt1711, anInt1709 - anInt1712, super.y - anInt1713);
			}
		}
		animatedModel.aBoolean1659 = true;
		return animatedModel;
	}

	void updateAppearance(Buffer stream) {
		stream.currentOffset = 0;
		gender = stream.readUByte();
		headIcon = stream.readUByte();
		skullIcon = stream.readUByte();
		bountyIcon = stream.readUByte();
		desc = null;
		team = 0;
		for (int bodyPart = 0; bodyPart < 12; bodyPart++) {
			int reset = stream.readUByte();
			if (reset == 0) {
				appearanceModels[bodyPart] = 0;
				continue;
			}
			int id = stream.readUByte();

			appearanceModels[bodyPart] = (reset << 8) + id;
			if (bodyPart == 0 && appearanceModels[0] == 65535) {
				desc = NpcDefinition.lookup(stream.readUShort());
				break;
			}

			if (appearanceModels[bodyPart] >= 512 && appearanceModels[bodyPart] - 512 < ItemDefinition.totalItems) {
				int team = ItemDefinition.lookup(appearanceModels[bodyPart] - 512).team;
				if (team != 0) {
					this.team = team;
				}
			}
		}

		for (int part = 0; part < 5; part++) {
			int color = stream.readUByte();
			if (color < 0 || color >= Client.PLAYER_BODY_RECOLOURS[part].length) {
				color = 0;
			}
			appearanceColors[part] = color;
		}

		int equip = appearanceModels[1];
		if (equip >= 512) {
			ItemDefinition def = ItemDefinition.lookup(equip - 512);
			if (maxCapeIds(equip - 512)) {
				this.maxCapeParticleColor = (def.originalModelColors == null || def.modifiedModelColors == null) ? defaultParticleColor : modelIntArray3[def.originalModelColors[2]];
			}

			if (maxCapeParticleColor == 10000525) {
				maxCapeParticleColor = 0;
			} else if (maxCapeParticleColor == 3288622) {
				maxCapeParticleColor = 7622056;
			}
			//System.out.println("particle color: "+maxCapeParticleColor);
		}

		super.idleAnimation = stream.readUShort();
		if (super.idleAnimation == 65535) {
			super.idleAnimation = -1;
		}

		super.turnAnimation = stream.readUShort();
		if (super.turnAnimation == 65535) {
			super.turnAnimation = -1;
		}

		super.walkingAnimation = stream.readUShort();
		if (super.walkingAnimation == 65535) {
			super.walkingAnimation = -1;
		}

		super.halfTurnAnimation = stream.readUShort();
		if (super.halfTurnAnimation == 65535) {
			super.halfTurnAnimation = -1;
		}

		super.quarterClockwiseTurnAnimation = stream.readUShort();
		if (super.quarterClockwiseTurnAnimation == 65535) {
			super.quarterClockwiseTurnAnimation = -1;
		}

		super.quarterAnticlockwiseTurnAnimation = stream.readUShort();
		if (super.quarterAnticlockwiseTurnAnimation == 65535) {
			super.quarterAnticlockwiseTurnAnimation = -1;
		}

		super.runAnimation = stream.readUShort();
		if (super.runAnimation == 65535) {
			super.runAnimation = -1;
		}
		name = StringUtils.fixName(StringUtils.nameForLong(stream.readLong()));
		title = StringUtils.fixName(stream.readString());
		titleColor = stream.readInt();
		clanChannel = stream.readString();
		clanTag = stream.readString();
		clanTagColor = stream.readString();
		cachedName = "<col=" + Integer.toHexString(titleColor) + ">" + " " + title + "</col> " + name;
		combatLevel = Double.longBitsToDouble(stream.readLong());
		privelage = stream.readUByte();
		skill = stream.readUShort();
		visible = true;
		appearanceHash = 0L;

		for (int k1 = 0; k1 < 12; k1++) {
			appearanceHash <<= 4;
			if (appearanceModels[k1] >= 256)
				appearanceHash += appearanceModels[k1] - 256;
		}

		if (appearanceModels[0] >= 256) {
			appearanceHash += appearanceModels[0] - 256 >> 4;
		}

		if (appearanceModels[1] >= 256) {
			appearanceHash += appearanceModels[1] - 256 >> 8;
		}

		for (int i2 = 0; i2 < 5; i2++) {
			appearanceHash <<= 3;
			appearanceHash += appearanceColors[i2];
		}

		appearanceHash <<= 1;
		appearanceHash += gender;

		appearanceHash += this.maxCapeParticleColor;
	}

	public boolean maxCapeIds(int itemId) {
		return ((itemId == 21285) || (itemId == 13329) || (itemId == 13280) || (itemId == 13331) || (itemId == 13335) || (itemId == 13337) || (itemId == 13333) || (itemId == 20760));
	}

	Model method452() {
		long bitset = appearanceHash;
		int i1 = -1;
		int j1 = -1;
		int k1 = -1;
		int currAnim = -1;
		int nextAnim = -1;
		int currCycle = -1;
		int nextCycle = -1;
		if (desc != null) {

			if (super.emoteAnimation >= 0 && super.animationDelay == 0) {
				final Animation seq = Animation.animations[super.emoteAnimation];
				currAnim = seq.primaryFrames[super.displayedEmoteFrames];
				if (Settings.TWEENING && super.nextAnimFrame != -1) {
					nextAnim = seq.primaryFrames[super.displayedEmoteFrames];
					currCycle = seq.durations[super.displayedEmoteFrames];
					nextCycle = super.emoteTimeRemaining;
				}
			} else if (super.anInt1517 >= 0) {
				final Animation seq = Animation.animations[super.anInt1517];
				currAnim = seq.primaryFrames[super.anInt1518];
				if (Settings.TWEENING && super.nextIdleFrame != -1) {
					nextAnim = seq.primaryFrames[super.nextIdleFrame];
					currCycle = seq.durations[super.anInt1518];
					nextCycle = super.anInt1519;
				}
			}
			Model model = desc.method164(-1, currAnim, nextAnim, currCycle, nextCycle, null);
			return model;
		}
		if (super.emoteAnimation >= 0 && super.animationDelay == 0) {
			Animation animation = Animation.animations[super.emoteAnimation];
			currAnim = animation.primaryFrames[super.displayedEmoteFrames];
			if (Settings.TWEENING && super.nextAnimFrame != -1) {
				nextAnim = animation.primaryFrames[super.nextAnimFrame];
				currCycle = animation.durations[super.displayedEmoteFrames];
				nextCycle = super.emoteTimeRemaining;
			}
			if (super.anInt1517 >= 0 && super.anInt1517 != super.idleAnimation) {
				i1 = Animation.animations[super.anInt1517].primaryFrames[super.anInt1518];
			}
			if (animation.playerOffhand >= 0) {
				j1 = animation.playerOffhand;
				bitset += j1 - appearanceModels[5] << 40;
			}
			if (animation.playerMainhand >= 0) {
				k1 = animation.playerMainhand;
				bitset += k1 - appearanceModels[3] << 48;
			}
		} else if (super.anInt1517 >= 0) {
			final Animation seq = Animation.animations[super.anInt1517];
			currAnim = seq.primaryFrames[super.anInt1518];
			if (Settings.TWEENING && super.nextIdleFrame != -1) {
				nextAnim = seq.primaryFrames[super.nextIdleFrame];
				currCycle = seq.durations[super.anInt1518];
				nextCycle = super.anInt1519;
			}
		}
		Model model_1 = (Model) mruNodes.get(bitset);
		if (model_1 == null) {
			boolean flag = false;
			for (int i2 = 0; i2 < 12; i2++) {
				int k2 = appearanceModels[i2];
				if (k1 >= 0 && i2 == 3)
					k2 = k1;
				if (j1 >= 0 && i2 == 5)
					k2 = j1;
				if (k2 >= 256 && k2 < 512 && !IdentityKit.kits[k2 - 256].bodyLoaded())
					flag = true;
				if (k2 >= 512 && !ItemDefinition.lookup(k2 - 512).method195(gender))
					flag = true;
			}

			if (flag) {
				if (aLong1697 != -1L)
					model_1 = (Model) mruNodes.get(aLong1697);
				if (model_1 == null)
					return null;
			}
		}
		boolean hasMaxCape = false;
		if (model_1 == null) {
			Model aclass30_sub2_sub4_sub6s[] = new Model[12];
			int j2 = 0;
			for (int l2 = 0; l2 < 12; l2++) {
				int i3 = appearanceModels[l2];
				if (k1 >= 0 && l2 == 3)
					i3 = k1;
				if (j1 >= 0 && l2 == 5)
					i3 = j1;
				if (i3 >= 256 && i3 < 512) {
					Model model_3 = IdentityKit.kits[i3 - 256].method538();
					if (model_3 != null)
						aclass30_sub2_sub4_sub6s[j2++] = model_3;
				}
				if (i3 >= 512) {
					int itemId = i3 - 512;
					Model model_4 = ItemDefinition.lookup(i3 - 512).method196(gender);
					if (maxCapeIds(itemId)) {
						hasMaxCape = true;
					}
					if (model_4 != null)
						aclass30_sub2_sub4_sub6s[j2++] = model_4;
				}
			}

			model_1 = new Model(j2, aclass30_sub2_sub4_sub6s);
			for (int j3 = 0; j3 < 5; j3++)
				if (appearanceColors[j3] != 0) {
					model_1.recolor(Client.PLAYER_BODY_RECOLOURS[j3][0], Client.PLAYER_BODY_RECOLOURS[j3][appearanceColors[j3]]);
					if (j3 == 1)
						model_1.recolor(Client.anIntArray1204[0], Client.anIntArray1204[appearanceColors[j3]]);
				}

			model_1.skin();
			if (Settings.CUSTOM_LIGHTING) {
				model_1.light(84, 1000, -90, -580, -90, true);
			} else {
				model_1.light(64, 850, -30, -50, -30, true);
			}
			if (hasMaxCape) {
				model_1.setParticleColors(this.maxCapeParticleColor);
			}
			mruNodes.put(model_1, bitset);
			aLong1697 = bitset;
		}
		if (aBoolean1699)
			return model_1;
		Model model_2 = Model.EMPTY_MODEL;
		model_2.method464(model_1, Frame.method532(currAnim) & Frame.method532(i1));
		if (currAnim != -1 && i1 != -1) {
			model_2.method471(Animation.animations[super.emoteAnimation].interleaveOrder, i1, currAnim);
		} else if (currAnim != -1) {
			if (Settings.TWEENING) {
				model_2.interpolateFrames(currAnim, nextAnim, nextCycle, currCycle);
			} else {
				model_2.method470(currAnim);
			}
		}
		model_2.method466();
		model_2.anIntArrayArray1658 = null;
		model_2.anIntArrayArray1657 = null;
		if (hasMaxCape) {
			model_2.setParticleColors(this.maxCapeParticleColor);
		}
		return model_2;
	}

	public boolean isVisible() {
		return visible;
	}

	public int privelage;

	public Model model() {
		if (!visible)
			return null;
		if (desc != null)
			return desc.method160();
		boolean flag = false;
		for (int i = 0; i < 12; i++) {
			int j = appearanceModels[i];
			if (j >= 256 && j < 512 && !IdentityKit.kits[j - 256].headLoaded())
				flag = true;
			if (j >= 512 && !ItemDefinition.lookup(j - 512).method192(gender))
				flag = true;
		}

		if (flag)
			return null;
		Model aclass30_sub2_sub4_sub6s[] = new Model[12];
		int k = 0;
		for (int l = 0; l < 12; l++) {
			int i1 = appearanceModels[l];
			if (i1 >= 256 && i1 < 512) {
				Model model_1 = IdentityKit.kits[i1 - 256].headModel();
				if (model_1 != null)
					aclass30_sub2_sub4_sub6s[k++] = model_1;
			}
			if (i1 >= 512) {
				Model model_2 = ItemDefinition.lookup(i1 - 512).method194(gender);
				if (model_2 != null)
					aclass30_sub2_sub4_sub6s[k++] = model_2;
			}
		}

		Model model = new Model(k, aclass30_sub2_sub4_sub6s);
		for (int j1 = 0; j1 < 5; j1++)
			if (appearanceColors[j1] != 0) {
				model.recolor(Client.PLAYER_BODY_RECOLOURS[j1][0], Client.PLAYER_BODY_RECOLOURS[j1][appearanceColors[j1]]);
				if (j1 == 1)
					model.recolor(Client.anIntArray1204[0], Client.anIntArray1204[appearanceColors[j1]]);
			}

		return model;
	}

}
