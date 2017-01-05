#include <sparkplug_b.pb.h>

#include <time.h>
#include <sys/time.h>

#ifdef __MACH__
#include <mach/clock.h>
#include <mach/mach.h>
#endif

#ifndef _SPARKPLUGLIB_H_
#define _SPARKPLUGLIB_H_

	// Constants
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

	// Global vars
	extern uint64_t seq;

	// Add a metric to an existing Payload
	extern void add_metric(com_cirruslink_sparkplug_protobuf_Payload *payload,
				const char *name,
				bool has_alias,
				uint64_t alias,
				uint64_t datatype,
				bool is_historical,
				bool is_transient,
				bool is_null,
				const void *value,
				size_t size_of_value);

	void free_payload(com_cirruslink_sparkplug_protobuf_Payload *payload);

	// Get the current timestamp in milliseconds
	extern uint64_t get_current_timestamp();

	// Display a full Sparkplug Payload
	extern void print_payload(com_cirruslink_sparkplug_protobuf_Payload *payload);

	// Encode a payload
	extern size_t encode_payload(uint8_t **buffer, size_t buffer_length, com_cirruslink_sparkplug_protobuf_Payload *payload);

	// Get the next empty payload
	extern void get_next_payload(com_cirruslink_sparkplug_protobuf_Payload *payload);
#endif
