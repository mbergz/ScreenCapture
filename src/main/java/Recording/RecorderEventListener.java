package Recording;

@FunctionalInterface
public interface RecorderEventListener {

    void onRecorderEvent(RecorderEventMessage recorderEventMessage);
}
