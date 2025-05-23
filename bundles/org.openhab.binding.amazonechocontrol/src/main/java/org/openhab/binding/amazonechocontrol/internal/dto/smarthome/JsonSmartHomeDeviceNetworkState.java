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
package org.openhab.binding.amazonechocontrol.internal.dto.smarthome;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 *
 * @author Lukas Knoeller - Initial contribution
 *
 */
@NonNullByDefault
public class JsonSmartHomeDeviceNetworkState {
    public static class SmartHomeDeviceNetworkState {
        public @Nullable String reachability;

        @Override
        public String toString() {
            return "SmartHomeDeviceNetworkState{reachability='" + reachability + "'}";
        }
    }

    public @Nullable SmartHomeDeviceNetworkState networkState;

    @Override
    public String toString() {
        return "JsonSmartHomeDeviceNetworkState{networkState=" + networkState + "}";
    }
}
