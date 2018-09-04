package nl.tudelft.simulation.actor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.media.j3d.Bounds;
import javax.vecmath.Point3d;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.actor.messagehandlers.MessageHandlerInterface;
import nl.tudelft.simulation.content.HandlerInterface;
import nl.tudelft.simulation.event.EventProducer;
import nl.tudelft.simulation.language.d3.BoundingBox;
import nl.tudelft.simulation.language.d3.DirectedPoint;
import nl.tudelft.simulation.messaging.Message;
import nl.tudelft.simulation.messaging.devices.components.ReceivingDeviceInterface;
import nl.tudelft.simulation.messaging.devices.components.SendingDeviceInterface;
import nl.tudelft.simulation.messaging.devices.types.DeviceType;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;
import nl.tudelft.simulation.unit.simulator.DEVSSimulatorInterfaceUnit;

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
public abstract class Actor extends EventProducer implements ActorInterface
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the content handlers for this actor */
    protected Map<Class<?>, Set<HandlerInterface>> contentHandlers = new HashMap<Class<?>, Set<HandlerInterface>>();

    /** the name of an actor */
    protected String name;

    /** the sending devices */
    protected Set<SendingDeviceInterface> sendingDevices;

    /** the receiving devices with their messageHandler */
    protected Map<ReceivingDeviceInterface, MessageHandlerInterface> receivingDevices;

    /** the simulator to schedule on */
    protected DEVSSimulatorInterfaceUnit simulator;

    /** the location of the actor */
    protected DirectedPoint location;

    /** the description of the location of an actor */
    protected String locationDescription = "";

    /** the logger. */
    private static Logger logger = LogManager.getLogger(Actor.class);

    /**
     * Constructs a new Actor
     * @param name the name of the actor
     * @param simulator the simulator to use
     * @param position the location of the actor
     */
    public Actor(final String name, final DEVSSimulatorInterfaceUnit simulator, final Point3d position)
    {
        super();
        this.name = name;
        this.sendingDevices = new HashSet<SendingDeviceInterface>();
        this.receivingDevices = new HashMap<ReceivingDeviceInterface, MessageHandlerInterface>();
        this.simulator = simulator;
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
            final DistContinuousDurationUnit checkInterval)
    {
        addReceivingDevice(device, messageHandler);
        setCheckInterval(device, checkInterval);
    }

    /**
     * The method implements a timer for an existing device to check periodically for content.
     * @param device the device to set the check interval for
     * @param checkInterval the interval for checking the device
     */
    public void setCheckInterval(final ReceivingDeviceInterface device, final DistContinuousDurationUnit checkInterval)
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
            logger.warn("setCheckInterval", e);
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

    /** {@inheritDoc} */
    @Override
    public boolean handleMessage(final Message message)
    {
        return this.handleContent(message.getBody());
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        Set<HandlerInterface> handlers = this.resolveContentHandlers(content.getClass());
        if (handlers.size() == 0)
        {
            logger.warn("handleContent - No actor content handler available for content type " + content.getClass() + ", actor "
                    + this.getName());
        }
        boolean success = false; // no correct handling yet
        for (HandlerInterface handler : handlers)
        {
            // Now we invoke the business logic on the handler
            success |= handler.handleContent(content);
        }
        if (!success)
        {
            logger.warn("handleContent - No actor content handler successfully handled content type " + content.getClass()
                    + ", actor " + this.getName());
        }
        return success;
    }

    /**
     * adds a handler for message content
     * @param contentClass the content class to add
     * @param handler the handler for the content class
     */
    public void addContentHandler(final Class<?> contentClass, final HandlerInterface handler)
    {
        Set<HandlerInterface> handlers = this.contentHandlers.get(contentClass);
        if (handlers == null)
        {
            handlers = new HashSet<HandlerInterface>();
            this.contentHandlers.put(contentClass, handlers);
        }
        handlers.add(handler);
    }

    /**
     * removes a handler for message content
     * @param contentClass the content class to add
     * @param handler the handler for the content class
     */
    public void removeContentHandler(final Class<?> contentClass, final HandlerInterface handler)
    {
        Set<HandlerInterface> handlers = this.contentHandlers.get(contentClass);
        if (handlers != null)
        {
            handlers.remove(handler);
        }
    }

    /**
     * Resolves the contentHandler for a specific content type. It also looks for a handler that handles any of the
     * superclasses. All matching handlers will be added to the Set that is returned.
     * @param contentClass the type expressed by the content
     * @return a handler
     */
    private Set<HandlerInterface> resolveContentHandlers(final Class<?> contentClass)
    {
        Class<?> classIterator = contentClass;
        Set<HandlerInterface> handlers = new HashSet<HandlerInterface>();
        try
        {
            while (classIterator != null)
            {
                if (this.contentHandlers.containsKey(classIterator))
                {
                    handlers.addAll(this.contentHandlers.get(classIterator));
                }
                classIterator = classIterator.getSuperclass();
            }
        }
        catch (Exception e)
        {
            logger.fatal("resolveContentHandlers", e);
        }
        return handlers;
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
    public String getName()
    {
        return this.name;
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

    /** {@inheritDoc} */
    @Override
    public DEVSSimulatorInterfaceUnit getSimulator()
    {
        return this.simulator;
    }

    /** {@inheritDoc} */
    @Override
    public Time getSimulatorTime()
    {
        Time time = Time.ZERO;
        try
        {
            time = this.simulator.getSimulatorTime().get();
        }
        catch (Exception e)
        {
            logger.warn("getSimulatorTime", e);
        }
        return time;
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
                    possibleDevices.put(new Double(1000.0 * _sendingDevices[i].getTransmissionDelay()
                            - 0.001 * _sendingDevices[i].getTransmissionFrequency()), _sendingDevices[i]);
                }
            }
        }
        if (possibleDevices.size() == 0)
        {
            logger.warn("resolveFastestDevice - No appropriate device(s) found between actor " + sender.getName() + " and "
                    + receiver.getName());
            return null;
        }
        // return the key with the highest value (speed or priority)
        return possibleDevices.get(possibleDevices.lastKey());
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return this.name;
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
