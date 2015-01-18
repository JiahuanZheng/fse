package chat.client.gui;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
//import java.util.logging.Logger;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import fudan.se.agent.AideAgent;
import fudan.se.agent.CommunicationInterface;
import fudan.se.location.MyLocation;
import fudan.se.location.MyLocationListener;
import fudan.se.pool.TaskTypeEnum;
import fudan.se.pool.Work2ServletMessage;

@SuppressLint("SimpleDateFormat")
public class ResultForServelt extends Activity {
	private String mCurrentPhotoPath;
	private String mCurrentAudioPath;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_TAKE_PHOTO = 2;
	static final int REQUEST_TAKE_AUDIO = 3;

	static final int LOCATION_UPDATE_REQUEST = 0x1000;

//	private Logger log = Logger.getLogger("log");
	private Uri photoUri = null;
	private String capacity = null;

//	private jade.util.Logger logger = jade.util.Logger.getJADELogger(this
//			.getClass().getName());
	private MicroRuntimeServiceBinder microRuntimeServiceBinder;
	private ServiceConnection serviceConnection;
	private AgentController agentController = null;
	private CommunicationInterface comInterface = null;

	private TextView noinfo = null;// 一开始的展示版面
	// WORD2PHOTO
	// private TextView wordToPhoto = null;


	private Button record = null;
	
	private TextView textView = null;

	private Button sendAudio = null;

	// private Button good = null;
	// private Button bad = null;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == TaskTypeEnum.PHOTO2WORD.getVal()) {
					setPhoto2WordView(msg);
			}

			if (msg.what == TaskTypeEnum.WORD2PHOTO.getVal()) {
				setWord2PhotoView(msg);
			}

//			 if (msg.what == LOCATION_UPDATE_REQUEST) {
//				 if(noinfo != null)
//				 noinfo.setText(myLocation.toString());
//			 }
		}
	};
	
	public void setWord2PhotoView(Message msg){
		setContentView(R.layout.word2photo);
		
		TextView w2PshowMessage = (TextView) findViewById(R.id.word2photo_show_message);
		final ImageView mImageView = (ImageView) findViewById(R.id.image);
		Button sendImg = (Button) findViewById(R.id.sendimg);
		Button snap = (Button) findViewById(R.id.snap);
		
		Bundle bundle = msg.getData();
		String showMsg = (String) bundle.getString("message");
		
		final long taskid = bundle.getLong("taskid");
		
		w2PshowMessage.setText(showMsg);
		snap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// File file = new File(mCurrentPhotoPath);
				// if(file.exists()) file.delete();
				// 一旦点击拍照按钮，上一次拍照的照片将被删除（如果有的话）
//				log.info("addr  ");
				dispatchTakePictureIntent();
//				log.info("addr  " + mCurrentPhotoPath);
			}
		});
		sendImg.setOnClickListener(new View.OnClickListener() {// 向后台的agent发送消息，告诉它，需要发送一个图片到serveltAgent
			@Override
			public void onClick(View v) {
				if (mCurrentPhotoPath != null
						&& new File(mCurrentPhotoPath).exists()) {// 路径不等于null，且其路径对应的文件存在
					try {
						Work2ServletMessage sendMsg = new Work2ServletMessage(
								TaskTypeEnum.WORD2PHOTO, mCurrentPhotoPath);
						
						sendMsg.setTaskid(taskid);
						
						agentController.putO2AObject(sendMsg, false);
					} catch (StaleProxyException e) {
						// TODO Auto-generated catch block
						System.out
								.println("when agentController invokes "
										+ "method putO2AObject(), exceptions happens!!!");
						e.printStackTrace();
					}
				} else {
					System.out.println("file not exist");
				}
				mImageView.setImageURI(null);
				setContentView(R.layout.noinformation);
//				View view = View.inflate(ResultForServelt.this, R.layout.noinformation, null);
//				view.invalidate();
			}
		});
		
	}
	
	public void setPhoto2WordView(Message msg) {
		
		
		setContentView(R.layout.photo2word);

		TextView receive_inputs_txt = (TextView) findViewById(R.id.receive_inputs_txt);
		final ImageView receive_p2w_image = (ImageView) findViewById(R.id.receive_p2w_image);
		Button reply_price_btn = (Button) findViewById(R.id.reply_price_btn);
		final EditText reply_price_edit = (EditText) findViewById(R.id.reply_price_edit);

		Bundle bundle = msg.getData();

		String imagePath = (String) bundle.getString("path");

		// -------------------------------------------------
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
		receive_p2w_image.setImageBitmap(bitmap);
		// -------------------------------------------------
		// 上述片段是减少图片占用的内存。

		// Uri uri = Uri.fromFile(new File(imagePath));
		// receive_p2w_image.setImageURI(uri);
		// String showMsg = (String)bundle.getString("message");

		String description = (String) bundle.getString("description");
		List<String> keys = bundle.getStringArrayList("keys");
		List<String> values = bundle.getStringArrayList("values");

		String showMsg = description + "\n";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String val = values.get(i);
			showMsg += "  \nKey" + i + ":" + key + "  \nVal" + i + ":" + val;
		}
		receive_inputs_txt.setText(showMsg);
		final long taskId = bundle.getLong("taskid");

		reply_price_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = reply_price_edit.getText().toString();
				if (content != null) {
					Work2ServletMessage sendMsg = new Work2ServletMessage(
							TaskTypeEnum.PHOTO2WORD, content);
					sendMsg.setTaskid(taskId);
					try {
						agentController.putO2AObject(sendMsg, false);
					} catch (StaleProxyException e) {
						e.printStackTrace();
					}
					receive_p2w_image.setImageURI(null);
					setContentView(R.layout.noinformation);// 将画面变回没有信息的界面。
				} else
					Toast.makeText(ResultForServelt.this,
							"The EditText Content You Input is Wrong !",
							Toast.LENGTH_SHORT);
			}
		});

	}

	

	public void setShow(int b_noinfo, int b_w2PshowMessage, int b_snap,
			int b_record, int b_mImageView, int b_textView, int b_sendImg,
			int b_sendAudio, int replyMsg) {

		// /注意，0可见，4不可见
		noinfo.setVisibility(b_noinfo);
//		w2PshowMessage.setVisibility(b_w2PshowMessage);//===
//		snap.setVisibility(b_snap);//===
		record.setVisibility(b_record);
//		mImageView.setVisibility(b_mImageView);//====
		textView.setVisibility(b_textView);
//		sendImg.setVisibility(b_sendImg);
		sendAudio.setVisibility(b_sendAudio);
		// good.setVisibility(b_good);
		// bad.setVisibility(b_bad);
		// int b_good,int b_bad
	}

	final private MyLocation myLocation = new MyLocation();
	private LocationListener locationListener = new MyLocationListener(myLocation);
	
	private String nickname;
	private RuntimeCallback<AgentController> agentStartupCallback = new RuntimeCallback<AgentController>() {
		@Override
		public void onSuccess(AgentController agent) {
			agentController = agent;
			try {
				comInterface = agent
						.getO2AInterface(CommunicationInterface.class);
				comInterface.setHandler(handler);
				comInterface.setCapacity(capacity);
				comInterface.setCustomLocation(myLocation);
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
			System.out.println("aaa1" + agent);
		}

		@Override
		public void onFailure(Throwable throwable) {
//			logger.log(Level.INFO, "Nickname already in use!");
		}
	};

	private LocationManager locationManager = null;

	private void launchLocationService() {
		locationManager = (LocationManager) ResultForServelt.this
				.getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, locationListener);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		launchLocationService();

		setContentView(R.layout.activity_resultforservelt);
		// buildGoogleApiClient();
		noinfo = (TextView) findViewById(R.id.noinfo2);
		// wordToPhoto = (TextView)findViewById(R.id.word2photo);


		record = (Button) findViewById(R.id.record);
		record.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ResultForServelt.this,
						MyAudioRecord.class);
				startActivityForResult(intent, REQUEST_TAKE_AUDIO);
			}
		});

		textView = (TextView) findViewById(R.id.audio);
		nickname = (String) getIntent().getExtras().get("agentname");
		capacity = (String) getIntent().getExtras().get("capacity");

		SharedPreferences settings = getSharedPreferences("jadeChatPrefsFile",
				0);
		String host = settings.getString("defaultHost", "");
		String port = settings.getString("defaultPort", "");
		
		
		
		startChat(nickname, host, port, agentStartupCallback);
		// 上面这一行是用于开启agent并且将agent controller 的值赋给 agentController字段。
		


		sendAudio = (Button) findViewById(R.id.sendaudio);
		sendAudio.setOnClickListener(new View.OnClickListener() {// 向后台的agent发送消息，告诉它，需要发送一个图片到serveltAgent
					@Override
					public void onClick(View v) {

						if (mCurrentAudioPath != null
								&& new File(mCurrentAudioPath).exists()) {// 路径不等于null，且其路径对应的文件存在
							try {
								Work2ServletMessage sendMsg = new Work2ServletMessage(
										TaskTypeEnum.WORD2AUDIO,
										mCurrentAudioPath);
								agentController.putO2AObject(sendMsg, false);
							} catch (StaleProxyException e) {
								// TODO Auto-generated catch block
								System.out
										.println("when agentController invokes "
												+ "method putO2AObject(), exceptions happens!!!");
								e.printStackTrace();
							}
						} else {
							System.out.println("file not exist");
						}

					}
				});

		setShow(0, 4, 4, 4, 4, 4, 4, 4, 4);// 注意这个方法应该在界面元素实例化时候才能使用，我暂时未加null判断。mark一下。
	}


	public void startChat(final String nickname, final String host,
			final String port,
			final RuntimeCallback<AgentController> agentStartupCallback) {
		final Properties profile = new Properties();
		profile.setProperty(Profile.MAIN_HOST, host);
		profile.setProperty(Profile.MAIN_PORT, port);
		profile.setProperty(Profile.MAIN, Boolean.FALSE.toString());
		profile.setProperty(Profile.JVM, Profile.ANDROID);

		if (AndroidHelper.isEmulator()) {
			profile.setProperty(Profile.LOCAL_HOST, AndroidHelper.LOOPBACK);
		} else {
			profile.setProperty(Profile.LOCAL_HOST,
					AndroidHelper.getLocalIPAddress());
		}
		profile.setProperty(Profile.LOCAL_PORT, "2000");

		if (microRuntimeServiceBinder == null) {
			serviceConnection = new ServiceConnection() {
				public void onServiceConnected(ComponentName className,
						IBinder service) {
					microRuntimeServiceBinder = (MicroRuntimeServiceBinder) service;
//					logger.log(Level.INFO,
//							"Gateway successfully bound to MicroRuntimeService");
					startContainer(nickname, profile, agentStartupCallback);
				};

				public void onServiceDisconnected(ComponentName className) {
					microRuntimeServiceBinder = null;
//					logger.log(Level.INFO,
//							"Gateway unbound from MicroRuntimeService");
				}
			};
//			logger.log(Level.INFO, "Binding Gateway to MicroRuntimeService...");

			bindService(new Intent(getApplicationContext(),
					MicroRuntimeService.class), serviceConnection,
					Context.BIND_AUTO_CREATE);

		} else {
//			logger.log(Level.INFO,
//					"MicroRumtimeGateway already binded to service");
			startContainer(nickname, profile, agentStartupCallback);
		}
	}

	private void startContainer(final String nickname, Properties profile,
			final RuntimeCallback<AgentController> agentStartupCallback) {
		if (!MicroRuntime.isRunning()) {

			RuntimeCallback<Void> rc = new RuntimeCallback<Void>() {
				@Override
				public void onSuccess(Void thisIsNull) {
//					logger.log(Level.INFO,
//							"Successfully start of the container...");
					startAgent(nickname, agentStartupCallback);
				}

				@Override
				public void onFailure(Throwable throwable) {
//					logger.log(Level.SEVERE, "Failed to start the container...");
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
//				logger.log(Level.INFO, "Successfully start of the "
//						+ AideAgent.class.getName() + "...");
				try {
					agentStartupCallback.onSuccess(MicroRuntime
							.getAgent(nickname));

				} catch (ControllerException e) {
					// Should never happen
					agentStartupCallback.onFailure(e);
				}
			}

			@Override
			public void onFailure(Throwable throwable) {
//				logger.log(Level.SEVERE, "Failed to start the "
//						+ AideAgent.class.getName() + "...");
				agentStartupCallback.onFailure(throwable);
			}
		};
		microRuntimeServiceBinder.startAgent(nickname,
				AideAgent.class.getName(),
				new Object[] { getApplicationContext() }, rc);
	}

	private File createImageFile() throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

		// .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			File photoFile = null;
			try {
				System.out.println("你被执行了么！！！");
				photoFile = createImageFile();
				System.out.println("你被执行了么！！！"+photoFile);
			} catch (IOException ex) {
				System.out.println("出错了："+ex);
			}

			if (photoFile != null) {
				System.out.println("我被执行了");
				photoUri = Uri.fromFile(photoFile);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
			System.out.println("mCurrentPhotoPath:" + mCurrentPhotoPath);
			ImageView mImageView = (ImageView) findViewById(R.id.image);
			Button sendImg = (Button) findViewById(R.id.sendimg);
			mImageView.setImageURI(photoUri);
			sendImg.setVisibility(0);// 设置sendImg按钮可见
		}
		if (requestCode == REQUEST_TAKE_AUDIO
				&& resultCode == REQUEST_TAKE_AUDIO) {
			mCurrentAudioPath = (String) data.getExtras().get("audiopath");
			textView.setText("当前音频的位置是:" + mCurrentAudioPath);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unbindService(serviceConnection);

//		logger.log(Level.INFO, "Destroy activity!");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// private GoogleApiClient mGoogleApiClient = null;
	// private Location mLastLocation = null;

	// protected synchronized void buildGoogleApiClient() {
	// mGoogleApiClient = new GoogleApiClient.Builder(this)
	// .addConnectionCallbacks(this)
	// .addOnConnectionFailedListener(this)
	// .addApi(LocationServices.API).build();
	// }
	//
	// @Override
	// public void onConnectionFailed(ConnectionResult arg0) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onConnected(Bundle connectionHint) {
	// // TODO Auto-generated method stub
	// mLastLocation = LocationServices.FusedLocationApi
	// .getLastLocation(mGoogleApiClient);
	// if (mLastLocation != null) {
	// myLocation.setLatAndLon(mLastLocation.getLatitude(),
	// mLastLocation.getLongitude());
	// }
	// }
	//
	// @Override
	// public void onConnectionSuspended(int arg0) {
	// // TODO Auto-generated method stub
	//
	// }

	// @Override
	// protected void onStart() {
	// mGoogleApiClient.connect();
	// }
	//
	// @Override
	// protected void onStop() {
	// mGoogleApiClient.disconnect();
	// super.onStop();
	// }

}

// good.setOnClickListener(new View.OnClickListener()
// {//向后台的agent发送消息，告诉它，需要发送一个图片到serveltAgent
// @Override
// public void onClick(View v) {
// Work2ServletMessage sendMsg = new
// Work2ServletMessage(TaskTypeEnum.PHOTO2WORD,"GOOD");
// try {
// agentController.putO2AObject(sendMsg, false);
// } catch (StaleProxyException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// mImageView.setImageURI(null);
// setShow(0,4,4,4,4,4,4,4,4,4);
// }
// });
//
// bad.setOnClickListener(new View.OnClickListener()
// {//向后台的agent发送消息，告诉它，需要发送一个图片到serveltAgent
// @Override
// public void onClick(View v) {
// Work2ServletMessage sendMsg = new
// Work2ServletMessage(TaskTypeEnum.PHOTO2WORD,"BAD");
// try {
// agentController.putO2AObject(sendMsg, false);
// } catch (StaleProxyException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// mImageView.setImageURI(null);
// setShow(0,4,4,4,4,4,4,4,4,4);
// }
// });

// System.out.println("我是位置："+location);
// updateView(location);
// locationManager.addGpsStatusListener(listener);
// System.out.println("请求位置更新！！！");
// locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
// 1000, 0, locationListener);
// System.out.println("请求位置更新！！222！");
// if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
// {// 这里面的if
// // 暂时不会被使用
// Bundle extras = data.getExtras();
// Bitmap imageBitmap = (Bitmap) extras.get("data");
// mImageView.setImageBitmap(imageBitmap);
// }

//
//new Thread(new Runnable(){
//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//		while(true){
//	   	 Message msg = new Message();
//	   	 msg.what = LOCATION_UPDATE_REQUEST;
//	   	 handler.sendMessage(msg);
//	   	 try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		}
//	}
//}).start();

//private Criteria getCriteria() {
//Criteria criteria = new Criteria();
//// 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
//criteria.setAccuracy(Criteria.ACCURACY_FINE);
//// 设置是否要求速度
//criteria.setSpeedRequired(false);
//// 设置是否允许运营商收费
//criteria.setCostAllowed(false);
//// 设置是否需要方位信息
//criteria.setBearingRequired(false);
//// 设置是否需要海拔信息
//criteria.setAltitudeRequired(false);
//// 设置对电源的需求
//criteria.setPowerRequirement(Criteria.POWER_LOW);
//return criteria;
//}
//
// if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
// {
// Toast.makeText(this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
// // 返回开启GPS导航设置界面
// Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
// startActivityForResult(intent, 0);
// return;
// }
//
// new Thread(new Runnable() {
//
// @Override
// public void run() {
// while (true) {
// Message msg = new Message();
// msg.what = LOCATION_UPDATE_REQUEST;
// handler.sendMessage(msg);
// try {
// Thread.sleep(1000 * 2);
// } catch (InterruptedException e) {
// e.printStackTrace();
// }
// }
// }
// }).start();