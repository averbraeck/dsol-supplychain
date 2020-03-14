package nl.tudelft.simulation.supplychain.contentstore.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.TimeUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.event.EventProducer;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Bill;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.content.Order;
import nl.tudelft.simulation.supplychain.content.OrderBasedOnQuote;
import nl.tudelft.simulation.supplychain.content.OrderConfirmation;
import nl.tudelft.simulation.supplychain.content.OrderStandAlone;
import nl.tudelft.simulation.supplychain.content.Payment;
import nl.tudelft.simulation.supplychain.content.Quote;
import nl.tudelft.simulation.supplychain.content.RequestForQuote;
import nl.tudelft.simulation.supplychain.content.Shipment;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.transport.TransportMode;

/**
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DatabaseWorker extends EventProducer implements DatabaseWorkerInterface
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** true for debug */
    private static final boolean DEBUG = false;

    /** the instantiation time of this simulation in milliseconds after 1-1-1970 */
    private long instantiationTime = 0;

    /** the description of the simulation */
    private String description = null;

    /** the database connector */
    private transient SQLDatabaseConnector sqlDatabaseConnector = null;

    /** the runId of the run record of the current run */
    private String runId = "";

    /** the mapping from actor name to SupplyChainActor */
    protected Map<String, SupplyChainActor> supplyChainActorMap = new LinkedHashMap<String, SupplyChainActor>();

    /** the mapping from product name to product */
    protected Map<String, Product> productMap = new LinkedHashMap<String, Product>();

    /** the mapping from transport mode name to transport mode */
    protected Map<String, TransportMode> transportModeMap = new LinkedHashMap<String, TransportMode>();

    /**
     * Constructs a new ContentStore
     * @param description the description of the simulation
     */
    public DatabaseWorker(final String description)
    {
        super();
        this.description = description;
        this.instantiationTime = System.currentTimeMillis();
        if (DatabaseWorker.DEBUG)
        {
            System.err.println("DEBUG -- DatabaseContentStore created at time " + this.instantiationTime + ", description = "
                    + this.description);
        }

        // create the Run record, and store the runId for future use
        this.sqlDatabaseConnector = new SQLDatabaseConnector("gamecontent", "game", "gamepasswd");
        if (this.sqlDatabaseConnector != null)
        {
            Record run = new Record(TableFactory.createRunTable(), this.sqlDatabaseConnector);
            run.setValue("description", this.description);
            run.setValue("instantiationTime", "" + this.instantiationTime);
            run.setValue("instantiationTimeString", CalendarUtility.formatDateTime(this.instantiationTime));
            run.addRecord();
            this.runId = run.getValue("runId");
            if (DatabaseWorker.DEBUG)
            {
                System.err.println("DEBUG -- run.instantiationTime = " + run.getValue("instantiationTimeString"));
                System.err.println("DEBUG -- run.runId = " + this.runId);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addContent(Content content, boolean sent)
    {
        // just dump it into the database
        if (content instanceof InternalDemand)
        {
            addInternalDemand((InternalDemand) content);
        }
        else if (content instanceof RequestForQuote)
        {
            addRequestForQuote((RequestForQuote) content);
        }
        else if (content instanceof Quote)
        {
            addQuote((Quote) content);
        }
        else if (content instanceof OrderBasedOnQuote)
        {
            addOrderBasedOnQuote((OrderBasedOnQuote) content);
        }
        else if (content instanceof OrderStandAlone)
        {
            addOrderStandAlone((OrderStandAlone) content);
        }
        else if (content instanceof OrderConfirmation)
        {
            addOrderConfirmation((OrderConfirmation) content);
        }
        else if (content instanceof Shipment)
        {
            addShipment((Shipment) content);
        }
        else if (content instanceof Bill)
        {
            addBill((Bill) content);
        }
        else if (content instanceof Payment)
        {
            addPayment((Payment) content);
        }
        else
        {
            // TODO: normal error handling later
            System.err.println("addContent -- unknown content: " + content);
            new Exception("addContent -- unknown content: " + content).printStackTrace();
        }
    }

    /**
     * @param record
     * @param content
     */
    private void fillStandard(final Record record, final Content content)
    {
        record.setValue("runId", this.runId);
        record.setValue("internalDemandId", content.getInternalDemandID().toString());
        this.supplyChainActorMap.put(content.getSender().getName(), content.getSender());
        this.supplyChainActorMap.put(content.getReceiver().getName(), content.getReceiver());
    }

    /**
     * @param content
     */
    private void addInternalDemand(final InternalDemand content)
    {
        Record record = new Record(TableFactory.createInternalDemandTable(), this.sqlDatabaseConnector);
        record.fillFromReflection(content);
        record.setValue("runId", this.runId);
        record.setValue("internalDemandId", content.getUniqueID().toString());
        record.addRecordIgnoreDuplicate();
        this.supplyChainActorMap.put(content.getSender().getName(), content.getSender());
        this.productMap.put(content.getProduct().getName(), content.getProduct());
    }

    /**
     * @param content
     */
    private void addRequestForQuote(final RequestForQuote content)
    {
        Record record = new Record(TableFactory.createRequestForQuoteTable(), this.sqlDatabaseConnector);
        record.fillFromReflection(content);
        this.fillStandard(record, content);
        record.setValue("requestForQuoteId", content.getUniqueID().toString());
        record.addRecordIgnoreDuplicate();
        this.productMap.put(content.getProduct().getName(), content.getProduct());
    }

    /**
     * @param content
     */
    private void addQuote(final Quote content)
    {
        Record record = new Record(TableFactory.createQuoteTable(), this.sqlDatabaseConnector);
        record.fillFromReflection(content);
        this.fillStandard(record, content);
        record.setValue("quoteId", content.getUniqueID().toString());
        record.setValue("requestForQuoteId", content.getRequestForQuote().getUniqueID().toString());
        record.addRecordIgnoreDuplicate();
        this.productMap.put(content.getProduct().getName(), content.getProduct());
        this.transportModeMap.put(content.getTransportMode().toString(), content.getTransportMode());
    }

    /**
     * @param content
     */
    private void addOrderBasedOnQuote(final OrderBasedOnQuote content)
    {
        Record record = new Record(TableFactory.createOrderBasedOnQuoteTable(), this.sqlDatabaseConnector);
        record.fillFromReflection(content);
        this.fillStandard(record, content);
        record.setValue("orderId", content.getUniqueID().toString());
        record.setValue("quoteId", content.getQuote().getUniqueID().toString());
        record.addRecordIgnoreDuplicate();
        this.productMap.put(content.getProduct().getName(), content.getProduct());
    }

    /**
     * @param content
     */
    private void addOrderStandAlone(final OrderStandAlone content)
    {
        Record record = new Record(TableFactory.createOrderStandAloneTable(), this.sqlDatabaseConnector);
        record.fillFromReflection(content);
        this.fillStandard(record, content);
        record.setValue("orderId", content.getUniqueID().toString());
        record.addRecordIgnoreDuplicate();
        this.productMap.put(content.getProduct().getName(), content.getProduct());
    }

    /**
     * @param content
     */
    private void addShipment(final Shipment content)
    {
        Record record = new Record(TableFactory.createShipmentTable(), this.sqlDatabaseConnector);
        record.fillFromReflection(content);
        this.fillStandard(record, content);
        record.setValue("shipmentId", content.getUniqueID().toString());
        record.setValue("orderId", content.getOrder().getUniqueID().toString());
        record.addRecordIgnoreDuplicate();
        this.productMap.put(content.getProduct().getName(), content.getProduct());
    }

    /**
     * @param content
     */
    private void addBill(final Bill content)
    {
        Record record = new Record(TableFactory.createBillTable(), this.sqlDatabaseConnector);
        record.fillFromReflection(content);
        this.fillStandard(record, content);
        record.setValue("billId", content.getUniqueID().toString());
        record.setValue("orderId", content.getOrder().getUniqueID().toString());
        record.addRecordIgnoreDuplicate();
        this.productMap.put(content.getProduct().getName(), content.getProduct());
    }

    /**
     * @param content
     */
    private void addPayment(final Payment content)
    {
        Record record = new Record(TableFactory.createPaymentTable(), this.sqlDatabaseConnector);
        record.fillFromReflection(content);
        this.fillStandard(record, content);
        record.setValue("paymentId", content.getUniqueID().toString());
        record.setValue("billId", content.getBill().getUniqueID().toString());
        record.addRecordIgnoreDuplicate();
        this.productMap.put(content.getProduct().getName(), content.getProduct());
    }

    /**
     * @param content
     */
    private void addOrderConfirmation(final OrderConfirmation content)
    {
        Record record = new Record(TableFactory.createOrderConfirmationTable(), this.sqlDatabaseConnector);
        record.fillFromReflection(content);
        this.fillStandard(record, content);
        record.setValue("orderConfirmationId", content.getUniqueID().toString());
        record.setValue("orderId", content.getOrder().getUniqueID().toString());
        record.addRecordIgnoreDuplicate();
    }

    /** {@inheritDoc} */
    @Override
    public void removeContent(Content content, boolean sent)
    {
        // ignore -- we want the database to retain all information
    }

    /** {@inheritDoc} */
    @Override
    public void removeSentReceivedContent(Content content, boolean sent)
    {
        // ignore -- we want the database to retain all information
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllContent(Serializable internalDemandID)
    {
        // ignore -- we want the database to retain all information
    }

    /** {@inheritDoc} */
    @Override
    public <C extends Content> List<C> getContentList(Serializable internalDemandID, Class<C> clazz, String actorName)
    {
        List<C> contentList = this.getContentList(internalDemandID, clazz, actorName, true);
        contentList.addAll(this.getContentList(internalDemandID, clazz, actorName, false));
        return contentList;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public <C extends Content> List<C> getContentList(Serializable internalDemandID, Class<C> clazz, String actorName,
            boolean sent)
    {
        List<C> contentList = new ArrayList<>();
        RecordList<Record> recordList = null;
        TableDescriptor td = null;
        if (InternalDemand.class.isAssignableFrom(clazz))
        {
            td = TableFactory.createInternalDemandTable();
        }
        else if (RequestForQuote.class.isAssignableFrom(clazz))
        {
            td = TableFactory.createRequestForQuoteTable();
        }
        else if (Quote.class.isAssignableFrom(clazz))
        {
            td = TableFactory.createQuoteTable();
        }
        else if (OrderBasedOnQuote.class.isAssignableFrom(clazz))
        {
            td = TableFactory.createOrderBasedOnQuoteTable();
        }
        else if (OrderStandAlone.class.isAssignableFrom(clazz))
        {
            td = TableFactory.createOrderStandAloneTable();
        }
        else if (OrderConfirmation.class.isAssignableFrom(clazz))
        {
            td = TableFactory.createOrderConfirmationTable();
        }
        else if (Shipment.class.isAssignableFrom(clazz))
        {
            td = TableFactory.createShipmentTable();
        }
        else if (Bill.class.isAssignableFrom(clazz))
        {
            td = TableFactory.createBillTable();
        }
        else if (Payment.class.isAssignableFrom(clazz))
        {
            td = TableFactory.createPaymentTable();
        }
        else
        {
            // TODO: normal error handling later
            System.err.println("addContent -- unknown content class: " + clazz.getSimpleName());
            return contentList;
        }

        // add the "WHERE" clauses, for runId and actorName
        String whereString =
                "( `runId` = " + this.runId + " ) AND ( `internalDemandId` = \"" + internalDemandID.toString() + "\" ) AND ( ";
        if (sent)
        {
            whereString += "`sender` = \"" + actorName + "\" )";
        }
        else
        {
            whereString += "`receiver` = \"" + actorName + "\" )";
        }
        td.addWhereString(whereString);
        recordList = new RecordList<Record>(td, this.sqlDatabaseConnector);
        recordList.read();

        // re-create the messages and put them in the list
        for (Record record : recordList)
        {
            if (InternalDemand.class.isAssignableFrom(clazz))
            {
                contentList.add((C) parseInternalDemand(record));
            }
            else if (RequestForQuote.class.isAssignableFrom(clazz))
            {
                contentList.add((C) parseRequestForQuote(record));
            }
            else if (Quote.class.isAssignableFrom(clazz))
            {
                contentList.add((C) parseQuote(record));
            }
            else if (OrderBasedOnQuote.class.isAssignableFrom(clazz))
            {
                contentList.add((C) parseOrderBasedOnQuote(record));
            }
            else if (OrderStandAlone.class.isAssignableFrom(clazz))
            {
                contentList.add((C) parseOrderStandAlone(record));
            }
            else if (OrderConfirmation.class.isAssignableFrom(clazz))
            {
                contentList.add((C) parseOrderConfirmation(record));
            }
            else if (Shipment.class.isAssignableFrom(clazz))
            {
                contentList.add((C) parseShipment(record));
            }
            else if (Bill.class.isAssignableFrom(clazz))
            {
                contentList.add((C) parseBill(record));
            }
            else if (Payment.class.isAssignableFrom(clazz))
            {
                contentList.add((C) parsePayment(record));
            }
        }
        return contentList;
    }

    /**
     * @param record the record to find and convert the value of the field
     * @param fieldName the field to look for
     * @return the double value
     */
    private double doubleValue(final Record record, final String fieldName)
    {
        double retValue = Double.NaN;
        if (record.getValues().containsKey(fieldName))
        {
            try
            {
                String retString = record.getValue(fieldName);
                retValue = Double.parseDouble(retString);
            }
            catch (Exception exception)
            {
                System.err.println("Record contains invalid field " + fieldName);
                exception.printStackTrace();
            }
        }
        else
        {
            System.err.println("Record does not contain field " + fieldName);
        }
        return retValue;
    }

    /**
     * @param record the record to find and convert the value of the field
     * @param fieldName the field to look for
     * @return the long value
     */
    @SuppressWarnings("unused")
    private long longValue(final Record record, final String fieldName)
    {
        long retValue = Long.MIN_VALUE;
        if (record.getValues().containsKey(fieldName))
        {
            try
            {
                String retString = record.getValue(fieldName);
                retValue = Long.parseLong(retString);
            }
            catch (Exception exception)
            {
                System.err.println("Record contains invalid field " + fieldName);
                exception.printStackTrace();
            }
        }
        else
        {
            System.err.println("Record does not contain field " + fieldName);
        }
        return retValue;
    }

    /**
     * @param record the record to find and convert the value of the field
     * @param fieldName the field to look for
     * @return the int value
     */
    private int intValue(final Record record, final String fieldName)
    {
        int retValue = Integer.MIN_VALUE;
        if (record.getValues().containsKey(fieldName))
        {
            try
            {
                String retString = record.getValue(fieldName);
                retValue = Integer.parseInt(retString);
            }
            catch (Exception exception)
            {
                System.err.println("Record contains invalid field " + fieldName);
                exception.printStackTrace();
            }
        }
        else
        {
            System.err.println("Record does not contain field " + fieldName);
        }
        return retValue;
    }

    /**
     * @param record the record to find and convert the value of the field
     * @param fieldName the field to look for
     * @return the value as a Duration
     */
    @SuppressWarnings("unused")
    private Duration durationValue(final Record record, final String fieldName)
    {
        // TODO: store the unit!
        Duration retValue = Duration.NaN;
        if (record.getValues().containsKey(fieldName))
        {
            try
            {
                String retString = record.getValue(fieldName);
                double d = Double.parseDouble(retString);
                retValue = new Duration(d, DurationUnit.SI);
            }
            catch (Exception exception)
            {
                System.err.println("Record contains invalid field " + fieldName);
                exception.printStackTrace();
            }
        }
        else
        {
            System.err.println("Record does not contain field " + fieldName);
        }
        return retValue;
    }

    /**
     * @param record the record to find and convert the value of the field
     * @param fieldName the field to look for
     * @return the value as a Time
     */
    private Time timeValue(final Record record, final String fieldName)
    {
        // TODO: store the unit!
        Time retValue = Time.ZERO;
        if (record.getValues().containsKey(fieldName))
        {
            try
            {
                String retString = record.getValue(fieldName);
                double d = Double.parseDouble(retString);
                retValue = new Time(d, TimeUnit.BASE_SECOND);
            }
            catch (Exception exception)
            {
                System.err.println("Record contains invalid field " + fieldName);
                exception.printStackTrace();
            }
        }
        else
        {
            System.err.println("Record does not contain field " + fieldName);
        }
        return retValue;
    }

    /**
     * @param record the record to find and convert the value of the field
     * @param fieldName the field to look for
     * @return the value as a Duration
     */
    private Money moneyValue(final Record record, final String fieldName)
    {
        // TODO: store the unit!
        Money retValue = new Money(Double.NaN, MoneyUnit.USD);
        if (record.getValues().containsKey(fieldName))
        {
            try
            {
                String retString = record.getValue(fieldName);
                double d = Double.parseDouble(retString);
                retValue = new Money(d, MoneyUnit.USD);
            }
            catch (Exception exception)
            {
                System.err.println("Record contains invalid field " + fieldName);
                exception.printStackTrace();
            }
        }
        else
        {
            System.err.println("Record does not contain field " + fieldName);
        }
        return retValue;
    }

    /**
     * @param id
     * @return the internal demand content
     */
    private InternalDemand readInternalDemand(final String id)
    {
        Record record = new Record(TableFactory.createInternalDemandTable(), this.sqlDatabaseConnector);
        record.readRecord(id);
        return parseInternalDemand(record);
    }

    /**
     * @param record
     * @return the internal demand content
     */
    private InternalDemand parseInternalDemand(final Record record)
    {
        InternalDemand content = new InternalDemand(parseSender(record), parseProduct(record), doubleValue(record, "amount"),
                timeValue(record, "earliestDeliveryDate"), timeValue(record, "latestDeliveryDate"));
        content.setUniqueID(record.getValue("internalDemandId"));
        return content;
    }

    /**
     * @param id
     * @return the rfq content
     */
    private RequestForQuote readRequestForQuote(final String id)
    {
        Record record = new Record(TableFactory.createRequestForQuoteTable(), this.sqlDatabaseConnector);
        record.readRecord(id);
        return parseRequestForQuote(record);
    }

    /**
     * @param record
     * @return the rfq content
     */
    private RequestForQuote parseRequestForQuote(final Record record)
    {
        InternalDemand internalDemand = this.readInternalDemand(record.getValue("internalDemandId"));
        RequestForQuote content = new RequestForQuote(parseSender(record), parseReceiver(record), internalDemand,
                parseProduct(record), doubleValue(record, "amount"), timeValue(record, "earliestDeliveryDate"),
                timeValue(record, "latestDeliveryDate"));
        content.setUniqueID(record.getValue("requestForQuoteId"));
        return content;
    }

    /**
     * @param id
     * @return the quote content
     */
    private Quote readQuote(final String id)
    {
        Record record = new Record(TableFactory.createQuoteTable(), this.sqlDatabaseConnector);
        record.readRecord(id);
        return parseQuote(record);
    }

    /**
     * @param record
     * @return the quote content
     */
    private Quote parseQuote(final Record record)
    {
        InternalDemand internalDemand = this.readInternalDemand(record.getValue("internalDemandId"));
        RequestForQuote rfq = this.readRequestForQuote(record.getValue("requestForQuoteId"));
        Quote content = new Quote(parseSender(record), parseReceiver(record), internalDemand, rfq, parseProduct(record),
                doubleValue(record, "amount"), moneyValue(record, "price"), timeValue(record, "proposedShippingDate"),
                timeValue(record, "validityTime"), parseTransportMode(record));
        content.setUniqueID(record.getValue("quoteId"));
        return content;
    }

    /**
     * @param id
     * @return the order content
     */
    private OrderBasedOnQuote readOrderBasedOnQuote(final String id)
    {
        Record record = new Record(TableFactory.createOrderBasedOnQuoteTable(), this.sqlDatabaseConnector);
        record.readRecord(id);
        return parseOrderBasedOnQuote(record);
    }

    /**
     * @param record
     * @return the order content
     */
    private OrderBasedOnQuote parseOrderBasedOnQuote(final Record record)
    {
        InternalDemand internalDemand = this.readInternalDemand(record.getValue("internalDemandId"));
        Quote quote = this.readQuote(record.getValue("quoteId"));
        OrderBasedOnQuote content = new OrderBasedOnQuote(parseSender(record), parseReceiver(record), internalDemand,
                timeValue(record, "deliveryDate"), quote);
        content.setUniqueID(record.getValue("orderId"));
        return content;
    }

    /**
     * @param id
     * @return the order content
     */
    @SuppressWarnings("unused")
    private OrderStandAlone readOrderStandAlone(final String id)
    {
        Record record = new Record(TableFactory.createOrderStandAloneTable(), this.sqlDatabaseConnector);
        record.readRecord(id);
        return parseOrderStandAlone(record);
    }

    /**
     * @param record
     * @return the order content
     */
    private OrderStandAlone parseOrderStandAlone(final Record record)
    {
        InternalDemand internalDemand = this.readInternalDemand(record.getValue("internalDemandId"));
        OrderStandAlone content = new OrderStandAlone(parseSender(record), parseReceiver(record), internalDemand,
                timeValue(record, "deliveryDate"), parseProduct(record), doubleValue(record, "amount"),
                moneyValue(record, "price"));
        content.setUniqueID(record.getValue("orderId"));
        return content;
    }

    /**
     * @param id
     * @return the orderconfirmation content
     */
    @SuppressWarnings("unused")
    private OrderConfirmation readOrderConfirmation(final String id)
    {
        Record record = new Record(TableFactory.createOrderConfirmationTable(), this.sqlDatabaseConnector);
        record.readRecord(id);
        return parseOrderConfirmation(record);
    }

    /**
     * @param record
     * @return the orderconfirmation content
     */
    private OrderConfirmation parseOrderConfirmation(final Record record)
    {
        InternalDemand internalDemand = this.readInternalDemand(record.getValue("internalDemandId"));
        Order order = this.readOrderBasedOnQuote(record.getValue("orderId"));
        OrderConfirmation content = new OrderConfirmation(parseSender(record), parseReceiver(record), internalDemand, order,
                this.intValue(record, "status"));
        content.setUniqueID(record.getValue("orderConfirmationId"));
        return content;
    }

    /**
     * @param id
     * @return the shipment content
     */
    @SuppressWarnings("unused")
    private Shipment readShipment(final String id)
    {
        Record record = new Record(TableFactory.createShipmentTable(), this.sqlDatabaseConnector);
        record.readRecord(id);
        return parseShipment(record);
    }

    /**
     * @param record
     * @return the shipment content
     */
    private Shipment parseShipment(final Record record)
    {
        InternalDemand internalDemand = this.readInternalDemand(record.getValue("internalDemandId"));
        Order order = this.readOrderBasedOnQuote(record.getValue("orderId"));
        Shipment content = new Shipment(parseSender(record), parseReceiver(record), internalDemand, order, parseProduct(record),
                doubleValue(record, "amount"), moneyValue(record, "totalCargoValue"));
        content.setUniqueID(record.getValue("shipmentId"));
        return content;
    }

    /**
     * @param id
     * @return the bill content
     */
    private Bill readBill(final String id)
    {
        Record record = new Record(TableFactory.createBillTable(), this.sqlDatabaseConnector);
        record.readRecord(id);
        return parseBill(record);
    }

    /**
     * @param record
     * @return the bill content
     */
    private Bill parseBill(final Record record)
    {
        InternalDemand internalDemand = this.readInternalDemand(record.getValue("internalDemandId"));
        Order order = this.readOrderBasedOnQuote(record.getValue("orderId"));
        Bill content = new Bill(parseSender(record), parseReceiver(record), internalDemand, order,
                timeValue(record, "finalPaymentDate"), moneyValue(record, "price"), record.getValue("description"));
        content.setUniqueID(record.getValue("billId"));
        return content;
    }

    /**
     * @param id
     * @return the payment content
     */
    protected Payment readPayment(final String id)
    {
        Record record = new Record(TableFactory.createPaymentTable(), this.sqlDatabaseConnector);
        record.readRecord(id);
        return parsePayment(record);
    }

    /**
     * @param record
     * @return the payment content
     */
    private Payment parsePayment(final Record record)
    {
        InternalDemand internalDemand = this.readInternalDemand(record.getValue("internalDemandId"));
        Bill bill = this.readBill(record.getValue("billId"));
        Payment content =
                new Payment(parseSender(record), parseReceiver(record), internalDemand, bill, moneyValue(record, "payment"));
        content.setUniqueID(record.getValue("paymentId"));
        return content;
    }

    /**
     * @param record
     * @return the sender
     */
    private SupplyChainActor parseSender(final Record record)
    {
        SupplyChainActor sender = null;
        if (record.getValues().containsKey("sender"))
        {
            sender = this.supplyChainActorMap.get(record.getValue("sender"));
        }
        return sender;
    }

    /**
     * @param record
     * @return the receiver
     */
    private SupplyChainActor parseReceiver(final Record record)
    {
        SupplyChainActor receiver = null;
        if (record.getValues().containsKey("receiver"))
        {
            receiver = this.supplyChainActorMap.get(record.getValue("receiver"));
        }
        return receiver;
    }

    /**
     * @param record
     * @return the product
     */
    private Product parseProduct(final Record record)
    {
        Product product = null;
        if (record.getValues().containsKey("product"))
        {
            product = this.productMap.get(record.getValue("product"));
        }
        return product;
    }

    /**
     * @param record
     * @return the transport mode
     */
    private TransportMode parseTransportMode(final Record record)
    {
        TransportMode transportMode = null;
        if (record.getValues().containsKey("transportMode"))
        {
            transportMode = this.transportModeMap.get(record.getValue("transportMode"));
        }
        return transportMode;
    }

    /**
     * @return the instantiation time
     */
    public long getInstantiationTime()
    {
        return this.instantiationTime;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return this.description;
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return "DatabaseWorker";
    }
}
