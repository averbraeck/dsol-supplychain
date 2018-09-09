package nl.tudelft.simulation.supplychain.roles;

import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;

/**
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class ProducingRole extends Role
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /**
     * @param owner the actor of this role
     * @param simulator the simulator to schedule on
     */
    public ProducingRole(final SupplyChainActor owner, final DEVSSimulatorInterface.TimeDoubleUnit simulator)
    {
        super(owner, simulator);
    }
}
