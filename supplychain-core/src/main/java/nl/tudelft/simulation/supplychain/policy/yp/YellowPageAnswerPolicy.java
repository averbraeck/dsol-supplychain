package nl.tudelft.simulation.supplychain.policy.yp;

import java.io.Serializable;
import java.util.List;

import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.actor.unit.dist.DistConstantDuration;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.supplychain.content.YellowPageAnswer;
import nl.tudelft.simulation.supplychain.content.YellowPageRequest;
import nl.tudelft.simulation.supplychain.contentstore.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.policy.SupplyChainHandler;

/**
 * The YellowPageAnswerHandler implements the business logic for a buyer who receives a YellowPageAnswer from a yellow page
 * supply chain actor. The most simple version that is implemented here, sends out RFQs to <b>all </b> the actors that are
 * reported back inside the YellowPageAnswer.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class YellowPageAnswerPolicy extends SupplyChainHandler
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the handling time of the handler in simulation time units */
    protected DistContinuousDuration handlingTime;

    /**
     * Constructs a new YellowPageAnswerHandler.
     * @param owner the owner of the handler
     * @param handlingTime the distribution of the time to react on the YP answer
     */
    public YellowPageAnswerPolicy(final SupplyChainActor owner, final DistContinuousDuration handlingTime)
    {
        super(owner);
        this.handlingTime = handlingTime;
    }

    /**
     * Constructs a new YellowPageAnswerHandler.
     * @param owner the owner of the handler
     * @param handlingTime the dconstant time to react on the YP answer
     */
    public YellowPageAnswerPolicy(final SupplyChainActor owner, final Duration handlingTime)
    {
        this(owner, new DistConstantDuration(handlingTime));
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
            Logger.warn("YPAnswerHandler - Actor '{}' could not find InternalDemandID '{}' for YPAnswer '{}'",
                    getOwner().getName(), ypRequest.getInternalDemandID(), ypAnswer.toString());
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
