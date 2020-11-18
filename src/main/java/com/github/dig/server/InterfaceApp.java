package com.github.dig.server;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
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

    private final static String REGISTRY_KEY = "InterfaceClient";

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

        Menu piMenu = new Menu("Device");
        MenuItem updateItem = new MenuItem("Update");
        MenuItem restartItem = new MenuItem("Restart");
        MenuItem shutdownItem = new MenuItem("Shutdown");

        updateItem.addActionListener(e -> {
            if (socket.isOpen()) {
                socket.send("update", String.valueOf(true));
            }
        });

        restartItem.addActionListener(e -> {
            if (socket.isOpen()) {
                socket.send("restart", String.valueOf(true));
            }
        });

        shutdownItem.addActionListener(e -> {
            if (socket.isOpen()) {
                socket.send("shutdown", String.valueOf(true));
            }
        });

        piMenu.add(updateItem);
        piMenu.add(restartItem);
        piMenu.add(shutdownItem);
        popup.add(piMenu);

        CheckboxMenuItem startupItem = new CheckboxMenuItem("Run on startup");
        startupItem.setState(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER,
                "Software\\Microsoft\\Windows\\CurrentVersion\\Run", REGISTRY_KEY));
        startupItem.addItemListener(e -> runOnStartup(startupItem.getState()));
        popup.add(startupItem);

        MenuItem exitItem = new MenuItem("Close");
        exitItem.addActionListener(e -> close());
        popup.add(exitItem);

        trayIcon = new TrayIcon(getResourceImage("off.png"), "InterfaceClient v" + getBuildVersion(), popup);
        trayIcon.setImageAutoSize(true);

        SystemTray.getSystemTray().add(trayIcon);
    }

    public void execute() {
        int reconnect = Integer.valueOf(configuration.getProperty("socket-reconnect", String.valueOf(Defaults.SOCKET_RECONNECT)));
        try {
            socket.connectBlocking();
            updateTray();
            tryMetricCollection();

            while (true) {
                if (socket.isClosed()) {
                    socket.reconnectBlocking();
                    updateTray();
                    tryMetricCollection();
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

    private void updateTray() {
        if (lastIconState != socket.isOpen()) {
            lastIconState = socket.isOpen();

            try {
                trayIcon.setImage(getResourceImage((socket.isOpen() ? "on" : "off") + ".png"));
            } catch (IOException e) {
                log.log(Level.SEVERE, "Unable to update tray icon", e);
            }
        }
    }

    private void tryMetricCollection() {
        if (socket.isOpen() && !metricCollection.isAlive()) {
            metricCollection.start();
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

    private void runOnStartup(boolean state) {
        boolean containsRegistry = Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER,
                "Software\\Microsoft\\Windows\\CurrentVersion\\Run", REGISTRY_KEY);

        if (state && !containsRegistry) {
            try {
                File executable = new File(InterfaceApp.class.getProtectionDomain().getCodeSource().getLocation()
                        .toURI());

                Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                        "Software\\Microsoft\\Windows\\CurrentVersion\\Run", REGISTRY_KEY, executable.getAbsolutePath());
            } catch (URISyntaxException e) {
                log.log(Level.SEVERE, "Unable to find executable", e);
            }
        } else if (!state && containsRegistry) {
            Advapi32Util.registryDeleteValue(WinReg.HKEY_CURRENT_USER,
                    "Software\\Microsoft\\Windows\\CurrentVersion\\Run", REGISTRY_KEY);
        }
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

    private String getBuildVersion() {
        return InterfaceApp.class.getPackage().getImplementationVersion();
    }

    public static void main(String args[]) {
        instance = new InterfaceApp();
        instance.execute();
    }
}
