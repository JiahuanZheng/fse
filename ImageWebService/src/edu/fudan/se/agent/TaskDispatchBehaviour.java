package edu.fudan.se.agent;

import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import edu.fudan.se.undergraduate.dbObject.AgentInfo;
import edu.fudan.se.undergraduate.dbObject.MicroTask;
import edu.fudan.se.undergraduate.opration.AgentDBOperation;
import edu.fudan.se.undergraduate.opration.MicroTaskOperation;
import fudan.se.pool.TaskTypeEnum;
import fudan.se.pool.Work2ServletMessage;

public class TaskDispatchBehaviour extends TickerBehaviour {

	private ServletAgent myAgent = null;

	public TaskDispatchBehaviour(ServletAgent a, long period) {
		super(a, period);
		// TODO Auto-generated constructor stub
		myAgent = a;
	}
	@SuppressWarnings("unchecked")
	@Override
	protected void onTick() {
		// TODO Auto-generated method stub
		// 这个地方先模拟servlet agent
		// 从数据库中模拟拿出了一个消息，这里假设是一个word2photo的消息。worker的名字叫做 ttt.

		List<MicroTask> taskList = MicroTaskOperation.queryTask("initial");
		for (MicroTask microTask : taskList) {
			MicroTaskOperation.updateMicroTask(microTask.getId(), "creating");
			// 将状态为initial的任务拿出来，并将其状态置为creating状态。这样可以防止一个任务派发两次。
			String template = microTask.getTemplate();
			
			ACLMessage aclMsg = new ACLMessage(ACLMessage.INFORM);
			aclMsg.setConversationId("delegate");
			Work2ServletMessage w2sMsg = new Work2ServletMessage(
					microTask.getId(), template);
			
			
			Document doc; 
			try {
				doc = DocumentHelper.parseText(template);
				List<Element> eles = doc.selectNodes("/UIdisplay/DisplayImage");
				if(eles != null && eles.size()>0){
					Element ele = eles.get(0);
					String path = ele.element("Value").getText();
					System.out.println("path si : "+path);
					File file = new File(path);
					long fileSize = file.length();
		
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
						System.out.println("file wrong !!!");
					}
					System.out.println("333  文件的长度是 ：  " + offset);
					fi.close();
					
//					String textTmp = Base64.getEncoder().encodeToString(bufferbytes);
					
//					System.out.println("将要发送到work上面"+textTmp);
					
//					ele.setText(textTmp);
					w2sMsg.setFileBytes(bufferbytes);
				}

				List<Element> eles2 = doc.selectNodes("/UIdisplay/DisplayAudio");
				if(eles2 != null && eles2.size()>0){
					//留待以后扩展.
				}
//				System.out.println("Servlet Agent Dispatch : " + doc.asXML());
				aclMsg.setContentObject(w2sMsg);
				List<AgentInfo> agentInfos;
				try {
					agentInfos = AgentDBOperation.getOnlineAgents();
					// 这个地方以后应该是要添加一些新的功能，比如agents是依据相关的限制选择出来的，这个里面现在是依据所有的在线的agent发送的。
					for (AgentInfo agent : agentInfos) {
						AID receiver = new AID();
						receiver.setName(agent.getGuid());
						System.out.println("agent.getGuid() : " + agent.getGuid());
						aclMsg.addReceiver(receiver);
					}
					myAgent.send(aclMsg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void processPhoto2Word(MicroTask microTask, String phototPath) {

		ACLMessage aclMsg = new ACLMessage(ACLMessage.INFORM);
		aclMsg.setConversationId("delegate");

		Work2ServletMessage w2sMsg = new Work2ServletMessage(
				TaskTypeEnum.PHOTO2WORD, microTask.getTemplate());
		w2sMsg.setTaskid(microTask.getId());
		try {
			File file = new File(phototPath);
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
				System.out.println("file wrong !!!");
			}
			System.out.println("333  文件的长度是 ：  " + offset);
			fi.close();
			w2sMsg.setFileBytes(bufferbytes);
			aclMsg.setContentObject(w2sMsg);

		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<AgentInfo> agentInfos;
		try {
			agentInfos = AgentDBOperation.getOnlineAgents();
			// 这个地方以后应该是要添加一些新的功能，比如agents是依据相关的限制选择出来的，这个里面现在是依据所有的在线的agent发送的。
			for (AgentInfo agent : agentInfos) {
				AID receiver = new AID();
				receiver.setName(agent.getGuid());
				aclMsg.addReceiver(receiver);
			}
			myAgent.send(aclMsg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void processWord2Photo(MicroTask microTask) {
		Work2ServletMessage w2sMsg = new Work2ServletMessage(
				TaskTypeEnum.WORD2PHOTO, microTask.getTemplate());
		w2sMsg.setTaskid(microTask.getId());

		ACLMessage aclMsg = new ACLMessage(ACLMessage.INFORM);
		aclMsg.setConversationId("delegate");
		try {
			aclMsg.setContentObject(w2sMsg);
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<AgentInfo> agentInfos;
		try {
			agentInfos = AgentDBOperation.getOnlineAgents();
			// 这个地方以后应该是要添加一些新的功能，比如agents是依据相关的限制选择出来的，这个里面现在是依据所有的在线的agent发送的。
			for (AgentInfo agent : agentInfos) {
				AID receiver = new AID();
				receiver.setName(agent.getGuid());
				aclMsg.addReceiver(receiver);
			}
			myAgent.send(aclMsg);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
// ======= Test Test
// System.out.println("沉睡10s后要发信息了");
// try {
// Thread.sleep(30 * 1000);
// } catch (InterruptedException e1) {
// // TODO Auto-generated catch block
// e1.printStackTrace();
// }
// Work2ServletMessage w2sMsg = new Work2ServletMessage(
// TaskTypeEnum.WORD2PHOTO, "Just Test");
//
// ACLMessage aclMsg = new ACLMessage(ACLMessage.INFORM);
// aclMsg.setConversationId("delegate");
// try {
// aclMsg.setContentObject(w2sMsg);
// } catch (IOException e) {
// e.printStackTrace();
// }
//
// List<AgentInfo> agentInfos;
// try {
// agentInfos = AgentDBOperation.getOnlineAgents();
// // 这个地方以后应该是要添加一些新的功能，比如agents是依据相关的限制选择出来的，这个里面现在是依据所有的在线的agent发送的。
// for (AgentInfo agent : agentInfos) {
// AID receiver = new AID("Bcd", false);
// receiver.setName(agent.getGuid());
// System.out.println("Agent Info :" + agent.getGuid());
// aclMsg.addReceiver(receiver);
// }
// myAgent.send(aclMsg);
// System.out.println("即将停止！！！");
// // System.exit(0);
// } catch (Exception e) {
// e.printStackTrace();
// }

// ======= Test Test