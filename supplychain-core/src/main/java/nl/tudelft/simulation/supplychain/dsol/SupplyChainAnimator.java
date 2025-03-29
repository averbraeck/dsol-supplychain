package nl.tudelft.simulation.supplychain.dsol;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.dsol.simulators.DevsRealTimeAnimator;
import nl.tudelft.simulation.naming.context.ContextInterface;

/**
 * SupplyChainAnimator extends the DevsRealTimeAnimator.TimeDoubleUnit, and offers absolute Time for the simulation time in
 * addition to relative duration.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SupplyChainAnimator extends DevsRealTimeAnimator.TimeDoubleUnit implements SupplyChainSimulatorInterface
{
    /** */
    private static final long serialVersionUID = 20221201L;

    /** the start time of the simulator. */
    private final Time absStartTime;

    /**
     * Construct the SupplyChainAnimator that extends the DevsRealTimeAnimator.TimeDoubleUnit, and offers absolute Time for the
     * simulation time in addition to relative duration.
     * @param id the simulator id
     * @param absStartTime the start time of the simulator
     */
    public SupplyChainAnimator(final Serializable id, final Time absStartTime)
    {
        super(id);
        this.absStartTime = absStartTime;
    }

    @Override
    public ContextInterface getContext()
    {
        return getReplication().getContext();
    }

    @Override
    public Time getAbsStartTime()
    {
        return this.absStartTime;
    }

    @Override
    public SupplyChainModelInterface getModel()
    {
        return (SupplyChainModelInterface) super.getModel();
    }

}
