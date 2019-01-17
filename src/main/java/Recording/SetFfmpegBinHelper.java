package Recording;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;


public class SetFfmpegBinHelper
{
    private SetFfmpegBinHelper(){}

    public static Optional<Path> setLocalFfmpegOnWindows()
    {
        final JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.home")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                ".exe", "exe");
        fc.setFileFilter(filter);
        fc.setDialogTitle("Select the path to ffmpeg.exe file...");
        int result = fc.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            // user selects a file
            File selectedFile = fc.getSelectedFile();
            // TODO write to config file
            return Optional.of(selectedFile.toPath());
        }
        return Optional.empty();
    }
}
