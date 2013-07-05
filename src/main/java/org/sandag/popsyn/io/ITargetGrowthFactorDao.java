package org.sandag.popsyn.io;

import java.util.HashMap;
import org.sandag.popsyn.Version;

public interface ITargetGrowthFactorDao
{
    HashMap<Integer, Double> getGrowthFactor(int zone, Version version);
}
