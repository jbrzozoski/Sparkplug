#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <sparkplug_b.h>
#include <sparkplug_b.pb.h>
#include <pb_decode.h>
#include <pb_encode.h>
#include <mosquitto.h>
#include <inttypes.h>

/* Mosquitto Callbacks */
void my_message_callback(struct mosquitto *mosq, void *userdata, const struct mosquitto_message *message);
void my_connect_callback(struct mosquitto *mosq, void *userdata, int result);
void my_subscribe_callback(struct mosquitto *mosq, void *userdata, int mid, int qos_count, const int *granted_qos);
void my_log_callback(struct mosquitto *mosq, void *userdata, int level, const char *str);

/* Local Functions */
void publisher(struct mosquitto *mosq, char *topic, void *buf, unsigned len);
void publish_births(struct mosquitto *mosq);
void publish_node_birth(struct mosquitto *mosq);
void publish_device_birth(struct mosquitto *mosq);

int main(int argc, char *argv[]) {

				const pb_field_t *field;
				for (field = com_cirruslink_sparkplug_protobuf_Payload_fields; field->tag != 0; field++) {
					printf("field->tag: %d, field->type: %d\n", field->tag, field->type);
				}
				for (field = com_cirruslink_sparkplug_protobuf_Payload_Metric_fields; field->tag != 0; field++) {
					printf("field->tag: %d, field->type: %d\n", field->tag, field->type);
				}


	// MQTT Stuff
        char *host = "cl-target1.chariot.io";
        int port = 1883;
        int keepalive = 60;
        bool clean_session = true;
        struct mosquitto *mosq = NULL;

	// Init the sequence number for Sparkplug MQTT messages
	seq = 0;

	// MQTT Setup
        srand(time(NULL));

        mosquitto_lib_init();
        mosq = mosquitto_new(NULL, clean_session, NULL);
        if(!mosq){
                fprintf(stderr, "Error: Out of memory.\n");
                return 1;
        }
        mosquitto_log_callback_set(mosq, my_log_callback);
        mosquitto_connect_callback_set(mosq, my_connect_callback);
        mosquitto_message_callback_set(mosq, my_message_callback);
        mosquitto_subscribe_callback_set(mosq, my_subscribe_callback);
        mosquitto_username_pw_set(mosq,"CLAdmin","CLAdm79!");
        mosquitto_will_set(mosq, "spBv1.0/Sparkplug B Devices/NDEATH/C Edge Node 1", 0, NULL, 0, false);

//      mosquitto_tls_insecure_set(mosq, true);
//      mosquitto_tls_opts_set(mosq, 0, "tlsv1.2", NULL);               // 0 is DO NOT SSL_VERIFY_PEER

        if(mosquitto_connect(mosq, host, port, keepalive)){
                fprintf(stderr, "Unable to connect.\n");
                return 1;
        }

	// Publish the NBIRTH and DBIRTH messages (Birth Certificates)
	publish_births(mosq);

        // Loop and publish more DDATA messages
        int i;
        for(i=0; i<100; i++) {
/*
                Kuradatatypes__KuraPayload payload;
                payload = getNextPayload(false);

                //printf("data size %zu\n", kuradatatypes__kura_payload__get_packed_size(&payload));
                publisher(mosq, "spAv1.0/Sparkplug B Devices/DDATA/C Edge Node 1/Emulated Device", payload);
                //freePayload(&payload);
*/
                int j;
                for(j=0; j<50; j++) {
                        usleep(100000);
                        mosquitto_loop(mosq, 0, 1);
                }
        }

        mosquitto_destroy(mosq);
        mosquitto_lib_cleanup();
        return 0;
}

void publisher(struct mosquitto *mosq, char *topic, void *buf, unsigned len) {
	// publish the data
	mosquitto_publish(mosq, NULL, topic, len, buf, 0, false);
}

void my_message_callback(struct mosquitto *mosq, void *userdata, const struct mosquitto_message *message) {

	if(message->payloadlen) {
		printf("%s :: %d\n", message->topic, message->payloadlen);
	} else {
		printf("%s (null)\n", message->topic);
	}
	fflush(stdout);

	// Do the decoding
	com_cirruslink_sparkplug_protobuf_Payload inbound_payload = com_cirruslink_sparkplug_protobuf_Payload_init_zero;
	pb_istream_t stream = pb_istream_from_buffer(message->payload, message->payloadlen);

//	bool status = pb_decode(&stream, com_cirruslink_sparkplug_protobuf_Payload_fields, &inbound_payload);
	bool status = false;

	if (!status) {
		printf("Decoding failed: %s\n", PB_GET_ERROR(&stream));
		printf("Bytes Remaining: %zd\n", stream.bytes_left);

		pb_wire_type_t payload_wire_type;
		uint32_t payload_tag;
		bool payload_eof;
		const pb_field_t *payload_field;

		while (pb_decode_tag(&stream, &payload_wire_type, &payload_tag, &payload_eof)) {
			printf("payload_eof: %s\n", payload_eof ? "true" : "false");
			printf("\tBytes Remaining: %zd\n", stream.bytes_left);
			printf("\tWiretype: %d\n", payload_wire_type);
			printf("\tTag: %d\n", payload_tag);

			if (payload_wire_type == PB_WT_VARINT) {
				for (payload_field = com_cirruslink_sparkplug_protobuf_Payload_fields; payload_field->tag != 0; payload_field++) {
					if (payload_field->tag == payload_tag && (((payload_field->type & PB_LTYPE_VARINT) == PB_LTYPE_VARINT) ||
											((payload_field->type & PB_LTYPE_UVARINT) == PB_LTYPE_UVARINT))) {
						printf("\tWire type is PB_WT_VARINT\n");
						uint64_t dest;
						status = pb_decode_varint(&stream, &dest);
						if (status) {
							printf("\tVARINT - Success - new value: %ld\n", dest);
						} else {
							printf("\tVARINT - Failed to decode variant!\n");
						}
					} else if (payload_field->tag == payload_tag && ((payload_field->type & PB_LTYPE_SVARINT) == PB_LTYPE_SVARINT)) {
						printf("\tWire type is PB_WT_SVARINT\n");
						int64_t dest;
						status = pb_decode_svarint(&stream, &dest);
						if (status) {
							printf("\tVARINT - Success - new value: %ld\n", dest);
						} else {
							printf("\tVARINT - Failed to decode variant!\n");
						}
					}
				}
			} else if (payload_wire_type == PB_WT_64BIT) {
				printf("\tWire type is PB_WT_64BIT\n");
			} else if (payload_wire_type == PB_WT_STRING) {
				printf("\tWire type is PB_WT_STRING\n");
				for (payload_field = com_cirruslink_sparkplug_protobuf_Payload_fields; payload_field->tag != 0; payload_field++) {
					if (payload_field->tag == payload_tag && ((payload_field->type & PB_LTYPE_SUBMESSAGE) == PB_LTYPE_SUBMESSAGE)) {
						printf("\tFound a PB_LTYPE_SUBMESSAGE\n");

						// Found a metric - get the ptr to it
						com_cirruslink_sparkplug_protobuf_Payload_Metric metric = com_cirruslink_sparkplug_protobuf_Payload_Metric_init_zero;
						//printf("\t\tsizeof(field->ptr): %zd\n", sizeof(field->ptr));

						pb_istream_t substream;
						const pb_field_t *submsg_fields = (const pb_field_t *)payload_field->ptr;
						if (!pb_make_string_substream(&stream, &substream)) {
							printf("FAILED\n");
						}

						if (payload_field->ptr == NULL) {
							printf("invalid field descriptor\n");
						}

						pb_wire_type_t metric_wire_type;
						uint32_t metric_tag;
						bool metric_eof;
						const pb_field_t *metric_field;

						while (pb_decode_tag(&substream, &metric_wire_type, &metric_tag, &metric_eof)) {
							printf("\teof: %s\n", metric_eof ? "true" : "false");
							printf("\t\tBytes Remaining: %zd\n", substream.bytes_left);
							printf("\t\tWiretype: %d\n", metric_wire_type);
							printf("\t\tTag: %d\n", metric_tag);

							if (metric_wire_type == PB_WT_VARINT) {
								printf("\t\tMetric Wire type is PB_WT_VARINT\n");
								for (metric_field = com_cirruslink_sparkplug_protobuf_Payload_Metric_fields; metric_field->tag != 0; metric_field++) {
									if (metric_field->tag == metric_tag && (((metric_field->type & PB_LTYPE_VARINT) == PB_LTYPE_VARINT) ||
														((metric_field->type & PB_LTYPE_UVARINT) == PB_LTYPE_UVARINT))) {
										printf("\t\tWire type is PB_WT_VARINT\n");
										uint64_t dest;
										status = pb_decode_varint(&substream, &dest);
										if (status) {
											printf("\t\tVARINT - Success - new value: %ld\n", dest);
										} else {
											printf("\t\tVARINT - Failed to decode variant!\n");
										}
									} else if (metric_field->tag == metric_tag && ((metric_field->type & PB_LTYPE_SVARINT) == PB_LTYPE_SVARINT)) {
										printf("\t\tWire type is PB_WT_SVARINT\n");
										int64_t dest;
										status = pb_decode_svarint(&substream, &dest);
										if (status) {
											printf("\t\tVARINT - Success - new value: %ld\n", dest);
										} else {
											printf("\t\tVARINT - Failed to decode variant!\n");
										}
									}
								}
							} else if (metric_wire_type == PB_WT_64BIT) {
								printf("\t\tMetric Wire type is PB_WT_64BIT\n");
							} else if (metric_wire_type == PB_WT_STRING) {
								printf("\t\tMetric Wire type is PB_WT_STRING\n");

								for (metric_field = com_cirruslink_sparkplug_protobuf_Payload_Metric_fields; metric_field->tag != 0; metric_field++) {
									if (metric_field->tag == metric_tag && ((metric_field->type & PB_LTYPE_SUBMESSAGE) == PB_LTYPE_SUBMESSAGE)) {
										printf("\t\tFound a PB_LTYPE_SUBMESSAGE\n");
									} else if (metric_field->tag == metric_tag &&
												((metric_field->type & PB_LTYPE_FIXED_LENGTH_BYTES) == PB_LTYPE_FIXED_LENGTH_BYTES)) {
										printf("\t\tFound a PB_LTYPE_FIXED_LENGTH_BYTES\n");
									} else if (metric_field->tag == metric_tag && ((metric_field->type & PB_LTYPE_STRING) == PB_LTYPE_STRING)) {
										printf("\t\tFound a PB_LTYPE_STRING\n");

										// Get the string size
										pb_byte_t string_size[1];
										status = pb_read(&substream, string_size, 1);
										if (status) {
											printf("\t\tString Size: %d\n", string_size[0]);
										}

										pb_byte_t dest[string_size[0]+1];
										status = pb_read(&substream, dest, string_size[0]);
										if (status) {
											printf("\t\tRead the String! %s\n", dest);
										} else {
											printf("\t\tFailed to read the String...\n");
										}


									} else if (metric_field->tag == metric_tag && ((metric_field->type & PB_LTYPE_BYTES) == PB_LTYPE_BYTES)) {
										printf("\t\tFound a PB_LTYPE_BYTES\n");
									} else {
										printf("\t\tother: %d\n", metric_field->type);
									}
								}

							} else if (metric_wire_type == PB_WT_32BIT) {
								printf("\t\tMetric Wire type is PB_WT_32BIT\n");
							} else {
								printf("\t\tMetric Other? %d\n", metric_wire_type);
							}
						}

						// Close the substream
						pb_close_string_substream(&stream, &substream);

					} else if (payload_field->tag == payload_tag && ((payload_field->type & PB_LTYPE_FIXED_LENGTH_BYTES) == PB_LTYPE_FIXED_LENGTH_BYTES)) {
						printf("\tFound a PB_LTYPE_FIXED_LENGTH_BYTES\n");
					} else if (payload_field->tag == payload_tag && ((payload_field->type & PB_LTYPE_STRING) == PB_LTYPE_STRING)) {
						printf("\tFound a PB_LTYPE_STRING\n");
					} else if (payload_field->tag == payload_tag && ((payload_field->type & PB_LTYPE_BYTES) == PB_LTYPE_BYTES)) {
						printf("\tFound a PB_LTYPE_BYTES\n");
					} else {
						printf("\tother: %d\n", payload_field->type);
					}
				}
			} else if (payload_wire_type == PB_WT_32BIT) {
				printf("\tWire type is PB_WT_32BIT\n");
			} else {
				printf("\tUnknown wiretype...\n");
			}
		}
	} else {
		printf("Decoding succeeded...\n");
	}

	// Print the message data
	print_payload(&inbound_payload);
}

void my_connect_callback(struct mosquitto *mosq, void *userdata, int result) {
	if(!result) {
		// Subscribe to commands
		mosquitto_subscribe(mosq, NULL, "spBv1.0/Sparkplug B Devices/NCMD/C Edge Node 1/#", 0);
		mosquitto_subscribe(mosq, NULL, "spBv1.0/Sparkplug B Devices/DCMD/C Edge Node 1/#", 0);
	} else {
		fprintf(stderr, "Connect failed\n");
	}
}

void my_subscribe_callback(struct mosquitto *mosq, void *userdata, int mid, int qos_count, const int *granted_qos) {
	int i;

	printf("Subscribed (mid: %d): %d", mid, granted_qos[0]);
	for(i=1; i<qos_count; i++) {
		printf(", %d", granted_qos[i]);
	}
	printf("\n");
}

void my_log_callback(struct mosquitto *mosq, void *userdata, int level, const char *str) {
	/* Print all log messages regardless of level. */
	printf("%s\n", str);
}

void publish_births(struct mosquitto *mosq) {
	publish_node_birth(mosq);
	publish_device_birth(mosq);
}

void publish_node_birth(struct mosquitto *mosq) {
	// Create the NBIRTH payload
	com_cirruslink_sparkplug_protobuf_Payload nbirth_payload;
	get_next_payload(&nbirth_payload);
	nbirth_payload.uuid = (char*)malloc((strlen("MyUUID")+1) * sizeof(char));
	strcpy(nbirth_payload.uuid, "MyUUID");

//	strcpy(nbirth_payload.body, "Setting the body to some chars");


	// Add some metrics
	printf("Adding 'Node Metric0'\n");
	char nbirth_metric_zero_value[] = "hello";
	add_metric(&nbirth_payload, "Node Metric0", true, 0, METRIC_DATA_TYPE_STRING, false, false, false, &nbirth_metric_zero_value, sizeof(nbirth_metric_zero_value));
	printf("Adding 'Node Metric1'\n");
	bool nbirth_metric_one_value = true;
	add_metric(&nbirth_payload, "Node Metric1", true, 1, METRIC_DATA_TYPE_BOOLEAN, false, false, false, &nbirth_metric_one_value, sizeof(nbirth_metric_one_value));
	printf("Adding 'Node Metric2'\n");
	uint32_t nbirth_metric_two_value = 13;
	add_metric(&nbirth_payload, "Node Metric2", true, 2, METRIC_DATA_TYPE_INT16, false, false, false, &nbirth_metric_two_value, sizeof(nbirth_metric_two_value));
//	printf("Adding 'Node Metric3'\n");
//	pb_byte_t nbirth_metric_three_value[] = {0,1,2,3,4,5,6,7,8};
//	add_metric(&nbirth_payload, "Node Metric3", true, 3, METRIC_DATA_TYPE_BYTES, false, false, false, &nbirth_metric_three_value, sizeof(nbirth_metric_three_value));

        // Print the payload
        print_payload(&nbirth_payload);

	// Encode the payload
	size_t buffer_length = 1024;
	uint8_t *binary_buffer = (uint8_t *)malloc(buffer_length * sizeof(uint8_t));
	size_t message_length = encode_payload(&binary_buffer, buffer_length, &nbirth_payload);

        // Publish the NBIRTH
        mosquitto_publish(mosq, NULL, "spBv1.0/Sparkplug B Devices/NBIRTH/C Edge Node 1", message_length, binary_buffer, 0, false);

	// Free the memory
	free(binary_buffer);
	free(nbirth_payload.uuid);
	free_payload(&nbirth_payload);
}

void publish_device_birth(struct mosquitto *mosq) {
	// Create the DBIRTH payload
	com_cirruslink_sparkplug_protobuf_Payload dbirth_payload;
	get_next_payload(&dbirth_payload);

	// Add some metrics
	printf("Adding 'Device Metric0'\n");
	char dbirth_metric_zero_value[] = "hello device";
	add_metric(&dbirth_payload, "Device Metric0", true, 10, METRIC_DATA_TYPE_STRING, false, false, false, &dbirth_metric_zero_value, sizeof(dbirth_metric_zero_value));
	printf("Adding 'Device Metric1'\n");
	bool dbirth_metric_one_value = true;
	add_metric(&dbirth_payload, "Device Metric1", true, 11, METRIC_DATA_TYPE_BOOLEAN, false, false, false, &dbirth_metric_one_value, sizeof(dbirth_metric_one_value));
	printf("Adding 'Device Metric2'\n");
	uint32_t dbirth_metric_two_value = 16;
	add_metric(&dbirth_payload, "Device Metric2", true, 12, METRIC_DATA_TYPE_INT16, false, false, false, &dbirth_metric_two_value, sizeof(dbirth_metric_two_value));
	printf("Adding 'sub/Device Metric3'\n");
	uint32_t dbirth_metric_three_value = 17;
	add_metric(&dbirth_payload, "sub/Device Metric3", true, 13, METRIC_DATA_TYPE_INT16, false, false, false, &dbirth_metric_three_value, sizeof(dbirth_metric_three_value));

        // Print the payload
        print_payload(&dbirth_payload);

	// Encode the payload
	size_t buffer_length = 1024;
	uint8_t *binary_buffer = (uint8_t *)malloc(buffer_length * sizeof(uint8_t));
	size_t message_length = encode_payload(&binary_buffer, buffer_length, &dbirth_payload);

        // Publish the NBIRTH
        mosquitto_publish(mosq, NULL, "spBv1.0/Sparkplug B Devices/DBIRTH/C Edge Node 1/Emulated Device", message_length, binary_buffer, 0, false);

	// Free the memory
	free(binary_buffer);
	free_payload(&dbirth_payload);
}
