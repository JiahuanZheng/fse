package fudan.se.agent;

import java.io.IOException;

import android.location.Location;
import fudan.se.pool.TaskTypeEnum;
import fudan.se.pool.Work2ServletMessage;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class AideHeartBeatBehaviour extends TickerBehaviour {

	private AideAgent myAgent;
	public AideHeartBeatBehaviour(AideAgent a, long period) {
		super(a, period);
		// TODO Auto-generated constructor stub
		myAgent = a;
	}

	@Override
	protected void onTick() {
		// TODO Auto-generated method stub
		ACLMessage aclMsg = new ACLMessage(ACLMessage.INFORM);
		aclMsg.setConversationId("RegisterORHeartBeat");
		Work2ServletMessage wMsg = new Work2ServletMessage(
				TaskTypeEnum.HEARTBEAT, myAgent.getMyLocation().toString());
		aclMsg.addReceiver(myAgent.getServlet());
		try {
			aclMsg.setContentObject(wMsg);
			myAgent.send(aclMsg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
