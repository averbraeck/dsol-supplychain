package nl.tudelft.simulation.supplychain.demo.mtsmto;

import java.awt.Dimension;

import javax.vecmath.Point3d;

import org.djunits.unit.MassUnit;
import org.djunits.unit.MoneyUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Mass;
import org.djunits.value.vdouble.scalar.Money;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.dsol.DSOLModel;
import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simtime.SimTimeDoubleUnit;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.language.d3.DirectedPoint;
import nl.tudelft.simulation.supplychain.animation.ContentAnimator;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.content.LeanContentStore;
import nl.tudelft.simulation.supplychain.product.BillOfMaterials;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.product.Unit;
import nl.tudelft.simulation.supplychain.roles.Role;
import nl.tudelft.simulation.supplychain.test.Client;
import nl.tudelft.simulation.supplychain.test.Factory;
import nl.tudelft.simulation.supplychain.test.PCShop;
import nl.tudelft.simulation.supplychain.test.TestModel;
import nl.tudelft.simulation.supplychain.transport.TransportMode;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;
import nl.tudelft.simulation.yellowpage.YellowPageInterface;

/**
 * MTSMTO.java. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class MTSMTOModel implements DSOLModel.TimeDoubleUnit
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the simulator. */
    private DEVSSimulatorInterface.TimeDoubleUnit devsSimulator;

    /**
     * constructs a new TestModel
     */
    public MTSMTOModel()
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
            this.devsSimulator = (DEVSSimulatorInterface.TimeDoubleUnit) simulator;
            if (this.devsSimulator instanceof AnimatorInterface)
            {
                // First we create some background. We set the zValue to -Double.Min value to ensure that it is actually drawn
                // "below" our actors and messages.
                new SingleImageRenderable(new DirectedPoint(0.0, 0.0, -Double.MIN_VALUE), new Dimension(800, 600),
                        this.devsSimulator,
                        TestModel.class.getResource("/nl/tudelft/simulation/supplychain/demo/mtsmto/images/background.gif"));
            }

            // basics
            StreamInterface streamMTS = new MersenneTwister();
            StreamInterface streamMTO = new MersenneTwister();

            // Products and BOM
            Product keyboard =
                    new Product("keyboard", Unit.PIECE, new Money(15.0, MoneyUnit.USD), new Mass(0.5, MassUnit.KILOGRAM), 0.0);
            Product casing =
                    new Product("casing", Unit.PIECE, new Money(400.0, MoneyUnit.USD), new Mass(10.0, MassUnit.KILOGRAM), 0.02);
            Product mouse =
                    new Product("mouse", Unit.PIECE, new Money(10.0, MoneyUnit.USD), new Mass(0.1, MassUnit.KILOGRAM), 0.0);
            Product monitor =
                    new Product("monitor", Unit.PIECE, new Money(200.0, MoneyUnit.USD), new Mass(5.0, MassUnit.KILOGRAM), 0.01);
            Product pc =
                    new Product("PC", Unit.PIECE, new Money(1100.0, MoneyUnit.USD), new Mass(16.0, MassUnit.KILOGRAM), 0.02);
            BillOfMaterials pcBOM = new BillOfMaterials(pc);
            pcBOM.add(keyboard, 1.0);
            pcBOM.add(casing, 1.0);
            pcBOM.add(mouse, 1.0);
            pcBOM.add(monitor, 1.0);

            // create the bank
            Bank ing = new Bank("ING", this.devsSimulator, new Point3d(0, 0, 0));
            ing.setAnnualInterestRateNeg(0.080);
            ing.setAnnualInterestRatePos(0.025);

            
            
            
            
            
            
            


            // Create the animation.
            ContentAnimator contentAnimator = new ContentAnimator(this.devsSimulator);
            // contentAnimator.subscribe(Factory);
            // contentAnimator.subscribe(pcShop);
            // contentAnimator.subscribe(Client);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /** {@inheritDoc} */
    @Override
    public SimulatorInterface<Time, Duration, SimTimeDoubleUnit> getSimulator()
    {
        return this.devsSimulator;
    }

}
