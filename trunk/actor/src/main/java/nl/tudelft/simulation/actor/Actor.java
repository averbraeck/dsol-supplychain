package nl.tudelft.simulation.actor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.media.j3d.Bounds;
import javax.vecmath.Point3d;

import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.actor.messagehandlers.MessageHandlerInterface;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.language.d3.BoundingBox;
import nl.tudelft.simulation.language.d3.DirectedPoint;
import nl.tudelft.simulation.messaging.devices.components.ReceivingDeviceInterface;
import nl.tudelft.simulation.messaging.devices.components.SendingDeviceInterface;
import nl.tudelft.simulation.messaging.devices.types.DeviceType;

/**
 * The actor is the basic entity in the nl.tudelft.simulation.actor package. It implements the behavior of a 'communicating'
 * object, that is able to exchange messages with other actors using devices. The devices can be found in the
 * nl.tudelft.simulation.messaging.device package; the default message object is nl.tudelft.simulation.messaging.Message. The
 * actor has to take care of periodically looking at the devices whether there are any messages, or -in case of some devices-
 * the actor is informed of the fact that there is a waiting message. All this is implemented through the event mechanism. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class Actor extends InternalActor implements ActorInterface
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the sending devices */
    protected Set<SendingDeviceInterface> sendingDevices;

    /** the receiving devices with their messageHandler */
    protected Map<ReceivingDeviceInterface, MessageHandlerInterface> receivingDevices;

    /** the location of the actor */
    protected DirectedPoint location;

    /** the description of the location of an actor */
    protected String locationDescription = "";

    /**
     * Constructs a new Actor
     * @param name the name of the actor
     * @param simulator the simulator to use
     * @param position the location of the actor
     */
    public Actor(final String name, final DEVSSimulatorInterface.TimeDoubleUnit simulator, final Point3d position)
    {
        super(name, simulator);
        this.sendingDevices = new LinkedHashSet<>();
        this.receivingDevices = new LinkedHashMap<>();
        this.location = new DirectedPoint(position);
    }

    /**
     * adds a sending device to the actor
     * @param device device
     * @return success or not
     */
    public boolean addSendingDevice(final SendingDeviceInterface device)
    {
        return this.sendingDevices.add(device);
    }

    /**
     * adds a receiving device to the actor. The method does not implement a timer to check periodically for content.
     * @param device device
     * @param messageHandler the handler to use
     */
    public void addReceivingDevice(final ReceivingDeviceInterface device, final MessageHandlerInterface messageHandler)
    {
        this.receivingDevices.put(device, messageHandler);
    }

    /**
     * adds a receiving device to the actor. The method implements a timer to check periodically for content.
     * @param device device
     * @param messageHandler the handler to use
     * @param checkInterval the distribution of times for checking the device
     */
    public void addReceivingDevice(final ReceivingDeviceInterface device, final MessageHandlerInterface messageHandler,
            final DistContinuousDuration checkInterval)
    {
        addReceivingDevice(device, messageHandler);
        setCheckInterval(device, checkInterval);
    }

    /**
     * The method implements a timer for an existing device to check periodically for content.
     * @param device the device to set the check interval for
     * @param checkInterval the interval for checking the device
     */
    public void setCheckInterval(final ReceivingDeviceInterface device, final DistContinuousDuration checkInterval)
    {
        try
        {
            if (!this.receivingDevices.containsKey(device))
            {
                throw new Exception("Receiving device " + device.getName() + " not found at actor " + this.getName());
            }
            Duration delta = checkInterval.draw();
            this.simulator.scheduleEventRel(delta, this, this, "checkReceivingDevice", new Object[] { device });
            this.simulator.scheduleEventRel(delta, this, this, "setCheckInterval",
                    new Serializable[] { device, checkInterval });
        }
        catch (Exception e)
        {
            Logger.warn(e, "setCheckInterval");
        }
    }

    /**
     * Checks whether the receiving device has messages to handle.
     * @param device the device to check
     */
    public void checkReceivingDevice(final ReceivingDeviceInterface device)
    {
        if (!device.getQueue().isEmpty())
        {
            MessageHandlerInterface messageHandler = this.receivingDevices.get(device);
            messageHandler.handleMessageQueue(device.getQueue());
        }
    }

    /** {@inheritDoc} */
    @Override
    public ReceivingDeviceInterface[] getReceivingDevices()
    {
        return this.receivingDevices.keySet().toArray(new ReceivingDeviceInterface[this.receivingDevices.size()]);
    }

    /** {@inheritDoc} */
    @Override
    public ReceivingDeviceInterface[] getReceivingDevices(final DeviceType deviceType)
    {
        ArrayList<ReceivingDeviceInterface> result = new ArrayList<ReceivingDeviceInterface>();
        for (ReceivingDeviceInterface device : this.receivingDevices.keySet())
        {
            if (device.getDeviceType().equals(deviceType))
            {
                result.add(device);
            }
        }
        return result.toArray(new ReceivingDeviceInterface[result.size()]);
    }

    /** {@inheritDoc} */
    @Override
    public SendingDeviceInterface[] getSendingDevices()
    {
        return this.sendingDevices.toArray(new SendingDeviceInterface[this.sendingDevices.size()]);
    }

    /** {@inheritDoc} */
    @Override
    public SendingDeviceInterface[] getSendingDevices(final DeviceType deviceType)
    {
        ArrayList<SendingDeviceInterface> result = new ArrayList<SendingDeviceInterface>();
        for (SendingDeviceInterface device : this.sendingDevices)
        {
            if (device.getDeviceType().equals(deviceType))
            {
                result.add(device);
            }
        }
        return result.toArray(new SendingDeviceInterface[result.size()]);
    }

    /**
     * removes a device to the actor
     * @param device device
     * @return succes
     */
    public boolean removeSendingDevice(final SendingDeviceInterface device)
    {
        return this.sendingDevices.remove(device);
    }

    /**
     * removed a device to the actor
     * @param device device
     */
    public void removeReceivingDevice(final ReceivingDeviceInterface device)
    {
        this.receivingDevices.remove(device);
    }

    /** {@inheritDoc} */
    @Override
    public DirectedPoint getLocation()
    {
        return this.location;
    }

    /** {@inheritDoc} */
    @Override
    public Bounds getBounds()
    {
        return new BoundingBox(1.0, 1.0, 2.0);
    }

    /**
     * Find the fastest connection between the sender and the receiver, based on the devices that they share. We look at the
     * minimal transmission delay, and sort within that on maximum transmission frequency.
     * @param sender the sender
     * @param receiver the receiver
     * @return the fastest device that is available at sender and receiver
     */
    protected SendingDeviceInterface resolveFastestDevice(final ActorInterface sender, final ActorInterface receiver)
    {
        SendingDeviceInterface[] _sendingDevices = sender.getSendingDevices();
        ReceivingDeviceInterface[] _receivingDevices = receiver.getReceivingDevices();
        SortedMap<Double, SendingDeviceInterface> possibleDevices = new TreeMap<Double, SendingDeviceInterface>();
        for (int i = 0; i < _sendingDevices.length; i++)
        {
            for (int j = 0; j < _receivingDevices.length; j++)
            {
                if (_sendingDevices[i].getDeviceType().equals(_receivingDevices[j].getDeviceType()))
                {
                    possibleDevices.put(Double.valueOf(1000.0 * _sendingDevices[i].getTransmissionDelay()
                            - 0.001 * _sendingDevices[i].getTransmissionFrequency()), _sendingDevices[i]);
                }
            }
        }
        if (possibleDevices.size() == 0)
        {
            Logger.warn("resolveFastestDevice - No appropriate device(s) found between actor {} and {}", sender.getName(),
                    receiver.getName());
            return null;
        }
        // return the key with the highest value (speed or priority)
        return possibleDevices.get(possibleDevices.lastKey());
    }

    /**
     * @return Returns the locationDescription.
     */
    public String getLocationDescription()
    {
        return this.locationDescription;
    }

    /**
     * @param locationDescription The locationDescription to set.
     */
    public void setLocationDescription(final String locationDescription)
    {
        this.locationDescription = locationDescription;
    }
}
