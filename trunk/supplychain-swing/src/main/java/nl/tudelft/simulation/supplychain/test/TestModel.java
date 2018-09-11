package nl.tudelft.simulation.supplychain.test;

import java.awt.Dimension;
import java.io.Serializable;

import javax.vecmath.Point3d;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.MassUnit;
import org.djunits.unit.MoneyUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Mass;
import org.djunits.value.vdouble.scalar.Money;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.dsol.DSOLModel;
import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simtime.SimTimeDoubleUnit;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.language.d3.DirectedPoint;
import nl.tudelft.simulation.supplychain.animation.ContentAnimator;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.content.LeanContentStore;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.product.Unit;
import nl.tudelft.simulation.supplychain.roles.Role;

/**
 * The TestModel for the supplychain package. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class TestModel implements DSOLModel.TimeDoubleUnit
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** timing run-time */
    private long startTimeMs = 0;

    /** the simulator. */
    private DEVSSimulatorInterface.TimeDoubleUnit devsSimulator;

    /**
     * constructs a new TestModel
     */
    public TestModel()
    {
        super();
        // We don't do anything to prevent state-based replications.
    }

    /** {@inheritDoc} */
    @Override
    public void constructModel(final SimulatorInterface<Time, Duration, SimTimeDoubleUnit> simulator)
    {
        try
        {
            this.startTimeMs = System.currentTimeMillis();
            this.devsSimulator = (DEVSSimulatorInterface.TimeDoubleUnit) simulator;
            if (this.devsSimulator instanceof AnimatorInterface)
            {
                // First we create some background. We set the zValue to -Double.Min value to ensure that it is actually drawn
                // "below" our actors and messages.
                new SingleImageRenderable(new DirectedPoint(0.0, 0.0, -Double.MIN_VALUE), new Dimension(1618, 716),
                        this.devsSimulator,
                        TestModel.class.getResource("/nl/tudelft/simulation/supplychain/images/worldmap.gif"));
            }

            // create the bank
            Bank ing = new Bank("ING", this.devsSimulator, new Point3d(0, 0, 0));
            ing.setAnnualInterestRateNeg(0.080);
            ing.setAnnualInterestRatePos(0.025);

            // create a product
            Product laptop =
                    new Product("Laptop", Unit.PIECE, new Money(1400.0, MoneyUnit.USD), new Mass(6.5, MassUnit.KILOGRAM), 0.0);

            // create a manufacturer
            Factory Factory = new Factory("Factory", this.devsSimulator, new Point3d(200, 200, 0), new Role[] {}, ing,
                    new Money(50000.0, MoneyUnit.USD), laptop, 1000);
            Factory.setContentStore(new LeanContentStore(Factory, this.devsSimulator));

            // create a retailer
            PCShop pcShop = new PCShop("PCshop", this.devsSimulator, new Point3d(20, 200, 0), new Role[] {}, ing,
                    new Money(50000.0, MoneyUnit.USD), laptop, 10, Factory);
            pcShop.setContentStore(new LeanContentStore(pcShop, this.devsSimulator));

            // create a customer
            Client Client = new Client("Client", this.devsSimulator, new Point3d(100, 100, 0), ing,
                    new Money(1500000.0, MoneyUnit.USD), laptop, pcShop);
            Client.setContentStore(new LeanContentStore(Client, this.devsSimulator));

            // schedule a remark that the simulation is ready
            Duration endTime = new Duration(simulator.getReplication().getTreatment().getRunLength().doubleValue() - 0.001,
                    DurationUnit.SI);
            this.devsSimulator.scheduleEventRel(endTime, this, this, "endSimulation", new Serializable[] {});

            // Create the animation.
            ContentAnimator contentAnimator = new ContentAnimator(this.devsSimulator);
            contentAnimator.subscribe(Factory);
            contentAnimator.subscribe(pcShop);
            contentAnimator.subscribe(Client);
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
        System.err.println("Simulation time = " + this.devsSimulator.getSimulatorTime());
    }

    /** {@inheritDoc} */
    @Override
    public SimulatorInterface<Time, Duration, SimTimeDoubleUnit> getSimulator()
    {
        return this.devsSimulator;
    }

}