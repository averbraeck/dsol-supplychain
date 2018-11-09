package nl.tudelft.simulation.supplychain.ato;

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
 * <p>
 * Copyright (c) 2016 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="http://opentrafficsim.org/docs/license.html">OpenTrafficSim License</a>.
 * </p>
 * $LastChangedDate: 2015-07-24 02:58:59 +0200 (Fri, 24 Jul 2015) $, @version $Revision: 1147 $, by $Author: averbraeck $,
 * initial version Oct 12, 2018 <br>
 * @author <a href="http://www.tbm.tudelft.nl/averbraeck">Alexander Verbraeck</a> 
 * @author <a href="http://https://www.tudelft.nl/tbm/over-de-faculteit/afdelingen/multi-actor-systems/people/phd-candidates/b-bahareh-zohoori/">Bahareh Zohoori</a> 
 */
public class ATOMarket extends Customer
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
    
    // Another constructor to handle demand not through yellowPage? if we want to create another constructor should we add everything again including communication? 
    public ATOMarket(String name, TimeDoubleUnit simulator, Point3d position, Bank bank, Money initialBankAccount,
            Product product, YellowPage ypCustomre, StreamInterface stream)
    {
        super(name, simulator, position, bank, initialBankAccount, new LeanContentStore(simulator));

        // COMMUNICATION
        //should it be different from webapplication of manufacturer? should we name them differently? 

        WebApplication www = new WebApplication("Web-" + name, this.simulator);
        super.addSendingDevice(www);
        MessageHandlerInterface webSystem = new HandleAllMessages(this);
        super.addReceivingDevice(www, webSystem, new DistConstantDurationUnit(new Duration(10.0, DurationUnit.SECOND)));

        // DEMAND GENERATION
// shall we createInternalDeman here? how to change demand to internal demand?
        Demand demand = new Demand(product, new DistContinuousDurationUnit(new DistExponential(stream, 8.0), DurationUnit.HOUR),
                new DistConstant(stream, 1.0), new DistConstantDurationUnit(Duration.ZERO),
                new DistConstantDurationUnit(new Duration(14.0, DurationUnit.DAY)));
        DemandGeneration dg = new DemandGeneration(this, simulator,
                new DistContinuousDurationUnit(new DistExponential(stream, 2.0), DurationUnit.MINUTE));
        dg.addDemandGenerator(product, demand);
        this.setDemandGeneration(dg);
      //why we can not use dg.createInternalDemand? what is the meaning of protection?

        // MESSAGE HANDLING
 
        DistContinuousDurationUnit administrativeDelayInternalDemand =
                new DistContinuousDurationUnit(new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
           // handling massage through yellow page-create yellow page request. what exactly ypCustomer does?
        // why in the Internal DemanHandler we use this? this is the owner of internal demand!
        InternalDemandHandlerYP internalDemandHandler = new InternalDemandHandlerYP(this, administrativeDelayInternalDemand, ypCustomre,
                new Length(1E6, LengthUnit.METER), 1000, null);
        
        //InternalDemandHandlerOrder internalDemandHandlerOrder = new InternalDemandhandlerOrder(this,administrativeDelayInternalDemand, stochInterface Stock?) 

        DistContinuousDurationUnit administrativeDelayYellowPageAnswer =
                new DistContinuousDurationUnit(new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
        // why its not YellowpageRequestHandler?beacuse that handler is taking care of yellowPage stuff(such as search ,...)
        // YellowPageAnswerHandler is the buyer who should reply to the findings of yellowpage
        YellowPageAnswerHandler ypAnswerHandler = new YellowPageAnswerHandler(this, administrativeDelayYellowPageAnswer);

        DistContinuousDurationUnit administrativeDelayQuote =
                new DistContinuousDurationUnit(new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
        // quotes are received from RFQ request        
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
                        ATOMarket.class.getResource("/nl/tudelft/simulation/supplychain/images/Market.gif"));
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



