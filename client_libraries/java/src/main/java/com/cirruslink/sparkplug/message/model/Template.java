/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A class representing a template associated with a metric
 */
public class Template {

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
	public Template(String version, String templateRef, boolean isDefinition, List<Metric> metrics,
			List<Parameter> parameters) {
		this.version = version;
		this.templateRef = templateRef;
		this.isDefinition = isDefinition;
		this.metrics = metrics;
		this.parameters = parameters;
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
	
	public void addMetric(Metric metric) {
		this.metrics.add(metric);
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
	
	public void addParameter(Parameter parameter) {
		this.parameters.add(parameter);
	}
	
	/**
	 * A builder for creating a {@link Template} instance.
	 */
	public static class TemplateBuilder {

		private String version;
		private String templateRef;
		private boolean isDefinition;
		private List<Metric> metrics;
		private List<Parameter> parameters;
		
		/**
		 * @param name
		 * @param version
		 * @param templateRef
		 * @param isDefinition
		 * @param metrics
		 * @param parameters
		 */
		public TemplateBuilder() {
			super();
			this.metrics = new ArrayList<Metric>();
			this.parameters = new ArrayList<Parameter>();
		}

		public TemplateBuilder version(String version) {
			this.version = version;
			return this;
		}

		public TemplateBuilder templateRef(String templateRef) {
			this.templateRef = templateRef;
			return this;
		}

		public TemplateBuilder definition(boolean isDefinition) {
			this.isDefinition = isDefinition;
			return this;
		}

		public TemplateBuilder addMetric(Metric metric) {
			this.metrics.add(metric);
			return this;
		}

		public TemplateBuilder addMetrics(Collection<Metric> metrics) {
			this.metrics.addAll(metrics);
			return this;
		}

		public TemplateBuilder addParameter(Parameter parameter) {
			this.parameters.add(parameter);
			return this;
		}

		public TemplateBuilder addParameters(Collection<Parameter> parameters) {
			this.parameters.addAll(parameters);
			return this;
		}
		
		public Template createTemplate() {
			return new Template(version, templateRef, isDefinition, metrics, parameters);
		}
	}
}
