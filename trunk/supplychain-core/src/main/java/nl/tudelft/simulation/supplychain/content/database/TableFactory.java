package nl.tudelft.simulation.supplychain.content.database;

import java.io.Serializable;

/**
 * Creates TableDescriptors and DataDescriptors <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class TableFactory implements Serializable
{
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /**
     * create a Run table descriptor
     * @return TableDescriptor
     */
    public static TableDescriptor createRunTable()
    {
        TableDescriptor td = new TableDescriptor("run", "runId", "description");
        td.add(new RecordDescriptor("runId", RecordDescriptor.FieldType.VARCHAR, true));
        td.add(new RecordDescriptor("description", RecordDescriptor.FieldType.VARCHAR, false));
        // instantiationTime is VARCHAR -- it needs no conversion
        td.add(new RecordDescriptor("instantiationTime", RecordDescriptor.FieldType.VARCHAR, false));
        td.add(new RecordDescriptor("instantiationTimeString", RecordDescriptor.FieldType.VARCHAR, false));
        return td;
    }

    /**
     * create an InternalDemand table descriptor
     * @return TableDescriptor
     */
    public static TableDescriptor createInternalDemandTable()
    {
        TableDescriptor td = new TableDescriptor("internaldemand", "internalDemandId", "internalDemandId");
        td.add(new RecordDescriptor("internalDemandId", RecordDescriptor.FieldType.VARCHAR, false));
        td.add(new RecordDescriptor("runId", RecordDescriptor.FieldType.VARCHAR, false));
        td.add(new RecordDescriptor("creationTime", RecordDescriptor.FieldType.DOUBLE, false));
        td.add(new RecordDescriptor("sender", RecordDescriptor.FieldType.VARCHAR, false));
        td.add(new RecordDescriptor("product", RecordDescriptor.FieldType.VARCHAR, false));
        td.add(new RecordDescriptor("amount", RecordDescriptor.FieldType.DOUBLE, false));
        td.add(new RecordDescriptor("earliestDeliveryDate", RecordDescriptor.FieldType.DOUBLE, false));
        td.add(new RecordDescriptor("latestDeliveryDate", RecordDescriptor.FieldType.DOUBLE, false));
        return td;
    }

    /**
     * @param td the table descriptor
     */
    private static void addContentFields(final TableDescriptor td)
    {
        td.add(new RecordDescriptor("internalDemandId", RecordDescriptor.FieldType.VARCHAR, false));
        td.add(new RecordDescriptor("runId", RecordDescriptor.FieldType.VARCHAR, false));
        td.add(new RecordDescriptor("creationTime", RecordDescriptor.FieldType.DOUBLE, false));
        td.add(new RecordDescriptor("sender", RecordDescriptor.FieldType.VARCHAR, false));
        td.add(new RecordDescriptor("receiver", RecordDescriptor.FieldType.VARCHAR, false));
    }

    /**
     * create a RequestForQuote table descriptor
     * @return TableDescriptor
     */
    public static TableDescriptor createRequestForQuoteTable()
    {
        TableDescriptor td = new TableDescriptor("requestforquote", "requestForQuoteId", "requestForQuoteId");
        td.add(new RecordDescriptor("requestForQuoteId", RecordDescriptor.FieldType.VARCHAR, false));
        TableFactory.addContentFields(td);
        td.add(new RecordDescriptor("product", RecordDescriptor.FieldType.VARCHAR, false));
        td.add(new RecordDescriptor("amount", RecordDescriptor.FieldType.DOUBLE, false));
        td.add(new RecordDescriptor("earliestDeliveryDate", RecordDescriptor.FieldType.DOUBLE, false));
        td.add(new RecordDescriptor("latestDeliveryDate", RecordDescriptor.FieldType.DOUBLE, false));
        td.add(new RecordDescriptor("cutoffDate", RecordDescriptor.FieldType.DOUBLE, false));
        return td;
    }

    /**
     * create a Quote table descriptor
     * @return TableDescriptor
     */
    public static TableDescriptor createQuoteTable()
    {
        TableDescriptor td = new TableDescriptor("quote", "quoteId", "quoteId");
        td.add(new RecordDescriptor("quoteId", RecordDescriptor.FieldType.VARCHAR, false));
        TableFactory.addContentFields(td);
        td.add(new RecordDescriptor("requestForQuoteId", RecordDescriptor.FieldType.VARCHAR, false));
        td.add(new RecordDescriptor("product", RecordDescriptor.FieldType.VARCHAR, false));
        td.add(new RecordDescriptor("amount", RecordDescriptor.FieldType.DOUBLE, false));
        td.add(new RecordDescriptor("price", RecordDescriptor.FieldType.DOUBLE, false));
        td.add(new RecordDescriptor("proposedShippingDate", RecordDescriptor.FieldType.DOUBLE, false));
        td.add(new RecordDescriptor("validityTime", RecordDescriptor.FieldType.DOUBLE, false));
        td.add(new RecordDescriptor("transportMode", RecordDescriptor.FieldType.VARCHAR, false));
        td.add(new RecordDescriptor("calculatedTransportationTime", RecordDescriptor.FieldType.DOUBLE, false));
        return td;
    }

    /**
     * create an OrderBasedOnQuote table descriptor
     * @return TableDescriptor
     */
    public static TableDescriptor createOrderBasedOnQuoteTable()
    {
        TableDescriptor td = new TableDescriptor("orderbasedonquote", "orderId", "orderId");
        td.add(new RecordDescriptor("orderId", RecordDescriptor.FieldType.VARCHAR, false));
        TableFactory.addContentFields(td);
        td.add(new RecordDescriptor("quoteId", RecordDescriptor.FieldType.VARCHAR, false));
        td.add(new RecordDescriptor("deliveryDate", RecordDescriptor.FieldType.DOUBLE, false));
        return td;
    }

    /**
     * create an OrderStandAlone table descriptor
     * @return TableDescriptor
     */
    public static TableDescriptor createOrderStandAloneTable()
    {
        TableDescriptor td = new TableDescriptor("orderstandalone", "orderId", "orderId");
        td.add(new RecordDescriptor("orderId", RecordDescriptor.FieldType.VARCHAR, false));
        TableFactory.addContentFields(td);
        td.add(new RecordDescriptor("deliveryDate", RecordDescriptor.FieldType.DOUBLE, false));
        td.add(new RecordDescriptor("product", RecordDescriptor.FieldType.VARCHAR, false));
        td.add(new RecordDescriptor("amount", RecordDescriptor.FieldType.DOUBLE, false));
        td.add(new RecordDescriptor("price", RecordDescriptor.FieldType.DOUBLE, false));
        return td;
    }

    /**
     * create an OrderConfirmation table descriptor
     * @return TableDescriptor
     */
    public static TableDescriptor createOrderConfirmationTable()
    {
        TableDescriptor td = new TableDescriptor("orderconfirmation", "orderConfirmationId", "orderConfirmationId");
        td.add(new RecordDescriptor("orderConfirmationId", RecordDescriptor.FieldType.VARCHAR, false));
        TableFactory.addContentFields(td);
        td.add(new RecordDescriptor("orderId", RecordDescriptor.FieldType.VARCHAR, false));
        td.add(new RecordDescriptor("status", RecordDescriptor.FieldType.INTEGER, false));
        return td;
    }

    /**
     * create an Shipment table descriptor
     * @return TableDescriptor
     */
    public static TableDescriptor createShipmentTable()
    {
        TableDescriptor td = new TableDescriptor("shipment", "shipmentId", "shipmentId");
        td.add(new RecordDescriptor("shipmentId", RecordDescriptor.FieldType.VARCHAR, false));
        TableFactory.addContentFields(td);
        td.add(new RecordDescriptor("product", RecordDescriptor.FieldType.VARCHAR, false));
        td.add(new RecordDescriptor("amount", RecordDescriptor.FieldType.DOUBLE, false));
        td.add(new RecordDescriptor("value", RecordDescriptor.FieldType.DOUBLE, false));
        td.add(new RecordDescriptor("orderId", RecordDescriptor.FieldType.VARCHAR, false));
        return td;
    }

    /**
     * create a Bill table descriptor
     * @return TableDescriptor
     */
    public static TableDescriptor createBillTable()
    {
        TableDescriptor td = new TableDescriptor("bill", "billId", "billId");
        td.add(new RecordDescriptor("billId", RecordDescriptor.FieldType.VARCHAR, false));
        TableFactory.addContentFields(td);
        td.add(new RecordDescriptor("price", RecordDescriptor.FieldType.DOUBLE, false));
        td.add(new RecordDescriptor("finalPaymentDate", RecordDescriptor.FieldType.DOUBLE, false));
        td.add(new RecordDescriptor("description", RecordDescriptor.FieldType.VARCHAR, false));
        td.add(new RecordDescriptor("isPaid", RecordDescriptor.FieldType.BOOLEAN, false));
        td.add(new RecordDescriptor("orderId", RecordDescriptor.FieldType.VARCHAR, false));
        return td;
    }

    /**
     * create a Payment table descriptor
     * @return TableDescriptor
     */
    public static TableDescriptor createPaymentTable()
    {
        TableDescriptor td = new TableDescriptor("payment", "paymentId", "paymentId");
        td.add(new RecordDescriptor("paymentId", RecordDescriptor.FieldType.VARCHAR, false));
        TableFactory.addContentFields(td);
        td.add(new RecordDescriptor("payment", RecordDescriptor.FieldType.DOUBLE, false));
        td.add(new RecordDescriptor("billId", RecordDescriptor.FieldType.VARCHAR, false));
        return td;
    }
}
