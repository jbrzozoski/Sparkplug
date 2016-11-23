import sparkplug_b_pb2
import time
from sparkplug_b_pb2 import Payload

seqNum = 0
bdSeq = 0

class MetricDataType:
    Unknown = 0

    # Basic Types
    Int8 = 1
    Int16 = 2
    Int32 = 3
    Int64 = 4
    UInt8 = 5
    UInt16 = 6
    UInt32 = 7
    UInt64 = 8
    Float = 9
    Double = 10
    Boolean = 11
    String = 12
    DateTime = 13
    Text = 14

    # Custom Types for Metrics
    UUID = 15
    DataSet = 16
    Bytes = 17
    File = 18
    Template = 19

# Always request this before requesting the Node Birth Payload
def getNodeDeathPayload():
    payload = sparkplug_b_pb2.Payload()
    addMetric(payload, "bdSeq", MetricDataType.Int64, getBdSeqNum())
    return payload

# Always request this after requesting the Node Death Payload
def getNodeBirthPayload():
    seqNum = 0
    payload = sparkplug_b_pb2.Payload()
    payload.timestamp = int(round(time.time() * 1000))
    payload.seq = getSeqNum()
    addMetric(payload, "bdSeq", MetricDataType.Int64, --bdSeq)
    addMetric(payload, "Node Control/Rebirth", MetricDataType.Boolean, False);
    return payload

def getDeviceBirthPayload():
    payload = sparkplug_b_pb2.Payload()
    payload.timestamp = int(round(time.time() * 1000))
    payload.seq = getSeqNum()
    return payload

def getDdataPayload():
    return getDeviceBirthPayload()

######################################################################
# Helper method for adding metrics to a payload
######################################################################
def addMetric(payload, name, type, value):
    metric = payload.metrics.add()
    metric.name = name
    metric.timestamp = int(round(time.time() * 1000))

    # print "Type: " + str(type)

    if type == MetricDataType.Int8:
        metric.datatype = MetricDataType.Int8
        metric.int_value = value
    elif type == MetricDataType.Int16:
        metric.datatype = MetricDataType.Int16
        metric.int_value = value
    elif type == MetricDataType.Int32:
        metric.datatype = MetricDataType.Int32
        metric.int_value = value
    elif type == MetricDataType.Int64:
        metric.datatype = MetricDataType.Int64
        metric.long_value = value
    elif type == MetricDataType.UInt8:
        metric.datatype = MetricDataType.UInt8
        metric.int_value = value
    elif type == MetricDataType.UInt16:
        metric.datatype = MetricDataType.UInt16
        metric.int_value = value
    elif type == MetricDataType.UInt32:
        metric.datatype = MetricDataType.UInt32
        metric.int_value = value
    elif type == MetricDataType.UInt64:
        metric.datatype = MetricDataType.UInt64
        metric.long_value = value
    elif type == MetricDataType.Float:
        metric.datatype = MetricDataType.Float
        metric.float_value = value
    elif type == MetricDataType.Double:
        metric.datatype = MetricDataType.Double
        metric.double_value = value
    elif type == MetricDataType.Boolean:
        metric.datatype = MetricDataType.Boolean
        metric.boolean_value = value
    elif type == MetricDataType.String:
        metric.datatype = MetricDataType.String
        metric.string_value = value
    elif type == MetricDataType.DateTime:
        metric.datatype = MetricDataType.DateTime
        metric.long_value = value
    elif type == MetricDataType.Text:
        metric.datatype = MetricDataType.Text
        metric.string_value = value
    else:
        print "Invalid: " + str(type)

######################################################################
# Helper method for getting the next sequence number
######################################################################
def getSeqNum():
    global seqNum
    retVal = seqNum
    # print("seqNum: " + str(retVal))
    seqNum += 1
    if seqNum == 256:
        seqNum = 0
    return retVal
######################################################################

######################################################################
# Helper method for getting the next birth/death sequence number
######################################################################
def getBdSeqNum():
    global bdSeq
    retVal = bdSeq
    # print("bdSeqNum: " + str(retVal))
    bdSeq += 1
    if bdSeq == 256:
        bdSeq = 0
    return retVal
######################################################################
