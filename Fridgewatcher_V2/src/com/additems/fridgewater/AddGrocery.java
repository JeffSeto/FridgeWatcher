package com.additems.fridgewater;

import java.util.ArrayList;

import com.example.fridgewater.R;
import com.example.fridgewater.R.id;
import com.example.fridgewater.R.layout;
import com.example.fridgewater.R.menu;
import com.filemanager.fridgewater.FoodFileReader;
import com.main.fridgewater.GroceryList;
import com.other.fridgewater.ContextPass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class AddGrocery extends Activity {
	// Declare EditText objects
	EditText groceryNameText, groceryAmountText;
	// Voice rec code
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_grocery);
		// Get the EditText objects
		groceryAmountText = (EditText) findViewById(R.id.groceryNumber);
		groceryNameText = (EditText) findViewById(R.id.enterGroceryName);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_grocery, menu);
		return true;
	}

	public void addGroceryClick(View v) {
		// Get the groceryName
		String groceryName = groceryNameText.getText().toString();
		// Get the groceryAmount
		int groceryAmount = Integer.parseInt(groceryAmountText.getText()
				.toString());
		// Remove all starting spaces from name String
		boolean run = true;
		int charCounter = 0;
		while (run) {
			if (charCounter < groceryName.length()
					&& groceryName.charAt(charCounter) == ' ') {
				charCounter++;
			} else {
				run = false;
			}
		}
		groceryName = groceryName.substring(charCounter);
		String errorText;
		// If there is an invalid value entered
		if (groceryName.equals("") || groceryAmount <= 0) {
			// If grocery name is blank and groceryAmount is an invalid number,
			// set errorText to "Invalid name and amount."
			if (groceryName.equals("") && groceryAmount <= 0) {
				errorText = "Invalid name and amount.";
				// If only grocery name is blank, set errorText to
				// "Invalid name."
			} else if (groceryName.equals("")) {
				errorText = "Invalid name.";
				// If only groceryAmount is less than 0, set errorText to
				// "Invalid amount."
			} else {
				errorText = "Invalid amount.";
			}
			// Create and display error message
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("Error");
			dialog.setMessage(errorText);
			dialog.setNegativeButton("OK", null);
			dialog.show();
		} else {
			
			GroceryItem temp = new GroceryItem(ContextPass.getGroceryContext(), GroceryList.getList(), groceryName, groceryAmount, 0);
			// Add object to grocery list
			GroceryList.getGroceryList().add(temp);
			// Add grocery item to grocery text list
			String[] data = { groceryName, "" + groceryAmount, "0" };
			FoodFileReader.addGroceryItem(data);
			// Go back to previous activity
			onBackPressed();
		}
	}

	public void voiceRecClick(View v) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				"Say \"Name\" or \"Quantity\" followed by the input you want");
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

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
					groceryNameText.setText(result);
				} catch (IndexOutOfBoundsException e) {
					failure = true;
				}
			} else if (quantity) {
				// Try and decipher the stated quantity
				try {
					// Get the word after "quantity" was stated
					result = wordList.get(1);
					// Set the numberOfItem textbox to it's assigned value
					groceryAmountText.setText(result);
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
	
	//Boolean that checks if there is internet connection
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
