/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2017 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package org.eclipse.tahu.json;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Used to register the {@link DeserializerModifier} instance.
 */
public class DeserializerModule extends SimpleModule {

    private BeanDeserializerModifier deserializerModifier;

    public DeserializerModule(BeanDeserializerModifier deserializerModifier) {
        super("DeserializerModule", Version.unknownVersion());
        this.deserializerModifier = deserializerModifier;
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.addBeanDeserializerModifier(deserializerModifier);
    }
}
