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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openhab.binding.discord.internal.DiscordApi;
import org.openhab.binding.discord.internal.DiscordHandler;
import org.openhab.binding.discord.internal.DiscordRequest;
import org.openhab.core.library.types.RawType;
import org.openhab.core.thing.binding.ThingActions;

/**
 * @author jens Calaerts - Initial contribution
 */
@NonNullByDefault
class DiscordActionsTest {

    private final DiscordApi api = mock(DiscordApi.class);
    private final DiscordHandler handler = mock(DiscordHandler.class);

    @BeforeEach
    void setUp() {
        when(handler.getDiscordApi()).thenReturn(api);
    }

    @Test
    void sendDiscordMessage_whenMessageIsTooLong_throwIllegalArgumentException() {
        DiscordActions discordActions = new DiscordActions();
        discordActions.setThingHandler(handler);

        assertThrows(IllegalArgumentException.class, () -> discordActions.sendDiscordMessage("a".repeat(2001)));
    }

    @Test
    void sendDiscordMessage_doesNotFailWhenHandlerIsNotSet() {
        DiscordActions discordActions = new DiscordActions();
        assertDoesNotThrow(() -> discordActions.sendDiscordMessage("a".repeat(2001)));
    }

    @Test
    void sendDiscordMessage_sendsMessage() {
        DiscordActions discordActions = new DiscordActions();
        discordActions.setThingHandler(handler);

        DiscordActions.sendDiscordMessage(discordActions, "message");
        verify(api).sendMessage(new DiscordRequest("message"));
    }

    @Test
    void sendDiscordMessage_sendsMessageWithAttachment() {
        DiscordActions discordActions = new DiscordActions();
        discordActions.setThingHandler(handler);

        RawType rawType = new RawType(new byte[0], "image/jpeg");
        DiscordActions.sendDiscordMessage(discordActions, "message", rawType, "filename.jpeg");
        verify(api).sendMessage(new DiscordRequest("message", rawType, "filename.jpeg"));
    }

    @Test
    void sendDiscordMessage_whenInvalidActions_throwIllegalArgumentException() {
        RawType rawType = new RawType(new byte[0], "image/jpeg");
        assertThrows(IllegalArgumentException.class,
                () -> DiscordActions.sendDiscordMessage(mock(ThingActions.class), "message", rawType, "filename.jpeg"));
    }

    @Test
    void sendDiscordMessage_whenInvalidActions_messageOnly_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> DiscordActions.sendDiscordMessage(mock(ThingActions.class), "message"));
    }
}
