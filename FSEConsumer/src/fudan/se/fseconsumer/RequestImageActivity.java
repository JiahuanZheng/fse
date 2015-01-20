package fudan.se.fseconsumer;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class RequestImageActivity extends Activity {
	final static int REQUEST_ADD_TEXT = 1;
	final static int HANDLE_RECEIVE_IMAGE = 2;
	final static int SET_VISIBLE = 3;
	private List<UploadInfo> infos = new ArrayList<UploadInfo>();

	private ImageView imageView = null;
	Button requestButton ;
	
	private final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == HANDLE_RECEIVE_IMAGE) {
				Bundle bundle = msg.getData();
				String path = bundle.getString("path");
				File file = new File(path);
				if(file.exists())
				imageView.setImageURI(Uri.fromFile(file));
				else {
					System.out.println("不存在path"+path);
				}
			}
			if(msg.what == SET_VISIBLE){
				requestButton.setEnabled(false);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request_image);
		Button add_text = (Button) findViewById(R.id.add_text);
		add_text.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(RequestImageActivity.this,
						AddTextActivity.class);
				startActivityForResult(intent, REQUEST_ADD_TEXT);
			}
		});

		imageView = (ImageView) findViewById(R.id.receive_pic);

		requestButton = (Button) findViewById(R.id.request_image);
		requestButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Message msg = new Message();
						msg.what = SET_VISIBLE;
						handler.sendMessage(msg);
						processRequestPic();
					}
				}).start();
			}
		});

	}
	
	
	public void processRequestPic(){
		String inputs = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<UIdisplay xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ " xsi:noNamespaceSchemaLocation=\"fsestandard.xsd\">"
				+"<Description>Help Me Take a Photo</Description>";
		int length = infos.size();
		for (int i = 0; i < length; i++) {
			UploadInfo info = infos.get(i);
			if (info.getType() == UploadType.TEXT) {
				inputs += "<TextDisplay><Key>"+ info.getName() +"</Key><Value>"+ info.getValue() +"</Value></TextDisplay>";
			}
		}
		inputs += "<TakeImage><Key></Key><Value></Value></TakeImage></UIdisplay>";
		
		int port = 9999;
		String url = "http://10.131.253.172:" + port + "/bimg";
		String ns = "http://abc.se.fudan.edu/";
		HttpTransportSE httpTranstation = new HttpTransportSE(
				url, 60000);

		SoapObject soapObject = new SoapObject(ns, "backImg");
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		soapObject.addProperty("arg0", inputs);
		System.out.println("good current2");
		envelope.dotNet = false;
		envelope.bodyOut = soapObject;
		System.out.println("good current3");
		try {
			httpTranstation.call(null, envelope);
			System.out.println("good current4");

			if (envelope.getResponse() != null) {
				SoapObject obj = (SoapObject) envelope.bodyIn;
//				System.out.println("consumer返回的结果是" + obj.getPropertyAsString(0).toString());
				
				String attachment = (String)obj.getPropertyAsString(0).toString();
				
//				System.out.println("para1 : " + attachment);
				Message msg = new Message();
				msg.what = HANDLE_RECEIVE_IMAGE;
				String timeStamp = new SimpleDateFormat(
						"yyyyMMdd_HHmmss").format(new Date());
				String imageFileName = "JPEG_" + timeStamp
						+ "_";
				File storageDir = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
				System.out.println(storageDir.getAbsolutePath()
						+ "绝对路径");
				// .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
				File image = File.createTempFile(imageFileName, /* prefix */
						".jpg", /* suffix */
						storageDir /* directory */
				);
//				System.out.println("locate  :  " + Base64.class.getProtectionDomain().getCodeSource().getLocation());
				byte[] buffer = Base64.decode(attachment, Base64.DEFAULT);
				
				byte2Image(buffer, image.getAbsolutePath());
				
				System.out.println("绝对路径:  "+image.getAbsolutePath());
				
				Bundle bundle = new Bundle();
				bundle.putString("path", image.getAbsolutePath());
				msg.setData(bundle);
				handler.sendMessage(msg);
			}

		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	

	public String byte2Image(byte[] data, String path) {
		if (data.length < 3 || path.equals("")) {
			System.out.println("taiduanle");
			return null;
		}
		try {
			File ftmp = new File(path);
			FileOutputStream imageOutput = new FileOutputStream(ftmp);
			imageOutput.write(data, 0, data.length);
			imageOutput.close();
			System.out.println("Make Picture success,Please find image in "
					+ path);
			return ftmp.getAbsolutePath();
		} catch (Exception ex) {
			System.out.println("Exception: " + ex);
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_ADD_TEXT && resultCode == REQUEST_ADD_TEXT) {
			Bundle bundle = data.getExtras();
			UploadInfo uploadInfo = new UploadInfo(UploadType.TEXT,
					bundle.getString("name"), bundle.getString("value"));
			infos.add(uploadInfo);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.request_image, menu);
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
