/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A class that maintains a set of properties associated with a {@link Metric}.
 */
public class PropertySet {
	
	private Map<String, PropertyValue> propertyMap;
	
	public PropertySet() {
		this.propertyMap = new HashMap<String, PropertyValue>();
	}
	
	private PropertySet(Map<String, PropertyValue> propertyMap) {
		this.propertyMap = propertyMap;
	}
	
	public PropertyValue getPropertyValue(String name) {
		return this.propertyMap.get(name);
	}
	
	public void setProperty(String name, PropertyValue value) {
		this.propertyMap.put(name, value);
	}
	
	public void removeProperty(String name) {
		this.propertyMap.remove(name);
	}
	
	public void clear() {
		this.propertyMap.clear();
	}
	
	public Set<String> getNames() {
		return propertyMap.keySet();
	}
	
	public Collection<PropertyValue> getValues() {
		return propertyMap.values();
	}
	
	public Map<String, PropertyValue> getPropertyMap() {
		return propertyMap;
	}
	
	@Override
	public String toString() {
		return "PropertySet [propertyMap=" + propertyMap + "]";
	}

	/**
	 * A builder for a PropertySet instance
	 */
	public static class PropertySetBuilder {
		
		private Map<String, PropertyValue> propertyMap;
		
		public PropertySetBuilder() {
			this.propertyMap = new HashMap<String, PropertyValue>();
		}
		
		public PropertySetBuilder addProperty(String name, PropertyValue value) {
			this.propertyMap.put(name, value);
			return this;
		}
		
		public PropertySetBuilder addProperties(Map<String, PropertyValue> properties) {
			this.propertyMap.putAll(properties);
			return this;
		}
		
		public PropertySet createPropertySet() {
			return new PropertySet(propertyMap);
		}
	}
}
