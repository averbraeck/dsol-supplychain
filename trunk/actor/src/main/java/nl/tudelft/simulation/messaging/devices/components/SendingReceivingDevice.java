package nl.tudelft.simulation.messaging.devices.components;

import java.io.Serializable;
import java.rmi.RemoteException;

import org.djutils.event.EventInterface;
import org.djutils.event.EventListenerInterface;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.messaging.Message;
import nl.tudelft.simulation.messaging.queues.MessageQueueInterface;

/**
 * A SendingReceivingDevice combines the sending device and receiving device in one aggregated object. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class SendingReceivingDevice extends AbstractDevice
        implements SendingDeviceInterface, ReceivingDeviceInterface, EventListenerInterface
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the sender */
    private SendingDeviceInterface sender = null;

    /** the receiver */
    private ReceivingDeviceInterface receiver = null;

    /**
     * constructs a new SendingReceivingDevice
     * @param name the name of the device
     * @param sender the sending part of this device
     * @param receiver the receiving part of this device
     */
    public SendingReceivingDevice(final String name, final ReceivingDeviceInterface receiver,
            final SendingDeviceInterface sender)
    {
        super(name, sender.getDeviceType());
        this.sender = sender;
        this.receiver = receiver;
        try
        {
            this.sender.addListener(this, DeviceInterface.STATE_CHANGE_EVENT);
            this.receiver.addListener(this, DeviceInterface.STATE_CHANGE_EVENT);
            this.receiver.addListener(this, ReceivingDeviceInterface.RECEIVED_NEW_MESSAGE_EVENT);
        }
        catch (RemoteException e)
        {
            Logger.warn(e, "<init>");
        }
    }

    /** {@inheritDoc} */
    @Override
    public Serializable send(final Message message)
    {
        return this.sender.send(message);
    }

    /** {@inheritDoc} */
    @Override
    public Serializable receive(final Message message)
    {
        return this.receiver.receive(message);
    }

    /** {@inheritDoc} */
    @Override
    public MessageQueueInterface getQueue()
    {
        return this.receiver.getQueue();
    }

    /** {@inheritDoc} */
    @Override
    public void notify(final EventInterface event)
    {
        this.fireEvent(event.getType(), event.getContent());
    }
}
