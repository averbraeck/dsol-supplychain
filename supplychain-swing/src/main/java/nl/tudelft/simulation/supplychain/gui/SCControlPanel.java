package nl.tudelft.simulation.supplychain.gui;

import java.rmi.RemoteException;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.dsol.model.DSOLModel;
import nl.tudelft.simulation.dsol.swing.gui.control.RunUntilPanel;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainAnimator;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;

/**
 * DEVS Real Time ControlPanel for a djunits double timeunit.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SCControlPanel extends SCRealTimeControlPanel
{
    /** */
    private static final long serialVersionUID = 20221123;

    /**
     * Construct a real time control panel for a djunits double time unit, with a different set of control buttons. The control
     * panel assumes a DevsSimulator and animation. The model specification is not necessarily specified as "real time"; its
     * execution is.
     * @param model the model for the control panel, to allow a reset of the model
     * @param simulator the simulator. Specified separately, because the model can have
     *            been specified with a superclass of the simulator that the ControlPanel actually needs (e.g., model has been
     *            specified with a DevsAnimator, whereas the panel needs a RealTimeControlAnimator)
     * @throws RemoteException when simulator cannot be accessed for listener attachment
     */
    public SCControlPanel(final SupplyChainModelInterface model, final SupplyChainAnimator simulator)
            throws RemoteException
    {
        super(model, simulator);
        setClockPanel(new SCClockPanel(simulator));
        setSpeedPanel(new SCSpeedPanel(simulator));
        setRunUntilPanel(new RunUntilPanel.TimeDoubleUnit(simulator));
    }

}
