/*
 * Copyright (c) 2010-2025 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.peblar.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link PeblarConfiguration} class contains fields mapping thing
 * configuration parameters.
 *
 * @author Jens Calaerts - Initial contribution
 */
@NonNullByDefault
public class PeblarConfiguration {

    /**
     * The ip or host name of the ev charger
     */
    public String hostname = "";

    /**
     * The access token
     */
    public String token = "";
    /**
     * the refresh interval in seconds
     */
    public int refreshInterval = 10;

    /**
     * the full uri of the ev interface endpoint
     */
    public String evInterface() {
        return "http://" + hostname + "/api/wlac/v1/evinterface";
    }

    /**
     * the full uri of the meter endpoint
     */
    public String meter() {
        return "http://" + hostname + "/api/wlac/v1/meter";
    }
}
