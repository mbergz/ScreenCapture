package Config.RecorderSpecific.Writer;

import Config.RecorderSpecific.RecorderJsonKeyConstants;

public interface RecorderConfigurationWriter {

    public void setProperty(RecorderJsonKeyConstants key, String newValue);

}
