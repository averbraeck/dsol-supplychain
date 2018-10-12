package Bahareh;

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
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.product.BillOfMaterials;

import nl.tudelft.simulation.supplychain.product.Unit;
import nl.tudelft.simulation.supplychain.test.Client;
import nl.tudelft.simulation.supplychain.test.Factory;
import nl.tudelft.simulation.supplychain.test.PCShop;
import nl.tudelft.simulation.supplychain.test.TestModel;



/**
 * <p>
 * Copyright (c) 2016 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="http://opentrafficsim.org/docs/license.html">OpenTrafficSim License</a>.
 * </p>
 * $LastChangedDate: 2015-07-24 02:58:59 +0200 (Fri, 24 Jul 2015) $, @version $Revision: 1147 $, by $Author: averbraeck $,
 * initial version Oct 8, 2018 <br>
 * @author <a href="http://www.tbm.tudelft.nl/averbraeck">Alexander Verbraeck</a> 
 * @author <a href="http://https://www.tudelft.nl/tbm/over-de-faculteit/afdelingen/multi-actor-systems/people/phd-candidates/b-bahareh-zohoori/">Bahareh Zohoori</a> 
 */
public class ATOModel  implements DSOLModel.TimeDoubleUnit
{
    /** the serial version uid */
    private static final long serialversionuid = 12L;
    
    
    /** timing run-time */
    private long startTimeMs = 0;
    
    /** the simulator. */
    private DEVSSimulatorInterface.TimeDoubleUnit devsSimulator;
    
   /** construct new ATOModel */
    public ATOModel()
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
            new SingleImageRenderable(new DirectedPoint(0.0, 0.0, -Double.MIN_VALUE), new Dimension(1618, 716),
                    this.devsSimulator,
                    ATOModel.class.getResource("/supplychain-ato/src/main/resources/images/europe-osm.png"));
         // basics
            StreamInterface streamATO = new MersenneTwister();
            StreamInterface streamMTS = new MersenneTwister();
        // Product & BOM
            
            //MTS
            Product keyboard =
                    new Product("keyboard", Unit.PIECE, new Money(15.0, MoneyUnit.USD), new Mass(0.5, MassUnit.KILOGRAM), 0.0);
            Product casing =
                    new Product("casing", Unit.PIECE, new Money(400.0, MoneyUnit.USD), new Mass(10.0, MassUnit.KILOGRAM), 0.02);
            Product mouse =
                    new Product("mouse", Unit.PIECE, new Money(10.0, MoneyUnit.USD), new Mass(0.1, MassUnit.KILOGRAM), 0.0);
            Product monitor =
                    new Product("monitor", Unit.PIECE, new Money(200.0, MoneyUnit.USD), new Mass(5.0, MassUnit.KILOGRAM), 0.01);
           
            //ATO
            Product pc = new Product("PC", Unit.PIECE, new Money(1100.0, MoneyUnit.USD), new Mass(16.0, MassUnit.KILOGRAM), 0.02);
           
            BillOfMaterials pcBOM = new BillOfMaterials(pc);
            pcBOM.add(keyboard, 1.0);
            pcBOM.add(casing, 1.0);
            pcBOM.add(mouse, 1.0);
            pcBOM.add(monitor, 1.0);
            
          
          // create the bank
            Bank ing = new Bank("ING", this.devsSimulator, new Point3d(0, 0, 0));
            ing.setAnnualInterestRateNeg(0.080);
            ing.setAnnualInterestRatePos(0.025);
        

        // Yellow page 
            atoYP ypCustomer = new atoYP("yellow-page-customer", this.devsSimulator, new Point3d(-300, -270, 1), ing);
            atoYP ypSupplier = new atoYP("yellow-page-supplier", this.devsSimulator, new Point3d(-300, -270, 1), ing);
            
            
         // customer-pc
          atoMarket CusUK = new atoMarket("customer-UK", this.devsSimulator, new Point3d(-360, -150, 1), ing,
                    new Money(10000.0, MoneyUnit.USD), pc, ypCustomer, streamATO);
          atoMarket CusTurkey = new atoMarket("customer-Turkey", this.devsSimulator, new Point3d(-360, 150, 1), ing,
                    new Money(10000.0, MoneyUnit.USD), pc, ypSupplier, streamATO);
            

         // Manufacturers ATO-pc
          atoManufacturer manNL = new atoManufacturer("manufacturer-NL", this.devsSimulator, new Point3d(0, -150, 1), ing,
                    new Money(1000000, MoneyUnit.USD), pc, 50, ypCustomer, ypSupplier, streamATO, false);
          atoManufacturer manDE = new atoManufacturer("Manufacturer_DE", this.devsSimulator, new Point3d(0, 150, 1), ing,
                    new Money(1000000, MoneyUnit.USD), pc, 50, ypCustomer,ypSupplier, streamATO, false);
            
         // Manufacturers MTS-monitor
            atoManufacturer manSpain = new atoManufacturer("Manufacturer-DE", this.devsSimulator, new Point3d(0, 150, 1), ing,
                    new Money(1000000, MoneyUnit.USD), monitor, 50, ypCustomer,ypSupplier, streamMTS, true);
            
         // Supplier MTS-mouse,casing
            atoSupplier SupDenmark= new atoSupplier("supplier-Denmark", this.devsSimulator, new Point3d(0, 150, 1), ing,
                    new Money(1000000, MoneyUnit.USD), mouse, 50, ypSupplier, streamATO, true);
       
            atoSupplier SupAustria= new atoSupplier("supplier-Austria", this.devsSimulator, new Point3d(0, 150, 1), ing,
                    new Money(1000000, MoneyUnit.USD), casing, 50, ypSupplier, streamATO, true);

         // supplier ATO-keyboard
            atoSupplier SupSpain= new atoSupplier("supplier spain", this.devsSimulator, new Point3d(0, 150, 1), ing,
                    new Money(1000000, MoneyUnit.USD), keyboard, 50, ypSupplier, streamATO, false);

        }        
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
     

            
            
           
     

