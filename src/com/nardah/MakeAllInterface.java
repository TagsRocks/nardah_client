package com.nardah;

/**
 * @author Ethan Kyle Millard <skype:pumpklins>
 * @since Tue, April 24, 2018 @ 3:52 PM
 */
public class MakeAllInterface extends RSInterface {


    public static int quanid = 57810;
    public static int itemid = 57820;
    public static int quanchild = 0;
    public static int itemchild = 0;
    public static int x;
    public static int itemx;
    public static int centerx = 70;
    public static float amount;

    public static void build(TextDrawingArea[] tda) {
        makeAllInterface(tda);
    }


    private static void makeAllInterface(TextDrawingArea[] tda) {
        RSInterface tab = addTabInterface(57800);
        addText(57801, "How many?", tda, 2, 0x403020, true, false);
        addText(57802, "Choose a quantity, then click an image to begin.", tda, 0, 0x403020, true, false);
        addHoverButton(57803, 877, 35, 30, "Select", -1, 57804, 1); //Close
        addHoveredButton(57804, 878, 35, 30, 57805); //Close Hover
        addText(57806, "1", tda, 0, 0x403020, true, false);

//        addModel(57809);
        addHoverButton(57806, 875, 100, 75, "Select", -1, 57807, 1); //Close
        addHoveredButton(57807, 876, 100, 75, 57808); //Close Hover

        tab.totalChildren(7);
        tab.child(0, 57801, 150, 0);
        tab.child(1, 57802, 150, 17);

        tab.child(2, 57803, 285 + x, 0);
        tab.child(3, 57804, 285 + x, 0);
//        tab.child(4, 57805, 285 + x, 0);

        tab.child(4, 57807, centerx + itemx, 35);
        tab.child(5, 57808, centerx +  itemx, 35);
        tab.child(6, 57809, centerx + 55 + itemx, 72);
        x += 40;
        itemid += 4;
        itemx += 120;

    }

    public static void addQuantity(TextDrawingArea[] tda, RSInterface quan) {

        addHoverButton(57811, 877, 35, 30, "Select", -1, 57812, 1); //Close
        addHoveredButton(57812, 878, 35, 30, 57813); //Close Hover
        addText(quanid + 3, "veveve", tda, 0, 0x403020, true, false);
        quan.child(quanchild++, quanid, 285 + x, 0);
        quan.child(quanchild++, quanid + 1, 285 + x, 0);
        quan.child(quanchild++, quanid + 3, 302 + x, 9);
        quanid += 4;
        x += 40;
    }
    public static void addItem(RSInterface item) {
//        addModel(itemid + 3);
        addHoverButton(57821, 875, 100, 75, "Select", -1, 57822, 1); //Close
        addHoveredButton(57822, 876, 100, 75, 57823); //Close Hover
//        addButton(itemid, 35300, 100, 75, 492, 492, itemid + 1, "Select");
//        addHoveredButton_sprite_loader(itemid + 1, 493, 100, 75, itemid + 2);
        item.child(itemchild++, itemid, centerx + itemx, 35);
        item.child(itemchild++, itemid + 1, centerx +  itemx, 35);
        item.child(itemchild++, itemid + 3, centerx + 55 + itemx, 72);
        itemid += 4;
        itemx += 120;

    }
}
