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

	Button reduceButton, optionsButton, saveA, saveB, saveC, saveD, saveE;
	private int rows, columns, height;
	EditText[][] myEditTextArray;
	String loadMatrix;
	String[] splitMatrix = null;
	RelativeLayout mainLayout;
	boolean isStarting = true;
	int saveButtonWidth;

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

		if (loadMatrix != null) {
			splitMatrix = loadMatrix.split(",");
		}

		mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
		myEditTextArray = new EditText[rows][columns];

		// Get dimensions of the screen
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		height = size.y;

		saveButtonWidth = width / 5;

		// Create the proper number of TextViews and arrange them according to the number of rows and columns
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				myEditTextArray[i][j] = new EditText(this);
				myEditTextArray[i][j].setLayoutParams(new LayoutParams(width / columns - 5, height / (rows + 2) - 5));
				if (columns == 1) {
					myEditTextArray[i][j].setTextSize(80f);
				} else {
					myEditTextArray[i][j].setTextSize(110f / columns);
				}
				myEditTextArray[i][j].setGravity(Gravity.CENTER);
				myEditTextArray[i][j].setPadding(0, 0, 0, 0);
				myEditTextArray[i][j].setBackgroundColor(Color.WHITE);
				myEditTextArray[i][j].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
				myEditTextArray[i][j].setX(j * width / columns);
				myEditTextArray[i][j].setY(i * height / (rows + 2));

				// If not creating a new matrix
				if (splitMatrix != null) {
					if ((((columns) * i) + j) < splitMatrix.length) {
						myEditTextArray[i][j].setText(splitMatrix[((columns) * i) + j]);
					} else {
						myEditTextArray[i][j].setText("");
					}
				}
				mainLayout.addView(myEditTextArray[i][j]);

				// Allow user to easily move from left to right using the next button
				if (i == rows - 1 && j == columns - 1) {
					myEditTextArray[i][j].setImeOptions(EditorInfo.IME_ACTION_DONE);
				} else {
					myEditTextArray[i][j].setImeOptions(EditorInfo.IME_ACTION_NEXT);

				}
			}
		}

		// Create options button (insert identity, clear all entries, clear entries with 0)
		optionsButton = new Button(this);
		optionsButton.setText(R.string.options);
		optionsButton.setId(R.id.optionsButton);
		optionsButton.setLayoutParams(new LayoutParams(width / 3, LayoutParams.WRAP_CONTENT));
		optionsButton.setX(((width / 3) / 2));
		optionsButton.setY(rows * height / (rows + 2));
		optionsButton.setOnClickListener(this);

		// Create reduce button
		reduceButton = new Button(this);
		reduceButton.setText(R.string.reduce);
		reduceButton.setId(R.id.reduceButton);
		reduceButton.setLayoutParams(new LayoutParams(width / 3, LayoutParams.WRAP_CONTENT));
		reduceButton.setX(width/2);
		reduceButton.setY(rows * height / (rows + 2));
		reduceButton.setOnClickListener(this);

		saveA = new Button(this);
		saveA.setText(R.string.saveA);
		saveA.setLayoutParams(new LayoutParams(saveButtonWidth, LayoutParams.WRAP_CONTENT));
		saveA.setX(0);
		saveA.setY(4 * height / 5);
		saveA.setOnClickListener(this);
		saveA.setId(R.id.saveToA);

		saveB = new Button(this);
		saveB.setText(R.string.saveB);
		saveB.setLayoutParams(new LayoutParams(saveButtonWidth, LayoutParams.WRAP_CONTENT));
		saveB.setX(width / 5);
		saveB.setY(4 * height / 5);
		saveB.setOnClickListener(this);
		saveB.setId(R.id.saveToB);

		saveC = new Button(this);
		saveC.setText(R.string.saveC);
		saveC.setLayoutParams(new LayoutParams(saveButtonWidth, LayoutParams.WRAP_CONTENT));
		saveC.setX(2 * width / 5);
		saveC.setY(4 * height / 5);
		saveC.setOnClickListener(this);
		saveC.setId(R.id.saveToC);

		saveD = new Button(this);
		saveD.setText(R.string.saveD);
		saveD.setLayoutParams(new LayoutParams(saveButtonWidth, LayoutParams.WRAP_CONTENT));
		saveD.setX(3 * width / 5);
		saveD.setY(4 * height / 5);
		saveD.setOnClickListener(this);
		saveD.setId(R.id.saveToD);

		saveE = new Button(this);
		saveE.setText(R.string.saveE);
		saveE.setLayoutParams(new LayoutParams(saveButtonWidth, LayoutParams.WRAP_CONTENT));
		saveE.setX(4 * width / 5);
		saveE.setY(4 * height / 5);
		saveE.setOnClickListener(this);
		saveE.setId(R.id.saveToE);

		// Add the buttons to the relative layout
		mainLayout.addView(reduceButton);
		mainLayout.addView(optionsButton);
		mainLayout.addView(saveA);
		mainLayout.addView(saveB);
		mainLayout.addView(saveC);
		mainLayout.addView(saveD);
		mainLayout.addView(saveE);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (isStarting) {
			saveA.setY(height - saveA.getHeight());
			saveB.setY(height - saveA.getHeight());
			saveC.setY(height - saveA.getHeight());
			saveD.setY(height - saveA.getHeight());
			saveE.setY(height - saveA.getHeight());
			isStarting = false;
		}
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
			saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					for (int i = 0; i < rows; i++) {
						for (int j = 0; j < columns; j++) {
							myEditTextArray[i][j].setText("");
						}
					}
					myEditTextArray[0][0].requestFocus();
				}
			});
			saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			saveDialog.show();
		} else if (id == R.id.clearZero) {
			AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
			saveDialog.setTitle("Clear Zero");
			saveDialog.setMessage("Clear only zero entries?");
			saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					for (int i = 0; i < rows; i++) {
						for (int j = 0; j < columns; j++) {
							if (myEditTextArray[i][j].getText().toString().equals("0")) {
								myEditTextArray[i][j].setText("");
							}
						}
					}
					myEditTextArray[0][0].requestFocus();
				}
			});
			saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			saveDialog.show();
		} else if (id == R.id.insertIdentity) {
			AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
			saveDialog.setTitle("Identity Matrix");
			saveDialog.setMessage("Insert the identity matrix?");
			saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (rows != columns) {
						Toast savedToast = Toast.makeText(getApplicationContext(), "Matrix must be square", Toast.LENGTH_SHORT);
						savedToast.show();
					} else {
						for (int i = 0; i < rows; i++) {
							for (int j = 0; j < columns; j++) {
								if (i == j) {
									myEditTextArray[i][j].setText("1");
								} else {
									myEditTextArray[i][j].setText("0");
								}
							}
						}
					}
					myEditTextArray[0][0].requestFocus();
				}
			});
			saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			saveDialog.show();
		}
		return super.onOptionsItemSelected(item);
	}

	private double[][] editTextToMatrix(int rows, int columns) {

		double[][] matrix = new double[rows][columns];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				if (myEditTextArray[i][j].getText().toString().equals("")) {
					matrix[i][j] = 0.0;
				} else if (myEditTextArray[i][j].getText().toString().contains("/")) {
					String[] s = myEditTextArray[i][j].getText().toString().split("/");
					try {
						matrix[i][j] = Double.parseDouble(s[0]) / Double.parseDouble(s[1]);
					} catch (NumberFormatException e) {
						// Leave element as 0
					}
				} else {
					try {
						matrix[i][j] = Double.parseDouble(myEditTextArray[i][j].getText().toString());
					} catch (NumberFormatException e) {
						// Leave element as 0
					}

				}
			}
		}

		return matrix;
	}

	private void saveMatrix(String letter) {
		String rowName = "row" + letter;
		String columnName = "column" + letter;
		double[][] currentMatrix = editTextToMatrix(rows, columns);
		String matrixString = MatrixOperations.matrixToString(rows, columns, currentMatrix);

		SharedPreferences prefs = this.getSharedPreferences("matrices", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString(letter, matrixString);
		editor.putInt(rowName, rows);
		editor.putInt(columnName, columns);
		editor.apply();

		Toast savedToast = Toast.makeText(getApplicationContext(), "Saved as Matrix " + letter + "!", Toast.LENGTH_SHORT);
		savedToast.show();
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == reduceButton.getId()) {

			// Grab the entries from the edit texts
			double[][] matrix = editTextToMatrix(rows, columns);

			double[][] resultMatrix = MatrixOperations.reduceMatrix(rows, columns, matrix);
			String matrixString = MatrixOperations.matrixToString(rows, columns, resultMatrix);

			Intent intent = new Intent(this, DisplayActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("matrix", matrixString);
			bundle.putInt("row", rows);
			bundle.putInt("column", columns);
			intent.putExtras(bundle);
			startActivity(intent);
		} else if (v.getId() == optionsButton.getId()) {
			openOptionsMenu();
		} else if (v.getId() == saveA.getId()) {
			this.saveMatrix("A");
		} else if (v.getId() == saveB.getId()) {
			this.saveMatrix("B");
		} else if (v.getId() == saveC.getId()) {
			this.saveMatrix("C");
		} else if (v.getId() == saveD.getId()) {
			this.saveMatrix("D");
		} else if (v.getId() == saveE.getId()) {
			this.saveMatrix("E");
		}

	}


}
