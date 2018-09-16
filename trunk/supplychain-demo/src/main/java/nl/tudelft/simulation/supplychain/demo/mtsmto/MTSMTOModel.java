package nl.tudelft.simulation.supplychain.demo.mtsmto;

import java.awt.Dimension;

import javax.vecmath.Point3d;

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
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.language.d3.DirectedPoint;
import nl.tudelft.simulation.supplychain.animation.ContentAnimator;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.demo.reference.DemoManufacturer;
import nl.tudelft.simulation.supplychain.demo.reference.DemoMarket;
import nl.tudelft.simulation.supplychain.demo.reference.DemoRetailer;
import nl.tudelft.simulation.supplychain.demo.reference.DemoSupplier;
import nl.tudelft.simulation.supplychain.demo.reference.DemoYP;
import nl.tudelft.simulation.supplychain.product.BillOfMaterials;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.product.Unit;
import nl.tudelft.simulation.supplychain.test.TestModel;

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

            // we create two yellow page 'domains', one between the customers and the retailers,
            // and one between the retailers, manufacturers, and suppliers
            DemoYP ypCustomerMTS = new DemoYP("YP_customer_MTS", this.devsSimulator, new Point3d(-300, -270, 1), ing);
            DemoYP ypCustomerMTO = new DemoYP("YP_customer_MTO", this.devsSimulator, new Point3d(-300, 30, 1), ing);
            DemoYP ypProductionMTS = new DemoYP("YP_production_MTS", this.devsSimulator, new Point3d(100, -270, 1), ing);
            DemoYP ypProductionMTO = new DemoYP("YP_production_MTO", this.devsSimulator, new Point3d(100, 30, 1), ing);

            // Markets
            DemoMarket marketMTS = new DemoMarket("Market_MTS", this.devsSimulator, new Point3d(-360, -150, 1), ing,
                    new Money(10000.0, MoneyUnit.USD), pc, ypCustomerMTS, streamMTS);
            DemoMarket marketMTO = new DemoMarket("Market_MTO", this.devsSimulator, new Point3d(-360, 150, 1), ing,
                    new Money(10000.0, MoneyUnit.USD), pc, ypCustomerMTO, streamMTO);

            // Retailers
            DemoRetailer mtsRet1 = new DemoRetailer("Seattle_MTS", this.devsSimulator, new Point3d(-200, -270, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTS, ypProductionMTS, streamMTS, true);
            DemoRetailer mtsRet2 = new DemoRetailer("LosAngeles_MTS", this.devsSimulator, new Point3d(-200, -210, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTS, ypProductionMTS, streamMTS, true);
            DemoRetailer mtsRet3 = new DemoRetailer("NewYork_MTS", this.devsSimulator, new Point3d(-200, -150, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTS, ypProductionMTS, streamMTS, true);
            DemoRetailer mtsRet4 = new DemoRetailer("Washington_MTS", this.devsSimulator, new Point3d(-200, -90, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTS, ypProductionMTS, streamMTS, true);
            DemoRetailer mtsRet5 = new DemoRetailer("Miami_MTS", this.devsSimulator, new Point3d(-200, -30, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTS, ypProductionMTS, streamMTS, true);

            DemoRetailer mtoRet1 = new DemoRetailer("Seattle_MTO", this.devsSimulator, new Point3d(-200, 30, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTO, ypProductionMTO, streamMTO, false);
            DemoRetailer mtoRet2 = new DemoRetailer("LosAngeles_MTO", this.devsSimulator, new Point3d(-200, 90, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTO, ypProductionMTO, streamMTO, false);
            DemoRetailer mtoRet3 = new DemoRetailer("NewYork_MTO", this.devsSimulator, new Point3d(-200, 150, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTO, ypProductionMTO, streamMTO, false);
            DemoRetailer mtoRet4 = new DemoRetailer("Washington_MTO", this.devsSimulator, new Point3d(-200, 210, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTO, ypProductionMTO, streamMTO, false);
            DemoRetailer mtoRet5 = new DemoRetailer("Miami_MTO", this.devsSimulator, new Point3d(-200, 270, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTO, ypProductionMTO, streamMTO, false);

            // Manufacturers
            DemoManufacturer mtsMan = new DemoManufacturer("MexicoCity_MTS", this.devsSimulator, new Point3d(0, -150, 1), ing,
                    new Money(1000000, MoneyUnit.USD), pc, 50, ypCustomerMTS, ypProductionMTS, streamMTS, true);
            DemoManufacturer mtoMan = new DemoManufacturer("MexicoCity_MTO", this.devsSimulator, new Point3d(0, 150, 1), ing,
                    new Money(1000000, MoneyUnit.USD), pc, 50, ypCustomerMTO, ypProductionMTO, streamMTO, false);

            // Suppliers

            // Create the animation.
            ContentAnimator contentAnimator = new ContentAnimator(this.devsSimulator);

            contentAnimator.subscribe(ypCustomerMTS);
            contentAnimator.subscribe(ypCustomerMTO);
            contentAnimator.subscribe(ypProductionMTS);
            contentAnimator.subscribe(ypProductionMTO);
            contentAnimator.subscribe(marketMTS);
            contentAnimator.subscribe(marketMTO);
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
