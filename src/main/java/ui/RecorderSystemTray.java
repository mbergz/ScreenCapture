package ui;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import Eventhandlers.Event;
import Eventhandlers.EventHandler;
import Eventhandlers.SubscribeEvent;
import Recording.Recorder;

public class RecorderSystemTray {
    private static final String START_RECORD = "Start record";
    private static final String STOP_RECORD = "Stop record";
    private Recorder recorder;

    public RecorderSystemTray(Recorder recorder){
        if (!SystemTray.isSupported()) {
            System.out.println("RecorderSystemTray is not supported");
            return;
        }
        EventHandler.getInstance().addHandler(this);
        this.recorder = recorder;
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon =
                new TrayIcon(createImage("images/videocam-filled-tool.png", "tray icon"));
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a pop-up menu components
        MenuItem recordItem = createToggleRecordingMenuItem();
        Menu settingsMenu = new Menu("Settings");
        Menu framerateItem = createFramerateMenu();
        MenuItem dirSaveItem = new MenuItem("Directory to save file");
        MenuItem resetSettingsMenuItem = new MenuItem("Reset to default settings");
        CheckboxMenuItem copyClipBoardItem = new CheckboxMenuItem("Copy to clipboard after recording");
        MenuItem shortCutItem = new MenuItem("Shortcuts...");

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(a -> System.exit(0));

        //Add components to pop-up menu
        popup.add(recordItem);
        popup.addSeparator();
        popup.add(settingsMenu);
        settingsMenu.add(framerateItem);
        settingsMenu.add(dirSaveItem);
        settingsMenu.add(copyClipBoardItem);
        settingsMenu.add(shortCutItem);
        settingsMenu.add(resetSettingsMenuItem);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
    }

    private Menu createFramerateMenu() {
        Menu framreateItem = new Menu("Framerate...");
        List<MenuItem> items = new ArrayList<>();
        items.add(new MenuItem("60"));
        items.add(new MenuItem("45"));
        items.add(new MenuItem("30"));
        items.add(new MenuItem("20"));
        items.add(new MenuItem("15"));
        items.add(new MenuItem("10"));
        items.add(new MenuItem("5"));
        items.forEach(item -> {
            item.addActionListener(a -> {
                int fpsLabel = Integer.parseInt(((MenuItem)a.getSource()).getLabel());
                recorder.setFps(fpsLabel);
            });
            framreateItem.add(item);
        });
        return framreateItem;
    }

    private MenuItem createToggleRecordingMenuItem() {
        MenuItem recordItem = new MenuItem(START_RECORD);
        recordItem.addActionListener(a -> {
            String currentLabel = ((MenuItem)a.getSource()).getLabel();
            try {
                if (currentLabel.equalsIgnoreCase(START_RECORD)) {
                    recorder.startRecording();
                    recordItem.setLabel(STOP_RECORD);
                } else {
                    recorder.stopRecording();
                    recordItem.setLabel(START_RECORD);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        return recordItem;
    }

    private Image createImage(String imagePath,  String description) {
        URL imageURL = this.getClass().getClassLoader().getResource(imagePath);
        if (imageURL == null) {
            System.err.println("Resource not found: " + imagePath);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }

    @SubscribeEvent(event = Event.RECORDING)
    public void onRecordingEvent(Object object) {
        System.out.println("onRecordingEvent was called... object: " + object);
    }
}
