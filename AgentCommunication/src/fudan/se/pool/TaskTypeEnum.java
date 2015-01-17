package fudan.se.pool;

public enum TaskTypeEnum {
	
    PHOTO2WORD(0),WORD2PHOTO(1),WORD2AUDIO(2),REGISTER(3),HEARTBEAT(4),OTHER(5);
	int value;
	TaskTypeEnum(int value){
		this.value = value;
	}
    public int getVal(){
    	return value;
    }
    //分别是：发来图片要求回复文字、发来文字要求图片、发来文字要求声音、其他。
    //
}
