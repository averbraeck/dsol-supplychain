package nl.tudelft.simulation.actor;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.djunits.value.vdouble.scalar.Time;
import org.djutils.event.EventProducer;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.content.HandlerInterface;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.messaging.Message;

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
public abstract class InternalActor extends EventProducer implements InternalActorInterface
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the content handlers for this actor */
    protected Map<Class<?>, Set<HandlerInterface>> contentHandlers = new LinkedHashMap<>();

    /** the name of an actor */
    protected String name;

    /** the simulator to schedule on */
    protected DEVSSimulatorInterface.TimeDoubleUnit simulator;

    /**
     * Constructs a new Actor
     * @param name the name of the actor
     * @param simulator the simulator to use
     */
    public InternalActor(final String name, final DEVSSimulatorInterface.TimeDoubleUnit simulator)
    {
        super();
        this.name = name;
        this.simulator = simulator;
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
        return handleContent(content, true);
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content, final boolean logWarnings)
    {
        Set<HandlerInterface> handlers = this.resolveContentHandlers(content.getClass());
        if (handlers.size() == 0 && logWarnings)
        {
            Logger.warn("handleContent - No actor content handler available for content type {}, actor {}", content.getClass(),
                    this.getName());
        }
        boolean success = false; // no correct handling yet
        for (HandlerInterface handler : handlers)
        {
            // Now we invoke the business logic on the handler
            success |= handler.handleContent(content);
        }
        if (!success && logWarnings)
        {
            Logger.warn("handleContent - No actor content handler successfully handled content type {}, actor {}",
                    content.getClass(), this.getName());
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
            handlers = new LinkedHashSet<HandlerInterface>();
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
    protected Set<HandlerInterface> resolveContentHandlers(final Class<?> contentClass)
    {
        Class<?> classIterator = contentClass;
        Set<HandlerInterface> handlers = new LinkedHashSet<HandlerInterface>();
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
            Logger.error(e, "resolveContentHandlers");
        }
        return handlers;
    }

    /** {@inheritDoc} */
    @Override
    public String getName()
    {
        return this.name;
    }

    /** {@inheritDoc} */
    @Override
    public DEVSSimulatorInterface.TimeDoubleUnit getSimulator()
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
            time = this.simulator.getSimulatorTime();
        }
        catch (Exception e)
        {
            Logger.warn(e, "getSimulatorTime");
        }
        return time;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return this.name;
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return getName();
    }
}
