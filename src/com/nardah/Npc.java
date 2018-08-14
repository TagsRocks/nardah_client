package com.nardah;

public final class Npc extends Entity {

	private Model method450() {
		int currAnim = -1;
		int nextAnim = -1;
		int currCycle = -1;
		int nextCycle = -1;
		if (super.emoteAnimation >= 0 && super.animationDelay == 0) {
			final Animation seq = Animation.animations[super.emoteAnimation];
			if (Settings.TWEENING && super.nextAnimFrame != -1) {
				nextAnim = seq.primaryFrames[super.nextAnimFrame];
				currCycle = seq.durations[super.displayedEmoteFrames];
				nextCycle = super.emoteTimeRemaining;
			}
			currAnim = seq.primaryFrames[super.displayedEmoteFrames];
			int idleAnim = -1;
			if (super.anInt1517 >= 0 && super.anInt1517 != super.idleAnimation) {
				idleAnim = Animation.animations[super.anInt1517].primaryFrames[super.anInt1518];
			}
			return definition.method164(idleAnim, currAnim, nextAnim, currCycle, nextCycle, Animation.animations[super.emoteAnimation].interleaveOrder);
		}
		if (super.anInt1517 >= 0) {
			final Animation seq = Animation.animations[super.anInt1517];
			currAnim = seq.primaryFrames[super.anInt1518];
			if (Settings.TWEENING && super.nextIdleFrame != -1) {
				nextAnim = seq.primaryFrames[super.nextIdleFrame];
				currCycle = seq.durations[super.anInt1518];
				nextCycle = super.anInt1519;
			}
		}
		return definition.method164(-1, currAnim, nextAnim, currCycle, nextCycle, null);
	}

	public Model getRotatedModel() {
		if (definition == null)
			return null;
		Model model = method450();
		if (model == null)
			return null;
		super.height = model.modelHeight;
		if (super.graphic != -1 && super.currentAnimation != -1) {
			Graphic spotAnim = Graphic.cache[super.graphic];
			Model model_1 = spotAnim.getModel();
			if (model_1 != null) {
				int j = spotAnim.aAnimation_407.primaryFrames[super.currentAnimation];
				Model model_2 = new Model(true, Frame.method532(j), false, model_1);
				model_2.method475(0, -super.graphicHeight, 0);
				model_2.skin();
				model_2.method470(j);
				model_2.anIntArrayArray1658 = null;
				model_2.anIntArrayArray1657 = null;
				if (spotAnim.resizeX != 128 || spotAnim.resizeY != 128)
					model_2.method478(spotAnim.resizeX, spotAnim.resizeX, spotAnim.resizeY);
				model_2.light(64 + spotAnim.ambience, 850 + spotAnim.contrast, -30, -50, -30, true);
				Model aModel[] = {model, model_2};
				model = new Model(aModel);
			}
		}
		if (definition.size == 1)
			model.aBoolean1659 = true;
		return model;
	}

	public boolean isVisible() {
		return definition != null;
	}

	Npc() {
	}

	public NpcDefinition definition;
}
