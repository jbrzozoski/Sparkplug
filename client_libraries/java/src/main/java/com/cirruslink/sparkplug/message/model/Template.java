/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.model;

import java.util.List;

/**
 * A Template
 */
public class Template {

	private String name;
	private String version;
	private String templateRef;
	private boolean isDefinition;
	private List<Metric> metrics;
	private List<Parameter> parameters;
	
	/**
	 * Constructor
	 * 
	 * @param name the template name
	 * @param version the template version
	 * @param templateRef a template reference
	 * @param isDefinition a flag indicating if this is a template definition
	 * @param metrics a list of metrics
	 * @param parmeters a list of parameters
	 */
	public Template(String name, String version, String templateRef, boolean isDefinition, List<Metric> metrics,
			List<Parameter> parameters) {
		super();
		this.name = name;
		this.version = version;
		this.templateRef = templateRef;
		this.isDefinition = isDefinition;
		this.metrics = metrics;
		this.parameters = parameters;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTemplateRef() {
		return templateRef;
	}

	public void setTemplateRef(String templateRef) {
		this.templateRef = templateRef;
	}

	public boolean isDefinition() {
		return isDefinition;
	}

	public void setDefinition(boolean isDefinition) {
		this.isDefinition = isDefinition;
	}

	public List<Metric> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<Metric> metrics) {
		this.metrics = metrics;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
}
