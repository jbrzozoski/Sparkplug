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

#include <sparkplug_b.pb.h>

#include <time.h>
#include <sys/time.h>

#ifdef __MACH__
#include <mach/clock.h>
#include <mach/mach.h>
#endif

#ifndef _SPARKPLUGLIB_H_
#define _SPARKPLUGLIB_H_

	// Enable/disable debug messages
	#define SPARKPLUG_DEBUG 1

	#ifdef SPARKPLUG_DEBUG
		#define DEBUG_PRINT(x) printf x
	#else
		#define DEBUG_PRINT(x) do {} while (0)
	#endif

	// Constants
	#define DATA_SET_DATA_TYPE_UNKNOWN 0
	#define DATA_SET_DATA_TYPE_INT8 1
	#define DATA_SET_DATA_TYPE_INT16 2
	#define DATA_SET_DATA_TYPE_INT32 3
	#define DATA_SET_DATA_TYPE_INT64 4
	#define DATA_SET_DATA_TYPE_UINT8 5
	#define DATA_SET_DATA_TYPE_UINT16 6
	#define DATA_SET_DATA_TYPE_UINT32 7
	#define DATA_SET_DATA_TYPE_UINT64 8
	#define DATA_SET_DATA_TYPE_FLOAT 9
	#define DATA_SET_DATA_TYPE_DOUBLE 10
	#define DATA_SET_DATA_TYPE_BOOLEAN 11
	#define DATA_SET_DATA_TYPE_STRING 12
	#define DATA_SET_DATA_TYPE_DATETIME 13
	#define DATA_SET_DATA_TYPE_TEXT 14

	#define METRIC_DATA_TYPE_UNKNOWN 0
	#define METRIC_DATA_TYPE_INT8 1
	#define METRIC_DATA_TYPE_INT16 2
	#define METRIC_DATA_TYPE_INT32 3
	#define METRIC_DATA_TYPE_INT64 4
	#define METRIC_DATA_TYPE_UINT8 5
	#define METRIC_DATA_TYPE_UINT16 6
	#define METRIC_DATA_TYPE_UINT32 7
	#define METRIC_DATA_TYPE_UINT64 8
	#define METRIC_DATA_TYPE_FLOAT 9
	#define METRIC_DATA_TYPE_DOUBLE 10
	#define METRIC_DATA_TYPE_BOOLEAN 11
	#define METRIC_DATA_TYPE_STRING 12
	#define METRIC_DATA_TYPE_DATETIME 13
	#define METRIC_DATA_TYPE_TEXT 14
	#define METRIC_DATA_TYPE_UUID 15
	#define METRIC_DATA_TYPE_DATASET 16
	#define METRIC_DATA_TYPE_BYTES 17
	#define METRIC_DATA_TYPE_FILE 18
	#define METRIC_DATA_TYPE_TEMPLATE 19

	#define PARAMETER_DATA_TYPE_UNKNOWN 0
	#define PARAMETER_DATA_TYPE_INT8 1
	#define PARAMETER_DATA_TYPE_INT16 2
	#define PARAMETER_DATA_TYPE_INT32 3
	#define PARAMETER_DATA_TYPE_INT64 4
	#define PARAMETER_DATA_TYPE_UINT8 5
	#define PARAMETER_DATA_TYPE_UINT16 6
	#define PARAMETER_DATA_TYPE_UINT32 7
	#define PARAMETER_DATA_TYPE_UINT64 8
	#define PARAMETER_DATA_TYPE_FLOAT 9
	#define PARAMETER_DATA_TYPE_DOUBLE 10
	#define PARAMETER_DATA_TYPE_BOOLEAN 11
	#define PARAMETER_DATA_TYPE_STRING 12
	#define PARAMETER_DATA_TYPE_DATETIME 13
	#define PARAMETER_DATA_TYPE_TEXT 14

	#define PROPERTY_DATA_TYPE_UNKNOWN 0
	#define PROPERTY_DATA_TYPE_INT8 1
	#define PROPERTY_DATA_TYPE_INT16 2
	#define PROPERTY_DATA_TYPE_INT32 3
	#define PROPERTY_DATA_TYPE_INT64 4
	#define PROPERTY_DATA_TYPE_UINT8 5
	#define PROPERTY_DATA_TYPE_UINT16 6
	#define PROPERTY_DATA_TYPE_UINT32 7
	#define PROPERTY_DATA_TYPE_UINT64 8
	#define PROPERTY_DATA_TYPE_FLOAT 9
	#define PROPERTY_DATA_TYPE_DOUBLE 10
	#define PROPERTY_DATA_TYPE_BOOLEAN 11
	#define PROPERTY_DATA_TYPE_STRING 12
	#define PROPERTY_DATA_TYPE_DATETIME 13
	#define PROPERTY_DATA_TYPE_TEXT 14

	/*
	 * Global variables
	 */
	extern uint64_t seq;		// The sequence number is globaly accessible to the applications

	/*
	 * Add Metadata to an existing Metric
	 */
	extern void add_metadata_to_metric(com_cirruslink_sparkplug_protobuf_Payload_Metric *metric,
						com_cirruslink_sparkplug_protobuf_Payload_MetaData *metadata);

	/*
	 * Add a complete Metric to an existing Payload
	 */
	extern void add_metric_to_payload(com_cirruslink_sparkplug_protobuf_Payload *payload,
						com_cirruslink_sparkplug_protobuf_Payload_Metric *metric);

	/*
	 * Add a simple Property to an existing PropertySet
	 */
	extern bool add_property_to_set(com_cirruslink_sparkplug_protobuf_Payload_PropertySet *propertyset,
					const char *key,
					uint32_t type,
					bool is_null,
					const void *value,
					size_t size_of_value);

	/*
	 * Add a PropertySet to an existing Metric
	 */
	extern void add_propertyset_to_metric(com_cirruslink_sparkplug_protobuf_Payload_Metric *metric,
						com_cirruslink_sparkplug_protobuf_Payload_PropertySet *properties);

	/*
	 * Add a simple Metric to an existing Payload
	 */
	extern void add_simple_metric(com_cirruslink_sparkplug_protobuf_Payload *payload,
					const char *name,
					bool has_alias,
					uint64_t alias,
					uint64_t datatype,
					bool is_historical,
					bool is_transient,
					bool is_null,
					const void *value,
					size_t size_of_value);

	/*
	 * Encode a Payload into an array of bytes
	 */
	extern size_t encode_payload(uint8_t **buffer,
					size_t buffer_length,
					com_cirruslink_sparkplug_protobuf_Payload *payload);

	/*
	 * Decode an array of bytes into a Payload
	 */
	extern bool decode_payload(com_cirruslink_sparkplug_protobuf_Payload *payload,
					const void *binary_payload,
					int binary_payloadlen);

	/*
	 * Free memory from an existing Payload
	 */
	void free_payload(com_cirruslink_sparkplug_protobuf_Payload *payload);

	/*
	 * Get the current timestamp in milliseconds
	 */
	extern uint64_t get_current_timestamp();

	/*
	 * Get the next empty Payload.  This populates the payload with the next sequence number and current timestamp
	 */
	extern void get_next_payload(com_cirruslink_sparkplug_protobuf_Payload *payload);

	/*
	 * Initialize a Dataset with the values passed in
	 */
	extern void init_dataset(com_cirruslink_sparkplug_protobuf_Payload_DataSet *dataset,
					uint64_t num_of_rows,
					uint64_t num_of_columns,
					uint32_t *datatypes,
					const char **column_keys,
					com_cirruslink_sparkplug_protobuf_Payload_DataSet_Row *row_data);

	/*
	 * Initialize a Metric with the values of the arguments passed in
	 */
	extern void init_metric(com_cirruslink_sparkplug_protobuf_Payload_Metric *metric,
	                        const char *name,
        	                bool has_alias,
        	                uint64_t alias,
        	                uint64_t datatype,
        	                bool is_historical,
        	                bool is_transient,
        	                bool is_null,
        	                const void *value,
        	                size_t size_of_value);

	// Display a full Sparkplug Payload
	extern void print_payload(com_cirruslink_sparkplug_protobuf_Payload *payload);

#endif
