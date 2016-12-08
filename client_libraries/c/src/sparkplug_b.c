#include <stdio.h>
#include "sparkplug_b.h"

int total_foo;

int foo(float y, float z) { 
    printf("y and z: %f*%f=%f\n", y, z, y*z);
    return y*z;
}
