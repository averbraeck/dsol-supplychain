package nl.tudelft.simulation.supplychain.role.financing;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.Invoice;
import nl.tudelft.simulation.supplychain.content.Payment;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiver;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.process.AutonomousProcess;
import nl.tudelft.simulation.supplychain.role.banking.BankingRole;
import nl.tudelft.simulation.supplychain.role.financing.process.FixedCostProcess;

/**
 * The FinancingRole manages the bank account of an organization and can take care of paying invoices and receiving money.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class FinancingRole extends Role<FinancingRole>
{
    /** */
    private static final long serialVersionUID = 20230413L;

    /** the bank of the actor. */
    private final BankingRole bank;

    /** the fixed costs for this supply chain actor. */
    private List<FixedCostProcess> fixedCosts = new ArrayList<FixedCostProcess>();

    /** the necessary content handlers. */
    private static Set<Class<? extends Content>> necessaryContentHandlers = Set.of(Invoice.class, Payment.class);

    /** the necessary autonomous processes. */
    private static Set<Class<? extends AutonomousProcess<FinancingRole>>> necessaryAutonomousProcesses =
            Set.of(FixedCostProcess.class);

    /**
     * Create a new FinancingRole with an attached BankAccount.
     * @param id the id of the role
     * @param owner the actor that has this role
     * @param messageReceiver the message handler to use for processing the messages
     * @param bank the that holds the account of this organization
     */
    public FinancingRole(final String id, final FinancingActor owner, final ContentReceiver messageReceiver,
            final BankingRole bank)
    {
        super("financing", owner, messageReceiver);
        this.bank = bank;
    }

    /**
     * Add a fixed cost item for this actor.
     * @param description the description of the fixed cost item
     * @param interval the interval at which the amount will be deduced from the bank account
     * @param amount the amount to deduce at each interval
     */
    public void addFixedCost(final String description, final Duration interval, final Money amount)
    {
        FixedCostProcess fixedCost = new FixedCostProcess(this, description, interval, amount);
        this.fixedCosts.add(fixedCost);
    }

    /**
     * Return the bank of the Actor belonging to this role.
     * @return the bank of the Actor belonging to this role.
     */
    public BankingRole getBank()
    {
        return this.bank;
    }

    /**
     * Return a list of the fixed cost items for this Actor.
     * @return a list of fixed costs items for this Actor.
     */
    public List<FixedCostProcess> getFixedCosts()
    {
        return this.fixedCosts;
    }

    @Override
    public FinancingActor getActor()
    {
        return (FinancingActor) super.getActor();
    }

    @Override
    protected Set<Class<? extends Content>> getNecessaryContentHandlers()
    {
        return necessaryContentHandlers;
    }

    @Override
    protected Set<Class<? extends AutonomousProcess<FinancingRole>>> getNecessaryAutonomousProcesses()
    {
        return necessaryAutonomousProcesses;
    }

}
