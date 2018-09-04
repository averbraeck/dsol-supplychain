package nl.tudelft.simulation.supplychain.roles;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.tudelft.simulation.content.HandlerInterface;
import nl.tudelft.simulation.event.EventProducer;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.unit.simulator.DEVSSimulatorInterfaceUnit;

/**
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Role extends EventProducer implements HandlerInterface
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the owner of the role */
    protected SupplyChainActor owner = null;

    /** the handlers for this role */
    protected Map<Class, Set<HandlerInterface>> contentHandlers = new HashMap<Class, Set<HandlerInterface>>();

    /** the default stream to use for the time delays */
    protected StreamInterface stream = null;

    /** the simulator to schedule on */
    protected DEVSSimulatorInterfaceUnit simulator;

    /** the logger. */
    private static Logger logger = LogManager.getLogger(Role.class);

    /**
     * Constructs a new Role
     * @param owner the owner of this role
     * @param simulator the simulator to schedule on
     */
    public Role(final SupplyChainActor owner, final DEVSSimulatorInterfaceUnit simulator)
    {
        super();
        this.owner = owner;
        this.simulator = simulator;
        try
        {
            this.stream = this.simulator.getReplication().getStream("default");
        }
        catch (RemoteException exception)
        {
            logger.fatal("<init>", exception);
        }
    }

    /**
     * @return Returns the owner.
     */
    public SupplyChainActor getOwner()
    {
        return this.owner;
    }

    /**
     * @see nl.tudelft.simulation.content.HandlerInterface#handleContent(java.io.Serializable)
     */
    public boolean handleContent(final Serializable content)
    {
        Set handlers = this.resolveContentHandlers(content.getClass());
        boolean success = false; // no correct handling yet
        Iterator i = handlers.iterator();
        while (i.hasNext())
        {
            HandlerInterface handler = (HandlerInterface) i.next();
            // Now we invoke the business logic on the handler
            success |= handler.handleContent(content);
        }
        return success;
    }

    /**
     * adds a handler for message content
     * @param contentClass the content class to add
     * @param handler the handler for the content class
     */
    public void addContentHandler(final Class contentClass, final HandlerInterface handler)
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
    public void removeContentHandler(final Class contentClass, final HandlerInterface handler)
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
    protected Set<HandlerInterface> resolveContentHandlers(final Class contentClass)
    {
        Class classIterator = contentClass;
        Set<HandlerInterface> handlers = new HashSet<HandlerInterface>();
        while (classIterator != null)
        {
            if (this.contentHandlers.get(classIterator) != null)
            {
                handlers.addAll(this.contentHandlers.get(classIterator));
            }
            classIterator = classIterator.getSuperclass();
        }
        return handlers;
    }

}
