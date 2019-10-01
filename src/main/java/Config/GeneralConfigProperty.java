package Config;

public enum GeneralConfigProperty {
    AUTO_REMOVE_OLD_RECORDING("autoRemoveOldRecording"),
    ;

    private String key;

    GeneralConfigProperty(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
