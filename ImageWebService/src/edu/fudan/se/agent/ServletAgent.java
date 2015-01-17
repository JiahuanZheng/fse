package edu.fudan.se.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import edu.fudan.se.undergraduate.dbObject.AgentInfo;
import edu.fudan.se.undergraduate.dbObject.MicroTask;
import edu.fudan.se.undergraduate.opration.AgentDBOperation;
import edu.fudan.se.undergraduate.opration.MicroTaskOperation;
import edu.fudan.se.undergraduate.opration.ResponseDBOperation;
import fudan.se.pool.TaskTypeEnum;
import fudan.se.pool.Work2ServletMessage;

public class ServletAgent extends Agent {
	private static final long serialVersionUID = 1L;

	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		super.setup();
		registe();
		addBehaviour(new TaskDispatchBehaviour(this, 1000));
		addBehaviour(new TaskReceiveBehaviour(this,1000));
		addBehaviour(new TaskRegisteAndHeartBehaviour(this, 1000));
	}
	public void registe(){
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getLocalName() + new Date().getTime());
		sd.setType("ServletAgent");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
