package com.github.dig.server;

import lombok.NonNull;
import lombok.extern.java.Log;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Level;

@Log
public class InterfaceApp {

    private Properties configuration;
    private TrayIcon trayIcon;

    private InterfaceSocket socket;

    public InterfaceApp() {
        File folder = new File(System.getenv("APPDATA") + File.separator + "InterfaceClient");
        if (!loadConfiguration(folder)) {
            try {
                Desktop.getDesktop().open(folder);
            } catch (IOException e) {}

            JOptionPane.showMessageDialog(null,
                    "config.properties not configured properly.",
                    "Configuration",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (SystemTray.isSupported()) {
            try {
                createTrayIcon();
            } catch (IOException | AWTException e) {
                log.log(Level.SEVERE, "Unable to create tray icon", e);
            }
        }

        try {
            String hostName = configuration.getProperty("hostUri", Defaults.HOST);
            socket = new InterfaceSocket(new URI(hostName));
            if (socket.isOpen()) {
                trayIcon.setImage(getResourceImage("on.png"));
            }
        } catch (URISyntaxException | IOException e) {
            log.log(Level.SEVERE, "Unable to create new socket connection", e);
        }
    }

    private void createTrayIcon() throws IOException, AWTException {
        PopupMenu popup = new PopupMenu();
        MenuItem exitItem = new MenuItem("Close");
        exitItem.addActionListener(e -> close());
        popup.add(exitItem);

        trayIcon = new TrayIcon(getResourceImage("off.png"), "Interface Server", popup);
        trayIcon.setImageAutoSize(true);

        SystemTray.getSystemTray().add(trayIcon);
    }

    private Image getResourceImage(@NonNull String imagePath) throws IOException {
        return ImageIO.read(getClass().getClassLoader().getResourceAsStream(imagePath));
    }

    private boolean loadConfiguration(@NonNull File dataFolder) {
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File config = new File(dataFolder, "config.properties");
        if (config.exists()) {
            try {
                configuration = PropertiesLoader.load(config);
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        return false;
    }

    private void close() {
        if (socket != null && socket.isOpen()) {
            socket.close();
        }
        System.exit(0);
    }

    public static void main(String args[]) {
        new InterfaceApp();
    }
}
