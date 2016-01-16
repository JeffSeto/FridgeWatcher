package com.other.fridgewater;

import android.content.Context;

public class ContextPass {
	static Context context;
	static Context groceryContext;
	
	//Set Context from Main Activity
	public static void setMainContext(Context context){
		ContextPass.context = context;
	}
	//Get Context from MainActivity
	public static Context getMainContext(){
		return context;
	}
	//Set Context from GroceryList
	public static void setGroceryContext(Context context){
		groceryContext = context;
	}
	//Get Context from GroceryList
	public static Context getGroceryContext(){
		return groceryContext;
	}
}
