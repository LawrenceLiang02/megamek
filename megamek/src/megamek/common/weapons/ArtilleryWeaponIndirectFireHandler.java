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
/*
 * Created on Sep 24, 2004
 *
 */
package megamek.common.weapons;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import megamek.common.AmmoType;
import megamek.common.BattleArmor;
import megamek.common.Compute;
import megamek.common.Coords;
import megamek.common.Entity;
import megamek.common.EntitySelector;
import megamek.common.IGame;
import megamek.common.INarcPod;
import megamek.common.LosEffects;
import megamek.common.Minefield;
import megamek.common.Mounted;
import megamek.common.Report;
import megamek.common.SpecialHexDisplay;
import megamek.common.TargetRoll;
import megamek.common.Targetable;
import megamek.common.ToHitData;
import megamek.common.VTOL;
import megamek.common.actions.ArtilleryAttackAction;
import megamek.common.actions.WeaponAttackAction;
import megamek.common.options.OptionsConstants;
import megamek.common.weapons.capitalweapons.CapitalMissileWeapon;
import megamek.server.Server;

/**
 * @author Sebastian Brocks
 */
public class ArtilleryWeaponIndirectFireHandler extends AmmoWeaponHandler {

    /**
     *
     */
    private static final long serialVersionUID = -1277649123562229298L;
    boolean handledAmmoAndReport = false;
    private int shootingBA = -1;

    /**
     * This constructor may only be used for deserialization.
     */
    protected ArtilleryWeaponIndirectFireHandler() {
        super();
    }

    /**
     * @param t
     * @param w
     * @param g
     */
    public ArtilleryWeaponIndirectFireHandler(ToHitData t,
            WeaponAttackAction w, IGame g, Server s) {
        super(t, w, g, s);
        if (w.getEntity(g) instanceof BattleArmor) {
            shootingBA = ((BattleArmor)w.getEntity(g)).getNumberActiverTroopers();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see megamek.common.weapons.AttackHandler#cares(int)
     */
    @Override
    public boolean cares(IGame.Phase phase) {
        if ((phase == IGame.Phase.PHASE_OFFBOARD)
                || (phase == IGame.Phase.PHASE_TARGETING)) {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see megamek.common.weapons.AttackHandler#handle(int, java.util.Vector)
     */
    @Override
    public boolean handle(IGame.Phase phase, Vector<Report> vPhaseReport) {
        if (!cares(phase)) {
            return true;
        }
        String artyMsg;
        ArtilleryAttackAction aaa = (ArtilleryAttackAction) waa;
        if (phase == IGame.Phase.PHASE_TARGETING) {
            if (!handledAmmoAndReport) {
                addHeat();
                // Report the firing itself
                Report r = new Report(3121);
                r.indent();
                r.newlines = 0;
                r.subject = subjectId;
                r.add(wtype.getName());
                r.add(aaa.getTurnsTilHit());
                vPhaseReport.addElement(r);
                Report.addNewline(vPhaseReport);
                handledAmmoAndReport = true;

                artyMsg = "Artillery fire Incoming, landing on round "
                        + (game.getRoundCount() + aaa.getTurnsTilHit())
                        + ", fired by "
                        + game.getPlayer(aaa.getPlayerId()).getName();
                game.getBoard().addSpecialHexDisplay(
                        aaa.getTarget(game).getPosition(),
                        new SpecialHexDisplay(
                                SpecialHexDisplay.Type.ARTILLERY_INCOMING, game
                                        .getRoundCount() + aaa.getTurnsTilHit(),
                                game.getPlayer(aaa.getPlayerId()), artyMsg,
                                SpecialHexDisplay.SHD_OBSCURED_TEAM));
            }
            // if this is the last targeting phase before we hit,
            // make it so the firing entity is announced in the
            // off-board attack phase that follows.
            if (aaa.getTurnsTilHit() == 0) {
                setAnnouncedEntityFiring(false);
            }
            return true;
        }
        if (aaa.getTurnsTilHit() > 0) {
            aaa.decrementTurnsTilHit();
            return true;
        }
        final Vector<Integer> spottersBefore = aaa.getSpotterIds();
        Coords targetPos = target.getPosition();
        final int playerId = aaa.getPlayerId();
        boolean isFlak = (target instanceof VTOL) || target.isAero();
        boolean asfFlak = target.isAero();
        Entity bestSpotter = null;
        if (ae == null) {
            System.err.println("Artillery Entity is null!");
            return true;
        }
        Mounted ammoUsed = ae.getEquipment(aaa.getAmmoId());
        final AmmoType atype = ammoUsed == null ? null : (AmmoType) ammoUsed
                .getType();
        // Are there any valid spotters?
        if ((null != spottersBefore) && !isFlak) {
            // fetch possible spotters now
            Iterator<Entity> spottersAfter = game
                    .getSelectedEntities(new EntitySelector() {
                        public int player = playerId;

                        public Targetable targ = target;

                        public boolean accept(Entity entity) {
                            Integer id = Integer.valueOf(entity.getId());
                            if ((player == entity.getOwnerId())
                                    && spottersBefore.contains(id)
                                    && !(LosEffects.calculateLos(game,
                                            entity.getId(), targ, true))
                                            .isBlocked()
                                    && entity.isActive()
                                    // airborne aeros can't spot for arty
                                    && !((entity.isAero()) && entity
                                            .isAirborne())
                                    && !entity.isINarcedWith(INarcPod.HAYWIRE)) {
                                return true;
                            }
                            return false;
                        }
                    });

            // Out of any valid spotters, pick the best.
            while (spottersAfter.hasNext()) {
                Entity ent = spottersAfter.next();
                if (bestSpotter == null) {
                    bestSpotter = ent;
                } else if (ent.hasAbility(OptionsConstants.MISC_FORWARD_OBSERVER)
                        && !bestSpotter.hasAbility(OptionsConstants.MISC_FORWARD_OBSERVER)) {
                    bestSpotter = ent;
                } else if (ent.getCrew().getGunnery() < bestSpotter.getCrew().getGunnery()
                        && !bestSpotter.hasAbility(OptionsConstants.MISC_FORWARD_OBSERVER)) {
                    bestSpotter = ent;
                } else if (bestSpotter.hasAbility(OptionsConstants.MISC_FORWARD_OBSERVER)
                        && ent.hasAbility(OptionsConstants.MISC_FORWARD_OBSERVER)) {
                    if (ent.getCrew().getGunnery() < bestSpotter.getCrew().getGunnery()) {
                        bestSpotter = ent;
                    }
                }
            }

        } // End have-valid-spotters

        // If at least one valid spotter, then get the benefits thereof.
        if (null != bestSpotter) {
            int foMod = 0;
            if (bestSpotter.hasAbility(OptionsConstants.MISC_FORWARD_OBSERVER)) {
                foMod = -1;
            }
            int mod = (bestSpotter.getCrew().getGunnery() - 4) / 2;
            mod += foMod;
            toHit.addModifier(mod, "Spotting modifier");
        }

        // Is the attacker still alive and we're not shooting FLAK?
        // then adjust the target
        if ((null != ae) && !isFlak) {

            // If the shot hit the target hex, then all subsequent
            // fire will hit the hex automatically.
            // This should only happen for indirect shots
            if (roll >= toHit.getValue() 
                    && !(this instanceof ArtilleryWeaponDirectFireHandler)) {
                ae.aTracker
                        .setModifier(TargetRoll.AUTOMATIC_SUCCESS, targetPos);
            }
            // If the shot missed, but was adjusted by a
            // spotter, future shots are more likely to hit.

            // Note: Because artillery fire is adjusted on a per-unit basis,
            // this can result in a unit firing multiple artillery weapons at 
            // the same hex getting this bonus more than once per turn. Since
            // the Artillery Modifiers Table on TacOps p. 180 lists a -1 per 
            // shot (not salvo!) previously fired at the target hex, this would
            // in fact appear to be correct.
            // Only apply these modifiers to indirect artillery
            else if ((null != bestSpotter) && !(this instanceof ArtilleryWeaponDirectFireHandler)) {
                // only add mods if it's not an automatic success
                if (ae.aTracker.getModifier(weapon, targetPos) != TargetRoll.AUTOMATIC_SUCCESS) {
                    if (bestSpotter.hasAbility(OptionsConstants.MISC_FORWARD_OBSERVER)) {
                        ae.aTracker.setSpotterHasForwardObs(true);
                    }
                    ae.aTracker.setModifier(ae.aTracker.getModifier(weapon, targetPos) - 1, targetPos);
                }
            }

        } // End artyAttacker-alive

        // Report weapon attack and its to-hit value.
        Report r = new Report(3120);
        r.indent();
        r.newlines = 0;
        r.subject = subjectId;
        if (wtype != null) {
            r.add(wtype.getName());
        } else {
            r.add("Error: From Nowhwere");
        }

        r.add(target.getDisplayName(), true);
        vPhaseReport.addElement(r);
        if (toHit.getValue() == TargetRoll.IMPOSSIBLE) {
            r = new Report(3135);
            r.subject = subjectId;
            r.add(toHit.getDesc());
            vPhaseReport.addElement(r);
            return false;
        } else if (toHit.getValue() == TargetRoll.AUTOMATIC_FAIL) {
            r = new Report(3140);
            r.newlines = 0;
            r.subject = subjectId;
            r.add(toHit.getDesc());
            vPhaseReport.addElement(r);
        } else if (toHit.getValue() == TargetRoll.AUTOMATIC_SUCCESS) {
            r = new Report(3145);
            r.newlines = 0;
            r.subject = subjectId;
            r.add(toHit.getDesc());
            vPhaseReport.addElement(r);
        } else {
            // roll to hit
            r = new Report(3150);
            r.newlines = 0;
            r.subject = subjectId;
            r.add(toHit.getValue());
            vPhaseReport.addElement(r);
        }

        // dice have been rolled, thanks
        r = new Report(3155);
        r.newlines = 0;
        r.subject = subjectId;
        r.add(roll);
        vPhaseReport.addElement(r);

        // do we hit?
        bMissed = roll < toHit.getValue();
        // Set Margin of Success/Failure.
        toHit.setMoS(roll - Math.max(2, toHit.getValue()));

        // Do this stuff first, because some weapon's miss report reference the
        // amount of shots fired and stuff.
        if (!handledAmmoAndReport) {
            addHeat();
        }
        if (!bMissed) {
            if (!isFlak) {
                r = new Report(3190);
            } else {
                r = new Report(3191);
            }
            r.subject = subjectId;
            r.add(targetPos.getBoardNum());
            vPhaseReport.addElement(r);

            artyMsg = "Artillery hit here on round " + game.getRoundCount() 
                    + ", fired by " + game.getPlayer(aaa.getPlayerId()).getName()
                    + " (this hex is now an auto-hit)";
            game.getBoard().addSpecialHexDisplay(
                    targetPos,
                    new SpecialHexDisplay(SpecialHexDisplay.Type.ARTILLERY_HIT,
                            game.getRoundCount(), game.getPlayer(aaa
                                    .getPlayerId()), artyMsg));

        } else {
            // direct fire artillery only scatters by one d6
            // we do this here to avoid duplicating handle()
            // in the ArtilleryWeaponDirectFireHandler
            Coords origPos = targetPos;
            int moF = toHit.getMoS();
            if (ae.hasAbility("oblique_artillery")) {
                // getMoS returns a negative MoF
                // simple math is better so lets make it positive
                if ((-moF -2) < 1) {
                    moF = 0;
                } else {
                    moF = moF +2;
                }
            }
            targetPos = Compute.scatterDirectArty(targetPos, moF);
            if (game.getBoard().contains(targetPos)) {
                // misses and scatters to another hex
                if (!isFlak) {
                    r = new Report(3195);
                    artyMsg = "Artillery missed here on round "
                            + game.getRoundCount() + ", fired by "
                            + game.getPlayer(aaa.getPlayerId()).getName();
                    game.getBoard().addSpecialHexDisplay(
                            origPos,
                            new SpecialHexDisplay(
                                    SpecialHexDisplay.Type.ARTILLERY_HIT, game
                                            .getRoundCount(), game
                                            .getPlayer(aaa.getPlayerId()),
                                    artyMsg));
                } else {
                    r = new Report(3192);
                }
                r.subject = subjectId;
                r.add(targetPos.getBoardNum());
                vPhaseReport.addElement(r);
            } else {
                // misses and scatters off-board
                if (isFlak) {
                    r = new Report(3193);
                } else {
                    r = new Report(3200);
                }
                r.subject = subjectId;
                vPhaseReport.addElement(r);
                return !bMissed;
            }
        }

        if (atype.getMunitionType() == AmmoType.M_FAE) {
            AreaEffectHelper.processFuelAirDamage(targetPos, 
                    atype, aaa.getEntity(game), vPhaseReport, server);
                        
            return false;
        }
        
        if (atype.getMunitionType() == AmmoType.M_FLARE) {
            int radius;
            if (atype.getAmmoType() == AmmoType.T_ARROW_IV) {
                radius = 4;
            } else if (atype.getAmmoType() == AmmoType.T_LONG_TOM) {
                radius = 3;
            } else if (atype.getAmmoType() == AmmoType.T_SNIPER) {
                radius = 2;
            } else {
                radius = 1;
            }
            server.deliverArtilleryFlare(targetPos, radius);
            return false;
        }
        if (atype.getMunitionType() == AmmoType.M_DAVY_CROCKETT_M) {
            // The appropriate term here is "Bwahahahahaha..."
            server.doNuclearExplosion(targetPos, 1, vPhaseReport);
            return false;
        }
        if (atype.getMunitionType() == AmmoType.M_FASCAM) {
            // Arrow IVs deliver fixed 30-point minefields.
            int rackSize = (atype.getAmmoType() == AmmoType.T_ARROW_IV) ? 30
                    : atype.getRackSize();
            server.deliverFASCAMMinefield(targetPos, ae.getOwner().getId(),
                    rackSize, ae.getId());
            return false;
        }
        if (atype.getMunitionType() == AmmoType.M_INFERNO_IV) {
            server.deliverArtilleryInferno(targetPos, ae, subjectId,
                    vPhaseReport);
            return false;
        }
        if (atype.getMunitionType() == AmmoType.M_VIBRABOMB_IV) {
            server.deliverThunderVibraMinefield(targetPos, ae.getOwner()
                    .getId(), 30, waa.getOtherAttackInfo(), ae.getId());
            return false;
        }
        if (atype.getMunitionType() == AmmoType.M_SMOKE) {
            server.deliverArtillerySmoke(targetPos, vPhaseReport);
            return false;
        }
        if (atype.getMunitionType() == AmmoType.M_LASER_INHIB) {
            server.deliverLIsmoke(targetPos, vPhaseReport);
            return false;
        }
        int altitude = 0;
        if (isFlak) {
            altitude = target.getElevation();
        }

        // check to see if this is a mine clearing attack
        // According to the RAW you have to hit the right hex to hit even if the
        // scatter hex has minefields
        boolean mineClear = target.getTargetType() == Targetable.TYPE_MINEFIELD_CLEAR;
        if (mineClear && !isFlak && !bMissed) {
            r = new Report(3255);
            r.indent(1);
            r.subject = subjectId;
            vPhaseReport.addElement(r);

            clearMineFields(targetPos, Minefield.CLEAR_NUMBER_WEAPON, vPhaseReport);
        }

        server.artilleryDamageArea(targetPos, aaa.getCoords(), atype,
                subjectId, ae, isFlak, altitude, mineClear, vPhaseReport,
                asfFlak, shootingBA);

        // artillery may unintentially clear minefields, but only if it wasn't
        // trying to
        if (!mineClear) {
            clearMineFields(targetPos, Minefield.CLEAR_NUMBER_WEAPON_ACCIDENT, vPhaseReport);
        }

        return false;
    }
    
    /**
     * Worker function that clears minefields.
     */
    private void clearMineFields(Coords targetPos, int targetNum, Vector<Report> vPhaseReport) {
        Enumeration<Minefield> minefields = game.getMinefields(targetPos).elements();
        ArrayList<Minefield> mfRemoved = new ArrayList<Minefield>();
        while (minefields.hasMoreElements()) {
            Minefield mf = minefields.nextElement();
            if (server.clearMinefield(mf, ae, targetNum, vPhaseReport)) {
                mfRemoved.add(mf);
            }
        }
        // we have to do it this way to avoid a concurrent error problem
        for (Minefield mf : mfRemoved) {
            server.removeMinefield(mf);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see megamek.common.weapons.WeaponHandler#calcDamagePerHit()
     */
    @Override
    protected int calcDamagePerHit() {
        double toReturn = wtype.rackSize;
        if (wtype instanceof CapitalMissileWeapon) {
            toReturn = wtype.getRoundShortAV();
        }
        // BA Tube artillery is the only artillery that can be mounted by BA
        // so we do the multiplication here
        if (ae instanceof BattleArmor) {
            BattleArmor ba = (BattleArmor)ae;
            toReturn *= ba.getNumberActiverTroopers();
        }
        // area effect damage is double
        if (target.isConventionalInfantry()) {
            toReturn /= 0.5;
        }

        toReturn = applyGlancingBlowModifier(toReturn, target.isConventionalInfantry());

        return (int) Math.ceil(toReturn);
    }
}
