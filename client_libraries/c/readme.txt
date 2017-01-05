# To generate the base protobuf sparkplug_b NanoPB C library (using Nanopb v0.3.5)
protoc --proto_path=../../ -osparkplug_b.pb ../../sparkplug_b/sparkplug_b.proto 
nanopb_generator -f sparkplug.options -v sparkplug_b.pb
mv sparkplug_b.pb src/
mv sparkplug_b.pb.c src/
mv sparkplug_b.pb.h src/
