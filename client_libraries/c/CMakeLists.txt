cmake_minimum_required(VERSION 3.8.2)

project(SparkplugLibrary)

add_library(sparkplug_b src/pb_common.c src/pb_decode.c src/pb_encode.c src/sparkplug_b.c src/sparkplug_b.pb.c)
target_include_directories(sparkplug_b PUBLIC include)

