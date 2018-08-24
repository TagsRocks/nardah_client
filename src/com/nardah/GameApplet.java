package com.nardah;

import javax.swing.*;

import com.nardah.login.LoginRenderer;
import com.nardah.login.ScreenType;
import com.nardah.updater.Updater;
import com.nardah.updater.screen.UpdaterRenderer;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

public class GameApplet extends Applet implements Runnable, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, FocusListener, WindowListener {
    private static final long serialVersionUID = 1L;
    public UpdaterRenderer updaterRenderer;
    public LoginRenderer loginRenderer;
    Console console = new Console();
    private int anInt4;
    private int delayTime;
    private int minDelay;
    private final long aLongArray7[] = new long[10];
    int fps;
    private boolean shouldDebug;
    public int myWidth;
    public int myHeight;
    public Graphics graphics;
    GraphicsBuffer fullGameScreen;
    GameWindow clientFrame;
    private boolean shouldClearScreen;
    boolean awtFocus;
    int idleTime;
    int clickMode2;
    public int mouseX;
    public int mouseY;
    private int clickMode1;
    private int clickX;
    private int clickY;
    long aLong29;
    public int lastMetaModifier;
    int lastClickX;
    int lastClickY;
    final int keyArray[] = new int[128];
    private final int charQueue[] = new int[128];
    private int readIndex;
    private int writeIndex;
    private boolean isApplet;
    int forceWidth = -1;
    int forceHeight = -1;

    void rebuildFrame(boolean undecorated, int width, int height, boolean resizable, boolean full) {
        Component component = getGameComponent();
        component.setBackground(Color.black);
        component.removeMouseWheelListener(this);
        component.removeMouseListener(this);
        component.removeMouseMotionListener(this);
        component.removeKeyListener(this);
        component.removeFocusListener(this);
        if (clientFrame != null) {
            clientFrame.removeWindowListener(this);
            clientFrame.setVisible(false);
            clientFrame.dispose();
            clientFrame = null;
        }
        component = getGameComponent();
        component.setBackground(Color.black);
        component.addMouseWheelListener(this);
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
        component.addKeyListener(this);
        component.addFocusListener(this);
        mouseX = mouseY = -1;
    }

    int getScreenWidth() {
        if (forceWidth >= 0) {
            return forceWidth;
        }
        return getRealScreenWidth();
    }

    int getScreenHeight() {
        if (forceHeight >= 0) {
            return forceHeight;
        }

        return getRealScreenHeight();
    }

    private int getRealScreenWidth() {
        Component component = getGameComponent();
        if (component == null) {
            return forceWidth >= 0 ? forceWidth : 765;
        }

        int w = component.getWidth();
        if (component instanceof java.awt.Container) {
            java.awt.Insets insets = ((java.awt.Container) component).getInsets();
            w -= insets.left + insets.right;
        }
        return w;
    }

    private int getRealScreenHeight() {
        Component component = getGameComponent();
        if (component == null) {
            return forceHeight >= 0 ? forceHeight : 503;
        }

        int h = component.getHeight();
        if (component instanceof java.awt.Container) {
            java.awt.Insets insets = ((java.awt.Container) component).getInsets();
            h -= insets.top + insets.bottom;
        }
        return h;
    }

    boolean appletClient() {
        return clientFrame == null && isApplet == true;
    }

    final void createClientFrame(int w, int h) {
        isApplet = false;
        myWidth = forceWidth = w;
        myHeight = forceHeight = h;
        clientFrame = new GameWindow(this, myWidth, myHeight, Client.frameMode == Client.ScreenMode.RESIZABLE, Client.frameMode == Client.ScreenMode.FULLSCREEN);
        clientFrame.setFocusTraversalKeysEnabled(false);
        graphics = getGameComponent().getGraphics();
        fullGameScreen = new GraphicsBuffer(myWidth, myHeight, getGameComponent());
        startRunnable(this, 1);
    }

    final void initClientFrame(int w, int h) {
        isApplet = true;
        myWidth = forceWidth = w;
        myHeight = forceHeight = h;
        graphics = getGameComponent().getGraphics();
        fullGameScreen = new GraphicsBuffer(myWidth, myHeight, getGameComponent());
        startRunnable(this, 1);
    }

    public void run() {
        getGameComponent().addMouseListener(this);
        getGameComponent().addMouseMotionListener(this);
        getGameComponent().addKeyListener(this);
        getGameComponent().addFocusListener(this);
        getGameComponent().addMouseWheelListener(this);
        if (clientFrame != null) {
            clientFrame.addWindowListener(this);
        }

        Updater.get().setup(Client.instance);

        int i = 0;
        int j = 256;
        int k = 1;
        int l = 0;
        int i1 = 0;
        for (int j1 = 0; j1 < 10; j1++) {
            aLongArray7[j1] = System.currentTimeMillis();
        }
        do {
            if (anInt4 < 0) {
                break;
            }
            if (anInt4 > 0) {
                anInt4--;
                if (anInt4 == 0) {
                    exit();
                    return;
                }
            }
            int k1 = j;
            int i2 = k;
            j = 300;
            k = 1;
            long currentTimeMillis = System.currentTimeMillis();
            if (aLongArray7[i] == 0L) {
                j = k1;
                k = i2;
            } else if (currentTimeMillis > aLongArray7[i]) {
                j = (int) ((long) (2560 * delayTime) / (currentTimeMillis - aLongArray7[i]));
            }
            if (j < 25) {
                j = 25;
            }
            if (j > 256) {
                j = 256;
                k = (int) ((long) delayTime - (currentTimeMillis - aLongArray7[i]) / 10L);
            }
            if (k > delayTime) {
                k = delayTime;
            }
            aLongArray7[i] = currentTimeMillis;
            i = (i + 1) % 10;
            if (k > 1) {
                for (int j2 = 0; j2 < 10; j2++) {
                    if (aLongArray7[j2] != 0L) {
                        aLongArray7[j2] += k;
                    }
                }

            }
            if (k < minDelay) {
                k = minDelay;
            }
            try {
                Thread.sleep(k);
            } catch (InterruptedException interruptedexception) {
                i1++;
            }
            for (; l < 256; l += j) {
                lastMetaModifier = clickMode1;
                lastClickX = clickX;
                lastClickY = clickY;
                clickMode1 = 0;
                processGameLoop();
                readIndex = writeIndex;
            }

            l &= 0xff;
            if (delayTime > 0) {
                fps = (1000 * j) / (delayTime * 256);
            }
            processDrawing();
            if (shouldDebug) {
                System.out.println((new StringBuilder()).append("ntime:").append(currentTimeMillis).toString());
                for (int k2 = 0; k2 < 10; k2++) {
                    int i3 = ((i - k2 - 1) + 20) % 10;
                    System.out.println((new StringBuilder()).append("otim").append(i3).append(":").append(aLongArray7[i3]).toString());
                }

                System.out.println((new StringBuilder()).append("fps:").append(fps).append(" ratio:").append(j).append(" count:").append(l).toString());
                System.out.println((new StringBuilder()).append("del:").append(k).append(" deltime:").append(delayTime).append(" mindel:").append(minDelay).toString());
                System.out.println((new StringBuilder()).append("intex:").append(i1).append(" opos:").append(i).toString());
                shouldDebug = false;
                i1 = 0;
            }
        } while (true);
        if (anInt4 == -1) {
            exit();
        }
    }

    private void exit() {
        anInt4 = -2;
        cleanUpForQuit();
        if (clientFrame != null) {
            try {
                Thread.sleep(1000L);
            } catch (Exception exception) {
            }
            try {
                System.exit(0);
            } catch (Throwable throwable) {
            }
        }
    }

    final void method4(int i) {
        delayTime = 1000 / i;
    }

    public final void start() {
        if (anInt4 >= 0) {
            anInt4 = 0;
        }
    }

    public final void stop() {
        if (anInt4 >= 0) {
            anInt4 = 4000 / delayTime;
        }
    }

    public final void destroy() {
        anInt4 = -1;
        try {
            Thread.sleep(5000L);
        } catch (Exception exception) {
        }
        if (anInt4 == -1) {
            exit();
        }
    }

    public final void update(Graphics g) {
        if (graphics == null) {
            graphics = g;
        }
        shouldClearScreen = true;
        raiseWelcomeScreen();
    }

    @Override
    public final void paint(Graphics g) {
        if (graphics == null) {
            graphics = g;
        }
        shouldClearScreen = true;
        raiseWelcomeScreen();
    }

    @Override
    public final void paintComponents(Graphics g) {
        if (graphics == null) {
            graphics = g;
        }
        shouldClearScreen = true;
        raiseWelcomeScreen();
    }

    private boolean canZoom = true;

    public void mouseWheelMoved(MouseWheelEvent event) {
        int rotation = event.getWheelRotation();
        handleInterfaceScrolling(event);
        if (mouseX > 0 && mouseX < 512 && mouseY > Client.frameHeight - 165 && mouseY < Client.frameHeight - 25) {
            int scrollPos = Client.anInt1089;
            scrollPos -= rotation * 30;
            if (scrollPos < 0)
                scrollPos = 0;
            if (scrollPos > Client.anInt1211 - 110)
                scrollPos = Client.anInt1211 - 110;
            if (Client.anInt1089 != scrollPos) {
                Client.anInt1089 = scrollPos;
                Client.redrawDialogueBox = true;
            }
        }
        if (Client.openInterfaceID == -1) {
            if (Client.controlIsDown && canZoom) {
                if (mouseX > 0 && mouseX < 512 && mouseY > Client.frameHeight - 165 && mouseY < Client.frameHeight - 25) {
                    return;
                }
                if (rotation == -1) {
                    if (Client.cameraZoom > 50) {
                        Client.cameraZoom -= 30;
                    }
                } else {
                    if (Client.cameraZoom < 1250) {
                        Client.cameraZoom += 30;
                    }
                }
            }
        }
    }

    private void handleInterfaceScrolling(MouseWheelEvent event) {
        int rotation = event.getWheelRotation();
        int positionX = 0;
        int positionY = 0;
        int width = 0;
        int height = 0;
        int offsetX = 0;
        int offsetY = 0;
        int childID = 0;
        int tabInterfaceID = Client.tabInterfaceIDs[Client.tabID];
        if (tabInterfaceID != -1) {
            RSInterface tab = RSInterface.getInterfaceCache()[tabInterfaceID];
            offsetX = Client.frameMode == Client.ScreenMode.FIXED ? Client.frameWidth - 218 : (Client.frameMode == Client.ScreenMode.FIXED ? 28 : Client.frameWidth - 197);
            offsetY = Client.frameMode == Client.ScreenMode.FIXED ? Client.frameHeight - 298 : (Client.frameMode == Client.ScreenMode.FIXED ? 37 : Client.frameHeight - (Client.frameWidth >= 1000 ? 37 : 74) - 267);

            if (tab.children != null || tab.children.length != 0) {
                for (int index = 0; index < tab.children.length; index++) {
                    RSInterface tabChild = RSInterface.getInterfaceCache()[tab.children[index]];
                    if (tabChild.children != null) {
                        for (int idx = 0; idx < tabChild.children.length; idx++) {
                            RSInterface tabChild2 = RSInterface.getInterfaceCache()[tabChild.children[idx]];
                            if (tabChild2.dropDown != null && tabChild2.dropDown.isOpen() && tabChild2.dropDown.getOptions().length > 5) {
                                canZoom = false;
                                tabChild2.dropDown.scroll += rotation;
                                if (tabChild2.dropDown.scroll >= tabChild2.dropDown.getOptions().length - 5) {
                                    tabChild2.dropDown.scroll = tabChild2.dropDown.getOptions().length - 5;
                                }
                                if (tabChild2.dropDown.scroll < 0) {
                                    tabChild2.dropDown.scroll = 0;
                                }
                                return;
                            }
                        }
                    }
                    if (tabChild.scrollMax > 0) {
                        childID = index;
                        positionX = tab.childX[index];
                        positionY = tab.childY[index];
                        width = RSInterface.getInterfaceCache()[tab.children[index]].width;
                        height = RSInterface.getInterfaceCache()[tab.children[index]].height;
                        break;
                    }
                }
            }
            if (mouseX > offsetX + positionX && mouseY > offsetY + positionY && mouseX < offsetX + positionX + width && mouseY < offsetY + positionY + height) {
                canZoom = false;
                RSInterface.getInterfaceCache()[tab.children[childID]].scrollPosition += rotation * 30;
            } else {
                canZoom = true;
            }
        }
        if (Client.openInterfaceID != -1) {
            int w = 512, h = 334;
            int x = (Client.frameWidth / 2) - 256;
            int y = (Client.frameHeight / 2) - 167;
            int count = !Client.changeTabArea ? 4 : 3;
            if (Client.frameMode != Client.ScreenMode.FIXED) {
                for (int i = 0; i < count; i++) {
                    if (x + w > (Client.frameWidth - 225)) {
                        x = x - 30;
                        if (x < 0) {
                            x = 0;
                        }
                    }
                    if (y + h > (Client.frameHeight - 182)) {
                        y = y - 30;
                        if (y < 0) {
                            y = 0;
                        }
                    }
                }
            }
            RSInterface rsi = RSInterface.getInterfaceCache()[Client.openInterfaceID];
            if (Client.openInterfaceID == 60000) {
                offsetX = Client.frameMode == Client.ScreenMode.FIXED ? 4 : (Client.frameWidth / 2) - 356;
                offsetY = Client.frameMode == Client.ScreenMode.FIXED ? 4 : (Client.frameHeight / 2) - 230;
            } else {
                offsetX = Client.frameMode == Client.ScreenMode.FIXED ? 4 : x;
                offsetY = Client.frameMode == Client.ScreenMode.FIXED ? 4 : y;
            }
            for (int index = 0; index < rsi.children.length; index++) {
                RSInterface tabChild = RSInterface.getInterfaceCache()[rsi.children[index]];
                if (tabChild.dropDown != null && tabChild.dropDown.isOpen() && tabChild.dropDown.getOptions().length > 5) {
                    tabChild.dropDown.scroll += rotation;
                    if (tabChild.dropDown.scroll >= tabChild.dropDown.getOptions().length - 5) {
                        tabChild.dropDown.scroll = tabChild.dropDown.getOptions().length - 5;
                    }
                    if (tabChild.dropDown.scroll < 0) {
                        tabChild.dropDown.scroll = 0;
                    }
                    return;
                }
                if (tabChild.scrollMax > 0) {
                    childID = index;
                    positionX = rsi.childX[index];
                    positionY = rsi.childY[index];
                    width = RSInterface.getInterfaceCache()[rsi.children[index]].width;
                    height = RSInterface.getInterfaceCache()[rsi.children[index]].height;
                    break;
                }

            }

            if ((mouseX > (offsetX + positionX)) && (mouseY > (offsetY + positionY)) && (mouseX < (offsetX + positionX + width)) && (mouseY < (offsetY + positionY + height))) {
                if (RSInterface.getInterfaceCache()[rsi.children[childID]].scrollPosition > 0) {
                    RSInterface.getInterfaceCache()[rsi.children[childID]].scrollPosition += rotation * rsi.scrollSpeed;
                    if (RSInterface.getInterfaceCache()[rsi.children[childID]].scrollPosition < 0) {
                        RSInterface.getInterfaceCache()[rsi.children[childID]].scrollPosition = 0;
                    }
                    return;
                } else {
                    if (rotation > 0) {
                        RSInterface.getInterfaceCache()[rsi.children[childID]].scrollPosition += rotation * rsi.scrollSpeed;
                        if (RSInterface.getInterfaceCache()[rsi.children[childID]].scrollPosition > RSInterface.getInterfaceCache()[rsi.children[childID]].scrollMax) {
                            RSInterface.getInterfaceCache()[rsi.children[childID]].scrollPosition = RSInterface.getInterfaceCache()[rsi.children[childID]].scrollMax;
                        }
                        return;
                    }
                }
            }
            if (mouseX > offsetX + positionX && mouseY > offsetY + positionY && mouseX < offsetX + positionX + width && mouseY < offsetY + positionY + height) {
                canZoom = false;
            } else {
                canZoom = true;
            }
        }
    }

    int clickType;
    private final int LEFT = 0;
    private final int RIGHT = 1;
    private final int DRAG = 2;
    private final int RELEASED = 3;
    private final int MOVE = 4;
    private int releasedX;
    private int releasedY;
    boolean mouseWheelDown;

    public final void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int type = e.getButton();
        if (Client.frameMode != Client.ScreenMode.FIXED && clientFrame != null) {
            Insets insets = clientFrame.getInsets();
            x -= insets.left;// 4
            y -= insets.top;// 22
        }
        idleTime = 0;
        clickX = x;
        clickY = y;
        aLong29 = System.currentTimeMillis();
        if (SwingUtilities.isMiddleMouseButton(e)) {
            mouseWheelDown = true;
            mouseWheelX = x;
            mouseWheelY = y;
            return;
        }
        if (SwingUtilities.isRightMouseButton(e)) {
            clickType = RIGHT;
            clickMode1 = 2;
            clickMode2 = 2;
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            clickType = LEFT;
            clickMode1 = 1;
            clickMode2 = 1;
        }
    }

    public final void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (Client.frameMode != Client.ScreenMode.FIXED && clientFrame != null) {
            Insets insets = clientFrame.getInsets();
            x -= insets.left;// 4
            y -= insets.top;// 22
        }
        releasedX = x;
        releasedY = y;
        idleTime = 0;
        clickMode2 = 0;
        clickType = RELEASED;
        mouseWheelDown = false;
        Client.instance.releasePrayer();
    }

    public final void mouseClicked(MouseEvent mouseevent) {
    }

    public final void mouseEntered(MouseEvent mouseevent) {
    }

    public final void mouseExited(MouseEvent mouseevent) {
        idleTime = 0;
        mouseX = -1;
        mouseY = -1;
    }

    private int mouseWheelX;
    private int mouseWheelY;

    public final void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (Client.frameMode != Client.ScreenMode.FIXED && clientFrame != null) {
            Insets insets = clientFrame.getInsets();
            x -= insets.left;// 4
            y -= insets.top;// 22
        }
        if (mouseWheelDown) {
            int xx = mouseWheelX - x;
            int yy = mouseWheelY - y;
            mouseWheelDragged(xx, yy);
            mouseWheelX = x;
            mouseWheelY = y;
            return;
        }
        idleTime = 0;
        mouseX = x;
        mouseY = y;
        clickType = DRAG;
    }

    void mouseWheelDragged(int param1, int param2) {

    }

    public final void mouseMoved(MouseEvent mouseevent) {
        int x = mouseevent.getX();
        int y = mouseevent.getY();
        if (Client.frameMode != Client.ScreenMode.FIXED && clientFrame != null) {
            Insets insets = clientFrame.getInsets();
            x -= insets.left;// 4
            y -= insets.top;// 22
        }
        idleTime = 0;
        mouseX = x;
        mouseY = y;
        clickType = MOVE;
    }

    public void keyPressed(KeyEvent keyevent) {
        idleTime = 0;

        int key_code = keyevent.getKeyCode();
        int key_char = keyevent.getKeyChar();

        if(keyevent.isShiftDown()){
            Client.shiftIsDown = true;
        }

        //keybind
        Client.instance.keybindManager.handleBind(key_code);

        if (key_char == 96)
            console.openConsole = !console.openConsole;
        if (console.openConsole && (key_code == KeyEvent.VK_PAGE_UP || key_code == KeyEvent.VK_PAGE_DOWN))
            console.chooseCommand(key_code == KeyEvent.VK_PAGE_UP);
        if (keyevent.isControlDown()) {
            Client.controlIsDown = true;
        }

        //Ctrl V
        if ((keyevent.isControlDown() && keyevent.getKeyCode() == KeyEvent.VK_V)) {
            String clipboard = Client.getClipboardContents();

            if (Client.loggedIn) {
                if (Client.inputString.length() + clipboard.length() >= 128)
                    return;
                Client.inputString += clipboard;
            } else if (Client.instance.loginRenderer.getScreen(ScreenType.MAIN)) {
                if (Client.instance.loginScreenCursorPos == 0) {
                    Client.instance.myUsername += clipboard;
                    if (Client.instance.myUsername.length() > 28) {
                        Client.instance.myUsername = Client.instance.myUsername.substring(0, 28);
                    }
                } else if (Client.instance.loginScreenCursorPos == 1) {
                    Client.instance.myPassword += clipboard;
                    if (Client.instance.myPassword.length() > 20) {
                        Client.instance.myPassword = Client.instance.myPassword.substring(0, 20);
                    }
                }
            }
        }
        if ((keyevent.isControlDown() && keyevent.getKeyCode() == KeyEvent.VK_X)) {
            Client.setClipboardContents(Client.inputString);
            Client.inputString = "";
        }

        if (key_char < 30)
            key_char = 0;
        if (key_code == 37)
            key_char = 1;
        if (key_code == 39)
            key_char = 2;
        if (key_code == 38)
            key_char = 3;
        if (key_code == 40)
            key_char = 4;
        if (key_code == 17)
            key_char = 5;
        if (key_code == 8)
            key_char = 8;
        if (key_code == 127)
            key_char = 8;
        if (key_code == 9)
            key_char = 9;
        if (key_code == 10)
            key_char = 10;
        if (key_code >= 112 && key_code <= 123)
            key_char = (1008 + key_code) - 112;
        if (key_code == 36)
            key_char = 1000;
        if (key_code == 35)
            key_char = 1001;
        if (key_code == 33)
            key_char = 1002;
        if (key_code == 34)
            key_char = 1003;
        if (key_char > 0 && key_char < 128)
            keyArray[key_char] = 1;
        if (key_char > 4) {
            charQueue[writeIndex] = key_char;
            writeIndex = writeIndex + 1 & 0x7f;
        }
    }

    public final void keyReleased(KeyEvent keyevent) {
        idleTime = 0;
        int i = keyevent.getKeyCode();
        char c = keyevent.getKeyChar();

        if (i == KeyEvent.VK_SHIFT) {
            Client.shiftIsDown = false;
        }

        if (c < '\036')
            c = '\0';
        if (i == 37)
            c = '\001';
        if (i == 39)
            c = '\002';
        if (i == 38)
            c = '\003';
        if (i == 40)
            c = '\004';
        if (i == 17)
            c = '\005';
        if (i == 8)
            c = '\b';
        if (i == 127)
            c = '\b';
        if (i == 9)
            c = '\t';
        if (i == 10)
            c = '\n';
        if (c > 0 && c < '\200')
            keyArray[c] = 0;
        if (i == KeyEvent.VK_CONTROL) {
            Client.controlIsDown = false;
            Client.instance.prayerGrabbed = null;
        }
    }

    public final void keyTyped(KeyEvent keyevent) {
    }

    public final int readChar(int dummy) {
        while (dummy >= 0) {
            for (int j = 1; j > 0; j++)
                ;
        }
        int k = -1;
        if (writeIndex != readIndex) {
            k = charQueue[readIndex];
            readIndex = readIndex + 1 & 0x7f;
        }
        return k;
    }

    public final void focusGained(FocusEvent focusevent) {
        awtFocus = true;
        shouldClearScreen = true;
        raiseWelcomeScreen();
    }

    public final void focusLost(FocusEvent focusevent) {
        awtFocus = false;
        for (int i = 0; i < 128; i++) {
            keyArray[i] = 0;
        }

    }

    public final void windowActivated(WindowEvent windowevent) {
    }

    public final void windowClosed(WindowEvent windowevent) {
    }

    public final void windowClosing(WindowEvent windowevent) {
        destroy();

    }

    public final void windowDeactivated(WindowEvent windowevent) {
    }

    public final void windowDeiconified(WindowEvent windowevent) {
    }

    public final void windowIconified(WindowEvent windowevent) {
    }

    public final void windowOpened(WindowEvent windowevent) {
    }

    void startUp() {
    }

    void processGameLoop() {
    }

    void cleanUpForQuit() {
    }

    void processDrawing() {
    }

    void raiseWelcomeScreen() {
    }

    Component getGameComponent() {
        if (clientFrame != null && !isApplet) {
            return clientFrame;
        } else {
            return this;
        }
    }

    public void startRunnable(Runnable runnable, int i) {
        Thread thread = new Thread(runnable);
        thread.start();
        thread.setPriority(i);
    }

    void drawLoadingText(int percentage, String loadingText) {
        while (graphics == null) {
            graphics = (isApplet ? this : clientFrame).getGraphics();
            try {
                getGameComponent().repaint();
            } catch (Exception ignored) {
            }
            try {
                Thread.sleep(1000L);
            } catch (Exception ignored) {
            }
        }
        Font font = new Font("Helvetica", 1, 13);
        FontMetrics fontmetrics = getGameComponent().getFontMetrics(font);
        Font font1 = new Font("Helvetica", 0, 13);
        FontMetrics fontmetrics1 = getGameComponent().getFontMetrics(font1);
        if (shouldClearScreen) {
            graphics.setColor(Color.black);
            graphics.fillRect(0, 0, Client.frameWidth, Client.frameHeight);
            shouldClearScreen = false;
        }
        Color color = new Color(140, 17, 17);
        int y = Client.frameHeight / 2 - 18;
        graphics.setColor(color);
        graphics.drawRect(Client.frameWidth / 2 - 152, y, 304, 34);
        graphics.fillRect(Client.frameWidth / 2 - 150, y + 2, percentage * 3, 30);
        graphics.setColor(Color.black);
        graphics.fillRect((Client.frameWidth / 2 - 150) + percentage * 3, y + 2, 300 - percentage * 3, 30);
        graphics.setFont(font);
        graphics.setColor(Color.white);
        graphics.drawString(loadingText, (Client.frameWidth - fontmetrics.stringWidth(loadingText)) / 2, y + 22);
        graphics.drawString("", (Client.frameWidth - fontmetrics1.stringWidth("")) / 2, y - 8);
    }

    GameApplet() {
        delayTime = 20;
        minDelay = 1;
        shouldDebug = false;
        shouldClearScreen = true;
        awtFocus = true;
    }

}
