package nl.tudelft.simulation.supplychain.role.banking.process;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.supplychain.process.AutonomousProcess;
import nl.tudelft.simulation.supplychain.role.banking.BankingRole;

/**
 * InterestProcess is an autonomous process to provide interest (positive or negative) on the bank acount balance on a
 * day-to-day basis, based on the annual interest rates.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class InterestProcess extends AutonomousProcess<BankingRole>
{
    /**
     * Create the autonomous interest process.
     * @param role the BankingRole to which this process belongs
     */
    public InterestProcess(final BankingRole role)
    {
        super(role);
        role.getSimulator().scheduleEventNow(this, "interest", null);
    }

    /**
     * receive or pay interest according to the current rates. Note that the negative interest rate is stored as a negative
     * number.
     */
    protected void interest()
    {
        for (var account : getRole().getBankAccounts().entrySet())
        {
            if (account.getValue().getAmount() < 0)
            {
                getRole().addToBalance(account.getKey(),
                        account.getValue().multiplyBy(getRole().getAnnualInterestRateNeg() / 365.0));
            }
            else
            {
                getRole().addToBalance(account.getKey(),
                        account.getValue().multiplyBy(getRole().getAnnualInterestRatePos() / 365.0));
            }
        }
        getSimulator().scheduleEventRel(new Duration(1.0, DurationUnit.DAY), this, "interest", null);
    }

}
