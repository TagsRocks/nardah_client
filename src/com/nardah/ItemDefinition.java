package com.nardah;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;

public final class ItemDefinition {

    public static void nullLoader() {
        mruNodes2 = null;
        spriteCache.clear();
        spriteCache = null;
        offsets = null;
        cache = null;
        dataBuf = null;
    }

    public boolean method192(int j) {
        int k = primaryMaleHeadPiece;
        int l = secondaryMaleHeadPiece;
        if (j == 1) {
            k = primaryFemaleHeadPiece;
            l = secondaryFemaleHeadPiece;
        }
        if (k == -1)
            return true;
        boolean flag = true;
        if (!Model.isCached(k))
            flag = false;
        if (l != -1 && !Model.isCached(l))
            flag = false;
        return flag;
    }

    public static void dumpList() {
        /*JsonArray array = new JsonArray();

        for (int index = 0; index < totalItems; index++) {
            try {
                ItemDefinition definition = lookup(index);

                if (definition.name == null || definition.name.equals("null") || definition.name.isEmpty())
                    continue;

                JsonObject object = new JsonObject();
                array.add(object);

                object.addProperty("id", definition.id);
                object.addProperty("name", definition.name);
                object.addProperty("examine", definition.description);

                if (definition.stackable) {
                    object.addProperty("stackable", true);
                }
                if (definition.itemActions != null) {
                    for (int idx = 0; idx < definition.itemActions.length; idx++) {
                        String action = definition.itemActions[idx];
                        if (action != null) {
                            if (action.contains("Wear") || action.contains("Wield")) {
                                object.addProperty("equipable", true);
                            }
                            if (action.contains("Destroy")) {
                                object.addProperty("destroyable", true);
                            }
                        }
                    }
                }
                if (definition.certTemplateID == -1 && definition.certID != -1) {
                    object.addProperty("noted-id", definition.certID);
                }
                if (definition.certTemplateID != -1 && definition.certID != definition.id) {
                    object.addProperty("unnoted-id", definition.certID);
                }
                if (definition.value > 1) {
                    object.addProperty("base-value", definition.value);
                }
                if (definition.itemActions != null) {
                    for (int idx = 0; idx < definition.itemActions.length; idx++) {
                        String action = definition.itemActions[idx];
                        if (action != null) {
                            if (action.contains("Wield")) {
                                object.addProperty("equipment-type", "WEAPON");
                            }
                            break;
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println(index);
                e.printStackTrace();
            }
        }

        try {
            Files.write(Paths.get("./item_dump.json"), new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(array).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public static void init(StreamLoader archive) {
        dataBuf = new Buffer(archive.getDataForName("obj.dat"));
        Buffer metaBuf = new Buffer(archive.getDataForName("obj.idx"));
        totalItems = metaBuf.readUShort();

        System.out.println(String.format("Loaded: %d items", totalItems));

        offsets = new int[totalItems];
        int offset = 2;
        for (int i = 0; i < totalItems; i++) {
            offsets[i] = offset;
            offset += metaBuf.readUShort();
        }

        cache = new ItemDefinition[10];

        for (int i = 0; i < 10; i++) {
            cache[i] = new ItemDefinition();
        }

    }

    Model method194(int j) {
        int k = primaryMaleHeadPiece;
        int l = secondaryMaleHeadPiece;
        if (j == 1) {
            k = primaryFemaleHeadPiece;
            l = secondaryFemaleHeadPiece;
        }
        if (k == -1)
            return null;
        Model model = Model.getModel(k);
        if (l != -1) {
            Model model_1 = Model.getModel(l);
            Model aclass30_sub2_sub4_sub6s[] = {model, model_1};
            model = new Model(2, aclass30_sub2_sub4_sub6s);
        }
        if (modifiedModelColors != null) {
            for (int i1 = 0; i1 < modifiedModelColors.length; i1++)
                model.recolor(modifiedModelColors[i1], originalModelColors[i1]);

        }
        return model;
    }

    public boolean method195(int j) {
        int k = primaryMaleModel;
        int l = secondaryMaleModel;
        int i1 = tertiaryMaleEquipmentModel;
        if (j == 1) {
            k = primaryFemaleModel;
            l = secondaryFemaleModel;
            i1 = tertiaryFemaleEquipmentModel;
        }
        if (k == -1)
            return true;
        boolean flag = true;
        if (!Model.isCached(k))
            flag = false;
        if (l != -1 && !Model.isCached(l))
            flag = false;
        if (i1 != -1 && !Model.isCached(i1))
            flag = false;
        return flag;
    }

    Model method196(int i) {
        int j = primaryMaleModel;
        int k = secondaryMaleModel;
        int l = tertiaryMaleEquipmentModel;
        if (i == 1) {
            j = primaryFemaleModel;
            k = secondaryFemaleModel;
            l = tertiaryFemaleEquipmentModel;
        }
        if (j == -1)
            return null;
        Model model = Model.getModel(j);
        if (k != -1)
            if (l != -1) {
                Model model_1 = Model.getModel(k);
                Model model_3 = Model.getModel(l);
                Model aclass30_sub2_sub4_sub6_1s[] = {model, model_1, model_3};
                model = new Model(3, aclass30_sub2_sub4_sub6_1s);
            } else {
                Model model_2 = Model.getModel(k);
                Model aclass30_sub2_sub4_sub6s[] = {model, model_2};
                model = new Model(2, aclass30_sub2_sub4_sub6s);
            }
        if (i == 0 && maleTranslation != 0)
            model.method475(0, maleTranslation, 0);
        if (i == 1 && femaleTranslation != 0)
            model.method475(0, femaleTranslation, 0);
        if (modifiedModelColors != null) {
            for (int i1 = 0; i1 < modifiedModelColors.length; i1++)
                model.recolor(modifiedModelColors[i1], originalModelColors[i1]);

        }
        return model;
    }

    private void setDefaults() {
        modelId = 0;
        name = "null";
        description = "";
        modifiedModelColors = null;
        originalModelColors = null;
        spriteScale = 2000;
        spritePitch = 0;
        spriteCameraRoll = 0;
        spriteCameraYaw = 0;
        spriteTranslateX = 0;
        spriteTranslateY = 0;
        stackable = false;
        value = 1;
        membersObject = false;
        groundActions = new String[]{null, null, "Take", null, null};;
        itemActions = new String[]{null, null, null, null, "Drop"};
        primaryMaleModel = -1;
        secondaryMaleModel = -1;
        maleTranslation = 0;
        primaryFemaleModel = -1;
        secondaryFemaleModel = -1;
        femaleTranslation = 0;
        tertiaryMaleEquipmentModel = -1;
        tertiaryFemaleEquipmentModel = -1;
        primaryMaleHeadPiece = -1;
        secondaryMaleHeadPiece = -1;
        primaryFemaleHeadPiece = -1;
        secondaryFemaleHeadPiece = -1;
        stackIds = null;
        stackAmounts = null;
        certID = -1;
        certTemplateID = -1;
        groundScaleX = 128;
        groundScaleY = 128;
        groundScaleZ = 128;
        ambience = 0;
        diffusion = 0;
        team = 0;
    }

    public static ItemDefinition lookup(int id) {
        for (int i = 0; i < 10; i++) {
            if (cache[i].id == id) {
                return cache[i];
            }
        }

        cacheIndex = (cacheIndex + 1) % 10;
        ItemDefinition itemDef = cache[cacheIndex];
        dataBuf.currentOffset = offsets[id];
        itemDef.id = id;
        itemDef.setDefaults();
        itemDef.decode(dataBuf);
        /* Customs added here? */

        switch (id) {
            case 13190:
                itemDef.name = "$10 Donator bond";
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Redeem";
                itemDef.modelId = 29210;
                itemDef.spriteScale = 2300;
                itemDef.spritePitch = 512;
                itemDef.originalModelColors = new int[] {5652, 5652, 5652, 5652, 5652, 5652, 5652};
                itemDef.modifiedModelColors = new int[] {22464, 20416, 22451, 22181, 22449, 22305, 21435};
                break;
            case 13191:
                itemDef.name = "$50 Donator bond";
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Redeem";
                itemDef.modelId = 29210;
                itemDef.spriteScale = 2300;
                itemDef.spritePitch = 512;
                itemDef.originalModelColors = new int[] {-21568, -21568, -21568, -21568, -21568, -21568, -21568};
                itemDef.modifiedModelColors = new int[] {22464, 20416, 22451, 22181, 22449, 22305, 21435};
                break;
            case 13192:
                itemDef.name = "$100 Donator bond";
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Redeem";
                itemDef.modelId = 29210;
                itemDef.spriteScale = 2300;
                itemDef.spritePitch = 512;
                itemDef.originalModelColors = new int[] {22464, 22464, 22464, 22464, 22464, 22464, 22464};
                itemDef.modifiedModelColors = new int[] {22464, 20416, 22451, 22181, 22449, 22305, 21435};
                break;
            case 13193:
                itemDef.name = "$200 Donator bond";
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Redeem";
                itemDef.modelId = 29210;
                itemDef.spriteScale = 2300;
                itemDef.spritePitch = 512;
                itemDef.originalModelColors = new int[] {-31858, -31858, -31858, -31858, -31858, -31858, -31858};
                itemDef.modifiedModelColors = new int[] {22464, 20416, 22451, 22181, 22449, 22305, 21435};
                break;
            case 13194:
                itemDef.name = "$500 Donator bond";
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Redeem";
                itemDef.modelId = 29210;
                itemDef.spriteScale = 2300;
                itemDef.spritePitch = 512;
                itemDef.originalModelColors = new int[] {8125, 8125, 8125, 8125, 8125, 8125, 8125};
                itemDef.modifiedModelColors = new int[] {22464, 20416, 22451, 22181, 22449, 22305, 21435};
                itemDef.certID = -1;
                itemDef.certTemplateID = -1;
                break;
            case 13111:
                itemDef.name = "Valyrian Sword";
                itemDef.description = "The most rarest sword, forged from great Valyrian steel.";

                break;
            case 19687:
                itemDef.name = "Vesta's full helm";
                break;
            case 6798:
                itemDef.name = "Arrowhead scroll";
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Unlock";
                break;
            case 6799:
                itemDef.name = "Masterbaiter scroll";
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Unlock";
                break;
            case 6800:
                itemDef.name = "Double wood scroll";
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Unlock";
                break;
            case 6801:
                itemDef.name = "Little birdy scroll";
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Unlock";
                break;
            case 6802:
                itemDef.name = "The rock scroll";
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Unlock";
                break;
            case 6803:
                itemDef.name = "Flame on scroll";
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Unlock";
                break;
            case 11941:
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Check";
                break;
            case 7478:
                itemDef.itemActions = new String[5];
                itemDef.name = "Vote token";
                itemDef.description = "Exchange these tokens for a mystery box and vote points by speaking to Hopleez!";
                break;
            case 8038:
                itemDef.name = "Pet mystery box";
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Open";
                break;
            case 6828:
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Open";
                break;

            case 6199:
                itemDef.name = "Bronze mystery box";
                itemDef.modelId = 2426;
                itemDef.spriteScale = 1180;
                itemDef.spriteCameraRoll = 172;
                itemDef.spritePitch = 160;
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Open";
                itemDef.modifiedModelColors = new int[]{22410, 2999};
                itemDef.originalModelColors = new int[]{5652, 7050};
                break;
            case 12955:
                itemDef.name = "Silver mystery box";
                itemDef.modelId = 2426;
                itemDef.spriteScale = 1180;
                itemDef.spriteCameraRoll = 172;
                itemDef.spritePitch = 160;
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Open";
                itemDef.modifiedModelColors = new int[]{22410, 2999};
                itemDef.originalModelColors = new int[]{115, 61};
                break;
            case 11739:
                itemDef.name = "Gold mystery box";
                itemDef.modelId = 2426;
                itemDef.spriteScale = 1180;
                itemDef.spriteCameraRoll = 172;
                itemDef.spritePitch = 160;
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Open";
                itemDef.modifiedModelColors = new int[]{22410, 2999};
                itemDef.originalModelColors = new int[]{8128, 6073};
                break;


            case 21216:
                itemDef.itemActions = new String[5];
                itemDef.itemActions[1] = "Wield";
                itemDef.name = "White whip"; //Name
                itemDef.description = "A white whip.";
                itemDef.spritePitch = 280;
                itemDef.spriteCameraRoll = 0;
                itemDef.spriteTranslateX = -1;
                itemDef.spriteTranslateY = 56;
                itemDef.modelId = 5412;
                itemDef.primaryMaleModel = 5409;
                itemDef.primaryFemaleModel = 5409;
                itemDef.spriteScale = 840;
                itemDef.originalModelColors = new int[2];
                itemDef.modifiedModelColors = new int[2];
                itemDef.modifiedModelColors[0] = 528;
                itemDef.originalModelColors[0] = 100;
                break;
            case 21217:
                itemDef.itemActions = new String[5];
                itemDef.itemActions[1] = "Wield";
                itemDef.name = "Iron whip"; //Name
                itemDef.description = "A Iron whip.";
                itemDef.spritePitch = 280;
                itemDef.spriteCameraRoll = 0;
                itemDef.spriteTranslateX = -1;
                itemDef.spriteTranslateY = 56;
                itemDef.modelId = 5412;
                itemDef.primaryMaleModel = 5409;
                itemDef.primaryFemaleModel = 5409;
                itemDef.spriteScale = 840;
                itemDef.originalModelColors = new int[2];
                itemDef.modifiedModelColors = new int[2];
                itemDef.modifiedModelColors[0] = 528;
                itemDef.originalModelColors[0] = 20;
                break;
            case 21218:
                itemDef.itemActions = new String[5];
                itemDef.itemActions[1] = "Wield";
                itemDef.name = "Steel whip"; //Name
                itemDef.description = "A Steel whip.";
                itemDef.spritePitch = 280;
                itemDef.spriteCameraRoll = 0;
                itemDef.spriteTranslateX = -1;
                itemDef.spriteTranslateY = 56;
                itemDef.modelId = 5412;
                itemDef.primaryMaleModel = 5409;
                itemDef.primaryFemaleModel = 5409;
                itemDef.spriteScale = 840;
                itemDef.originalModelColors = new int[2];
                itemDef.modifiedModelColors = new int[2];
                itemDef.modifiedModelColors[0] = 528;
                itemDef.originalModelColors[0] = 70;
                break;
            case 21219:
                itemDef.itemActions = new String[5];
                itemDef.itemActions[1] = "Wield";
                itemDef.name = "Black whip"; //Name
                itemDef.description = "A Black whip.";
                itemDef.spritePitch = 280;
                itemDef.spriteCameraRoll = 0;
                itemDef.spriteTranslateX = -1;
                itemDef.spriteTranslateY = 56;
                itemDef.modelId = 5412;
                itemDef.primaryMaleModel = 5409;
                itemDef.primaryFemaleModel = 5409;
                itemDef.spriteScale = 840;
                itemDef.originalModelColors = new int[2];
                itemDef.modifiedModelColors = new int[2];
                itemDef.modifiedModelColors[0] = 528;
                itemDef.originalModelColors[0] = 0;
                break;
            case 21220:
                itemDef.itemActions = new String[5];
                itemDef.itemActions[1] = "Wield";
                itemDef.name = "Mithril whip"; //Name
                itemDef.description = "A Mithril whip.";
                itemDef.spritePitch = 280;
                itemDef.spriteCameraRoll = 0;
                itemDef.spriteTranslateX = -1;
                itemDef.spriteTranslateY = 56;
                itemDef.modelId = 5412;
                itemDef.primaryMaleModel = 5409;
                itemDef.primaryFemaleModel = 5409;
                itemDef.spriteScale = 840;
                itemDef.originalModelColors = new int[2];
                itemDef.modifiedModelColors = new int[2];
                itemDef.modifiedModelColors[0] = 528;
                itemDef.originalModelColors[0] = 43297;
                break;
            case 21221:
                itemDef.itemActions = new String[5];
                itemDef.itemActions[1] = "Wield";
                itemDef.name = "Adamant whip"; //Name
                itemDef.description = "A Adamant whip.";
                itemDef.spritePitch = 280;
                itemDef.spriteCameraRoll = 0;
                itemDef.spriteTranslateX = -1;
                itemDef.spriteTranslateY = 56;
                itemDef.modelId = 5412;
                itemDef.primaryMaleModel = 5409;
                itemDef.primaryFemaleModel = 5409;
                itemDef.spriteScale = 840;
                itemDef.originalModelColors = new int[2];
                itemDef.modifiedModelColors = new int[2];
                itemDef.modifiedModelColors[0] = 528;
                itemDef.originalModelColors[0] = 21662;
                break;
            case 21222:
                itemDef.itemActions = new String[5];
                itemDef.itemActions[1] = "Wield";
                itemDef.name = "Rune whip"; //Name
                itemDef.description = "A Rune whip.";
                itemDef.spritePitch = 280;
                itemDef.spriteCameraRoll = 0;
                itemDef.spriteTranslateX = -1;
                itemDef.spriteTranslateY = 56;
                itemDef.modelId = 5412;
                itemDef.primaryMaleModel = 5409;
                itemDef.primaryFemaleModel = 5409;
                itemDef.spriteScale = 840;
                itemDef.originalModelColors = new int[2];
                itemDef.modifiedModelColors = new int[2];
                itemDef.modifiedModelColors[0] = 528;
                itemDef.originalModelColors[0] = 36133;
                break;
            case 21223:
                itemDef.itemActions = new String[5];
                itemDef.itemActions[1] = "Wield";
                itemDef.name = "Dragon whip"; //Name
                itemDef.description = "A Dragon whip.";
                itemDef.spritePitch = 280;
                itemDef.spriteCameraRoll = 0;
                itemDef.spriteTranslateX = -1;
                itemDef.spriteTranslateY = 56;
                itemDef.modelId = 5412;
                itemDef.primaryMaleModel = 5409;
                itemDef.primaryFemaleModel = 5409;
                itemDef.spriteScale = 840;
                itemDef.originalModelColors = new int[2];
                itemDef.modifiedModelColors = new int[2];
                itemDef.modifiedModelColors[0] = 528;
                itemDef.originalModelColors[0] = 926;
                break;
            case 21224:
                itemDef.itemActions = new String[5];
                itemDef.itemActions[1] = "Wield";
                itemDef.name = "Barrows whip"; //Name
                itemDef.description = "A Barrows whip.";
                itemDef.spritePitch = 280;
                itemDef.spriteCameraRoll = 0;
                itemDef.spriteTranslateX = -1;
                itemDef.spriteTranslateY = 56;
                itemDef.modelId = 5412;
                itemDef.primaryMaleModel = 5409;
                itemDef.primaryFemaleModel = 5409;
                itemDef.spriteScale = 840;
                itemDef.originalModelColors = new int[2];
                itemDef.modifiedModelColors = new int[2];
                itemDef.modifiedModelColors[0] = 528;
                itemDef.originalModelColors[0] = 5652;
                break;
            case 21225:
                itemDef.itemActions = new String[5];
                itemDef.itemActions[1] = "Wield";
                itemDef.name = "Lime whip"; //Name
                itemDef.description = "A Lime whip.";
                itemDef.spritePitch = 280;
                itemDef.spriteCameraRoll = 0;
                itemDef.spriteTranslateX = -1;
                itemDef.spriteTranslateY = 56;
                itemDef.modelId = 5412;
                itemDef.primaryMaleModel = 5409;
                itemDef.primaryFemaleModel = 5409;
                itemDef.spriteScale = 840;
                itemDef.originalModelColors = new int[2];
                itemDef.modifiedModelColors = new int[2];
                itemDef.modifiedModelColors[0] = 528;
                itemDef.originalModelColors[0] = 17350;
                break;
            case 21226:
                itemDef.itemActions = new String[5];
                itemDef.itemActions[1] = "Wield";
                itemDef.name = "Pink whip"; //Name
                itemDef.description = "A Pink whip.";
                itemDef.spritePitch = 280;
                itemDef.spriteCameraRoll = 0;
                itemDef.spriteTranslateX = -1;
                itemDef.spriteTranslateY = 56;
                itemDef.modelId = 5412;
                itemDef.primaryMaleModel = 5409;
                itemDef.primaryFemaleModel = 5409;
                itemDef.spriteScale = 840;
                itemDef.originalModelColors = new int[2];
                itemDef.modifiedModelColors = new int[2];
                itemDef.modifiedModelColors[0] = 528;
                itemDef.originalModelColors[0] = 257770;
                break;
            case 21227:
                itemDef.itemActions = new String[5];
                itemDef.itemActions[1] = "Wield";
                itemDef.name = "Elemental whip"; //Name
                itemDef.description = "A Elemental whip.";
                itemDef.spritePitch = 280;
                itemDef.spriteCameraRoll = 0;
                itemDef.spriteTranslateX = -1;
                itemDef.spriteTranslateY = 56;
                itemDef.modelId = 5412;
                itemDef.primaryMaleModel = 5409;
                itemDef.primaryFemaleModel = 5409;
                itemDef.spriteScale = 840;
                itemDef.originalModelColors = new int[2];
                itemDef.modifiedModelColors = new int[2];
                itemDef.modifiedModelColors[0] = 528;
                itemDef.originalModelColors[0] = 51120;
                break;
            case 12006:
                itemDef.name = "Abyssal whip(p+)"; //Name
                itemDef.description = "A Poisoned a Abbysal whip.";
                break;
            //Pickup the fucking phone you mother fucker
            case 21228:
                itemDef.itemActions = new String[5];
                itemDef.itemActions[1] = "Wield";
                itemDef.name = "Frost whip"; //Name
                itemDef.description = "A Frost whip.";
                itemDef.spritePitch = 280;
                itemDef.spriteCameraRoll = 0;
                itemDef.spriteTranslateX = -1;
                itemDef.spriteTranslateY = 56;
                itemDef.modelId = 5412;
                itemDef.primaryMaleModel = 5409;
                itemDef.primaryFemaleModel = 5409;
                itemDef.spriteScale = 840;
                itemDef.originalModelColors = new int[2];
                itemDef.modifiedModelColors = new int[2];
                itemDef.modifiedModelColors[0] = 528;
                itemDef.originalModelColors[0] = 34770;
                break;
            case 6640:
                itemDef.name = "Blood Crystal";
                itemDef.itemActions[0] = "Open";
                break;
            case 5509:
            case 5510:
            case 5512:
            case 5514:
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Fill";
                itemDef.itemActions[2] = "Empty";
                break;

            case 5733:
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Yum";
                break;

            case 4155:
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Check";
                break;
            case 15098:
                itemDef.name = "Dice (up to 100)";
                itemDef.modelId = 47852;
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Private-roll";
                itemDef.itemActions[2] = "Clan-roll";
                itemDef.spriteCameraRoll = 215;
                itemDef.spritePitch = 94;
                itemDef.spriteScale = 1104;
                itemDef.ambience = 25;
                break;
            case 6831:
                itemDef.name = "Mime Box";
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Open";
                itemDef.description = "Contains a random reward of mime costume, mime emote or 75k coins.";
                break;
            case 6832:
                itemDef.name = "Drill Demon Box";
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Open";
                itemDef.description = "Contains a random reward of drill demon costume, drill demon emote or 75k coins.";
                break;
            case 13307:
                itemDef.name = "Credits";
                itemDef.description = "Lovely credits!";
                break;
            case 12897:
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Open";
                itemDef.name = "Clan Showcase Box";
                itemDef.description = "Opening this box will award a random showcase item based on the clan's level.";
                break;
            case 6854:
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Open";
                itemDef.name = "Clan Resource Box";
                itemDef.description = "Inside this box contains random resources for the clan's bank.";
                break;
            case 2568:
                itemDef.itemActions[2] = "Check charges";
                break;
            case 13188:
                itemDef.name = "Dragon claws";
                itemDef.itemActions = new String[5];
                itemDef.itemActions[1] = "Wield";
                break;
            case 8013:
                itemDef.name = "Home teleport";
                break;
            case 20527:
                itemDef.name = "Near Reality tokens";
                itemDef.description = "The main currency for NR.";
                itemDef.stackable = true;
                break;

            case 10834:
                itemDef.name = "@gre@100M Cash Bag";
                itemDef.itemActions[0] = "Claim";
                itemDef.description = "Can be claimed for 100 Mill Coins";
                itemDef.stackable = true;
                break;

            case 10835:
                itemDef.name = "@gre@500M Cash Bag";
                itemDef.itemActions[0] = "Claim";
                itemDef.description = "Can be claimed for 500 Mill Coins";
                itemDef.stackable = true;
                break;
            case 11188:
                itemDef.name = "2x experience (1hr)";
                itemDef.description = "This lamp will give clan members double experience for a random skill for 1 hour.";
                break;
            case 11189:
                itemDef.name = "2x experience (3hr)";
                itemDef.description = "This lamp will give clan members double experience for a random skill for 3 hour.";
                break;
            case 11679:
                itemDef.name = "2x experience (5hr)";
                itemDef.description = "This lamp will give clan members double experience for a random skill for 5 hour.";
                break;
            case 11187:
                itemDef.name = "250k experience lamp";
                itemDef.description = "This lamp will give clan members 250,000 experience for a random skill.";
                break;
            case 4447:
                itemDef.name = "500k experience lamp";
                itemDef.description = "This lamp will give clan members 500,000 experience for a random skill.";
                break;
            case 6543:
                itemDef.name = "Drop rate increase (1.5x)";
                itemDef.description = "This lamp will give clan members a 1x drop rate increase for a random boss. (15min)";
                break;
            case 7498:
                itemDef.name = "Drop rate increase (2x)";
                itemDef.description = "This lamp will give clan members a 3x drop rate increase for a random boss. (15min)";
                break;
            case 11137:
                itemDef.name = "Drop rate increase (2.5x)";
                itemDef.description = "This lamp will give clan members a 5x drop rate increase for a random boss. (15min)";
                break;
            case 11139:
                itemDef.name = "2x resource (15min)";
                itemDef.description = "This lamp will give clan members double resource rewards for 15 minutes.";
                break;
            case 12690://Armour sets
            case 12873:
            case 12875:
            case 12877:
            case 12879:
            case 12881:
            case 12883:
            case 12962:
            case 12972:
            case 12974:
            case 12984:
            case 12986:
            case 12988:
            case 12990:
            case 13000:
            case 13002:
            case 13012:
            case 13014:
            case 13024:
            case 13026:
            case 11738:
            case 9666:
            case 9670:
            case 12865:
            case 12867:
            case 12869:
            case 12871:
            case 12966:
            case 12964:
            case 12968:
            case 12970:
            case 12976:
            case 12978:
            case 12980:
            case 12982:
            case 12992:
            case 12994:
            case 12996:
            case 12998:
            case 13004:
            case 13006:
            case 13008:
            case 13010:
            case 13016:
            case 13018:
            case 13020:
            case 13022:
            case 13028:
            case 13030:
            case 13032:
            case 13034:
            case 13036:
            case 13038:
            case 12960:
            case 13173:
            case 13175:
            case 13064:
            case 13066:
                itemDef.itemActions = new String[5];
                itemDef.itemActions[0] = "Unpack";
                break;
        }

        if (itemDef.certTemplateID != -1)
            itemDef.toNote();
        return itemDef;
    }
    

    private void toNote() {
        ItemDefinition noted = lookup(certTemplateID);
        modelId = noted.modelId;
        spriteScale = noted.spriteScale;
        spritePitch = noted.spritePitch;
        spriteCameraRoll = noted.spriteCameraRoll;

        spriteCameraYaw = noted.spriteCameraYaw;
        spriteTranslateX = noted.spriteTranslateX;
        spriteTranslateY = noted.spriteTranslateY;
        modifiedModelColors = noted.modifiedModelColors;
        originalModelColors = noted.originalModelColors;
        ItemDefinition unnoted = lookup(certID);

        if (unnoted == null || unnoted.name == null) {
            return;
        }

        name = unnoted.name;
        membersObject = unnoted.membersObject;
        value = unnoted.value;
        String aOrAn = "a";
        char vowelChar = unnoted.name.charAt(0);
        if (vowelChar == 'A' || vowelChar == 'E' || vowelChar == 'I' || vowelChar == 'O' || vowelChar == 'U') {
            aOrAn = "an";
        }

        description = "Swap this note at any bank for " + aOrAn + " " + unnoted.name + ".";
        stackable = true;
    }

    public static Sprite getSprite(int item, int amount, int k) {
        if (k == 0) {
            Sprite sprite = spriteCache.get(item);
            if (sprite != null && sprite.resizeHeight != amount && sprite.resizeHeight != -1) {

                sprite.unlink();
                sprite = null;
            }
            if (sprite != null)
                return sprite;
        }
        ItemDefinition itemDef = lookup(item);
        if (itemDef.stackIds == null)
            amount = -1;
        if (amount > 1) {
            int i1 = -1;
            for (int j1 = 0; j1 < 10; j1++)
                if (amount >= itemDef.stackAmounts[j1] && itemDef.stackAmounts[j1] != 0)
                    i1 = itemDef.stackIds[j1];

            if (i1 != -1)
                itemDef = lookup(i1);
        }
        Model model = itemDef.method201(1);
        if (model == null)
            return null;
        Sprite sprite = null;
        if (itemDef.certTemplateID != -1) {
            sprite = getSprite(itemDef.certID, 10, -1);
            if (sprite == null)
                return null;
        }
        Sprite enabledSprite = new Sprite(32, 32);
        int k1 = Rasterizer.textureInt1;
        int l1 = Rasterizer.textureInt2;
        int ai[] = Rasterizer.anIntArray1472;
        int ai1[] = Raster.pixels;
        int i2 = Raster.width;
        int j2 = Raster.height;
        int k2 = Raster.topX;
        int l2 = Raster.bottomX;
        int i3 = Raster.topY;
        int j3 = Raster.bottomY;
        Rasterizer.aBoolean1464 = false;
        Raster.initDrawingArea(enabledSprite.raster, 32, 32);
        Raster.fillRectangle(0, 0, 32, 32, 0);
        Rasterizer.method364();
        int k3 = itemDef.spriteScale;
        if (k == -1)
            k3 = (int) ((double) k3 * 1.5D);
        if (k > 0)
            k3 = (int) ((double) k3 * 1.04D);
        int l3 = Rasterizer.anIntArray1470[itemDef.spritePitch] * k3 >> 16;
        int i4 = Rasterizer.anIntArray1471[itemDef.spritePitch] * k3 >> 16;
        model.method482(itemDef.spriteCameraRoll, itemDef.spriteCameraYaw, itemDef.spritePitch, itemDef.spriteTranslateX, l3 + model.modelHeight / 2 + itemDef.spriteTranslateY, i4 + itemDef.spriteTranslateY);
        for (int i5 = 31; i5 >= 0; i5--) {
            for (int j4 = 31; j4 >= 0; j4--)
                if (enabledSprite.raster[i5 + j4 * 32] == 0)
                    if (i5 > 0 && enabledSprite.raster[(i5 - 1) + j4 * 32] > 1)
                        enabledSprite.raster[i5 + j4 * 32] = 1;
                    else if (j4 > 0 && enabledSprite.raster[i5 + (j4 - 1) * 32] > 1)
                        enabledSprite.raster[i5 + j4 * 32] = 1;
                    else if (i5 < 31 && enabledSprite.raster[i5 + 1 + j4 * 32] > 1)
                        enabledSprite.raster[i5 + j4 * 32] = 1;
                    else if (j4 < 31 && enabledSprite.raster[i5 + (j4 + 1) * 32] > 1)
                        enabledSprite.raster[i5 + j4 * 32] = 1;

        }

        if (k > 0) {
            for (int j5 = 31; j5 >= 0; j5--) {
                for (int k4 = 31; k4 >= 0; k4--)
                    if (enabledSprite.raster[j5 + k4 * 32] == 0)
                        if (j5 > 0 && enabledSprite.raster[(j5 - 1) + k4 * 32] == 1)
                            enabledSprite.raster[j5 + k4 * 32] = k;
                        else if (k4 > 0 && enabledSprite.raster[j5 + (k4 - 1) * 32] == 1)
                            enabledSprite.raster[j5 + k4 * 32] = k;
                        else if (j5 < 31 && enabledSprite.raster[j5 + 1 + k4 * 32] == 1)
                            enabledSprite.raster[j5 + k4 * 32] = k;
                        else if (k4 < 31 && enabledSprite.raster[j5 + (k4 + 1) * 32] == 1)
                            enabledSprite.raster[j5 + k4 * 32] = k;

            }

        } else if (k == 0) {
            for (int k5 = 31; k5 >= 0; k5--) {
                for (int l4 = 31; l4 >= 0; l4--)
                    if (enabledSprite.raster[k5 + l4 * 32] == 0 && k5 > 0 && l4 > 0 && enabledSprite.raster[(k5 - 1) + (l4 - 1) * 32] > 0)
                        enabledSprite.raster[k5 + l4 * 32] = 0x302020;

            }

        }
        if (itemDef.certTemplateID != -1) {
            int l5 = sprite.resizeWidth;
            int j6 = sprite.resizeHeight;
            sprite.resizeWidth = 32;
            sprite.resizeHeight = 32;
            sprite.drawSprite(0, 0);
            sprite.resizeWidth = l5;
            sprite.resizeHeight = j6;
        }
        if (k == 0)
            spriteCache.put(item, enabledSprite);
        Raster.initDrawingArea(ai1, i2, j2);
        Raster.setDrawingArea(k2, i3, l2, j3);
        Rasterizer.textureInt1 = k1;
        Rasterizer.textureInt2 = l1;
        Rasterizer.anIntArray1472 = ai;
        Rasterizer.aBoolean1464 = true;
        if (itemDef.stackable)
            enabledSprite.resizeWidth = 33;
        else
            enabledSprite.resizeWidth = 32;
        enabledSprite.resizeHeight = amount;
        return enabledSprite;
    }

    public static Sprite getSprite(int item, int amount, int k, int zoom) {
        if (k == 0) {
            Sprite sprite = spriteCache.get(item);
            if (sprite != null && sprite.resizeHeight != amount && sprite.resizeHeight != -1) {

                sprite.unlink();
                sprite = null;
            }
            if (sprite != null)
                return sprite;
        }
        ItemDefinition itemDef = lookup(item);
        if (itemDef.stackIds == null)
            amount = -1;
        if (amount > 1) {
            int i1 = -1;
            for (int j1 = 0; j1 < 10; j1++)
                if (amount >= itemDef.stackAmounts[j1] && itemDef.stackAmounts[j1] != 0)
                    i1 = itemDef.stackIds[j1];

            if (i1 != -1)
                itemDef = lookup(i1);
        }
        Model model = itemDef.method201(1);
        if (model == null)
            return null;
        Sprite sprite = null;
        if (itemDef.certTemplateID != -1) {
            sprite = getSprite(itemDef.certID, 10, -1);
            if (sprite == null)
                return null;
        }
        Sprite enabledSprite = new Sprite(32, 32);
        int k1 = Rasterizer.textureInt1;
        int l1 = Rasterizer.textureInt2;
        int ai[] = Rasterizer.anIntArray1472;
        int ai1[] = Raster.pixels;
        int i2 = Raster.width;
        int j2 = Raster.height;
        int k2 = Raster.topX;
        int l2 = Raster.bottomX;
        int i3 = Raster.topY;
        int j3 = Raster.bottomY;
        Rasterizer.aBoolean1464 = false;
        Raster.initDrawingArea(enabledSprite.raster, 32, 32);
        Raster.fillRectangle(0, 0, 32, 32, 0);
        Rasterizer.method364();
        int k3 = itemDef.spriteScale;
        if (k == -1)
            k3 = (int) ((double) k3 * 1.5D);
        if (k > 0)
            k3 = (int) ((double) k3 * 1.04D);
        int l3 = Rasterizer.anIntArray1470[itemDef.spritePitch] * k3 >> 16;
        int i4 = Rasterizer.anIntArray1471[itemDef.spritePitch] * k3 >> 16;
        model.method482(itemDef.spriteCameraRoll, itemDef.spriteCameraYaw, itemDef.spritePitch, itemDef.spriteTranslateX, l3 + model.modelHeight / 2 + itemDef.spriteTranslateY, i4 + itemDef.spriteTranslateY);
        for (int i5 = 31; i5 >= 0; i5--) {
            for (int j4 = 31; j4 >= 0; j4--)
                if (enabledSprite.raster[i5 + j4 * 32] == 0)
                    if (i5 > 0 && enabledSprite.raster[(i5 - 1) + j4 * 32] > 1)
                        enabledSprite.raster[i5 + j4 * 32] = 1;
                    else if (j4 > 0 && enabledSprite.raster[i5 + (j4 - 1) * 32] > 1)
                        enabledSprite.raster[i5 + j4 * 32] = 1;
                    else if (i5 < 31 && enabledSprite.raster[i5 + 1 + j4 * 32] > 1)
                        enabledSprite.raster[i5 + j4 * 32] = 1;
                    else if (j4 < 31 && enabledSprite.raster[i5 + (j4 + 1) * 32] > 1)
                        enabledSprite.raster[i5 + j4 * 32] = 1;

        }

        if (k > 0) {
            for (int j5 = 31; j5 >= 0; j5--) {
                for (int k4 = 31; k4 >= 0; k4--)
                    if (enabledSprite.raster[j5 + k4 * 32] == 0)
                        if (j5 > 0 && enabledSprite.raster[(j5 - 1) + k4 * 32] == 1)
                            enabledSprite.raster[j5 + k4 * 32] = k;
                        else if (k4 > 0 && enabledSprite.raster[j5 + (k4 - 1) * 32] == 1)
                            enabledSprite.raster[j5 + k4 * 32] = k;
                        else if (j5 < 31 && enabledSprite.raster[j5 + 1 + k4 * 32] == 1)
                            enabledSprite.raster[j5 + k4 * 32] = k;
                        else if (k4 < 31 && enabledSprite.raster[j5 + (k4 + 1) * 32] == 1)
                            enabledSprite.raster[j5 + k4 * 32] = k;

            }

        } else if (k == 0) {
            for (int k5 = 31; k5 >= 0; k5--) {
                for (int l4 = 31; l4 >= 0; l4--)
                    if (enabledSprite.raster[k5 + l4 * 32] == 0 && k5 > 0 && l4 > 0 && enabledSprite.raster[(k5 - 1) + (l4 - 1) * 32] > 0)
                        enabledSprite.raster[k5 + l4 * 32] = 0x302020;

            }

        }
        if (itemDef.certTemplateID != -1) {
            int l5 = sprite.resizeWidth;
            int j6 = sprite.resizeHeight;
            sprite.resizeWidth = 32;
            sprite.resizeHeight = 32;
            sprite.drawSprite(0, 0);
            sprite.resizeWidth = l5;
            sprite.resizeHeight = j6;
        }
        if (k == 0)
            spriteCache.put(item, enabledSprite);
        Raster.initDrawingArea(ai1, i2, j2);
        Raster.setDrawingArea(k2, i3, l2, j3);
        Rasterizer.textureInt1 = k1;
        Rasterizer.textureInt2 = l1;
        Rasterizer.anIntArray1472 = ai;
        Rasterizer.aBoolean1464 = true;
        if (itemDef.stackable)
            enabledSprite.resizeWidth = 33;
        else
            enabledSprite.resizeWidth = 32;
        enabledSprite.resizeHeight = amount;
        return enabledSprite;
    }

    public Model method201(int i) {
        if (stackIds != null && i > 1) {
            int j = -1;
            for (int k = 0; k < 10; k++)
                if (i >= stackAmounts[k] && stackAmounts[k] != 0)
                    j = stackIds[k];

            if (j != -1)
                return lookup(j).method201(1);
        }
        Model model = (Model) mruNodes2.get(id);
        if (model != null)
            return model;
        model = Model.getModel(modelId);
        if (model == null)
            return null;
        if (groundScaleX != 128 || groundScaleY != 128 || groundScaleZ != 128)
            model.method478(groundScaleX, groundScaleZ, groundScaleY);
        if (modifiedModelColors != null) {
            for (int l = 0; l < modifiedModelColors.length; l++)
                model.recolor(modifiedModelColors[l], originalModelColors[l]);

        }
        model.light(64 + ambience, 768 + diffusion, -50, -10, -50, true);
        model.aBoolean1659 = true;
        mruNodes2.put(model, id);
        return model;
    }

    public Model method202(int i) {
        if (stackIds != null && i > 1) {
            int j = -1;
            for (int k = 0; k < 10; k++)
                if (i >= stackAmounts[k] && stackAmounts[k] != 0)
                    j = stackIds[k];

            if (j != -1)
                return lookup(j).method202(1);
        }
        Model model = Model.getModel(modelId);
        if (model == null)
            return null;
        if (modifiedModelColors != null) {
            for (int l = 0; l < modifiedModelColors.length; l++)
                model.recolor(modifiedModelColors[l], originalModelColors[l]);

        }
        return model;
    }

    private void decode(Buffer buffer) {
        while(true){
            int opcode = buffer.readUByte();

            if (opcode == 0) {
                return;
            } else if (opcode == 1) {
                modelId = buffer.readUShort();
            } else if (opcode == 2) {
                name = buffer.readString();
            } else if (opcode == 4) {
                spriteScale = buffer.readUShort();
            } else if (opcode == 5) {
                spritePitch = buffer.readUShort();
            } else if (opcode == 6) {
                spriteCameraRoll = buffer.readUShort();
            } else if (opcode == 7) {
                spriteTranslateX = buffer.readUShort();
                if (spriteTranslateX > 32767) {
                    spriteTranslateX -= 0x10000;
                }
            } else if (opcode == 8) {
                spriteTranslateY = buffer.readUShort();
                if (spriteTranslateY > 32767) {
                    spriteTranslateY -= 0x10000;
                }
            } else if (opcode == 11) {
                stackable = true;
            } else if (opcode == 12) {
                value = buffer.readInt();
            } else if (opcode == 16) {
                membersObject = true;
            } else if (opcode == 23) {
                primaryMaleModel = buffer.readUShort();
                maleTranslation = buffer.readSignedByte();
            } else if (opcode == 24) {
                secondaryMaleModel = buffer.readUShort();
            } else if (opcode == 25) {
                primaryFemaleModel = buffer.readUShort();
                femaleTranslation = buffer.readSignedByte();
            } else if (opcode == 26) {
                secondaryFemaleModel = buffer.readUShort();
            } else if (opcode >= 30 && opcode < 35) {
                if (groundActions == null) {
                    groundActions = new String[5];
                }
                groundActions[opcode - 30] = buffer.readString();
                if (groundActions[opcode - 30].equalsIgnoreCase("Hidden")) {
                    groundActions[opcode - 30] = null;
                }
            } else if (opcode >= 35 && opcode < 40) {
                if (itemActions == null) {
                    itemActions = new String[5];
                }

                itemActions[opcode - 35] = buffer.readString();
            } else if (opcode == 40) {
                int len = buffer.readUByte();
                originalModelColors = new int[len];
                modifiedModelColors = new int[len];
                for (int i = 0; i < len; i++) {
                    originalModelColors[i] = buffer.readUShort();
                    modifiedModelColors[i] = buffer.readUShort();
                }
            } else if (opcode == 41) {
                int len = buffer.readUByte();
                originalTexture = new short[len];
                modifiedTexture = new short[len];
                for (int i = 0; i < len; i++) {
                    originalTexture[i] = (short) buffer.readUShort();
                    modifiedTexture[i] = (short) buffer.readUShort();
                }
            } else if (opcode == 42) {
                shiftClickIndex = buffer.readUByte();
            } else if (opcode == 65) {
                searchable = true;
            }  else if (opcode == 78) {
                tertiaryMaleEquipmentModel = buffer.readUShort();
            } else if (opcode == 79) {
                tertiaryFemaleEquipmentModel = buffer.readUShort();
            } else if (opcode == 90) {
                primaryMaleHeadPiece = buffer.readUShort();
            } else if (opcode == 91) {
                primaryFemaleHeadPiece = buffer.readUShort();
            } else if (opcode == 92) {
                secondaryMaleHeadPiece = buffer.readUShort();
            } else if (opcode == 93) {
                secondaryFemaleHeadPiece = buffer.readUShort();
            } else if (opcode == 95) {
                spriteCameraYaw = buffer.readUShort();
            } else if (opcode == 97) {
                certID = buffer.readUShort();
            } else if (opcode == 98) {
                certTemplateID = buffer.readUShort();
            } else if (opcode >= 100 && opcode < 110) {
                if (stackIds == null) {
                    stackIds = new int[10];
                    stackAmounts = new int[10];
                }
                stackIds[opcode - 100] = buffer.readUShort();
                stackAmounts[opcode - 100] = buffer.readUShort();
            } else if (opcode == 110) {
                groundScaleX = buffer.readUShort();
            } else if (opcode == 111) {
                groundScaleY = buffer.readUShort();
            } else if (opcode == 112) {
                groundScaleZ = buffer.readUShort();
            } else if (opcode == 113) {
                ambience = buffer.readSignedByte();
            } else if (opcode == 114) {
                diffusion = buffer.readSignedByte();
            } else if (opcode == 115) {
                team = buffer.readUByte();
            } else if (opcode == 139) {
                unnotedId = buffer.readUShort(); // un-noted id
            } else if (opcode == 140) {
                notedId = buffer.readUShort(); // noted id
            } else if (opcode == 148) {
                buffer.readUShort(); // placeholder id
            } else if (opcode == 149) {
                buffer.readUShort(); // placeholder template
            }
        }
    }

    private ItemDefinition() {
        id = -1;
    }

    private byte femaleTranslation;
    private byte maleTranslation;
    public int id;// anInt157
    public int team;
    public int value;// anInt155
    public int certID;
    public int modelId;// dropModel
    public int primaryMaleHeadPiece;
    public int primaryFemaleModel;// femWieldModel
    public int spriteCameraYaw;// modelPositionUp
    public int secondaryFemaleModel;// femArmModel
    public int primaryFemaleHeadPiece;
    public int secondaryMaleModel;// maleArmModel
    public int primaryMaleModel;// maleWieldModel
    public int spriteScale;
    public int spriteTranslateX;
    public int spriteTranslateY;//
    public int certTemplateID;
    public int unnotedId = -1;
    public int notedId = -1;
    public int spriteCameraRoll;// modelRotateRight
    public int spritePitch;// modelRotateUp
    public static int totalItems;
    public int[] stackIds;// modelStack
    public int[] stackAmounts;// itemAmount
    public int[] modifiedModelColors;// newModelColor
    public int[] originalModelColors;
    public short[] modifiedTexture;
    public short[] originalTexture;
    private int ambience;
    private int tertiaryFemaleEquipmentModel;
    private int secondaryMaleHeadPiece;
    private int diffusion;
    private int tertiaryMaleEquipmentModel;
    private int groundScaleZ;
    private int groundScaleY;
    private int groundScaleX;
    private int secondaryFemaleHeadPiece;
    private int shiftClickIndex = -2;
    private boolean stockMarket;
    public boolean searchable;
    private static int cacheIndex;
    private static int[] offsets;
    public boolean stackable;// itemStackable
    public boolean membersObject;// aBoolean161
    public static boolean isMembers = true;
    public String name;// itemName
    public String description;// itemExamine
    public String itemActions[];// itemMenuOption
    public String groundActions[];
    private static Buffer dataBuf;
    private static ItemDefinition[] cache;
    public static Int2ObjectArrayMap<Sprite> spriteCache = new Int2ObjectArrayMap<>();
    public static Cache mruNodes2 = new Cache(50);
}