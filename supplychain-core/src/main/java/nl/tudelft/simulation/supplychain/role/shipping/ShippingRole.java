package nl.tudelft.simulation.supplychain.role.shipping;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.ShippingOrder;
import nl.tudelft.simulation.supplychain.content.TransportQuote;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiverDirect;
import nl.tudelft.simulation.supplychain.process.AutonomousProcess;
import nl.tudelft.simulation.supplychain.role.transporting.TransportOption;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingActor;

/**
 * The ShippingRole is concerned with booking transport and taking products out of the warehouse to have them shipped by a
 * transporter.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ShippingRole extends Role<ShippingRole>
{
    /** */
    private static final long serialVersionUID = 20221206L;

    /** the received transport quotes belonging to transport options. */
    private Map<TransportOption, TransportQuote> transportQuoteMap = new LinkedHashMap<>();

    /** the necessary autonomous processes. */
    private static Set<Class<? extends AutonomousProcess<ShippingRole>>> necessaryAutonomousProcesses = Set.of();

    /** the necessary content handlers. */
    private static Set<Class<? extends Content>> necessaryContentHandlers = Set.of(ShippingOrder.class);

    /**
     * Create a SellingRole object for an actor.
     * @param owner the owner of this role
     */
    public ShippingRole(final WarehousingActor owner)
    {
        super("shipping", owner, new ContentReceiverDirect());
    }

    /**
     * Add a transport quote in the map for later retrieval.
     * @param tq the transport quote to add
     */
    public void addTransportQuote(final TransportQuote tq)
    {
        this.transportQuoteMap.put(tq.transportOption(), tq);
    }

    /**
     * Remove a transport option and the related transport quote from the map.
     * @param to the transport option to remove
     */
    public void removeTransportOption(final TransportOption to)
    {
        this.transportQuoteMap.remove(to);
    }

    /**
     * Return a transport quote from the map.
     * @param to the transport option to retrieve
     * @return the transport quote belongin to the transport option
     */
    public TransportQuote getTransportQuote(final TransportOption to)
    {
        return this.transportQuoteMap.get(to);
    }

    @Override
    public WarehousingActor getActor()
    {
        return (WarehousingActor) super.getActor();
    }

    @Override
    protected Set<Class<? extends AutonomousProcess<ShippingRole>>> getNecessaryAutonomousProcesses()
    {
        return necessaryAutonomousProcesses;
    }

    @Override
    protected Set<Class<? extends Content>> getNecessaryContentHandlers()
    {
        return necessaryContentHandlers;
    }
}
