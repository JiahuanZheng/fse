package fudan.se.agent;

import jade.android.AndroidHelper;
import jade.android.MicroRuntimeService;
import jade.android.MicroRuntimeServiceBinder;
import jade.android.RuntimeCallback;
import jade.core.MicroRuntime;
import jade.core.Profile;
import jade.util.leap.Properties;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import chat.client.gui.ResultForServelt;

public class AgentLauncher {
	private MicroRuntimeServiceBinder microRuntimeServiceBinder;
//	private RuntimeCallback<AgentController> rc;
	private ResultForServelt rfs;
	
	
	public AgentLauncher(ResultForServelt rfs) {
		// TODO Auto-generated constructor stub
		this.rfs = rfs;
	}
	public void start() {
		// TODO Auto-generated method stub
		final RuntimeCallback<AgentController> agentStartupCallback = new AgentCallback(rfs);
		
		SharedPreferences settings = rfs.getSharedPreferences("jadeChatPrefsFile",
				0);
		String host = settings.getString("defaultHost", "");
		String port = settings.getString("defaultPort", "");
		final Properties profile = new Properties();
		profile.setProperty(Profile.MAIN_HOST, host);
		profile.setProperty(Profile.MAIN_PORT, port);
		profile.setProperty(Profile.MAIN, Boolean.FALSE.toString());
		profile.setProperty(Profile.JVM, Profile.ANDROID);

		String nickname = rfs.getNickname();
		
		if (AndroidHelper.isEmulator()) {
			profile.setProperty(Profile.LOCAL_HOST, AndroidHelper.LOOPBACK);
		} else {
			profile.setProperty(Profile.LOCAL_HOST,
					AndroidHelper.getLocalIPAddress());
		}
		profile.setProperty(Profile.LOCAL_PORT, "2000");
		
	
//		final RuntimeCallback<AgentController> agentStartupCallback = rc;
		ServiceConnection serviceConnection  = null;
		final ResultForServelt rfs2 = rfs;
		if (microRuntimeServiceBinder == null) {
			
			serviceConnection = new ServiceConnection() {
				public void onServiceConnected(ComponentName className,
						IBinder service) {
					microRuntimeServiceBinder = (MicroRuntimeServiceBinder) service;
					startContainer(rfs2.getNickname(), profile, agentStartupCallback);
				};

				public void onServiceDisconnected(ComponentName className) {
					microRuntimeServiceBinder = null;
				}
			};
			rfs.setServiceConnection(serviceConnection);

			rfs.bindService(new Intent(rfs.getApplicationContext(),
					MicroRuntimeService.class), serviceConnection,
					Context.BIND_AUTO_CREATE);

		} else {
			startContainer(nickname, profile, agentStartupCallback);
		}
		
	}
	private void startContainer(final String nickname, Properties profile,
			final RuntimeCallback<AgentController> agentStartupCallback) {
		if (!MicroRuntime.isRunning()) {

			RuntimeCallback<Void> rc = new RuntimeCallback<Void>() {
				@Override
				public void onSuccess(Void thisIsNull) {
					startAgent(nickname, agentStartupCallback);
				}

				@Override
				public void onFailure(Throwable throwable) {
					throwable.printStackTrace();
				}
			};

			microRuntimeServiceBinder.startAgentContainer(profile, rc);
		} else {
			startAgent(nickname, agentStartupCallback);
		}
	}
	private void startAgent(final String nickname,
			final RuntimeCallback<AgentController> agentStartupCallback) {

		RuntimeCallback<Void> rc = new RuntimeCallback<Void>() {
			@Override
			public void onSuccess(Void thisIsNull) {
				try {
					agentStartupCallback.onSuccess(MicroRuntime
							.getAgent(nickname));

				} catch (ControllerException e) {
					agentStartupCallback.onFailure(e);
				}
			}

			@Override
			public void onFailure(Throwable throwable) {
				agentStartupCallback.onFailure(throwable);
			}
		};
		microRuntimeServiceBinder.startAgent(nickname,
				AideAgent.class.getName(),
				new Object[] { rfs.getApplicationContext() }, rc);
	}
	
}

class AgentCallback extends RuntimeCallback<AgentController>{
	
	private ResultForServelt rfs = null;
	
	public AgentCallback(ResultForServelt rfs) {
		this.rfs = rfs;
	}
	
	@Override
	public void onSuccess(AgentController agent) {
		rfs.setAgentController(agent);
		CommunicationInterface comInterface = null;
		try {
			comInterface = agent
					.getO2AInterface(CommunicationInterface.class);
			comInterface.setHandler(rfs.getHandler());
			comInterface.setCapacity(rfs.getCapacity());
			comInterface.setCustomLocation(rfs.getMyLocation());
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		System.out.println("aaa1" + agent);
	}

	@Override
	public void onFailure(Throwable throwable) {
	}
}
 