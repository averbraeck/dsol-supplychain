package nl.tudelft.simulation.supplychain.roles;

import java.io.Serializable;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.banking.BankAccount;
import nl.tudelft.simulation.unit.simulator.DEVSSimulatorInterfaceUnit;

/**
 * The (abstract) buying role is a role that can handle several types of message content: internal demand, order confirmation,
 * bill, and shipment. Depending on the extension of the BuyingRole, which actually indicates the type if InternalDemandHandler
 * used, several other messages can be handled as well. For the InternalDemandHandlerOrder, no extra types are necessary. For
 * the InternalDemandhandlerRFQ, a Quote has to be handled as well. For an InternalDemandhandlerYP, a YellowPageHandler can be
 * received, and has to be handled. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class BuyingRole extends Role
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the bank account to use */
    protected BankAccount bankAccount = null;

    /**
     * Constructs a new BuyingRole
     * @param owner the owner this role
     * @param simulator the simulator to schedule on
     * @param bankAccount the bank account to use
     */
    public BuyingRole(final SupplyChainActor owner, final DEVSSimulatorInterfaceUnit simulator, final BankAccount bankAccount)
    {
        super(owner, simulator);
        this.bankAccount = bankAccount;
        // TODO: add standard handlers for this role
        /*
         * super.addContentHandler(OrderConfirmation.class, new OrderConfirmationHandler(getOwner()));
         * super.addContentHandler(Bill.class, new BillHandler(getOwner(), this.bankAccount, BillHandler.PAYMENT_ON_TIME, new
         * DistConstant(new Java2Random(), 0.0)));
         */
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        /*
         * if (super.resolveContentHandlers(InternalDemand.class).size() == 0) throw new RuntimeException( "BuyingRole does not
         * have InternalDemand handler for actor " + getOwner()); if
         * (super.resolveContentHandlers(OrderConfirmation.class).size() == 0) throw new RuntimeException( "BuyingRole does not
         * have OrderConfirmation handler for actor " + getOwner()); if (super.resolveContentHandlers(Bill.class).size() == 0)
         * throw new RuntimeException( "BuyingRole does not have Bill handler for actor " + getOwner()); if
         * (super.resolveContentHandlers(Shipment.class).size() == 0) throw new RuntimeException( "BuyingRole does not have
         * Shipment handler for actor " + getOwner());
         */
        return super.handleContent(content);
    }
}
