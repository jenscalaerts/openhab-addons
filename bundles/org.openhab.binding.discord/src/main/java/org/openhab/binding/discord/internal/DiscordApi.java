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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.client.util.MultiPartContentProvider;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.openhab.core.io.net.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link DiscordApi} api to do requests to the webhook api
 *
 * @author jens Calaerts - Initial contribution
 */
@NonNullByDefault
public class DiscordApi {
    private final Logger logger = LoggerFactory.getLogger(DiscordApi.class);
    private final URI webHookUri;
    private final HttpClient httpClient;

    public DiscordApi(URI webHookUri, HttpClient httpClient) {
        this.webHookUri = webHookUri;
        this.httpClient = httpClient;
    }

    public void sendMessage(DiscordRequest discordRequest) {
        MultiPartContentProvider multiPart = createMultiPartRequest(discordRequest);
        sendRequest(multiPart);
    }

    private void sendRequest(MultiPartContentProvider multiPart) {
        try {
            logger.trace("Sending message to discord {}", multiPart);
            ContentResponse response = this.httpClient.newRequest(this.webHookUri).method(HttpMethod.POST)
                    .content(multiPart).send();
            logger.trace("Response is {}", response.getContentAsString());
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            logger.warn("Request to discord failed. Verify if the webhook url is correct and openhab can connect to it",
                    e);
            logger.debug("Request to discord failed.", e);
        }
    }

    private MultiPartContentProvider createMultiPartRequest(DiscordRequest discordRequest) {
        MultiPartContentProvider multiPart = new MultiPartContentProvider();
        multiPart.addFieldPart("content", new StringContentProvider(discordRequest.getMessage()), null);
        if (discordRequest.getRawType() != null) {
            multiPart.addFilePart("files", discordRequest.getFilename(), new BytesContentProvider(
                    discordRequest.getRawType().getMimeType(), discordRequest.getRawType().getBytes()), null);
        }

        multiPart.close();
        return multiPart;
    }

    public String getWebhook() throws IOException {
        return HttpUtil.executeUrl("GET", String.valueOf(webHookUri), 5000);
    }
}
