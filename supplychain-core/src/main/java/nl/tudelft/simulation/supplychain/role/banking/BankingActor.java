package nl.tudelft.simulation.supplychain.role.banking;

import nl.tudelft.simulation.supplychain.actor.Actor;

/**
 * BankingActor is an interface to indicate that an Actor has a BankingRole.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface BankingActor extends Actor
{
    /**
     * Return the BankingRole for this actor.
     * @return the BankingRole for this actor
     */
    BankingRole getBankingRole();

    /**
     * Set the BankingRole for this actor.
     * @param bankingRole the new BankingRole for this actor
     */
    void setBankingRole(BankingRole bankingRole);

}
