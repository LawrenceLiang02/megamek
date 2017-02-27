/**
 * MegaMek - Copyright (C) 2005 Ben Mazur (bmazur@sev.org)
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 */
package megamek.common.weapons;

import megamek.common.TechAdvancement;

/**
 * @author Sebastian Brocks
 */
public class ISLRM20 extends LRMWeapon {

    /**
     *
     */
    private static final long serialVersionUID = 3287950524687857609L;

    /**
     *
     */
    public ISLRM20() {
        super();
        name = "LRM 20";
        setInternalName(name);
        addLookupName("IS LRM-20");
        addLookupName("ISLRM20");
        addLookupName("IS LRM 20");
        heat = 6;
        rackSize = 20;
        minimumRange = 6;
        tonnage = 10.0f;
        criticals = 5;
        bv = 181;
        cost = 250000;
        shortAV = 12;
        medAV = 12;
        longAV = 12;
        maxRange = RANGE_LONG;
        techAdvancement.setTechBase(TechAdvancement.TECH_BASE_ALL);
        techAdvancement.setISAdvancement(2295, 2300, 2400);
        techAdvancement.setClanAdvancement(DATE_NONE, DATE_NONE, 2400, 2830);
        techAdvancement.setIntroLevel(true);
        techAdvancement.setTechRating(RATING_C);
        techAdvancement.setAvailability( new int[] { RATING_C, RATING_C, RATING_C, RATING_C });
    }
}
