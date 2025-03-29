package nl.tudelft.simulation.supplychain.demo.bullwhip;

import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.MassUnit;
import org.djunits.unit.VolumeUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Mass;
import org.djunits.value.vdouble.scalar.Volume;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.model.AbstractDSOLModel;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistDiscrete;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.distributions.DistNormal;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiverDirect;
import nl.tudelft.simulation.supplychain.demo.DemoContentAnimator;
import nl.tudelft.simulation.supplychain.demo.reference.DemoManufacturer;
import nl.tudelft.simulation.supplychain.demo.reference.DemoMarket;
import nl.tudelft.simulation.supplychain.demo.reference.DemoRetailer;
import nl.tudelft.simulation.supplychain.demo.reference.DemoYP;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.Bank;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;
import nl.tudelft.simulation.supplychain.product.BillOfMaterials;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.product.Sku;
import nl.tudelft.simulation.supplychain.role.demand.Demand;
import nl.tudelft.simulation.supplychain.test.TestModel;
import nl.tudelft.simulation.supplychain.util.DistDiscreteTriangular;

/**
 * BullwhipModel.java. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class BullwhipModel extends AbstractDSOLModel<Duration, SupplyChainSimulatorInterface>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the simulator. */
    private SupplyChainSimulatorInterface devsSimulator;

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
        try
        {
            if (getSimulator() instanceof AnimatorInterface)
            {
                // First we create some background. We set the zValue to -Double.Min value to ensure that it is actually drawn
                // "below" our actors and messages.
                new SingleImageRenderable<>(new OrientedPoint3d(0.0, 0.0, -Double.MIN_VALUE), new Bounds3d(800, 600, 0),
                        getSimulator(),
                        TestModel.class.getResource("/nl/tudelft/simulation/supplychain/demo/bullwhip/images/background.gif"));
            }

            // basics
            StreamInterface stream = new MersenneTwister();

            // Products and BOM
            Product keyboard = new Product("keyboard", Sku.PIECE, new Money(15.0, MoneyUnit.USD),
                    new Mass(0.5, MassUnit.KILOGRAM), new Volume(50.0 * 15.0 * 2.0, VolumeUnit.CUBIC_CENTIMETER), 0.0);
            Product casing = new Product("casing", Sku.PIECE, new Money(400.0, MoneyUnit.USD),
                    new Mass(10.0, MassUnit.KILOGRAM), new Volume(60.0 * 50.0 * 20.0, VolumeUnit.CUBIC_CENTIMETER), 0.02);
            Product mouse = new Product("mouse", Sku.PIECE, new Money(10.0, MoneyUnit.USD), new Mass(0.1, MassUnit.KILOGRAM),
                    new Volume(10.0 * 5.0 * 4.0, VolumeUnit.CUBIC_CENTIMETER), 0.0);
            Product monitor = new Product("monitor", Sku.PIECE, new Money(200.0, MoneyUnit.USD),
                    new Mass(5.0, MassUnit.KILOGRAM), new Volume(60.0 * 40.0 * 10.0, VolumeUnit.CUBIC_CENTIMETER), 0.01);
            Product pc = new Product("PC", Sku.PIECE, new Money(1100.0, MoneyUnit.USD), new Mass(16.0, MassUnit.KILOGRAM),
                    new Volume(10.0 * 5.0 * 4.0, VolumeUnit.CUBIC_CENTIMETER), 0.02);
            BillOfMaterials pcBOM = new BillOfMaterials(pc);
            pcBOM.add(keyboard, 1.0);
            pcBOM.add(casing, 1.0);
            pcBOM.add(mouse, 1.0);
            pcBOM.add(monitor, 1.0);

            // create the bank
            Bank ing = new Bank("ING", new ContentReceiverDirect(), getSimulator(), new OrientedPoint3d(0, 0, 0), null);
            ing.setAnnualInterestRateNeg(0.080);
            ing.setAnnualInterestRatePos(0.025);

            // we create two yellow page 'domains', one between the customers and the retailers,
            // and one between the retailers, manufacturers, and suppliers
            DemoYP ypCustomerMTS = new DemoYP("YP_customer_MTS", getSimulator(), new OrientedPoint3d(400, 40, 0), ing);
            DemoYP ypCustomerMTO = new DemoYP("YP_customer_MTO", getSimulator(), new OrientedPoint3d(500, 40, 0), ing);
            DemoYP ypProductionMTS = new DemoYP("YP_production_MTS", getSimulator(), new OrientedPoint3d(600, 40, 0), ing);
            DemoYP ypProductionMTO = new DemoYP("YP_production_MTO", getSimulator(), new OrientedPoint3d(700, 40, 0), ing);

            // CUSTOMER or MARKET
            DemoMarket customer = new DemoMarket("US East", getSimulator(), new OrientedPoint3d(40, 150, 0), ing,
                    new Money(10000.0, MoneyUnit.USD), pc, ypCustomerMTS, stream);
            // Buy AINT(TRIA(2,5,10)) computers every NORM(3,1) hour starting at t=0.1
            DistContinuousDuration generatorStartTime1 =
                    new DistContinuousDuration(new DistConstant(stream, 0.1), DurationUnit.HOUR);
            DistContinuousDuration generatorEndTime =
                    new DistContinuousDuration(new DistConstant(stream, 1.0E6), DurationUnit.HOUR);
            DistContinuousDuration interBuyingTime1 =
                    new DistContinuousDuration(new DistNormal(stream, 3.0, 1.0), DurationUnit.HOUR);
            DistDiscrete batchSize1 = new DistDiscreteTriangular(stream, 2.0, 5.0, 10.0);
            customer.getDemandGeneration().addDemandGenerator(pc,
                    new Demand(pc, interBuyingTime1, batchSize1, generatorStartTime1, generatorEndTime));
            // Buy AINT(TRIA(2,4,5)) computers every NORM(4,2) hour starting at t=0.1
            DistContinuousDuration generatorStartTime2 =
                    new DistContinuousDuration(new DistConstant(stream, 0.1), DurationUnit.HOUR);
            DistContinuousDuration interBuyingTime2 =
                    new DistContinuousDuration(new DistNormal(stream, 4.0, 2.0), DurationUnit.HOUR);
            DistDiscrete batchSize2 = new DistDiscreteTriangular(stream, 2.0, 4.0, 5.0);
            customer.getDemandGeneration().addDemandGenerator(pc,
                    new Demand(pc, interBuyingTime2, batchSize2, generatorStartTime2, generatorEndTime));
            // Buy AINT(TRIA(5,8,12)) computers every NORM(5,3) hour starting at t=0.1
            DistContinuousDuration generatorStartTime3 =
                    new DistContinuousDuration(new DistConstant(stream, 0.1), DurationUnit.HOUR);
            DistContinuousDuration interBuyingTime3 =
                    new DistContinuousDuration(new DistNormal(stream, 5.0, 3.0), DurationUnit.HOUR);
            DistDiscrete batchSize3 = new DistDiscreteTriangular(stream, 5.0, 8.0, 12.0);
            customer.getDemandGeneration().addDemandGenerator(pc,
                    new Demand(pc, interBuyingTime3, batchSize3, generatorStartTime3, generatorEndTime));
            // Buy AINT(TRIA(3,8,10)) computers every EXPO(1) hour starting at t=504.0
            DistContinuousDuration generatorStartTime4 =
                    new DistContinuousDuration(new DistConstant(stream, 504.0), DurationUnit.HOUR);
            DistContinuousDuration interBuyingTime4 =
                    new DistContinuousDuration(new DistExponential(stream, 1.0), DurationUnit.HOUR);
            DistDiscrete batchSize4 = new DistDiscreteTriangular(stream, 3.0, 8.0, 10.0);
            customer.getDemandGeneration().addDemandGenerator(pc,
                    new Demand(pc, interBuyingTime4, batchSize4, generatorStartTime4, generatorEndTime));
            // set max distance for suppliers to 6000 km
            // TODO: customer.setMaxDistanceSuppliers(6000.0);

            // Retailers
            StreamInterface streamMTS = new MersenneTwister(2L);
            StreamInterface streamMTO = new MersenneTwister(4L);
            DemoRetailer[] mtsRet = new DemoRetailer[5];
            mtsRet[0] = new DemoRetailer("Seattle_MTS", getSimulator(), new OrientedPoint3d(-200, -270, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTS, ypProductionMTS, streamMTS, true);
            mtsRet[1] = new DemoRetailer("LosAngeles_MTS", getSimulator(), new OrientedPoint3d(-200, -210, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTS, ypProductionMTS, streamMTS, true);
            mtsRet[2] = new DemoRetailer("NewYork_MTS", getSimulator(), new OrientedPoint3d(-200, -150, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTS, ypProductionMTS, streamMTS, true);
            mtsRet[3] = new DemoRetailer("Washington_MTS", getSimulator(), new OrientedPoint3d(-200, -90, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTS, ypProductionMTS, streamMTS, true);
            mtsRet[4] = new DemoRetailer("Miami_MTS", getSimulator(), new OrientedPoint3d(-200, -30, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTS, ypProductionMTS, streamMTS, true);

            DemoRetailer[] mtoRet = new DemoRetailer[5];
            mtoRet[0] = new DemoRetailer("Seattle_MTO", getSimulator(), new OrientedPoint3d(-200, 30, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTO, ypProductionMTO, streamMTO, false);
            mtoRet[1] = new DemoRetailer("LosAngeles_MTO", getSimulator(), new OrientedPoint3d(-200, 90, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTO, ypProductionMTO, streamMTO, false);
            mtoRet[2] = new DemoRetailer("NewYork_MTO", getSimulator(), new OrientedPoint3d(-200, 150, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTO, ypProductionMTO, streamMTO, false);
            mtoRet[3] = new DemoRetailer("Washington_MTO", getSimulator(), new OrientedPoint3d(-200, 210, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTO, ypProductionMTO, streamMTO, false);
            mtoRet[4] = new DemoRetailer("Miami_MTO", getSimulator(), new OrientedPoint3d(-200, 270, 1), ing,
                    new Money(100000, MoneyUnit.USD), pc, 4.0, ypCustomerMTO, ypProductionMTO, streamMTO, false);

            // Manufacturers
            DemoManufacturer mtsMan = new DemoManufacturer("MexicoCity_MTS", getSimulator(), new OrientedPoint3d(0, -150, 1),
                    ing, new Money(1000000, MoneyUnit.USD), pc, 50, ypCustomerMTS, ypProductionMTS, streamMTS, true);
            DemoManufacturer mtoMan = new DemoManufacturer("MexicoCity_MTO", getSimulator(), new OrientedPoint3d(0, 150, 1),
                    ing, new Money(1000000, MoneyUnit.USD), pc, 50, ypCustomerMTO, ypProductionMTO, streamMTO, false);

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

    @Override
    public Serializable getSourceId()
    {
        return "MTSMTOModel";
    }

}
