# To generate the base protobuf sparkplug_b NanoPB C library
protoc --proto_path=../../ -osparkplug_b.pb ../../sparkplug_b/sparkplug_b.proto 
nanopb_generator sparkplug_b.pb 
