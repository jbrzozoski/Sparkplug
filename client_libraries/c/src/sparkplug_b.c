#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <pb_decode.h>
#include <pb_encode.h>
#include <sparkplug_b.h>
#include <sparkplug_b.pb.h>

// Global vars
uint64_t seq;

// Internal
int grow_array(com_cirruslink_sparkplug_protobuf_Payload_Metric **metric_array, int current_size, int num_new_elems) {
        const int total_size = current_size + num_new_elems;
        com_cirruslink_sparkplug_protobuf_Payload_Metric *temp = (com_cirruslink_sparkplug_protobuf_Payload_Metric*)realloc(*metric_array,
                                                                 (total_size * sizeof(com_cirruslink_sparkplug_protobuf_Payload_Metric)));

        if (temp == NULL) {
                printf("Cannot allocate more memory.\n");
                return 0;
        } else {
                *metric_array = temp;
        }

        return total_size;
}

// External
void add_metric(com_cirruslink_sparkplug_protobuf_Payload *payload,
			const char *name,
			bool has_alias,
			uint64_t alias,
			uint64_t datatype,
			bool is_historical,
			bool is_transient,
			bool is_null,
			const void *value,
			size_t size_of_value) {

	int size = payload->metrics_count;
	if (size == 0) {
		payload->metrics = (com_cirruslink_sparkplug_protobuf_Payload_Metric *) calloc(1, sizeof(com_cirruslink_sparkplug_protobuf_Payload_Metric));
		if(payload->metrics == NULL) {
			printf("Cannot allocate initial memory for data\n");
		} else {
			size = 1;
		}
	} else {
		size = grow_array(&payload->metrics, size, 1);
	}

	payload->metrics[size-1].name = (char *)malloc((strlen(name)+1)*sizeof(char));
	strcpy(payload->metrics[size-1].name, name);
	payload->metrics[size-1].has_alias = has_alias;
	if (has_alias) {
		payload->metrics[size-1].alias = alias;
	}
	payload->metrics[size-1].has_timestamp = true;
	payload->metrics[size-1].timestamp = get_current_timestamp();
	payload->metrics[size-1].has_datatype = true;
	payload->metrics[size-1].datatype = datatype;
	payload->metrics[size-1].has_is_historical = is_historical;
	if (is_historical) {
		payload->metrics[size-1].is_historical = is_historical;
	}
	payload->metrics[size-1].has_is_transient = is_transient;
	if (is_transient) {
		payload->metrics[size-1].is_transient = is_transient;
	}
	payload->metrics[size-1].has_is_null = is_null;
	if (is_null) {
		payload->metrics[size-1].is_null = is_null;
	}
	payload->metrics[size-1].has_metadata = false;
	payload->metrics[size-1].has_properties = false;

	// Default dynamically allocated members to NULL
	payload->metrics[size-1].value.string_value = NULL;

/* TODO
    bool has_metadata;
    com_cirruslink_sparkplug_protobuf_Payload_MetaData metadata;
    bool has_properties;
    com_cirruslink_sparkplug_protobuf_Payload_PropertySet properties;
*/

	DEBUG_PRINT(("Setting datatype and value - value size is %zd\n", size_of_value));
	if (datatype == METRIC_DATA_TYPE_UNKNOWN) {
		printf("Can't create metric with unknown datatype!\n");
	} else if (datatype == METRIC_DATA_TYPE_INT8 || datatype == METRIC_DATA_TYPE_INT16 || datatype == METRIC_DATA_TYPE_INT32 ||
			datatype == METRIC_DATA_TYPE_UINT8 || datatype == METRIC_DATA_TYPE_UINT16 || datatype == METRIC_DATA_TYPE_UINT32) {
		DEBUG_PRINT(("Setting datatype: %zd, with value: %d\n", datatype, *((uint32_t *)value)));
		payload->metrics[size-1].which_value = com_cirruslink_sparkplug_protobuf_Payload_Metric_int_value_tag;
		payload->metrics[size-1].value.int_value = *((uint32_t *)value);
	} else if (datatype == METRIC_DATA_TYPE_INT64 || datatype == METRIC_DATA_TYPE_UINT64 || datatype == METRIC_DATA_TYPE_DATETIME) {
		DEBUG_PRINT(("Setting datatype: %zd, with value: %zd\n", datatype, *((uint64_t *)value)));
		payload->metrics[size-1].which_value = com_cirruslink_sparkplug_protobuf_Payload_Metric_long_value_tag;
		payload->metrics[size-1].value.long_value = *((uint64_t *)value);
	} else if (datatype == METRIC_DATA_TYPE_FLOAT) {
		DEBUG_PRINT(("Setting datatype: %zd, with value: %f\n", datatype, *((float *)value)));
		payload->metrics[size-1].which_value = com_cirruslink_sparkplug_protobuf_Payload_Metric_float_value_tag;
		payload->metrics[size-1].value.float_value = *((float *)value);
	} else if (datatype == METRIC_DATA_TYPE_DOUBLE) {
		DEBUG_PRINT(("Setting datatype: %zd, with value: %f\n", datatype, *((double *)value)));
		payload->metrics[size-1].which_value = com_cirruslink_sparkplug_protobuf_Payload_Metric_double_value_tag;
		payload->metrics[size-1].value.double_value = *((double *)value);
	} else if (datatype == METRIC_DATA_TYPE_BOOLEAN) {
		DEBUG_PRINT(("Setting datatype: %zd, with value: %d\n", datatype, *((bool *)value)));
		payload->metrics[size-1].which_value = com_cirruslink_sparkplug_protobuf_Payload_Metric_boolean_value_tag;
		payload->metrics[size-1].value.boolean_value = *((bool *)value);
	} else if (datatype == METRIC_DATA_TYPE_STRING || datatype == METRIC_DATA_TYPE_TEXT || datatype == METRIC_DATA_TYPE_UUID) {
		DEBUG_PRINT(("Setting datatype: %zd, with value: %s\n", datatype, (char *)value));
		payload->metrics[size-1].which_value = com_cirruslink_sparkplug_protobuf_Payload_Metric_string_value_tag;
		payload->metrics[size-1].value.string_value = (char *)malloc(size_of_value*sizeof(char));
		strcpy(payload->metrics[size-1].value.string_value, (char *)value);
	} else if (datatype == METRIC_DATA_TYPE_BYTES) {
		DEBUG_PRINT(("Setting datatype: %zd, with value ", datatype));
		int i;
		for (i = 0; i<size_of_value; i++) {
			if (i > 0) DEBUG_PRINT((":"));
			DEBUG_PRINT(("%02X", ((pb_byte_t *)value)[i]));
		}
		DEBUG_PRINT(("\n"));

/*
		pb_bytes_array_t *bytes_value = (pb_bytes_array_t *)malloc(sizeof(pb_bytes_array_t));
		bytes_value->size = size_of_value;
		memcpy(bytes_value->bytes, (pb_byte_t *)value, size_of_value);
		payload->metrics[size-1].which_value = com_cirruslink_sparkplug_protobuf_Payload_Metric_bytes_value_tag;
		payload->metrics[size-1].value.bytes_value = &bytes_value;
*/

	} else if (datatype == METRIC_DATA_TYPE_DATASET) {
		DEBUG_PRINT(("Datatype DATASET - Not yet supported\n"));
	} else if (datatype == METRIC_DATA_TYPE_FILE) {
		DEBUG_PRINT(("Datatype FILE - Not yet supported\n"));
	} else if (datatype == METRIC_DATA_TYPE_TEMPLATE) {
		DEBUG_PRINT(("Datatype TEMPLATE - Not yet supported\n"));
	} else {
		DEBUG_PRINT(("Unknown datatype %ju\n", datatype));
	}

	payload->metrics_count++;
	DEBUG_PRINT(("Size of metrics payload %d\n", payload->metrics_count));
}

// Internal for now
void add_entire_metric(com_cirruslink_sparkplug_protobuf_Payload *payload, com_cirruslink_sparkplug_protobuf_Payload_Metric *metric) {

	int size = payload->metrics_count;
	if (size == 0) {
		payload->metrics = (com_cirruslink_sparkplug_protobuf_Payload_Metric *) calloc(1, sizeof(com_cirruslink_sparkplug_protobuf_Payload_Metric));
		if(payload->metrics == NULL) {
			printf("Cannot allocate initial memory for data\n");
		} else {
			size = 1;
		}
	} else {
		size = grow_array(&payload->metrics, size, 1);
	}

	// Assign the metric
	payload->metrics[payload->metrics_count] = *metric;

	// Increment the metric count
	payload->metrics_count++;
}

// External
void free_payload(com_cirruslink_sparkplug_protobuf_Payload *payload) {
	int i=0;
	for (i=0; i<payload->metrics_count; i++) {
		free(payload->metrics[i].name);

// More TODO...
	}
}

/***************************************************************************************************
 * Get current time in MS
 */
// External
uint64_t get_current_timestamp() {
	// Set the timestamp
	struct timespec ts;
	#ifdef __MACH__ // OS X does not have clock_gettime, use clock_get_time
		clock_serv_t cclock;
		mach_timespec_t mts;
		host_get_clock_service(mach_host_self(), CALENDAR_CLOCK, &cclock);
		clock_get_time(cclock, &mts);
		mach_port_deallocate(mach_task_self(), cclock);
		ts.tv_sec = mts.tv_sec;
		ts.tv_nsec = mts.tv_nsec;
	#else
		clock_gettime(CLOCK_REALTIME, &ts);
	#endif
	return ts.tv_sec * UINT64_C(1000) + ts.tv_nsec / 1000000;
}

// External
void print_payload(com_cirruslink_sparkplug_protobuf_Payload *payload) {
/*
        nbirth_payload.uuid = (char*)malloc((strlen("MyUUID")+1) * sizeof(char));
        strcpy(nbirth_payload.uuid, "MyUUID");
	strcpy(nbirth_payload.body, "Setting the body to some chars");
*/
	printf("Payload:  has_timestamp: %s\n", payload->has_timestamp ? "true" : "false");
	if (payload->has_timestamp) {
		printf("Payload:  timestamp: %zd\n", payload->timestamp);
	}
	printf("Payload:  has_seq: %s\n", payload->has_seq ? "true" : "false");
	if (payload->has_seq) {
		printf("Payload:  seq: %zd\n", payload->seq);
	}
	printf("Payload:  UUID: %s\n", payload->uuid);

	printf("Payload:  Size of metric array: %d\n", payload->metrics_count);
	int i=0;
	for (i=0; i<payload->metrics_count; i++) {
		printf("Payload:  Metric %d:  name: %s\n", i, payload->metrics[i].name);
		printf("Payload:  Metric %d:  has_alias: %s\n", i, payload->metrics[i].has_alias ? "true" : "false");
		if (payload->metrics[i].has_alias) {
			printf("Payload:  Metric %d:  alias: %zd\n", i, payload->metrics[i].alias);
		}
		printf("Payload:  Metric %d:  has_timestamp: %s\n", i, payload->metrics[i].has_timestamp ? "true" : "false");
		if (payload->metrics[i].has_timestamp) {
			printf("Payload:  Metric %d:  timestamp: %zd\n", i, payload->metrics[i].timestamp);
		}
		printf("Payload:  Metric %d:  has_datatype: %s\n", i, payload->metrics[i].has_datatype ? "true" : "false");
		if (payload->metrics[i].has_datatype) {
			printf("Payload:  Metric %d:  datatype: %d\n", i, payload->metrics[i].datatype);
		}
		printf("Payload:  Metric %d:  has_is_historical: %s\n", i, payload->metrics[i].has_is_historical ? "true" : "false");
		if (payload->metrics[i].has_is_historical) {
			printf("Payload:  Metric %d:  is_historical: %s\n", i, payload->metrics[i].is_historical ? "true" : "false");
		}
		printf("Payload:  Metric %d:  has_is_transient: %s\n", i, payload->metrics[i].has_is_transient ? "true" : "false");
		if (payload->metrics[i].has_is_transient) {
			printf("Payload:  Metric %d:  is_transient: %s\n", i, payload->metrics[i].is_transient ? "true" : "false");
		}
		printf("Payload:  Metric %d:  has_is_null: %s\n", i, payload->metrics[i].has_is_null ? "true" : "false");
		if (payload->metrics[i].has_is_null) {
			printf("Payload:  Metric %d:  is_null: %s\n", i, payload->metrics[i].is_null ? "true" : "false");
		}
		printf("Payload:  Metric %d:  has_metadata: %s\n", i, payload->metrics[i].has_metadata ? "true" : "false");
		printf("Payload:  Metric %d:  has_properties: %s\n", i, payload->metrics[i].has_properties ? "true" : "false");

		if (payload->metrics[i].datatype == METRIC_DATA_TYPE_UNKNOWN) {
			printf("Payload:  Metric %d:  datatype: unknown datatype!\n", i);
		} else if (payload->metrics[i].datatype == METRIC_DATA_TYPE_INT8 ||
					payload->metrics[i].datatype == METRIC_DATA_TYPE_INT16 ||
					payload->metrics[i].datatype == METRIC_DATA_TYPE_INT32 ||
					payload->metrics[i].datatype == METRIC_DATA_TYPE_UINT8 ||
					payload->metrics[i].datatype == METRIC_DATA_TYPE_UINT16 ||
					payload->metrics[i].datatype == METRIC_DATA_TYPE_UINT32) {
			printf("Payload:  Metric %d:  datatype: %d, with value: %d\n", i, payload->metrics[i].datatype, payload->metrics[i].value.int_value);
		} else if (payload->metrics[i].datatype == METRIC_DATA_TYPE_INT64 ||
					payload->metrics[i].datatype == METRIC_DATA_TYPE_UINT64 ||
					payload->metrics[i].datatype == METRIC_DATA_TYPE_DATETIME) {
			printf("Payload:  Metric %d:  datatype: %d, with value: %zd\n", i, payload->metrics[i].datatype, payload->metrics[i].value.long_value);
		} else if (payload->metrics[i].datatype == METRIC_DATA_TYPE_FLOAT) {
			printf("Payload:  Metric %d:  datatype: %d, with value: %f\n", i, payload->metrics[i].datatype, payload->metrics[i].value.float_value);
		} else if (payload->metrics[i].datatype == METRIC_DATA_TYPE_DOUBLE) {
			printf("Payload:  Metric %d:  datatype: %d, with value: %f\n", i, payload->metrics[i].datatype, payload->metrics[i].value.double_value);
		} else if (payload->metrics[i].datatype == METRIC_DATA_TYPE_BOOLEAN) {
			printf("Payload:  Metric %d:  datatype: %d, with value: %d\n", i, payload->metrics[i].datatype, payload->metrics[i].value.boolean_value);
		} else if (payload->metrics[i].datatype == METRIC_DATA_TYPE_STRING ||
					payload->metrics[i].datatype == METRIC_DATA_TYPE_TEXT ||
					payload->metrics[i].datatype == METRIC_DATA_TYPE_UUID) {
			printf("Payload:  Metric %d:  datatype: %d, with value: %s\n", i, payload->metrics[i].datatype, payload->metrics[i].value.string_value);
		} else if (payload->metrics[i].datatype == METRIC_DATA_TYPE_BYTES) {
			printf("Payload:  Metric %d:  datatype: %d, with value ", i, payload->metrics[i].datatype);
/*
			int i;
			for (i = 0; i<sizeof(payload->metrics[i].value.bytes_value); i++) {
				if (i > 0) printf(":");
				printf("%02X", payload->metrics[i].value.bytes_value[i]);
			}
*/
			printf("\n");
		} else if (payload->metrics[i].datatype == METRIC_DATA_TYPE_DATASET) {
			printf("Payload:  Metric %d:  datatype DATASET - Not yet supported\n", i);
		} else if (payload->metrics[i].datatype == METRIC_DATA_TYPE_FILE) {
			printf("Payload:  Metric %d:  datatype FILE - Not yet supported\n", i);
		} else if (payload->metrics[i].datatype == METRIC_DATA_TYPE_TEMPLATE) {
			printf("Payload:  Metric %d:  datatype TEMPLATE - Not yet supported\n", i);
		} else {
			printf("Payload:  Metric %d:  datatype: %d\n", i, payload->metrics[i].datatype);
		}
	}
}

// External
size_t encode_payload(uint8_t **buffer, size_t buffer_length, com_cirruslink_sparkplug_protobuf_Payload *payload) {
        size_t message_length;
	bool node_status;

	// Create the stream
	pb_ostream_t node_stream = pb_ostream_from_buffer(*buffer, buffer_length);

	// Encode the payload
	DEBUG_PRINT(("Encoding...\n"));
	node_status = pb_encode(&node_stream, com_cirruslink_sparkplug_protobuf_Payload_fields, payload);
	message_length = node_stream.bytes_written;
	DEBUG_PRINT(("Message length: %zd\n", message_length));

        // Error Check
        if (!node_status) {
                printf("Encoding failed: %s\n", PB_GET_ERROR(&node_stream));
                return -1;
        } else {
                DEBUG_PRINT(("Encoding succeeded\n"));
		return message_length;
        }
}

// External
void get_next_payload(com_cirruslink_sparkplug_protobuf_Payload *payload) {
	// Initialize payload
	DEBUG_PRINT(("Current Sequence Number: %zd\n", seq));
	payload->has_timestamp = true;
	payload->timestamp = get_current_timestamp();
	payload->metrics_count = 0;
	payload->metrics = NULL;
	payload->has_seq = true;
	payload->seq = seq;
	payload->uuid = NULL;
	payload->body = NULL;
	payload->extensions = NULL;

	// Increment/wrap the sequence number
	if (seq == 256) {
		seq = 0;
	} else {
		seq++;
	}
}

// Internal
bool decode_metric(com_cirruslink_sparkplug_protobuf_Payload_Metric *metric, pb_istream_t *stream) {
	bool status;
	pb_istream_t substream;

	if (!pb_make_string_substream(stream, &substream)) {
		printf("FAILED\n");
		return false;
	}

	pb_wire_type_t metric_wire_type;
	uint32_t metric_tag;
	bool metric_eof;
	const pb_field_t *metric_field;

	while (pb_decode_tag(&substream, &metric_wire_type, &metric_tag, &metric_eof)) {
		DEBUG_PRINT(("\teof: %s\n", metric_eof ? "true" : "false"));
		DEBUG_PRINT(("\t\tBytes Remaining: %zd\n", substream.bytes_left));
		DEBUG_PRINT(("\t\tWiretype: %d\n", metric_wire_type));
		DEBUG_PRINT(("\t\tTag: %d\n", metric_tag));

		if (metric_wire_type == PB_WT_VARINT) {
			DEBUG_PRINT(("\t\tMetric Wire type is PB_WT_VARINT\n"));
			for (metric_field = com_cirruslink_sparkplug_protobuf_Payload_Metric_fields; metric_field->tag != 0; metric_field++) {
				if (metric_field->tag == metric_tag && (((metric_field->type & PB_LTYPE_VARINT) == PB_LTYPE_VARINT) ||
													((metric_field->type & PB_LTYPE_UVARINT) == PB_LTYPE_UVARINT))) {
					DEBUG_PRINT(("\t\tWire type is PB_WT_VARINT\n"));
					uint64_t dest;
					status = pb_decode_varint(&substream, &dest);
					if (status) {
						DEBUG_PRINT(("\t\tVARINT - Success - new value: %ld\n", dest));
						if (metric_field->tag == com_cirruslink_sparkplug_protobuf_Payload_Metric_timestamp_tag) {
							metric->has_timestamp = true;
							metric->timestamp = dest;
						} else if (metric_field->tag == com_cirruslink_sparkplug_protobuf_Payload_Metric_datatype_tag) {
							metric->has_datatype = true;
							metric->datatype = dest;
						} else if (metric_field->tag == com_cirruslink_sparkplug_protobuf_Payload_Metric_is_historical_tag) {
							metric->has_is_historical = true;
							metric->is_historical = dest;
						} else if (metric_field->tag == com_cirruslink_sparkplug_protobuf_Payload_Metric_is_transient_tag) {
							metric->has_is_transient = true;
							metric->is_transient = dest;
						} else if (metric_field->tag == com_cirruslink_sparkplug_protobuf_Payload_Metric_is_null_tag) {
							metric->has_is_null = true;
							metric->is_null = dest;
						} else if (metric_field->tag == com_cirruslink_sparkplug_protobuf_Payload_Metric_int_value_tag) {
							metric->which_value = com_cirruslink_sparkplug_protobuf_Payload_Metric_int_value_tag;
							metric->value.int_value = dest;
						} else if (metric_field->tag == com_cirruslink_sparkplug_protobuf_Payload_Metric_long_value_tag) {
							metric->which_value = com_cirruslink_sparkplug_protobuf_Payload_Metric_long_value_tag;
							metric->value.long_value = dest;
						} else if (metric_field->tag == com_cirruslink_sparkplug_protobuf_Payload_Metric_boolean_value_tag) {
							metric->which_value = com_cirruslink_sparkplug_protobuf_Payload_Metric_boolean_value_tag;
							metric->value.boolean_value = dest;
						}
					} else {
						printf("\t\tVARINT - Failed to decode variant!\n");
						return false;
					}
				} else if (metric_field->tag == metric_tag && ((metric_field->type & PB_LTYPE_SVARINT) == PB_LTYPE_SVARINT)) {
					DEBUG_PRINT(("\t\tWire type is PB_WT_SVARINT\n"));
					int64_t dest;
					status = pb_decode_svarint(&substream, &dest);
					if (status) {
						DEBUG_PRINT(("\t\tVARINT - Success - new value: %ld\n", dest));
					} else {
						printf("\t\tVARINT - Failed to decode variant!\n");
						return false;
					}
				}
			}
		} else if (metric_wire_type == PB_WT_64BIT) {
			DEBUG_PRINT(("\t\tMetric Wire type is PB_WT_64BIT\n"));
		} else if (metric_wire_type == PB_WT_STRING) {
			DEBUG_PRINT(("\t\tMetric Wire type is PB_WT_STRING\n"));

			for (metric_field = com_cirruslink_sparkplug_protobuf_Payload_Metric_fields; metric_field->tag != 0; metric_field++) {
				if (metric_field->tag == metric_tag && ((metric_field->type & PB_LTYPE_SUBMESSAGE) == PB_LTYPE_SUBMESSAGE)) {
					DEBUG_PRINT(("\t\tFound a PB_LTYPE_SUBMESSAGE\n"));
				} else if (metric_field->tag == metric_tag &&
							((metric_field->type & PB_LTYPE_FIXED_LENGTH_BYTES) == PB_LTYPE_FIXED_LENGTH_BYTES)) {
					DEBUG_PRINT(("\t\tFound a PB_LTYPE_FIXED_LENGTH_BYTES\n"));
				} else if (metric_field->tag == metric_tag && ((metric_field->type & PB_LTYPE_STRING) == PB_LTYPE_STRING)) {
					DEBUG_PRINT(("\t\tFound a PB_LTYPE_STRING\n"));

					// Get the string size
					pb_byte_t string_size[1];
					status = pb_read(&substream, string_size, 1);
					if (status) {
						DEBUG_PRINT(("\t\tString Size: %d\n", string_size[0]));
					} else {
						printf("\t\tFailed to get the size\n");
						return false;
					}

					pb_byte_t dest[string_size[0]+1];
					status = pb_read(&substream, dest, string_size[0]);
					if (status) {
						dest[string_size[0]] = '\0';

						// This is either the metric name or string value
						if (metric_field->tag == com_cirruslink_sparkplug_protobuf_Payload_Metric_name_tag) {
							DEBUG_PRINT(("\t\tRead the Metric name! %s\n", dest));
							metric->name = (char *)malloc((strlen(dest)+1)*sizeof(char));
							strcpy(metric->name, dest);
						} else if (metric_field->tag == com_cirruslink_sparkplug_protobuf_Payload_Metric_string_value_tag) {
							DEBUG_PRINT(("\t\tRead the Metric string_value! %s\n", dest));
							metric->which_value = com_cirruslink_sparkplug_protobuf_Payload_Metric_string_value_tag;
							metric->value.string_value = dest;
						}
					} else {
						printf("\t\tFailed to read the String...\n");
						return false;
					}
				} else if (metric_field->tag == metric_tag && ((metric_field->type & PB_LTYPE_BYTES) == PB_LTYPE_BYTES)) {
					DEBUG_PRINT(("\t\tFound a PB_LTYPE_BYTES\n"));
//				} else {
//					DEBUG_PRINT(("\t\tother: %d\n", metric_field->type);
				}
			}

		} else if (metric_wire_type == PB_WT_32BIT) {
			DEBUG_PRINT(("\t\tMetric Wire type is PB_WT_32BIT\n"));
//		} else {
//			DEBUG_PRINT(("\t\tMetric Other? %d\n", metric_wire_type);
		}
	}

	// Close the substream
	pb_close_string_substream(stream, &substream);
}

// External
bool decode_payload(com_cirruslink_sparkplug_protobuf_Payload *payload, const void *binary_payload, int binary_payloadlen) {

	pb_istream_t stream = pb_istream_from_buffer(binary_payload, binary_payloadlen);
	DEBUG_PRINT(("Bytes Remaining: %zd\n", stream.bytes_left));

	// Local vars for payload decoding
	bool status;
	pb_wire_type_t payload_wire_type;
	uint32_t payload_tag;
	bool payload_eof;
	const pb_field_t *payload_field;

	// Loop over blocks while decoding portions of the payload
	while (pb_decode_tag(&stream, &payload_wire_type, &payload_tag, &payload_eof)) {
		DEBUG_PRINT(("payload_eof: %s\n", payload_eof ? "true" : "false"));
		DEBUG_PRINT(("\tBytes Remaining: %zd\n", stream.bytes_left));
		DEBUG_PRINT(("\tWiretype: %d\n", payload_wire_type));
		DEBUG_PRINT(("\tTag: %d\n", payload_tag));

/*
uint64	timestamp	field->tag: 1, field->type: 17		0001 0001		PB_LTYPE_UVARINT
Metric	metrics		field->tag: 2, field->type: 167		1010 0111		PB_LTYPE_SUBMESSAGE
uint64	seq		field->tag: 3, field->type: 17		0001 0001		PB_LTYPE_UVARINT
string	uuid		field->tag: 4, field->type: 150		1001 0110		PB_LTYPE_STRING
bytes	body		field->tag: 5, field->type: 149		1001 0101		PB_LTYPE_BYTES
extensions		field->tag: 6, field->type: 88		0101 1000		PB_LTYPE_EXTENSION
*/

		if (payload_wire_type == PB_WT_VARINT) {
			for (payload_field = com_cirruslink_sparkplug_protobuf_Payload_fields; payload_field->tag != 0; payload_field++) {
				if (payload_field->tag == payload_tag && (((payload_field->type & PB_LTYPE_VARINT) == PB_LTYPE_VARINT) ||
										((payload_field->type & PB_LTYPE_UVARINT) == PB_LTYPE_UVARINT))) {
					DEBUG_PRINT(("\tWire type is PB_WT_VARINT\n"));
					uint64_t dest;
					status = pb_decode_varint(&stream, &dest);
					if (status) {
						DEBUG_PRINT(("\tVARINT - Success - new value: %ld\n", dest));
					} else {
						printf("\tVARINT - Failed to decode variant!\n");
						return false;
					}

					if (payload_field->tag == com_cirruslink_sparkplug_protobuf_Payload_timestamp_tag) {
						payload->has_timestamp = true;
						payload->timestamp = dest;
					} else if (payload_field->tag == com_cirruslink_sparkplug_protobuf_Payload_seq_tag) {
						payload->has_seq = true;
						payload->seq = dest;
					}
				} else if (payload_field->tag == payload_tag && ((payload_field->type & PB_LTYPE_SVARINT) == PB_LTYPE_SVARINT)) {
					DEBUG_PRINT(("\tWire type is PB_WT_SVARINT\n"));
					int64_t dest;
					status = pb_decode_svarint(&stream, &dest);
					if (status) {
						DEBUG_PRINT(("\tVARINT - Success - new value: %ld\n", dest));
					} else {
						printf("\tVARINT - Failed to decode variant!\n");
						return false;
					}
				}
			}
		} else if (payload_wire_type == PB_WT_64BIT) {
			DEBUG_PRINT(("\tWire type is PB_WT_64BIT\n"));
		} else if (payload_wire_type == PB_WT_STRING) {
			DEBUG_PRINT(("\tWire type is PB_WT_STRING\n"));
			for (payload_field = com_cirruslink_sparkplug_protobuf_Payload_fields; payload_field->tag != 0; payload_field++) {
				if (payload_field->tag == payload_tag && ((payload_field->type & PB_LTYPE_SUBMESSAGE) == PB_LTYPE_SUBMESSAGE)) {
					DEBUG_PRINT(("\tFound a PB_LTYPE_SUBMESSAGE\n"));

					// This is a metric!
					if (payload_field->ptr == NULL) {
						printf("invalid field descriptor\n");
						return false;
					}

					com_cirruslink_sparkplug_protobuf_Payload_Metric metric = com_cirruslink_sparkplug_protobuf_Payload_Metric_init_zero;
					if(decode_metric(&metric, &stream)) {
						DEBUG_PRINT(("DECODING METRIC SUCCEEDED\n"));
						add_entire_metric(payload, &metric);
					} else {
						printf("DECODING METRIC FAILED\n");
						return false;
					}
				} else if (payload_field->tag == payload_tag && ((payload_field->type & PB_LTYPE_FIXED_LENGTH_BYTES) == PB_LTYPE_FIXED_LENGTH_BYTES)) {
					DEBUG_PRINT(("\tFound a PB_LTYPE_FIXED_LENGTH_BYTES\n"));
				} else if (payload_field->tag == payload_tag && ((payload_field->type & PB_LTYPE_STRING) == PB_LTYPE_STRING)) {
					DEBUG_PRINT(("\tFound a PB_LTYPE_STRING\n"));

					// Get the UUID
					pb_byte_t string_size[1];
					status = pb_read(&stream, string_size, 1);
					if (status) {
						DEBUG_PRINT(("\t\tUUID Size: %d\n", string_size[0]));
					} else {
						printf("\t\tFailed to read the UUID\n");
						return false;
					}

					pb_byte_t dest[string_size[0]+1];
					status = pb_read(&stream, dest, string_size[0]);
					if (status) {
						dest[string_size[0]] = '\0';
						DEBUG_PRINT(("\t\tRead the UUID: %s\n", dest));
						payload->uuid = (char *)malloc((strlen(dest)+1)*sizeof(char));;
						strcpy(payload->uuid, dest);
					} else {
						printf("\t\tFailed to read the UUID...\n");
						return false;
					}


				} else if (payload_field->tag == payload_tag && ((payload_field->type & PB_LTYPE_BYTES) == PB_LTYPE_BYTES)) {
					DEBUG_PRINT(("\tFound a PB_LTYPE_BYTES\n"));
//				} else {
//					DEBUG_PRINT(("\tother: %d\n", payload_field->type);
				}
			}
		} else if (payload_wire_type == PB_WT_32BIT) {
			DEBUG_PRINT(("\tWire type is PB_WT_32BIT\n"));
		} else {
			printf("\tUnknown wiretype...\n");
		}
	}

	// Print the message data
	print_payload(payload);
	return true;
}
