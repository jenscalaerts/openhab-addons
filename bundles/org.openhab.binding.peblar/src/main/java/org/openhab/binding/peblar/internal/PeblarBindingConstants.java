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
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link PeblarBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Jens Calaerts - Initial contribution
 */
@NonNullByDefault
public class PeblarBindingConstants {

    private static final String BINDING_ID = "peblar";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE = new ThingTypeUID(BINDING_ID, "peblar-charger");

    // List of all Channel ids
    public static final String CHANNEL_CHARGE_CURRENT_LIMIT = "charge-current-limit";
    public static final String CHANNEL_CHARGE_CURRENT_LIMIT_ACTUAL = "charge-current-limit-actual";
    public static final String CHANNEL_CHARGE_CURRENT_LIMIT_SOURCE = "charge-current-limit-source";
    public static final String CHANNEL_FORCE_ONE_PHASE = "force-one-phase";
    public static final String CHANNEL_LOCK_STATE = "lock-state";

    public static final String CHANNEL_CURRENT_PHASE_1 = "current-phase-1";
    public static final String CHANNEL_CURRENT_PHASE_2 = "current-phase-2";
    public static final String CHANNEL_CURRENT_PHASE_3 = "current-phase-3";
    public static final String CHANNEL_VOLTAGE_PHASE_1 = "voltage-phase-1";
    public static final String CHANNEL_VOLTAGE_PHASE_2 = "voltage-phase-2";
    public static final String CHANNEL_VOLTAGE_PHASE_3 = "voltage-phase-3";
    public static final String CHANNEL_POWER_PHASE_1 = "power-phase-1";
    public static final String CHANNEL_POWER_PHASE_2 = "power-phase-2";
    public static final String CHANNEL_POWER_PHASE_3 = "power-phase-3";
    public static final String CHANNEL_POWER_TOTAL = "power-total";
    public static final String CHANNEL_ENERGY_TOTAL = "energy-total";
    public static final String CHANNEL_ENERGY_SESSION = "energy-session";
    public static final String CHANNEL_CHARGE_POINT_STATE = "charge-point-state";
}
