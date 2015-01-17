/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
 *****************************************************************/

package chat.client.gui;

import jade.util.Logger;

import java.util.logging.Level;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * This activity implement the main interface.
 * 
 * @author Michele Izzo - Telecomitalia
 */

public class MainActivity extends Activity {

	private Logger logger = Logger.getJADELogger(this.getClass().getName());

//	private MicroRuntimeServiceBinder microRuntimeServiceBinder;
//	private ServiceConnection serviceConnection;
//
//	private AgentController agentController = null;
	
	static final int CHAT_REQUEST = 0;
	static final int SETTINGS_REQUEST = 1;

	private String nickname;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_zjh);
		Button button = (Button) findViewById(R.id.button_chat);
		button.setOnClickListener(buttonChatListener);

	}

	private static boolean checkName(String name) {
		if (name == null || name.trim().equals("")) {
			return false;
		}
		return true;
	}

	private OnClickListener buttonChatListener = new OnClickListener() {
		public void onClick(View v) {
			final EditText nameField = (EditText) findViewById(R.id.edit_nickname);
			final EditText capacity = (EditText)findViewById(R.id.edit_capacity);
			nickname = nameField.getText().toString();
			if (!checkName(nickname)) {
				logger.log(Level.INFO, "Invalid nickname!");
			} else {
				try {
					Intent intent = new Intent(MainActivity.this,
							ResultForServelt.class);
					intent.putExtra("agentname", nickname);
					intent.putExtra("capacity", capacity.getText().toString());
					startActivity(intent);
				} catch (Exception ex) {
					System.out.println(ex.toString()
							+ "i want to konwekrwlkejr");
					logger.log(Level.SEVERE,
							"Unexpected exception creating chat agent!");
				}
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent showSettings = new Intent(MainActivity.this,
					SettingsActivity.class);
			MainActivity.this.startActivityForResult(showSettings,
					SETTINGS_REQUEST);
			return true;
		case R.id.menu_exit:
			finish();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (requestCode == CHAT_REQUEST) {
//			if (resultCode == RESULT_CANCELED) {
//				RuntimeCallback<Void> rc = new RuntimeCallback<Void>() {
//					@Override
//					public void onSuccess(Void thisIsNull) {
//					}
//
//					@Override
//					public void onFailure(Throwable throwable) {
//						logger.log(Level.SEVERE, "Failed to stop the "
//								+ AideAgent.class.getName() + "...");
//						agentStartupCallback.onFailure(throwable);
//					}
//				};
//				logger.log(Level.INFO, "Stopping Jade...");
//				microRuntimeServiceBinder.stopAgentContainer(rc);
//			}
//		}
//	}
	
}
//@Override
//protected void onDestroy() {
//	super.onDestroy();
//
//	unbindService(serviceConnection);
//
//	logger.log(Level.INFO, "Destroy activity!");
//}