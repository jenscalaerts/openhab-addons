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

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.discord.internal.action.DiscordActions;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link DiscordHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author jens Calaerts - Initial contribution
 */
@NonNullByDefault
public class DiscordHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(DiscordHandler.class);
    private final HttpClient httpClient;
    private @Nullable DiscordConfiguration config;
    private @Nullable DiscordApi discordApi;

    public DiscordHandler(Thing thing, HttpClient httpClient) {
        super(thing);
        this.httpClient = httpClient;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public void initialize() {
        logger.info("Initializing discord");
        config = getConfigAs(DiscordConfiguration.class);
        if (config == null || "".equals(config.webhookUrl)) {
            updateStatus(ThingStatus.OFFLINE);
            return;
        }
        discordApi = new DiscordApi(URI.create(config.webhookUrl), httpClient);
        updateStatus(ThingStatus.UNKNOWN);
        scheduler.execute(() -> {
            try {
                verifyWebhookExists();
            } catch (IOException e) {
                updateToOfflineState();
            }
        });
    }

    private void updateToOfflineState() {
        logger.error("Could not fetch webhook information");
        updateStatus(ThingStatus.OFFLINE);
    }

    private void verifyWebhookExists() throws IOException {
        logger.debug("Verifying the existence of the rule with url {}", config.webhookUrl);
        discordApi.getWebhook();
        updateStatus(ThingStatus.ONLINE);
    }

    public @Nullable DiscordApi getDiscordApi() {
        return discordApi;
    }

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return Collections.singleton(DiscordActions.class);
    }
}
