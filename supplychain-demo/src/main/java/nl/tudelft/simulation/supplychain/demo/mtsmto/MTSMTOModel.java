package nl.tudelft.simulation.supplychain.demo.mtsmto;

import java.io.Serializable;

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
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.supplychain.demo.DemoContentAnimator;
import nl.tudelft.simulation.supplychain.demo.reference.DemoManufacturer;
import nl.tudelft.simulation.supplychain.demo.reference.DemoMarket;
import nl.tudelft.simulation.supplychain.demo.reference.DemoRetailer;
import nl.tudelft.simulation.supplychain.demo.reference.DemoYP;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainAnimator;
import nl.tudelft.simulation.supplychain.money.Bank;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.money.MoneyUnit;
import nl.tudelft.simulation.supplychain.product.BillOfMaterials;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.product.Sku;
import nl.tudelft.simulation.supplychain.test.TestModel;

/**
 * MTSMTOModel.java. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MTSMTOModel extends AbstractDSOLModel<Duration, SupplyChainAnimator>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the simulator. */
    private SupplyChainAnimator devsSimulator;

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
        try
        {
            if (getSimulator() instanceof AnimatorInterface)
            {
                // First we create some background. We set the zValue to -Double.Min value to ensure that it is actually drawn
                // "below" our actors and messages.
                new SingleImageRenderable<>(new OrientedPoint3d(0.0, 0.0, -Double.MIN_VALUE), new Bounds3d(800, 600, 0),
                        getSimulator(),
                        TestModel.class.getResource("/nl/tudelft/simulation/supplychain/demo/mtsmto/images/background.gif"));
            }

            // basics
            StreamInterface streamMTS = new MersenneTwister();
            StreamInterface streamMTO = new MersenneTwister();

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
            Bank ing = new Bank("ING", getSimulator(), new OrientedPoint3d(0, 0, 0));
            ing.setAnnualInterestRateNeg(0.080);
            ing.setAnnualInterestRatePos(0.025);

            // we create two search 'domains', one between the customers and the retailers,
            // and one between the retailers, manufacturers, and suppliers
            DemoYP ypCustomerMTS = new DemoYP("YP_customer_MTS", getSimulator(), new OrientedPoint3d(-300, -270, 1), ing);
            DemoYP ypCustomerMTO = new DemoYP("YP_customer_MTO", getSimulator(), new OrientedPoint3d(-300, 30, 1), ing);
            DemoYP ypProductionMTS = new DemoYP("YP_production_MTS", getSimulator(), new OrientedPoint3d(100, -270, 1), ing);
            DemoYP ypProductionMTO = new DemoYP("YP_production_MTO", getSimulator(), new OrientedPoint3d(100, 30, 1), ing);

            // Markets
            DemoMarket marketMTS = new DemoMarket("Market_MTS", getSimulator(), new OrientedPoint3d(-360, -150, 1), ing,
                    new Money(10000.0, MoneyUnit.USD), pc, ypCustomerMTS, streamMTS);
            DemoMarket marketMTO = new DemoMarket("Market_MTO", getSimulator(), new OrientedPoint3d(-360, 150, 1), ing,
                    new Money(10000.0, MoneyUnit.USD), pc, ypCustomerMTO, streamMTO);

            // Retailers
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
            contentAnimator.subscribe(marketMTS);
            contentAnimator.subscribe(marketMTO);
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
