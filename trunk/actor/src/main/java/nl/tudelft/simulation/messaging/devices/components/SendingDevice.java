package nl.tudelft.simulation.messaging.devices.components;

import nl.tudelft.simulation.messaging.devices.types.DeviceType;

/**
 * Abstract implementation of a sending device. The send method still needs to be implemented. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class SendingDevice extends AbstractDevice implements SendingDeviceInterface
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /**
     * constructs a new device, take the transmission delay and frequency from the DeviceType.
     * @param name the name or description of the device
     * @param deviceType the type of device
     */
    public SendingDevice(final String name, final DeviceType deviceType)
    {
        super(name, deviceType);
    }

    /**
     * constructs a new device, override the default transmission delay and frequency from the DeviceType.
     * @param name the name or description of the device
     * @param deviceType the type of device
     * @param transmissionDelay the default logarithmic transmission delay of the device.
     * @param transmissionFrequency the maximum transmission frequency of the device.
     */
    public SendingDevice(final String name, final DeviceType deviceType, final int transmissionDelay,
            final double transmissionFrequency)
    {
        super(name, deviceType, transmissionDelay, transmissionFrequency);
    }
}
