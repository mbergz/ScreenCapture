package Config.RecorderSpecific;

import Recording.SetFfmpegBinHelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

// TODO
public class RecorderValueDynamicConstans {
    public static String MOVIE_NAME = "screen_capture" + UUID.randomUUID() + ".mov";

    public static Path getFfmpegBinPath() {
        return setUpFfmpegPathBin();
    }

    private static Path setUpFfmpegPathBin() {
        final String OS = System.getProperty("os.name").toLowerCase();
        if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) {
            return Paths.get("ffmpeg");
        } else if (OS.contains("win")) {
            Optional<Path> ffmpegPathOnWin = SetFfmpegBinHelper.setLocalFfmpegOnWindows();
            if (ffmpegPathOnWin.isPresent()) {
                return ffmpegPathOnWin.get();
            } else {
                throw new FfmpegCouldNotBeLocatedRunTimeException("Could not locate ffmpeg.exe on windows C:// drive, exiting...");
            }
        }
        return null;
    }

    private static class FfmpegCouldNotBeLocatedRunTimeException extends RuntimeException {

        FfmpegCouldNotBeLocatedRunTimeException(String message) {
            super(message);
        }

        FfmpegCouldNotBeLocatedRunTimeException(String message, Throwable cause) {
            super(message, cause);
        }

    }
}
