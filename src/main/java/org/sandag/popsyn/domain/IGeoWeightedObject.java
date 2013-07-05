package org.sandag.popsyn.domain;

import org.sandag.common.montecarlo.IWeightedObject;

public interface IGeoWeightedObject
        extends IWeightedObject, Cloneable
{
    int getZone();

    void setZone(int aZoneId);

}
