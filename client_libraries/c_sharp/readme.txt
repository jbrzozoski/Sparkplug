# To generate the base protobuf sparkplug_b Java library
protoc --proto_path=../../ --csharp_out=src --csharp_opt=base_namespace=Com.Cirruslink.Sparkplug.Protobuf ../../sparkplug_b/sparkplug_b_c_sharp.proto
