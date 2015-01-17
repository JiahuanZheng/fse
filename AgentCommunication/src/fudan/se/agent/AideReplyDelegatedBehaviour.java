package fudan.se.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import fudan.se.pool.TaskTypeEnum;
import fudan.se.pool.Work2ServletMessage;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class AideReplyDelegatedBehaviour extends TickerBehaviour{

	private AideAgent myAgent = null;
	private long period ;
	
	public AideReplyDelegatedBehaviour(AideAgent a, long aPeriod) {
		super(a, aPeriod);
		// TODO Auto-generated constructor stub
		myAgent = a;
		period = aPeriod;
	}

	 // 发送处理之后的消息
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("resource")
	@Override
	protected void onTick() {
		// TODO Auto-generated method stub
		System.out.println("I am executing ...");
		Object tmpObj = myAgent.getO2AObject();
		if (tmpObj != null) { // UI界面通过putO2AObject的方式给我发了一个消息，告诉我照片或者音频或者消息已经处理好了。
			Work2ServletMessage sendMsg = (Work2ServletMessage) tmpObj;

			if (sendMsg.getType() == TaskTypeEnum.PHOTO2WORD) {
				ACLMessage aclMsg = new ACLMessage(ACLMessage.INFORM);
				aclMsg.setConversationId("result");
				try {
					aclMsg.setContentObject(sendMsg);
					if (myAgent.getServletAID() != null) {
						// 如果servletAID存在，则就认为此消息是此servletAID需要的
						// System.out.println("ahhhha"+servletAID);
						aclMsg.addReceiver(myAgent.getServletAID());
						myAgent.send(aclMsg);// ================================================
						myAgent.setServletAID(null);// 好让继续接收消息
					} else {
						System.out.println("没有找到对应的servlet！！！");
						System.exit(0); // 每一个任务的完成都对应一个发送方，如果发送给我消息的人为空，显然是错误的。
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (sendMsg.getType() == TaskTypeEnum.WORD2AUDIO
					|| sendMsg.getType() == TaskTypeEnum.WORD2PHOTO) {
				// 这个if里面处理的是图片或者音频
				try {
					File file = new File(sendMsg.getMess());
					long fileSize = file.length();
					if (fileSize > Integer.MAX_VALUE) { // 注意
														// 这里面是和Integer的最大值相比较的，因为字节数组中的单位最大只能够开到int。
						System.out.println("444");
						new Exception("The file is too big!!!");
					}
					FileInputStream fi = new FileInputStream(file);
					byte[] bufferbytes = new byte[(int) fileSize];
					int offset = 0;
					int numRead = 0;
					while (offset < bufferbytes.length
							&& (numRead = fi.read(bufferbytes, offset,
									bufferbytes.length - offset)) >= 0) {
						offset += numRead;
					}
					// 确保所有数据均被读取
					if (offset != bufferbytes.length) {
						throw new Exception(
								"Could not completely read file "
										+ file.getName());
					}
					System.out.println("333  文件的长度是 ：  " + offset);
					fi.close();
					sendMsg.setFileBytes(bufferbytes);
					// seek servelt agent.
					ACLMessage aclMsg = new ACLMessage(ACLMessage.INFORM);
					aclMsg.setConversationId("result");
					aclMsg.setContentObject(sendMsg);// 将这个消息的类型，以及图片或者是音频的字节码发过去。
					if (myAgent.getServletAID() != null) {// 如果servletAID存在，则就认为此消息是此servletAID需要的
						// System.out.println("ahhhha"+servletAID);
						aclMsg.addReceiver(myAgent.getServletAID());
						myAgent.send(aclMsg);// ================================================
						myAgent.setServletAID(null);// 好让继续接收消息
					} else {
						System.out.println("没有找到对应的servlet！！！");
						System.exit(0); // 每一个任务的完成都对应一个发送方，如果发送给我消息的人为空，显然是错误的。
					}
					//
				} catch (Exception e) {
					System.out.println("Wrong with importing file!!!");
					e.printStackTrace();
				}
			}
		}// UI界面通过putO2AObject的方式给我发了一个消息，告诉我照片或者音频或者消息已经处理好了。
	}

}
