package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;

import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.streams.Java2Random;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.supplychain.content.YellowPageAnswer;
import nl.tudelft.simulation.supplychain.content.YellowPageRequest;

/**
 * The YellowPageAnswerHandler implements the business logic for a buyer who receives a YellowPageAnswer from a yellow page
 * supply chain actor. The most simple version that is implemented here, sends out RFQs to <b>all </b> the actors that are
 * reported back inside the YellowPageAnswer. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class YellowPageAnswerHandler extends SupplyChainHandler
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the handling time of the handler in simulation time units */
    protected DistContinuous handlingTime;

    /**
     * Constructs a new YellowPageAnswerHandler.
     * @param owner the owner of the handler
     * @param handlingTime the distribution of the time to react on the YP answer
     */
    public YellowPageAnswerHandler(final SupplyChainActor owner, final DistContinuous handlingTime)
    {
        super(owner);
        this.handlingTime = handlingTime;
    }

    /**
     * Constructs a new YellowPageAnswerHandler.
     * @param owner the owner of the handler
     * @param handlingTime the dconstant time to react on the YP answer
     */
    public YellowPageAnswerHandler(final SupplyChainActor owner, final double handlingTime)
    {
        this(owner, new DistConstant(new Java2Random(), handlingTime));
    }

    /**
     * @see nl.tudelft.simulation.content.HandlerInterface#handleContent(java.io.Serializable)
     */
    public boolean handleContent(final Serializable content)
    {
        YellowPageAnswer ypAnswer = (YellowPageAnswer) checkContent(content);
        if (!isValidContent(ypAnswer))
        {
            return false;
        }
        ContentStoreInterface contentStore = getOwner().getContentStore();
        YellowPageRequest ypRequest = ypAnswer.getYellowPageRequest();
        InternalDemand internalDemand =
                (InternalDemand) contentStore.getContentList(ypRequest.getInternalDemandID(), InternalDemand.class);
        SupplyChainActor[] potentialSuppliers = ypAnswer.getSuppliers();
        double delay = this.handlingTime.draw();
        for (int i = 0; i < potentialSuppliers.length; i++)
        {
            RequestForQuote rfq = new RequestForQuote(getOwner(), potentialSuppliers[i], internalDemand,
                    internalDemand.getProduct(), internalDemand.getAmount(), internalDemand.getEarliestDeliveryDate(),
                    internalDemand.getLatestDeliveryDate());
            getOwner().sendContent(rfq, delay);
        }
        return true;
    }

    /**
     * @see nl.tudelft.simulation.supplychain.handlers.SupplyChainHandler#checkContentClass(java.io.Serializable)
     */
    protected boolean checkContentClass(final Serializable content)
    {
        return (content instanceof YellowPageAnswer);
    }
}
