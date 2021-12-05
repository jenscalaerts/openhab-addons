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

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.library.types.RawType;

/**
 * @author jens Calaerts - Initial contribution
 */
@NonNullByDefault
public class DiscordRequest {
    private final String message;
    private final @Nullable RawType rawType;
    private final @Nullable String filename;

    public DiscordRequest(String message) {
        this(message, null, null);
    }

    public DiscordRequest(String message, @Nullable RawType rawType, @Nullable String filename) {
        this.filename = filename;
        if (message.length() > 2000) {
            throw new IllegalArgumentException("A message to discord must be smaller then 2000 characters");
        }
        this.message = message;
        this.rawType = rawType;
    }

    @Nullable
    public RawType getRawType() {
        return rawType;
    }

    public String getMessage() {
        return message;
    }

    @Nullable
    public String getFilename() {
        return filename;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DiscordRequest that = (DiscordRequest) o;
        return Objects.equals(message, that.message) && Objects.equals(rawType, that.rawType)
                && Objects.equals(filename, that.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, rawType, filename);
    }
}
