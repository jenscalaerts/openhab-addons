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

import static org.openhab.binding.peblar.internal.PeblarBindingConstants.CHANNEL_CHARGE_CURRENT_LIMIT;
import static org.openhab.binding.peblar.internal.PeblarBindingConstants.CHANNEL_FORCE_ONE_PHASE;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.OpenClosedType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.unit.MetricPrefix;
import org.openhab.core.library.unit.Units;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * The {@link PeblarHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Jens Calaerts - Initial contribution
 */
@NonNullByDefault
public class PeblarHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(PeblarHandler.class);
    private static final String JSON = "application/json";

    private @Nullable PeblarConfiguration config;
    private final HttpClient httpClient;
    private final Gson gson;
    private @Nullable ScheduledFuture<?> schedule;

    public PeblarHandler(Thing thing, HttpClient httpClient, Gson gson) {
        super(thing);
        this.httpClient = httpClient;
        this.gson = gson;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            update();
            return;
        }

        try {
            if (CHANNEL_FORCE_ONE_PHASE.equals(channelUID.getId())) {
                if (command instanceof OnOffType onOff) {
                    updateDataForceSinglePhase(onOff);
                }
            }
            if (CHANNEL_CHARGE_CURRENT_LIMIT.equals(channelUID.getId())) {
                if (command instanceof QuantityType number) {
                    updateChargeLimit(number);
                }
            }
        } catch (PeblarCommunicationException e) {
            logger.warn("failed to handle {} to channel {}: {}", command, channelUID, e.getMessage());
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
        }
    }

    /**
     * starts the {@link PeblarHandler}
     */
    @Override
    public void initialize() {
        config = getConfig().as(PeblarConfiguration.class);

        updateStatus(ThingStatus.UNKNOWN);

        schedule = scheduler.scheduleWithFixedDelay(() -> {
            try {
                update();
                updateStatus(ThingStatus.ONLINE);
            } catch (PeblarCommunicationException e) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
                logger.warn("Failed to fetch charger data {}", e.getMessage());
            }
        }, 0, getConfiguration().refreshInterval, TimeUnit.SECONDS);
    }

    /**
     * Calls the evinterface endpoint
     * 
     * @return The response of the api
     */
    private EvResponse fetchEvData() {
        var request = httpClient.newRequest(getConfiguration().evInterface()).method(HttpMethod.GET);
        return sendRequest(request, EvResponse.class);
    }

    /**
     * adds authorization headers, executes the request and deserializes the
     * response.
     * 
     * @throws PeblarCommunicationException when the a non 200 status code is
     *             returned or the response can not be deserialized.
     */
    private <T> T sendRequest(Request request, Class<T> clazz) {
        try {
            ContentResponse response = request.header(HttpHeader.AUTHORIZATION, getConfiguration().token).send();
            if (response.getStatus() != 200) {
                throw new PeblarCommunicationException("Invalid response code " + response.getStatus());
            }
            var result = gson.fromJson(response.getContentAsString(), clazz);
            if (result == null) {
                throw new PeblarCommunicationException("Invalid response content");
            }
            return result;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new PeblarCommunicationException(e);
        }
    }

    /**
     * Calls the meter data Endpoint
     * 
     * @return The response as MeterResponse
     */
    private MeterResponse fetchMeterData() {
        return sendRequest(httpClient.newRequest(getConfiguration().meter()), MeterResponse.class);
    }

    /**
     * sends and update Request
     * 
     * @throws PeblarCommunicationException when the request failes.
     */
    private void update(Map<String, Object> map) {
        var request = httpClient.newRequest(getConfiguration().evInterface()).method(HttpMethod.PATCH)
                .content(new StringContentProvider(JSON, gson.toJson(map), StandardCharsets.UTF_8), JSON);
        var response = sendRequest(request, EvResponse.class);
        updateEvState(response);
    }

    private void updateDataForceSinglePhase(OnOffType state) {
        Map<String, Object> request = Map.of("Force1Phase", state == OnOffType.ON);
        update(request);
        logger.debug("successfully updated single phase to {}", state);
    }

    /**
     * updates the charge limit to the given state.
     * 
     * @param quantityType
     * @param state the new charge
     */
    private void updateChargeLimit(QuantityType<ElectricCurrent> state) {
        var milliAmps = Objects.requireNonNull(state.toUnit(MetricPrefix.MILLI(Units.AMPERE)));
        Map<String, Object> request = Map.of("ChargeCurrentLimit", milliAmps.intValue());
        update(request);
        logger.debug("successfully updated Charge limit to {}", state);
    }

    /**
     * Updates all related Channels
     */
    private void update() {
        updateEvState(fetchEvData());
        var meterdata = fetchMeterData();

        updateState(PeblarBindingConstants.CHANNEL_CURRENT_PHASE_1, toCurrent(meterdata.currentPhase1()));
        updateState(PeblarBindingConstants.CHANNEL_CURRENT_PHASE_2, toCurrent(meterdata.currentPhase2()));
        updateState(PeblarBindingConstants.CHANNEL_CURRENT_PHASE_3, toCurrent(meterdata.currentPhase3()));

        updateState(PeblarBindingConstants.CHANNEL_VOLTAGE_PHASE_1, toVoltage(meterdata.voltagePhase1()));
        updateState(PeblarBindingConstants.CHANNEL_VOLTAGE_PHASE_2, toVoltage(meterdata.voltagePhase2()));
        updateState(PeblarBindingConstants.CHANNEL_VOLTAGE_PHASE_3, toVoltage(meterdata.voltagePhase3()));

        updateState(PeblarBindingConstants.CHANNEL_POWER_PHASE_1, toPower(meterdata.powerPhase1()));
        updateState(PeblarBindingConstants.CHANNEL_POWER_PHASE_2, toPower(meterdata.powerPhase2()));
        updateState(PeblarBindingConstants.CHANNEL_POWER_PHASE_3, toPower(meterdata.powerPhase3()));

        updateState(PeblarBindingConstants.CHANNEL_POWER_TOTAL, toPower(meterdata.powerTotal()));
        updateState(PeblarBindingConstants.CHANNEL_ENERGY_TOTAL, toEnergy(meterdata.energyTotal()));
        updateState(PeblarBindingConstants.CHANNEL_ENERGY_SESSION, toEnergy(meterdata.energySession()));
    }

    /**
     * Updates the state based on EvResponse.
     * 
     * @param data the data to update the state to.
     */
    private void updateEvState(EvResponse data) {
        updateState(CHANNEL_FORCE_ONE_PHASE, OnOffType.from(data.force1Phase()));
        updateState(PeblarBindingConstants.CHANNEL_CHARGE_CURRENT_LIMIT, toCurrent(data.chargeCurrentLimit()));
        updateState(PeblarBindingConstants.CHANNEL_CHARGE_CURRENT_LIMIT_ACTUAL,
                toCurrent(data.chargeCurrentLimitActual()));
        updateState(PeblarBindingConstants.CHANNEL_CHARGE_CURRENT_LIMIT_SOURCE,
                StringType.valueOf(data.chargeCurrentLimitSource()));
        updateState(PeblarBindingConstants.CHANNEL_LOCK_STATE,
                data.lockState() ? OpenClosedType.CLOSED : OpenClosedType.OPEN);
        updateState(PeblarBindingConstants.CHANNEL_CHARGE_POINT_STATE, StringType.valueOf(data.cpState()));
    }

    /**
     * Creates an current {@link QuantityType quantityType}
     * 
     * @param the number of milliamps
     * @return The data as an ampere quantity
     */
    private QuantityType<ElectricCurrent> toCurrent(int data) {
        return QuantityType.valueOf(data / 1000., Units.AMPERE);
    }

    /**
     * Creates an Potential {@link QuantityType quantityType}
     * 
     * @param data The number of volts
     * @return The data in an ampere quantity in volt
     */
    private QuantityType<ElectricPotential> toVoltage(int data) {
        return QuantityType.valueOf(data, Units.VOLT);
    }

    /**
     * Creates a power {@link QuantityType quantityType}
     * 
     * @param data The number of wats
     * @return The data in an power quantity in Watt
     */
    private QuantityType<Power> toPower(int data) {
        return QuantityType.valueOf(data, Units.WATT);
    }

    /**
     * Creates an energy {@link QuantityType quantityType}
     * 
     * @param data The number of wattHours
     * @return The data in an Energy quantity in Watt Hour
     */
    private QuantityType<Energy> toEnergy(int data) {
        return QuantityType.valueOf(data, Units.WATT_HOUR);
    }

    @Override
    public void dispose() {
        var job = schedule;
        if (job != null) {
            job.cancel(false);
            schedule = null;
        }
    }

    /**
     * a DTO of the Meter endpoint response
     */
    private record MeterResponse(int currentPhase1, int currentPhase2, int currentPhase3, int voltagePhase1,
            int voltagePhase2, int voltagePhase3, int powerPhase1, int powerPhase2, int powerPhase3, int powerTotal,
            int energyTotal, int energySession) {
    }

    private PeblarConfiguration getConfiguration() {
        return Objects.requireNonNull(Optional.ofNullable(config).orElse(new PeblarConfiguration()));
    }
}
