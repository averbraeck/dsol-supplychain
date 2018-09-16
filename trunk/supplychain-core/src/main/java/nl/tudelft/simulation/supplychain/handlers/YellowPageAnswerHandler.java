package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;
import java.util.List;

import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.supplychain.content.YellowPageAnswer;
import nl.tudelft.simulation.supplychain.content.YellowPageRequest;
import nl.tudelft.simulation.supplychain.contentstore.ContentStoreInterface;
import nl.tudelft.simulation.unit.dist.DistConstantDurationUnit;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;

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
    protected DistContinuousDurationUnit handlingTime;

    /**
     * Constructs a new YellowPageAnswerHandler.
     * @param owner the owner of the handler
     * @param handlingTime the distribution of the time to react on the YP answer
     */
    public YellowPageAnswerHandler(final SupplyChainActor owner, final DistContinuousDurationUnit handlingTime)
    {
        super(owner);
        this.handlingTime = handlingTime;
    }

    /**
     * Constructs a new YellowPageAnswerHandler.
     * @param owner the owner of the handler
     * @param handlingTime the dconstant time to react on the YP answer
     */
    public YellowPageAnswerHandler(final SupplyChainActor owner, final Duration handlingTime)
    {
        this(owner, new DistConstantDurationUnit(handlingTime));
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        if (!isValidContent(content))
        {
            return false;
        }
        YellowPageAnswer ypAnswer = (YellowPageAnswer) content;
        ContentStoreInterface contentStore = getOwner().getContentStore();
        YellowPageRequest ypRequest = ypAnswer.getYellowPageRequest();
        List<InternalDemand> internalDemandList =
                contentStore.getContentList(ypRequest.getInternalDemandID(), InternalDemand.class);
        if (internalDemandList.size() == 0) // we send it to ourselves, so it is 2x in the content store
        {
            Logger.warn("YPAnswerHandler - Actor '{}' could not find InternalDemandID '{}' for YPAnswer '{}'", getOwner().getName(),
                    ypRequest.getInternalDemandID(), ypAnswer.toString());
            return false;
        }
        InternalDemand internalDemand = internalDemandList.get(0);
        List<SupplyChainActor> potentialSuppliers = ypAnswer.getSuppliers();
        Duration delay = this.handlingTime.draw();
        for (SupplyChainActor supplier : potentialSuppliers)
        {
            RequestForQuote rfq = new RequestForQuote(getOwner(), supplier, internalDemand, internalDemand.getProduct(),
                    internalDemand.getAmount(), internalDemand.getEarliestDeliveryDate(),
                    internalDemand.getLatestDeliveryDate());
            getOwner().sendContent(rfq, delay);
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Class<? extends Content> getContentClass()
    {
        return YellowPageAnswer.class;
    }

}
