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
import android.os.Handler;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.HashMap;
import java.util.Map;

public class StartActivity extends Activity implements OnClickListener, OnItemSelectedListener {

	TextView createTextView, editTextView, calculationTextView;
	Button createButton, matrixA, matrixB, matrixC, matrixD, calculateButton, matrixE, scalarButton;
	Spinner rowMenu, columnMenu, operator;
	Spinner[] matrixMenus = new Spinner[2];
	int row, column, width, height, op, first, second, viewHeights;
	RelativeLayout mainLayout;
	boolean isStarting = true, hasSecondMatrix;
	float startHeight;
	int saveButtonWidths;
	final int NUM_MATRICES = 5;
	float scalar = 1f;
	StringBuilder matrix = new StringBuilder();
	Context context = this;
	Map<Integer, String> map = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_start);
		getWindow().getDecorView().setBackgroundColor(Color.rgb(34, 177, 76));

		AdView mAdView = (AdView) findViewById(R.id.ad_view);
		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.build();
		mAdView.loadAd(adRequest);

		// Get dimensions of screen and set up display variables
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y - AdSize.SMART_BANNER.getHeightInPixels(this);

		startHeight = height / 50;
		viewHeights = height / 13;

		saveButtonWidths = width / 2;

		map.put(0, "A");
		map.put(1, "B");
		map.put(2, "C");
		map.put(3, "D");
		map.put(4, "E");

		// ---------------------------------------------
		// Define all the views
		// ---------------------------------------------
		mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);

		createTextView = new TextView(this);
		createTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, viewHeights));
		createTextView.setX(0);
		createTextView.setY(startHeight);
		createTextView.setTextSize(30f);
		createTextView.setText(R.string.createMatrix);

		editTextView = new TextView(this);
		editTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, viewHeights));
		editTextView.setX(0);
		editTextView.setTextSize(30f);
		editTextView.setText(R.string.editMatrix);

		calculationTextView = new TextView(this);
		calculationTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, viewHeights));
		calculationTextView.setX(0);
		calculationTextView.setTextSize(30f);
		calculationTextView.setText(R.string.calculateMatrix);

		rowMenu = new Spinner(this);
		rowMenu.setId(R.id.rowMenu);
		rowMenu.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, viewHeights));
		rowMenu.setX(0);
		ArrayAdapter<CharSequence> rowAdapter = ArrayAdapter.createFromResource(this, R.array.rowArray, R.layout.spinner_item_left);
		rowAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		rowMenu.setAdapter(rowAdapter);
		rowMenu.setOnItemSelectedListener(this);
		rowMenu.setSelection(2);

		columnMenu = new Spinner(this);
		columnMenu.setId(R.id.columnMenu);
		columnMenu.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, viewHeights));
		columnMenu.setX(0);
		ArrayAdapter<CharSequence> columnAdapter = ArrayAdapter.createFromResource(this, R.array.columnArray, R.layout.spinner_item_left);
		columnAdapter.setDropDownViewResource(R.layout.spinner_item);
		columnMenu.setAdapter(columnAdapter);
		columnMenu.setOnItemSelectedListener(this);
		columnMenu.setSelection(2);

		for (int i = 0; i < 2; i++) {
			matrixMenus[i] = new Spinner(this);
			matrixMenus[i].setId(View.generateViewId());
			matrixMenus[i].setLayoutParams(new LayoutParams(width / 3, LayoutParams.WRAP_CONTENT));
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.matrixArray, R.layout.spinner_item);
			matrixMenus[i].setAdapter(adapter);
			matrixMenus[i].setSelection(i);
			matrixMenus[i].setOnItemSelectedListener(this);

			if (i == 0) matrixMenus[i].setX(5);
			else matrixMenus[i].setX(2 + 2 * width / 3);

			mainLayout.addView(matrixMenus[i]);
		}

		operator = new Spinner(this);
		operator.setId(R.id.operator);
		operator.setLayoutParams(new LayoutParams(width / 3, LayoutParams.WRAP_CONTENT));
		operator.setX(2 + width / 3);
		ArrayAdapter<CharSequence> opAdapter = ArrayAdapter.createFromResource(this, R.array.symbolArray, R.layout.spinner_item);
		rowAdapter.setDropDownViewResource(R.layout.spinner_item);
		operator.setAdapter(opAdapter);
		operator.setOnItemSelectedListener(this);
		operator.setSelection(0);



		createButton = new Button(this);
		createButton.setText(R.string.create);
		createButton.setId(R.id.createButton);
		createButton.setLayoutParams(new LayoutParams(width / 3, viewHeights));
		createButton.setX(width / 2 - ((width / 3) / 2));
		createButton.setOnClickListener(this);

		calculateButton = new Button(this);
		calculateButton.setText(R.string.calculate);
		calculateButton.setId(R.id.calculateButton);
		calculateButton.setLayoutParams(new LayoutParams(width / 3, viewHeights));
		calculateButton.setX(width / 2 - ((width / 3) / 2));
		calculateButton.setOnClickListener(this);

		matrixA = new Button(this);
		matrixA.setText(R.string.matrixA);
		matrixA.setId(R.id.matrixA);
		matrixA.setLayoutParams(new LayoutParams(saveButtonWidths, viewHeights));
		matrixA.setX(0);
		matrixA.setOnClickListener(this);

		matrixB = new Button(this);
		matrixB.setText(R.string.matrixB);
		matrixB.setId(R.id.matrixB);
		matrixB.setLayoutParams(new LayoutParams(saveButtonWidths, viewHeights));
		matrixB.setOnClickListener(this);

		matrixC = new Button(this);
		matrixC.setText(R.string.matrixC);
		matrixC.setId(R.id.matrixC);
		matrixC.setLayoutParams(new LayoutParams(saveButtonWidths, viewHeights));
		matrixC.setX(0);
		matrixC.setOnClickListener(this);

		matrixD = new Button(this);
		matrixD.setText(R.string.matrixD);
		matrixD.setId(R.id.matrixD);
		matrixD.setLayoutParams(new LayoutParams(saveButtonWidths, viewHeights));
		matrixD.setOnClickListener(this);

		matrixE = new Button(this);
		matrixE.setText(R.string.matrixE);
		matrixE.setId(R.id.matrixE);
		matrixA.setX(0);
		matrixE.setLayoutParams(new LayoutParams(saveButtonWidths, viewHeights));
		matrixE.setOnClickListener(this);

		scalarButton = new Button(this);
		scalarButton.setText(R.string.scalar);
		scalarButton.setId(R.id.scalar);
		scalarButton.setLayoutParams(new LayoutParams(saveButtonWidths, viewHeights));
		scalarButton.setOnClickListener(this);

		// Add these views to the relative layout
		mainLayout.addView(rowMenu);
		mainLayout.addView(columnMenu);
		mainLayout.addView(operator);
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

		for (int i = 0; i < NUM_MATRICES - 1; i++) {
			for (int j = 0; j < NUM_MATRICES - 1; j++) {
				sb.append("0,");
			}
		}

		SharedPreferences prefs = this.getSharedPreferences("matrices", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();

		if (prefs.getString("A", null) == null) {
			editor.putString("A", sb.toString());
			editor.putInt("rowA", 3);
			editor.putInt("columnA", 3);
		}
		if (prefs.getString("B", null) == null) {
			editor.putString("B", sb.toString());
			editor.putInt("rowB", 3);
			editor.putInt("columnB", 3);
		}
		if (prefs.getString("C", null) == null) {
			editor.putString("C", sb.toString());
			editor.putInt("rowC", 3);
			editor.putInt("columnC", 3);
		}
		if (prefs.getString("D", null) == null) {
			editor.putString("D", sb.toString());
			editor.putInt("rowD", 3);
			editor.putInt("columnD", 3);
		}
		if (prefs.getString("E", null) == null) {
			editor.putString("E", sb.toString());
			editor.putInt("rowE", 3);
			editor.putInt("columnE", 3);
		}
		editor.apply();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (isStarting) {
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

			matrixMenus[0].setY(calculationTextView.getY() + calculationTextView.getHeight() + 5);
			operator.setY(calculationTextView.getY() + calculationTextView.getHeight() + 5);
			matrixMenus[1].setY(calculationTextView.getY() + calculationTextView.getHeight() + 5);

			calculateButton.setY(height - 3 * calculateButton.getHeight() / 2);

			isStarting = false;
		}

		SharedPreferences prefs = this.getSharedPreferences("matrices", Context.MODE_PRIVATE);
		int displayRowA = prefs.getInt("rowA", 0); //0 is the default value
		int displayColumnA = prefs.getInt("columnA", 0); //0 is the default value
		String matrixA_display = "Matrix A (" + displayRowA + "x" + displayColumnA + ")";
		matrixA.setText(matrixA_display);

		int displayRowB = prefs.getInt("rowB", 0); //0 is the default value
		int displayColumnB = prefs.getInt("columnB", 0); //0 is the default value
		String matrixB_display = "Matrix B (" + displayRowB + "x" + displayColumnB + ")";
		matrixB.setText(matrixB_display);

		int displayRowC = prefs.getInt("rowC", 0); //0 is the default value
		int displayColumnC = prefs.getInt("columnC", 0); //0 is the default value
		String matrixC_display = "Matrix C (" + displayRowC + "x" + displayColumnC + ")";
		matrixC.setText(matrixC_display);

		int displayRowD = prefs.getInt("rowD", 0); //0 is the default value
		int displayColumnD = prefs.getInt("columnD", 0); //0 is the default value
		String matrixD_display = "Matrix D (" + displayRowD + "x" + displayColumnD + ")";
		matrixD.setText(matrixD_display);

		int displayRowE = prefs.getInt("rowE", 0); //0 is the default value
		int displayColumnE = prefs.getInt("columnE", 0); //0 is the default value
		String matrixE_display = "Matrix E (" + displayRowE + "x" + displayColumnE + ")";
		matrixE.setText(matrixE_display);

		float displayScalar = prefs.getFloat("scalar", 1f); //0 is the default value
		if (Math.abs(displayScalar % 1) < 1E-6) {
			String scalar_int_display = "c = " + (int) displayScalar;
			scalarButton.setText(scalar_int_display);
		} else {
			String scalar_float_display = "c = " + displayScalar;
			scalarButton.setText(scalar_float_display);
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

	private void startNewMatrixActivity(String letter) {
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

	private void parseMatrices(double[][][] matrices, String letter, int matrixNum) {
		String rowName = "row" + letter;
		String columnName = "column" + letter;

		SharedPreferences prefs = this.getSharedPreferences("matrices", Context.MODE_PRIVATE);
		int storedRows = prefs.getInt(rowName, 0); //0 is the default value
		int storedColumns = prefs.getInt(columnName, 0); //0 is the default value
		String storedMatrix = prefs.getString(letter, "0"); //"0" is the default value
		String[] theMatrix = storedMatrix.split(",");
		matrices[matrixNum] = new double[storedRows][storedColumns];

		for (int i = 0; i < storedRows; i++) {
			for (int j = 0; j < storedColumns; j++) {
				if ((((storedColumns) * i) + j) < theMatrix.length && !theMatrix[((storedColumns) * i) + j].contains("/")) {
					try {
						matrices[matrixNum][i][j] = Double.parseDouble(theMatrix[((storedColumns) * i) + j]);
					} catch (NumberFormatException e) {
						// Leave element as 0
					}
				} else {
					String[] s = theMatrix[((storedColumns) * i) + j].split("/");
					try {
						matrices[matrixNum][i][j] = Double.parseDouble(s[0]) / Double.parseDouble(s[1]);
					} catch (NumberFormatException e) {
						// Leave element as 0
					}
				}

			}
		}
	}

	public void saveScalar() {
		SharedPreferences prefs = this.getSharedPreferences("matrices", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putFloat("scalar", scalar);
		editor.apply();
	}

	public float getScalar() {
		SharedPreferences prefs = this.getSharedPreferences("matrices", Context.MODE_PRIVATE);
		return prefs.getFloat("scalar", 1f);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == createButton.getId()) {

			// Start new activity with blank matrix

			matrix.delete(0, matrix.length());

			for (int i = 0; i < row; i++) {
				for (int j = 0; j < column; j++) {
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
		} else if (v.getId() == matrixA.getId()) {
			this.startNewMatrixActivity("A");    // Edit Matrix A
		} else if (v.getId() == matrixB.getId()) {
			this.startNewMatrixActivity("B");    // Edit Matrix B
		} else if (v.getId() == matrixC.getId()) {
			this.startNewMatrixActivity("C");    // Edit Matrix C
		} else if (v.getId() == matrixD.getId()) {
			this.startNewMatrixActivity("D");    // Edit Matrix D
		} else if (v.getId() == matrixE.getId()) {
			this.startNewMatrixActivity("E");    // Edit Matrix D
		} else if (v.getId() == scalarButton.getId()) {
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
					if (!input.getText().toString().equals("")) {
						try {
							scalar = Float.parseFloat(input.getText().toString());
						} catch (NumberFormatException e) {
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
		} else if (v.getId() == calculateButton.getId()) {

			//----------------------------------------------------------------------------------------Begin Calculate

			// Run calculation in new thread
			Handler handler = new Handler();
			handler.post(new Runnable() {
				@Override
				public void run() {

					boolean isReady = true;

					double[][][] matrices;
					if (hasSecondMatrix) matrices = new double[2][][];
					else matrices = new double[1][][];

					// Load matrices being calculated
					parseMatrices(matrices, map.get(first), 0);
					int firstRow = matrices[0].length;
					int firstColumn = matrices[0][0].length;

					int secondRow = 0, secondColumn = 0;
					if (hasSecondMatrix) {
						parseMatrices(matrices, map.get(second), 1);
						secondRow = matrices[1].length;
						secondColumn = matrices[1][0].length;
					}

					int resultRow = firstRow;
					int resultColumn = firstColumn;
					double[][] resultMatrix = new double[resultRow][resultColumn];


					// ---------------------------------------------------------
					// Look at op code and perform the appropriate calculation
					// ---------------------------------------------------------

					// Adding matrices
					if (op == 0) {
						if (firstRow == secondRow && firstColumn == secondColumn) {
							resultMatrix = MatrixOperations.addMatrix(matrices[0], matrices[1]);
						} else {
							Toast message = Toast.makeText(getApplicationContext(), "Cannot add with these dimensions", Toast.LENGTH_SHORT);
							message.show();
							isReady = false;
						}
					}

					// Subtracting matrices
					else if (op == 1) {
						if (firstRow == secondRow && firstColumn == secondColumn) {
							resultMatrix = MatrixOperations.subtractMatrix(matrices[0], matrices[1]);
						} else {
							Toast message = Toast.makeText(getApplicationContext(), "Cannot subtract with these dimensions", Toast.LENGTH_SHORT);
							message.show();
							isReady = false;
						}
					}

					// Multiplying matrices
					else if (op == 2) {
						if (firstColumn == secondRow) {
							resultMatrix = MatrixOperations.multiplyMatrix(matrices[0], matrices[1]);
						} else {
							Toast message = Toast.makeText(getApplicationContext(), "Cannot multiply with these dimensions", Toast.LENGTH_SHORT);
							message.show();
							isReady = false;
						}
					}

					// Transposing matrices
					else if (op == 3) {
						resultMatrix = MatrixOperations.transposeMatrix(matrices[0]);
					}

					// Inverting matrices
					else if (op == 4) {

						if (firstRow == firstColumn) {
							resultMatrix = MatrixOperations.invertMatrix(resultRow, resultColumn, matrices[0]);
						} else {
							Toast message = Toast.makeText(getApplicationContext(), "Cannot invert with these dimensions", Toast.LENGTH_SHORT);
							message.show();
							isReady = false;
						}

						if (isReady && MatrixOperations.isZeroMatrix(resultMatrix.length, resultMatrix[0].length, resultMatrix)) {
							Toast message = Toast.makeText(getApplicationContext(), "Matrix is not invertible", Toast.LENGTH_SHORT);
							message.show();
							isReady = false;
						}
					}

					// Multiply by constant lambda
					else if (op == 5) {
						scalar = getScalar();
						resultMatrix = MatrixOperations.multiplyScalar(resultRow, resultColumn, matrices[0], scalar);
					}

					// Basis for null space
					else if (op == 6) {
						if (MatrixOperations.isZeroMatrix(resultRow, resultColumn, matrices[0])) {
							Toast message = Toast.makeText(getApplicationContext(), "Null space does not exist.", Toast.LENGTH_SHORT);
							message.show();
							isReady = false;
						} else {
							resultMatrix = MatrixOperations.calculateNullSpace(resultRow, resultColumn, matrices[0]);
						}

						if (isReady && MatrixOperations.isZeroMatrix(resultMatrix.length, resultMatrix[0].length, resultMatrix)) {
							Toast message = Toast.makeText(getApplicationContext(), "Null space does not exist.", Toast.LENGTH_SHORT);
							message.show();
							isReady = false;
						}
					}

					// Bases for column space
					else if (op == 7) {
						if (MatrixOperations.isZeroMatrix(resultRow, resultColumn, matrices[0])) {
							Toast message = Toast.makeText(getApplicationContext(), "Column space does not exist.", Toast.LENGTH_SHORT);
							message.show();
							isReady = false;
						} else {
							resultMatrix = MatrixOperations.calculateColumnSpace(resultRow, resultColumn, matrices[0]);
						}

						if (isReady && MatrixOperations.isZeroMatrix(resultMatrix.length, resultMatrix[0].length, resultMatrix)) {
							Toast message = Toast.makeText(getApplicationContext(), "Column space does not exist.", Toast.LENGTH_SHORT);
							message.show();
							isReady = false;
						}
					}

					// Bases for row space
					else if (op == 8) {
						if (MatrixOperations.isZeroMatrix(resultRow, resultColumn, matrices[0])) {
							Toast message = Toast.makeText(getApplicationContext(), "Row space does not exist.", Toast.LENGTH_SHORT);
							message.show();
							isReady = false;
						} else {
							resultMatrix = MatrixOperations.calculateRowSpace(resultRow, resultColumn, matrices[0]);
						}

						if (isReady && MatrixOperations.isZeroMatrix(resultMatrix.length, resultMatrix[0].length, resultMatrix)) {
							Toast message = Toast.makeText(getApplicationContext(), "Row space does not exist.", Toast.LENGTH_SHORT);
							message.show();
							isReady = false;
						}
					}

					// Determinant
					else if (op == 9) {
						if (resultRow == resultColumn) {
							AlertDialog.Builder alert = new AlertDialog.Builder(context);

							alert.setTitle("Determinant");
							alert.setMessage("");

							final TextView display = new TextView(context);
							display.setGravity(Gravity.CENTER);
							display.setPadding(0, 0, 0, 0);
							display.setBackgroundColor(Color.WHITE);
							String det = "" + MatrixOperations.calculateDeterminant(resultRow, resultColumn, matrices[0]);
							display.setText(det);
							display.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
							display.setTextSize(30f);
							alert.setView(display);

							alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {

								}
							});

							alert.show();
							isReady = false;
						} else {
							Toast message = Toast.makeText(getApplicationContext(), "Cannot calculate determinant.", Toast.LENGTH_SHORT);
							message.show();
							isReady = false;
						}
					}

					// Eigenvalues
					else if (op == 10) {
						if (resultRow == resultColumn) {
							AlertDialog.Builder alert = new AlertDialog.Builder(context);

							alert.setTitle("Determinant");
							alert.setMessage("");

							final TextView display = new TextView(context);
							display.setGravity(Gravity.CENTER);
							display.setPadding(0, 0, 0, 0);
							display.setBackgroundColor(Color.WHITE);
							String det = "" + MatrixOperations.calculateEigenvalues(resultRow, resultColumn, matrices[0]);
							display.setText(det);
							display.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
							display.setTextSize(30f);
							alert.setView(display);

							alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									dialog.cancel();
								}
							});

							alert.show();
							isReady = false;
						} else {
							Toast message = Toast.makeText(getApplicationContext(), "Cannot calculate eigenvalues.", Toast.LENGTH_SHORT);
							message.show();
							isReady = false;
						}

					}

					// If there is no problem with the dimensions, display the calculation in a new activity
					if (isReady) {
						resultRow = resultMatrix.length;
						resultColumn = resultMatrix[0].length;
						String matrixString = MatrixOperations.matrixToString(resultRow, resultColumn, resultMatrix);
						Intent intent = new Intent(context, DisplayActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt("row", resultRow);
						bundle.putInt("column", resultColumn);
						bundle.putString("matrix", matrixString);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}
			});
			//----------------------------------------------------------------------------------------End Calculate


		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		if (parent.getId() == rowMenu.getId()) {
			row = pos + 1;
		} else if (parent.getId() == columnMenu.getId()) {
			column = pos + 1;
		} else if (parent.getId() == operator.getId()) {
			if (pos >= 3) {
				matrixMenus[1].setVisibility(View.INVISIBLE);
				hasSecondMatrix = false;
			} else {
				matrixMenus[1].setVisibility(View.VISIBLE);
				hasSecondMatrix = true;
			}

			op = pos;
		} else if (parent.getId() == matrixMenus[0].getId()) {
			first = pos;
		} else if (parent.getId() == matrixMenus[1].getId()) {
			second = pos;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// Do nothing
	}
}
