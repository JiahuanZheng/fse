package fudan.se.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import java.io.IOException;

import android.os.Handler;
import fudan.se.pool.TaskTypeEnum;
import fudan.se.pool.Work2ServletMessage;

public class AideAgent extends Agent implements CommunicationInterface {

	private static final long serialVersionUID = 1L;
	private Handler handler;
	private String capacity = null;
	private AID servlet = new AID("servlet", false);
	private AID servletAID = null;// 利用agent是一个线程的优势来做这件事情。
	private MyLocation myLocation = new MyLocation();

	public AideAgent() {
		super();
		setEnabledO2ACommunication(true, 0);
		registerO2AInterface(CommunicationInterface.class, this);
	}
	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		// super.setup();
		searchTaskAgent();
		// =================================上方代码注意，必须在servletangent启动之后才能运行。
		register();
		addBehaviour(new AideReceiveDelegatedBehaviour(this, 1000));
		addBehaviour(new AideReplyDelegatedBehaviour(this, 1000));
		addBehaviour(new AideHeartBeatBehaviour(this, 1000));
	}
	
	public void searchTaskAgent(){
		DFAgentDescription dfd2 = new DFAgentDescription();
		ServiceDescription sd2 = new ServiceDescription();
		sd2.setType("ServletAgent");
		dfd2.addServices(sd2);
		try {
			DFAgentDescription[] result = DFService.search(this, dfd2);
			if (result != null && result.length > 0)
				servlet = result[0].getName();
			System.out.println(result);
		} catch (FIPAException e1) {
			e1.printStackTrace();
		}
	}
	
	public void register(){
		ACLMessage aclMsg = new ACLMessage(ACLMessage.INFORM);
		aclMsg.setConversationId("RegisterORHeartBeat");
		Work2ServletMessage wMsg = new Work2ServletMessage(
				TaskTypeEnum.REGISTER, capacity);
		aclMsg.addReceiver(servlet);
		try {
			System.out.println("要发送注册信息了。");
			aclMsg.setContentObject(wMsg);
			send(aclMsg);
			System.out.println("发送完毕");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Handler getHandler() {
		return handler;
	}
	public AID getServlet() {
		return servlet;
	}
	public AID getServletAID() {
		return servletAID;
	}
	public void setServletAID(AID servletAID) {
		this.servletAID = servletAID;
	}
	@Override
	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}
	@Override
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	@Override
	protected void takeDown() {
		// try {
		// DFService.deregister(this);
		// } catch (FIPAException fe) {
		// fe.printStackTrace();
		// }
	}
	public MyLocation getMyLocation() {
		return myLocation;
	}

	@Override
	public void setCustomLocation(MyLocation myLocation) {
		this.myLocation = myLocation;
	}
}

// =================================上方这是一个模块，注册用。

// 个人认为 没有必要注册worker到DF黄页上面
// DFAgentDescription dfd = new DFAgentDescription();
// dfd.setName(getAID());
// ServiceDescription sd = new ServiceDescription();
// sd.setName(getLocalName() + new Date().getTime());
// sd.setType("Worker" + new Date().getTime());
// dfd.addServices(sd);
//
// getAID().setLocalName("Worker" + new Date().getTime());
// try {
// DFService.register(this, dfd);
// } catch (FIPAException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }

// DFAgentDescription tmp = new
// DFAgentDescription();
// ServiceDescription sd = new ServiceDescription();
// sd.setType("ServletAgent");
// tmp.addServices(sd);
// DFAgentDescription[] result = DFService.search(
// myAgent, tmp);
// if(result.length > 0){
// System.out.println("servlet agent name" +
// result[0].getName());
// aclMsg.addReceiver(result[0].getName());
// send(aclMsg);//================================================
// }else
// System.out.println("没有找到对应的servlet！！！");
