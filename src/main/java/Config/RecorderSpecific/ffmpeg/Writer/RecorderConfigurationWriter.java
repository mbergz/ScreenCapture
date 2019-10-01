package Config.RecorderSpecific.ffmpeg.Writer;

import Config.RecorderSpecific.ffmpeg.FfmpegProcessArgument;

public interface RecorderConfigurationWriter {

    void setProcessArgumentProperty(FfmpegProcessArgument key, String newValue);

    void setGeneralProperty(String key, String newValue);

}
