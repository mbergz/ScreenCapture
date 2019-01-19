import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import Config.ConfigurationFileReader;
import Config.Recorder.Reader.RecorderConfigurationReader;
import Recording.FfmpegRecorder;
import Recording.SetFfmpegBinHelper;
import ui.RecorderSystemTray;

public class Launcher {
    private static final int DEFAULT_FRAMERATE = 15;
    private static final Path DEFUALT_PATH = Paths.get("c:\\Users\\Martin\\Documents\\ffmpeg\\ffmpeg-20190112-1ea5529-win64-static\\bin\\ffmpeg.exe");

    public static void main(String[] args) throws IOException, InterruptedException {
        /*Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                new ProcessBuilder("taskkill", "/f", "/im", "ffmpeg.exe").start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        */
        // Defualt or read from config file, if exists...
        FfmpegRecorder recorder = new FfmpegRecorder(new RecorderConfigurationReader() {
            @Override
            public boolean isShouldAutoRemoveOld() {
                return false;
            }

            @Override
            public Path getDirPathToSavedRecordings() {
                return Paths.get(".");
            }

            @Override
            public Path getFfmpegBinPath() {
                try {
                    Path locatedPath = setUpFfmpegPathBin();
                    if (locatedPath != null) {
                        return locatedPath;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return DEFUALT_PATH;
            }

            @Override
            public int getFps() {
                return DEFAULT_FRAMERATE;
            }

            @Override
            public String getMovieName() {
                return "screen_capture" + UUID.randomUUID() + ".mov";
            }
        });
        new RecorderSystemTray(recorder);
    }

    private static Path setUpFfmpegPathBin() throws FileNotFoundException {
        ConfigurationFileReader configHandler = ConfigurationFileReader.getInstance();
        Optional<String> ffmpegConfigValue = configHandler.getProperty("ffmpegPathBin");
        if (ffmpegConfigValue.isPresent()){
            return Paths.get(ffmpegConfigValue.get());
        }
        final String OS = System.getProperty("os.name").toLowerCase();
        if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) {
            return Paths.get("ffmpeg");
        } else if (OS.contains("win")) {
            Optional<Path> ffmpegPathOnWin = SetFfmpegBinHelper.setLocalFfmpegOnWindows();
            if (ffmpegPathOnWin.isPresent()) {
                return ffmpegPathOnWin.get();
            } else {
                throw new FileNotFoundException("Could not locate ffmpeg.exe on windows C:// drive");
            }
        }
        return null;
    }
}
