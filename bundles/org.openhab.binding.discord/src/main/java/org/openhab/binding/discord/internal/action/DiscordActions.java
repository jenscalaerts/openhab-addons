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
package org.openhab.binding.discord.internal.action;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.discord.internal.DiscordHandler;
import org.openhab.binding.discord.internal.DiscordRequest;
import org.openhab.core.automation.annotation.ActionInput;
import org.openhab.core.automation.annotation.RuleAction;
import org.openhab.core.library.types.RawType;
import org.openhab.core.thing.binding.ThingActions;
import org.openhab.core.thing.binding.ThingActionsScope;
import org.openhab.core.thing.binding.ThingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the actions for the Discord API.
 *
 * @author Jens Calaerts - Initial contribution
 */
@ThingActionsScope(name = "discord")
@NonNullByDefault
public class DiscordActions implements ThingActions {
    private final Logger logger = LoggerFactory.getLogger(DiscordActions.class);
    private @Nullable DiscordHandler handler;

    public static void sendDiscordMessage(ThingActions actions, String message) {
        if (actions instanceof DiscordActions) {
            ((DiscordActions) actions).sendDiscordMessage(message);
        } else {
            throw new IllegalArgumentException("Instance is not an DiscordActions class.");
        }
    }

    public static void sendDiscordMessage(ThingActions actions, String message, RawType rawType, String filename) {
        if (actions instanceof DiscordActions) {
            ((DiscordActions) actions).sendDiscordMessage(message, rawType, filename);
        } else {
            throw new IllegalArgumentException("Instance is not an DiscordActions class.");
        }
    }

    @RuleAction(label = "Send a message", description = "Send a discord message using a discord webhook")
    public void sendDiscordMessage(@ActionInput(name = "message") String message) {
        if (handler == null || handler.getDiscordApi() == null) {
            logger.warn("The discord handler was not initialized, Ignoring send message");
            return;
        }
        handler.getDiscordApi().sendMessage(new DiscordRequest(message));
    }

    @RuleAction(label = "Send a message", description = "Send a discord message using a discord webhook")
    public void sendDiscordMessage(@ActionInput(name = "message") String message,
            @ActionInput(name = "image") RawType rawType, @ActionInput(name = "filename") String filename) {
        if (handler == null || handler.getDiscordApi() == null) {
            logger.warn("The discord handler was not initialized, Ignoring send message");
            return;
        }
        handler.getDiscordApi().sendMessage(new DiscordRequest(message, rawType, filename));
    }

    @Override
    public @Nullable ThingHandler getThingHandler() {
        return handler;
    }

    @Override
    public void setThingHandler(ThingHandler handler) {
        this.handler = (DiscordHandler) handler;
    }
}
