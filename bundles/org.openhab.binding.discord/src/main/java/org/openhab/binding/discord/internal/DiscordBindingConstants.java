/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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
package org.openhab.binding.discord.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link DiscordBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author jens Calaerts - Initial contribution
 */
@NonNullByDefault
public class DiscordBindingConstants {

    private static final String BINDING_ID = "discord";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_DISCORD = new ThingTypeUID(BINDING_ID, "discordBot");
}
