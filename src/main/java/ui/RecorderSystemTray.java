package ui;

import Config.RecorderSpecific.Writer.RecorderConfigurationWriter;
import Eventhandlers.Event;
import Eventhandlers.EventHandler;
import Eventhandlers.Payload.Payload;
import Eventhandlers.Payload.RecordingStoppedEventPayload;
import Eventhandlers.SubscribeEvent;
import Config.RecorderSpecific.Writer.RecorderConfigurationFromFileWriterImpl;
import Recording.Recorder;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class RecorderSystemTray {
    private static final String START_RECORD = "Start record";
    private static final String STOP_RECORD = "Stop record";
    private Recorder recorder;
    private RecorderConfigurationWriter recorderConfigWriter = new RecorderConfigurationFromFileWriterImpl();
    private final TrayIcon trayIcon = new TrayIcon(createImage("images/videocam-filled-tool.png", "tray icon"));

    public RecorderSystemTray(Recorder recorder){
        if (!SystemTray.isSupported()) {
            System.out.println("RecorderSystemTray is not supported");
            return;
        }
        EventHandler.getInstance().addHandler(this);
        this.recorder = recorder;
        final PopupMenu popup = new PopupMenu();
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a pop-up menu components
        MenuItem recordItem = createToggleRecordingMenuItem();

        Menu settingsMenu = new Menu("Settings");
        MenuItem dirSaveItem = createSaveDirMenuItem();
        CheckboxMenuItem autoRemoveOld = createAutoRemoveOldRecordinsMenuItem();
        Menu framerateItem = createFramerateMenu();
        MenuItem resetSettingsMenuItem = new MenuItem("Reset to default settings");

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(a -> System.exit(0));

        //Add components to pop-up menu
        popup.add(recordItem);
        popup.addSeparator();
        popup.add(settingsMenu);
        settingsMenu.add(framerateItem);
        settingsMenu.add(dirSaveItem);
        settingsMenu.add(autoRemoveOld);
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
        var items = new ArrayList<MenuItem>();
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
                recorderConfigWriter.setFps(fpsLabel);
            });
            framreateItem.add(item);
        });
        return framreateItem;
    }

    private CheckboxMenuItem createAutoRemoveOldRecordinsMenuItem(){
        CheckboxMenuItem item = new CheckboxMenuItem("Autoremove recordings");
        item.addItemListener(itemEvent -> {
            int itemState = itemEvent.getStateChange();
            if (itemState == ItemEvent.SELECTED){
                recorderConfigWriter.setAutoRemovalOfOldRecording(true);
            } else {
                recorderConfigWriter.setAutoRemovalOfOldRecording(false);
            }
        });
        return item;
    }

    private MenuItem createSaveDirMenuItem() {
        MenuItem item = new MenuItem("Directory to save file");
        item.addActionListener(a -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("Choose directory to store recordings in");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                System.out.println("getCurrentDirectory(): "
                        +  chooser.getCurrentDirectory());
                recorderConfigWriter.setDirectoryToSaveRecordings(chooser.getCurrentDirectory().toPath());
            }
            else {
                System.out.println("No Selection, keeping default directory");
            }
        });
        return item;
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

    @SubscribeEvent(event = {Event.RECORDING_STARTED, Event.RECORDING_STOPPED} )
    public void onRecordingEvent(Payload payload) {
        trayIcon.displayMessage("ScreenCapture™", payload.getMessage(), TrayIcon.MessageType.INFO);
    }

    @SubscribeEvent(event = {Event.RECORDING_STOPPED} )
    public void onRecordingStoppedEvent(RecordingStoppedEventPayload payload) {
        payload.getPathToRecordedFile().ifPresent(filePath -> {
            System.out.println(filePath );
            StringSelection stringSelection = new StringSelection(filePath);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);

            try {
                Runtime.getRuntime().exec("explorer.exe /select," + filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
