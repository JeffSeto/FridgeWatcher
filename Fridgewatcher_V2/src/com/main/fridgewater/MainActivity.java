package com.main.fridgewater;

import java.util.ArrayList;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.ToggleButton;

import com.additems.fridgewater.AddFood;
import com.additems.fridgewater.FoodItem;
import com.example.fridgewater.R;
import com.example.fridgewater.R.drawable;
import com.example.fridgewater.R.id;
import com.filemanager.fridgewater.FoodFileReader;
import com.other.fridgewater.ContextPass;
import com.other.fridgewater.MyReceiver;

public class MainActivity extends Activity {
	public static LinearLayout list;
	public static ArrayList<FoodItem> foodList;
	ScrollView scrollView;
	Intent addFoodPage;
	
	boolean scanned;

	static int notificationCounter = 0;
	Display display;
	Button scanButton, addFoodButton;
	ToggleButton purple, green, red, blue, yellow, orange, expiring, expired;
	static boolean purpleDisabled, greenDisabled, redDisabled, blueDisabled,
			yellowDisabled, orangeDisabled, expiringDisabled, expiredDisabled;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		System.out.println("Start");
		ContextPass.setMainContext(this);		
		// Create ArrayList
		foodList = new ArrayList();
		
		scanned = false;

		// Declare buttons
		scanButton = (Button) findViewById(id.scan);
		addFoodButton = (Button) findViewById(id.addFood);

		// Declare toggle buttons
		purple = (ToggleButton) findViewById(id.purpleToggle);
		green = (ToggleButton) findViewById(id.greenToggle);
		red = (ToggleButton) findViewById(id.redToggle);
		blue = (ToggleButton) findViewById(id.blueToggle);
		yellow = (ToggleButton) findViewById(id.yellowToggle);
		orange = (ToggleButton) findViewById(id.orangeToggle);
		expiring = (ToggleButton) findViewById(id.expiringToggle);
		expired = (ToggleButton) findViewById(id.expiredToggle);

		// Set all toggle buttons to true
		purple.setChecked(false);
		green.setChecked(false);
		yellow.setChecked(false);
		red.setChecked(false);
		orange.setChecked(false);
		blue.setChecked(false);
		expiring.setChecked(false);
		expired.setChecked(false);

		// Set all toggle buttons to display white (on) text
		purple.setTextColor(Color.WHITE);
		green.setTextColor(Color.WHITE);
		red.setTextColor(Color.WHITE);
		orange.setTextColor(Color.WHITE);
		blue.setTextColor(Color.WHITE);
		yellow.setTextColor(Color.WHITE);
		expiring.setTextColor(Color.WHITE);
		expired.setTextColor(Color.WHITE);
		
		//Set the text size of the toggle buttons
		purple.setTextSize((float) 30);
		green.setTextSize((float) 30);
		red.setTextSize((float) 30);
		orange.setTextSize((float) 30);
		blue.setTextSize((float) 30);
		yellow.setTextSize((float) 30);
		expiring.setTextSize((float)30);
		expired.setTextSize((float)30);

		// Set toggle button colours
		purple.setBackgroundColor(Color.parseColor(FoodItem.PURPLE_CODE));
		green.setBackgroundColor(Color.parseColor(FoodItem.GREEN_CODE));
		red.setBackgroundColor(Color.parseColor(FoodItem.RED_CODE));
		blue.setBackgroundColor(Color.parseColor(FoodItem.BLUE_CODE));
		yellow.setBackgroundColor(Color.parseColor(FoodItem.YELLOW_CODE));
		orange.setBackgroundColor(Color.parseColor(FoodItem.ORANGE_CODE));

		// Declare LinearLayout
		list = (LinearLayout) findViewById(id.foodList);
		// Declare ScrollView
		scrollView = (ScrollView) findViewById(R.id.foodListLayout);
		// Set background image of ScrollView as the fridge_image
		scrollView.setBackgroundResource(drawable.fridge_image);
		//scrollView.setBackgroundColor(Color.GRAY);

		// Get ArrayList of data saved on text file
		ArrayList<String[]> list = FoodFileReader.getFoodList();
		for(int i = 0; i < list.size(); i++){
			System.out.println(list.get(i)[0]);
		}
		// Declare alarm manager
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		// Create a temporary intent
		Intent tempIntent = new Intent(this, MyReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				tempIntent, 0);
		
		DateTimeZone timeZone = DateTimeZone.forTimeZone(TimeZone.getDefault());
		// Get the current date
		DateTime today = new DateTime(timeZone);
		//Get the midnight time of day
		DateTime alarmStart = today.plusDays(1).withTimeAtStartOfDay();

		// Set a repeating "alarm" that updates the FoodList
		alarm.setRepeating(AlarmManager.RTC, alarmStart.getMillis(),
				AlarmManager.INTERVAL_DAY, pendingIntent);
		// Generate list of food using data from saved text file
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i)[3].equals("Yes")){
				// Create FoodItem
				foodList.add(new FoodItem(this, this.list, list.get(i)[0], list
						.get(i)[1], Integer.parseInt(list.get(i)[2]), true, 1, Integer.parseInt(list.get(i)[5])));
			} else {
				// Create FoodItem
				foodList.add(new FoodItem(this, this.list, list.get(i)[0], list
						.get(i)[1], Integer.parseInt(list.get(i)[2]), false, Integer.parseInt(list.get(i)[4]), Integer.parseInt(list.get(i)[5])));
			}
			
		}
		// Create addFoodPage intent
		addFoodPage = new Intent(this, AddFood.class);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.clearAll: {
			// Declare dialog builder
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			System.out.println("Test");
			// Set title and message
			dialog.setTitle("Confirm");
			dialog.setMessage("Are you sure you want to remove all items?");
			// Create positive button with text "Yes"
			dialog.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						// If positive button was clicked...
						public void onClick(DialogInterface dialog, int which) {
							//Delete all food items
							for (int i = 0; i < foodList.size(); i++) {
								if (!foodList.get(i).isDeleted()) {
									System.out.println("Attempting to delete");
									foodList.get(i).deleteFoodItem();
								}
							}
							//Create foodList ArrayList
							foodList = new ArrayList();
							FoodFileReader.clearFoodList();
						}
					});
			// Create negative button with text "No"
			dialog.setNegativeButton("No", null);
			dialog.show();
			break;
		}
		case R.id.organizeByExpiry: {
			// Run organizeByExpiry method
			organizeByExpiry();
			break;
		}
		case R.id.openGrocery: {
			//Open the grocery list activity
			Intent openGrocery = new Intent(this, GroceryList.class);
			startActivity(openGrocery);
			break;
		}
		default: {
			break;
		}
		}
		return true;
	}

	// Method that is run when the addFood button is clicked
	public void addFoodClick(View v) {
		// Save current instance
		ContextPass.setMainContext(this);
		// Go to the next activity
		startActivity(addFoodPage);
	}

	// Method that is run when the purpleToggle button is clicked
	public void purpleToggleClick(View v) {
		//If the purple toggle button is now on
		if (purple.isChecked()) {
			//Set purpleDisabled to true
			purpleDisabled = true;
			//Set text color to black
			purple.setTextColor(Color.BLACK);
			//Remove all purple labels from view
			for (int i = 0; i < foodList.size(); i++) {
				if (!foodList.get(i).isDeleted()
						&& foodList.get(i).getColour() == FoodItem.PURPLE
						&& foodList.get(i).getDaysRemaining() > FoodItem.EXPIRY_REMINDER) {
					foodList.get(i).setInvisible(true);
				}
			}
		} else {
			//Set purpleDisabled to false;
			purpleDisabled = false;
			//Set text color to white
			purple.setTextColor(Color.WHITE);
			//Make all purple labels that viewable
			for (int i = 0; i < foodList.size(); i++) {
				if (!foodList.get(i).isDeleted()
						&& foodList.get(i).getColour() == FoodItem.PURPLE
						&& foodList.get(i).getDaysRemaining() > FoodItem.EXPIRY_REMINDER) {
					foodList.get(i).setInvisible(false);
				}
			}
		}
	}

	// Method that is run when the greenToggle button is clicked
	public void greenToggleClick(View v) {
		//If the green toggle button is now on
		if (green.isChecked()) {
			//Set greenDisabled to true;
			greenDisabled = true;
			//Set text color to white
			green.setTextColor(Color.BLACK);
			//Remove all green labels from view
			for (int i = 0; i < foodList.size(); i++) {
				if (!foodList.get(i).isDeleted()
						&& foodList.get(i).getColour() == FoodItem.GREEN
						&& foodList.get(i).getDaysRemaining() > FoodItem.EXPIRY_REMINDER) {
					foodList.get(i).setInvisible(true);
				}
			}
		} else {
			//Set greenDisabled to false
			greenDisabled = false;
			//Set text color to white
			green.setTextColor(Color.WHITE);
			//Make all green labels viewable
			for (int i = 0; i < foodList.size(); i++) {
				if (!foodList.get(i).isDeleted()
						&& foodList.get(i).getColour() == FoodItem.GREEN
						&& foodList.get(i).getDaysRemaining() > FoodItem.EXPIRY_REMINDER) {
					foodList.get(i).setInvisible(false);
				}
			}
		}
	}

	// Method that is run when the redToggle button is clicked
	public void redToggleClick(View v) {
		//If the red toggle button is now on
		if (red.isChecked()) {
			//Set redDisabled to true
			redDisabled = true;
			//Set text color to black
			red.setTextColor(Color.BLACK);
			//Remove all red labels from view
			for (int i = 0; i < foodList.size(); i++) {
				if (!foodList.get(i).isDeleted()
						&& foodList.get(i).getColour() == FoodItem.RED
						&& foodList.get(i).getDaysRemaining() > FoodItem.EXPIRY_REMINDER) {
					foodList.get(i).setInvisible(true);
				}
			}
		} else {
			//Set redDisabled to false
			redDisabled = false;
			//Set text color to white
			red.setTextColor(Color.WHITE);
			//Make all red labels viewable
			for (int i = 0; i < foodList.size(); i++) {
				if (!foodList.get(i).isDeleted()
						&& foodList.get(i).getColour() == FoodItem.RED
						&& foodList.get(i).getDaysRemaining() > FoodItem.EXPIRY_REMINDER) {
					foodList.get(i).setInvisible(false);
				}
			}
		}
	}

	// Method that is run when the purpleToggle button is clicked
	public void blueToggleClick(View v) {
		//If the blue toggle button is now on
		if (blue.isChecked()) {
			//Set blueDisabled to true
			blueDisabled = true;
			//Set text color to black
			blue.setTextColor(Color.BLACK);
			//Remove all blue labels from view
			for (int i = 0; i < foodList.size(); i++) {
				if (!foodList.get(i).isDeleted()
						&& foodList.get(i).getColour() == FoodItem.BLUE
						&& foodList.get(i).getDaysRemaining() > FoodItem.EXPIRY_REMINDER) {
					foodList.get(i).setInvisible(true);
				}
			}
		} else {
			blueDisabled = false;
			blue.setTextColor(Color.WHITE);
			for (int i = 0; i < foodList.size(); i++) {
				if (!foodList.get(i).isDeleted()
						&& foodList.get(i).getColour() == FoodItem.BLUE
						&& foodList.get(i).getDaysRemaining() > FoodItem.EXPIRY_REMINDER) {
					foodList.get(i).setInvisible(false);
				}
			}
		}
	}

	// Method that is run when the purpleToggle button is clicked
	public void yellowToggleClick(View v) {
		if (yellow.isChecked()) {
			yellowDisabled = true;
			yellow.setTextColor(Color.BLACK);
			for (int i = 0; i < foodList.size(); i++) {
				if (!foodList.get(i).isDeleted()
						&& foodList.get(i).getColour() == FoodItem.YELLOW
						&& foodList.get(i).getDaysRemaining() > FoodItem.EXPIRY_REMINDER) {
					foodList.get(i).setInvisible(true);
				}
			}
		} else {
			yellowDisabled = false;
			yellow.setTextColor(Color.WHITE);
			for (int i = 0; i < foodList.size(); i++) {
				if (!foodList.get(i).isDeleted()
						&& foodList.get(i).getColour() == FoodItem.YELLOW
						&& foodList.get(i).getDaysRemaining() > FoodItem.EXPIRY_REMINDER) {
					foodList.get(i).setInvisible(false);
				}
			}
		}
	}

	// Method that is run when the purpleToggle button is clicked
	public void orangeToggleClick(View v) {
		if (orange.isChecked()) {
			orangeDisabled = true;
			orange.setTextColor(Color.BLACK);
			for (int i = 0; i < foodList.size(); i++) {
				if (!foodList.get(i).isDeleted()
						&& foodList.get(i).getColour() == FoodItem.ORANGE
						&& foodList.get(i).getDaysRemaining() > FoodItem.EXPIRY_REMINDER) {
					foodList.get(i).setInvisible(true);
				}
			}
		} else {
			orangeDisabled = false;
			orange.setTextColor(Color.WHITE);
			for (int i = 0; i < foodList.size(); i++) {
				if (!foodList.get(i).isDeleted()
						&& foodList.get(i).getColour() == FoodItem.ORANGE
						&& foodList.get(i).getDaysRemaining() > FoodItem.EXPIRY_REMINDER) {
					foodList.get(i).setInvisible(false);
				}
			}
		}
	}

	// Method that is run when the purpleToggle button is clicked
	public void expiringToggleClick(View v) {
		if (expiring.isChecked()) {
			expiringDisabled = true;
			expiring.setTextColor(Color.BLACK);
			for (int i = 0; i < foodList.size(); i++) {
				if (!foodList.get(i).isDeleted()
						&& foodList.get(i).getDaysRemaining() <= FoodItem.EXPIRY_REMINDER
						&& foodList.get(i).getDaysRemaining() > 0) {
					foodList.get(i).setInvisible(true);
				}
			}
		} else {
			expiring.setTextColor(Color.WHITE);
			expiringDisabled = false;
			for (int i = 0; i < foodList.size(); i++) {
				if (!foodList.get(i).isDeleted()
						&& foodList.get(i).getDaysRemaining() <= FoodItem.EXPIRY_REMINDER
						&& foodList.get(i).getDaysRemaining() > 0) {
					foodList.get(i).setInvisible(false);
				}
			}
		}
	}

	// Method that is run when the purpleToggle button is clicked
	public void expiredToggleClick(View v) {
		if (expired.isChecked()) {
			expiredDisabled = true;
			expired.setTextColor(Color.BLACK);
			for (int i = 0; i < foodList.size(); i++) {
				if (!foodList.get(i).isDeleted()
						&& foodList.get(i).getDaysRemaining() < 0) {
					foodList.get(i).setInvisible(true);
				}
			}
		} else {
			expiredDisabled = false;
			expired.setTextColor(Color.WHITE);
			for (int i = 0; i < foodList.size(); i++) {
				if (!foodList.get(i).isDeleted()
						&& foodList.get(i).getDaysRemaining() < 0) {
					foodList.get(i).setInvisible(false);
				}
			}
		}
	}

	public void scanClick(View v) {
		if(scanned){
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("Error");
			dialog.setMessage("You have purchased no new groceries");
			dialog.setNegativeButton("OK", null);
			dialog.show();
		} else {
			
		}
	}

	// Return LinearLayout list
	public static LinearLayout getList() {
		return list;
	}

	// Return ArrayList of FoodItem's
	public static ArrayList<FoodItem> getFoodList() {
		return foodList;
	}

	public static boolean isPurpleDisabled() {
		return purpleDisabled;
	}

	public static boolean isRedDisabled() {
		return redDisabled;
	}

	public static boolean isGreenDisabled() {
		return greenDisabled;
	}

	public static boolean isBlueDisabled() {
		return blueDisabled;
	}

	public static boolean isYellowDisabled() {
		return yellowDisabled;
	}

	public static boolean isOrangeDisabled() {
		return orangeDisabled;
	}

	public static boolean isExpiringDisabled() {
		return expiringDisabled;
	}

	public static boolean isExpiredDisabled() {
		return expiredDisabled;
	}

	public void organizeByExpiry() {
		ArrayList<FoodItem> tempList = new ArrayList();
		for (int i = 0; i < foodList.size(); i++) {
			if (!foodList.get(i).isDeleted()) {
				tempList.add(foodList.get(i));
			}
			foodList.get(i).deleteFoodItem();
		}
		foodList = new ArrayList();
		boolean sorted = false;
		while (!sorted) {
			sorted = true;
			for (int i = 1; i < tempList.size(); i++) {
				if (tempList.get(i - 1).getDaysRemaining() > tempList.get(i)
						.getDaysRemaining()) {
					FoodItem temp = tempList.get(i);
					tempList.set(i, tempList.get(i - 1));
					tempList.set(i - 1, temp);
					sorted = false;
				}
			}
		}
		for (int i = 0; i < tempList.size(); i++) {
			String percentageRequiredString;
			if(tempList.get(i).isPercentagRequired()){
				percentageRequiredString = "Yes";
				foodList.add(new FoodItem(this, list, tempList.get(i).getName(),
						tempList.get(i).getDate(), tempList.get(i).getColour(), true, 1, tempList.get(i).getCompletion()));
			} else {
				percentageRequiredString = "No";
				foodList.add(new FoodItem(this, list, tempList.get(i).getName(),
						tempList.get(i).getDate(), tempList.get(i).getColour(), false, tempList.get(i).getQuantity(), tempList.get(i).getCompletion()));
			}
			String[] data = { tempList.get(i).getName(),
					tempList.get(i).getDate(), "" + tempList.get(i).getColour(), percentageRequiredString, "" + tempList.get(i).getQuantity(), "" + tempList.get(i).getCompletion() };
			FoodFileReader.addFoodItem(data);
		}
	}

}
