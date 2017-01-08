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

/*
extern const pb_field_t com_cirruslink_sparkplug_protobuf_Payload_fields[7];
extern const pb_field_t com_cirruslink_sparkplug_protobuf_Payload_Template_fields[7];
extern const pb_field_t com_cirruslink_sparkplug_protobuf_Payload_Template_Parameter_fields[10];
extern const pb_field_t com_cirruslink_sparkplug_protobuf_Payload_Template_Parameter_ParameterValueExtension_fields[2];
extern const pb_field_t com_cirruslink_sparkplug_protobuf_Payload_DataSet_fields[6];
extern const pb_field_t com_cirruslink_sparkplug_protobuf_Payload_DataSet_DataSetValue_fields[8];
extern const pb_field_t com_cirruslink_sparkplug_protobuf_Payload_DataSet_DataSetValue_DataSetValueExtension_fields[2];
extern const pb_field_t com_cirruslink_sparkplug_protobuf_Payload_DataSet_Row_fields[3];
extern const pb_field_t com_cirruslink_sparkplug_protobuf_Payload_PropertyValue_fields[10];
extern const pb_field_t com_cirruslink_sparkplug_protobuf_Payload_PropertyValue_PropertyValueExtension_fields[2];
extern const pb_field_t com_cirruslink_sparkplug_protobuf_Payload_PropertySet_fields[4];
extern const pb_field_t com_cirruslink_sparkplug_protobuf_Payload_PropertySetList_fields[3];
extern const pb_field_t com_cirruslink_sparkplug_protobuf_Payload_MetaData_fields[10];
extern const pb_field_t com_cirruslink_sparkplug_protobuf_Payload_Metric_fields[19];
extern const pb_field_t com_cirruslink_sparkplug_protobuf_Payload_Metric_MetricValueExtension_fields[2];
*/

/*
	const pb_field_t *field;
	printf("Payload_fields\n");
	for (field = com_cirruslink_sparkplug_protobuf_Payload_fields; field->tag != 0; field++) {
		printf("field->tag: %d, field->type: %d\n", field->tag, field->type);
	}
	printf("Payload_Metric_fields\n");
	for (field = com_cirruslink_sparkplug_protobuf_Payload_Metric_fields; field->tag != 0; field++) {
		printf("field->tag: %d, field->type: %d\n", field->tag, field->type);
	}
	printf("Payload_MetaData_fields\n");
	for (field = com_cirruslink_sparkplug_protobuf_Payload_MetaData_fields; field->tag != 0; field++) {
		printf("field->tag: %d, field->type: %d\n", field->tag, field->type);
	}
	printf("Payload_PropertySet_fields\n");
	for (field = com_cirruslink_sparkplug_protobuf_Payload_PropertySet_fields; field->tag != 0; field++) {
		printf("field->tag: %d, field->type: %d\n", field->tag, field->type);
	}
	printf("Payload_PropertyValue_fields\n");
	for (field = com_cirruslink_sparkplug_protobuf_Payload_PropertyValue_fields; field->tag != 0; field++) {
		printf("field->tag: %d, field->type: %d\n", field->tag, field->type);
	}
	printf("Payload_PropertySet_fields\n");
	for (field = com_cirruslink_sparkplug_protobuf_Payload_PropertySet_fields; field->tag != 0; field++) {
		printf("field->tag: %d, field->type: %d\n", field->tag, field->type);
	}
	printf("Payload_PropertySetList_fields\n");
	for (field = com_cirruslink_sparkplug_protobuf_Payload_PropertySetList_fields; field->tag != 0; field++) {
		printf("field->tag: %d, field->type: %d\n", field->tag, field->type);
	}
	printf("Payload_DataSet_fields\n");
	for (field = com_cirruslink_sparkplug_protobuf_Payload_DataSet_fields; field->tag != 0; field++) {
		printf("field->tag: %d, field->type: %d\n", field->tag, field->type);
	}
	printf("Payload_DataSet_Row_fields\n");
	for (field = com_cirruslink_sparkplug_protobuf_Payload_DataSet_Row_fields; field->tag != 0; field++) {
		printf("field->tag: %d, field->type: %d\n", field->tag, field->type);
	}
	printf("Payload_DataSet_DataSetValue_fields\n");
	for (field = com_cirruslink_sparkplug_protobuf_Payload_DataSet_DataSetValue_fields; field->tag != 0; field++) {
		printf("field->tag: %d, field->type: %d\n", field->tag, field->type);
	}
	printf("Payload_Template_fields\n");
	for (field = com_cirruslink_sparkplug_protobuf_Payload_Template_fields; field->tag != 0; field++) {
		printf("field->tag: %d, field->type: %d\n", field->tag, field->type);
	}
	printf("Payload_Template_Parameter_fields\n");
	for (field = com_cirruslink_sparkplug_protobuf_Payload_Template_Parameter_fields; field->tag != 0; field++) {
		printf("field->tag: %d, field->type: %d\n", field->tag, field->type);
	}
*/

	// MQTT Stuff
        char *host = "localhost";
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
        mosquitto_username_pw_set(mosq,"admin","changeme");
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

	// Decode the payload
	com_cirruslink_sparkplug_protobuf_Payload inbound_payload = com_cirruslink_sparkplug_protobuf_Payload_init_zero;
	if(decode_payload(&inbound_payload, message->payload, message->payloadlen)) {
	} else {
		printf("FAILED TO DECODE THE PAYLOAD\n");
	}

	// Print the message data
	//print_payload(&inbound_payload);
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
	// Print all log messages regardless of level.
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

	// Add some metrics
	printf("Adding 'Node Metric0'\n");
	char nbirth_metric_zero_value[] = "hello";
	add_simple_metric(&nbirth_payload, "Node Metric0", true, 0, METRIC_DATA_TYPE_STRING, false, false, false, &nbirth_metric_zero_value, sizeof(nbirth_metric_zero_value));
	printf("Adding 'Node Metric1'\n");
	bool nbirth_metric_one_value = true;
	add_simple_metric(&nbirth_payload, "Node Metric1", true, 1, METRIC_DATA_TYPE_BOOLEAN, false, false, false, &nbirth_metric_one_value, sizeof(nbirth_metric_one_value));
	printf("Adding 'Node Metric2'\n");
	uint32_t nbirth_metric_two_value = 13;
	add_simple_metric(&nbirth_payload, "Node Metric2", true, 2, METRIC_DATA_TYPE_INT16, false, false, false, &nbirth_metric_two_value, sizeof(nbirth_metric_two_value));

	// Create a metric called RPMs for the UDT definition
	com_cirruslink_sparkplug_protobuf_Payload_Metric rpms_metric = com_cirruslink_sparkplug_protobuf_Payload_Metric_init_default;
	uint32_t rpms_value = 0;
	init_metric(&rpms_metric, "RPMs", false, 0, METRIC_DATA_TYPE_INT32, false, false, false, &rpms_value, sizeof(rpms_value));
	com_cirruslink_sparkplug_protobuf_Payload_PropertySet rpms_propertyset = com_cirruslink_sparkplug_protobuf_Payload_PropertySet_init_default;
	uint32_t rpms_property_value = 0;
	add_property_to_set(&rpms_propertyset, "tagType", METRIC_DATA_TYPE_INT32, false, &rpms_property_value, sizeof(rpms_property_value));

	// Create a metric called AMPs for the UDT definition
	com_cirruslink_sparkplug_protobuf_Payload_Metric amps_metric = com_cirruslink_sparkplug_protobuf_Payload_Metric_init_default;
	uint32_t amps_value = 0;
	init_metric(&amps_metric, "AMPs", false, 0, METRIC_DATA_TYPE_INT32, false, false, false, &amps_value, sizeof(amps_value));
	com_cirruslink_sparkplug_protobuf_Payload_PropertySet amps_propertyset = com_cirruslink_sparkplug_protobuf_Payload_PropertySet_init_default;
	uint32_t amps_property_value = 0;
	add_property_to_set(&amps_propertyset, "tagType", METRIC_DATA_TYPE_INT32, false, &amps_property_value, sizeof(amps_property_value));

	// Create a Template/UDT Parameter
	com_cirruslink_sparkplug_protobuf_Payload_Template_Parameter parameter = com_cirruslink_sparkplug_protobuf_Payload_Template_Parameter_init_default;
	parameter.name = (char *)malloc((strlen("Index")+1)*sizeof(char));
        strcpy(parameter.name, "Index");
	parameter.has_type = true;
	parameter.type = PARAMETER_DATA_TYPE_STRING;
	parameter.which_value = com_cirruslink_sparkplug_protobuf_Payload_Template_Parameter_string_value_tag;
	parameter.value.string_value = (char *)malloc((strlen("0")+1)*sizeof(char));
	strcpy(parameter.value.string_value, "0");

	// Create the UDT defintion
	com_cirruslink_sparkplug_protobuf_Payload_Template udt_template = com_cirruslink_sparkplug_protobuf_Payload_Template_init_default;
	udt_template.version = (char *)malloc((strlen("v1.1")+1)*sizeof(char));
        strcpy(udt_template.version, "v1.1");
	udt_template.metrics_count = 2;
	udt_template.metrics = (com_cirruslink_sparkplug_protobuf_Payload_Metric *) calloc(2, sizeof(com_cirruslink_sparkplug_protobuf_Payload_Metric));
	udt_template.metrics[0] = rpms_metric;
	udt_template.metrics[1] = amps_metric;
	udt_template.parameters_count = 1;
	udt_template.parameters = (com_cirruslink_sparkplug_protobuf_Payload_Template_Parameter *) calloc(1, sizeof(com_cirruslink_sparkplug_protobuf_Payload_Template_Parameter));
	udt_template.parameters[0] = parameter;
	udt_template.template_ref = NULL;
	udt_template.has_is_definition = true;
	udt_template.is_definition = true;

	// Create the root UDT definition and add the UDT value
	com_cirruslink_sparkplug_protobuf_Payload_Metric metric = com_cirruslink_sparkplug_protobuf_Payload_Metric_init_default;
	init_metric(&metric, "_types_/Custom_Motor", false, 0, METRIC_DATA_TYPE_TEMPLATE, false, false, false, &udt_template, sizeof(udt_template));

	com_cirruslink_sparkplug_protobuf_Payload_PropertySet propertyset = com_cirruslink_sparkplug_protobuf_Payload_PropertySet_init_default;
	uint32_t nbirth_propvalue_one = 9;
	add_property_to_set(&propertyset, "tagType", METRIC_DATA_TYPE_INT32, false, &nbirth_propvalue_one, sizeof(nbirth_propvalue_one));

	// Add the UDT to the payload
	add_entire_metric(&nbirth_payload, &metric);

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
	add_simple_metric(&dbirth_payload, "Device Metric0", true, 10, METRIC_DATA_TYPE_STRING, false, false, false, &dbirth_metric_zero_value, sizeof(dbirth_metric_zero_value));
	printf("Adding 'Device Metric1'\n");
	bool dbirth_metric_one_value = true;
	add_simple_metric(&dbirth_payload, "Device Metric1", true, 11, METRIC_DATA_TYPE_BOOLEAN, false, false, false, &dbirth_metric_one_value, sizeof(dbirth_metric_one_value));
	printf("Adding 'Device Metric2'\n");
	uint32_t dbirth_metric_two_value = 16;
	add_simple_metric(&dbirth_payload, "Device Metric2", true, 12, METRIC_DATA_TYPE_INT16, false, false, false, &dbirth_metric_two_value, sizeof(dbirth_metric_two_value));
	printf("Adding 'sub/Device Metric3'\n");
	uint32_t dbirth_metric_three_value = 17;
	add_simple_metric(&dbirth_payload, "sub/Device Metric3", true, 13, METRIC_DATA_TYPE_INT16, false, false, false, &dbirth_metric_three_value, sizeof(dbirth_metric_three_value));

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
