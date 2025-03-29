package nl.tudelft.simulation.supplychain.role.financing;

import java.util.ArrayList;
import java.util.List;

import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiver;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiverDirect;
import nl.tudelft.simulation.supplychain.finance.BankAccount;
import nl.tudelft.simulation.supplychain.finance.FixedCost;
import nl.tudelft.simulation.supplychain.finance.Money;

/**
 * The FinancingRole manages the bank account of an organization and can take care of paying bills and receiving money.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class FinancingRole extends Role
{
    /** */
    private static final long serialVersionUID = 20230413L;

    /** the bank account of the actor. */
    private final BankAccount bankAccount;

    /** the fixed costs for this supply chain actor. */
    private List<FixedCost> fixedCosts = new ArrayList<FixedCost>();

    /**
     * Create a new FinancingRole with an attached BankAccount.
     * @param id String; the id of the role
     * @param owner FinancingActor; the actor that has this role
     * @param messageReceiver MessageReceiver; the message handler to use for processing the messages
     * @param bankAccount BankAccount; the BankAccount
     */
    public FinancingRole(final String id, final FinancingActor owner, final ContentReceiver messageReceiver,
            final BankAccount bankAccount)
    {
        super("financing", owner, messageReceiver);
        this.bankAccount = bankAccount;
    }

    /**
     * Create a new FinancingRole with an attached BankAccount.
     * @param id String; the id of the role
     * @param owner FinancingActor; the actor that has this role
     * @param bankAccount BankAccount; the BankAccount
     */
    public FinancingRole(final String id, final FinancingActor owner, final BankAccount bankAccount)
    {
        this(id, owner, new ContentReceiverDirect(), bankAccount);
    }

    /**
     * Add a fixed cost item for this actor.
     * @param description String; the description of the fixed cost item
     * @param interval Duration; the interval at which the amount will be deduced from the bank account
     * @param amount Money; the amount to deduce at each interval
     */
    public void addFixedCost(final String description, final Duration interval, final Money amount)
    {
        FixedCost fixedCost = new FixedCost(this, description, interval, amount);
        this.fixedCosts.add(fixedCost);
    }

    /**
     * Return the bank account of the Actor.
     * @return BankAccount; the bankAccount of the Actor.
     */
    public BankAccount getBankAccount()
    {
        return this.bankAccount;
    }

    /**
     * Return a list of the fixed cost items for this Actor.
     * @return List&lt;FixedCosts&gt;; a list of fixed costs items for this Actor.
     */
    public List<FixedCost> getFixedCosts()
    {
        return this.fixedCosts;
    }

}
