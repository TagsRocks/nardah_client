package com.nardah.osbuddy;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ColorUIResource;

import com.nardah.osbuddy.parser.OSBHighscoresParser;
import com.nardah.Client;
import com.nardah.Configuration;
import com.nardah.Utility;

/**
 * @author Ethan Millard <Torv>
 * @author Charlie <chaflie, charliesalter@hotmail.com>
 */
public class OSBLoader extends JFrame {//someone updated the data i can see it becuz of the error

    public static final String[] SKILL_NAME = {"Attack", "Defence", "Strength", "Hitpoints", "Ranged", "Prayer",
            "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining",
            "Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecrafting", "Hunter", "Construction",
            "Summoning"};

    private static final long serialVersionUID = 4111341527646793793L;
    private JPanel contentPane ;
    private Point compCoords;
    static OSBLoader frame = null;

    Client myApplet;

    public static JPanel gamePanel = new JPanel();
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

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                frame = new OSBLoader();
                frame.setVisible(true);
                Configuration.USING_OSBUDDY = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        });
    }

    /** Resizes the frame when switching between modes */
    public static void resize(Client.ScreenMode frameMode, int width, int height) {
        try {
            if (frameMode == Client.ScreenMode.FIXED) {
                width = 765;
                height = 503;
            } else if (frameMode == Client.ScreenMode.RESIZABLE) {
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
            int heightModifier = buttonPanel.getHeight();
            if (frameMode != Client.ScreenMode.FULLSCREEN) {
                frame.setBounds(0, 0, width + widthModifier, height + heightModifier);
            }

            Client.instance.myWidth = width;
            Client.instance.myHeight = height;

            frame.setResizable(frameMode == Client.ScreenMode.RESIZABLE);
            if (frameMode == Client.ScreenMode.FIXED) {
                frame.pack();
            }
            frame.setLocationRelativeTo(null);
            frame.requestFocus();
            frame.toFront();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    stat_exp.setText("Experience: " + NumberFormat.getInstance(Locale.ENGLISH).format(Client.totalExperience));
                    stat_rank.setVisible(true);
                    stat_name.setVisible(true);
                    stat_exp.setVisible(true);
                    return;
                }
                final int currentLevel = parser.getLevelForExperience(Client.experience[skillId]);


//                System.out.println("I have " + Client.experience[skillId] + " exp.");
//                System.out.println("Level " + currentLevel + " to " + (currentLevel + 1)  + " " + (parser.getXPForLevel(currentLevel + 1) - parser.getXPForLevel(currentLevel)) + " exp.");
//                System.out.println("Total exp required for level " + (currentLevel + 1) + " is " + OSBHighscoresParser.EXP_FOR_LEVEL[currentLevel]);
//                System.out.println("Total exp required for level " + (currentLevel) + " is " + OSBHighscoresParser.EXP_FOR_LEVEL[currentLevel - 1]);
//                System.out.println(Arrays.toString(Client.experience));

                int nextLvl = parser.getLevelForExperience(Client.experience[skillId]) + 1;
                double xpForNxt = (parser.getXPForLevel(nextLvl) - Client.experience[skillId]);
                stat_name.setText("Stat: " + SKILL_NAME[skillId - 1]);
                stat_rank.setText("Prestige: " + (int) Client.prestige[skillId]);
                stat_exp.setText("Experience: " + NumberFormat.getInstance(Locale.ENGLISH).format(Client.experience[skillId]));
                if (currentLevel <= 98) {
                    stat_xpToLevel.setText("Exp to level(" + (nextLvl) + "): " + NumberFormat.getInstance(Locale.ENGLISH).format(xpForNxt));

                    progressBar.setMaximum(100);
                    progressBar.setValue((int) ((Client.experience[skillId] - OSBHighscoresParser.EXP_FOR_LEVEL[currentLevel - 1]) / (OSBHighscoresParser.EXP_FOR_LEVEL[currentLevel] - OSBHighscoresParser.EXP_FOR_LEVEL[currentLevel - 1]) * 100));
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

    /**
     * Create the frame.
     */
    public OSBLoader() {
        setUndecorated(true);
        Image icon = Toolkit.getDefaultToolkit().getImage(Utility.findcachedir() + "/osb/title_icon.png");

        setIconImage(icon);
        setResizable(true);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                System.out.println(me);
            }
        });
        UIDefaults uid = UIManager.getDefaults();
        // create a resource
        ColorUIResource thumbColor = new ColorUIResource(new Color(34, 34, 34));
        // replace the old resource.

        setLayout(new BorderLayout());

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1037, 532);
        if (!expanded)
            setSize(773, 532);
        else
            setSize(1037, 532);
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(34, 34, 34));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);


        gamePanel.setOpaque(false);
        gamePanel.setBounds(4, 26, 765, 503);
        contentPane.add(gamePanel, BorderLayout.CENTER);

        JPanel titleBar = new JPanel();
        titleBar.setOpaque(false);
        titleBar.setBounds(0, 0, 1037, 27);
        contentPane.add(titleBar, BorderLayout.NORTH);
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

        titleBar.add(titleBar_settingsButton);

        JButton titleBar_cameraButton = new JButton("");
        titleBar_cameraButton.setRolloverIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_camera_h.png"));
        titleBar_cameraButton.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_camera.png"));
        titleBar_cameraButton.setFocusPainted(false);
        titleBar_cameraButton.setContentAreaFilled(false);
        titleBar_cameraButton.setBorderPainted(false);
        titleBar_cameraButton.setBorder(null);
        titleBar_cameraButton.setBounds(813, 6, 25, 24);
        if (!expanded)
            titleBar_cameraButton.setBounds(titleBar_cameraButton.getX() - 263, titleBar_cameraButton.getY(), titleBar_cameraButton.getWidth(), titleBar_cameraButton.getHeight());

//        titleBar.add(titleBar_cameraButton);

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

        titleBar.add(titleBar_discordButton);
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

        titleBar.add(titleBar_linksButton);
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
        titleBar.add(titleBar_closeButton);
        titleBar_closeButton.addActionListener(a ->{ System.exit(0);});


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
       // titleBar.add(titleBar_maxButton);
      /*  titleBar_maxButton.addActionListener(a -> {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setSize(screenSize.width, screenSize.height);
        });*/

        JButton titleBar_minButton = new JButton("");
        titleBar_minButton.setRolloverIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_minimize_h.png"));
        titleBar_minButton.setIcon(new ImageIcon(Utility.findcachedir() + "/osb/osb_minimize.png"));
        titleBar_minButton.setFocusPainted(false);
        titleBar_minButton.setContentAreaFilled(false);
        titleBar_minButton.setBorderPainted(false);
        titleBar_minButton.setBorder(null);
        titleBar_minButton.setBounds(/*943, 1, 33, 26*/967, 4, 33, 21);
        if (!expanded)
            titleBar_minButton.setBounds(titleBar_minButton.getX() - 263, titleBar_minButton.getY(), titleBar_minButton.getWidth(), titleBar_minButton.getHeight());
        titleBar_minButton.addActionListener(a -> frame.setState(JFrame.ICONIFIED));

        titleBar.add(titleBar_minButton);

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
            titleBar_cameraButton.setLocation(titleBar_cameraButton.getX() + denom, titleBar_cameraButton.getY());
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
        contentPane.add(buttonPanel);
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
        contentPane.add(hiscorePanel);
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

        ActionListener processTimer = new ActionListener() {
            private int ticks;
            private boolean gameLoaded;

            public void actionPerformed(ActionEvent evt) {
                ticks++;

                if (ticks >= 3 && !gameLoaded) {

                    myApplet = new Client();
                    myApplet.setBounds(0, 0, 765, 503);
                    gamePanel.add(myApplet);
                    myApplet.start();
                    myApplet.init();
                    myApplet.setLayout(null);
                    gamePanel.setVisible(true);
//					setTitle("Inferno - ver. 1.0." + Configuration.CLIENT_VERSION);
                    gameLoaded = true;
                }
            }
        };

        Timer process = new Timer(1, processTimer);

        process.setRepeats(true);
        process.start();


        compCoords = null;
        titleBar.addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent e) {
                compCoords = null;
            }

            public void mousePressed(MouseEvent e) {
                compCoords = e.getPoint();
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
            }
        });
        titleBar.addMouseMotionListener(new MouseMotionListener() {
            public void mouseMoved(MouseEvent e) {
            }

            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                setLocation(currCoords.x - compCoords.x, currCoords.y - compCoords.y);
            }
        });

    }

    public void buildHighscoreSlots(JLabel[] label) {
        for (int i = 0; i < 8; i++) {
            label = new JLabel[i];
            label[i].setHorizontalAlignment(SwingConstants.CENTER);
            label[i].setForeground(Color.LIGHT_GRAY);
            label[i].setFont(new Font("Arial", Font.BOLD, 11));
            int y = 49;
            label[i].setBounds(32, y, 64, 20);
            y += 31;
            hiscorePanel.add(label[i]);
        }
    }
}
