package fudan.se.fseconsumer;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import fudan.se.fseconsumer.R;

public class ImageUploadActivity extends Activity {

	private Uri photoUri = null;
	private String mCurrentPhotoPath = null;
	final static int REQUEST_ADD_TEXT = 1;
	final static int REQUEST_ADD_IMG = 2;
	private ImageView mImageView = null;

	List<UploadInfo> infos = new ArrayList<UploadInfo>();
	Handler handler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final Button addText = (Button) findViewById(R.id.add_text);
		final Button addImg = (Button) findViewById(R.id.add_img);
		final Button upload = (Button) findViewById(R.id.upload);
		final TextView show_result = (TextView)findViewById(R.id.show_result);
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 1){
					addText.setEnabled(false); 
					addImg.setEnabled(false);
					upload.setEnabled(false);
				}
				if(msg.what == 2){
					Bundle bundle = msg.getData();
					String result = bundle.getString("result");
					if(result != null){
					show_result.setText(result);
					}else System.out.println("是空值.....");
				}
			}
		};
		
		Button display = (Button) findViewById(R.id.display);
		display.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Intent intent = new Intent(ImageUploadActivity.this,
				// PreviewActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putSerializable("infos", (Serializable) infos);
				// intent.putExtras(bundle);
				// startActivity(intent);
				Toast.makeText(ImageUploadActivity.this, "此功能留待以后扩展",
						Toast.LENGTH_SHORT);
			}
		});

		mImageView = (ImageView) findViewById(R.id.image);

		upload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						Message msg = new Message();
						msg.what = 1;
						handler.sendMessage(msg);
						System.out.println("准备调用webservice" + new Date());
						String result = WSUploadImg.uploadImg(infos);
						System.out.println("webservice调用完毕" + new Date());
						Message msg2 = new Message();
						msg2.what = 2;
						Bundle bundle = new Bundle();
						bundle.putString("result", result);
						msg2.setData(bundle);
						handler.sendMessage(msg2);
					}
				}).start();
			}
		});

		addText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ImageUploadActivity.this,
						AddTextActivity.class);
				startActivityForResult(intent, REQUEST_ADD_TEXT);
			}
		});

		addImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dispatchTakePictureIntent();
			}
		});

	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			System.out.println("hahah898");
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				System.out.println("发生异常" + ex.getMessage());
			}

			if (photoFile != null) {
				System.out.println("hahah");
				photoUri = Uri.fromFile(photoFile);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
				startActivityForResult(takePictureIntent, REQUEST_ADD_IMG);
			}
		}
	}

	@SuppressLint("SimpleDateFormat")
	private File createImageFile() throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		System.out.println(storageDir.getAbsolutePath() + "绝对路径");
		// .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);
		mCurrentPhotoPath = image.getAbsolutePath();
		System.out.println("这个地方被晕次那个了？？？" + mCurrentPhotoPath);
		return image;
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
		if (requestCode == REQUEST_ADD_IMG && resultCode == RESULT_OK) {
			System.out.println("mCurrentPhotoPath:" + mCurrentPhotoPath);
			mImageView.setImageURI(photoUri);
			for (UploadInfo info : infos) {
				if (info.getType() == UploadType.IMG) {
					infos.remove(info);
					break;
				}
			}
			UploadInfo uploadInfo = new UploadInfo(UploadType.IMG, "Object",
					mCurrentPhotoPath);
			infos.add(uploadInfo);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
