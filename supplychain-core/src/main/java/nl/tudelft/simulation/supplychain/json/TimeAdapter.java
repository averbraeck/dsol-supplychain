package nl.tudelft.simulation.supplychain.json;

import java.lang.reflect.Type;

import org.djunits.value.vdouble.scalar.Time;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Serialize and deserialize Time with GSON.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TimeAdapter implements JsonSerializer<Time>, JsonDeserializer<Time>
{
    /** {@inheritDoc} */
    @Override
    public Time deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
            throws JsonParseException
    {
        return Time.valueOf(json.getAsJsonPrimitive().getAsString());
    }

    /** {@inheritDoc} */
    @Override
    public JsonElement serialize(final Time src, final Type typeOfSrc, final JsonSerializationContext context)
    {
        return new JsonPrimitive(src.getInUnit() + " " + src.getDisplayUnit());
    }
}
