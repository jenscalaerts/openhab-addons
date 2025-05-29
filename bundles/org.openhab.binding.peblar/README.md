# Peblar Binding

A binding connecting to a [Peblar EV chargers](https://peblar.com/) using its [local REST API](https://developer.peblar.com/local-rest-api).
The binding supports Peblar Home, Peblar Home Plus, and Peblar Business chargers.
The binding will allow you to read the current EV interface information and meter information.

To before using this binding, the local api must be enabled on your Peblar charger.
This can be done in the settings->advanced tab on the charger.
Instructions on how to do this can be found on the [Peblar website](https://developer.peblar.com/local-rest-api#section/General).

## Supported Things

- `peblar-charger`: a peblar charger

## Discovery

Auto discovery is not available for this binding.

## Thing Configuration

### `peblar-charger` Thing Configuration

| Name            | Type    | Description                           | Default | Required | Advanced |
|-----------------|---------|---------------------------------------|---------|----------|----------|
| hostname        | text    | Hostname or IP address of the device  | N/A     | yes      | no       |
| token           | text    | The token to access the device        | N/A     | yes      | no       |
| refreshInterval | integer | Interval the device is polled in sec. | 10      | no       | yes      |

## Channels

| Channel                     | Type                      | Read/Write | Description                                                     |
|-----------------------------|---------------------------|------------|-----------------------------------------------------------------|
| charge-current-limit        | Number:ElectricCurrent    | RW         | The maximal current per phase allowed by the `Local REST API`   |
| charge-current-limit-source | String                    | R          | Source of the charge speed limit                                |
| control-pilot-state         | String                    | R          | The state of the control pilot. Shows if the charger is active. |
| lock-state                  | Contact                   | R          | The state of the socket lock                                    |
| force-one-phase             | Switch                    | RW         | Use only 1 phase for charging                                   |
| current-phase-1             | Number:ElectericCurrent   | R          | The current on phase 1                                          |
| current-phase-2             | Number:ElectericCurrent   | R          | The current on phase 2                                          |
| current-phase-3             | Number:ElectericCurrent   | R          | The current on phase 3                                          |
| voltage-phase-1             | Number:ElectericPotential | R          | The voltage on phase 1                                          |
| voltage-phase-2             | Number:ElectericPotential | R          | The voltage on phase 2                                          |
| voltage-phase-3             | Number:ElectericPotential | R          | The voltage on phase 3                                          |
| power-phase-1               | Number:Power              | R          | The power on Phase 1                                            |
| power-phase-2               | Number:Power              | R          | The power on Phase 2                                            |
| power-phase-3               | Number:Power              | R          | The power on Phase 3                                            |
| power-total                 | Number:Power              | R          | The power on all phase combined                                 |
| energy-total                | Number:Energy             | R          | The total engery delivered by the charger                       |
| energy-session              | Number:Energy             | R          | The energy supplied in the last or current charging session     |

### Charge limit sources

From the [API documentation](https://developer.peblar.com/local-rest-api#tag/EVInterface/paths/~1evinterface/get).

- Charging cable: The maximum rated current of the attached cable.
- High temperature: Charger internal temperature.
- Installation limit: The maximum installation current configured during commissioning.
- Dynamic load balancing: Household installation phase current reached maximum.
- Group load balancing: A maximum communicated by the leader of the group.
- Overcurrent protection: EV exceeded communicated maximum current.
- Hardware limitation: Physical limits of the charger.
- Power factor: EV charged with too low power factor.
- OCPP smart charging: Smart charging profile installed by CPO.
- Phase imbalance: Too much imbalance between phases.
- Local scheduled charging: Locally configured scheduled charging.
- Solar charging: Amount of exported energy.
- Current limiter: User selected limit via web web-interface.
- Local REST API: Limit set by this API.
- Local Modbus API: Limit set by the Modbus API.
- External power limit: External IO defined limit.
- Household power limit: Total household power capacity limit.

### Control Pilot State

- State A: No EV connected
- State B: EV connected but suspended by either EV or charger
- State C: EV connected and charging
- State E: Error, short to PE or powered off
- State F: Fault detected by charger
- Invalid: Invalid CP level measured
- Unknown: CP signal cannot be measured.

## Full Example

### Thing Configuration

```java
Thing peblar:peblar-charger:my_charger [ hostname="192.168.1.67", token="keepitsecret", refreshDelay=5 ]
```

### Item Configuration

```java
Number:ElectricCurrent Peblar_Binding_Thing_Current "Current" ["Current", "Setpoint"] { channel="peblar:peblar-charger:my_charger:charge-current-limit" }
Number:ElectricCurrent Peblar_Binding_Thing_Currentphase1 "Electric Current Phase 1" <energy> ["Current", "Measurement"] { channel="peblar:peblar-charger:my_charger:current-phase-1" }
Number:ElectricCurrent Peblar_Binding_Thing_Currentphase2 "Electric Current Phase 2" <energy> ["Current", "Measurement"] { channel="peblar:peblar-charger:my_charger:current-phase-2" }
Number:ElectricCurrent Peblar_Binding_Thing_Currentphase3 "Electric Current Phase 3" <energy> ["Current", "Measurement"] { channel="peblar:peblar-charger:my_charger:current-phase-3" }
Number:ElectricPotential Peblar_Binding_Thing_Voltage_Phase1 "Electric Voltage" <energy> ["Measurement", "Voltage"] { channel="peblar:peblar-charger:my_charger:voltage-phase-1" }
Number:ElectricPotential Peblar_Binding_Thing_Voltage_Phase2 "Electric Voltage" <energy> ["Measurement", "Voltage"] { channel="peblar:peblar-charger:my_charger:voltage-phase-2" }
Number:ElectricPotential Peblar_Binding_Thing_Voltage_Phase3 "Electric Voltage" <energy> ["Measurement", "Voltage"] { channel="peblar:peblar-charger:my_charger:voltage-phase-3" }
Number:Energy Peblar_Binding_Thing_Energy_session "Energy Session" <energy> ["Energy", "Measurement"] { channel="peblar:peblar-charger:my_charger:energy-session" }
Number:Energy Peblar_Binding_Thing_Energy_total "Energy Total" <energy> ["Energy", "Measurement"] { channel="peblar:peblar-charger:my_charger:energy-total" }
Number:Power Peblar_Binding_Thing_Power_Phase1 "Electric Power" <energy> ["Measurement", "Power"] { channel="peblar:peblar-charger:my_charger:power-phase-1" }
Number:Power Peblar_Binding_Thing_Power_Phase2 "Electric Power" <energy> ["Measurement", "Power"] { channel="peblar:peblar-charger:my_charger:power-phase-2" }
Number:Power Peblar_Binding_Thing_Power_Phase3 "Electric Power" <energy> ["Measurement", "Power"] { channel="peblar:peblar-charger:my_charger:power-phase-3" }
Number:Power Peblar_Binding_Thing_Power_Total "Electric Power Total" <energy> ["Measurement", "Power"] { channel="peblar:peblar-charger:my_charger:power-total" }
String Peblar_Binding_Thing_Control_Pilot_State "Control Pilot State"
```
