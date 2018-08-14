package com.nardah;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ColorUIResource;

import com.nardah.osbuddy.CalcSubClass;
import com.nardah.osbuddy.ComponentResizer;
import com.nardah.osbuddy.parser.OSBHighscoresParser;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Creates a new user interface to render the client
 *
 * @author Zion
 */
public class OSBClientFrame extends ClientFrame {

    /**
     * The default serialized version
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create our frame
     */
    public JFrame frame;

    /**
     * Create our game panel to render the client
     */
    private JPanel gamePanel;

    /**
     * Our jpanel for the menu bar
     */
    private JPanel menuPanel;

    /**
     * Handles the hovers of the nav buttons
     */
    private int leftHovers = -1;
    private int rightHovers = -1;

    /**
     * Handles the logo component
     */
    private static Image LOGO;
    private Image scaledLogo = LOGO.getScaledInstance(220, 53, Image.SCALE_SMOOTH);

    private static Image BG;
    private Image scaledBG;

    private static Image BUTTON_IMAGE;
    private static Image BUTTON_HOVER_IMAGE;

    /**
     * Handles the task bar icon component
     */
    private static Image ICON;

    private static final int TEXT_COLOR = 0xF7D878;

    private JTextField stat_entryName;
    static JPanel hiscorePanel = new JPanel();
    static JPanel buttonPanel = new JPanel();
    public static JLabel stat[] = new JLabel[24];
    public static JLabel player_name = new JLabel("-");
    public static JLabel stat_total = new JLabel("-");
    public static JLabel titleBar_title = new JLabel("OSBuddy");
    static JLabel stat_rank = new JLabel("Rank: 338,933");
    static JLabel stat_exp = new JLabel("Experience: 19,878");
    static JLabel stat_xpToLevel = new JLabel("Exp to level (34): 346");
    public static JLabel stat_name = new JLabel("Stat: Hunter");
    static JProgressBar progressBar = new JProgressBar();
    public static JLabel stat_combat = new JLabel("-");
    private JPanel contentPane;
    private String loadType;

    /**
     * Stores the names of the button labels
     */
    private String[] leftLabels = {"Website", "Community", "Discord"};
    private String[] rightLabels = {"Vote", "Store", "Hiscores"};

    public static final String[] SKILL_NAME = {"Attack", "Defence", "Strength", "Hitpoints", "Ranged", "Prayer",
            "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining",
            "Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecrafting", "Hunter", "Construction",
            "Summoning"};

    /**
     * Stores the text to draw onto tooltips
     */
    private String[] leftTooltips = {"Opens the " + Configuration.NAME + " forums.", "Opens the " + Configuration.NAME + " voting page.", "Opens the " + Configuration.NAME + " hiscores."};
    private String[] rightTooltips = {"Opens the " + Configuration.NAME + " support page.", "Opens the " + Configuration.NAME + " FAQ page.", "Opens the " + Configuration.NAME + " store."};

    /**
     * Creates a new jframe to render the client
     */
    OSBClientFrame() {
        try {
            initializeUserInterface();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Loads all the client frame resources.
     */
    public static void load() {
        //TODO Better means grabbing from site takes too long
        try {
            ICON = ImageIO.read(OSBClientFrame.class.getResource("/images/icon.png"));
            LOGO = ImageIO.read(OSBClientFrame.class.getResource("/images/logo.png"));
            BG = ImageIO.read(OSBClientFrame.class.getResource("/images/bg.png"));
            BUTTON_IMAGE = ImageIO.read(OSBClientFrame.class.getResource("/images/button.png"));
            BUTTON_HOVER_IMAGE = ImageIO.read(OSBClientFrame.class.getResource("/images/hover.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the jframe
     */
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
            frame.setUndecorated(true);
            frame.setLayout(new BorderLayout());
            frame.setResizable(true);
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
            frame.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);

            UIDefaults uid = UIManager.getDefaults();
            // create a resource
            ColorUIResource thumbColor = new ColorUIResource(new Color(34, 34, 34));
            // replace the old resource.

            for (int i = 1; i < 24; i++) {
                stat[i] = new JLabel();
                stat[i].setText("-");
                stat[i].setHorizontalAlignment(SwingConstants.CENTER);
                stat[i].setFont(new Font("Arial", Font.BOLD, 11));
                stat[i].setForeground(Color.LIGHT_GRAY);
                stat[i].addMouseListener(hover(i));
            }

            uid.put("ScrollBar.thumb", thumbColor);
            uid.put("ScrollBar.track", thumbColor);
            uid.put("scrollbar", thumbColor);
            uid.put("ScrollBar.border", BorderFactory.createEmptyBorder());

            Set<KeyStroke> forwardKeys = new HashSet<KeyStroke>(1);
            forwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.CTRL_MASK));
            setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

            Set<KeyStroke> backwardKeys = new HashSet<KeyStroke>(1);
            backwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));
            setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);

            /*
             * Set our frame size
             */
            int width = !expanded ? 773 : 1037;
            int height = 550;
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
             * We initialize our game panel
             */
            initializeGamePanel();

//            contentPane = new JPanel();
//            frame.getContentPane().setBackground(new Color(34, 34, 34));
//            contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
//            frame.add(contentPane);
//            contentPane.setLayout(null);

            gamePanel.setBackground(new Color(34, 34, 34));
            gamePanel.setBorder(new EmptyBorder(5, 5, 5, 5));

         /*   JPanel titleBar = new JPanel();
            titleBar.setOpaque(false);
            titleBar.setBounds(0, 0, 1037, 27);
            frame.getContentPane().add(titleBar, BorderLayout.NORTH);
            titleBar.setLayout(null);

            JButton titleBar_settingsButton = new JButton("");
            titleBar_settingsButton
                    .setRolloverSelectedIcon(null);
            titleBar_settingsButton
                    .setSelectedIcon(null);
            titleBar_settingsButton.setFocusPainted(false);
            titleBar_settingsButton.setRolloverIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_settings_h.png"));
            titleBar_settingsButton.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_settings.png"));
            System.out.println("This is broken.");
            titleBar_settingsButton.setContentAreaFilled(false);
            titleBar_settingsButton.setBorderPainted(false);
            titleBar_settingsButton.setBorder(null);
            titleBar_settingsButton.setBounds(914, 6, 21, 23);
            if (!expanded)
                titleBar_settingsButton.setBounds(titleBar_settingsButton.getX() - 263, titleBar_settingsButton.getY(), titleBar_settingsButton.getWidth(), titleBar_settingsButton.getHeight());

            titleBar.add(titleBar_settingsButton);*/


            /*
             * Set the frame as non focusable
             */
            frame.setFocusable(true);

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

    /**
     * Initializes the game panel to render the applet
     */
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

        getMenuPanel().setLayout(new BorderLayout());

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
       /* menuPanel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                int x = (this.getWidth() / 2 - (765 / 2)) + 2;
                int x2 = (this.getWidth() / 2 - 79) + (765 / 2);
                g.setColor(new Color(34, 34, 34));
                scaledBG = BG.getScaledInstance(this.getWidth(), 55, Image.SCALE_SMOOTH);
//                g.drawImage(scaledBG, 0, 0, null);
//                g.drawImage(scaledLogo, this.getWidth() / 2 - 105, 2, null);

                *//*private final*//*
                int[] TEXT_LEFT_POSITION_X = {7, 14, 22};
                *//*private final*//*
                int[] TEXT_RIGHT_POSITION_X = {7, 16, 22};

                for (int i = 0; i < 3; i++) {
                    g.setFont(new Font("Verdana", Font.PLAIN, 12));
                    g.drawImage(leftHovers == i ? BUTTON_IMAGE : BUTTON_HOVER_IMAGE, x + (78 * i), 19, null);
                    g.drawImage(rightHovers == i ? BUTTON_IMAGE : BUTTON_HOVER_IMAGE, x2 - (78 * i), 19, null);
//
                    drawCenteredString(g, leftLabels[i], (x + 32) + (70 * i) + TEXT_LEFT_POSITION_X[i], 34, TEXT_COLOR);
                    drawCenteredString(g, rightLabels[i], (x2 + 45) - (70 * i) - TEXT_RIGHT_POSITION_X[i], 34, TEXT_COLOR);
//                    if (leftHovers == i) {
//                        menuPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
//                        drawTooltip(g, leftTooltips[i], x + (67 * i) + random(6), 15 - random(6));
//                    }
//                    if (rightHovers == i) {
//                        int xOffset = 0;
//                        if (rightHovers == 1) {
//                            xOffset += 100;
//                        } else if (rightHovers == 0) {
//                            xOffset += 175;
//                        }
//                        menuPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
//                        drawTooltip(g, rightTooltips[i], x2 - xOffset - (67 * i) + random(6), 15 - random(6));
//                    }
                }
            }
        };*/
        menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setBounds(0, 0, 1037, 27);
//        contentPane.add(menuPanel, BorderLayout.NORTH);
//        menuPanel.setBackground(new Color(34,34,34));
        menuPanel.setLayout(null);

        JPanel titleBar = new JPanel();
        titleBar.setOpaque(false);
        titleBar.setBounds(0, 0, 1037, 27);
        menuPanel.add(titleBar, BorderLayout.NORTH);
        titleBar.setLayout(null);

        JButton titleBar_settingsButton = new JButton("");
        titleBar_settingsButton
                .setRolloverSelectedIcon(null);
        titleBar_settingsButton
                .setSelectedIcon(null);
        titleBar_settingsButton.setFocusPainted(false);
        titleBar_settingsButton.setRolloverIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_settings_h.png"));
        titleBar_settingsButton.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_settings.png"));
        System.out.println("This is broken.");
        titleBar_settingsButton.setContentAreaFilled(false);
        titleBar_settingsButton.setBorderPainted(false);
        titleBar_settingsButton.setBorder(null);
        titleBar_settingsButton.setBounds(914, 6, 21, 23);
        if (!expanded)
            titleBar_settingsButton.setBounds(titleBar_settingsButton.getX() - 263, titleBar_settingsButton.getY(), titleBar_settingsButton.getWidth(), titleBar_settingsButton.getHeight());

        menuPanel.add(titleBar_settingsButton);

        JButton titleBar_discordButton = new JButton("");
        titleBar_discordButton.setRolloverIcon(new ImageIcon(Utility.findcachedir() + "/osb//osb_discord_h.png"));
        titleBar_discordButton.setIcon(new ImageIcon(Utility.findcachedir() + "/osb//osb_discord.png"));
        titleBar_discordButton.setFocusPainted(false);
        titleBar_discordButton.setContentAreaFilled(false);
        titleBar_discordButton.setBorderPainted(false);
        titleBar_discordButton.setBorder(null);
        titleBar_discordButton.setBounds(841, 4, 21, 26);
        if (!expanded)
            titleBar_discordButton.setBounds(titleBar_discordButton.getX() - 263, titleBar_discordButton.getY(), titleBar_discordButton.getWidth(), titleBar_discordButton.getHeight());

        menuPanel.add(titleBar_discordButton);
        titleBar_discordButton.addActionListener(a -> {
            try {
                Desktop.getDesktop().browse(new URL(Configuration.DISCORD_LINK).toURI());
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });

        JButton titleBar_linksButton = new JButton("");
        titleBar_linksButton.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_links.png"));
        titleBar_linksButton.setRolloverIcon(new ImageIcon(Utility.findcachedir() + "/osb/images/osb_links_h.png"));
        titleBar_linksButton.setFocusPainted(false);
        titleBar_linksButton.setContentAreaFilled(false);
        titleBar_linksButton.setBorderPainted(false);
        titleBar_linksButton.setBorder(null);
        titleBar_linksButton.setBounds(866, 4, 21, 26);
        if (!expanded)
            titleBar_linksButton.setBounds(titleBar_linksButton.getX() - 263, titleBar_linksButton.getY(), titleBar_linksButton.getWidth(), titleBar_linksButton.getHeight());

        menuPanel.add(titleBar_linksButton);
        titleBar_linksButton.addActionListener(a -> {
            try {
                Desktop.getDesktop().browse(new URL(Configuration.WEBSITE_LINK).toURI());
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });

        JButton titleBar_closeButton = new JButton("");
        titleBar_closeButton.setRolloverIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_close_h.png"));
        titleBar_closeButton.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_close.png"));
        titleBar_closeButton.setFocusPainted(false);
        titleBar_closeButton.setContentAreaFilled(false);
        titleBar_closeButton.setBorderPainted(false);
        titleBar_closeButton.setBorder(null);
        titleBar_closeButton.setBounds(1000, 2, 33, 26);
        if (!expanded)
            titleBar_closeButton.setBounds(titleBar_closeButton.getX() - 263, titleBar_closeButton.getY(), titleBar_closeButton.getWidth(), titleBar_closeButton.getHeight());
        menuPanel.add(titleBar_closeButton);
        titleBar_closeButton.addActionListener(a -> {
            System.exit(0);
        });


        JButton titleBar_maxButton = new JButton("");
        titleBar_maxButton.setRolloverIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_max_h.png"));
        titleBar_maxButton.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_max.png"));
        titleBar_maxButton.setFocusPainted(false);
        titleBar_maxButton.setContentAreaFilled(false);
        titleBar_maxButton.setBorderPainted(false);
        titleBar_maxButton.setBorder(null);
        titleBar_maxButton.setBounds(967, 4, 33, 21);
        if (!expanded)
            titleBar_maxButton.setBounds(titleBar_maxButton.getX() - 263, titleBar_maxButton.getY(), titleBar_maxButton.getWidth(), titleBar_maxButton.getHeight());
        menuPanel.add(titleBar_maxButton);
        titleBar_maxButton.addActionListener(a -> {
//            resize(ScreenMode.RESIZABLE, 1033, 765);
//            gamePanel.setSize(1033, 765);
            frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            gamePanel.requestFocus();

           /* Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setSize(screenSize.width, screenSize.height);*/
        });

        JButton titleBar_minButton = new JButton("");
        titleBar_minButton.setRolloverIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_minimize_h.png"));
        titleBar_minButton.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_minimize.png"));
        titleBar_minButton.setFocusPainted(false);
        titleBar_minButton.setContentAreaFilled(false);
        titleBar_minButton.setBorderPainted(false);
        titleBar_minButton.setBorder(null);
        titleBar_minButton.setBounds(/*943, 1, 33, 26*/943, 4, 33, 21);
        if (!expanded)
            titleBar_minButton.setBounds(titleBar_minButton.getX() - 263, titleBar_minButton.getY(), titleBar_minButton.getWidth(), titleBar_minButton.getHeight());
        titleBar_minButton.addActionListener(a -> frame.setState(JFrame.ICONIFIED));

        menuPanel.add(titleBar_minButton);

        JButton titleBar_collapseButton = new JButton("");
        titleBar_collapseButton.addActionListener(arg0 -> {
            expanded = !expanded;
            int denom = expanded ? 263 : -263;
            hiscorePanel.setVisible(expanded);
            buttonPanel.setVisible(expanded);
            if (!expanded)
                setSize(773, 532);
            else
                setSize(1037, 532);

            titleBar_closeButton.setLocation(titleBar_closeButton.getX() + denom, titleBar_closeButton.getY());
            titleBar_minButton.setLocation(titleBar_minButton.getX() + denom, titleBar_minButton.getY());
            titleBar_maxButton.setLocation(titleBar_maxButton.getX() + denom, titleBar_maxButton.getY());
            titleBar_discordButton.setLocation(titleBar_discordButton.getX() + denom, titleBar_discordButton.getY());
//            titleBar_cameraButton.setLocation(titleBar_cameraButton.getX() + denom, titleBar_cameraButton.getY());
            titleBar_linksButton.setLocation(titleBar_linksButton.getX() + denom, titleBar_linksButton.getY());
            titleBar_collapseButton.setLocation(titleBar_collapseButton.getX() + denom, titleBar_collapseButton.getY());
            if (!expanded)
                titleBar_collapseButton.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/titleBar_expand.png"));
            else
                titleBar_collapseButton.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/titleBar_collapse.png"));

            titleBar_settingsButton.setLocation(titleBar_settingsButton.getX() + denom, titleBar_settingsButton.getY());
        });
        if (!expanded)
            titleBar_collapseButton.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/titleBar_expand.png"));
        else
            titleBar_collapseButton.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/titleBar_collapse.png"));


        titleBar_collapseButton.setFocusPainted(false);
        titleBar_collapseButton.setContentAreaFilled(false);
        titleBar_collapseButton.setBorderPainted(false);
        titleBar_collapseButton.setBorder(null);
        titleBar_collapseButton.setBounds(893, 8, 16, 16);
        if (!expanded)
            titleBar_collapseButton.setBounds(titleBar_collapseButton.getX() - 263, titleBar_collapseButton.getY(), titleBar_collapseButton.getWidth(), titleBar_collapseButton.getHeight());
        titleBar.add(titleBar_collapseButton);


        JLabel titleBar_icon = new JLabel("");
        titleBar_icon.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/title_icon.png"));
        titleBar_icon.setBounds(10, 2, 36, 24);
        titleBar.add(titleBar_icon);


        titleBar_title.setForeground(Color.LIGHT_GRAY);
        titleBar_title.setFont(new Font("Arial", Font.BOLD, 11));
        titleBar_title.setBounds(37, 7, 54, 14);
        titleBar.add(titleBar_title);

        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(1002, 29, 46, 512);
        menuPanel.add(buttonPanel);
        buttonPanel.setLayout(null);

        JButton button = new JButton("");
        button.setRolloverIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_news_h.png"));
        button.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_news.png"));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setBorder(null);
        button.setBounds(0, 0, 31, 30);
        buttonPanel.add(button);

        JButton button_1 = new JButton("");
        button_1.setRolloverIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_world_h.png"));
        button_1.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_world.png"));
        button_1.setFocusPainted(false);
        button_1.setContentAreaFilled(false);
        button_1.setBorderPainted(false);
        button_1.setBorder(null);
        button_1.setBounds(0, 30, 31, 30);
        buttonPanel.add(button_1);

        JButton button_2 = new JButton("");
        button_2.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_highscores_h.png"));
        button_2.setRolloverIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_highscores_h.png"));
        button_2.setFocusPainted(false);
        button_2.setContentAreaFilled(false);
        button_2.setBorderPainted(false);
        button_2.setBorder(null);
        button_2.setBounds(0, 60, 31, 30);
        buttonPanel.add(button_2);

        JButton button_4 = new JButton("");
        button_4.setRolloverIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_tracker_h.png"));
        button_4.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_tracker.png"));
        button_4.setFocusPainted(false);
        button_4.setContentAreaFilled(false);
        button_4.setBorderPainted(false);
        button_4.setBorder(null);
        button_4.setBounds(0, 90, 31, 30);
        buttonPanel.add(button_4);

        JButton button_5 = new JButton("");
        button_5.setRolloverIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_ge_h.png"));
        button_5.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_ge.png"));
        button_5.setFocusPainted(false);
        button_5.setContentAreaFilled(false);
        button_5.setBorderPainted(false);
        button_5.setBorder(null);
        button_5.setBounds(0, 120, 31, 30);
        buttonPanel.add(button_5);

        JButton button_6 = new JButton("");
        button_6.setRolloverIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_kills_h.png"));
        button_6.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_kills.png"));
        button_6.setFocusPainted(false);
        button_6.setContentAreaFilled(false);
        button_6.setBorderPainted(false);
        button_6.setBorder(null);
        button_6.setBounds(0, 150, 31, 30);
        buttonPanel.add(button_6);

        JButton button_7 = new JButton("");
        button_7.setRolloverIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_farming_h.png"));
        button_7.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_farming.png"));
        button_7.setFocusPainted(false);
        button_7.setContentAreaFilled(false);
        button_7.setBorderPainted(false);
        button_7.setBorder(null);
        button_7.setBounds(0, 180, 31, 30);
        buttonPanel.add(button_7);

        JButton button_8 = new JButton("");
        button_8.setRolloverIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_calc_h.png"));
        button_8.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_calc.png"));
        button_8.setFocusPainted(false);
        button_8.setContentAreaFilled(false);
        button_8.setBorderPainted(false);
        button_8.setBorder(null);
        button_8.setBounds(0, 210, 31, 30);
        buttonPanel.add(button_8);

        //Button Listner for the calculator button.
        button_8.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                CalcSubClass calc = new CalcSubClass();
                calc.pack();
                System.out.println("Calculator JFrame extended. its ugly ik :(");
            }
        });

        JButton button_9 = new JButton("");
        button_9.setRolloverIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_timer_h.png"));
        button_9.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_timer.png"));
        button_9.setFocusPainted(false);
        button_9.setContentAreaFilled(false);
        button_9.setBorderPainted(false);
        button_9.setBorder(null);
        button_9.setBounds(0, 240, 31, 30);
        buttonPanel.add(button_9);

        JButton button_10 = new JButton("");
        button_10.setRolloverIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_notes_h.png"));
        button_10.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_notes.png"));
        button_10.setFocusPainted(false);
        button_10.setContentAreaFilled(false);
        button_10.setBorderPainted(false);
        button_10.setBorder(null);
        button_10.setBounds(0, 270, 31, 30);
        buttonPanel.add(button_10);

        JButton button_11 = new JButton("");
        button_11.setRolloverIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_twitch_h.png"));
        button_11.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_twitch.png"));
        button_11.setFocusPainted(false);
        button_11.setContentAreaFilled(false);
        button_11.setBorderPainted(false);
        button_11.setBorder(null);
        button_11.setBounds(0, 300, 31, 30);
        buttonPanel.add(button_11);

        hiscorePanel.setBorder(new LineBorder(new Color(44, 44, 44)));
        hiscorePanel.setOpaque(false);
        hiscorePanel.setBounds(768, 31, 233, 499);
        menuPanel.add(hiscorePanel);
        hiscorePanel.setLayout(null);

        stat_entryName = new JTextField();
        stat_entryName.addActionListener(arg0 -> {
            stat_name.setText("Please wait...");
            for (int i = 1; i < 24; i++)
                stat[i].setText("-");
            stat_total.setText("-");
            stat_combat.setText("-");
            stat_name.setVisible(true);
            progressBar.setVisible(false);
            stat_rank.setVisible(false);
            stat_name.setVisible(false);
            stat_exp.setVisible(false);
            stat_xpToLevel.setVisible(false);
            OSBHighscoresParser parser = new OSBHighscoresParser();
            parser.deserialize(stat_entryName.getText());
//				Client.instance.sendPacket(new RequestHighscores(stat_entryName.getText()));
        });
        stat_entryName.setCaretColor(Color.LIGHT_GRAY);
        stat_entryName.setSelectedTextColor(Color.LIGHT_GRAY);
        stat_entryName.setSelectionColor(new Color(234, 109, 34));
        stat_entryName.setForeground(Color.LIGHT_GRAY);
        stat_entryName.setFont(new Font("Arial", Font.BOLD, 12));
        stat_entryName.setOpaque(false);
        stat_entryName.setBorder(null);
        stat_entryName.setBounds(11, 18, 183, 20);
        hiscorePanel.add(stat_entryName);
        stat_entryName.setColumns(10);


        stat_combat.setHorizontalAlignment(SwingConstants.CENTER);
        stat_combat.setForeground(Color.LIGHT_GRAY);
        stat_combat.setFont(new Font("Arial", Font.BOLD, 11));
        stat_combat.setBounds(137, 301, 64, 20);
        hiscorePanel.add(stat_combat);

//        JLabel label_24 = new JLabel("-");
        player_name.setHorizontalAlignment(SwingConstants.CENTER);
        player_name.setForeground(Color.LIGHT_GRAY);
        player_name.setFont(new Font("Arial", Font.BOLD, 11));
        player_name.setBounds(40, 339, 64, 20);
        hiscorePanel.add(player_name);

        stat_name.setHorizontalAlignment(SwingConstants.LEFT);
        stat_name.setForeground(Color.LIGHT_GRAY);
        stat_name.setFont(new Font("Arial", Font.BOLD, 11));
        stat_name.setBounds(10, 373, 213, 20);
        stat_name.setVisible(false);
        hiscorePanel.add(stat_name);

        stat_rank.setHorizontalAlignment(SwingConstants.LEFT);
        stat_rank.setForeground(Color.LIGHT_GRAY);
        stat_rank.setFont(new Font("Arial", Font.BOLD, 11));
        stat_rank.setBounds(11, 386, 213, 20);
        stat_rank.setVisible(false);
        hiscorePanel.add(stat_rank);

        stat_exp.setHorizontalAlignment(SwingConstants.LEFT);
        stat_exp.setForeground(Color.LIGHT_GRAY);
        stat_exp.setFont(new Font("Arial", Font.BOLD, 11));
        stat_exp.setBounds(11, 399, 213, 20);
        stat_exp.setVisible(false);
        hiscorePanel.add(stat_exp);

        stat_xpToLevel.setHorizontalAlignment(SwingConstants.LEFT);
        stat_xpToLevel.setForeground(Color.LIGHT_GRAY);
        stat_xpToLevel.setFont(new Font("Arial", Font.BOLD, 11));
        stat_xpToLevel.setBounds(11, 412, 213, 20);
        stat_xpToLevel.setVisible(false);
        hiscorePanel.add(stat_xpToLevel);


        progressBar.setFont(new Font("Arial", Font.BOLD, 12));
        progressBar.setValue(82);

        progressBar.setForeground(new Color(234, 109, 34));
        progressBar.setBorder(null);
        progressBar.setBorderPainted(false);
        progressBar.setBackground(new Color(34, 34, 34, 0));
        progressBar.setOpaque(true);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        progressBar.setBounds(10, 435, 208, 19);
        hiscorePanel.add(progressBar);

        stat[1].setBounds(32, 49, 64, 20);


        stat[3].setBounds(32, 80, 64, 20);


        stat[2].setBounds(32, 111, 64, 20);

        stat[5].setBounds(32, 139, 64, 20);

        stat[6].setBounds(32, 170, 64, 20);

        stat[7].setBounds(32, 201, 64, 20);

        stat[21].setBounds(32, 229, 64, 20);

        stat[23].setBounds(32, 260, 64, 20);

        stat[4].setBounds(102, 49, 64, 20);

        stat[17].setBounds(102, 80, 64, 20);

        stat[16].setBounds(102, 111, 64, 20);

        stat[18].setBounds(102, 142, 64, 20);

        stat[13].setBounds(102, 170, 64, 20);

        stat[10].setBounds(102, 201, 64, 20);

        stat[19].setBounds(102, 229, 64, 20);

        stat[22].setBounds(102, 260, 64, 20);

        stat[15].setBounds(166, 49, 64, 20);

        stat[14].setBounds(166, 80, 64, 20);

        stat[11].setBounds(166, 111, 64, 20);

        stat[8].setBounds(166, 139, 64, 20);

        stat[12].setBounds(166, 170, 64, 20);

        stat[20].setBounds(166, 229, 64, 20);

        stat[9].setBounds(166, 201, 64, 20);
        stat_total.setFont(new Font("Arial", Font.BOLD, 11));
        stat_total.setForeground(Color.LIGHT_GRAY);
        stat_total.setHorizontalAlignment(SwingConstants.CENTER);

        stat_total.setBounds(58, 301, 64, 20);
        stat_total.addMouseListener(hover(590));
        hiscorePanel.add(stat_total);

        for (int i = 1; i < 24; i++)
            hiscorePanel.add(stat[i]);

        JLabel hiscores_background = new JLabel("");
        hiscores_background.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/statsbar.png"));
        hiscores_background.setBounds(0, 11, 223, 451);
        hiscorePanel.add(hiscores_background);

        hiscorePanel.setVisible(expanded);
        buttonPanel.setVisible(expanded);


        titleBar_title.setForeground(Color.LIGHT_GRAY);
        titleBar_title.setFont(new Font("Arial", Font.BOLD, 11));
        titleBar_title.setBounds(37, 7, 54, 14);
        menuPanel.add(titleBar_title);

        compCoords = null;

//        addListeners();

        ComponentResizer cr = new ComponentResizer();
        cr.registerComponent(frame);

        /*
         * Add our menu panel to our frame
         */
        NavListener navListener = new NavListener(menuPanel);
        menuPanel.addMouseMotionListener(navListener);
        menuPanel.addMouseListener(navListener);
        menuPanel.setPreferredSize(new Dimension(765, 27));
        menuPanel.setMinimumSize(new Dimension(765, 27));
        frame.setMinimumSize(new Dimension(765, 27));
        frame.setResizable(true);
        frame.getContentPane().setBackground(new Color(34, 34, 34));
        frame.getContentPane().add(menuPanel, BorderLayout.NORTH);
    }

    private Point compCoords;
    private boolean drag = false;
    private Point dragLocation = new Point();

    private void addListeners() {
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drag = true;
                dragLocation = e.getPoint();
                requestFocus();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                drag = false;
                requestFocus();
            }
        });

        gamePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (drag) {

//                    me.translatePoint(me.getComponent().getLocation().x, me.getComponent().getLocation().y);
//                    p.setLocation(me.getX(), me.getY());
                    if (dragLocation.getX() > getWidth() - 10 && !(dragLocation.getY() <= 532 && dragLocation.getX() <= 1027)) {
                        setSize((int) (getWidth() + (e.getPoint().getX() - dragLocation.getX())),
                                (int) (getHeight() + (e.getPoint().getY() - dragLocation.getY())));
                        resize(ScreenMode.RESIZABLE, (int) (getWidth() + (e.getPoint().getX() - dragLocation.getX())), (int) (getHeight() + (e.getPoint().getY() - dragLocation.getY())));
                        dragLocation = e.getPoint();
                        requestFocus();
                    }
                }
            }
        });
        /*menuPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drag = true;
                dragLocation = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                drag = false;
            }
        });

        menuPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (drag) {
                    if (dragLocation.getX() > getWidth() - 10 && dragLocation.getY() > getHeight() - 10) {
                        System.err.println("in");
                        setSize((int) (getWidth() + (e.getPoint().getX() - dragLocation.getX())),
                                (int) (getHeight() + (e.getPoint().getY() - dragLocation.getY())));
                        setSize((int) (getWidth() + (e.getPoint().getX() - dragLocation.getX())),
                                (int) (getHeight() + (e.getPoint().getY() - dragLocation.getY())));
                        dragLocation = e.getPoint();
                    }
                }
            }
        });*/
    }

    /**
     * Generates a random number
     */
    public static int random(int range) {
        return (int) (java.lang.Math.random() * (range + 1));
    }

    /**
     * Draws an RS2 tooltip
     */
    private void drawTooltip(Graphics g, String text, int x, int y) {
        g.setColor(new Color(0xFFFFA0));
        g.fillRect(x, y, g.getFontMetrics().stringWidth(text) + 5, 15);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, g.getFontMetrics().stringWidth(text) + 5, 15);
        g.setColor(Color.BLACK);
        g.drawString(text, x + 2, y + 12);
    }

    /**
     * Draws a string centered to the 'x' position
     */
    private void drawCenteredString(Graphics g, String text, int x, int y, int color) {
        int width = g.getFontMetrics().stringWidth(text);
        g.setColor(Color.BLACK);
        g.drawString(text, x - width / 2 + 2, y + 2);
        g.setColor(new Color(color));
        g.drawString(text, x - width / 2, y);
    }


    /**
     * Resizes the frame when switching between modes
     */
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

    private boolean expanded = false;

    public static MouseListener hover(int skillId) {

        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                OSBHighscoresParser parser = new OSBHighscoresParser();

                progressBar.setVisible(false);
                stat_xpToLevel.setVisible(false);

                if (skillId == 590) {
                    stat_name.setText("Stat: Overall");
                    stat_rank.setText("Rank: -");
                    stat_exp.setText("Experience: " + NumberFormat.getInstance(Locale.ENGLISH).format(totalExperience));
                    stat_rank.setVisible(true);
                    stat_name.setVisible(true);
                    stat_exp.setVisible(true);
                    return;
                }
                final int currentLevel = parser.getLevelForExperience(experience[skillId]);


//                System.out.println("I have " + Client.experience[skillId] + " exp.");
//                System.out.println("Level " + currentLevel + " to " + (currentLevel + 1)  + " " + (parser.getXPForLevel(currentLevel + 1) - parser.getXPForLevel(currentLevel)) + " exp.");
//                System.out.println("Total exp required for level " + (currentLevel + 1) + " is " + OSBHighscoresParser.EXP_FOR_LEVEL[currentLevel]);
//                System.out.println("Total exp required for level " + (currentLevel) + " is " + OSBHighscoresParser.EXP_FOR_LEVEL[currentLevel - 1]);
//                System.out.println(Arrays.toString(Client.experience));

                int nextLvl = parser.getLevelForExperience(experience[skillId]) + 1;
                double xpForNxt = (parser.getXPForLevel(nextLvl) - experience[skillId]);
                stat_name.setText("Stat: " + SKILL_NAME[skillId - 1]);
                stat_rank.setText("Prestige: " + (int) prestige[skillId]);
                stat_exp.setText("Experience: " + NumberFormat.getInstance(Locale.ENGLISH).format(experience[skillId]));
                if (currentLevel <= 98) {
                    stat_xpToLevel.setText("Exp to level(" + (nextLvl) + "): " + NumberFormat.getInstance(Locale.ENGLISH).format(xpForNxt));

                    progressBar.setMaximum(100);
                    progressBar.setValue((int) ((experience[skillId] - OSBHighscoresParser.EXP_FOR_LEVEL[currentLevel - 1]) / (OSBHighscoresParser.EXP_FOR_LEVEL[currentLevel] - OSBHighscoresParser.EXP_FOR_LEVEL[currentLevel - 1]) * 100));
                    progressBar.setVisible(true);
                    stat_xpToLevel.setVisible(true);
                } else {
                    progressBar.setVisible(false);
                    stat_xpToLevel.setVisible(false);
                }
                stat_rank.setVisible(true);
                stat_name.setVisible(true);
                stat_exp.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent evt) {

            }
        };
        return mouseListener;
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

