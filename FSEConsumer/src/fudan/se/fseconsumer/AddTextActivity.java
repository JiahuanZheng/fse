package fudan.se.fseconsumer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddTextActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_text);
		
		final EditText name = (EditText)findViewById(R.id.name);
		
		final EditText value = (EditText)findViewById(R.id.value);
		
		
		Button backBtn = (Button)findViewById(R.id.back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String nameStr = name.getText().toString();
				String valueStr = value.getText().toString();
				if(!(nameStr.equals("") || valueStr.equals(""))){
					Bundle bundle = new Bundle();
					bundle.putString("name", nameStr);
					bundle.putString("value", valueStr);
					Intent intent = getIntent();
					intent.putExtras(bundle);
					AddTextActivity.this.setResult(1, intent);
					AddTextActivity.this.finish();
				}	
				else Toast.makeText(AddTextActivity.this, "您输入的不符合规范", Toast.LENGTH_SHORT);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_text, menu);
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
