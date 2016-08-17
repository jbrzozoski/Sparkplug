/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.model;

import java.util.List;

public class Row {
	
	/*
    message DataSet {
        message Value {
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

                required DataType type          = 1;
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


        required uint64 num_of_columns          = 1;
        repeated Value columns                  = 2;
        repeated Row rows                       = 3;
        extensions                              4 to max;       // For third party extensions
    }*/

	private List<Value<?>> values;

	public Row() {
		super();
	}

	public Row(List<Value<?>> values) {
		super();
		this.values = values;
	}

	public List<Value<?>> getValues() {
		return values;
	}

	public void setValues(List<Value<?>> values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return "Row [values=" + values + "]";
	}
}
