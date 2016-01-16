package com.additems.fridgewater;

import java.util.ArrayList;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.fridgewater.R;
import com.example.fridgewater.R.drawable;
import com.example.fridgewater.R.id;
import com.example.fridgewater.R.layout;
import com.filemanager.fridgewater.FoodFileReader;
import com.main.fridgewater.MainActivity;

public class FoodItem extends LinearLayout {

	public final static int PURPLE = 0;
	public final static int GREEN = 1;
	public final static int RED = 2;
	public final static int BLUE = 3;
	public final static int YELLOW = 4;
	public final static int ORANGE = 5;

	public final static String PURPLE_CODE = "#9999FF";
	public final static String GREEN_CODE = "#00FF00";
	public final static String ORANGE_CODE = "#E86C19";
	public final static String RED_CODE = "#FF4747";
	public final static String BLUE_CODE = "#3399FF";
	public final static String YELLOW_CODE = "#FFFF19";

	public final static String PURPLE_TEXT = "#2A009E";
	public final static String GREEN_TEXT = "#007F0E";
	public final static String RED_TEXT = "#7F0000";
	public final static String ORANGE_TEXT = "#B54800";
	public final static String BLUE_TEXT = "#001AAD";
	public final static String YELLOW_TEXT = "#877000";

	public final static String WARNING_BACKGROUND = "#7F3300";
	public final static String WARNING_FONT = "#FFB728";

	public final static String EXPIRED_FONT = "#007F0E";
	public final static String EXPIRED_BACKGROUND = "#404040";

	public final static int EXPIRY_REMINDER = 5;

	String name;
	String date;
	int day, month, year;
	boolean deleted;
	boolean visible;
	boolean percentageRequired;
	int quantity;
	int daysRemaining;
	int percentageCompletion;
	int startingPercentageCompletion;
	LinearLayout parent;
	int colour;

	TextView nameView, expiryView, daysRemainingView, percentageView;
	SeekBar percentage;
	ImageButton removeButton;
	Button recipeButton;
	Context temp;
	View view;

	public FoodItem(Context context, final LinearLayout parent,
			final String name, final String date, final int colour,
			boolean percentageRequired, final int quantity, int completion) {
		super(context);

		temp = context;

		this.name = name;
		this.date = date;
		this.parent = parent;
		this.colour = colour;
		this.quantity = quantity;
		this.percentageRequired = percentageRequired;
		this.percentageCompletion = completion;

		String tempDay = "";
		String tempMonth = "";
		String tempYear = "";
		char character = date.charAt(0);
		int charCounter = 0;
		while (character != '/') {
			tempDay += character;
			charCounter++;
			character = date.charAt(charCounter);
		}
		charCounter++;
		character = date.charAt(charCounter);
		while (character != '/') {
			tempMonth += character;
			charCounter++;
			character = date.charAt(charCounter);
		}
		charCounter++;
		character = date.charAt(charCounter);
		boolean run = true;
		while (run) {
			tempYear += character;
			charCounter++;
			try {
				character = date.charAt(charCounter);
			} catch (StringIndexOutOfBoundsException e) {
				run = false;
			}
		}

		day = Integer.parseInt(tempDay);
		month = Integer.parseInt(tempMonth);
		year = Integer.parseInt(tempYear);

		LayoutInflater inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.food_list, this, true);

		nameView = (TextView) findViewById(R.id.foodName);
		expiryView = (TextView) findViewById(R.id.expiryDate);
		daysRemainingView = (TextView) findViewById(R.id.remainingDays);
		percentageView = (TextView) findViewById(R.id.percentageView);
		removeButton = (ImageButton) findViewById(R.id.deleteItem);
		recipeButton = (Button) findViewById(R.id.checkRecipe);
		percentage = (SeekBar) findViewById(R.id.percentage);

		nameView.setText(name);
		String expiryDate = "Expiry Date: " + date;
		expiryView.setText(expiryDate);
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
								deleteFoodItem();

							}
						});
				// Create negative button with text "No"
				dialog.setNegativeButton("No", null);
				dialog.show();
			}

		});
		recipeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(temp);
				dialog.setTitle("Confirm");
				dialog.setMessage("Browse internet for recipe's using: " + name
						+ "?");
				// Create positive button with text "Yes"
				dialog.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							// If positive button was clicked...
							public void onClick(DialogInterface dialog,
									int which) {
								int charCounter = 0;
								ArrayList<String> wordList = new ArrayList();
								String word = "";
								while (charCounter < name.length()) {
									if (name.charAt(charCounter) != ' ') {
										word = word + name.charAt(charCounter);
									} else {
										if (!word.equals("")) {
											wordList.add(word);
											word = "";
										}
									}
									charCounter++;
								}
								if (!word.equals("")) {
									wordList.add(word);
								}
								String url = "https://www.google.com/search?q=recipes+using";
								for (int i = 0; i < wordList.size(); i++) {
									url = url + "+" + wordList.get(i);
								}
								Intent openBrowser = new Intent(
										Intent.ACTION_VIEW, Uri.parse(url));
								temp.startActivity(openBrowser);

							}
						});
				// Create negative button with text "No"
				dialog.setNegativeButton("No", null);
				dialog.show();

			}
		});

		if (percentageRequired) {
			percentage.setMax(100);
			percentageView.setText(percentageCompletion + "%");
			percentage.setProgress(percentageCompletion);
			percentage
					.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							percentageView.setText(progress + "%");
							percentageCompletion = progress;
							if (progress == 100) {
								AlertDialog.Builder dialog = new AlertDialog.Builder(
										temp);
								dialog.setTitle("Confirm");
								dialog.setMessage("Remove this item??");
								// Create positive button with text "Yes"
								dialog.setPositiveButton("Yes",
										new DialogInterface.OnClickListener() {
											// If positive button was clicked...
											public void onClick(
													DialogInterface dialog,
													int which) {
												deleteFoodItem();

											}
										});
								// Create negative button with text "No"
								dialog.setNegativeButton("No", null);
								dialog.show();
							}
						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							startingPercentageCompletion = percentageCompletion;

						}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							String[] data = { name, date, "" + colour, "Yes",
									"" + quantity,
									"" + startingPercentageCompletion };
							FoodFileReader.updateListCompletion(data,
									percentageCompletion);
						}

					});
		} else if (quantity > 1) {
			percentage.setMax(quantity);
			percentageView.setText(percentageCompletion + "/" + quantity);
			percentage.setProgress(percentageCompletion);
			percentage
					.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							percentageView.setText(progress + "/" + quantity);
							percentageCompletion = progress;
							if (progress == quantity) {
								AlertDialog.Builder dialog = new AlertDialog.Builder(
										temp);
								dialog.setTitle("Confirm");
								dialog.setMessage("Remove this item??");
								// Create positive button with text "Yes"
								dialog.setPositiveButton("Yes",
										new DialogInterface.OnClickListener() {
											// If positive button was clicked...
											public void onClick(
													DialogInterface dialog,
													int which) {
												deleteFoodItem();

											}
										});
								// Create negative button with text "No"
								dialog.setNegativeButton("No", null);
								dialog.show();
							}
						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							startingPercentageCompletion = percentageCompletion;
						}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							String[] data = { name, date, "" + colour, "No",
									"" + quantity,
									"" + startingPercentageCompletion };
							FoodFileReader.updateListCompletion(data,
									percentageCompletion);

						}

					});
		} else {
			percentage.setVisibility(View.INVISIBLE);
			percentageView.setVisibility(View.INVISIBLE);
		}
		boolean makeVisible = true;
		switch (colour) {
		case PURPLE: {
			view.setBackgroundResource(drawable.purple_fresh);
			nameView.setTextColor(Color.parseColor(PURPLE_TEXT));
			// nameView.setBackgroundColor(Color.parseColor(PURPLE_CODE));
			expiryView.setTextColor(Color.parseColor(PURPLE_TEXT));
			// expiryView.setBackgroundColor(Color.parseColor(PURPLE_CODE));
			daysRemainingView.setTextColor(Color.parseColor(PURPLE_TEXT));
			// daysRemainingView.setBackgroundColor(Color.parseColor(PURPLE_CODE));
			if (MainActivity.isPurpleDisabled()) {
				makeVisible = false;
			}
			break;
		}
		case GREEN: {
			view.setBackgroundResource(drawable.green_fresh);
			nameView.setTextColor(Color.parseColor(GREEN_TEXT));
			// nameView.setBackgroundColor(Color.parseColor(GREEN_CODE));
			expiryView.setTextColor(Color.parseColor(GREEN_TEXT));
			// expiryView.setBackgroundColor(Color.parseColor(GREEN_CODE));
			daysRemainingView.setTextColor(Color.parseColor(GREEN_TEXT));
			// daysRemainingView.setBackgroundColor(Color.parseColor(GREEN_CODE));
			if (MainActivity.isGreenDisabled()) {
				makeVisible = false;
			}
			break;
		}
		case ORANGE: {
			view.setBackgroundResource(drawable.orange_fresh);
			nameView.setTextColor(Color.parseColor(ORANGE_TEXT));
			// nameView.setBackgroundColor(Color.parseColor(ORANGE_CODE));
			expiryView.setTextColor(Color.parseColor(ORANGE_TEXT));
			// expiryView.setBackgroundColor(Color.parseColor(ORANGE_CODE));
			daysRemainingView.setTextColor(Color.parseColor(ORANGE_TEXT));
			// daysRemainingView.setBackgroundColor(Color.parseColor(ORANGE_CODE));

			if (MainActivity.isOrangeDisabled()) {
				makeVisible = false;
			}
			break;
		}
		case RED: {
			view.setBackgroundResource(drawable.red_fresh);
			nameView.setTextColor(Color.parseColor(RED_TEXT));
			// nameView.setBackgroundColor(Color.parseColor(RED_CODE));
			expiryView.setTextColor(Color.parseColor(RED_TEXT));
			// expiryView.setBackgroundColor(Color.parseColor(RED_CODE));
			daysRemainingView.setTextColor(Color.parseColor(RED_TEXT));
			// daysRemainingView.setBackgroundColor(Color.parseColor(RED_CODE));
			if (MainActivity.isRedDisabled()) {
				makeVisible = false;
			}
			break;
		}
		case BLUE: {
			view.setBackgroundResource(drawable.blue_fresh);
			nameView.setTextColor(Color.parseColor(BLUE_TEXT));
			// nameView.setBackgroundColor(Color.parseColor(BLUE_CODE));
			expiryView.setTextColor(Color.parseColor(BLUE_TEXT));
			// expiryView.setBackgroundColor(Color.parseColor(BLUE_CODE));
			daysRemainingView.setTextColor(Color.parseColor(BLUE_TEXT));
			// .setBackgroundColor(Color.parseColor(BLUE_CODE));
			if (MainActivity.isBlueDisabled()) {
				makeVisible = false;
			}
			break;
		}
		case YELLOW: {
			view.setBackgroundResource(drawable.yellow_fresh);
			nameView.setTextColor(Color.parseColor(YELLOW_TEXT));
			// nameView.setBackgroundColor(Color.parseColor(YELLOW_CODE));
			expiryView.setTextColor(Color.parseColor(YELLOW_TEXT));
			// expiryView.setBackgroundColor(Color.parseColor(YELLOW_CODE));
			daysRemainingView.setTextColor(Color.parseColor(YELLOW_TEXT));
			// daysRemainingView.setBackgroundColor(Color.parseColor(YELLOW_CODE));
			if (MainActivity.isYellowDisabled()) {
				makeVisible = false;
			}
			break;
		}
		}
		checkDate();
		if (daysRemaining <= EXPIRY_REMINDER && daysRemaining >= 0
				&& MainActivity.isExpiringDisabled()) {
			makeVisible = false;
		}
		if (daysRemaining < 0 && MainActivity.isExpiredDisabled()) {
			makeVisible = false;
		}
		if (makeVisible) {
			parent.addView(this);
		}
	}

	public int getCompletion() {
		return percentageCompletion;
	}

	public boolean isPercentagRequired() {
		return percentageRequired;
	}

	public int getQuantity() {
		return quantity;
	}

	public String getName() {
		return name;
	}

	public String getDate() {
		return date;
	}

	public int getDaysRemaining() {
		return daysRemaining;
	}

	// Return's day
	public int getDay() {
		return day;
	}

	// Return's month
	public int getMonth() {
		return month;
	}

	// Return's year
	public int getYear() {
		return year;
	}

	// Return's an integer that represents the item's colour
	public int getColour() {
		return colour;
	}

	// Returns deleted
	public boolean isDeleted() {
		return deleted;
	}

	// Returns ingredient

	// Delete the food item
	public void deleteFoodItem() {
		parent.removeView(this);
		deleted = true;
		String percentageRequiredString;
		if (percentageRequired) {
			percentageRequiredString = "Yes";
		} else {
			percentageRequiredString = "No";
		}
		String[] data = { name, date, "" + colour, percentageRequiredString,
				"" + quantity, "" + percentageCompletion };
		FoodFileReader.removeFromFoodList(data);
	}

	// Make the food item invisible/visible
	public void setInvisible(boolean invisible) {
		if (invisible) {
			parent.removeView(this);
			visible = false;
		} else {
			parent.addView(this);
			visible = true;
		}
	}

	public void checkDate() {
		// Date date = new Date();
		DateTimeZone timeZone = DateTimeZone.forTimeZone(TimeZone.getDefault());
		DateTime date = new DateTime(timeZone);
		DateTime expiryDate = new DateTime(year, month, day, 0, 0, timeZone);

		long dateTime = date.getMillis();
		long expiryTime = expiryDate.getMillis();

		long timeDif = expiryTime - dateTime;

		int dayDif = (int) (Math.ceil(timeDif / (1000 * 60 * 60 * 24)));
		daysRemaining = dayDif + 1;
		daysRemainingView.setText("Days remaining: " + dayDif);
		if (dayDif < 0) {
			daysRemainingView.setText("This item has expired");
			recipeButton.setVisibility(View.INVISIBLE);
			view.setBackgroundResource(drawable.expired_warning);
			nameView.setBackgroundColor(Color.parseColor(EXPIRED_BACKGROUND));
			nameView.setTextColor(Color.WHITE);
			expiryView.setBackgroundColor(Color.parseColor(EXPIRED_BACKGROUND));
			expiryView.setTextColor(Color.WHITE);
			daysRemainingView.setBackgroundColor(Color
					.parseColor(EXPIRED_BACKGROUND));
			daysRemainingView.setTextColor(Color.WHITE);
		} else if (dayDif <= EXPIRY_REMINDER) {
			expiryView.setBackgroundColor(Color.parseColor(WARNING_BACKGROUND));
			nameView.setBackgroundColor(Color.parseColor(WARNING_BACKGROUND));
			daysRemainingView.setBackgroundColor(Color
					.parseColor(WARNING_BACKGROUND));
			expiryView.setTextColor(Color.parseColor(WARNING_FONT));
			nameView.setTextColor(Color.parseColor(WARNING_FONT));
			daysRemainingView.setTextColor(Color.parseColor(WARNING_FONT));
			view.setBackgroundResource(R.drawable.expiration_warning);
			// daysRemainingView.setTextColor(Color.RED);
		}

	}
}
