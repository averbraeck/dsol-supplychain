package nl.tudelft.simulation.supplychain.role.banking.handler;

import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.supplychain.content.BankTransfer;
import nl.tudelft.simulation.supplychain.handler.ContentHandler;
import nl.tudelft.simulation.supplychain.role.banking.BankingActor;
import nl.tudelft.simulation.supplychain.role.banking.BankingRole;

/**
 * The BankTransferHandler is a simple implementation of the business logic for a BankTransfer that comes in.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class BankTransferHandler extends ContentHandler<BankTransfer, BankingRole>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /**
     * Constructs a new BankTransferHandler.
     * @param owner the owner of the handler.
     */
    public BankTransferHandler(final BankingActor owner)
    {
        super("BankTransferHandler", owner.getBankingRole(), BankTransfer.class);
    }

    @Override
    public boolean handleContent(final BankTransfer bankTransfer)
    {
        if (!isValidContent(bankTransfer))
        {
            return false;
        }
        if (!getRole().equals(bankTransfer.sender().getFinancingRole().getBank()))
        {
            CategoryLogger.always().warn("Bank transfer, but bank != sender's bank");
            return false;
        }

        // Note that sender and payee can each have different banks.
        getRole().withdrawFromBalance(bankTransfer.sender(), bankTransfer.money());
        bankTransfer.payee().getFinancingRole().getBank().addToBalance(bankTransfer.payee(), bankTransfer.money());
        return true;
    }

}
