package edu.fudan.se.agent;

import edu.fudan.se.undergraduate.opration.AgentDBOperation;
import fudan.se.pool.TaskTypeEnum;
import fudan.se.pool.Work2ServletMessage;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class TaskRegisteAndHeartBehaviour extends TickerBehaviour {

	private ServletAgent myAgent = null;

	public TaskRegisteAndHeartBehaviour(ServletAgent a, long period) {
		super(a, period);
		// TODO Auto-generated constructor stub
		myAgent = a;
	}

	@Override
	protected void onTick() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchConversationId("RegisterORHeartBeat"));
		ACLMessage aclMsg = myAgent.receive(mt);
		// System.out.println("接受心跳中！！！");
		if (aclMsg != null) {
			String guid = aclMsg.getSender().getName();
			// System.out.println("zheshi shenlksdjflkas +" + guid);
			try {
				Work2ServletMessage regOrHeart = (Work2ServletMessage) aclMsg
						.getContentObject();
				if (regOrHeart.getType() == TaskTypeEnum.REGISTER) {
					String capacity = regOrHeart.getMess(); // 如果是注册的话，则默认包含的内容是worker的Capacity。
					// System.out.println("服务器这边收到了，为：" +guid +",,,,,,"+
					// capacity);
					AgentDBOperation.register(guid, null, capacity);
					// 原代码中有address字段，但是目前我不知道那个字段有什么特别的用处。所以暂时一致性为null
				}

				if (regOrHeart.getType() == TaskTypeEnum.HEARTBEAT) {
					// System.out.println("xintaio");
					String location = regOrHeart.getMess();// 如果是心跳机制的话，则默认包含的内容是位置location
					AgentDBOperation.updateWorkerInfo(guid, location);// 这个语句内部会自动更新lastActive时间。
					// System.out.println("位置信息"+location);
					// System.out.println("xintaio");
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
