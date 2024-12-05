/**
 * Course: ITMD 411
 * Date: Dec/ 04/ 2024
 * Done by: Md. Mahmudul Hasan (A20502196)
 * PROJECT: Database Recording
 */

package javaapplication1;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class ticketsJTable {

	public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {

		// Retrieve metadata about the ResultSet (e.g., column names and types)
		ResultSetMetaData metaData = rs.getMetaData();

		// Extract column names from the ResultSet
		Vector<String> columnNames = new Vector<>();
		int columnCount = metaData.getColumnCount(); // Get the total number of columns
		for (int column = 1; column <= columnCount; column++) {
			columnNames.add(metaData.getColumnName(column)); // Add each column name to the vector
		}

		// Extract data rows from the ResultSet
		Vector<Vector<Object>> data = new Vector<>();
		while (rs.next()) {
			Vector<Object> vector = new Vector<>();
			for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
				vector.add(rs.getObject(columnIndex)); // Add each column value to the row vector
			}
			data.add(vector); // Add the row vector to the data vector
		}

		// Create and return a DefaultTableModel using the extracted column names and
		// data
		return new DefaultTableModel(data, columnNames);
	}
}
