package fudan.se.agent;

import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import android.util.Base64;
import fudan.se.pool.Work2ServletMessage;

public class AideReplyDelegatedBehaviour extends TickerBehaviour {

	// 发送处理之后的消息
	private static final long serialVersionUID = 1L;
	
	private AideAgent myAgent = null;

	public AideReplyDelegatedBehaviour(AideAgent a, long aPeriod) {
		super(a, aPeriod);
		// TODO Auto-generated constructor stub
		myAgent = a;
	}

	@SuppressWarnings({"unchecked" })
	@Override
	protected void onTick() {
		// TODO Auto-generated method stub
		System.out.println("I am executing ...");
		Object tmpObj = myAgent.getO2AObject();
		if (tmpObj != null) { // UI界面通过putO2AObject的方式给我发了一个消息，告诉我照片或者音频或者消息已经处理好了。

			ACLMessage aclMsg = new ACLMessage(ACLMessage.INFORM);
			aclMsg.setConversationId("result");

			Work2ServletMessage sendMsg = (Work2ServletMessage) tmpObj;
			String dataXML = sendMsg.getMess();
			Document doc;
			try {
				doc = DocumentHelper.parseText(dataXML);

				List<Element> eles = doc.selectNodes("/UIdisplay/TakeImage");
				if (eles != null && eles.size() > 0) {
					String path = eles.get(0).element("Value").getText();
					byte[] bytes = image2Bytes(path);
//					String base64Str = Base64.encodeToString(bytes,Base64.DEFAULT);
//					eles.get(0).element("Value").setText(base64Str);
//					sendMsg.setMess(doc.asXML());
					sendMsg.setFileBytes(bytes);
				}
				
			//这个地方其实还对应着判别TakeAudio等。。。。。后续添加。
				
				System.out.println("Back answer : " + doc.asXML());
				
				aclMsg.setContentObject(sendMsg);
				if (myAgent.getServletAID() != null) {
					aclMsg.addReceiver(myAgent.getServletAID());
					myAgent.send(aclMsg);// ================================================
					myAgent.setServletAID(null);// 好让继续接收消息
				} else {
					System.out.println("没有找到对应的servlet！！！");
					System.exit(0); // 每一个任务的完成都对应一个发送方，如果发送给我消息的人为空，显然是错误的。
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	@SuppressWarnings("resource")
	public byte[] image2Bytes(String path) throws Exception {
		File file = new File(path);
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
				&& (numRead = fi.read(bufferbytes, offset, bufferbytes.length
						- offset)) >= 0) {
			offset += numRead;
		}
		// 确保所有数据均被读取
		if (offset != bufferbytes.length) {
			throw new Exception("Could not completely read file "
					+ file.getName());
		}
		System.out.println("333  文件的长度是 ：  " + offset);
		fi.close();
		return bufferbytes;
	}

}
