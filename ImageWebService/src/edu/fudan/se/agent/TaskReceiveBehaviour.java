package edu.fudan.se.agent;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

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

	@Override
	protected void onTick() {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		// System.out.println("T am Servlet Agent !!!");
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchConversationId("result"));
		ACLMessage aclMsg = myAgent.receive(mt);

		// String workerGUID = aclMsg.getSender().getName();// 获取worker的全局ID。

		if (aclMsg != null) {
			try {
				Work2ServletMessage sendMsg = (Work2ServletMessage) aclMsg
						.getContentObject();
				if (sendMsg.getType() == TaskTypeEnum.WORD2PHOTO) {
					byte[] bytes = sendMsg.getFileBytes();
					byte2Image(bytes,
							"C:/Users/Jiahuan/Desktop/" + new Date().getTime()
									+ "tmp.jpg");

					// 并把数据写入数据库中
				}
				if (sendMsg.getType() == TaskTypeEnum.WORD2AUDIO) {
					byte[] bytes = sendMsg.getFileBytes();
					byte2Image(bytes, "C:/Users/Jiahuan/Desktop/tmp.3gp");
					// 并把数据写入数据库中
				}
				if (sendMsg.getType() == TaskTypeEnum.PHOTO2WORD) {
					System.out.println("收到的消息: type  " + sendMsg.getType()
							+ "  mess:  " + sendMsg.getMess());

					long taskid = sendMsg.getTaskid();
					String workerGuid = aclMsg.getSender().getName();
					ResponseDBOperation.insertResponse(taskid, workerGuid, "",
							sendMsg.getMess());
					// 这里面是忽略了template的作用。
					System.out.println("PHOTO2WORD的结果服务器已经收到 ：价格是: "
							+ sendMsg.getMess());
				}

				// System.exit(0);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// else {
		// // System.out.println("没有消息！！！");
		// }
	}

	public void byte2Image(byte[] data, String path) {
		if (data.length < 3 || path.equals("")) {
			System.out.println("taiduanle");
			return;
		}
		try {
			FileOutputStream imageOutput = new FileOutputStream(new File(path));
			imageOutput.write(data, 0, data.length);
			imageOutput.close();
			System.out.println("Make Picture success,Please find image in "
					+ path);
		} catch (Exception ex) {
			System.out.println("Exception: " + ex);
			ex.printStackTrace();
		}
	}
}
