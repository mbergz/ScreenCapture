package Config.RecorderSpecific.ffmpeg;

public enum FfmpegProcessArgument {
    FFMPEG_PATH_BIN("ffmpegPathBin"),
    PATH_TO_SAVED_RECORDING("pathToSaved"),
    FPS("framerate"),
    MOVIE_NAME("movieName"),
    VIDEO_SIZE("video_size")
    ;

    private String jsonKey;

    FfmpegProcessArgument(String jsonKey) {
        this.jsonKey = jsonKey;
    }

    public String key() {
        return jsonKey;
    }

}
