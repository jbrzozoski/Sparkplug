/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package org.eclipse.tahu.message.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * A Sparkplug MQTT Topic
 */
@JsonInclude(Include.NON_NULL)
public class Topic {
	
	/**
	 * The Sparkplug namespace version.
	 */
	private String namespace;

	/**
	 * The ID of the logical grouping of Edge of Network (EoN) Nodes and devices.
	 */
	private String groupId;
	
	/**
	 * The ID of the Edge of Network (EoN) Node.
	 */
	private String edgeNodeId;
	
	/**
	 * The ID of the device.
	 */
	private String deviceId;
	
	/**
	 * The message type.
	 */
	private MessageType type;

	/**
	 * A Constructor.
	 * 
	 * @param namespace the namespace.
	 * @param groupId the group ID.
	 * @param edgeNodeId the edge node ID.
	 * @param deviceId the device ID.
	 * @param type the message type.
	 */
	public Topic(String namespace, String groupId, String edgeNodeId, String deviceId, MessageType type) {
		super();
		this.namespace = namespace;
		this.groupId = groupId;
		this.edgeNodeId = edgeNodeId;
		this.deviceId = deviceId;
		this.type = type;
	}

	/**
	 * A Constructor.
	 * 
	 * @param namespace the namespace.
	 * @param groupId the group ID.
	 * @param edgeNodeId the edge node ID.
	 * @param type the message type.
	 */
	public Topic(String namespace, String groupId, String edgeNodeId, MessageType type) {
		super();
		this.namespace = namespace;
		this.groupId = groupId;
		this.edgeNodeId = edgeNodeId;
		this.deviceId = null;
		this.type = type;
	}

	/**
	 * Returns the Sparkplug namespace version.
	 * 
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * Returns the ID of the logical grouping of Edge of Network (EoN) Nodes and devices.
	 * 
	 * @return the group ID
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * Returns the ID of the Edge of Network (EoN) Node.
	 * 
	 * @return the edge node ID
	 */
	public String getEdgeNodeId() {
		return edgeNodeId;
	}

	/**
	 * Returns the ID of the device.
	 * 
	 * @return the device ID
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * Returns the message type.
	 * 
	 * @return the message type
	 */
	public MessageType getType() {
		return type;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getNamespace()).append("/")
				.append(getGroupId()).append("/")
				.append(getType()).append("/")
				.append(getEdgeNodeId());
		if (getDeviceId() != null) {
			sb.append("/").append(getDeviceId());
		}
		return sb.toString();
	}
	
	/**
	 * Returns true if this topic's type matches the passes in type, false otherwise.
	 * 
	 * @param type the type to check
	 * @return true if this topic's type matches the passes in type, false otherwise
	 */
	public boolean isType(MessageType type) {
		return this.type != null && this.type.equals(type);
	}
	

}
