package com.nardah.login.impl;


import com.nardah.*;
import com.nardah.login.LoginComponent;
import com.nardah.login.ScreenType;

/**
 * Handles the main screen of login.
 *
 * @author Daniel
 */
public class MainScreen extends LoginComponent {

    private static final int EMAIL_CHARACTER_LIMIT = 28;


    @Override
    public void render(Client client) {
        int centerX = getX();
        int centerY = getY();
        refresh(client);
        load(client, 10);

        /* Message Check */
        if (client.loginMessage1.length() > 0 || client.loginMessage2.length() > 0) {
            client.loginRenderer.setScreen(new MessageScreen());
        }

        /* Background */
        Client.spriteCache.get(57).drawTransparentSprite((Client.frameWidth / 2) - (Client.spriteCache.get(57).width / 2), (Client.frameHeight / 2) - (Client.spriteCache.get(57).height / 2), client.loginTick);

        /* Login Button */
        if (client.mouseInRegion(318, 285, 452, 328)) {
            Client.spriteCache.get(59).drawSprite(318, 285, 0);
            addTooltip("Log into BattleRune");
        } else {
            Client.spriteCache.get(58).drawTransparentSprite(318, 285, client.loginTick);
        }

        client.newFancyFont.drawCenteredString("Remember me", 315, 268);
        /* Remember Me Button */
        Client.spriteCache.get(Settings.REMEMBER_ME ? 881 : 880).drawTransparentSprite(244, 251, client.loginTick);
        if (client.mouseInRegion(244, 254, 261, 270)) {
            addTooltip("Toggle remember account details");
        }

        /* Information */
        if (client.mouseInRegion(275, 168, 524, 197)) {
            Client.spriteCache.get(882).drawSprite(274, 169, 0);
            addTooltip("Enter your username");
        }
        if (client.mouseInRegion(275, 215, 524, 239)) {
            Client.spriteCache.get(882).drawSprite(274, 215, 0);
            addTooltip("Enter your password");
        }

        client.regularText.drawText(true, centerX - 101, 0xFFFFFF, Utility.formatName(client.myUsername) + ((client.loginScreenCursorPos == 0) & (Client.tick % 40 < 20) ? "|" : ""), centerY - 60);
        client.regularText.drawText(true, centerX - 101, 0xFFFFFF, StringUtils.toAsterisks(client.myPassword) + ((client.loginScreenCursorPos == 1) & (Client.tick % 40 < 20) ? "|" : ""), centerY - 13);

        /* World Button */
        if (client.mouseInRegion(671, 441, 746, 476)) {
            Client.spriteCache.get(61).drawSprite(670, 440, 0);
//            addTooltip("Toggle world");
        } else {
            Client.spriteCache.get(60).drawSprite(670, 440, 0);
        }

//        client.regularText.drawCenteredText(0xFFFFFF, 715, "W" + (Configuration.CONNECTION.ordinal() + 1) + ":", 451, false);
        client.regularText.drawCenteredText(0x4FB533, 722, Configuration.CONNECTION.name, 466, false);

        /* Social Buttons */
//        if (client.mouseInRegion(centerX + 241, centerY - 194, centerX + 257, centerY - 215)) {
//            Client.spriteCache.get(191).drawTransparentSprite(680, 35, 50);
//            addTooltip("View youtube page");
//        } else {
//            Client.spriteCache.get(190).drawTransparentSprite(680, 35, 255);
//        }
//        if (client.mouseInRegion(centerX + 177, centerY + 136, centerX + 201, centerY + 157)) {
//            Client.spriteCache.get(189).drawTransparentSprite(650, 35, 50);
//            addTooltip("View twitter page");
//        } else {
//            Client.spriteCache.get(188).drawTransparentSprite(650, 35, 255);
//        }
//        if (client.mouseInRegion(centerX + 260, centerY - 215, centerX + 242, centerY - 193)) {
//            Client.spriteCache.get(187).drawTransparentSprite(620, 35, 50);
//            addTooltip("Join discord channel");
//        } else {
//            Client.spriteCache.get(186).drawTransparentSprite(620, 35, 255);
//        }

//        /* Forgot Password */
//        if (client.mouseInRegion(centerX - 33, centerY + 110, centerX + 82, centerY + 118)) {
//            client.smallFont.drawCenteredText(0xE56161, 407, "Forgot your password?", 375, false);
//            addTooltip("Click to recover your password");
//        } else {
//            client.smallFont.drawCenteredText(0x7B7B7B, 407, "Forgot your password?", 375, false);
//        }

        /* Announcement */
        announcement(client);

        /* Account */
        drawAccount(client);

        /* Bubble */
        drawSetting(client);

        /* Other */
        client.smallFont.drawCenteredText(0xFFFFFF, centerX + 330, "Client Build: " + Configuration.GAME_VERSION, centerY + 245, true);
                if (Configuration.DEBUG_MODE) {
        client.smallFont.drawCenteredText(0xFFFFFF, centerX + 300, "MouseX: " + (client.mouseX) + " Mouse Y: " + (client.mouseY), centerY - 225, true);
                }

        /* Drawing */
        Client.loginScreenIP.drawGraphics(client.graphics, 0, 0);
        Raster.reset();
    }

    @Override
    public void click(Client client) {
        int centerX = getX();
        int centerY = getY();

        /* Username */
        if (client.lastMetaModifier == 1 && client.mouseInRegion(275, 168, 524, 197))
            client.loginScreenCursorPos = 0;

        /* Password */
        if (client.lastMetaModifier == 1 && client.mouseInRegion(275, 215, 524, 239))
            client.loginScreenCursorPos = 1;

//        /* Forgot Password */
//        if (client.lastMetaModifier == 1 && client.mouseInRegion(centerX - 33, centerY + 110, centerX + 82, centerY + 118))
//            Utility.launchURL("https://www.osroyale.com/community/index.php?/lostpassword/");

        /* Remember Me */
        if (client.lastMetaModifier == 1 && client.mouseInRegion(244, 254, 261, 270)) {
            Settings.REMEMBER_ME = !Settings.REMEMBER_ME;
        }

//        /* Social Buttons */
//        if (client.lastMetaModifier == 1 && client.mouseInRegion(centerX + 147, centerY + 136, centerX + 171, centerY + 157)) {
//            Utility.launchURL("https://discord.gg/fGkt9Sj");
//        }
//        if (client.lastMetaModifier == 1 && client.mouseInRegion(centerX + 177, centerY + 136, centerX + 201, centerY + 157)) {
//            Utility.launchURL("https://twitter.com/OSRoyalePS");
//        }
//        if (client.lastMetaModifier == 1 && client.mouseInRegion(centerX + 207, centerY + 136, centerX + 231, centerY + 157)) {
//            Utility.launchURL("https://www.youtube.com/channel/UC8wR4kTWHgeD1F_1XTtA-wA");
//        }

        /* Account */
        int xPos = 270;
        int yPos = centerY - 70;
        if (AccountManager.ACCOUNTS != null) {
            for (int index = 0; index < AccountManager.ACCOUNTS.size(); index++, xPos += 150) {
                AccountData accountData = AccountManager.ACCOUNTS.get(index);
                if (client.lastMetaModifier == 1 && client.mouseInRegion(xPos, 374, xPos + 55, 431)) {
                    client.lastAccount = accountData;
                    client.myUsername = Utility.formatName(accountData.username.toLowerCase());
                    client.myPassword = accountData.password;
                    client.attemptLogin(accountData.username, accountData.password, false);
                    if (Client.loggedIn) {
                        return;
                    }
                }
                if (client.lastMetaModifier == 1 && client.mouseInRegion(xPos - 25, 435, xPos + 136, 458)) {
                    if (client.lastAccount == null) {
                        client.loginMessage1 = "There was an issue loading your account.";
                        return;
                    }
                    client.lastAccount = accountData;
                    client.loginRenderer.setScreen(new AccountScreen());
                    return;
                }
                if (client.lastMetaModifier == 1 && client.mouseInRegion(xPos + 67, 413, xPos + 78, 428)) {
                    AccountManager.removeAccount(accountData);
                }
            }
        }

        /* Bubble */
        settingButton(client);

        /* World Button */
        if (client.lastMetaModifier == 1 && client.mouseInRegion(671, 441, 746, 476)) {
            switch (Configuration.CONNECTION) {
                case ECONOMY:
                    Configuration.CONNECTION = Connection.MANAGEMENT;
                    break;
                case MANAGEMENT:
                    Configuration.CONNECTION = Connection.DEVELOPMENT;
                    break;
                case DEVELOPMENT:
                    Configuration.CONNECTION = Connection.ECONOMY;
                    break;
            }
            Client.server = Configuration.CONNECTION.address;
        }

        /* Login Buttons */
        if (client.lastMetaModifier == 1 && client.mouseInRegion(318, 285, 452, 328))
            if (!Client.loggedIn)
                client.attemptLogin(client.myUsername, client.myPassword, false);

        /* Writing */
        handleWriting(client);
    }

    /**
     * Handles drawing the accounts on the login screen.
     */
    @SuppressWarnings("ConstantConditions")
    private void drawAccount(Client client) {
//        Raster.fillRectangle(155, 100, 313, 164, 0x2E2827, 150);

        int centerX = getX();
        int centerY = getY();
        int xPos = 270;
        int yPos = centerY - 70;
        int frameColor = 0x3d3427;
        for (int index = 0; index < 2; index++, xPos += 150) {
            AccountData accountData = AccountManager.get(index);

            if (accountData == null) {
                Client.spriteCache.get(73).drawARGBSprite(xPos, 374);
                Client.spriteCache.get(676).drawARGBSprite(xPos  - 30, 438);
            } else {
                if (client.mouseInRegion(xPos + 67, 413, xPos + 78, 428)) {
                    Client.spriteCache.get(675).drawARGBSprite(xPos + 65, 410);
                    addTooltip("Delete profile");
                } else {
                    Client.spriteCache.get(674).drawARGBSprite(xPos + 65, 410);
                }
                Client.spriteCache.get(accountData.avatar).drawARGBSprite(xPos, 374);
                Client.spriteCache.get(676).drawARGBSprite(xPos  - 30, 438);
                Raster.drawRectangle(xPos, 374, Client.spriteCache.get(accountData.avatar).width,Client.spriteCache.get(accountData.avatar).height, frameColor);
                int rank = (accountData.rank - 1);
                String name = accountData.username;
//                if (rank <= -1) {
//                    client.newSmallFont.drawCenteredString(Utility.formatName(name), xPos + 30, 455);
//                } else {
//                    client.newSmallFont.drawCenteredString((rank <= -1 ? Utility.formatName(name) : "<img=" + rank + ">"), centerX - 145, yPos + 13);
                    client.newSmallFont.drawCenteredString((rank <= -1 ? "" : "<img=" + rank + ">") + "<col=ffffff>" + Utility.formatName(name.toLowerCase()), xPos + 30, 455);
//                }
                if (client.mouseInRegion(xPos - 25, 435, xPos + 136, 458)) {
                    Raster.drawRectangle(xPos  - 30, 438, Client.spriteCache.get(676).width, Client.spriteCache.get(676).height, 0x1F1D19);
                    Raster.fillRectangle(xPos  - 30, 438, Client.spriteCache.get(676).width, Client.spriteCache.get(676).height, 0x1F1D19, 50);
                    addTooltip("Manage profile details");
                }
                if (client.mouseInRegion(xPos, 374, xPos + 55, 431)) {
                    Raster.drawRectangle(xPos, 374, Client.spriteCache.get(accountData.avatar).getWidth(), Client.spriteCache.get(accountData.avatar).getHeight(), 0x1F1D19);
                    Raster.fillRectangle(xPos, 374, Client.spriteCache.get(accountData.avatar).getWidth(), Client.spriteCache.get(accountData.avatar).getHeight(), 0x1F1D19, 50);
                    addTooltip("Login profile");
                }
            }
        }
    }

    /**
     * Handles writing in the client.
     */
    private void handleWriting(Client client) {
        do {
            int line = client.readChar(-796);
            if (line == -1)
                break;
            boolean flag = false;
            for (int index = 0; index < Client.validUserPassChars.length(); index++) {
                if (line != Client.validUserPassChars.charAt(index))
                    continue;
                flag = true;
                break;
            }

            // Main account username
            if (client.loginScreenCursorPos == 0) {
                if (line == 8 && client.myUsername.length() > 0)
                    client.myUsername = client.myUsername.substring(0, client.myUsername.length() - 1);
                if (line == 9 || line == 10 || line == 13) {
                    client.loginScreenCursorPos = 1;
                }
                if (flag) {
                    client.myUsername += (char) line;
                }

                if (client.myUsername.length() > EMAIL_CHARACTER_LIMIT) {
                    client.myUsername = client.myUsername.substring(0, EMAIL_CHARACTER_LIMIT);
                }

                // Main account password
            } else if (client.loginScreenCursorPos == 1) {
                if (line == 8 && client.myPassword.length() > 0)
                    client.myPassword = client.myPassword.substring(0, client.myPassword.length() - 1);
                if (line == 9 || line == 10 || line == 13) {
                    client.attemptLogin(client.myUsername, client.myPassword, false);
                }
                if (flag) {
                    client.myPassword += (char) line;
                }
                if (client.myPassword.length() > 20) {
                    client.myPassword = client.myPassword.substring(0, 20);
                }
            }
        } while (true);
        return;
    }

    @Override
    public ScreenType type() {
        return ScreenType.MAIN;
    }
}
