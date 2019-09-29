package Recording;

import java.util.Optional;

public class RecorderEventMessage {
    private RecorderEvent recorderEvent;
    private String message;

    public RecorderEventMessage(RecorderEvent recorderEvent) {
        this.recorderEvent = recorderEvent;
    }

    public RecorderEventMessage(RecorderEvent recorderEvent, String message) {
        this.recorderEvent = recorderEvent;
        this.message = message;
    }

    public RecorderEvent getRecorderEvent() {
        return recorderEvent;
    }

    public Optional<String> getMessage() {
        return Optional.of(message);
    }
}
