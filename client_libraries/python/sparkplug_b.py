import sparkplug_b_pb2
import time
from sparkplug_b_pb2 import Payload

seqNum = 0
bdSeq = 0

# Always request this before requesting the Node Birth Payload
def getNodeDeathPayload():
    payload = sparkplug_b_pb2.Payload()
    addMetric(payload, "bdSeq", "Int8", getBdSeqNum())
    return payload

# Always request this after requesting the Node Death Payload
def getNodeBirthPayload():
    seqNum = 0
    payload = sparkplug_b_pb2.Payload()
    payload.timestamp = int(round(time.time() * 1000))
    payload.seq = getSeqNum()
    addMetric(payload, "bdSeq", "Int8", --bdSeq)
    addMetric(payload, "Node Control/Rebirth", "Boolean", False);
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
    metric = payload.metric.add()
    metric.name = name
    metric.timestamp = int(round(time.time() * 1000))

    if type == "Int1":
        metric.datatype = sparkplug_b_pb2.Payload.Metric.Int1
        metric.int_value = value
    elif type == "Int2":
        metric.datatype = sparkplug_b_pb2.Payload.Metric.Int2
        metric.int_value = value
    elif type == "Int4":
        metric.datatype = sparkplug_b_pb2.Payload.Metric.Int4
        metric.int_value = value
    elif type == "Int8":
        metric.datatype = sparkplug_b_pb2.Payload.Metric.Int8
        metric.long_value = value
    elif type == "Float4":
        metric.datatype = sparkplug_b_pb2.Payload.Metric.Float4
        metric.float_value = value
    elif type == "Float8":
        metric.datatype = sparkplug_b_pb2.Payload.Metric.Float8
        metric.double_value = value
    elif type == "Boolean":
        metric.datatype = sparkplug_b_pb2.Payload.Metric.Boolean
        metric.boolean_value = value
    elif type == "String":
        metric.datatype = sparkplug_b_pb2.Payload.Metric.String
        metric.string_value = value
    elif type == "DateTime":
        print "unsupported"
    elif type == "Dataset":
        print "unsupported"
    elif type == "Text":
        print "unsupported"
    elif type == "Bytes":
        print "unsupported"
    elif type == "File":
        print "unsupported"
    elif type == "UdtDef":
        print "unsupported"
    elif type == "UdtInst":
        print "unsupported"
    else:
        print "invalid"

######################################################################
# Helper method for getting the next sequence number
######################################################################
def getSeqNum():
    global seqNum
    retVal = seqNum
    print("seqNum: " + str(retVal))
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
    print("bdSeqNum: " + str(retVal))
    bdSeq += 1
    if bdSeq == 256:
        bdSeq = 0
    return retVal
######################################################################
