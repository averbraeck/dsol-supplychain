package nl.tudelft.simulation.supplychain.gui;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.actor.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.dsol.swing.gui.control.SpeedPanel;

/**
 * SCSpeedPanel takes 1 hour (3600 seconds) as "1".
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SCSpeedPanel extends SpeedPanel.TimeDoubleUnit
{
    /** */
    private static final long serialVersionUID = 20221123L;

    /**
     * Construct a clock panel with a djutils Duration.
     * @param simulator SimulatorInterface; the simulator
     */
    public SCSpeedPanel(final SCSimulatorInterface simulator)
    {
        super(simulator);
    }

    /** {@inheritDoc} */
    @Override
    protected String formatSpeed(final Duration simulationTime)
    {
        if (simulationTime == null)
        {
            return "0.0";
        }
        double speed = (simulationTime.si - getPrevSimTime().si) / (3600.0 * (0.001 * getUpdateIntervalMs()));
        setPrevSimTime(simulationTime);
        return String.format("%6.2f x ", speed);
    }

}
