package com.additems.fridgewater;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.fridgewater.R;
import com.filemanager.fridgewater.FoodFileReader;

public class GroceryItem extends LinearLayout{
	String name;
	int quantity;
	int completion;
	Context temp;
	LinearLayout parent;
	View view;
	
	ImageButton removeButton;
	TextView nameText, quantityText, completionText;
	SeekBar completionBar;
	
	boolean deleted;
	
	int startCompletion;
	int currentCompletion;
	
	
	public GroceryItem(Context context, final LinearLayout parent,
			final String name, final int quantity, int completion) {
		super(context);
		
		LayoutInflater inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.grocery_list, this, true);
		
		temp = context;
		this.name = name;
		this.quantity = quantity;
		currentCompletion = completion;
		this.parent = parent;
		deleted = false;
		
		
		removeButton = (ImageButton) findViewById(R.id.deleteGrocery);
		nameText = (TextView) findViewById(R.id.groceryName);
		quantityText = (TextView) findViewById(R.id.numberOfGroceries);
		completionText = (TextView) findViewById(R.id.groceryCompletionText);
		completionBar = (SeekBar) findViewById(R.id.groceryCompletionBar);
		
		
		removeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(temp);
				dialog.setTitle("Confirm");
				dialog.setMessage("Remove this item??");
				// Create positive button with text "Yes"
				dialog.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							// If positive button was clicked...
							public void onClick(DialogInterface dialog,
									int which) {
								deleteGroceryItem();
							}
						});
				// Create negative button with text "No"
				dialog.setNegativeButton("No", null);
				dialog.show();
			}
		});
		
		nameText.setText(name);
		quantityText.setText("Quantity of \"" + name + "\" wanted: " + quantity);
		if(quantity == 1){
			completionText.setVisibility(View.INVISIBLE);
			completionBar.setVisibility(View.INVISIBLE);
		} else {
			completionBar.setMax(quantity);
			completionBar.setProgress(completion);
			completionText.setText("" + completion + "/" + quantity);
			
			completionBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					currentCompletion = completionBar.getProgress();
					completionText.setText("" + currentCompletion + "/" + quantity);
					if(currentCompletion == quantity){
						AlertDialog.Builder dialog = new AlertDialog.Builder(temp);
						dialog.setTitle("Confirm");
						dialog.setMessage("Remove this item??");
						// Create positive button with text "Yes"
						dialog.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									// If positive button was clicked...
									public void onClick(DialogInterface dialog,
											int which) {
										deleteGroceryItem();

									}
								});
						// Create negative button with text "No"
						dialog.setNegativeButton("No", null);
						dialog.show();
					}
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					startCompletion = currentCompletion;
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					String[] data = {name, "" + quantity, "" + startCompletion};
					FoodFileReader.updateGroceryItem(data, currentCompletion);
				}
			});
		}
		parent.addView(this);
	
	}
	
	public boolean isDeleted(){
		return deleted;
	}
	public void deleteGroceryItem(){
		deleted = true;
		parent.removeView(this);
		String[] data = {name, "" + quantity, "" + currentCompletion};
		FoodFileReader.deleteGroceryItem(data);
	}
	
}
