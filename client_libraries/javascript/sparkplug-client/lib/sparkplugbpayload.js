/**
 * Copyright (c) 2012, 2016 Cirrus Link Solutions
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Cirrus Link Solutions
 */

/**
 * Provides support for generating Kura payloads.
 */
(function () {
    var ProtoBuf = require("protobufjs"),
        ByteBuffer = require("bytebuffer"),
        tempBuffer = ByteBuffer.allocate(1024);

    var builder = ProtoBuf.loadProto("package com.cirruslink.sparkplug.protobuf; message Payload { message Template { " +
            "message Parameter { optional string name = 1;optional uint32 type = 2; oneof value { uint32 int_value = 3; uint64 long_value = 4; " +
            "float float_value = 5; double double_value = 6; bool boolean_value = 7; string string_value = 8; ParameterValueExtension extension_value = 9; } " +
            "message ParameterValueExtension { extensions 1 to max; } } optional string version = 1; repeated Metric metrics = 2; " +
            "repeated Parameter parameters = 3; optional string template_ref = 4; optional bool is_definition = 5; extensions 6 to max; } " +
            "message DataSet { " +
            "message DataSetValue { oneof value { uint32 int_value = 1; uint64 long_value = 2; float float_value = 3; double double_value = 4; " +
            "bool boolean_value = 5; string string_value = 6; DataSetValueExtension extension_value = 7; } " +
            "message DataSetValueExtension { extensions 1 to max; } } " +
            "message Row { repeated DataSetValue elements = 1; extensions 2 to max; } optional uint64 num_of_columns = 1; repeated string columns = 2; " +
            "repeated uint32 types = 3; repeated Row rows = 4; extensions 5 to max; } " +
            "message PropertyValue { optional uint32 type = 1; optional bool is_null = 2;  oneof value { uint32 int_value = 3; uint64 long_value = 4; " +
            "float float_value = 5; double double_value = 6; bool boolean_value = 7; string string_value = 8; PropertySet propertyset_value = 9; " +
            "PropertySetList propertysets_value = 10; PropertyValueExtension extension_value = 11; } " +
            "message PropertyValueExtension { extensions 1 to max; } } " +
            "message PropertySet { repeated string keys = 1; repeated PropertyValue values = 2; extensions 3 to max; } " +
            "message PropertySetList { repeated PropertySet propertyset = 1; extensions 2 to max; } " +
            "message MetaData { optional bool is_multi_part = 1; optional string content_type = 2; optional uint64 size = 3; optional uint64 seq = 4; " +
            "optional string file_name = 5; optional string file_type = 6; optional string md5 = 7; optional string description = 8; extensions 9 to max; } " +
            "message Metric { optional string name = 1; optional uint64 alias = 2; optional uint64 timestamp = 3; optional uint32 datatype = 4; " +
            "optional bool is_historical = 5; optional bool is_transient = 6; optional bool is_null = 7; optional MetaData metadata = 8; " +
            "optional PropertySet properties = 9; oneof value { uint32 int_value = 10; uint64 long_value = 11; float float_value = 12; double double_value = 13; " +
            "bool boolean_value = 14; string string_value = 15; bytes bytes_value = 16; DataSet dataset_value = 17; Template template_value = 18; " +
            "MetricValueExtension extension_value = 19; } " +
            "message MetricValueExtension { extensions 1 to max; } } optional uint64 timestamp = 1; repeated Metric metrics = 2; optional uint64 seq = 3; " +
            "optional string uuid = 4; optional bytes body = 5; extensions 6 to max; } "),
        SparkplugBDataTypes = builder.build('com.cirruslink.sparkplug.protobuf'),
        Payload = SparkplugBDataTypes.Payload,
        Template = Payload.Template,
        Parameter = Template.Parameter,
        DataSet = Payload.DataSet,
        DataSetValue = DataSet.DataSetValue,
        Row = DataSet.Row,
        PropertyValue = Payload.PropertyValue,
        PropertySet = Payload.PropertySet,
        PropertyList = Payload.PropertyList,
        MetaData =Payload.MetaData,
        Metric = Payload.Metric;

    /**
     * Sets the value of an object given it's type expressed as an integer
     */
    setValue = function(type, value, object) {
        switch (type) {
            case 1: // Int8
            case 2: // Int16
            case 3: // Int32
            case 5: // UInt8
            case 6: // UInt32
                object.int_value = value;
                break;
            case 4: // Int64
            case 7: // UInt32
            case 8: // UInt64
            case 13: // DataTime
                object.long_value = value;
                break;
            case 9: // Float
                object.float_value = value;
                break;
            case 10: // Double
                object.double_value = value;
                break;
            case 11: // Boolean
                object.boolean_value = value;
                break;
            case 12: // String
            case 14: // Text
            case 15: // UUID
                object.string_value = value;
                break;
            case 16: // DataSet
                object.dataset_value = generateDataSet(value);
                break;
            case 17: // Bytes
            case 18: // File
                object.bytes_value = value;
                break;
            case 19: // Template
                object.template_value = generateTemplate(value);
                break;
            case 20: // PropertySet
                object.propertyset_value = generatePropertySet(value);
                break;
            case 21:
                object.propertysets_value = geneneratePropertySetList(value);
                break;
        } 
    }

    convertType = function(typeString) {
        switch (typeString.toUpperCase()) {
            case "INT8":
                return 1;
            case "INT16":
                return 2;
            case "INT32":
            case "INT":
                return 3;
            case "INT64":
            case "LONG":
                return 4;
            case "UINT8":
                return 5;
            case "UINT16":
                return 6;
            case "UINT32":
                return 7;
            case "UINT64":
                return 8;
            case "FLOAT":
                return 9;
            case "DOUBLE":
                return 10;
            case "BOOLEAN":
                return 11;
            case "STRING":
                return 12;
            case "DATETIME":
                return 13;
            case "TEXT":
                return 14;
            case "UUID":
                return 15;
            case "DATASET":
                return 16;
            case "BYTES":
                return 17;
            case "FILE":
                return 18;
            case "TEMPLATE":
                return 19;
            case "PROPERTYSET":
                return 20;
            case "PROPERTYSETLIST":
                return 21;
            default:
                return 0;
        }
    }

    convertTypes = function(typeArray) {
        var types = [];
        for (var i = 0; i < typeArray.length; i++) {
            types.push(convertType(typeArray[i]));
        }
        return types;
    }

    generateDataSet = function(object) {
        var num = object.numOfColumns,
            types = convertTypes(object.types),
            rows = object.rows,
            newDataSet = new DataSet(num, object.columns, types),
            newRows = [];
        // Loop over all the rows
        for (var i = 0; i < rows.length; i++) {
            var row = row[i];
                newRow = [];
            // Loop over all the values in each row
            for (var t = 0; t < num; t++) {
                var newValue = new DataSetValue();
                setValue(types[t], row[t], newValue);
                newRow.push(newValue);
            }
            newRows.push(newRow);
        }
        newDataSet.rows = newRows;
        return dataSet;
    }

    generateMetaData = function(object) {
        var metadata = new MetaData();

        if (object.isMultiPart !== undefined) {
            metadata.is_multi_part = object.isMultiPart;
        }

        if (object.contentType !== undefined) {
            metadata.content_type = object.contentType;
        }

        if (object.size !== undefined) {
            metadata.size = object.size;
        }

        if (object.seq !== undefined) {
            metadata.seq = object.seq;
        }

        if (object.fileName !== undefined) {
            metadata.file_name = object.fileName;
        }

        if (object.fileType !== undefined) {
            metadata.file_type = object.fileType;
        }

        if (object.md5 !== undefined) {
            metadata.md5 = object.md5;
        }

        if (object.description !== undefined) {
            metadata.description = object.description;
        }

        return metadata;
    }

    generatePropertyValue = function(object) {
        var type = convertType(object.type),
            newPropertyValue = new PropertyValue(type);

        if (object.isNull !== undefined && object.isNull !== null) {
            newPropertyValue.is_null = object.isNull;
        }

        setValue(type, object.value, newPropertyValue);

        return newPropertyValue;
    }

    generatePropertySet = function(object) {
        var keys = [],
            values = [];

        for (var key in object) {
            if (object.hasOwnProperty(key)) {
                keys.push(key);
                values.push(generatePropertyValue(object[key]))  
            }
        }

        return new PropertySet(keys, values);
    }

    generatePropertySetList = function(object) {
        var propertySets = []
        for (var i = 0; i < object.length; i++) {
            propertySets.push(generatePropertySet(object[i]))
        }
        return new PropertySetList(propertySets);
    }

    generateParameter = function(object) {
        var type = convertType(object.type),
            newParameter = new Parameter(object.name, type);
        setValue(type, object.value, newParameter);
        return newParameter;
    }

    generateTemplate = function(object) {
        var newTemplate = new Template(object.name),
            metrics = template.metrics,
            parameters = template.parameters,
            isDef = object.isDefinition,
            ref = object.templateRef,
            version = object.version;

        if (version !== undefined && version !== null) {
            newTemplate.version = version;    
        }

        if (ref !== undefined && ref !== null) {
            newTemplate.template_ref = ref;    
        }

        if (isDef !== undefined && isDef !== null) {
            newTemplate.is_definition = isDef;    
        }

        // Build up the metric
        if (object.metrics !== undefined && object.metrics !== null) {
            var newMetrics = []
                metrics = object.metrics;
            // loop over array of metrics
            for (var i = 0; i < metrics.length; i++) {
                newMetrics.push(generateMetric(metrics[i]));
            }
            newTemplate.metrics = newMetrics;
        }

        // Build up the parameters
        if (object.parameters !== undefined && object.parameters !== null) {
            var newParameter = [];
            // loop over array of parameters
            for (var i = 0; i < object.parameters.length; i++) {
                newParameter.push(generateParameter(object.parameters[i]));
            }
            newTemplate.parameters = newParameter;
        }

        return newTemplate;
    }

    generateMetric = function(metric) {
        var newMetric = new Metric(metric.name),
            value = metric.value,
            datatype = convertType(metric.type);
        
        // Get metric type and value
        newMetric.datatype = datatype;
        setValue(datatype, value, newMetric);

        if (metric.alias !== undefined) {
            newMetric.alias = metric.alias;
        }

        if (metric.isHistorical !== undefined) {
            newMetric.is_historical = metric.isHistorical;
        }

        if (metric.isTransient !== undefined) {
            newMetric.is_transient = metric.isTransient;
        }

        if (metric.isNull !== undefined) {
            newMetric.is_null = metric.isNull;
        }

        if (metric.metadata !== undefined && metric.metadata !== null) {
            newMetric.metadata = generateMetaData(metric.metadata);
        }

        if (metric.properties !== undefined && metric.properties !== null) {
            newMetric.properties = generatePropertySet(metric.properties);
        }

        return newMetric;
    }

    exports.generateSparkplugPayload = function(object) {
        var payload = new Payload(object.timestamp);
        console.log("object: " + JSON.stringify(object));

        // Build up the metric
        if (object.metrics !== undefined && object.metrics !== null) {
            var newMetrics = [],
                metrics = object.metrics;
            // loop over array of metric
            for (var i = 0; i < metrics.length; i++) {
                newMetrics.push(generateMetric(metrics[i]));
            }
            payload.metrics = newMetrics;
        }

        if (object.seq !== undefined && object.seq !== null) {
            payload.seq = object.seq;
        }

        if (object.uuid !== undefined && object.uuid !== null) {
            payload.uuid = object.uuid;
        }

        if (object.body !== undefined && object.body !== null) {
            payload.body = object.body;
        }

        return payload.toBuffer();
    }
}());
















