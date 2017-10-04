/**
 * Copyright (c) 2017 Cirrus Link Solutions
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Cirrus Link Solutions
 */

var kurapayload = require('./lib/kurapayload.js'),
    sparkplugbpayload = require('./lib/sparkplugbpayload.js');

exports.get = function(namespace) {
    if (namespace !== undefined && namespace !== null) {
        if (namespace === "spBv1.0") {
            return sparkplugbpayload;
        } else if (namespace === "spAv1.0") {
            return kurapayload;
        }
    }
    return null;
};
