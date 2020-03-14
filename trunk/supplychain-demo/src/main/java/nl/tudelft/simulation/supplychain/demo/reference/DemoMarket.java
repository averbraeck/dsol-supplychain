package nl.tudelft.simulation.supplychain.demo.reference;

import java.rmi.RemoteException;

import javax.media.j3d.Bounds;
import javax.naming.NamingException;
import javax.vecmath.Point3d;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.LengthUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;

import nl.tudelft.simulation.actor.messagehandlers.HandleAllMessages;
import nl.tudelft.simulation.actor.messagehandlers.MessageHandlerInterface;
import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface.TimeDoubleUnit;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.distributions.DistTriangular;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.language.d3.BoundingBox;
import nl.tudelft.simulation.messaging.devices.reference.WebApplication;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.contentstore.memory.LeanContentStore;
import nl.tudelft.simulation.supplychain.demand.Demand;
import nl.tudelft.simulation.supplychain.demand.DemandGeneration;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.policy.bill.BillPolicy;
import nl.tudelft.simulation.supplychain.policy.internaldemand.InternalDemandPolicyYP;
import nl.tudelft.simulation.supplychain.policy.orderconfirmation.OrderConfirmationPolicy;
import nl.tudelft.simulation.supplychain.policy.payment.PaymentPolicyEnum;
import nl.tudelft.simulation.supplychain.policy.quote.QuoteComparatorEnum;
import nl.tudelft.simulation.supplychain.policy.quote.QuotePolicy;
import nl.tudelft.simulation.supplychain.policy.quote.QuotePolicyAll;
import nl.tudelft.simulation.supplychain.policy.shipment.ShipmentPolicy;
import nl.tudelft.simulation.supplychain.policy.shipment.ShipmentPolicyConsume;
import nl.tudelft.simulation.supplychain.policy.yp.YellowPageAnswerPolicy;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Customer;
import nl.tudelft.simulation.supplychain.reference.YellowPage;
import nl.tudelft.simulation.supplychain.roles.BuyingRole;
import nl.tudelft.simulation.unit.dist.DistConstantDuration;

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
        super.addReceivingDevice(www, webSystem, new DistConstantDuration(new Duration(10.0, DurationUnit.SECOND)));

        // DEMAND GENERATION

        Demand demand = new Demand(product, new DistContinuousDuration(new DistExponential(stream, 8.0), DurationUnit.HOUR),
            new DistConstant(stream, 1.0), new DistConstantDuration(Duration.ZERO), new DistConstantDuration(new Duration(
                14.0, DurationUnit.DAY)));
        DemandGeneration dg = new DemandGeneration(this, new DistContinuousDuration(new DistExponential(stream, 2.0),
            DurationUnit.MINUTE));
        dg.addDemandGenerator(product, demand);
        this.setDemandGeneration(dg);

        // MESSAGE HANDLING

        DistContinuousDuration administrativeDelayInternalDemand = new DistContinuousDuration(new DistTriangular(stream, 2,
            2.5, 3), DurationUnit.HOUR);
        InternalDemandPolicyYP internalDemandHandler = new InternalDemandPolicyYP(this, administrativeDelayInternalDemand,
            ypCustomre, new Length(1E6, LengthUnit.METER), 1000, null);

        DistContinuousDuration administrativeDelayYellowPageAnswer = new DistContinuousDuration(new DistTriangular(stream, 2,
            2.5, 3), DurationUnit.HOUR);
        YellowPageAnswerPolicy ypAnswerHandler = new YellowPageAnswerPolicy(this, administrativeDelayYellowPageAnswer);

        DistContinuousDuration administrativeDelayQuote = new DistContinuousDuration(new DistTriangular(stream, 2, 2.5, 3),
            DurationUnit.HOUR);
        QuotePolicy quoteHandler = new QuotePolicyAll(this, QuoteComparatorEnum.SORT_PRICE_DATE_DISTANCE,
            administrativeDelayQuote, 0.5, 0);

        OrderConfirmationPolicy orderConfirmationHandler = new OrderConfirmationPolicy(this);

        ShipmentPolicy shipmentHandler = new ShipmentPolicyConsume(this);

        DistContinuousDuration paymentDelay = new DistContinuousDuration(new DistConstant(stream, 0.0), DurationUnit.HOUR);
        BillPolicy billHandler = new BillPolicy(this, this.getBankAccount(), PaymentPolicyEnum.PAYMENT_ON_TIME,
            paymentDelay);

        BuyingRole buyingRole = new BuyingRole(this, simulator, internalDemandHandler, ypAnswerHandler, quoteHandler,
            orderConfirmationHandler, shipmentHandler, billHandler);
        this.setBuyingRole(buyingRole);

        // ANIMATION

        if (simulator instanceof AnimatorInterface)
        {
            try
            {
                new SingleImageRenderable<>(this, simulator, DemoMarket.class.getResource(
                    "/nl/tudelft/simulation/supplychain/images/Market.gif"));
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
