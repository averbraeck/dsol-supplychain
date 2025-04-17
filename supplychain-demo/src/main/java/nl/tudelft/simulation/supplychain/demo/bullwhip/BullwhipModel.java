package nl.tudelft.simulation.supplychain.demo.bullwhip;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.MassUnit;
import org.djunits.unit.VolumeUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Mass;
import org.djunits.value.vdouble.scalar.Volume;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;
import org.djutils.draw.point.Point2d;

import nl.tudelft.simulation.dsol.animation.d2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistDiscrete;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.distributions.DistNormal;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.supplychain.actor.Geography;
import nl.tudelft.simulation.supplychain.demo.DemoContentAnimator;
import nl.tudelft.simulation.supplychain.demo.reference.DemoBank;
import nl.tudelft.simulation.supplychain.demo.reference.DemoDirectory;
import nl.tudelft.simulation.supplychain.demo.reference.DemoManufacturer;
import nl.tudelft.simulation.supplychain.demo.reference.DemoMarket;
import nl.tudelft.simulation.supplychain.demo.reference.DemoRetailer;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModel;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.money.MoneyUnit;
import nl.tudelft.simulation.supplychain.product.BillOfMaterials;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.product.Sku;
import nl.tudelft.simulation.supplychain.reference.Bank;
import nl.tudelft.simulation.supplychain.role.consuming.process.DemandGeneratingProcess;
import nl.tudelft.simulation.supplychain.test.TestModel;
import nl.tudelft.simulation.supplychain.util.DistConstantDuration;
import nl.tudelft.simulation.supplychain.util.DistDiscreteTriangular;

/**
 * BullwhipModel.java. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class BullwhipModel extends SupplyChainModel
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * constructs a new BullwhipModel.
     * @param simulator the simulator
     */
    public BullwhipModel(final SupplyChainSimulatorInterface simulator)
    {
        super(simulator);
        // We don't do anything to prevent state-based replications.
    }

    @Override
    public void constructModel()
    {
        var devsSimulator = (SupplyChainSimulatorInterface) this.simulator;
        try
        {
            if (getSimulator() instanceof AnimatorInterface)
            {
                // First we create some background. We set the zValue to -Double.Min value to ensure that it is actually drawn
                // "below" our actors and messages.
                new SingleImageRenderable<>(new OrientedPoint3d(0.0, 0.0, 0.0), new Bounds3d(800, 600, 0), devsSimulator,
                        TestModel.class.getResource("/nl/tudelft/simulation/supplychain/demo/bullwhip/images/background.gif"));
            }

            // basics
            StreamInterface stream = new MersenneTwister();

            // Products and BOM
            Product keyboard = new Product(this, "keyboard", Sku.PIECE, new Money(15.0, MoneyUnit.USD),
                    new Mass(0.5, MassUnit.KILOGRAM), new Volume(50.0 * 15.0 * 2.0, VolumeUnit.CUBIC_CENTIMETER), 0.0);
            Product casing = new Product(this, "casing", Sku.PIECE, new Money(400.0, MoneyUnit.USD),
                    new Mass(10.0, MassUnit.KILOGRAM), new Volume(60.0 * 50.0 * 20.0, VolumeUnit.CUBIC_CENTIMETER), 0.02);
            Product mouse = new Product(this, "mouse", Sku.PIECE, new Money(10.0, MoneyUnit.USD),
                    new Mass(0.1, MassUnit.KILOGRAM), new Volume(10.0 * 5.0 * 4.0, VolumeUnit.CUBIC_CENTIMETER), 0.0);
            Product monitor = new Product(this, "monitor", Sku.PIECE, new Money(200.0, MoneyUnit.USD),
                    new Mass(5.0, MassUnit.KILOGRAM), new Volume(60.0 * 40.0 * 10.0, VolumeUnit.CUBIC_CENTIMETER), 0.01);
            Product pc = new Product(this, "PC", Sku.PIECE, new Money(1100.0, MoneyUnit.USD), new Mass(16.0, MassUnit.KILOGRAM),
                    new Volume(10.0 * 5.0 * 4.0, VolumeUnit.CUBIC_CENTIMETER), 0.02);
            BillOfMaterials pcBOM = new BillOfMaterials(pc);
            pcBOM.add(keyboard, 1.0);
            pcBOM.add(casing, 1.0);
            pcBOM.add(mouse, 1.0);
            pcBOM.add(monitor, 1.0);

            // create the bank
            Bank ing = new DemoBank("bank", "Bank", this, new Point2d(0, 0), "Bank", "Europe");
            ing.getBankingRole().setAnnualInterestRateNeg(-0.080);
            ing.getBankingRole().setAnnualInterestRatePos(0.025);

            // we create two search 'domains', one between the customers and the retailers,
            // and one between the retailers, manufacturers, and suppliers
            var ypCustomerMTS = new DemoDirectory("YP_customer_MTS", this, new Point2d(400, 40));
            var ypCustomerMTO = new DemoDirectory("YP_customer_MTO", this, new Point2d(500, 40));
            var ypProductionMTS = new DemoDirectory("YP_production_MTS", this, new Point2d(600, 40));
            var ypProductionMTO = new DemoDirectory("YP_production_MTO", this, new Point2d(700, 40));

            // CUSTOMER or MARKET
            var customer = new DemoMarket("US East", this, new Geography(new Point2d(40, 150), "", "US"), ing,
                    new Money(10000.0, MoneyUnit.USD), ypCustomerMTS);
            // Buy AINT(TRIA(2,5,10)) computers every EXPO(3) hour starting at t=0
            new DemandGeneratingProcess(customer.getConsumingRole(), pc)
                    .setIntervalDistribution(new DistContinuousDuration(new DistExponential(stream, 3.0), DurationUnit.HOUR))
                    .setAmountDistribution(new DistDiscreteTriangular(stream, 2.0, 5.0, 10.0))
                    .setEarliestDeliveryDuration(new Duration(1.0, DurationUnit.DAY))
                    .setLatestDeliveryDuration(new Duration(7.0, DurationUnit.DAY)).setStartAfterInterval().start();
            // Buy AINT(TRIA(2,4,5)) computers every EXPO(4) hour starting at t=0
            new DemandGeneratingProcess(customer.getConsumingRole(), pc)
                    .setIntervalDistribution(new DistContinuousDuration(new DistExponential(stream, 4.0), DurationUnit.HOUR))
                    .setAmountDistribution(new DistDiscreteTriangular(stream, 2.0, 4.0, 5.0))
                    .setEarliestDeliveryDuration(new Duration(1.0, DurationUnit.DAY))
                    .setLatestDeliveryDuration(new Duration(7.0, DurationUnit.DAY)).setStartAfterInterval().start();
            // Buy AINT(TRIA(5,8,12)) computers every EXPO(5) hour starting at t=0
            new DemandGeneratingProcess(customer.getConsumingRole(), pc)
                    .setIntervalDistribution(new DistContinuousDuration(new DistExponential(stream, 5.0), DurationUnit.HOUR))
                    .setAmountDistribution(new DistDiscreteTriangular(stream, 5.0, 8.0, 12.0))
                    .setEarliestDeliveryDuration(new Duration(1.0, DurationUnit.DAY))
                    .setLatestDeliveryDuration(new Duration(7.0, DurationUnit.DAY)).setStartAfterInterval().start();
            // Buy AINT(TRIA(3,8,10)) computers every EXPO(1) hour starting at t=504.0
            new DemandGeneratingProcess(customer.getConsumingRole(), pc)
                    .setIntervalDistribution(new DistContinuousDuration(new DistExponential(stream, 1.0), DurationUnit.HOUR))
                    .setAmountDistribution(new DistDiscreteTriangular(stream, 3.0, 8.0, 10.0))
                    .setEarliestDeliveryDuration(new Duration(1.0, DurationUnit.DAY))
                    .setLatestDeliveryDuration(new Duration(7.0, DurationUnit.DAY))
                    .setStartAfter(new Duration(504.0, DurationUnit.HOUR)).start();

            // Retailers
            StreamInterface streamMTS = new MersenneTwister(2L);
            StreamInterface streamMTO = new MersenneTwister(4L);
            DemoRetailer[] mtsRet = new DemoRetailer[5];
            mtsRet[0] = new DemoRetailer("Seattle_MTS", getSimulator(), new Point2d(-200, -270, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTS, ypProductionMTS, streamMTS, true);
            mtsRet[1] = new DemoRetailer("LosAngeles_MTS", getSimulator(), new Point2d(-200, -210, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTS, ypProductionMTS, streamMTS, true);
            mtsRet[2] = new DemoRetailer("NewYork_MTS", getSimulator(), new Point2d(-200, -150, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTS, ypProductionMTS, streamMTS, true);
            mtsRet[3] = new DemoRetailer("Washington_MTS", getSimulator(), new Point2d(-200, -90, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTS, ypProductionMTS, streamMTS, true);
            mtsRet[4] = new DemoRetailer("Miami_MTS", getSimulator(), new Point2d(-200, -30, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTS, ypProductionMTS, streamMTS, true);

            DemoRetailer[] mtoRet = new DemoRetailer[5];
            mtoRet[0] = new DemoRetailer("Seattle_MTO", getSimulator(), new Point2d(-200, 30, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTO, ypProductionMTO, streamMTO, false);
            mtoRet[1] = new DemoRetailer("LosAngeles_MTO", getSimulator(), new Point2d(-200, 90, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTO, ypProductionMTO, streamMTO, false);
            mtoRet[2] = new DemoRetailer("NewYork_MTO", getSimulator(), new Point2d(-200, 150, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTO, ypProductionMTO, streamMTO, false);
            mtoRet[3] = new DemoRetailer("Washington_MTO", getSimulator(), new Point2d(-200, 210, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTO, ypProductionMTO, streamMTO, false);
            mtoRet[4] = new DemoRetailer("Miami_MTO", getSimulator(), new Point2d(-200, 270, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTO, ypProductionMTO, streamMTO, false);

            // Manufacturers
            DemoManufacturer mtsMan = new DemoManufacturer("MexicoCity_MTS", getSimulator(), new Point2d(0, -150, 1), ing,
                    new Money(1000000, MoneyUnit.USD), pc, 50, ypCustomerMTS, ypProductionMTS, streamMTS, true);
            DemoManufacturer mtoMan = new DemoManufacturer("MexicoCity_MTO", getSimulator(), new Point2d(0, 150, 1), ing,
                    new Money(1000000, MoneyUnit.USD), pc, 50, ypCustomerMTO, ypProductionMTO, streamMTO, false);

            // Suppliers

            // Create the animation.
            DemoContentAnimator contentAnimator = new DemoContentAnimator(getSimulator());

            contentAnimator.subscribe(ypCustomerMTS);
            contentAnimator.subscribe(ypCustomerMTO);
            contentAnimator.subscribe(ypProductionMTS);
            contentAnimator.subscribe(ypProductionMTO);
            contentAnimator.subscribe(customer);
            // contentAnimator.subscribe(marketMTO);
            for (DemoRetailer r : mtsRet)
                contentAnimator.subscribe(r);
            for (DemoRetailer r : mtoRet)
                contentAnimator.subscribe(r);
            contentAnimator.subscribe(mtsMan);
            contentAnimator.subscribe(mtoMan);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
