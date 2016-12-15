#include <stdio.h>
#include <stdbool.h>
#include <sparkplug_b.h>
#include <sparkplug_b.pb.h>

#include <inttypes.h>

#include <time.h>
#include <sys/time.h>

#ifdef __MACH__
#include <mach/clock.h>
#include <mach/mach.h>
#endif

uint64_t get_current_timestamp();
void print_payload(com_cirruslink_sparkplug_protobuf_Payload *payload);

int main(int argc, char *argv[]) {
	//int output = foo(1.2, 3.4);
	//printf("Foo: %d\n", output);

	com_cirruslink_sparkplug_protobuf_Payload payload;
	payload.has_timestamp = true;
	payload.timestamp = get_current_timestamp();
	payload.has_seq = true;
	payload.seq = 0;

/*
    pb_callback_t metrics;
    pb_callback_t uuid;
    pb_callback_t body;
    pb_extension_t *extensions;
*/

	print_payload(&payload);

	return 0;
}


/***************************************************************************************************
 * Get current time in MS
 */
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

void print_payload(com_cirruslink_sparkplug_protobuf_Payload *payload) {
	printf("payload.has_timestamp: %s\n", payload->has_timestamp ? "true" : "false");
	printf("payload.timestamp: %ju\n", payload->timestamp);
	printf("payload.has_seq: %s\n", payload->has_seq ? "true" : "false");
	printf("payload.seq: %ju\n", payload->seq);
}
