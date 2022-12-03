package nl.tudelft.simulation.supplychain.gui;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.dsol.swing.gui.control.ClockPanel;
import nl.tudelft.simulation.supplychain.dsol.SCAnimator;

/**
 * SCClockPanel for a djunits Time.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SCClockPanel extends ClockPanel.TimeDoubleUnit
{
    /** */
    private static final long serialVersionUID = 20221123L;

    /**
     * Construct a clock panel with a double time with a unit.
     * @param simulator SCSimulatorInterface; the simulator
     */
    public SCClockPanel(final SCAnimator simulator)
    {
        super(simulator);
    }

    /** {@inheritDoc} */
    @Override
    protected String formatSimulationTime(final Duration simulationTime)
    {
        return simulationTime.toString(DurationUnit.HOUR, false, true);
    }

}
