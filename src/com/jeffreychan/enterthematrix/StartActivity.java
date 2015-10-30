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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends Activity implements OnClickListener, OnItemSelectedListener {

	TextView createTextView, editTextView, calculationTextView;
	Button createButton, matrixA, matrixB, matrixC, matrixD, calculateButton, matrixE, scalarButton;
	Spinner rowMenu, columnMenu, firstMatrix, operator, secondMatrix;
	int row, column, width, height, op, first, second, viewHeights;
	RelativeLayout mainLayout;
	boolean isStarting = true;
	float startHeight;
	int saveButtonWidths;
	final int NUM_MATRICES = 5;
	float scalar = 1f;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_start);
		getWindow().getDecorView().setBackgroundColor(Color.rgb(34, 177, 76));
		
		// Get dimensions of screen and set up display variables
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;		
		startHeight = height/50;
		viewHeights = height/13;
		
		saveButtonWidths = width/2;
		
		// ---------------------------------------------
		// Define all the views
		// ---------------------------------------------
		mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
		
		createTextView = new TextView(this);
		createTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, viewHeights));
		createTextView.setX(0);
		createTextView.setY(startHeight);
		createTextView.setTextSize(30f);
		createTextView.setText(" Create new matrix:");
		
		editTextView = new TextView(this);
		editTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, viewHeights));
		editTextView.setX(0);
		editTextView.setTextSize(30f);
		editTextView.setText(" Edit saved matrices:");
		
		calculationTextView = new TextView(this);
		calculationTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, viewHeights));
		calculationTextView.setX(0);
		calculationTextView.setTextSize(30f);
		calculationTextView.setText(" Matrix Calculations:");
		
		rowMenu = new Spinner(this);
		rowMenu.setId(0);
		rowMenu.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, viewHeights));
		rowMenu.setX(0);
		ArrayAdapter<CharSequence> rowAdapter = ArrayAdapter.createFromResource(this, R.array.rowArray, R.layout.spinner_item_left);
		rowAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		rowMenu.setAdapter(rowAdapter);
		rowMenu.setOnItemSelectedListener(this);
		rowMenu.setSelection(2);

		columnMenu = new Spinner(this);
		columnMenu.setId(1);
		columnMenu.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, viewHeights));
		columnMenu.setX(0);
		ArrayAdapter<CharSequence> columnAdapter = ArrayAdapter.createFromResource(this, R.array.columnArray, R.layout.spinner_item_left);
		columnAdapter.setDropDownViewResource(R.layout.spinner_item);
		columnMenu.setAdapter(columnAdapter);
		columnMenu.setOnItemSelectedListener(this);
		columnMenu.setSelection(2);
		
		firstMatrix = new Spinner(this);
		firstMatrix.setId(2);
		firstMatrix.setLayoutParams(new LayoutParams(width/3, LayoutParams.WRAP_CONTENT));
		firstMatrix.setX(5);
		ArrayAdapter<CharSequence> firstAdapter = ArrayAdapter.createFromResource(this, R.array.matrixArray, R.layout.spinner_item);
		rowAdapter.setDropDownViewResource(R.layout.spinner_item);
		firstMatrix.setAdapter(firstAdapter);
		firstMatrix.setOnItemSelectedListener(this);
		firstMatrix.setSelection(0);
				
		operator = new Spinner(this);
		operator.setId(3);
		operator.setLayoutParams(new LayoutParams(width/3, LayoutParams.WRAP_CONTENT));
		operator.setX(2 + width/3);
		ArrayAdapter<CharSequence> opAdapter = ArrayAdapter.createFromResource(this, R.array.symbolArray, R.layout.spinner_item);
		rowAdapter.setDropDownViewResource(R.layout.spinner_item);
		operator.setAdapter(opAdapter);
		operator.setOnItemSelectedListener(this);
		operator.setSelection(0);

		secondMatrix = new Spinner(this);
		secondMatrix.setId(4);
		secondMatrix.setLayoutParams(new LayoutParams(width/3, LayoutParams.WRAP_CONTENT));
		secondMatrix.setX(2 + 2*width/3);
		ArrayAdapter<CharSequence> secondAdapter = ArrayAdapter.createFromResource(this, R.array.matrixArray, R.layout.spinner_item);
		rowAdapter.setDropDownViewResource(R.layout.spinner_item);
		secondMatrix.setAdapter(secondAdapter);
		secondMatrix.setOnItemSelectedListener(this);
		secondMatrix.setSelection(1);

		createButton = new Button(this);
		createButton.setText("Create");
		createButton.setId(0);
		createButton.setLayoutParams(new LayoutParams(width/3, viewHeights));
		createButton.setX(width/2 - ((width/3)/2));
		createButton.setOnClickListener(this);
		
		calculateButton = new Button(this);
		calculateButton.setText("Calculate");
		calculateButton.setId(5);
		calculateButton.setLayoutParams(new LayoutParams(width/3, viewHeights));
		calculateButton.setX(width/2 - ((width/3)/2));
		calculateButton.setOnClickListener(this);
		
		matrixA = new Button(this);
		matrixA.setText("Matrix A");
		matrixA.setId(1);
		matrixA.setLayoutParams(new LayoutParams(saveButtonWidths, viewHeights));
		matrixA.setX(0);
		matrixA.setOnClickListener(this);
		
		matrixB = new Button(this);
		matrixB.setText("Matrix B");
		matrixB.setId(2);
		matrixB.setLayoutParams(new LayoutParams(saveButtonWidths, viewHeights));
		matrixB.setOnClickListener(this);
		
		matrixC = new Button(this);
		matrixC.setText("Matrix C");
		matrixC.setId(3);
		matrixC.setLayoutParams(new LayoutParams(saveButtonWidths, viewHeights));
		matrixC.setX(0);
		matrixC.setOnClickListener(this);
		
		matrixD = new Button(this);
		matrixD.setText("Matrix D");
		matrixD.setId(4);
		matrixD.setLayoutParams(new LayoutParams(saveButtonWidths, viewHeights));
		matrixD.setOnClickListener(this);
		
		matrixE = new Button(this);
		matrixE.setText("Matrix E");
		matrixE.setId(6);
		matrixA.setX(0);
		matrixE.setLayoutParams(new LayoutParams(saveButtonWidths, viewHeights));
		matrixE.setOnClickListener(this);
		
		scalarButton = new Button(this);
		scalarButton.setText("λ");
		scalarButton.setId(7);
		scalarButton.setLayoutParams(new LayoutParams(saveButtonWidths, viewHeights));
		scalarButton.setOnClickListener(this);
		
		// Add these views to the relative layout
		mainLayout.addView(rowMenu);
		mainLayout.addView(columnMenu);
		mainLayout.addView(firstMatrix);
		mainLayout.addView(operator);
		mainLayout.addView(secondMatrix);
		mainLayout.addView(createTextView);
		mainLayout.addView(createButton);
		mainLayout.addView(editTextView);
		mainLayout.addView(matrixA);
		mainLayout.addView(matrixB);
		mainLayout.addView(matrixC);
		mainLayout.addView(matrixD);
		mainLayout.addView(matrixE);
		mainLayout.addView(scalarButton);
		mainLayout.addView(calculationTextView);
		mainLayout.addView(calculateButton);
		
		// ------------------------------------------------------------------------------------------
		// If stored matrices are empty (fresh install), then fill them with 3x3 zero matrices
		// ------------------------------------------------------------------------------------------
		StringBuilder sb = new StringBuilder();
    	
    	for (int i = 0; i < NUM_MATRICES - 1; i++){
	    	for (int j = 0; j < NUM_MATRICES - 1; j++){
	    		sb.append("0,");
	    	}
    	}
    	
		SharedPreferences prefs = this.getSharedPreferences("matrices", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();

		if (prefs.getString("A", null) == null){
			editor.putString("A", sb.toString());
			editor.putInt("rowA", 3);
			editor.putInt("columnA", 3);
		}
		if (prefs.getString("B", null) == null){
			editor.putString("B", sb.toString());
			editor.putInt("rowB", 3);
			editor.putInt("columnB", 3);
		}
		if (prefs.getString("C", null) == null){
			editor.putString("C", sb.toString());
			editor.putInt("rowC", 3);
			editor.putInt("columnC", 3);
		}
		if (prefs.getString("D", null) == null){
			editor.putString("D", sb.toString());
			editor.putInt("rowD", 3);
			editor.putInt("columnD", 3);
		}
		if (prefs.getString("E", null) == null){
			editor.putString("E", sb.toString());
			editor.putInt("rowE", 3);
			editor.putInt("columnE", 3);
		}
		editor.commit();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (isStarting){
			rowMenu.setY(startHeight + createTextView.getHeight() + 5);
			columnMenu.setY(rowMenu.getY() + rowMenu.getHeight() + 5);	
			createButton.setY(columnMenu.getY() + columnMenu.getHeight() + 5);
			
			editTextView.setY(createButton.getY() + createButton.getHeight() + 10);

			matrixB.setX(matrixA.getWidth());
			matrixD.setX(matrixA.getWidth());
			scalarButton.setX(matrixA.getWidth());

			matrixA.setY(editTextView.getY() + editTextView.getHeight() + 5);
			matrixB.setY(editTextView.getY() + editTextView.getHeight() + 5);
			matrixC.setY(matrixA.getY() + matrixA.getHeight() + 5);
			matrixD.setY(matrixB.getY() + matrixB.getHeight() + 5);
			matrixE.setY(matrixC.getY() + matrixC.getHeight() + 5);
			scalarButton.setY(matrixD.getY() + matrixD.getHeight() + 5);

			calculationTextView.setY(matrixE.getY() + matrixE.getHeight() + 10);
			
			firstMatrix.setY(calculationTextView.getY() + calculationTextView.getHeight() + 5);
			operator.setY(calculationTextView.getY() + calculationTextView.getHeight() + 5);
			secondMatrix.setY(calculationTextView.getY() + calculationTextView.getHeight() + 5);

			calculateButton.setY(height - 3*calculateButton.getHeight()/2);

			isStarting = false;
		}
		
		SharedPreferences prefs = this.getSharedPreferences("matrices", Context.MODE_PRIVATE);
		int displayRowA = prefs.getInt("rowA", 0); //0 is the default value
		int displayColumnA = prefs.getInt("columnA", 0); //0 is the default value
		matrixA.setText("Matrix A (" + displayRowA + "x" + displayColumnA + ")");
		
		int displayRowB = prefs.getInt("rowB", 0); //0 is the default value
		int displayColumnB = prefs.getInt("columnB", 0); //0 is the default value
		matrixB.setText("Matrix B (" + displayRowB + "x" + displayColumnB + ")");
		
		int displayRowC = prefs.getInt("rowC", 0); //0 is the default value
		int displayColumnC = prefs.getInt("columnC", 0); //0 is the default value
		matrixC.setText("Matrix C (" + displayRowC + "x" + displayColumnC + ")");
		
		int displayRowD = prefs.getInt("rowD", 0); //0 is the default value
		int displayColumnD = prefs.getInt("columnD", 0); //0 is the default value
		matrixD.setText("Matrix D (" + displayRowD + "x" + displayColumnD + ")");
		
		int displayRowE = prefs.getInt("rowE", 0); //0 is the default value
		int displayColumnE = prefs.getInt("columnE", 0); //0 is the default value
		matrixE.setText("Matrix E (" + displayRowE + "x" + displayColumnE + ")");
		
		float displayScalar = prefs.getFloat("scalar", 1f); //0 is the default value
		if (Math.abs(displayScalar % 1) < 1E-6){
			scalarButton.setText("c = " + (int) displayScalar);
		}
		else {
			scalarButton.setText("c = " + displayScalar);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	private void startNewMatrixActivity(String letter){
		String rowName = "row" + letter;
		String columnName = "column" + letter;

		SharedPreferences prefs = this.getSharedPreferences("matrices", Context.MODE_PRIVATE);
		String storedMatrix = prefs.getString(letter, null); //null is the default value
		int storedRows = prefs.getInt(rowName, 0); //0 is the default value
		int storedColumns = prefs.getInt(columnName, 0); //0 is the default value
		Intent intent = new Intent(this, MatrixActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("row", storedRows); 
		bundle.putInt("column", storedColumns); 
		bundle.putString("loadMatrix", storedMatrix);
		intent.putExtras(bundle); 
		startActivity(intent);
	}
	
	private void parseMatrices(double[][][] matrices, String letter){
		String rowName = "row" + letter;
		String columnName = "column" + letter;
		int matrixNum = 0;
		
		switch(letter){
		case "A":
			matrixNum = 0;
			break;
		case "B":
			matrixNum = 1;
			break;
		case "C":
			matrixNum = 2;
			break;
		case "D":
			matrixNum = 3;
			break;
		case "E":
			matrixNum = 4;
			break;
		}
		
		SharedPreferences prefs = this.getSharedPreferences("matrices", Context.MODE_PRIVATE);
		String storedMatrix = prefs.getString(letter, null); //0 is the default value
		String[] theMatrix = storedMatrix.split(",");
		int storedRows = prefs.getInt(rowName, 0); //0 is the default value
		int storedColumns = prefs.getInt(columnName, 0); //0 is the default value
		matrices[matrixNum] = new double[storedRows][storedColumns];
		
		for (int i = 0; i < storedRows; i++){
			for (int j = 0; j < storedColumns; j++){
				if ((((storedColumns)*i) + j) < theMatrix.length && !theMatrix[((storedColumns)*i) + j].contains("/")){
					try {
						matrices[matrixNum][i][j] = Double.parseDouble(theMatrix[((storedColumns)*i) + j]);
					} catch (NumberFormatException e){
						// Leave element as 0
					}
				}
				else {
					String[] s = theMatrix[((storedColumns)*i) + j].split("/");
					try {
						matrices[matrixNum][i][j] = Double.parseDouble(s[0]) / Double.parseDouble(s[1]);
					} catch (NumberFormatException e){
						// Leave element as 0
					}
				}
				
			}
		}
	}
	
	public void saveScalar(){
		SharedPreferences prefs = this.getSharedPreferences("matrices", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putFloat("scalar", (float) scalar);
		editor.commit();
	}
	
	public float getScalar(){
		SharedPreferences prefs = this.getSharedPreferences("matrices", Context.MODE_PRIVATE);
		return prefs.getFloat("scalar", 1f);
	}
	
	@Override
	public void onClick(View v) {
		
	    if (v.getId() == createButton.getId()){
	    	
	    	// Start new activity with blank matrix

	    	StringBuilder matrix = new StringBuilder();
	    	
	    	for (int i = 0; i < row; i++){
		    	for (int j = 0; j < column; j++){
		    		matrix.append("0,");
		    	}
	    	}
	    	
			Intent intent = new Intent(this, MatrixActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("row", row); 
			bundle.putInt("column", column); 
			bundle.putString("loadMatrix", null);
			intent.putExtras(bundle); 
			startActivity(intent);
		}
	    else if (v.getId() == matrixA.getId()){
	    	this.startNewMatrixActivity("A");	// Edit Matrix A
		}
	    else if (v.getId() == matrixB.getId()){
	    	this.startNewMatrixActivity("B");	// Edit Matrix B
		}
	    else if (v.getId() == matrixC.getId()){
	    	this.startNewMatrixActivity("C");	// Edit Matrix C
		}
	    else if (v.getId() == matrixD.getId()){
	    	this.startNewMatrixActivity("D");	// Edit Matrix D
		}
	    else if (v.getId() == matrixE.getId()){
	    	this.startNewMatrixActivity("E");	// Edit Matrix D
		}
	    else if (v.getId() == scalarButton.getId()){
	    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

	    	alert.setTitle("Edit Scalar Constant");
	    	alert.setMessage("Enter a value for c:");

	    	final EditText input = new EditText(this);
	    	input.setGravity(Gravity.CENTER);
	    	input.setPadding(0, 0, 0, 0);
	    	input.setBackgroundColor(Color.WHITE);
	    	input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
	    	alert.setView(input);

	    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    		public void onClick(DialogInterface dialog, int whichButton) {
	    			if (!input.getText().toString().equals("")){
	    				try{
			    			scalar = Float.parseFloat(input.getText().toString());
						}
						catch(NumberFormatException e){
							scalar = getScalar();
						}
		    			saveScalar();
	    			}
	    		}
	    	});

	    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	    		public void onClick(DialogInterface dialog, int whichButton) {
	    			dialog.cancel();
	    		}
	    	});

	    	alert.show();
	    }
	    else if (v.getId() == calculateButton.getId()){
	    	boolean isReady = true;
	    	double[][][] matrices = new double[NUM_MATRICES][][];
	    	
	    	// Load all four matrices	    	
	    	String[] letters = {"A", "B", "C", "D", "E"};
	    	for (String s : letters){
	    		this.parseMatrices(matrices, s);
	    	}
	    	
			int firstRow = matrices[first].length;
			int firstColumn = matrices[first][0].length;
			int secondRow = matrices[second].length;
			int secondColumn = matrices[second][0].length;
			
			int resultRow = firstRow;
			int resultColumn = firstColumn;
			double[][] resultMatrix = null;

			
			// ---------------------------------------------------------
			// Look at op code and perform the appropriate calculation
			// ---------------------------------------------------------
			
			// Adding matrices
			if (op == 0){
				if (firstRow == secondRow && firstColumn == secondColumn){
					resultMatrix = MatrixOperations.addMatrix(matrices[first], matrices[second]);
				}
				else {
					Toast message = Toast.makeText(getApplicationContext(), "Cannot add with these dimensions", Toast.LENGTH_SHORT);
					message.show();
					isReady = false;
				}
			}

			// Subtracting matrices
			else if (op == 1){
				if (firstRow == secondRow && firstColumn == secondColumn){
					resultMatrix = MatrixOperations.subtractMatrix(matrices[first], matrices[second]);
				}
				else {
					Toast message = Toast.makeText(getApplicationContext(), "Cannot subtract with these dimensions", Toast.LENGTH_SHORT);
					message.show();
					isReady = false;
				}
			}
			
			// Multiplying matrices
			else if (op == 2){
				if (firstColumn == secondRow){
					resultColumn = secondColumn;
					resultMatrix = MatrixOperations.multiplyMatrix(matrices[first], matrices[second]);
				}
				else {
					Toast message = Toast.makeText(getApplicationContext(), "Cannot multiply with these dimensions", Toast.LENGTH_SHORT);
					message.show();
					isReady = false;
				}
			}

			// Transposing matrices
			else if (op == 3){
				
				// Reverse the rows and columns to transpose
				
				resultColumn = firstRow;
				resultRow = firstColumn;
				resultMatrix = MatrixOperations.transposeMatrix(matrices[first]);
			}
			
			// Inverting matrices
			else if (op == 4){
				
				if (firstRow == firstColumn){
					boolean isInvertible = true;
					resultMatrix = new double[firstRow][2*firstColumn];
					
					// The first partition is the matrix to be inverted
					for (int i = 0; i < matrices[first].length; i++){
						for (int j = 0; j < matrices[first][0].length; j++){
							resultMatrix[i][j] = matrices[first][i][j];
						}
					}
					
					// The second partition is the identity matrix
					for (int i = 0; i < matrices[first].length; i++){
						for (int j = matrices[first][0].length; j < 2*matrices[first][0].length; j++){
							if (i == (j - matrices[first][0].length)){
								resultMatrix[i][j] = 1;
							}
							else {
								resultMatrix[i][j] = 0;
							}
						}
					}
					
					resultMatrix = MatrixOperations.reduceMatrix(firstRow, 2*firstColumn, resultMatrix);
					
					// Check for the identity matrix in the first partition
					for (int i = 0; i < matrices[first].length; i++){
						for (int j = 0; j < matrices[first][0].length; j++){
							if (i == j && resultMatrix[i][j] != 1){
								isInvertible = false;
							}
						}
					}
					
					if (isInvertible){
						
						// Grab the second partition and store it as resultMatrix
						double[][] tempMatrix = new double[firstRow][firstColumn];
						for (int i = 0; i < matrices[first].length; i++){
							for (int j = 0; j < matrices[first][0].length; j++){
								tempMatrix[i][j] = resultMatrix[i][j + firstColumn];
							}
						}
						resultMatrix = tempMatrix;
						
					}
					else {						
						Toast message = Toast.makeText(getApplicationContext(), "This matrix is not invertible", Toast.LENGTH_SHORT);
						message.show();
						isReady = false;
					}
					
				}
				else {
					Toast message = Toast.makeText(getApplicationContext(), "Cannot invert with these dimensions", Toast.LENGTH_SHORT);
					message.show();
					isReady = false;
				}
			}
			
			// Multiply by constant lambda
			else if (op == 5){
				scalar = getScalar();
				resultMatrix = new double[resultRow][resultColumn];
				resultMatrix = MatrixOperations.multiplyScalar(resultRow, resultColumn, matrices[first], scalar);
			}

			// Basis for null space
			else if (op == 6){
				resultMatrix = new double[resultRow][resultColumn];
				resultMatrix = MatrixOperations.reduceMatrix(resultRow, resultColumn, resultMatrix);
				// TODO: Determine free variables and get bases
			}
			
			// Bases for column space
			else if (op == 7){
				resultMatrix = MatrixOperations.calculateColumnSpace(matrices[first], resultRow, resultColumn);
			}
			
			// Bases for row space
			else if (op == 8){
				double[][] tempMatrix = new double[resultRow][resultColumn];
				tempMatrix = MatrixOperations.reduceMatrix(resultRow, resultColumn, matrices[first]);
				
				int[] pivotRows = new int[resultRow];
				int row = 0;
				
				for (int i = 0; i < resultRow; i++){
					int pivotCounter = 0;
					for (int j = 0; j < resultRow; j++){
						if (tempMatrix[i][j] == 1){
							pivotCounter++;
						}
					}
					if (pivotCounter == 1){
						pivotRows[row] = i;
						row++;
					}
				}
				
				tempMatrix = MatrixOperations.transposeMatrix(tempMatrix);
				
				resultRow = resultColumn;
				resultColumn = row;
				resultMatrix = new double[resultRow][resultColumn];
				
				for (int i = 0; i < resultRow; i++){
					for (int j = 0; j < resultColumn; j++){
						resultMatrix[i][j] = tempMatrix[i][j];
					}
				}
			}
			
			// If there is no problem with the dimensions, display the calculation in a new activity
			if (isReady){
				String matrixString = MatrixOperations.matrixToString(resultRow, resultColumn, resultMatrix);
				
				Intent intent = new Intent(this, DisplayActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("row", resultRow); 
				bundle.putInt("column", resultColumn); 
				bundle.putString("matrix", matrixString);
				intent.putExtras(bundle); 
				startActivity(intent);
			}
	    }		
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		if (parent.getId() == rowMenu.getId()){
			row = pos + 1;
		}
		else if (parent.getId() == columnMenu.getId()){
			column = pos + 1;
		}
		else if (parent.getId() == operator.getId()){
			if (pos >= 3){
				secondMatrix.setVisibility(View.INVISIBLE);
			}
			else{
				secondMatrix.setVisibility(View.VISIBLE);
			}
			
			op = pos;
		}
		else if (parent.getId() == firstMatrix.getId()){
			first = pos;
		}
		else if (parent.getId() == secondMatrix.getId()){			
			second = pos;
		}
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// Do nothing
	}
}
