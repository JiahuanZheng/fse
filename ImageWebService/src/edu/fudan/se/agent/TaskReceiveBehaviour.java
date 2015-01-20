package edu.fudan.se.agent;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import edu.fudan.se.undergraduate.opration.ResponseDBOperation;
import fudan.se.pool.TaskTypeEnum;
import fudan.se.pool.Work2ServletMessage;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class TaskReceiveBehaviour extends TickerBehaviour {

	private ServletAgent myAgent = null;

	public TaskReceiveBehaviour(ServletAgent a, long period) {
		// TODO Auto-generated constructor stub
		super(a, period);
		myAgent = a;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onTick() {
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchConversationId("result"));
		ACLMessage aclMsg = myAgent.receive(mt);
		if (aclMsg != null) {
			try {
				Work2ServletMessage sendMsg = (Work2ServletMessage) aclMsg
						.getContentObject();
				String dataXML = sendMsg.getMess();
				System.out.println("Servlet Receive : " + dataXML);
				Document doc =  DocumentHelper.parseText(dataXML);
				List<Element> ls = doc.selectNodes("/UIdisplay/TakeImage");
				if(ls != null && ls.size()>0){
					byte[] bytes = sendMsg.getFileBytes();
					String path = byte2Image(bytes,"tmpphoto/" + new Date().getTime() + "tmp.jpg");
					ls.get(0).element("Value").setText(path);
				}
				String workerGuid = aclMsg.getSender().getName();
				long taskid = sendMsg.getTaskid();
				ResponseDBOperation.insertResponse(taskid, workerGuid, "",
						doc.asXML());
				System.out.println(doc.asXML());
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public String byte2Image(byte[] data, String path) {
		if (data.length < 3 || path.equals("")) {
			System.out.println("taiduanle");
			return null;
		}
		try {
			File ftmp = new File(path);
			FileOutputStream imageOutput = new FileOutputStream(ftmp);
			imageOutput.write(data, 0, data.length);
			imageOutput.close();
			System.out.println("Make Picture success,Please find image in "
					+ path);
			return ftmp.getAbsolutePath();
		} catch (Exception ex) {
			System.out.println("Exception: " + ex);
			ex.printStackTrace();
		}
		return null;
	}
}

//
//if (sendMsg.getType() == TaskTypeEnum.WORD2PHOTO) {
//	byte[] bytes = sendMsg.getFileBytes();
//	String path = byte2Image(bytes,"tmpphoto/" + new Date().getTime() + "tmp.jpg");
//	
//	
//	System.out.println("WORD2PHOTO的结果服务器已经收到 存入的地址是 "
//			+ path);
//}
//if (sendMsg.getType() == TaskTypeEnum.WORD2AUDIO) {
//	byte[] bytes = sendMsg.getFileBytes();
//	byte2Image(bytes, "C:/Users/Jiahuan/Desktop/tmp.3gp");
//	// 并把数据写入数据库中
//}
//if (sendMsg.getType() == TaskTypeEnum.PHOTO2WORD) {
//	System.out.println("收到的消息: type  " + sendMsg.getType()
//			+ "  mess:  " + sendMsg.getMess());
//
//	long taskid = sendMsg.getTaskid();
//	String workerGuid = aclMsg.getSender().getName();
//	ResponseDBOperation.insertResponse(taskid, workerGuid, "",
//			sendMsg.getMess());
//	// 这里面是忽略了template的作用。
//	System.out.println("PHOTO2WORD的结果服务器已经收到 ：价格是: "
//			+ sendMsg.getMess());
//}
//
//// System.exit(0);

