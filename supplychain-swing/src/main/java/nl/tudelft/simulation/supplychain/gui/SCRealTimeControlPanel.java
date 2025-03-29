package nl.tudelft.simulation.supplychain.gui;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;

import javax.swing.JButton;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.event.Event;

import nl.tudelft.simulation.dsol.simulators.DevsRealTimeAnimator;
import nl.tudelft.simulation.dsol.simulators.RunState;
import nl.tudelft.simulation.dsol.swing.gui.control.DEVSControlPanel;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainAnimator;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;

/**
 * SCRealTimeControlPanel.java.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SCRealTimeControlPanel extends DEVSControlPanel<Duration, SupplyChainAnimator> implements PropertyChangeListener
{
    /** */
    private static final long serialVersionUID = 20201227L;

    /** The timeWarpPanel to control the speed. */
    private final SCRunSpeedSliderPanel runSpeedSliderPanel;

    /** The default animation delay (stored during fast forward). */
    private long savedAnimationDelay = 100L;

    /**
     * Generic control panel with a different set of control buttons. The control panel assumes a RealTimeDevsAnimator and
     * animation, but the model specification is not necessarily specified as "real time"; its execution is.
     * @param model DSOLModel&lt;T, ? extends DevsSimulationInterface&lt;T&gt;&gt;; the model for the control panel, to allow a
     *            reset of the model
     * @param simulator S; the simulator. Specified separately, because the model can have been specified with a superclass of
     *            the simulator that the ControlPanel actually needs (e.g., model has been specified with a DevsAnimator,
     *            whereas the panel needs a RealTimeControlAnimator)
     * @throws RemoteException when simulator cannot be accessed for listener attachment
     */
    public SCRealTimeControlPanel(final SupplyChainModelInterface model, final SupplyChainAnimator simulator)
            throws RemoteException
    {
        super(model, simulator);

        getControlButtonsPanel().add(makeButton("fastForwardButton", "/resources/FastForward.png", "FastForward",
                "Run the simulation as fast as possible", true));

        this.runSpeedSliderPanel = new SCRunSpeedSliderPanel(0.1, 1000, 1, 3600.0, 3, getSimulator());
        add(this.runSpeedSliderPanel);

        getSimulator().addListener(this, DevsRealTimeAnimator.CHANGE_SPEED_FACTOR_EVENT);
    }

    @Override
    public void actionPerformed(final ActionEvent actionEvent)
    {
        String actionCommand = actionEvent.getActionCommand();
        try
        {
            if (actionCommand.equals("FastForward"))
            {
                if (getSimulator().isStoppingOrStopped())
                {
                    this.savedAnimationDelay = getSimulator().getAnimationDelay();
                    getSimulator().setAnimationDelay(0L);
                    getSimulator().setUpdateMsec(1000);
                    getSimulator().setAnimationDelay(500); // 2 Hz
                    getSimulator().start();
                }
            }
            if (actionCommand.equals("RunPause") || actionCommand.equals("Reset"))
            {
                getSimulator().setAnimationDelay(this.savedAnimationDelay);
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        super.actionPerformed(actionEvent); // includes fixButtons()
    }

    @Override
    protected void fixButtons()
    {
        final boolean moreWorkToDo = getSimulator().getRunState() != RunState.ENDED;
        for (JButton button : getControlButtons())
        {
            final String actionCommand = button.getActionCommand();
            if (actionCommand.equals("FastForward"))
            {
                button.setEnabled(moreWorkToDo && isControlButtonsEnabled() && getSimulator().isStoppingOrStopped());
            }
        }
        super.fixButtons(); // handles the start/stop button
    }

    @Override
    protected void invalidateButtons()
    {
        for (JButton button : getControlButtons())
        {
            final String actionCommand = button.getActionCommand();
            if (actionCommand.equals("FastForward"))
            {
                button.setEnabled(false);
            }
        }
        super.invalidateButtons(); // handles the start/stop button
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt)
    {
        // TODO: when external change on speed -- update the slider panel
    }

    @Override
    public void notify(final Event event) throws RemoteException
    {
        if (event.getType().equals(DevsRealTimeAnimator.CHANGE_SPEED_FACTOR_EVENT))
        {
            this.runSpeedSliderPanel.setSpeed((Double) event.getContent());
            fixButtons();
        }
        super.notify(event);
    }

}
