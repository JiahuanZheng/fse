package fudan.se.pool;

import jade.core.AID;
import jade.util.leap.Serializable;

public class Work2ServletMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	//如果type的值是0，那么path存的就是用户回答的文字信息，如果是一，path存的就是图片的位置，如果是2，那么存的就是音频的位置。
	private TaskTypeEnum type = TaskTypeEnum.OTHER;//0 代表单纯的回复字符  1 代表发送 照片  2 代表发送的是 音频
	private String mess = null;//可能是文件的路径，也有可能是消息。
	private byte[] fileBytes= null;

	private AID servletAID = null;
	
	private long taskid = 0;
	public long getTaskid() {
		return taskid;
	}
	public void setTaskid(long taskid) {
		this.taskid = taskid;
	}
	public TaskTypeEnum getType() {
		return type;
	}
	public void setType(TaskTypeEnum type) {
		this.type = type;
	}
	public String getMess() {
		return mess;
	}
	public void setMess(String mess) {
		this.mess = mess;
	}
	public Work2ServletMessage(TaskTypeEnum type,String mess){
		this.type = type;
		this.mess = mess;
	}
	public byte[] getFileBytes() {
		return fileBytes;
	}
	public void setFileBytes(byte[] fileBytes) {
		this.fileBytes = fileBytes;
	}
	public AID getServletAID() {
		return servletAID;
	}
	public void setServletAID(AID servletAID) {
		this.servletAID = servletAID;
	}

}
