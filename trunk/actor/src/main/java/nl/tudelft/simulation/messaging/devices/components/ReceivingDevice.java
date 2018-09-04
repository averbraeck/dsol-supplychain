package nl.tudelft.simulation.messaging.devices.components;

import java.io.Serializable;

import nl.tudelft.simulation.messaging.Message;
import nl.tudelft.simulation.messaging.devices.types.DeviceType;
import nl.tudelft.simulation.messaging.queues.MessageQueueInterface;

/**
 * Standard implementation of a receiving device. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class ReceivingDevice extends AbstractDevice implements ReceivingDeviceInterface
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** The queue to store the messages in */
    protected MessageQueueInterface queue = null;

    /**
     * constructs a new receiving device, override the default transmission delay and frequency from the DeviceType.
     * @param name the name or description of the device
     * @param deviceType the type of device
     * @param queue the type of queue to store the messages in
     */
    public ReceivingDevice(final String name, final DeviceType deviceType, final MessageQueueInterface queue)
    {
        super(name, deviceType);
        this.queue = queue;
    }

    /**
     * constructs a new receiving device, override the default transmission delay and frequency from the DeviceType.
     * @param name the name or description of the device
     * @param deviceType the type of device
     * @param transmissionDelay the default logarithmic transmission delay of the device.
     * @param transmissionFrequency the maximum transmission frequency of the device.
     * @param queue the type of queue to store the messages in
     */
    public ReceivingDevice(final String name, final DeviceType deviceType, final int transmissionDelay,
            final double transmissionFrequency, final MessageQueueInterface queue)
    {
        super(name, deviceType, transmissionDelay, transmissionFrequency);
        this.queue = queue;
    }

    /** {@inheritDoc} */
    @Override
    public Serializable receive(final Message message)
    {
        if (!super.getState().isWorking())
        {
            return new Exception("Device not working, state is " + super.getState().getDescription());
        }
        this.queue.add(message);
        super.fireEvent(ReceivingDeviceInterface.RECEIVED_NEW_MESSAGE_EVENT, null);
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public MessageQueueInterface getQueue()
    {
        return this.queue;
    }
}
