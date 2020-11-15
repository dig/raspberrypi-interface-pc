package com.github.dig.server;

import lombok.Getter;
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
@Getter
public class InterfaceApp {

    @Getter
    private static InterfaceApp instance;

    private Properties configuration;

    private TrayIcon trayIcon;
    private boolean lastIconState;

    private InterfaceSocket socket;
    private Thread metricCollection;

    public InterfaceApp() {
        File folder = new File(System.getenv("APPDATA") + File.separator + "InterfaceClient");
        if (!folder.exists()) {
            folder.mkdir();
        }

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
            String hostName = configuration.getProperty("host-uri", Defaults.HOST);
            socket = new InterfaceSocket(new URI(hostName), configuration.getProperty("auth-key", Defaults.AUTH_KEY));
        } catch (URISyntaxException e) {
            log.log(Level.SEVERE, "Unable to create new socket connection", e);
        }

        metricCollection = new MetricCollection(socket, configuration);
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

    public void execute() {
        metricCollection.start();

        int reconnect = Integer.valueOf(configuration.getProperty("socket-reconnect", String.valueOf(Defaults.SOCKET_RECONNECT)));
        try {
            socket.connectBlocking();
            updateTrayIcon();

            while (true) {
                if (socket.isClosed()) {
                    socket.reconnectBlocking();
                    updateTrayIcon();
                }
                Thread.sleep(reconnect * 1000);
            }
        } catch (InterruptedException e) {
            log.log(Level.SEVERE, "Thread interrupted, shutting down...", e);
            close();
        }
    }

    private Image getResourceImage(@NonNull String imagePath) throws IOException {
        return ImageIO.read(getClass().getClassLoader().getResourceAsStream(imagePath));
    }

    private void updateTrayIcon() {
        if (lastIconState != socket.isOpen()) {
            lastIconState = socket.isOpen();

            try {
                trayIcon.setImage(getResourceImage((socket.isOpen() ? "on" : "off") + ".png"));
            } catch (IOException e) {
                log.log(Level.SEVERE, "Unable to update tray icon", e);
            }
        }
    }

    private boolean loadConfiguration(@NonNull File dataFolder) {
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
        if (metricCollection != null) {
            metricCollection.interrupt();
        }
        if (socket != null && socket.isOpen()) {
            socket.close();
        }
        System.exit(0);
    }

    public static void main(String args[]) {
        instance = new InterfaceApp();
        instance.execute();
    }
}
