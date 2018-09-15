package nl.tudelft.simulation.supplychain.demo.reference;

import javax.vecmath.Point3d;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.LengthUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Money;

import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface.TimeDoubleUnit;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistTriangular;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.content.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.reference.Retailer;
import nl.tudelft.simulation.supplychain.stock.Stock;
import nl.tudelft.simulation.supplychain.transport.TransportMode;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;
import nl.tudelft.simulation.yellowpage.YellowPageInterface;

/**
 * Retailer.java. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DemoRetailer extends Retailer
{
    protected DistContinuousDurationUnit administrativeDelayInternalDemand;

    protected DistContinuousDurationUnit routeDelayInternalDemand;

    protected DistContinuousDurationUnit administrativeDelayYellowPageAnswer;

    protected DistContinuousDurationUnit routeDelayYellowPageAnswer;

    protected DistContinuousDurationUnit administrativeDelayQuote;

    protected DistContinuousDurationUnit routeDelayQuote;

    protected DistContinuousDurationUnit administrativeDelayBill;

    protected DistContinuousDurationUnit routeDelayBill;

    protected DistContinuousDurationUnit administrativeDelayRFQ;

    protected DistContinuousDurationUnit routeDelayRFQ;

    protected DistContinuousDurationUnit administrativeDelayOrder;

    protected DistContinuousDurationUnit routeDelayOrder;

    protected YellowPageInterface yellowPage;

    protected Length maxDistanceSuppliers;

    protected int maxNumberRFQs;

    protected Duration paymentTime;

    protected TransportMode transportMode;

    protected DistTriangular profitDist;

    /**
     * @param name
     * @param simulator
     * @param position
     * @param bank
     * @param initialBankBalance
     * @param contentStore 
     */
    public DemoRetailer(String name, TimeDoubleUnit simulator, Point3d position, Bank bank, Money initialBankBalance, ContentStoreInterface contentStore)
    {
        super(name, simulator, position, bank, initialBankBalance, contentStore);
    }

    /**
     * @param name
     * @param simulator
     * @param position
     * @param bank
     * @param contentStore 
     */
    public DemoRetailer(String name, TimeDoubleUnit simulator, Point3d position, Bank bank, ContentStoreInterface contentStore)
    {
        super(name, simulator, position, bank, contentStore);
    }

    public void init(SimulatorInterface simulator, StreamInterface stream, String name, Point3d location, 
            Stock initialStock, YellowPageInterface yellowPage)
    {
        this.yellowPage = yellowPage;

        this.administrativeDelayInternalDemand =
                new DistContinuousDurationUnit(new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
        this.routeDelayInternalDemand = new DistContinuousDurationUnit(new DistConstant(stream, 5), DurationUnit.HOUR);
        this.administrativeDelayYellowPageAnswer =
                new DistContinuousDurationUnit(new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
        this.routeDelayYellowPageAnswer = new DistContinuousDurationUnit(new DistConstant(stream, 5), DurationUnit.HOUR);
        this.administrativeDelayQuote =
                new DistContinuousDurationUnit(new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
        this.routeDelayQuote = new DistContinuousDurationUnit(new DistConstant(stream, 5), DurationUnit.HOUR);
        this.administrativeDelayBill = new DistContinuousDurationUnit(new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
        this.routeDelayBill = new DistContinuousDurationUnit(new DistConstant(stream, 5), DurationUnit.HOUR);
        this.administrativeDelayRFQ = new DistContinuousDurationUnit(new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
        this.routeDelayRFQ = new DistContinuousDurationUnit(new DistConstant(stream, 5), DurationUnit.HOUR);
        this.administrativeDelayOrder =
                new DistContinuousDurationUnit(new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
        this.routeDelayOrder = new DistContinuousDurationUnit(new DistConstant(stream, 5), DurationUnit.HOUR);
        this.profitDist = new DistTriangular(stream, 0.9, 1.0, 1.2);

        this.maxDistanceSuppliers = new Length(1E6, LengthUnit.KILOMETER);
        this.maxNumberRFQs = Integer.MAX_VALUE;
        this.paymentTime = new Duration(14.0 * 24.0, DurationUnit.HOUR);
        this.transportMode = TransportMode.TRUCK;
    }
}
