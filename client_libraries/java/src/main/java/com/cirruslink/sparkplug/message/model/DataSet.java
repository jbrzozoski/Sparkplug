/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.model;

import java.util.List;

/**
 * A data set that represents a table of data.
 */
public class DataSet {

	/**
	 * The number of columns
	 */
	private long numOfColumns;
	
	/**
	 * A list containing the names of each column
	 */
	private List<String> columnNames;
	
	/**
	 * A list containing the data types of each column
	 */
	private List<DataSetDataType> types;
	
	/**
	 * A list containing the rows in the data set
	 */
	private List<Row> rows;
	
	public DataSet() {
		super();
	}
	
	public DataSet(long numOfColumns, List<String> columnNames, List<DataSetDataType> types, List<Row> rows) {
		super();
		this.numOfColumns = numOfColumns;
		this.columnNames = columnNames;
		this.types = types;
		this.rows = rows;
	}

	public long getNumOfColumns() {
		return numOfColumns;
	}

	public void setNumOfColumns(long numOfColumns) {
		this.numOfColumns = numOfColumns;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

	public List<Row> getRows() {
		return rows;
	}

	public void addRow(Row row) {
		this.rows.add(row);
	}
	
	public void addRow(int index, Row row) {
		this.rows.add(index, row);
	}
	
	public Row removeRow(int index) {
		return rows.remove(index);
	}
	
	public boolean removeRow(Row row) {
		return rows.remove(row);
	}
	
	public void setRows(List<Row> rows) {
		this.rows = rows;
	}

	public List<DataSetDataType> getTypes() {
		return types;
	}

	public void setTypes(List<DataSetDataType> types) {
		this.types = types;
	}
	
	public void addType(int index, DataSetDataType type) {
		this.types.add(index, type);
	}
}
