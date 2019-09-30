package Config.RecorderSpecific;

public enum RecorderJsonKeyConstants {
    FFMPEG_PATH_BIN("ffmpegPathBin"),
    AUTO_REMOVE_OLD_RECORDING("autoRemoveOldRecording"),
    PATH_TO_SAVED_RECORDING("pathToSaved"),
    FPS("fps"),
    MOVIE_NAME("movieName"),
    VIDEO_SIZE("videoSize")
    ;

    private String jsonKey;

    private RecorderJsonKeyConstants(String jsonKey) {
        this.jsonKey = jsonKey;
    }

    public String getJsonKey() {
        return jsonKey;
    }

}
