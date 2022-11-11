package nl.tudelft.simulation.supplychain.test;

import java.awt.Dimension;
import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.MassUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Mass;
import org.djutils.draw.point.OrientedPoint3d;
import org.djutils.draw.point.Point3d;

import nl.tudelft.simulation.actor.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.model.AbstractDSOLModel;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.contentstore.database.CachingDatabaseWorker;
import nl.tudelft.simulation.supplychain.contentstore.database.CentralDatabaseContentStore;
import nl.tudelft.simulation.supplychain.contentstore.database.DatabaseWorkerInterface;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.product.Unit;

/**
 * The TestModel for the supplychain package. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class TestModelCentralDatabase extends AbstractDSOLModel<Duration, SCSimulatorInterface><SCSimulatorInterface>
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** timing run-time */
    private long startTimeMs = 0;

    /** the simulator. */
    private SCSimulatorInterface devsSimulator;

    /**
     * constructs a new TestModel
     * @param simulator the simulator
     */
    public TestModelCentralDatabase(final SCSimulatorInterface simulator)
    {
        super(simulator);
        // We don't do anything to prevent state-based replications.
    }

    /** {@inheritDoc} */
    @Override
    public void constructModel()
    {
        try
        {
            this.startTimeMs = System.currentTimeMillis();
            this.devsSimulator = (SCSimulatorInterface) simulator;
            if (this.devsSimulator instanceof AnimatorInterface)
            {
                // First we create some background. We set the zValue to
                // -Double.Min
                // value to ensure that it is actually drawn "below" our actors and
                // messages.
                new SingleImageRenderable<>(new OrientedPoint3d(0.0, 0.0, -Double.MIN_VALUE), new Dimension(1618, 716),
                    this.devsSimulator, TestModel.class.getResource(
                        "/nl/tudelft/simulation/supplychain/images/worldmap.gif"));
            }
            // create the bank
            Bank ing = new Bank("ING", this.devsSimulator, new Point3d(0, 0, 0));
            ing.setAnnualInterestRateNeg(0.080);
            ing.setAnnualInterestRatePos(0.025);
            // create a product
            Product laptop = new Product("Laptop", Unit.PIECE, new Money(1400.0, MoneyUnit.USD), new Mass(6.5,
                MassUnit.KILOGRAM), 0.0);

            // create a ContentStore
            DatabaseWorkerInterface dbw = new CachingDatabaseWorker("TestModel_simulation");

            // create a manufacturer
            Factory Factory = new Factory("Factory", this.devsSimulator, new Point3d(200, 200, 0), ing, new Money(50000.0,
                MoneyUnit.USD), laptop, 1000, new CentralDatabaseContentStore(dbw));

            // create a retailer
            PCShop pcShop = new PCShop("PCshop", this.devsSimulator, new Point3d(20, 200, 0), ing, new Money(50000.0,
                MoneyUnit.USD), laptop, 10, Factory, new CentralDatabaseContentStore(dbw));

            // create a customer
            Client Client = new Client("Client", this.devsSimulator, new Point3d(100, 100, 0), ing, new Money(1500000.0,
                MoneyUnit.USD), laptop, pcShop, new CentralDatabaseContentStore(dbw));

            // schedule a remark that the simulation is ready
            Duration endTime = new Duration(simulator.getReplication().getTreatment().getRunLength().doubleValue() - 0.001,
                DurationUnit.SI);
            this.devsSimulator.scheduleEventRel(endTime, this, this, "endSimulation", new Serializable[] {});
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * end of simulation -- display a message
     */
    protected void endSimulation()
    {
        System.err.println("End of TestModel replication");
        System.err.println("Runtime = " + ((System.currentTimeMillis() - this.startTimeMs) / 1000) + " seconds.");
        System.err.println("cacheHitsAdd = " + CachingDatabaseWorker.cacheHitsAdd);
        System.err.println("cacheMissesAdd = " + CachingDatabaseWorker.cacheMissesAdd);
        System.err.println("cacheHitsGet = " + CachingDatabaseWorker.cacheHitsGet);
        System.err.println("cacheMissesget = " + CachingDatabaseWorker.cacheMissesGet);
        System.err.println("Simulation time = " + this.devsSimulator.getSimulatorTime());
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return "TestModelCentralDatabase";
    }

}
