package com.jeffreychan.enterthematrix;

import java.text.DecimalFormat;

public class MatrixOperations {

	public static String matrixToString(int rows, int columns, double[][] matrix){
		
		// Create a string of all the elements to pass to the next activity
	
		StringBuilder sb = new StringBuilder();
	
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++){
				if (matrix[i][j] == 0){
					// If the element is 0, append 0
					sb.append("0,");
				}
				else if (Math.abs(matrix[i][j]) % 1 < 1E-6) {		
					// If the remainder is nearly 0, remove the decimal places and append
					sb.append((int) matrix[i][j] + ",");
				}
				else if (Math.abs(matrix[i][j]) % 1 > 0.999999 && matrix[i][j] > 0) {
					// If the element is positive and remainder is nearly 1, round up and append
					sb.append((int) Math.ceil(matrix[i][j]) + ",");
				}
				else if (Math.abs(matrix[i][j]) % 1 > 0.999999 && matrix[i][j] < 0) {
					// If the element is negative and remainder is nearly 1, round down and append
					sb.append((int) Math.floor(matrix[i][j]) + ",");
				}
				else{
					// The element is not a whole number
	
					boolean done = false;
					double n = 2.0;
					final int MAX_DENOM = 10000;
					
					// Finds possible denominators up to MAX_DENOM and append the appropriate fraction
					while (!done && n <= MAX_DENOM) {
						if ( (Math.abs(matrix[i][j]) / (1.0/n) )  % 1 < 1E-6 || (Math.abs(matrix[i][j]) / (1.0/n) ) % 1 > 0.999999)  {
							done = true;
							sb.append( (int) (Math.round(matrix[i][j] / (1.0/n)))  + "/" + (int) n + ",");
						}
	
						n++;
					}
	
					// If a denominator cannot be found, just round the number to two decimal places
					if (!done) {
						DecimalFormat df = new DecimalFormat("#.00");
						sb.append(df.format(matrix[i][j]) + ",");
					}
				}
	
	
			}
	
		}
		
		return sb.toString();
	}

	public static double[][] reduceMatrix(int rows, int columns, double[][] matrixA) {

		double[][] matrix = new double[rows][columns];
		for (int i = 0; i < rows; i++){
			for (int j = 0; j < columns; j++){
				matrix[i][j] = matrixA[i][j];
			}
		}
		
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

		return matrix;

	}
	
	public static double[][] multiplyMatrix(double[][] matrixA, double[][] matrixB){
		double[][] matrixC = new double[matrixA.length][matrixB[0].length];
		
		for (int i = 0; i < matrixA.length; i++){
			for (int j = 0; j < matrixB[0].length; j++){
				for (int k = 0; k < matrixB.length; k++){
					matrixC[i][j] += matrixA[i][k]*matrixB[k][j];
				}
			}
		}
		
		return matrixC;
	}
	
	public static double[][] multiplyScalar(int rows, int columns, double[][] matrixA, float scalar){
		double[][] matrixB = new double[rows][columns];
		
		for (int i = 0; i < rows; i++){
			for (int j = 0; j < columns; j++){
				matrixB[i][j] = matrixA[i][j] * scalar;
			}
		}
		
		return matrixB;
	}
	
	public static double[][] addMatrix(double[][] matrixA, double[][] matrixB){
		
		double[][] matrixC = new double[matrixA.length][matrixB[0].length];

		for (int i = 0; i < matrixA.length; i++){
			for (int j = 0; j < matrixB[0].length; j++){
				matrixC[i][j] = matrixA[i][j] + matrixB[i][j];
			}
		}
		
		return matrixC;
	}
	
	public static double[][] subtractMatrix(double[][] matrixA, double[][] matrixB){
		
		double[][] matrixC = new double[matrixA.length][matrixB[0].length];

		for (int i = 0; i < matrixA.length; i++){
			for (int j = 0; j < matrixB[0].length; j++){
				matrixC[i][j] = matrixA[i][j] - matrixB[i][j];
			}
		}
		
		return matrixC;
	}
	
	public static double[][] transposeMatrix(double[][] matrixA){
		
		double[][] matrixC = new double[matrixA[0].length][matrixA.length];
		
		for (int i = 0; i < matrixA.length; i++){
			for (int j = 0; j < matrixA[0].length; j++){
				matrixC[j][i] = matrixA[i][j];
			}
		}
		
		return matrixC;
	}
}
