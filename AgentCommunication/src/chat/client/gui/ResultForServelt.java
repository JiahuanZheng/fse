package chat.client.gui;

import jade.wrapper.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.dom4j.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.*;
import android.graphics.*;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import fudan.se.agent.AgentLauncher;
import fudan.se.location.*;
import fudan.se.pool.*;

@SuppressLint("SimpleDateFormat")
public class ResultForServelt extends Activity {
	final private MyLocation myLocation = new MyLocation();
	private String mCurrentPhotoPath;
	private String mCurrentAudioPath;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_TAKE_PHOTO = 2;
	static final int REQUEST_TAKE_AUDIO = 3;

	static final int LOCATION_UPDATE_REQUEST = 0x1000;
	private Uri photoUri = null;
	private String capacity = null;

	private List<EditText> edits = new ArrayList<EditText>();//将动态生成的代码中需要取回值得View存起来.
	private HashMap<String,String> mediaMap = new HashMap<String,String>(); 
	private String dataXML = null;//注意，这个是界面生成以及结果返回必须的标记位。
	
	private ImageView mImageView = null;
	
	public String getCapacity() {
		return capacity;
	}

	private ServiceConnection serviceConnection;

	public ServiceConnection getServiceConnection() {
		return serviceConnection;
	}

	public void setServiceConnection(ServiceConnection serviceConnection) {
		this.serviceConnection = serviceConnection;
	}

	private AgentController agentController = null;

	public void setAgentController(AgentController agentController) {
		this.agentController = agentController;
	}

//	private Button record = null;
//	private Button sendAudio = null;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				View view = getMyView(msg);
				setContentView(view);
			}
		}
	};

	public Handler getHandler() {
		return handler;
	}
	
	public void setWord2PhotoView(Message msg) {
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
				dispatchTakePictureIntent();
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
			}
		});

	}

	
	@SuppressWarnings("unchecked")
	public View getMyView(Message msg) {
		
		Bundle bundle = msg.getData();
		dataXML = (String) bundle.getString("message");
		final long taskid = bundle.getLong("taskid");
		
		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		
		LinearLayout mainLayout = new LinearLayout(this);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.setLayoutParams(layoutParams);
		try {
			Document doc = DocumentHelper.parseText(dataXML);
			
			List<Element> eles = doc.selectNodes("/UIdisplay/Description");
			///UIdisplay/Description选出来的节点只是显示，所以是不需要记录的
			TextView description = new TextView(this);
			description.setLayoutParams(layoutParams);
			description.setText(eles.get(0).getText());
			mainLayout.addView(description);
			
			
			List<Element> eles2 = doc.selectNodes("/UIdisplay/TextDisplay");
			String sum = "";
			if(eles2 != null && eles2.size() > 0)
			for(Element ele : eles2){
				Element e0 = ele.element("Key");
			    Element e1 = ele.element("Value");
			    sum += "\nKey:" + e0.getText() + "\nValue:" + e1.getText();
			}
			TextView textDisplay = new TextView(this);
			textDisplay.setLayoutParams(layoutParams);
			textDisplay.setText(sum);
			mainLayout.addView(textDisplay);
			
			List<Element> eles3 = doc.selectNodes("/UIdisplay/DisplayImage");
			if(eles3 != null && eles3.size() > 0){
				Element ele = eles3.get(0);
				String path = ele.element("Value").getText();
				File file = new File(path);
				ImageView imageView= new ImageView(this);
				imageView.setLayoutParams(layoutParams);
				imageView.setImageURI(Uri.fromFile(file));
				mainLayout.addView(imageView);
			}
			
			List<Element> eles4 = doc.selectNodes("/UIdisplay/TextInput");
			if(eles4 != null && eles4.size() > 0)
			for(Element ele : eles4){
				String key = ele.element("Key").getText();
				TextView inputKey = new TextView(this);
				inputKey.setLayoutParams(layoutParams);
				inputKey.setText(key);
				mainLayout.addView(inputKey);
				
				String value = ele.element("Value").getText();
				EditText inputValue = new EditText(this);
				inputValue.setLayoutParams(layoutParams);
				mainLayout.addView(inputValue);
				
				edits.add(inputValue);
				//If you need collect the result of user input,
				//you need use a global variable to store it.
			}
			
			List<Element> eles5 = doc.selectNodes("/UIdisplay/TakeImage");
			if(eles5 != null && eles5.size() > 0){
				String key = eles5.get(0).element("Key").getText();
				TextView inputKey = new TextView(this);
				inputKey.setLayoutParams(layoutParams);
				inputKey.setText(key);
				mainLayout.addView(inputKey);
				
				Button snap = new Button(this);
				snap.setLayoutParams(layoutParams);
				snap.setText("SNAP");
				snap.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dispatchTakePictureIntent();
					}
				});
				mainLayout.addView(snap);
			}
			
			Button submit = new Button(this);
			submit.setLayoutParams(layoutParams);
			submit.setText("Finish");
			submit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Document doc;
					try {
						doc = DocumentHelper.parseText(dataXML);
						for(int i = 0; i < edits.size();i++){
							EditText edit = edits.get(i);
							String s = edit.getText().toString();
							List<Element> eles = doc.selectNodes("/UIdisplay/TextInput");
							eles.get(i).element("Value").setText(s);
						}
						
						List<Element> eles = doc.selectNodes("/UIdisplay/TakeImage");
						if(eles != null && eles.size() > 0){
							eles.get(0).element("Value").setText(mediaMap.get("photo"));
						}
						
						mediaMap.clear();
						edits.clear();
						
						Work2ServletMessage sendMsg = new Work2ServletMessage(taskid, doc.asXML());
						System.out.println("hehehhehehe"+doc.asXML());
						agentController.putO2AObject(sendMsg, false);
						
						setContentView(R.layout.activity_resultforservelt);
						
					} catch (DocumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (StaleProxyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			mainLayout.addView(submit);
			ScrollView scrollView = new ScrollView(this);
			scrollView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 
					LayoutParams.MATCH_PARENT));
			scrollView.addView(mainLayout);
			return scrollView;
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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

	public MyLocation getMyLocation() {
		return myLocation;
	}

	private String nickname;

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_resultforservelt);
		nickname = (String) getIntent().getExtras().get("agentname");
		capacity = (String) getIntent().getExtras().get("capacity");
		LocationLauncher.launchLocationService(this, myLocation);

		new AgentLauncher(this).start();
	}

	private File createImageFile() throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

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
				System.out.println("你被执行了么！！！" + photoFile);
			} catch (IOException ex) {
				System.out.println("出错了：" + ex);
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
			mImageView.setImageURI(photoUri);
			mediaMap.put("image", mCurrentPhotoPath);
		}
		if (requestCode == REQUEST_TAKE_AUDIO
				&& resultCode == REQUEST_TAKE_AUDIO) {
			mCurrentAudioPath = (String) data.getExtras().get("audiopath");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection);
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
	
}
// public void setShow(int b_noinfo, int b_w2PshowMessage, int b_snap,
// int b_record, int b_mImageView, int b_textView, int b_sendImg,
// int b_sendAudio, int replyMsg) {
//
// // /注意，0可见，4不可见
// noinfo.setVisibility(b_noinfo);
// // w2PshowMessage.setVisibility(b_w2PshowMessage);//===
// // snap.setVisibility(b_snap);//===
// record.setVisibility(b_record);
// // mImageView.setVisibility(b_mImageView);//====
// textView.setVisibility(b_textView);
// // sendImg.setVisibility(b_sendImg);
// sendAudio.setVisibility(b_sendAudio);
// // good.setVisibility(b_good);
// // bad.setVisibility(b_bad);
// // int b_good,int b_bad
// }
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
// new Thread(new Runnable(){
// @Override
// public void run() {
// // TODO Auto-generated method stub
// while(true){
// Message msg = new Message();
// msg.what = LOCATION_UPDATE_REQUEST;
// handler.sendMessage(msg);
// try {
// Thread.sleep(1000);
// } catch (InterruptedException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// }
// }
// }).start();

// private Criteria getCriteria() {
// Criteria criteria = new Criteria();
// // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
// criteria.setAccuracy(Criteria.ACCURACY_FINE);
// // 设置是否要求速度
// criteria.setSpeedRequired(false);
// // 设置是否允许运营商收费
// criteria.setCostAllowed(false);
// // 设置是否需要方位信息
// criteria.setBearingRequired(false);
// // 设置是否需要海拔信息
// criteria.setAltitudeRequired(false);
// // 设置对电源的需求
// criteria.setPowerRequirement(Criteria.POWER_LOW);
// return criteria;
// }
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