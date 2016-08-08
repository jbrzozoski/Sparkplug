# To generate the base protobuf sparkplug_b python library
protoc -I=../../sparkplug_b/ --python_out=. ../../sparkplug_b/sparkplug_b.proto 
