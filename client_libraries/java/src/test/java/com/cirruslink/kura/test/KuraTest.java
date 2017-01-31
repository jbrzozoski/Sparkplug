/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.kura.test;

import java.util.Date;

import org.eclipse.kura.message.KuraPayload;

import junit.framework.TestCase;

public class KuraTest extends TestCase {

	public KuraTest(String testName) {
		super(testName);
	}

	public void testTest() {
		assertTrue(true);
	}
	
	public void testJsonPayload() {
		KuraPayload payload = new KuraPayload();
		payload.setTimestamp(new Date());
		payload.addMetric("metric 1", 1);
		try {
			System.out.println("Payload " + payload.toJsonString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
