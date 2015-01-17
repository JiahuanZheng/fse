package fudan.se.fseconsumer;

import java.io.Serializable;

public class UploadInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UploadType type = UploadType.TEXT;
	private String name = null;
	private String value = null;
	
	public UploadInfo(){
		
	}
	public UploadInfo(UploadType type,String name,String value){
		this.type = type;
		this.name = name;
		this.value = value;
	}
	
	public UploadType getType() {
		return type;
	}
	public void setType(UploadType type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
