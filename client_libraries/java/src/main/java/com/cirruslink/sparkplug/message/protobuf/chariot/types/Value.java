package com.cirruslink.sparkplug.message.protobuf.chariot.types;

public class Value<V> {

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
	
	private ValueDataType type;
	private V value;
	
	public Value() {
		super();
	}

	public Value(ValueDataType type, V value) {
		super();
		this.type = type;
		this.value = value;
	}

	public ValueDataType getType() {
		return type;
	}

	public void setType(ValueDataType type) {
		this.type = type;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Value [type=" + type + ", value=" + value + "]";
	}
}
