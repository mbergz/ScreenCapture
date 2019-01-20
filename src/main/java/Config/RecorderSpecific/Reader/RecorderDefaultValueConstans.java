package Config.RecorderSpecific.Reader;

import Recording.SetFfmpegBinHelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

class RecorderDefaultValueConstans {
    static final boolean SHOULD_AUTOREMOVE = false;
    static final int FRAMERATE = 15;
    static final Path DIR_TO_SAVED_RECORDING = Paths.get(".");
    static final String MOVIE_NAMME = "screen_capture" + UUID.randomUUID() + ".mov";

    static Path getFfmpegBinPath() {
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
