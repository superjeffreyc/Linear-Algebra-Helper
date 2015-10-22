package com.jeffreychan.enterthematrix;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.InputType;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MatrixActivity extends Activity implements OnClickListener {

	Button reduceButton, saveA, saveB, saveC, saveD;
	private int rows, columns, width, height;
	private double[][] matrix;
	EditText[][] myEditTextArray;
	String loadMatrix;
	String[] splitMatrix = null;
	RelativeLayout mainLayout;
	boolean isStarting = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_matrix);
		getWindow().getDecorView().setBackgroundColor(Color.rgb(34, 177, 76));
		
		// Grab values from the previous activity
		Bundle bundle = getIntent().getExtras();
		rows = bundle.getInt("row");
		columns = bundle.getInt("column");
		loadMatrix = bundle.getString("loadMatrix");
		
		if (loadMatrix != null){
			splitMatrix = loadMatrix.split(",");
		}

		mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
		myEditTextArray = new EditText[rows][columns];
		
		// Get dimensions of the screen
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;

		// Create the proper number of TextViews and arrange them according to the number of rows and columns
		for (int i = 0; i < rows; i++){
			for (int j = 0; j < columns; j++){
				myEditTextArray[i][j] = new EditText(this);
				myEditTextArray[i][j].setLayoutParams(new LayoutParams(width/columns - 5, height/(rows+2) - 5));
				if (columns == 1){
					myEditTextArray[i][j].setTextSize(80f);
				}
				else {
					myEditTextArray[i][j].setTextSize(110f/columns);
				}
				myEditTextArray[i][j].setGravity(Gravity.CENTER);
				myEditTextArray[i][j].setPadding(0, 0, 0, 0);
				myEditTextArray[i][j].setBackgroundColor(Color.WHITE);
				myEditTextArray[i][j].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
				myEditTextArray[i][j].setX(j * width/columns);
				myEditTextArray[i][j].setY(i * height/(rows+2));
				
				// If not creating a new matrix
				if (splitMatrix != null){
					if ((((columns)*i) + j) < splitMatrix.length){
						myEditTextArray[i][j].setText(splitMatrix[((columns)*i) + j]);
					}
					else {
						myEditTextArray[i][j].setText("");
					}
				}
				mainLayout.addView(myEditTextArray[i][j]);

				// Allow user to easily move from left to right using the next button
				if (i == rows - 1 && j == columns - 1){
					myEditTextArray[i][j].setImeOptions(EditorInfo.IME_ACTION_DONE);
				}
				else {
					myEditTextArray[i][j].setImeOptions(EditorInfo.IME_ACTION_NEXT);

				}				
			}
		}
		
		// Create reduce and save buttons
		reduceButton = new Button(this);
		reduceButton.setText("Reduce");
		reduceButton.setId(0);
		reduceButton.setLayoutParams(new LayoutParams(width/3, LayoutParams.WRAP_CONTENT));
		reduceButton.setX(width/2 - ((width/3)/2));
		reduceButton.setY(rows * height/(rows+2));
		reduceButton.setOnClickListener(this);
		
		saveA = new Button(this);
		saveA.setText("Save to A");
		saveA.setLayoutParams(new LayoutParams(width/4, LayoutParams.WRAP_CONTENT));
		saveA.setX(0);
		saveA.setY(4*height/5);
		saveA.setOnClickListener(this);
		saveA.setId(1);
				
		saveB = new Button(this);
		saveB.setText("Save to B");
		saveB.setLayoutParams(new LayoutParams(width/4, LayoutParams.WRAP_CONTENT));
		saveB.setX(width/4);
		saveB.setY(4*height/5);
		saveB.setOnClickListener(this);
		saveB.setId(2);
		
		saveC = new Button(this);
		saveC.setText("Save to C");
		saveC.setLayoutParams(new LayoutParams(width/4, LayoutParams.WRAP_CONTENT));
		saveC.setX(width/2);
		saveC.setY(4*height/5);
		saveC.setOnClickListener(this);
		saveC.setId(3);
		
		saveD = new Button(this);
		saveD.setText("Save to D");
		saveD.setLayoutParams(new LayoutParams(width/4, LayoutParams.WRAP_CONTENT));
		saveD.setX(3*width/4);
		saveD.setY(4*height/5);
		saveD.setOnClickListener(this);
		saveD.setId(4);
		
		// Add the buttons to the relative layout
		mainLayout.addView(reduceButton);
		mainLayout.addView(saveA);
		mainLayout.addView(saveB);
		mainLayout.addView(saveC);
		mainLayout.addView(saveD);


	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (isStarting){
			saveA.setY(height - saveA.getHeight());
			saveB.setY(height - saveA.getHeight());
			saveC.setY(height - saveA.getHeight());
			saveD.setY(height - saveA.getHeight());
			isStarting = false;
		}
	}
	
	public static String reduceMatrix(int rows, int columns, double[][] matrix) {

		// Check for zero matrix
		boolean isAllZero = true;			
		for (int i = 0; i < rows; i++){
			for (int j = 0; j < columns; j++){
				if (matrix[i][j] != 0){
					isAllZero = false;
				}
			}
		}

		if (!isAllZero){

			int currentRowIndex = 0;	// Current row we are on
			
			// MAIN LOOP reducing by column
			for (int j = 0; j < columns; j++) {

				boolean columnIsAllZero = true;
				// Check that the current column j is all zero or not
				for (int i = currentRowIndex; i < rows; i++){
					if (matrix[i][j] != 0) {
						columnIsAllZero = false;
					}
				}

				if (!columnIsAllZero) {

					// Bubble sort the rows based on the current column, ignoring previously completed rows
					for (int a = currentRowIndex; a < rows - 1; a++) {
						for (int i = currentRowIndex; i < rows - 1; i++) {
							if (matrix[i][j] < matrix[i+1][j] && matrix[i+1][j] != 0) {
								double[] temp = matrix[i];
								matrix[i] = matrix[i+1];
								matrix[i+1] = temp;
							}
						}
					}

					// Add a multiple of one row to another row
					for (int i = 0; i < rows; i++) {
						if (i != currentRowIndex && matrix[i][j] != 0 && matrix[currentRowIndex][j] != 0) {
							double factor = -1 * (matrix[i][j] / matrix[currentRowIndex][j]);

							for (int k = 0; k < columns; k++){
								matrix[i][k] += (factor * matrix[currentRowIndex][k]);
								if (Math.abs(matrix[i][k]) % 1 < 0.0000000001) {
									matrix[i][k] = (int) matrix[i][k];
								}
							}
						}
					}

					currentRowIndex++;
				}
				else {
					// we're done with the current column
				}

			}	// End of MAIN LOOP

			// Multiply the rows by a non-zero constant to get the leading entries to equal 1
			double simplify;
			for (int i = 0; i < rows; i++) {
				boolean isZero = true;
				simplify = 1;
				for (int j = 0; j < columns; j++) {
					if (isZero && (matrix[i][j] != 0)) {
						isZero = false;
						if (matrix[i][j] != 1) {
							simplify = matrix[i][j];
						}
					}
				}

				for (int j = 0; j < columns; j++) {
					matrix[i][j] /= simplify;
				}

			}


		}			
		else {
			// we're done with the entire matrix
		}

		return MatrixActivity.matrixToString(rows, columns, matrix);

	}

	public static String matrixToString(int rows, int columns, double[][] matrix){
		
		// Create a string of all the elements to pass to the next activity

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++){
				if (matrix[i][j] == 0){
					// If the element is 0, append 0
					sb.append("0,");
				}
				else if (Math.abs(matrix[i][j]) % 1 < 0.0000000001) {		
					// If the remainder is nearly 0, remove the decimal places and append
					sb.append((int) matrix[i][j] + ",");
				}
				else if (Math.abs(matrix[i][j]) % 1 > 0.9999999999 && matrix[i][j] > 0) {
					// If the element is positive and remainder is nearly 1, round up and append
					sb.append((int) Math.ceil(matrix[i][j]) + ",");
				}
				else if (Math.abs(matrix[i][j]) % 1 > 0.9999999999 && matrix[i][j] < 0) {
					// If the element is negative and remainder is nearly 1, round down and append
					sb.append((int) Math.floor(matrix[i][j]) + ",");
				}
				else{
					// The element is not a whole number

					boolean done = false;
					double n = 2.0;
					final int MAX_DENOM = 9000;
					
					// Finds possible denominators up to 9000 and append the appropriate fraction
					while (!done && n <= MAX_DENOM) {
						if ( (Math.abs( matrix[i][j]*1000000.0/1000000.0   ) / (1.0 /n) )  % 1 < 0.0000000001 || (Math.abs( matrix[i][j]*1000000.0/1000000.0   ) / (1.0 /n) )  % 1 > 0.9999999999)  {
							done = true;
							sb.append( (int) (Math.round(matrix[i][j]*1000000.0/1000000.0   / (1.0/n)))  + "/" + (int) n + ",");
						}

						n++;
					}

					// If a denominator cannot be found, just append the number as is
					if (!done) {
						sb.append(Math.round(matrix[i][j]*1000000.0/1000000.0) + ",");
					}
				}


			}

		}
		
		return sb.toString();
	}
	
	private String editTextToString(){
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < rows; i++){
	    	for (int j = 0; j < columns; j++){
	    		if (myEditTextArray[i][j].getText().toString().equals("")){
	    			sb.append("0,");
	    		}
	    		else{
	    			sb.append(myEditTextArray[i][j].getText().toString() + ",");
	    		}
	    	}
    	}
		return sb.toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.matrix, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		int id = item.getItemId();
		
		if (id == R.id.clearAll) {
			AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
			saveDialog.setTitle("Clear All");
			saveDialog.setMessage("Clear all entries?");
			saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			    	for (int i = 0; i < rows; i++){
						for (int j = 0; j < columns; j++){
							myEditTextArray[i][j].setText("");
						}
					}
			    	myEditTextArray[0][0].requestFocus();
			    }
			});
			saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			        dialog.cancel();
			    }
			});
			saveDialog.show();
		}
		else if (id == R.id.clearZero) {
			AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
			saveDialog.setTitle("Clear Zero");
			saveDialog.setMessage("Clear only zero entries?");
			saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			    	for (int i = 0; i < rows; i++){
						for (int j = 0; j < columns; j++){
							if (myEditTextArray[i][j].getText().toString().equals("0")){
								myEditTextArray[i][j].setText("");
							}
						}
					}
			    	myEditTextArray[0][0].requestFocus();
			    }
			});
			saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			        dialog.cancel();
			    }
			});
			saveDialog.show();
		}
		else if (id == R.id.insertIdentity) {
			AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
			saveDialog.setTitle("Identity Matrix");
			saveDialog.setMessage("Insert the identity matrix?");
			saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			    	if (rows != columns){
			    		Toast savedToast = Toast.makeText(getApplicationContext(), "Matrix must be square", Toast.LENGTH_SHORT);
						savedToast.show();
			    	}
			    	else {
				    	for (int i = 0; i < rows; i++){
							for (int j = 0; j < columns; j++){
								if (i == j){
									myEditTextArray[i][j].setText("1");
								}
								else {
									myEditTextArray[i][j].setText("0");
								}
							}
						}
			    	}
			    	myEditTextArray[0][0].requestFocus();
			    }
			});
			saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			        dialog.cancel();
			    }
			});
			saveDialog.show();
		}
		return super.onOptionsItemSelected(item);
	}

	private void saveMatrix(String letter){
		String rowName = "row" + letter;
		String columnName = "column" + letter;
		String matrixString = this.editTextToString();

		SharedPreferences prefs = this.getSharedPreferences("matrices", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString(letter, matrixString);
		editor.putInt(rowName, rows);
		editor.putInt(columnName, columns);
		editor.commit();
		
		Toast savedToast = Toast.makeText(getApplicationContext(), "Saved as Matrix " + letter + "!", Toast.LENGTH_SHORT);
    	savedToast.show();
	}
	
	@Override
	public void onClick(View v) {

		if (v.getId() == reduceButton.getId()){
			
			matrix = new double[rows][columns]; // Set up the matrix

			// Grab the entries from the edit texts
			for (int i = 0; i < rows; i++){
				for (int j = 0; j < columns; j++){
					if (myEditTextArray[i][j].getText().toString().equals("")){
						matrix[i][j] = 0.0;
					}
					else{
						try{
							matrix[i][j] = Double.parseDouble(myEditTextArray[i][j].getText().toString());
						}
						catch(NumberFormatException e){

						}

					}
				}
			}
			
			String matrixString = reduceMatrix(rows, columns, matrix);
			Intent intent = new Intent(this, DisplayActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("matrix", matrixString);
			bundle.putInt("row", rows);
			bundle.putInt("column", columns);
			intent.putExtras(bundle); 
			startActivity(intent);
		}
		else if (v.getId() == saveA.getId()){
			this.saveMatrix("A");
		}
		else if (v.getId() == saveB.getId()){
			this.saveMatrix("B");
		}
		else if (v.getId() == saveC.getId()){
			this.saveMatrix("C");
		}
		else if (v.getId() == saveD.getId()){
			this.saveMatrix("D");
		}
		
		
	}
	
	
	

}
