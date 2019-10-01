package Recording;

import Config.RecorderSpecific.ffmpeg.FfmpegProcessArgument;

public interface ProcessRecorder extends Recorder {

    void setProcessConfigurationValue(FfmpegProcessArgument key, String value);

}
