package fudan.se.agent;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import fudan.se.pool.TaskTypeEnum;
import fudan.se.pool.Work2ServletMessage;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.leap.Iterator;

public class AideReceiveDelegatedBehaviour extends TickerBehaviour {

	private AideAgent myAgent;
	private long period ;
	
	public AideReceiveDelegatedBehaviour(AideAgent agent, long aperiod) {
		// TODO Auto-generated constructor stub
		super(agent, aperiod);
		myAgent = agent;
		period = aperiod;
	}

	@Override
	protected void onTick() {

		// TODO Auto-generated method stub
		if (myAgent.getServletAID() == null) {
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("delegate"));
//			MessageTemplate aidMatch = MessageTemplate
//					.MatchReceiver(new AID[] { myAgentAID });
//			mt = MessageTemplate.and(mt, aidMatch);

			ACLMessage aclMsg = myAgent.receive(mt);
			if (aclMsg != null) {
				System.out.println("有人委托给我任务............");
				Iterator tt = aclMsg.getAllReceiver();
				while (tt.hasNext()) {
					System.out.println("hahaha" + tt.next().toString());
				}
				System.out.println("消息打印结束............");
				myAgent.setServletAID(aclMsg.getSender());
				// System.out.println("servletAID:"+servletAID.getName());
				try {
					Work2ServletMessage rcvMsg = (Work2ServletMessage) aclMsg
							.getContentObject();
					// rcvMsg.setServletAID(aclMsg.getSender());
					Message msg = new Message();
					msg.what = rcvMsg.getType().getVal();

					if (rcvMsg.getType() == TaskTypeEnum.WORD2PHOTO) {
						// 当前发来的请求是消息文本，要求的是图片。
						Bundle bundle = new Bundle();
						bundle.putString("message", rcvMsg.getMess());
						// System.out.println("000消息已经接受到了  消息为 : + " +
						// rcvMsg.getMess());
						bundle.putLong("taskid", rcvMsg.getTaskid());
						msg.setData(bundle);
					}
					if (rcvMsg.getType() == TaskTypeEnum.PHOTO2WORD) {
						// 如果任务类型是发送来的图片，请求评论的，则这个图片默认保存在/sdcard/下方。
						// 为了简化任务，采用很多手机 上面的jpg格式。
						byte[] bytes = rcvMsg.getFileBytes();
						String path = Environment.getExternalStorageDirectory()
								.getPath()
								+ "/photo2word"
								+ new Date().getTime() + ".jpg";
						byte2Image(bytes, path);
						Bundle bundle = new Bundle();
						bundle.putString("path", path);

						String message = rcvMsg.getMess();
						Document doc;
						try {
							doc = DocumentHelper.parseText(message);
							List<Element> eles = doc
									.selectNodes("/task/description");
							Element ele = eles.get(0);
							String description = ele.getText();

							List<Element> eles2 = doc
									.selectNodes("/task/inputs/input");
							ArrayList<String> nameList = new ArrayList<String>();
							ArrayList<String> valList = new ArrayList<String>();
							for (Element element : eles2) {
								if ("TEXT".equalsIgnoreCase(element.element(
										"type").getText())) {
									nameList.add(element.element("name")
											.getText());
									valList.add(element.element("value")
											.getText());

									System.out.println("name"
											+ element.element("name").getText()
											+ "value"
											+ element.element("value")
													.getText());

								}
							}
							// -----------------------------------------------------
							// bundle.putString("message",
							// rcvMsg.getMess());

							bundle.putStringArrayList("keys", nameList);
							bundle.putStringArrayList("values", valList);
							bundle.putString("description", description);
						} catch (DocumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						bundle.putLong("taskid", rcvMsg.getTaskid());
						msg.setData(bundle);
					}

					if (rcvMsg.getType() == TaskTypeEnum.WORD2AUDIO) {
						// ....暂缺，原理相同
					}

					myAgent.getHandler().sendMessage(msg);
				} catch (UnreadableException e) {
					System.out.println("接受servlet消息出错！！！");
				}
			}
		}

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
