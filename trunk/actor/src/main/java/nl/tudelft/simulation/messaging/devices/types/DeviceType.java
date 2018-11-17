package nl.tudelft.simulation.messaging.devices.types;

import java.io.Serializable;

/**
 * The DeviceType is introduced to be able to match individual sending devices and receiving devices of different Actors. When
 * one actor has a device "FAX5544215", and another actor has a "Samsung Fax", how could we determine that the first fax is able
 * to send to the second one? The DeviceType is able to resolve this, because both Devices will have the same DeviceType.
 * DeviceType contains a default "speed" field, that can be overridden for individual devices. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DeviceType implements Serializable
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the unique name of the device type to identify it */
    private String name;

    /** the transmission delay of the device */
    private int transmissionDelay;

    /** the maximum transmission frequency of the device */
    private double transmissionFrequency;

    /**
     * the NETWORK delay, defined on a log-10 scale, indicating the time it takes averagely in seconds to reach the other Actor.
     */
    public static final int DELAY_NETWORK = -1;

    /**
     * the EMAIL delay, defined on a log-10 scale, indicating the time it takes averagely in seconds to reach the other Actor.
     */
    public static final int DELAY_EMAIL = 0;

    /**
     * the FAX delay, defined on a log-10 scale, indicating the time it takes averagely in seconds to reach the other Actor.
     */
    public static final int DELAY_FAX = 3;

    /**
     * the PHONE delay, defined on a log-10 scale, indicating the time it takes averagely in seconds to reach the other Actor.
     */
    public static final int DELAY_PHONE = 4;

    /**
     * the LETTER delay, defined on a log-10 scale, indicating the time it takes averagely in seconds to reach the other Actor.
     */
    public static final int DELAY_LETTER = 5;

    /*
     * the reference implementations of the DeviceType
     */

    /** the NETWORK DEVICETYPE */
    public static final DeviceType NETWORK = new DeviceType("Network", DeviceType.DELAY_NETWORK, 100.0);

    /** the EMAIL DEVICETYPE */
    public static final DeviceType EMAIL = new DeviceType("Email", DeviceType.DELAY_EMAIL, 20.0);

    /** the FAX DEVICETYPE */
    public static final DeviceType FAX = new DeviceType("Fax", DeviceType.DELAY_FAX, 0.1);

    /** the PHONE DEVICETYPE */
    public static final DeviceType PHONE = new DeviceType("Phone", DeviceType.DELAY_PHONE, 0.01);

    /** the LETTER DEVICETYPE */
    public static final DeviceType LETTER = new DeviceType("Letter", DeviceType.DELAY_LETTER, 1.0e-5);

    /** the OTHER DEVICETYPE */
    public static final DeviceType OTHER = new DeviceType("Other", DeviceType.DELAY_LETTER, 1.0e-5);

    /**
     * Create a new device type. Please note: the uniqueness of the name is not cheched. The speed of the device type is an
     * indication of the time it takes to get the message from sender to receiver. The speed is given in several classes, for
     * which static constants have been defined. The maximum transmission frequency indicates the average number of messages
     * (say 1 page with data) that can be sent per second over the communication channel.
     * @param name the unique name of the device type.
     * @param transmissionDelay the default logarithmic transmission delay of the device type.
     * @param transmissionFrequency the maximum transmission frequency of the device type.
     */
    public DeviceType(final String name, final int transmissionDelay, final double transmissionFrequency)
    {
        super();
        this.name = name;
        this.transmissionDelay = transmissionDelay;
        this.transmissionFrequency = transmissionFrequency;
    }

    /**
     * give back the unique name of the device type
     * @return Returns the name.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * The transmission delay is defined on a log-10 scale, indicating the time it takes averagely in seconds to reach the other
     * Actor.
     * @return Returns the transmissionDelay.
     */
    public int getTransmissionDelay()
    {
        return this.transmissionDelay;
    }

    /**
     * The maximum transmission frequency indicates the average number of messages (say 1 page with data) that can be sent per
     * second over the communication channel.
     * @return Returns the transmissionFrequency.
     */
    public double getTransmissionFrequency()
    {
        return this.transmissionFrequency;
    }
}
