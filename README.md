# Sparkplug

Sparkplug is a specification for MQTT enabled devices and applications to send and receive messages in a stateful way.
While MQTT is stateful by nature it doesn't ensure that all data on a receiving MQTT application is current or valid.
Sparkplug provides a mechanism for ensuring that remote device or application data is current and valid.

The examples here provide client libraries and reference implementations in various languages and for various devices
to show how the device/remote application must connect and disconnect from the MQTT server.  This includes device
lifecycle messages such as the required birth and last will & testament messages that must be sent to ensure the device
lifecycle state and data integrity.

The Sparkplug specification which explains these examples can be found below.  There are two revisions of the
specification that differ in how payloads are encoded.  Both formats use Google Protocol Buffers but use different
proto definitions.

(Deprecated) Sparkplug A utilizes a preexisting payload encoded definition from the Eclipse Kura IoT project.  The original
definition is here and is the same version that was used for the Sparplug A payload definition:
https://github.com/eclipse/kura/blob/KURA_2.1.0_RELEASE/kura/org.eclipse.kura.core.cloud/src/main/protobuf/kurapayload.proto

If just starting development - it is highly recommended to use Sparkplug B.

Sparkplug B was developed to add additional features and capabilities that were not possible in the original Kura payload
definition.  These features include:
* Complex data types using templates
* Datasets
* Richer metrics with the ability to add property metadata for each metric
* Metric alias support to maintain rich metric naming while keeping bandwidth usage to a minimum
* Historical data
* File data

Sparkplug B Specification:
https://s3.amazonaws.com/cirrus-link-com/Sparkplug+Topic+Namespace+and+State+ManagementV2.1+Apendix++Payload+B+format.pdf

Sparkplug A Specification (Deprecated):
https://s3.amazonaws.com/cirrus-link-com/Sparkplug+Topic+Namespace+and+State+ManagementV2.1+Appendix+Payload+A+Format.pdf

Tutorials showing how to use this reference code can be found here:
https://docs.chariot.io/
