package com.github.dig.server;

import lombok.extern.java.Log;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;

@Log
public class InterfaceApp {

    private SystemTray tray;

    public InterfaceApp() {
        if (SystemTray.isSupported()) {
            try {
                tray = SystemTray.getSystemTray();
                createTrayIcon();
            } catch (IOException | AWTException e) {
                log.log(Level.SEVERE, "Unable to create tray icon", e);
            }
        }
    }

    private void createTrayIcon() throws IOException, AWTException {
        PopupMenu popup = new PopupMenu();
        MenuItem exitItem = new MenuItem("Close");
        exitItem.addActionListener(e -> close());
        popup.add(exitItem);

        Image image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("icon.png"));
        TrayIcon trayIcon = new TrayIcon(image, "Interface Server", popup);
        trayIcon.setImageAutoSize(true);

        tray.add(trayIcon);
    }

    private void close() {
        System.exit(0);
    }

    public static void main(String args[]) {
        new InterfaceApp();
    }
}
