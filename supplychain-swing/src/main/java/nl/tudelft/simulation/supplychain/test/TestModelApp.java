package nl.tudelft.simulation.supplychain.test;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.logger.CategoryLogger;
import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulator;

/**
 * Test application of Supply Chain test model.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TestModelApp
{
    /** the model. */
    private TestModel model;

    /** Make and start a simulation. */
    public TestModelApp()
    {
        var simulator = new SupplyChainSimulator("MTSMTO", Time.ZERO);
        this.model = new TestModel(simulator);
        Replication<Duration> replication =
                new SingleReplication<Duration>("rep1", Duration.ZERO, Duration.ZERO, new Duration(1800.0, DurationUnit.HOUR));
        simulator.initialize(this.model, replication);
        simulator.start();
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

}
