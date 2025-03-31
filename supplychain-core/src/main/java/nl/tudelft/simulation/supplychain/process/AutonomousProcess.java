package nl.tudelft.simulation.supplychain.process;

import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;

/**
 * An AutonomousProcess belong to a role, and carries out actions that do not relate to a received message. Examples are
 * consumption, calculating interest, or calculating depriciation of a product in inventory.
 * <p>
 * Copyright (c) 2023-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface AutonomousProcess
{
    /**
     * Initialize the processing at the start of the simulation. This method needs to schedule the autonomous activities that
     * need to take place, one-time or repeatedly.
     * @param simulator the simulator to schedule the autonomous processes
     */
    void init(SupplyChainSimulatorInterface simulator);
    
}
