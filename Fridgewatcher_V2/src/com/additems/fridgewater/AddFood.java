package com.additems.fridgewater;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fridgewater.R;
import com.example.fridgewater.R.id;
import com.example.fridgewater.R.layout;
import com.example.fridgewater.R.menu;
import com.filemanager.fridgewater.FoodFileReader;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.main.fridgewater.MainActivity;
import com.other.fridgewater.ContextPass;

public class AddFood extends Activity {
	// Voice Recognition Constant
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
	// EditText declarations
	EditText nameText, dayText, monthText, yearText, numberOfItem;
	// List of spinner labels
	final String[] spinnerList = { "Purple", "Green", "Red", "Blue", "Yellow",
			"Orange" };
	// Get display
	Display display;
	// Declare spinner
	Spinner colourSpinner;
	// Declare percentage checkbox
	CheckBox percentageRequired;
	// Int for selected colour
	int selectedColour;
	// Boolean for ingredient
	boolean ingredient;

	@Override
	// Method that is called on activity creation.
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_food);
		// Get display
		display = getWindowManager().getDefaultDisplay();
		// Get the EditText objects
		nameText = (EditText) findViewById(R.id.enterFoodName);
		dayText = (EditText) findViewById(R.id.enterDay);
		monthText = (EditText) findViewById(R.id.enterMonth);
		yearText = (EditText) findViewById(R.id.enterYear);
		numberOfItem = (EditText) findViewById(R.id.foodNumber);
		// Get the Spinner object
		colourSpinner = (Spinner) findViewById(R.id.colourSpinner);
		// Get the Checkbox object
		percentageRequired = (CheckBox) findViewById(R.id.requirePercentage);
		// Set the sizes of day,month, and year EditText objects
		int width = display.getWidth();
		dayText.setWidth((int) Math.round(width / 4));
		monthText.setWidth((int) Math.round(width / 4));
		yearText.setWidth((int) Math.round(width / 4));
		// Set starting text for numberOfItem EditText to 1
		numberOfItem.setText("1");
		// Set ingredient to true
		ingredient = true;
		// Create Spinner using the declared spinner labels and custom spinner
		colourSpinner.setAdapter(new CustomAdapter(this,
				R.layout.colour_spinner, spinnerList));
		// Allow internet access (SHOULD CHANGE)
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}

	@Override
	// Method that is called on Activity creation
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_food, menu);
		return true;
	}

	// AddFoodClick method
	public void addFoodClick(View v) {
		// Get the LinearList from MainActivity
		LinearLayout list = MainActivity.getList();
		// Get the text from EditText boxes
		String name = nameText.getText().toString();
		String day = dayText.getText().toString();
		String month = monthText.getText().toString();
		String year = yearText.getText().toString();
		String date = day + "/" + month + "/" + year;
		int numberOfFoodItems = Integer.parseInt(numberOfItem.getText()
				.toString());
		// Remove all extra spaces in the name string
		boolean run = true;
		int charCounter = 0;
		while (run) {
			if (charCounter < name.length() && name.charAt(charCounter) == ' ') {
				charCounter++;
			} else {
				run = false;
			}
		}
		name = name.substring(charCounter);
		// Get the selected colour
		selectedColour = colourSpinner.getSelectedItemPosition();
		int dayValue, monthValue, yearValue;
		// Get the integer value in day EditText
		try {
			dayValue = (Integer.parseInt(day));
		} catch (NumberFormatException e) {
			// Set dayValue to 0 if day EditText is invalid
			dayValue = 0;
		}
		// Get the integer value in month EditText
		try {
			monthValue = (Integer.parseInt(month));
		} catch (NumberFormatException e) {
			// Set monthValue to 0 if month EditText is invalid
			monthValue = 0;
		}
		// Get the integer value in year EditText
		try {
			yearValue = (Integer.parseInt(year));
		} catch (NumberFormatException e) {
			// Set yearValue to 0 if year EditText is invalid
			yearValue = 0;
		}
		// Try and set the date
		try {
			DateTime temp = new DateTime(yearValue, monthValue, dayValue, 0, 0);
		} catch (IllegalFieldValueException e) {
			// If the date is invalid, set dayValue to -1. This will be caught
			// later
			dayValue = -1;
		}
		// If any of the text values are invalid
		if (name.equals("") || (numberOfFoodItems <= 0) || (dayValue > 31)
				|| (dayValue <= 0) || (monthValue > 12) || (monthValue <= 0)
				|| (yearValue <= 0)) {
			// Create dialog box
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("Error");
			ArrayList<String> errors = new ArrayList();
			// If the name is blank, add "name" to error list
			if (name.equals("")) {
				errors.add("name");
			}
			// if the day is an invalid number, add "day" to error list
			if ((dayValue > 31) || (dayValue <= 0)) {
				errors.add("day");
			}
			// If the month is an invalid number, add "month" to error list
			if ((monthValue > 12) || (monthValue <= 0)) {
				errors.add("month");
			}
			// If the year is an invalid number, add "year" to error list
			if (yearValue <= 0) {
				errors.add("year");
			}
			// If numberOfFoodItems is an invalid number, add "number of items"
			// to error list
			if (numberOfFoodItems <= 0) {
				errors.add("number of items");
			}
			// Generate error text
			String errorMessage = "Invalid " + errors.get(0);
			for (int i = 1; i < errors.size() - 1; i++) {
				errorMessage = errorMessage + ", " + errors.get(i);
			}
			// Add last error to the String
			if (errors.size() > 1) {
				errorMessage = errorMessage + " and "
						+ errors.get(errors.size() - 1);
			}
			errorMessage = errorMessage + ".";
			// Display error message
			dialog.setMessage(errorMessage);
			dialog.setNegativeButton("OK", null);
			dialog.show();
		} else {
			// If percentage require is checked, create the required number of
			// FoodItems with a percentage bar
			if (percentageRequired.isChecked()) {
				for (int i = 0; i < numberOfFoodItems; i++) {
					MainActivity.getFoodList().add(
							new FoodItem(ContextPass.getMainContext(), list,
									name, date, selectedColour, true, 1, 0));
					String[] data = { name, date, "" + selectedColour, "Yes",
							"1", "0" };
					FoodFileReader.addFoodItem(data);
				}
			} else {
				// Create FoodItem without percentage bar
				MainActivity.getFoodList().add(
						new FoodItem(ContextPass.getMainContext(), list, name,
								date, selectedColour, false, numberOfFoodItems,
								0));
				String[] data = { name, date, "" + selectedColour, "No",
						"" + numberOfFoodItems, "0" };
				FoodFileReader.addFoodItem(data);
			}
			// Go back to last activity (Main Activity)
			onBackPressed();
		}
	}

	// Method that is called
	public void voiceRecClick(View v) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				"Say \"Name\", \"Date\", or \"Quantity\" followed by the input you want");
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	// Method that is run when barcodeScan is clicked
	public void barcodeScanClick(View v) {
		// Check if camera is working
		if (isCameraWorking()) {
			// Start barcode scanner
			IntentIntegrator scanner = new IntentIntegrator(this);
			scanner.initiateScan();
		} else {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("Camera Not Available");
			dialog.setMessage("Your camera on your device is not available");
			dialog.setNegativeButton("OK", null);
			dialog.show();
		}
	}

	// Method that is called when this activity is called by another intent
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// If there is no internet connection
		if (!isNetworkAvailable()) {
			// Create dialog box
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			// Tell the user there is no internet connection, and that the
			// service cannot be run
			dialog.setTitle("No Internet Connection");
			dialog.setMessage("You have no internet connection.  Connect to the internet to use this service");
			dialog.setNegativeButton("OK", null);
			dialog.show();
			return;
		}
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {
			// If there was a result
			if (resultCode == RESULT_OK) {
				// Get the list of all possible sentences the user said
				ArrayList<String> textMatchList = intent
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				String result = "";
				ArrayList<String> wordList = null;
				// Declare booleans
				boolean failure = false;
				boolean name = false;
				boolean date = false;
				boolean quantity = false;
				// Check all sentences
				for (int i = 0; i < textMatchList.size(); i++) {
					// If the current sentence includes the word "name" in it
					if (textMatchList.get(i).contains("name")) {
						// Save the sentence
						wordList = getSentence(textMatchList.get(i));
						// Exit the for loop
						i = textMatchList.size();
						name = true;
					} else if (textMatchList.get(i).contains("date")) {
						// Save the sentence.
						wordList = getSentence(textMatchList.get(i));
						// Exit the for loop
						i = textMatchList.size();
						date = true;

					} else if (textMatchList.get(i).contains("quantity")) {
						// Save the sentence
						wordList = getSentence(textMatchList.get(i));
						// Exit the for loop
						i = textMatchList.size();
						quantity = true;
					}
				}
				// If the command was name
				if (name) {
					try {
						// Try and get all the words after "name" was stated
						result = wordList.get(1);
						for (int i = 2; i < wordList.size(); i++) {
							result = result + " " + wordList.get(i);
						}
						// Set the name text box to it's assigned value
						nameText.setText(result);
					} catch (IndexOutOfBoundsException e) {
						failure = true;
					}
				} else if (date) {
					// Try and decipher the words after "date" was stated
					try {
						// Declare strings
						String dayValue;
						String monthValue;
						String yearValue;
						// Get the month number
						if (wordList.contains("January")) {
							monthValue = "1";
						} else if (wordList.contains("February")) {
							monthValue = "2";
						} else if (wordList.contains("March")) {
							monthValue = "3";
						} else if (wordList.contains("April")) {
							monthValue = "4";
						} else if (wordList.contains("May")) {
							monthValue = "5";
						} else if (wordList.contains("June")) {
							monthValue = "6";
						} else if (wordList.contains("July")) {
							monthValue = "7";
						} else if (wordList.contains("August")) {
							monthValue = "8";
						} else if (wordList.contains("September")) {
							monthValue = "9";
						} else if (wordList.contains("October")) {
							monthValue = "10";
						} else if (wordList.contains("November")) {
							monthValue = "11";
						} else if (wordList.contains("December")) {
							monthValue = "12";
						} else {
							monthValue = "";
						}
						// Get the numerical day value
						String temp = wordList.get(2);
						dayValue = "";
						for (int i = 0; i < temp.length(); i++) {
							if (isNumber(temp.charAt(i))) {
								dayValue = dayValue + temp.charAt(i);
							}
						}
						// Get the year value
						yearValue = wordList.get(3);
						// Set the text boxes to their assigned values
						dayText.setText(dayValue);
						monthText.setText(monthValue);
						yearText.setText(yearValue);
					} catch (IndexOutOfBoundsException e) {
						failure = true;
					}
				} else if (quantity) {
					// Try and decipher the stated quantity
					try {
						// Get the word after "quantity" was stated
						result = wordList.get(1);
						// Set the numberOfItem textbox to it's assigned value
						numberOfItem.setText(result);
					} catch (IndexOutOfBoundsException e) {
						failure = true;
					}
				} else {
					failure = true;
				}
				// If there was a failure
				if (failure) {
					// Create dialog box
					AlertDialog.Builder dialog = new AlertDialog.Builder(this);
					// Tell the user that there was an error
					dialog.setTitle("Error");
					dialog.setMessage("Unable to recognize your command.  Please try again.");
					dialog.setNegativeButton("OK", null);
					dialog.show();
				}
			}
		} else {
			// Get the scanning result
			IntentResult scanningResult = IntentIntegrator.parseActivityResult(
					requestCode, resultCode, intent);
			// If there was a scanning result
			if (scanningResult != null) {
				// Get the scan contents
				String scanContents = scanningResult.getContents();
				// Create outpan URL
				String url = "http://www.outpan.com/api/get_product.php?barcode="
						+ scanContents;
				// Create HTTP Client
				HttpClient client = new DefaultHttpClient();
				// Create HTTP request using the URL string
				HttpGet get = new HttpGet(url);
				try {
					// Get HTTP response
					HttpResponse response = client.execute(get);
					// Get response data
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						// Convert response data into string
						String entityString = EntityUtils.toString(entity);
						try {
							// Create JSON object with the data from
							// entityString
							JSONObject jsonObj = new JSONObject(entityString);
							// Get the name of the item
							String itemName = jsonObj.getString("name");
							// Set the nameText text to the scanned name
							nameText.setText(itemName);
						} catch (JSONException e) {
							// Create dialog box
							AlertDialog.Builder dialog = new AlertDialog.Builder(
									this);
							// Tell user that the product could not be found
							dialog.setTitle("Product not found");
							dialog.setMessage("We could not find the product using this bardcode");
							dialog.setNegativeButton("Ok", null);
							dialog.show();
						}
					} else {
						// Create dialog box
						AlertDialog.Builder dialog = new AlertDialog.Builder(
								this);
						// Tell the user no code was found
						dialog.setTitle("No Code");
						dialog.setMessage("No barcode found");
						dialog.setNegativeButton("OK", null);
						dialog.show();
					}
				} catch (IOException e) {
					System.out.println("IO Exception");
				}
			} else {
				System.out.println("No result");
			}
		}
	}

	private ArrayList<String> getSentence(String sentence) {
		ArrayList<String> list = new ArrayList();
		// Declare blank string
		String word = "";
		// Loop for length of sentence string
		for (int i = 0; i < sentence.length(); i++) {
			// If the character scanned is an allowed character, add it to the
			// word string
			if (isCharacter(sentence.charAt(i)) || isNumber(sentence.charAt(i))) {
				word = word + sentence.charAt(i);
				// If not an allowed character and word string is not empty, add
				// it to the list
			} else if (!word.equals("")) {
				list.add(word);
				word = "";
			}
		}
		// Add any remaining word to the list.
		if (!word.equals("")) {
			list.add(word);
		}
		return list;
	}

	// Return's true if the character is an allowed character
	private boolean isCharacter(char character) {
		if (((int) character >= 65 && character <= 95)
				|| ((int) character >= 97 && (int) character <= 122)
				|| character == '\'') {
			return true;
		}
		return false;
	}

	// Return's true if the character is a number
	private boolean isNumber(char character) {
		if ((int) character >= 48 && (int) character <= 57) {
			return true;
		}
		return false;
	}

	// Boolean that checks if there is internet connection
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	// Boolean that checks if the camera is working
	public boolean isCameraWorking() {
		PackageManager pm = getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}

	// Custom adapter for Spinner
	public class CustomAdapter extends ArrayAdapter<String> {
		// Constructor
		public CustomAdapter(Context context, int resource, String[] objects) {
			super(context, resource, objects);
		}

		@Override
		public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
			return getCustomView(position, cnvtView, prnt);
		}

		@Override
		public View getView(int pos, View cnvtView, ViewGroup prnt) {
			return getCustomView(pos, cnvtView, prnt);
		}

		// Returns the custom view
		public View getCustomView(int pos, View view, ViewGroup parent) {
			// Get layout
			LayoutInflater inflater = getLayoutInflater();
			// Get the current view
			View mySpinner = inflater.inflate(R.layout.colour_spinner, parent,
					false);
			// Get the label object
			TextView label = (TextView) mySpinner.findViewById(R.id.label);
			// Switch the background of the view depending on the Position of
			// the spinner
			switch (pos) {
			// Set the background to the purple background
			case FoodItem.PURPLE: {
				label.setText("Purple");
				label.setBackgroundColor(Color.parseColor("#9999FF"));
				label.setTextColor(Color.BLACK);
				break;
			}
			// Set background to the green background
			case FoodItem.GREEN: {
				label.setText("Green");
				label.setBackgroundColor(Color.parseColor("#00FF00"));
				label.setTextColor(Color.BLACK);
				break;
			}
			// Set background to the orange background
			case FoodItem.ORANGE: {
				label.setText("Orange");
				label.setBackgroundColor(Color.parseColor("#E86C19"));
				label.setTextColor(Color.BLACK);
				break;
			}
			// Set background to the red background
			case FoodItem.RED: {
				label.setText("Red");
				label.setBackgroundColor(Color.parseColor("#FF4747"));
				label.setTextColor(Color.BLACK);
				break;
			}
			// Set background to the blue background
			case FoodItem.BLUE: {
				label.setText("Blue");
				label.setBackgroundColor(Color.parseColor("#3399FF"));
				label.setTextColor(Color.BLACK);
				break;
			}
			// Set the background to the blue background
			case FoodItem.YELLOW: {
				label.setText("Yellow");
				label.setBackgroundColor(Color.parseColor(FoodItem.YELLOW_CODE));
				label.setTextColor(Color.BLACK);
				break;
			}

			}
			// Return the view
			return mySpinner;
		}

	}

}
