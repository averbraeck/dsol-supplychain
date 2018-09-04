package nl.tudelft.simulation.messaging.devices.components;

import nl.tudelft.simulation.event.EventProducer;
import nl.tudelft.simulation.messaging.devices.types.DeviceType;

/**
 * An abstract Device that can be extended to a sending device, a receiving device, or a device that combines sending and
 * receiving. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class AbstractDevice extends EventProducer implements DeviceInterface
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** state of the device */
    private DeviceState state = DeviceState.IDLE;

    /** device type */
    private DeviceType deviceType;

    /** Name of the device */
    protected String name = null;

    /** the transmission delay of the device */
    private int transmissionDelay;

    /** the maximum transmission frequency of the device */
    private double transmissionFrequency;

    /**
     * constructs a new device, take the transmission delay and frequency from the DeviceType.
     * @param name the name or description of the device
     * @param deviceType the type of device
     */
    public AbstractDevice(final String name, final DeviceType deviceType)
    {
        this(name, deviceType, deviceType.getTransmissionDelay(), deviceType.getTransmissionFrequency());
    }

    /**
     * constructs a new device, override the default transmission delay and frequency from the DeviceType.
     * @param name the name or description of the device
     * @param deviceType the type of device
     * @param transmissionDelay the default logarithmic transmission delay of the device.
     * @param transmissionFrequency the maximum transmission frequency of the device.
     */
    public AbstractDevice(final String name, final DeviceType deviceType, final int transmissionDelay,
            final double transmissionFrequency)
    {
        super();
        this.name = name;
        this.deviceType = deviceType;
        this.transmissionDelay = transmissionDelay;
        this.transmissionFrequency = transmissionFrequency;
    }

    /** {@inheritDoc} */
    @Override
    public String getName()
    {
        return this.name;
    }

    /** {@inheritDoc} */
    @Override
    public DeviceState getState()
    {
        return this.state;
    }

    /**
     * @param state the new device state
     */
    public void setState(final DeviceState state)
    {
        this.state = state;
        super.fireEvent(DeviceInterface.STATE_CHANGE_EVENT, state);
    }

    /** {@inheritDoc} */
    @Override
    public DeviceType getDeviceType()
    {
        return this.deviceType;
    }

    /** {@inheritDoc} */
    @Override
    public int getTransmissionDelay()
    {
        return this.transmissionDelay;
    }

    /** {@inheritDoc} */
    @Override
    public double getTransmissionFrequency()
    {
        return this.transmissionFrequency;
    }
}
