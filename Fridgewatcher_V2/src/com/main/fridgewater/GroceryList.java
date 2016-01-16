package com.main.fridgewater;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.additems.fridgewater.AddGrocery;
import com.additems.fridgewater.FoodItem;
import com.additems.fridgewater.GroceryItem;
import com.example.fridgewater.R;
import com.filemanager.fridgewater.FoodFileReader;
import com.other.fridgewater.ContextPass;

public class GroceryList extends Activity {
	static LinearLayout list;
	static ArrayList<GroceryItem> groceryList;
	ScrollView scrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grocery_list);

		ContextPass.setGroceryContext(this);
		list = (LinearLayout) findViewById(R.id.groceryList);
		ArrayList<String[]> groceryList = FoodFileReader.getGroceryList();
		scrollView = (ScrollView) findViewById(R.id.groceryListLayout);
		scrollView.setBackgroundResource(R.drawable.fridge_image);
		this.groceryList = new ArrayList();
		for (int i = 0; i < groceryList.size(); i++) {
			this.groceryList.add(new GroceryItem(this, list,
					groceryList.get(i)[0],
					Integer.parseInt(groceryList.get(i)[1]), Integer
							.parseInt(groceryList.get(i)[2])));
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.grocery_list, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.clearGrocery: {
			// Declare dialog builder
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			// Set title and message
			dialog.setTitle("Confirm");
			dialog.setMessage("Are you sure you want to remove all items?");
			// Create positive button with text "Yes"
			dialog.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						// If positive button was clicked...
						public void onClick(DialogInterface dialog, int which) {
							// Delete all food items
							for (int i = 0; i < groceryList.size(); i++) {
								if (!groceryList.get(i).isDeleted()) {
									groceryList.get(i).deleteGroceryItem();
								}
							}
							// Create foodList ArrayList
							groceryList = new ArrayList();
							FoodFileReader.clearGroceryList();
						}
					});
			// Create negative button with text "No"
			dialog.setNegativeButton("No", null);
			dialog.show();

			break;
		}
		default: {
			break;
		}
		}

		return true;
	}

	public static ArrayList<GroceryItem> getGroceryList() {
		return groceryList;
	}

	public static LinearLayout getList() {
		return list;
	}

	public void addGroceryClick(View v) {
		Intent addGrocery = new Intent(this, AddGrocery.class);
		startActivity(addGrocery);
	}

	public void backClick(View v) {
		onBackPressed();
	}
}
