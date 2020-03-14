package nl.tudelft.simulation.supplychain.roles;

import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Bill;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.content.OrderConfirmation;
import nl.tudelft.simulation.supplychain.content.Quote;
import nl.tudelft.simulation.supplychain.content.Shipment;
import nl.tudelft.simulation.supplychain.content.YellowPageAnswer;
import nl.tudelft.simulation.supplychain.policy.bill.BillPolicy;
import nl.tudelft.simulation.supplychain.policy.internaldemand.InternalDemandPolicy;
import nl.tudelft.simulation.supplychain.policy.internaldemand.InternalDemandPolicyOrder;
import nl.tudelft.simulation.supplychain.policy.internaldemand.InternalDemandPolicyRFQ;
import nl.tudelft.simulation.supplychain.policy.internaldemand.InternalDemandPolicyYP;
import nl.tudelft.simulation.supplychain.policy.orderconfirmation.OrderConfirmationPolicy;
import nl.tudelft.simulation.supplychain.policy.quote.QuotePolicy;
import nl.tudelft.simulation.supplychain.policy.shipment.ShipmentPolicy;
import nl.tudelft.simulation.supplychain.policy.yp.YellowPageAnswerPolicy;

/**
 * The buying role is a role that can handle several types of message content: internal demand, order confirmation, bill, and
 * shipment. Depending on the extension of the BuyingRole, which actually indicates the type if InternalDemandHandler used,
 * several other messages can be handled as well. For the InternalDemandHandlerOrder, no extra types are necessary. For the
 * InternalDemandhandlerRFQ, a Quote has to be handled as well. For an InternalDemandhandlerYP, a YellowPageAnswer can be
 * received, and has to be handled. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class BuyingRole extends Role implements BuyingRoleInterface
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /**
     * Constructs a new BuyingRole for Generic Demand - Confirmation - Shipment - Bill.
     * @param owner the owner this role
     * @param simulator the simulator to schedule on
     * @param internalDemandHandler the internal demand handler
     * @param orderConfirmationHandler the order confirmation handler
     * @param shipmentHandler the shipment handler
     * @param billHandler the bill handler
     */
    public BuyingRole(final SupplyChainActor owner, final DEVSSimulatorInterface.TimeDoubleUnit simulator,
            final InternalDemandPolicy internalDemandHandler, final OrderConfirmationPolicy orderConfirmationHandler,
            final ShipmentPolicy shipmentHandler, final BillPolicy billHandler)
    {
        super(owner, owner.getName() + "-BUYING", simulator);
        addContentHandler(InternalDemand.class, internalDemandHandler);
        addContentHandler(OrderConfirmation.class, orderConfirmationHandler);
        addContentHandler(Shipment.class, shipmentHandler);
        addContentHandler(Bill.class, billHandler);
    }

    /**
     * Constructs a new BuyingRole for Order-based Demand - Confirmation - Shipment - Bill.
     * @param owner the owner this role
     * @param simulator the simulator to schedule on
     * @param internalDemandHandler the internal demand handler, based on an order
     * @param orderConfirmationHandler the order confirmation handler
     * @param shipmentHandler the shipment handler
     * @param billHandler the bill handler
     */
    public BuyingRole(final SupplyChainActor owner, final DEVSSimulatorInterface.TimeDoubleUnit simulator,
            final InternalDemandPolicyOrder internalDemandHandler, final OrderConfirmationPolicy orderConfirmationHandler,
            final ShipmentPolicy shipmentHandler, final BillPolicy billHandler)
    {
        super(owner, owner.getName() + "-BUYING", simulator);
        addContentHandler(InternalDemand.class, internalDemandHandler);
        addContentHandler(OrderConfirmation.class, orderConfirmationHandler);
        addContentHandler(Shipment.class, shipmentHandler);
        addContentHandler(Bill.class, billHandler);
    }

    /**
     * Constructs a new BuyingRole for Demand - Quote - Confirmation - Shipment - Bill.
     * @param owner the owner this role
     * @param simulator the simulator to schedule on
     * @param internalDemandHandler the internal demand handler, results in ending out an RFQ
     * @param quoteHandler the quote handler
     * @param orderConfirmationHandler the order confirmation handler
     * @param shipmentHandler the shipment handler
     * @param billHandler the bill handler
     */
    public BuyingRole(final SupplyChainActor owner, final DEVSSimulatorInterface.TimeDoubleUnit simulator,
            final InternalDemandPolicyRFQ internalDemandHandler, final QuotePolicy quoteHandler,
            final OrderConfirmationPolicy orderConfirmationHandler, final ShipmentPolicy shipmentHandler,
            final BillPolicy billHandler)
    {
        super(owner, owner.getName() + "-BUYING", simulator);
        addContentHandler(InternalDemand.class, internalDemandHandler);
        addContentHandler(Quote.class, quoteHandler);
        addContentHandler(OrderConfirmation.class, orderConfirmationHandler);
        addContentHandler(Shipment.class, shipmentHandler);
        addContentHandler(Bill.class, billHandler);
    }

    /**
     * Constructs a new BuyingRole for Demand - YPAnswer - Quote - Confirmation - Shipment - Bill.
     * @param owner the owner this role
     * @param simulator the simulator to schedule on
     * @param internalDemandHandler the internal demand handler, results in ending out an RFQ
     * @param ypAnswerHandler the yellow page answer handler
     * @param quoteHandler the quote handler
     * @param orderConfirmationHandler the order confirmation handler
     * @param shipmentHandler the shipment handler
     * @param billHandler the bill handler
     */
    public BuyingRole(final SupplyChainActor owner, final DEVSSimulatorInterface.TimeDoubleUnit simulator,
            final InternalDemandPolicyYP internalDemandHandler, final YellowPageAnswerPolicy ypAnswerHandler,
            final QuotePolicy quoteHandler, final OrderConfirmationPolicy orderConfirmationHandler,
            final ShipmentPolicy shipmentHandler, final BillPolicy billHandler)
    {
        super(owner, owner.getName() + "-BUYING", simulator);
        addContentHandler(InternalDemand.class, internalDemandHandler);
        addContentHandler(YellowPageAnswer.class, ypAnswerHandler);
        addContentHandler(Quote.class, quoteHandler);
        addContentHandler(OrderConfirmation.class, orderConfirmationHandler);
        addContentHandler(Shipment.class, shipmentHandler);
        addContentHandler(Bill.class, billHandler);
    }

}
