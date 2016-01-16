package com.additems.fridgewater;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.fridgewater.R;

public class ItemInfo extends Activity {
	
	int day,month,year,quantity, completion;
	String name,comments;
	
	EditText nameView, dayView, monthView, yearView, quantityView, commentsView;
	TextView completionUpdate; 
	SeekBar completionBar;
	RelativeLayout mainLayout;
	ScrollView scrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_info);
		
		mainLayout = (RelativeLayout) findViewById(R.id.infoRelativeLayout);
		
		nameView = (EditText) findViewById(R.id.infoItemName);
		dayView = (EditText) findViewById(R.id.infoDay);
		monthView = (EditText) findViewById(R.id.infoMonth);
		yearView = (EditText) findViewById(R.id.infoYear);
		quantityView = (EditText) findViewById(R.id.infoQuantityEdit);
		commentsView = (EditText) findViewById(R.id.infoComment);
		
		completionBar = (SeekBar) findViewById(R.id.infoCompletionEdit);
		
		completionUpdate = (TextView) findViewById(R.id.infoCompletionDisplay);
		
		if(quantity == 1){
			mainLayout.removeView(completionBar);
			mainLayout.removeView(completionUpdate);
		} else {
			completionBar.setMax(quantity);
			completionBar.setProgress(completion);
			completionUpdate.setText("" + completion + "/" + quantity);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.item_info, menu);
		return true;
	}
	
	

}
