package nl.tudelft.simulation.supplychain.roles;

import java.io.Serializable;

import nl.tudelft.simulation.actor.InternalActor;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;

/**
 * A Role is a bundled set of Handlers that belong together. A Role takes care of the "consistent" implementation of a number of
 * handlers. The typical usage is as follows:
 * 
 * <pre>
 * / Customer customer = new Customer(name, simulator, position, bank);
 * / InternalDemandHandler demandHandler = new InternalDemandHandler(customer, args);
 * / OrderConfirmationHandler confirmationHandler = new OrderConfirmationHandler(customer, args);
 * / ShipmentHandler shipmentHandler = new ShipmentHandler(customer, args);
 * / BillHandler billHandler = new BillHandler(customer, args);
 * / BuyingRole buyingRole = new BuyingRole(customer, simulator, demandHandler, confirmationHandler, shipmentHandler, billHandler);
 * </pre>
 * 
 * Note: customer.addRole(buyingRole); will be executed automatically!<br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class Role extends InternalActor
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the owner of the role */
    protected SupplyChainActor owner = null;

    /** the default stream to use for the time delays */
    protected StreamInterface stream = null;

    /**
     * Constructs a new Role
     * @param owner the owner of this role
     * @param name the role name
     * @param simulator the simulator to schedule on
     */
    public Role(final SupplyChainActor owner, final String name, final DEVSSimulatorInterface.TimeDoubleUnit simulator)
    {
        super(name, simulator);
        this.owner = owner;
        this.simulator = simulator;
        this.stream = this.simulator.getReplication().getStream("default");
        
        // register the role with the owner
        owner.addRole(this);
    }

    /**
     * @return Returns the owner.
     */
    public SupplyChainActor getOwner()
    {
        return this.owner;
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(Serializable content)
    {
        return super.handleContent(content, false);
    }
    
    
}
