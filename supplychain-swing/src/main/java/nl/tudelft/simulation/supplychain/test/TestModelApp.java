package nl.tudelft.simulation.supplychain.test;

import java.rmi.RemoteException;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.logger.CategoryLogger;
import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulator;

/**
 * Test application of Supply Chain test model.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TestModelApp implements EventListener
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the model. */
    private TestModel model;
    
    /** Make and start a simulation. */
    public TestModelApp()
    {
        var simulator = new SupplyChainSimulator("MTSMTO", Time.ZERO);
        this.model = new TestModel(simulator);
        subscribeToMessages();
        Replication<Duration> replication =
                new SingleReplication<Duration>("rep1", Duration.ZERO, Duration.ZERO, new Duration(1800.0, DurationUnit.HOUR));
        simulator.initialize(this.model, replication);
        simulator.start();
    }

    /**
     * Subscribe to messages and print them.
     */
    private void subscribeToMessages()
    {
        this.model.client.addListener(this, Actor.SEND_CONTENT_EVENT);
        this.model.factory.addListener(this, Actor.SEND_CONTENT_EVENT);
        this.model.pcShop.addListener(this, Actor.SEND_CONTENT_EVENT);
        this.model.bank.addListener(this, Actor.SEND_CONTENT_EVENT);
    }
    
    /**
     * Test application of Supply Chain test model.
     * @param args not used
     * @throws Exception on dsol error
     */
    public static void main(final String[] args) throws Exception
    {
        CategoryLogger.setAllLogLevel(Level.INFO);
        CategoryLogger.setAllLogMessageFormat("{level} - {class_name}.{method}:{line}  {message}");
        new TestModelApp();
    }

    @Override
    public void notify(final Event event) throws RemoteException
    {
    }

}
