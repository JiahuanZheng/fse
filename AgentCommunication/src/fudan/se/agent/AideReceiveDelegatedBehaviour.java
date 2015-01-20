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
import android.util.Base64;
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
	private long period;

	public AideReceiveDelegatedBehaviour(AideAgent agent, long aperiod) {
		// TODO Auto-generated constructor stub
		super(agent, aperiod);
		myAgent = agent;
		period = aperiod;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onTick() {

		if (myAgent.getServletAID() == null) {
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("delegate"));

			ACLMessage aclMsg = myAgent.receive(mt);
			if (aclMsg != null) {
				myAgent.setServletAID(aclMsg.getSender());
				try {
					Work2ServletMessage rcvMsg = (Work2ServletMessage) aclMsg
							.getContentObject();
					String dataXml = rcvMsg.getMess();
					Document doc = DocumentHelper.parseText(dataXml);
					
					System.out.println("dataXML" + dataXml);
									
					List<Element> eles = doc
							.selectNodes("/UIdisplay/DisplayImage");
					if (eles != null && eles.size() > 0) {
						// 证明xml文件中含有图片信息，所以在此处解析它并将其值替换成路径，以减少UI线程的负担。
						byte[] bytes = rcvMsg.getFileBytes();
						String path = Environment.getExternalStorageDirectory()
								.getPath()
								+ "/photo2word"
								+ new Date().getTime() + ".jpg";
						byte2Image(bytes, path);
						eles.get(0).element("Value").setText(path);
					}

					Bundle bundle = new Bundle();
					bundle.putLong("taskid", rcvMsg.getTaskid());
					bundle.putString("message", doc.asXML());
					Message msg = new Message();
					msg.what = 1;
					msg.setData(bundle);
					myAgent.getHandler().sendMessage(msg);
				} catch (UnreadableException e) {
					System.out.println("接受servlet消息出错！！！");
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
