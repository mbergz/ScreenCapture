package Eventhandlers.Payload;

import java.util.Optional;

// TODO
public class RecordingStoppedEventPayload implements Payload {
    private String message;
    private String pathToRecordedFile;

    @Override
    public String getMessage() {
        return message;
    }

    public Optional<String> getPathToRecordedFile() {
        return Optional.of(pathToRecordedFile);
    }

    public RecordingStoppedEventPayload setMessage(String message) {
        this.message = message;
        return this;
    }

    public RecordingStoppedEventPayload setPathToRecordedFile(String pathToRecordedFile) {
        this.pathToRecordedFile = pathToRecordedFile;
        return this;
    }
}
