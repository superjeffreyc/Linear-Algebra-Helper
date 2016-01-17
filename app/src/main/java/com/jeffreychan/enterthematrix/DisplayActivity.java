package com.jeffreychan.enterthematrix;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayActivity extends Activity implements OnClickListener {

	TextView[][] myTextViewArray;
	private int rows, columns, height;
	RelativeLayout mainLayout;
	Button saveA, saveB, saveC, saveD, saveE;
	boolean isStarting = true;
	int saveButtonWidth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().getDecorView().setBackgroundColor(Color.rgb(34, 177, 76));
		setContentView(R.layout.activity_display);

		// Grab values from the previous activity
		Bundle bundle = getIntent().getExtras();
		String s = bundle.getString("matrix");
		String[] myString = s.split(",");
		rows = bundle.getInt("row");
		columns = bundle.getInt("column");
		myTextViewArray = new TextView[rows][columns];

		mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);

		// Get dimensions of the screen
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		height = size.y;

		saveButtonWidth = width / 5;

		// Create save buttons
		saveA = new Button(this);
		saveA.setText(R.string.saveA);
		saveA.setLayoutParams(new LayoutParams(saveButtonWidth, LayoutParams.WRAP_CONTENT));
		saveA.setX(0);
		saveA.setY(4 * height / 5);
		saveA.setOnClickListener(this);
		saveA.setId(R.id.saveToA);
		mainLayout.addView(saveA);

		saveB = new Button(this);
		saveB.setText(R.string.saveB);
		saveB.setLayoutParams(new LayoutParams(saveButtonWidth, LayoutParams.WRAP_CONTENT));
		saveB.setX(width / 5);
		saveB.setY(4 * height / 5);
		saveB.setOnClickListener(this);
		saveB.setId(R.id.saveToB);
		mainLayout.addView(saveB);

		saveC = new Button(this);
		saveC.setText(R.string.saveC);
		saveC.setLayoutParams(new LayoutParams(saveButtonWidth, LayoutParams.WRAP_CONTENT));
		saveC.setX(2 * width / 5);
		saveC.setY(4 * height / 5);
		saveC.setOnClickListener(this);
		saveC.setId(R.id.saveToC);
		mainLayout.addView(saveC);

		saveD = new Button(this);
		saveD.setText(R.string.saveD);
		saveD.setLayoutParams(new LayoutParams(saveButtonWidth, LayoutParams.WRAP_CONTENT));
		saveD.setX(3 * width / 5);
		saveD.setY(4 * height / 5);
		saveD.setOnClickListener(this);
		saveD.setId(R.id.saveToD);
		mainLayout.addView(saveD);

		saveE = new Button(this);
		saveE.setText(R.string.saveE);
		saveE.setLayoutParams(new LayoutParams(saveButtonWidth, LayoutParams.WRAP_CONTENT));
		saveE.setX(4 * width / 5);
		saveE.setY(4 * height / 5);
		saveE.setOnClickListener(this);
		saveE.setId(R.id.saveToE);
		mainLayout.addView(saveE);

		// Create the proper number of TextViews and arrange them according to the number of rows and columns
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				myTextViewArray[i][j] = new TextView(this);
				myTextViewArray[i][j].setLayoutParams(new LayoutParams(width / columns - 5, height / (rows + 2) - 5));
				if (columns == 1) {
					myTextViewArray[i][j].setTextSize(80f);
				} else {
					myTextViewArray[i][j].setTextSize(110f / columns);
				}
				myTextViewArray[i][j].setGravity(Gravity.CENTER);
				myTextViewArray[i][j].setBackgroundColor(Color.WHITE);
				myTextViewArray[i][j].setX(j * width / columns);
				myTextViewArray[i][j].setY(i * height / (rows + 2));
				if ((((columns) * i) + j) < myString.length) {
					myTextViewArray[i][j].setText(myString[((columns) * i) + j]);
				} else {
					myTextViewArray[i][j].setText("0");
				}
				mainLayout.addView(myTextViewArray[i][j]);

			}
		}
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
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return super.onOptionsItemSelected(item);
	}

	private String textViewToString() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				if (myTextViewArray[i][j].getText().toString().equals("")) {
					sb.append("0");
				} else {
					sb.append(myTextViewArray[i][j].getText().toString());
				}

				sb.append(",");
			}
		}
		return sb.toString();
	}

	private void saveMatrix(String letter) {
		String rowName = "row" + letter;
		String columnName = "column" + letter;
		String matrixString = this.textViewToString();

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
		if (v.getId() == saveA.getId()) {
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
