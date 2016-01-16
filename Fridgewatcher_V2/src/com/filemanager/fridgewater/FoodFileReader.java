package com.filemanager.fridgewater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import android.app.Activity;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;

import com.other.fridgewater.ContextPass;

public class FoodFileReader {
	//SD Card directory
	final static File sdCard = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	//FoodList file name
	final static String FOOD_NAME = "FoodList.txt";
	//GroceryList file name
	final static String GROCERY_NAME = "GroceryList.txt";

	public static ArrayList<String[]> getFoodList() {
		ArrayList<String[]> foodList = new ArrayList();
		try {
			FileOutputStream fileOutput = ContextPass.getMainContext().openFileOutput(FOOD_NAME, Activity.MODE_APPEND);
		} catch (FileNotFoundException e1) {
			System.out.println("No File Found");
		}
		boolean finished = false;
		//Create reader
		FileInputStream reader = null;
		Scanner sc;
		try {
			reader = ContextPass.getMainContext().openFileInput(FOOD_NAME);
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
		sc = new Scanner(reader);
		////Read all values from the text file
		while (finished == false) {
			try {
				String[] data = new String[6];
				data[0] = sc.nextLine();
				data[1] = sc.nextLine();
				data[2] = sc.nextLine();
				data[3] = sc.nextLine();
				data[4] = sc.nextLine();
				data[5] = sc.nextLine();
				foodList.add(data);
			} catch (NoSuchElementException e) {
				finished = true;
			}
		}
		//Return foodList ArrayList		
		return foodList;
	}
	
	//Method for removing a specific FoodItem from the text file
	public static void removeFromFoodList(String[] data) {
		//Get the ArrayList from getFoodList()
		ArrayList<String[]> foodList = getFoodList();
		boolean removed = false;
		try {
			//Create file writer
			FileOutputStream fileOutput = ContextPass.getMainContext().openFileOutput(FOOD_NAME, Activity.MODE_PRIVATE);
			PrintStream print = new PrintStream(fileOutput);
			for (int i = 0; i < foodList.size(); i++) {
				//If the current FoodItem has the exact same data as the FoodItem flagged for removal
				if (data[0].equals(foodList.get(i)[0])
						&& data[1].equals(foodList.get(i)[1])
						&& data[2].equals(foodList.get(i)[2]) 
						&& data[3].equals(foodList.get(i)[3])
						&& data[4].equals(foodList.get(i)[4]) 
						&& data[5].equals(foodList.get(i)[5])&& !removed) {
					//Set remove to true, and do not print the FoodItem
					removed = true;
				} else{
					//If the current FoodItem 
					for (int j = 0; j < foodList.get(i).length; j++) {
						print.println(foodList.get(i)[j]);
					}
				}
			}
			print.close();
			fileOutput.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("IOException");
		}
	}

	public static void addFoodItem(String[] data) {
		try {
			ArrayList<String[]> list = getFoodList();
			//FileOutputStream fileOutput = new FileOutputStream(new File(directory, FOOD_NAME));
			FileOutputStream fileOutput = ContextPass.getMainContext().openFileOutput(FOOD_NAME, Activity.MODE_APPEND);
			PrintStream print = new PrintStream(fileOutput);
			for (int i = 0; i < data.length; i++) {
				print.println(data[i]);
			}
			print.close();
			fileOutput.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("IOException");
		}
	}
	
	public static void updateListCompletion(String[] data, int completion){
		ArrayList<String[]> foodList = getFoodList();
		boolean updated = false;
		try {
			FileOutputStream fileOutput = ContextPass.getMainContext().openFileOutput(FOOD_NAME, Activity.MODE_PRIVATE);
			PrintStream print = new PrintStream(fileOutput);
			for (int i = 0; i < foodList.size(); i++) {
				if (data[0].equals(foodList.get(i)[0])
						&& data[1].equals(foodList.get(i)[1])
						&& data[2].equals(foodList.get(i)[2]) 
						&& data[3].equals(foodList.get(i)[3])
						&& data[4].equals(foodList.get(i)[4]) 
						&& data[5].equals(foodList.get(i)[5]) && !updated) {
					System.out.println("Replacing");
					
					
					for(int j = 0; j < foodList.get(i).length - 1; j++){
						print.println(foodList.get(i)[j]);
					}
					updated = true;
					print.println(completion);
				} else{
					for (int j = 0; j < foodList.get(i).length; j++) {
						print.println(foodList.get(i)[j]);
					}
				}
			}
			print.close();
			fileOutput.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("IOException");
		}
	}
	
	public static void clearFoodList(){
		try {
			FileOutputStream fileOutput = ContextPass.getMainContext().openFileOutput(FOOD_NAME, Activity.MODE_PRIVATE);
			fileOutput.close();
		} catch (IOException e) {
			System.out.println("IOException");
		}
	}
	
	public static ArrayList getGroceryList(){
		ArrayList<String[]> groceryList = new ArrayList();
		try {
			FileOutputStream fileOutput = ContextPass.getGroceryContext().openFileOutput(GROCERY_NAME, Activity.MODE_APPEND);
		} catch (FileNotFoundException e1) {
			System.out.println("File not found");
		}
		boolean finished = false;
		FileInputStream reader = null;
		Scanner sc;
		try {
			reader= ContextPass.getGroceryContext().openFileInput(GROCERY_NAME);
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
		sc = new Scanner(reader);
		while (finished == false) {
			try {
				String[] data = new String[3];
				data[0] = sc.nextLine();
				data[1] = sc.nextLine();
				data[2] = sc.nextLine();
				groceryList.add(data);
			} catch (NoSuchElementException e) {
				finished = true;
			}
		}
		return groceryList;
	}
	
	
	public static void addGroceryItem(String[] data){
		try {
			ArrayList<String[]> list = getGroceryList();
			FileOutputStream fileOutput = ContextPass.getGroceryContext().openFileOutput(GROCERY_NAME, Activity.MODE_APPEND);
			PrintStream print = new PrintStream(fileOutput);
			for(int i = 0; i < list.size(); i++){
				for(int j = 0; j < list.get(i).length; j++){
					print.println(list.get(i)[j]);
				}
			}
			for (int i = 0; i < data.length; i++) {
				print.println(data[i]);
			}
			print.close();
			fileOutput.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("IOException");
		}
	}
	
	public static void deleteGroceryItem(String[] data){
		ArrayList<String[]> groceryList = getGroceryList();
		boolean removed = false;
		try {
			FileOutputStream fileOutput = ContextPass.getGroceryContext().openFileOutput(GROCERY_NAME, Activity.MODE_PRIVATE);
			PrintStream print = new PrintStream(fileOutput);
			for (int i = 0; i < groceryList.size(); i++) {
				if (data[0].equals(groceryList.get(i)[0])
						&& data[1].equals(groceryList.get(i)[1])
						&& data[2].equals(groceryList.get(i)[2]) && !removed) {
					removed = true;
				} else{
					for (int j = 0; j < groceryList.get(i).length; j++) {
						print.println(groceryList.get(i)[j]);
					}
				}
			}
			print.close();
			fileOutput.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("IOException");
		}
	}
	public static void updateGroceryItem(String[] data, int completion){
		ArrayList<String[]> groceryList = getGroceryList();
		boolean updated = false;
		try {
			FileOutputStream fileOutput = ContextPass.getGroceryContext().openFileOutput(GROCERY_NAME, Activity.MODE_PRIVATE);
			PrintStream print = new PrintStream(fileOutput);
			for (int i = 0; i < groceryList.size(); i++) {
				if (data[0].equals(groceryList.get(i)[0])
						&& data[1].equals(groceryList.get(i)[1])
						&& data[2].equals(groceryList.get(i)[2]) && !updated) {	
					for(int j = 0; j < groceryList.get(i).length - 1; j++){
						print.println(groceryList.get(i)[j]);
					}
					//System.out.println("Replaced");
					updated = true;
					print.println(completion);
				} else{
					for (int j = 0; j < groceryList.get(i).length; j++) {
						print.println(groceryList.get(i)[j]);
					}
				}
			}
			print.close();
			fileOutput.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("IOException");
		}
	}
	public static void clearGroceryList(){
		try {
			FileOutputStream fileOutput = ContextPass.getGroceryContext().openFileOutput(GROCERY_NAME, Activity.MODE_PRIVATE);
			fileOutput.close();
		} catch (IOException e) {
			System.out.println("IOException");
		}
	}
}
