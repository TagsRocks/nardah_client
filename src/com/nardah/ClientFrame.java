package com.nardah;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


/**
 * Creates a new user interface to render the client
 *
 * @author Zion
 */
public class ClientFrame extends Client {

    /** The default serialized version */
    private static final long serialVersionUID = 1L;

    /** Create our frame */
    public JFrame frame;

    /** Create our game panel to render the client */
    private JPanel gamePanel;

    /** Our jpanel for the menu bar */
    private JPanel menuPanel;

    /** Handles the hovers of the nav buttons */
    private int leftHovers = -1;
    private int rightHovers = -1;

    /** Handles the logo component */
    private static Image LOGO;
    private Image scaledLogo = LOGO.getScaledInstance(220, 53, Image.SCALE_SMOOTH);

    private static Image BG;
    private Image scaledBG;

    private static Image BUTTON_IMAGE;
    private static Image BUTTON_HOVER_IMAGE;

    /** Handles the task bar icon component */
    private static Image ICON;

    private static final int TEXT_COLOR = 0xF7D878;

    /** Stores the names of the button labels */
    private String[] leftLabels = {"Website", "Community", "Discord"};
    private String[] rightLabels = {"Vote", "Store", "Hiscores"};

    /** Stores the text to draw onto tooltips */
    private String[] leftTooltips = {"Opens the " + Configuration.NAME + " forums.", "Opens the " + Configuration.NAME + " voting page.", "Opens the " + Configuration.NAME + " hiscores."};
    private String[] rightTooltips = {"Opens the " + Configuration.NAME + " support page.", "Opens the " + Configuration.NAME + " FAQ page.", "Opens the " + Configuration.NAME + " store."};

    /** Creates a new jframe to render the client */
    ClientFrame() {
        try {
            initializeUserInterface();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Loads all the client frame resources. */
    public static void load() {
        //TODO Better means grabbing from site takes too long
        try {
            ICON = ImageIO.read(ClientFrame.class.getResource("/images/icon.png"));
            LOGO = ImageIO.read(ClientFrame.class.getResource("/images/logo.png"));
            BG = ImageIO.read(ClientFrame.class.getResource("/images/bg.png"));
            BUTTON_IMAGE = ImageIO.read(ClientFrame.class.getResource("/images/button.png"));
            BUTTON_HOVER_IMAGE = ImageIO.read(ClientFrame.class.getResource("/images/hover.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Initializes the jframe */
    private void initializeUserInterface() {
        try {
            JPopupMenu.setDefaultLightWeightPopupEnabled(true);
            UIManager.put("InternalFrame.activeTitleBackground", new ColorUIResource(Color.black));
            UIManager.put("InternalFrame.activeTitleForeground", new ColorUIResource(Color.WHITE));
            UIManager.put("InternalFrame.titleFont", new Font("Dialog", Font.PLAIN, 11));

			/*
             * Initialize the jframe which will hold everything
			 */
            frame = new JFrame(Configuration.NAME + " - where legends go after RS got shit");
            frame.setLayout(new BorderLayout());
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
            frame.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);

            Set<KeyStroke> forwardKeys = new HashSet<KeyStroke>(1);
            forwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.CTRL_MASK));
            setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

            Set<KeyStroke> backwardKeys = new HashSet<KeyStroke>(1);
            backwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));
            setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);

			/*
             * Set our frame size
			 */
            int width = 765;
            int height = 503;
            Insets insets = getInsets();
            frame.setSize(width + insets.left + insets.right, height + insets.top + insets.bottom);
            /*
             * Set the location to the center of the screen
			 */
            frame.setLocationRelativeTo(null);

			/*
			 * Add our window listener to check when the x is clicked
			 */
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent we) {
                    String options[] = {"Yes", "No"};
                    int userPrompt = JOptionPane.showOptionDialog(null, "Are you sure you wish to exit?", Configuration.NAME, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                    if (userPrompt == JOptionPane.YES_OPTION) {
                        Settings.save();
                        System.exit(0);
                    }
                }

                @Override
                public void windowGainedFocus(WindowEvent e) {
                    getGamePanel().requestFocusInWindow();
                    getGamePanel().requestFocus();
                }

            });

            if (ICON != null) {
                frame.setIconImage(ICON);
            }

			/*
			 * Initialize our menu bar to be displayed on top
			 */
           // initializeMenuBar();

			/*
			 * We initialize our game panel
			 */
            initializeGamePanel();

			/*
			 * Set the frame as non focusable
			 */
            frame.setFocusable(false);

			/*
			 * Pack the frame to remove any empty space and fit the applet
			 */
            frame.pack();

			/*
			 * Finally set our frame to visible
			 */
            frame.setVisible(true);

			/*
			 * Create our applet
			 */
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Initializes the game panel to render the applet */
    private void initializeGamePanel() {

		/*
		 * Initialize
		 */
        setGamePanel(new JPanel());

		/*
		 * Set the layout to border layout
		 */
        getGamePanel().setLayout(new BorderLayout());

		/*
		 * Add the client to the game panel
		 */
        getGamePanel().add(this);

		/*
		 * Set the size of the game panel
		 */
        Dimension dimension = new Dimension(765, 503);
        getGamePanel().setPreferredSize(dimension);
        getGamePanel().setSize(dimension);

		/*
		 * Set the game panel as focusable
		 */
        getGamePanel().setFocusable(false);

		/*
		 * Request the focus to the game panel
		 */
        getGamePanel().requestFocus();

		/*
		 * Disable focus traversal keys
		 */
        getGamePanel().setFocusTraversalKeysEnabled(false);

        getGamePanel().setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        getGamePanel().setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);

		/*
		 * Place the game panel which will render the applet in the center
		 */
        frame.getContentPane().add(getGamePanel(), BorderLayout.CENTER);
   // }

    /** Initializes the menu bar */
 //   private void initializeMenuBar() {

		/*
		 * Initialize our menu panel to hold our menu buttons
		 */
        menuPanel = new JPanel();

		/*
		 * Set the menu panel as non focusable
		 */
        menuPanel.setFocusable(false);

		/*
		 * Disable focus traversal keys
		 */
        menuPanel.setFocusTraversalKeysEnabled(false);

        menuPanel.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        menuPanel.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);

		/*
		 * Create our menu panel
		 */
        menuPanel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                int x = (this.getWidth() / 2 - (765 / 2)) + 2;
                int x2 = (this.getWidth() / 2 - 79) + (765 / 2);
                g.setColor(new Color(0x221F19));
                scaledBG = BG.getScaledInstance(this.getWidth(), 55, Image.SCALE_SMOOTH);
                g.drawImage(scaledBG, 0, 0, null);
                g.drawImage(scaledLogo, this.getWidth() / 2 - 105, 2, null);

                /*private final*/
                int[] TEXT_LEFT_POSITION_X = {7, 14, 22};
                    /*private final*/
                int[] TEXT_RIGHT_POSITION_X = {7, 16, 22};

                for (int i = 0; i < 3; i++) {
                    g.setFont(new Font("Verdana", Font.PLAIN, 12));
                    g.drawImage(leftHovers == i ? BUTTON_IMAGE : BUTTON_HOVER_IMAGE, x + (78 * i), 19, null);
                    g.drawImage(rightHovers == i ? BUTTON_IMAGE : BUTTON_HOVER_IMAGE, x2 - (78 * i), 19, null);
//
                    drawCenteredString(g, leftLabels[i], (x + 32) + (70 * i) + TEXT_LEFT_POSITION_X[i], 34, TEXT_COLOR);
                    drawCenteredString(g, rightLabels[i], (x2 + 45) - (70 * i) - TEXT_RIGHT_POSITION_X[i], 34, TEXT_COLOR);
                                        if (leftHovers == i) {
                                            menuPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
//                                            drawTooltip(g, leftTooltips[i], x + (67 * i) + random(6), 15 - random(6));
                                        }
                                        if (rightHovers == i) {
                                            int xOffset = 0;
                                            if (rightHovers == 1) {
                                                xOffset += 100;
                                            } else if (rightHovers == 0) {
                                                xOffset += 175;
                                            }
                                            menuPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
//                                            drawTooltip(g, rightTooltips[i], x2 - xOffset - (67 * i) + random(6), 15 - random(6));
                                        }
                }
            }
        };

		/*
		 * Add our menu panel to our frame
		 */
        NavListener navListener = new NavListener(menuPanel);
        menuPanel.addMouseMotionListener(navListener);
        menuPanel.addMouseListener(navListener);
        menuPanel.setPreferredSize(new Dimension(765, 55));
        menuPanel.setMinimumSize(new Dimension(765, 55));
        frame.getContentPane().add(menuPanel, BorderLayout.NORTH);
    }

    /** Generates a random number */
    public static int random(int range) {
        return (int) (java.lang.Math.random() * (range + 1));
    }

    /** Draws an RS2 tooltip */
    private void drawTooltip(Graphics g, String text, int x, int y) {
        g.setColor(new Color(0xFFFFA0));
        g.fillRect(x, y, g.getFontMetrics().stringWidth(text) + 5, 15);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, g.getFontMetrics().stringWidth(text) + 5, 15);
        g.setColor(Color.BLACK);
        g.drawString(text, x + 2, y + 12);
    }

    /** Draws a string centered to the 'x' position */
    private void drawCenteredString(Graphics g, String text, int x, int y, int color) {
        int width = g.getFontMetrics().stringWidth(text);
        g.setColor(Color.BLACK);
        g.drawString(text, x - width / 2 + 2, y + 2);
        g.setColor(new Color(color));
        g.drawString(text, x - width / 2, y);
    }


    /** Resizes the frame when switching between modes */
    void resize(ScreenMode frameMode, int width, int height) {
        try {
            if (frameMode == ScreenMode.FIXED) {
                width = 765;
                height = 503;
            } else if (frameMode == ScreenMode.RESIZABLE) {
                width = width;
                height = height;
            } else {
                width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
                height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
            }

            Dimension dimension = new Dimension(width, height);

            gamePanel.setPreferredSize(dimension);

            frame.setSize(dimension);
            frame.setMinimumSize(dimension);

            Insets insets = frame.getInsets();
            int widthModifier = 0 + insets.left + insets.right;
            int heightModifier = menuPanel.getHeight();
            if (frameMode != ScreenMode.FULLSCREEN) {
                frame.setBounds(0, 0, width + widthModifier, height + heightModifier);
            }

            super.myWidth = width;
            super.myHeight = height;

            frame.setResizable(frameMode == ScreenMode.RESIZABLE);
            if (frameMode == ScreenMode.FIXED) {
                frame.pack();
            }
            frame.setLocationRelativeTo(null);
            frame.requestFocus();
            frame.toFront();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel getGamePanel() {
        return gamePanel;
    }

    private void setGamePanel(JPanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public JPanel getMenuPanel() {
        return menuPanel;
    }

    public void setTitle(String title) {
        frame.setTitle(title);
    }

    public JFrame getFrame() {
        return frame;
    }



    private final class NavListener extends MouseAdapter {

        private final JPanel panel;

        NavListener(JPanel panel) {
            this.panel = panel;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            int x_left = (panel.getWidth() / 2 - (765 / 2));
            int x_right = (panel.getWidth() / 2) + (765 / 2) + 2;
            int leftPos = (e.getX() - x_left) / 72;
            int rightPos = (e.getX() - x_right) / 72;
            if (rightPos == -1) {
                rightPos = 1;
            } else if (rightPos == -2) {
                rightPos = 2;
            }
            if (leftPos != leftHovers) {
                leftHovers = leftPos;
                panel.repaint();
            } else if (rightPos != rightHovers) {
                rightHovers = rightPos;
                panel.repaint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            int x_left = (panel.getWidth() / 2 - (765 / 2));
            int x_right = (panel.getWidth() / 2) + (765 / 2) + 2;
            int leftPos = (e.getX() - x_left) / 72;
            int rightPos = (e.getX() - x_right) / 72;
            if (rightPos == -1) {
                rightPos = 1;
            } else if (rightPos == -2) {
                rightPos = 2;
            }
            if (leftPos != leftHovers) {
                leftHovers = leftPos;
                panel.repaint();
            } else if (rightPos != rightHovers) {
                rightHovers = rightPos;
                panel.repaint();
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            int leftPos = leftHovers;
            int rightPos = rightHovers;
            leftHovers = -1;
            rightHovers = -1;
            if (leftPos != leftHovers) {
                panel.repaint();
            } else if (rightPos != rightHovers) {
                panel.repaint();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                int x_left = (panel.getWidth() / 2 - (765 / 2));
                int x_right = (panel.getWidth() / 2) + (765 / 2) + 2;
                int tabs_left = (e.getX() - x_left) / 72;
                int tabs_right = (e.getX() - x_right) / 72;
                if (tabs_right == -1) {
                    tabs_right = 1;
                } else if (tabs_right == -2) {
                    tabs_right = 2;
                }
             //   if (tabs_left > -1 && tabs_left < Configuration.LEFT_NAV_LINKS.length) {
             //       launchURL(Configuration.LEFT_NAV_LINKS[tabs_left]);
               // } else if (tabs_right > -1 && tabs_right < Configuration.RIGHT_NAV_LINKS.length) {
               //     launchURL(Configuration.RIGHT_NAV_LINKS[tabs_right]);
                }
            }
        }
    }
//}

