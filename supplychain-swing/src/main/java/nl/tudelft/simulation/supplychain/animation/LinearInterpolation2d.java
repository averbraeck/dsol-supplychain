package nl.tudelft.simulation.supplychain.animation;

import org.djutils.draw.point.OrientedPoint2d;

/**
 * LinearInterpolation2d interpolates between two points.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class LinearInterpolation2d
{
    /** the start time. */
    private double startTime = Double.NaN;

    /** the end time. */
    private double endTime = Double.NaN;

    /** the origin. */
    private OrientedPoint2d origin = null;

    /** the destination. */
    private OrientedPoint2d destination = null;

    /**
     * constructs a new LinearInterpolation.
     * @param startTime double; the startingTime
     * @param endTime double; the endTime
     * @param origin OrientedPoint2d; the origin
     * @param destination OrientedPoint2d; the destination
     */
    public LinearInterpolation2d(final double startTime, final double endTime, final OrientedPoint2d origin,
            final OrientedPoint2d destination)
    {
        super();
        if (endTime < startTime)
        {
            throw new IllegalArgumentException("endTime < startTime");
        }
        this.startTime = startTime;
        this.endTime = endTime;
        this.origin = origin; // immutable
        this.destination = destination; // immutable
    }

    /**
     * returns the current location.
     * @param time double; the current time
     * @return OrientedPoint3d the current location
     */
    public OrientedPoint2d getLocation(final double time)
    {
        if (time <= this.startTime)
        {
            return this.origin;
        }
        if (time >= this.endTime)
        {
            return this.destination;
        }
        double fraction = (time - this.startTime) / (this.endTime - this.startTime);
        double x = this.origin.getX() + (this.destination.getX() - this.origin.getX()) * fraction;
        double y = this.origin.getY() + (this.destination.getY() - this.origin.getY()) * fraction;
        double rotZ = this.origin.getDirZ() + (this.destination.getDirZ() - this.origin.getDirZ()) * fraction;
        return new OrientedPoint2d(x, y, rotZ);
    }

}
