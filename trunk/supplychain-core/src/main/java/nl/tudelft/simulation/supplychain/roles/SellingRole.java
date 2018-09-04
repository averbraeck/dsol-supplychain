package nl.tudelft.simulation.supplychain.roles;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.unit.simulator.DEVSSimulatorInterfaceUnit;

/**
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class SellingRole extends Role
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /**
     * Constructs a new SellingRole
     * @param owner the owner of the role
     * @param simulator the simulator to schedule on
     */
    public SellingRole(final SupplyChainActor owner, final DEVSSimulatorInterfaceUnit simulator)
    {
        super(owner, simulator);
        // add necessary handlers for this role
        super.addContentHandler(RequestForQuote.class, super.getOwner());
    }
}
