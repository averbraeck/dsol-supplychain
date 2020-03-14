package nl.tudelft.simulation.supplychain.risk.handler;

import org.djutils.event.EventListenerInterface;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.policy.SupplyChainHandler;

/**
 * AbstractRiskAwareHandler.java. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class AbstractRiskAwareHandler extends SupplyChainHandler implements EventListenerInterface
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param owner the owner of this handler
     */
    public AbstractRiskAwareHandler(final SupplyChainActor owner)
    {
        super(owner);
    }


}

