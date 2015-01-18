package fudan.se.agent;

import fudan.se.location.MyLocation;
import android.os.Handler;

public interface CommunicationInterface {
	public void setHandler(Handler handler);
	public void setCapacity(String capacity);
	public void setCustomLocation(MyLocation myLocation);
}
