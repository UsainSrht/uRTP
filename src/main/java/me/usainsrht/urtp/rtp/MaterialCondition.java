package me.usainsrht.urtp.rtp;

import org.bukkit.block.Block;

public enum MaterialCondition {

    SOLID,
    LIQUID,
    OCCLUDING,
    FLAMMABLE,
    COLLIDABLE,
    PASSABLE,
    BURNABLE,
    GRAVITY;

    public Boolean getValue(Block block) {
        switch (this) {
            case SOLID -> {
                return block.isSolid();
            }
            case GRAVITY -> {
                return block.getType().hasGravity();
            }
            case BURNABLE -> {
                return block.isBurnable();
            }
            case LIQUID -> {
                return block.isLiquid();
            }
            case FLAMMABLE -> {
                return block.getType().isFlammable();
            }
            case PASSABLE -> {
                return block.isPassable();
            }
            case OCCLUDING -> {
                return block.getType().isOccluding();
            }
            case COLLIDABLE -> {
                return block.isCollidable();
            }
        }
        return null;
    }

}
