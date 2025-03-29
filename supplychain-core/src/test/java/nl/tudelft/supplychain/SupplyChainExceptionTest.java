package nl.tudelft.supplychain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.tudelft.simulation.supplychain.SupplyChainException;
import nl.tudelft.simulation.supplychain.SupplyChainRuntimeException;

/**
 * SupplyChainExceptionTest tests the generic exceptions in the supply chain project.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SupplyChainExceptionTest
{

    /**
     * Test SupplyChainException.
     */
    @Test
    public void testSupplyChainException()
    {
        Exception e1 = new SupplyChainException();
        assertEquals("Supply chain exception", e1.getMessage());
        assertEquals(null, e1.getCause());
        assertEquals("testSupplyChainException", e1.getStackTrace()[0].getMethodName());

        e1 = new SupplyChainException("error");
        assertEquals("error", e1.getMessage());
        assertEquals(null, e1.getCause());
        assertEquals("testSupplyChainException", e1.getStackTrace()[0].getMethodName());

        e1 = new SupplyChainException(new RuntimeException("rte"));
        assertTrue(e1.getMessage().contains("Supply chain exception"));
        assertEquals(RuntimeException.class, e1.getCause().getClass());
        assertEquals("testSupplyChainException", e1.getStackTrace()[0].getMethodName());
        assertEquals("rte", e1.getCause().getMessage());
        assertEquals(null, e1.getCause().getCause());

        e1 = new SupplyChainException("error", new RuntimeException("rte"));
        assertEquals("error", e1.getMessage());
        assertEquals(RuntimeException.class, e1.getCause().getClass());
        assertEquals("testSupplyChainException", e1.getStackTrace()[0].getMethodName());
        assertEquals("rte", e1.getCause().getMessage());
        assertEquals(null, e1.getCause().getCause());

        e1 = new SupplyChainException("error", new RuntimeException("rte"), false, true);
        assertEquals("error", e1.getMessage());
        assertEquals(RuntimeException.class, e1.getCause().getClass());
        assertEquals("testSupplyChainException", e1.getStackTrace()[0].getMethodName());
        assertEquals("rte", e1.getCause().getMessage());
        assertEquals(null, e1.getCause().getCause());
        assertEquals(0, e1.getSuppressed().length);
    }
    
    /**
     * Test SupplyChainRuntimeException.
     */
    @Test
    public void testSupplyChainRuntimeException()
    {
        Exception e1 = new SupplyChainRuntimeException();
        assertEquals("Supply chain runtime exception", e1.getMessage());
        assertEquals(null, e1.getCause());
        assertEquals("testSupplyChainRuntimeException", e1.getStackTrace()[0].getMethodName());

        e1 = new SupplyChainRuntimeException("error");
        assertEquals("error", e1.getMessage());
        assertEquals(null, e1.getCause());
        assertEquals("testSupplyChainRuntimeException", e1.getStackTrace()[0].getMethodName());

        e1 = new SupplyChainRuntimeException(new RuntimeException("rte"));
        assertTrue(e1.getMessage().contains("Supply chain runtime exception"));
        assertEquals(RuntimeException.class, e1.getCause().getClass());
        assertEquals("testSupplyChainRuntimeException", e1.getStackTrace()[0].getMethodName());
        assertEquals("rte", e1.getCause().getMessage());
        assertEquals(null, e1.getCause().getCause());

        e1 = new SupplyChainRuntimeException("error", new RuntimeException("rte"));
        assertEquals("error", e1.getMessage());
        assertEquals(RuntimeException.class, e1.getCause().getClass());
        assertEquals("testSupplyChainRuntimeException", e1.getStackTrace()[0].getMethodName());
        assertEquals("rte", e1.getCause().getMessage());
        assertEquals(null, e1.getCause().getCause());

        e1 = new SupplyChainRuntimeException("error", new RuntimeException("rte"), false, true);
        assertEquals("error", e1.getMessage());
        assertEquals(RuntimeException.class, e1.getCause().getClass());
        assertEquals("testSupplyChainRuntimeException", e1.getStackTrace()[0].getMethodName());
        assertEquals("rte", e1.getCause().getMessage());
        assertEquals(null, e1.getCause().getCause());
        assertEquals(0, e1.getSuppressed().length);
    }

}
