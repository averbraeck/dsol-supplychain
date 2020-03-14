package nl.tudelft.simulation.supplychain.ato;

import java.awt.Dimension;
import java.io.Serializable;

import javax.vecmath.Point3d;

import org.djunits.unit.MassUnit;
import org.djunits.value.vdouble.scalar.Mass;

import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.model.AbstractDSOLModel;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.language.d3.DirectedPoint;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;
import nl.tudelft.simulation.supplychain.product.BillOfMaterials;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.product.Unit;

/**
 * <p>
 * Copyright (c) 2016 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="http://opentrafficsim.org/docs/license.html">OpenTrafficSim License</a>.
 * </p>
 * $LastChangedDate: 2015-07-24 02:58:59 +0200 (Fri, 24 Jul 2015) $, @version $Revision: 1147 $, by $Author: averbraeck $,
 * initial version Oct 8, 2018 <br>
 * @author <a href="http://www.tbm.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href=
 *         "http://https://www.tudelft.nl/tbm/over-de-faculteit/afdelingen/multi-actor-systems/people/phd-candidates/b-bahareh-zohoori/">Bahareh
 *         Zohoori</a>
 */
public class ATOModel extends AbstractDSOLModel.TimeDoubleUnit<DEVSSimulatorInterface.TimeDoubleUnit>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** timing run-time */
    private long startTimeMs = 0;

    /**
     * construct new ATOModel
     * @param simulator the simulator
     */
    public ATOModel(final DEVSSimulatorInterface.TimeDoubleUnit simulator)
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
            if (getSimulator() instanceof AnimatorInterface)
            {
                // First we create some background. We set the zValue to -Double.Min value to ensure that it is actually drawn
                // "below" our actors and messages.
                new SingleImageRenderable<>(new DirectedPoint(0.0, 0.0, -Double.MIN_VALUE), new Dimension(1618, 716),
                    getSimulator(), ATOModel.class.getResource("/supplychain-ato/src/main/resources/images/europe-osm.png"));
                // basics
                // do we need 2 streams?
                StreamInterface streamATO = new MersenneTwister();

                // Product
                // we should define a new attribute for product as well to show mts and mto for restocking policy!

                Product partA = new Product("partA", Unit.PIECE, new Money(10.0, MoneyUnit.USD), new Mass(0.5,
                    MassUnit.KILOGRAM), 0.0);
                Product partB = new Product("PartB", Unit.PIECE, new Money(20.0, MoneyUnit.USD), new Mass(10.0,
                    MassUnit.KILOGRAM), 0.02);
                Product partC = new Product("partC", Unit.PIECE, new Money(100.0, MoneyUnit.USD), new Mass(0.1,
                    MassUnit.KILOGRAM), 0.0);
                Product partD = new Product("partD", Unit.PIECE, new Money(200.0, MoneyUnit.USD), new Mass(5.0,
                    MassUnit.KILOGRAM), 0.01);
                Product partCa = new Product("partCa", Unit.PIECE, new Money(50.0, MoneyUnit.USD), new Mass(5.0,
                    MassUnit.KILOGRAM), 0.01);
                Product partCb = new Product("partCb", Unit.PIECE, new Money(30.0, MoneyUnit.USD), new Mass(5.0,
                    MassUnit.KILOGRAM), 0.01);
                Product partCc = new Product("partCc", Unit.PIECE, new Money(10.0, MoneyUnit.USD), new Mass(5.0,
                    MassUnit.KILOGRAM), 0.01);
                Product partDa = new Product("partDa", Unit.PIECE, new Money(100.0, MoneyUnit.USD), new Mass(5.0,
                    MassUnit.KILOGRAM), 0.01);
                Product partDb = new Product("partDb", Unit.PIECE, new Money(80.0, MoneyUnit.USD), new Mass(5.0,
                    MassUnit.KILOGRAM), 0.01);
                Product subAssembly = new Product("Sub-Assembly", Unit.PIECE, new Money(50.0, MoneyUnit.USD), new Mass(5.0,
                    MassUnit.KILOGRAM), 0.01);
                Product finalProduct = new Product("finalProduct", Unit.PIECE, new Money(1000.0, MoneyUnit.USD), new Mass(
                    5.0, MassUnit.KILOGRAM), 0.01);

                // BOM

                BillOfMaterials partCbom = new BillOfMaterials(partC);
                partCbom.add(partCa, 1);
                partCbom.add(partCb, 1);
                partCbom.add(partCc, 1);

                BillOfMaterials partDbom = new BillOfMaterials(partD);
                partDbom.add(partDa, 1);
                partDbom.add(partDb, 1);

                BillOfMaterials subAssemblyBom = new BillOfMaterials(subAssembly);
                subAssemblyBom.add(partA, 1);
                subAssemblyBom.add(partB, 1);

                BillOfMaterials finalProductBom = new BillOfMaterials(finalProduct);
                finalProductBom.add(subAssembly, 1);
                finalProductBom.add(partC, 1);
                finalProductBom.add(partD, 1);

                // create the bank
                Bank ing = new Bank("ING", getSimulator(), new Point3d(0, 0, 0));
                ing.setAnnualInterestRateNeg(0.080);
                ing.setAnnualInterestRatePos(0.025);

                // Yellow page
                ATOYP ypCustomer = new ATOYP("yellow-page-customer", getSimulator(), new Point3d(-300, -270, 1), ing);
                ATOYP ypManufacturer = new ATOYP("yellow-page-manufacturer", getSimulator(), new Point3d(-300, -270, 1),
                    ing);
                ATOYP ypSupplier = new ATOYP("yellow-page-supplier", getSimulator(), new Point3d(-300, -270, 1), ing);

                // customer
                // do we need to define order amount?
                ATOMarket CustomerUK = new ATOMarket("customer-UK", getSimulator(), new Point3d(-360, -150, 1), ing,
                    new Money(10000.0, MoneyUnit.USD), finalProduct, ypCustomer, streamATO);
                ATOMarket CustomerTurkey = new ATOMarket("customer-Turkey", getSimulator(), new Point3d(-360, 150, 1), ing,
                    new Money(10000.0, MoneyUnit.USD), finalProduct, ypCustomer, streamATO);

                // Manufacturer-fianlProduct
                // why manufacturer need yp-customer? and why it does not need yp supplier?
                // how to deal with sub-assembly?
                ATOManufacturer manufacturerFNNL = new ATOManufacturer("manufacturer-finalProduct-NL", getSimulator(),
                    new Point3d(0, -150, 1), ing, new Money(1000000, MoneyUnit.USD), finalProduct, 0, ypCustomer,
                    ypManufacturer, streamATO, false);
                // Manufacturer-subAssembly
                ATOManufacturer manufacturerSANL = new ATOManufacturer("manufacturer-subAssembly-NL", getSimulator(),
                    new Point3d(0, -150, 1), ing, new Money(1000000, MoneyUnit.USD), subAssembly, 10, ypCustomer,
                    ypManufacturer, streamATO, true);

                // how to create internalDemand for MTO suppliers who dont have restocking policy for creating internal demand?
                // Supplier-1st tier-MTS
                ATOSupplier SupPartA = new ATOSupplier("supplier-partA-Germany", getSimulator(), new Point3d(0, 150, 1), ing,
                    new Money(1000000, MoneyUnit.USD), partA, 20, ypSupplier, streamATO, true);

                ATOSupplier SupPartB = new ATOSupplier("supplier-partB-Italy", getSimulator(), new Point3d(0, 150, 1), ing,
                    new Money(1000000, MoneyUnit.USD), partB, 30, ypSupplier, streamATO, true);

                // Supplier-1st tier-MTo
                ATOSupplier SupPartC = new ATOSupplier("supplier-partC-France", getSimulator(), new Point3d(0, 150, 1), ing,
                    new Money(1000000, MoneyUnit.USD), partC, 0, ypSupplier, streamATO, false);
                ATOSupplier SupPartD = new ATOSupplier("supplier-partD-NL", getSimulator(), new Point3d(0, 150, 1), ing,
                    new Money(1000000, MoneyUnit.USD), partD, 0, ypSupplier, streamATO, false);

                // Supplier-2nd tier-MTS
                ATOSupplier SupPartCa = new ATOSupplier("supplier-partCa-Poland", getSimulator(), new Point3d(0, 150, 1),
                    ing, new Money(1000000, MoneyUnit.USD), partCa, 10, ypSupplier, streamATO, true);
                ATOSupplier SupPartCb = new ATOSupplier("supplier-partCb-Denmark", getSimulator(), new Point3d(0, 150, 1),
                    ing, new Money(1000000, MoneyUnit.USD), partCb, 20, ypSupplier, streamATO, true);
                ATOSupplier SupPartCc = new ATOSupplier("supplier-partCb-Austria", getSimulator(), new Point3d(0, 150, 1),
                    ing, new Money(1000000, MoneyUnit.USD), partCc, 25, ypSupplier, streamATO, true);
                ATOSupplier SupPartDa = new ATOSupplier("supplier-partDa-UK", getSimulator(), new Point3d(0, 150, 1), ing,
                    new Money(1000000, MoneyUnit.USD), partDa, 30, ypSupplier, streamATO, true);
                ATOSupplier SupPartDb = new ATOSupplier("supplier-partDb-Spain", getSimulator(), new Point3d(0, 150, 1), ing,
                    new Money(1000000, MoneyUnit.USD), partDb, 50, ypSupplier, streamATO, true);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return "ATOModel";
    }

}
