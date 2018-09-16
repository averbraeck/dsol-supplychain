package nl.tudelft.simulation.supplychain.demo.reference;

import java.rmi.RemoteException;

import javax.media.j3d.Bounds;
import javax.naming.NamingException;
import javax.vecmath.Point3d;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.LengthUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Money;

import nl.tudelft.simulation.actor.messagehandlers.HandleAllMessages;
import nl.tudelft.simulation.actor.messagehandlers.MessageHandlerInterface;
import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface.TimeDoubleUnit;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.distributions.DistTriangular;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.language.d3.BoundingBox;
import nl.tudelft.simulation.messaging.devices.reference.WebApplication;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.contentstore.memory.LeanContentStore;
import nl.tudelft.simulation.supplychain.demand.Demand;
import nl.tudelft.simulation.supplychain.demand.DemandGeneration;
import nl.tudelft.simulation.supplychain.handlers.BillHandler;
import nl.tudelft.simulation.supplychain.handlers.InternalDemandHandlerYP;
import nl.tudelft.simulation.supplychain.handlers.OrderConfirmationHandler;
import nl.tudelft.simulation.supplychain.handlers.PaymentPolicyEnum;
import nl.tudelft.simulation.supplychain.handlers.QuoteComparatorEnum;
import nl.tudelft.simulation.supplychain.handlers.QuoteHandler;
import nl.tudelft.simulation.supplychain.handlers.QuoteHandlerAll;
import nl.tudelft.simulation.supplychain.handlers.ShipmentHandler;
import nl.tudelft.simulation.supplychain.handlers.ShipmentHandlerConsume;
import nl.tudelft.simulation.supplychain.handlers.YellowPageAnswerHandler;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Customer;
import nl.tudelft.simulation.supplychain.reference.YellowPage;
import nl.tudelft.simulation.supplychain.roles.BuyingRole;
import nl.tudelft.simulation.unit.dist.DistConstantDurationUnit;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;

/**
 * MtsMtomarket.java. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DemoMarket extends Customer
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param name
     * @param simulator
     * @param position
     * @param bank
     * @param initialBankAccount 
     * @param product 
     * @param ypCustomre 
     * @param stream 
     */
    public DemoMarket(String name, TimeDoubleUnit simulator, Point3d position, Bank bank, Money initialBankAccount,
            Product product, YellowPage ypCustomre, StreamInterface stream)
    {
        super(name, simulator, position, bank, initialBankAccount, new LeanContentStore(simulator));

        // COMMUNICATION

        WebApplication www = new WebApplication("Web-" + name, this.simulator);
        super.addSendingDevice(www);
        MessageHandlerInterface webSystem = new HandleAllMessages(this);
        super.addReceivingDevice(www, webSystem, new DistConstantDurationUnit(new Duration(10.0, DurationUnit.SECOND)));

        // DEMAND GENERATION

        Demand demand = new Demand(product, new DistContinuousDurationUnit(new DistExponential(stream, 8.0), DurationUnit.HOUR),
                new DistConstant(stream, 1.0), new DistConstantDurationUnit(Duration.ZERO),
                new DistConstantDurationUnit(new Duration(14.0, DurationUnit.DAY)));
        DemandGeneration dg = new DemandGeneration(this, simulator,
                new DistContinuousDurationUnit(new DistExponential(stream, 2.0), DurationUnit.MINUTE));
        dg.addDemandGenerator(product, demand);
        this.setDemandGeneration(dg);

        // MESSAGE HANDLING

        DistContinuousDurationUnit administrativeDelayInternalDemand =
                new DistContinuousDurationUnit(new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
        InternalDemandHandlerYP internalDemandHandler = new InternalDemandHandlerYP(this, administrativeDelayInternalDemand, ypCustomre,
                new Length(1E6, LengthUnit.METER), 1000, null);

        DistContinuousDurationUnit administrativeDelayYellowPageAnswer =
                new DistContinuousDurationUnit(new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
        YellowPageAnswerHandler ypAnswerHandler = new YellowPageAnswerHandler(this, administrativeDelayYellowPageAnswer);

        DistContinuousDurationUnit administrativeDelayQuote =
                new DistContinuousDurationUnit(new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
        QuoteHandler quoteHandler =
                new QuoteHandlerAll(this, QuoteComparatorEnum.SORT_PRICE_DATE_DISTANCE, administrativeDelayQuote, 0.5, 0);

        OrderConfirmationHandler orderConfirmationHandler = new OrderConfirmationHandler(this);

        ShipmentHandler shipmentHandler = new ShipmentHandlerConsume(this);

        DistContinuousDurationUnit paymentDelay =
                new DistContinuousDurationUnit(new DistConstant(stream, 0.0), DurationUnit.HOUR);
        BillHandler billHandler = new BillHandler(this, this.getBankAccount(), PaymentPolicyEnum.PAYMENT_ON_TIME, paymentDelay);

        BuyingRole buyingRole = new BuyingRole(this, simulator, internalDemandHandler, ypAnswerHandler, quoteHandler,
                orderConfirmationHandler, shipmentHandler, billHandler);
        this.setBuyingRole(buyingRole);

        // ANIMATION
        
        if (simulator instanceof AnimatorInterface)
        {
            try
            {
                new SingleImageRenderable(this, simulator,
                        DemoMarket.class.getResource("/nl/tudelft/simulation/supplychain/images/Market.gif"));
            }
            catch (RemoteException | NamingException exception)
            {
                exception.printStackTrace();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Bounds getBounds()
    {
        return new BoundingBox(25.0, 25.0, 1.0);
    }

}
