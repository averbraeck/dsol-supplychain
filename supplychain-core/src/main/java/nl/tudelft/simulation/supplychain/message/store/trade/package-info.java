/**
 * MessageStore for instances of TradeMessage. The EmptyTradeMessageStore does not store any messages for retrieval. The
 * TradeMessageStore keeps messages related to an InternalDemand till the entire transaction is over. The LeanTradeMessageStore
 * also looks for timeouts, and removes messages when no reply is given before the timeout. This avoids the problem of the
 * TradeMessageStore that keeps the non-answered conversations eternally.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
package nl.tudelft.simulation.supplychain.message.store.trade;
