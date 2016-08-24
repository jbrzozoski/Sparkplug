/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.model;

import java.util.List;

public class DataSet {
	
	/*
                message DataSet {

                        enum DataType {
                                Unknown                 = 0;
                                Int1                    = 1;
                                Int2                    = 2;
                                Int4                    = 3;
                                Int8                    = 4;
                                Float4                  = 5;
                                Float8                  = 6;
                                Boolean                 = 7;
                                String                  = 8;
                                DateTime                = 9;
                                Text                    = 10;
                                Null                    = 11;
                        }

                        message Value {

                                optional uint32 int_value       = 2;            // Int1, Int2, Int4
                                optional uint64 long_value      = 3;            // Int8, DateTime
                                optional float  float_value     = 4;            // Float4
                                optional double double_value    = 5;            // Float8
                                optional bool   boolean_value   = 6;            // Boolean
                                optional string string_value    = 7;            // String, Text
                                extensions                      8 to max;       // For third party extensions
                        }

                        message Row {
                                repeated Value element          = 1;
                                extensions                      2 to max;       // For third party extensions
                        }

                        optional uint64 num_of_columns          = 1;
                        repeated string columns                 = 2;
                        repeated DataType types                 = 3;
                        repeated Row rows                       = 4;
                        extensions                              5 to max;       // For third party extensions
                }*/

	private long numOfColumns;
	private List<String> columnNames;
	private List<DataSetDataType> types;
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
