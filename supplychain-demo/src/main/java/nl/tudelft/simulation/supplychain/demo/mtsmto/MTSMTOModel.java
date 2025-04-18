package nl.tudelft.simulation.supplychain.demo.mtsmto;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.LengthUnit;
import org.djunits.unit.MassUnit;
import org.djunits.unit.VolumeUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Mass;
import org.djunits.value.vdouble.scalar.Volume;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;
import org.djutils.draw.point.Point;
import org.djutils.draw.point.Point2d;

import nl.tudelft.simulation.dsol.animation.d2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
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
import nl.tudelft.simulation.supplychain.demo.reference.DemoTransporter;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainAnimator;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModel;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.money.MoneyUnit;
import nl.tudelft.simulation.supplychain.product.BillOfMaterials;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.product.Sku;
import nl.tudelft.simulation.supplychain.reference.Bank;
import nl.tudelft.simulation.supplychain.role.consuming.process.DemandGeneratingProcess;
import nl.tudelft.simulation.supplychain.role.transporting.TransportingActor;
import nl.tudelft.simulation.supplychain.test.TestModel;
import nl.tudelft.simulation.supplychain.util.DistDiscreteTriangular;

/**
 * MTSMTOModel.java.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MTSMTOModel extends SupplyChainModel
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * constructs a new MTSMTOModel.
     * @param simulator the simulator
     */
    public MTSMTOModel(final SupplyChainAnimator simulator)
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
                new SingleImageRenderable<>(new OrientedPoint3d(0.0, 0.0, -Double.MIN_VALUE), new Bounds3d(800, 600, 0),
                        devsSimulator,
                        TestModel.class.getResource("/nl/tudelft/simulation/supplychain/demo/mtsmto/images/background.gif"));
            }

            // basics
            StreamInterface streamMTS = new MersenneTwister();
            StreamInterface streamMTO = new MersenneTwister();

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

            // create the transporter
            var trucking = new DemoTransporter("Trucker", "Trucker", this, new Point2d(250, 0), "continent", "US", ing,
                    new Money(1E5, MoneyUnit.USD));
            var transporters = new TransportingActor[] {trucking};

            // we create two search 'domains', one between the customers and the retailers,
            // and one between the retailers, manufacturers, and suppliers
            var ypCustomerMTS = new DemoDirectory("YP_customer_MTS", this, new Point2d(-300, -270));
            var ypCustomerMTO = new DemoDirectory("YP_customer_MTO", this, new Point2d(-300, 30));
            var ypProductionMTS = new DemoDirectory("YP_production_MTS", this, new Point2d(100, -270));
            var ypProductionMTO = new DemoDirectory("YP_production_MTO", this, new Point2d(100, 30));

            // Markets
            var marketMTS = new DemoMarket("Market_MTS", this, new Geography(new Point2d(-360, -150), "", "MTS"), ing,
                    new Money(10000.0, MoneyUnit.USD), ypCustomerMTS);
            new DemandGeneratingProcess(marketMTS, pc)
                    .setIntervalDistribution(new DistContinuousDuration(new DistExponential(streamMTS, 8.0), DurationUnit.HOUR))
                    .setAmountDistribution(new DistDiscreteTriangular(streamMTS, 1.0, 4.0, 8.0))
                    .setEarliestDeliveryDuration(new Duration(2.0, DurationUnit.DAY))
                    .setLatestDeliveryDuration(new Duration(7.0, DurationUnit.DAY)).setStartAfterInterval().start();
            var marketMTO = new DemoMarket("Market_MTO", this, new Geography(new Point2d(-360, 150), "", "MTO"), ing,
                    new Money(10000.0, MoneyUnit.USD), ypCustomerMTO);
            new DemandGeneratingProcess(marketMTO, pc)
                    .setIntervalDistribution(new DistContinuousDuration(new DistExponential(streamMTO, 8.0), DurationUnit.HOUR))
                    .setAmountDistribution(new DistDiscreteTriangular(streamMTO, 1.0, 4.0, 8.0))
                    .setEarliestDeliveryDuration(new Duration(2.0, DurationUnit.DAY))
                    .setLatestDeliveryDuration(new Duration(7.0, DurationUnit.DAY)).setStartAfterInterval().start();

            // Retailers
            DemoRetailer[] mtsRet = new DemoRetailer[5];
            mtsRet[0] = new DemoRetailer("Seattle_MTS", this, new Geography(new Point2d(-200, -270), "Seattle", "US"), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTS, ypProductionMTS, streamMTS, true, transporters);
            mtsRet[1] = new DemoRetailer("LosAngeles_MTS", this, new Geography(new Point2d(-200, -210), "Los Angeles", "US"),
                    ing, new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTS, ypProductionMTS, streamMTS, true,
                    transporters);
            mtsRet[2] = new DemoRetailer("NewYork_MTS", this, new Geography(new Point2d(-200, -150), "New York", "US"), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTS, ypProductionMTS, streamMTS, true, transporters);
            mtsRet[3] = new DemoRetailer("Washington_MTS", this, new Geography(new Point2d(-200, -90), "Washington", "US"), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTS, ypProductionMTS, streamMTS, true, transporters);
            mtsRet[4] = new DemoRetailer("Miami_MTS", this, new Geography(new Point2d(-200, -30), "Miami", "US"), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTS, ypProductionMTS, streamMTS, true, transporters);

            DemoRetailer[] mtoRet = new DemoRetailer[5];
            mtoRet[0] = new DemoRetailer("Seattle_MTO", this, new Geography(new Point2d(-200, 30), "Seattle", "US"), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTO, ypProductionMTO, streamMTO, false, transporters);
            mtoRet[1] = new DemoRetailer("LosAngeles_MTO", this, new Geography(new Point2d(-200, 90), "Los Angeles", "US"), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTO, ypProductionMTO, streamMTO, false, transporters);
            mtoRet[2] = new DemoRetailer("NewYork_MTO", this, new Geography(new Point2d(-200, 150), "New York", "US"), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTO, ypProductionMTO, streamMTO, false, transporters);
            mtoRet[3] = new DemoRetailer("Washington_MTO", this, new Geography(new Point2d(-200, 210), "Washington", "US"), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTO, ypProductionMTO, streamMTO, false, transporters);
            mtoRet[4] = new DemoRetailer("Miami_MTO", this, new Geography(new Point2d(-200, 270), "Miami", "US"), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTO, ypProductionMTO, streamMTO, false, transporters);

            // Manufacturers
            DemoManufacturer mtsMan = new DemoManufacturer("MexicoCity_MTS", this,
                    new Geography(new Point2d(0, -150), "Mexico City", "US"), ing, new Money(1000000, MoneyUnit.USD), pc, 50,
                    ypCustomerMTS, ypProductionMTS, streamMTS, true, transporters);
            DemoManufacturer mtoMan = new DemoManufacturer("MexicoCity_MTO", this,
                    new Geography(new Point2d(0, 150), "Nexico City", "US"), ing, new Money(1000000, MoneyUnit.USD), pc, 50,
                    ypCustomerMTO, ypProductionMTO, streamMTO, false, transporters);

            // Suppliers

            // Create the animation.
            DemoContentAnimator contentAnimator = new DemoContentAnimator(getSimulator());

            contentAnimator.subscribe(ypCustomerMTS);
            contentAnimator.subscribe(ypCustomerMTO);
            contentAnimator.subscribe(ypProductionMTS);
            contentAnimator.subscribe(ypProductionMTO);
            contentAnimator.subscribe(marketMTS);
            contentAnimator.subscribe(marketMTO);
            for (DemoRetailer r : mtsRet)
            {
                contentAnimator.subscribe(r);
            }
            for (DemoRetailer r : mtoRet)
            {
                contentAnimator.subscribe(r);
            }
            contentAnimator.subscribe(mtsMan);
            contentAnimator.subscribe(mtoMan);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public Length calculateDistance(final Point<?> loc1, final Point<?> loc2)
    {
        double dx = loc2.getX() - loc1.getX();
        double dy = loc2.getY() - loc1.getY();
        return new Length(Math.sqrt(dx * dx + dy * dy), LengthUnit.KILOMETER);
    }
}
