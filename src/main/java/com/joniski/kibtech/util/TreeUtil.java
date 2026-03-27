package com.joniski.kibtech.util;

import java.util.ArrayList;
import java.util.List;

import com.joniski.kibtech.KibTech;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class TreeUtil {
    public static boolean isLog(Level level, BlockPos pos){
        if (level.getBlockState(pos) == null){
            return false;
        }

        for (TagKey tag : level.getBlockState(pos).getTags().toList()){
            if (tag.toString().contains("minecraft:logs")){
                return true;
            }
        }

        return false;
    }

    public static boolean isLeaf(Level level, BlockPos leaf){
        if (level.getBlockState(leaf) == null){
            return false;
        }

        for (TagKey tag : level.getBlockState(leaf).getTags().toList()){
            if (tag.toString().contains("leaves")){
                return true;
            }
        }

        return false;
    }

    public static List<BlockPos> getNearbyLogs(Level level, BlockPos pos){
        List<BlockPos> nearby = new ArrayList<BlockPos>();

        for (int x = 0; x < 3; ++x){
            for (int y = 0; y < 3; ++y){
                for (int z = 0; z < 3; ++z){
                    BlockPos newPos = pos.offset(x-1, y-1, z-1);
                    if (isLog(level, newPos)){
                        // Make sure its the same tree, different trees wont have branches that face up
                        if (newPos.getX() != pos.getX() || newPos.getZ() != pos.getZ()){
                            if(level.getBlockState(newPos).hasProperty(RotatedPillarBlock.AXIS)){
                                Direction.Axis axis = level.getBlockState(newPos).getValue(RotatedPillarBlock.AXIS);
                                if (axis == Direction.Axis.Y){
                                    continue;
                                }
                            }
                        }
                        nearby.add(newPos);
                    }
                }
            }
        }

        return nearby;
    }

    public static List<BlockPos> getNearbyLeaf(Level level, BlockPos pos){
        List<BlockPos> nearby = new ArrayList<BlockPos>();

        for (int x = 0; x < 3; ++x){
            for (int y = 0; y < 3; ++y){
                for (int z = 0; z < 3; ++z){
                    BlockPos newPos = pos.offset(x-1, y-1, z-1);
                    if (isLeaf(level, newPos)){
                        nearby.add(newPos);
                    }
                }
            }
        }

        return nearby;
    }

    public static List<BlockPos> lookForBranch(Level level, BlockPos pos, List<BlockPos> foundLogs){
        List<BlockPos> nearbyLogs = getNearbyLogs(level, pos);
        
        for(BlockPos log : nearbyLogs){
            boolean alreadyFound = false;
            for (BlockPos foundLog: foundLogs){
                if (log.getX() == foundLog.getX() && log.getY() == foundLog.getY() && log.getZ() == foundLog.getZ()){
                    alreadyFound = true;
                    break;
                }
            }

            if (alreadyFound == false){
                foundLogs.add(log);
                lookForBranch(level, log, foundLogs);
            }
        }


        return null;
    }

    public static List<BlockPos> lookForLeaves(Level level, BlockPos pos, List<BlockPos> foundLeaves){
        List<BlockPos> nearbyLeaves = getNearbyLeaf(level, pos);
        
        for(BlockPos log : nearbyLeaves){
            boolean alreadyFound = false;
            for (BlockPos foundLog: foundLeaves){
                if (log.getX() == foundLog.getX() && log.getY() == foundLog.getY() && log.getZ() == foundLog.getZ()){
                    alreadyFound = true;
                    break;
                }
            }

            if (alreadyFound == false){
                foundLeaves.add(log);
                lookForLeaves(level, log, foundLeaves);
            }
        }


        return null;
    }

    public static boolean isValidLeaf(Level level, BlockPos pos, BlockPos log){
        // Technically its by block and that if there is a gap the distance will be bigger
        // but the cases wrong leaves getting done are low and wont be much and this is alot easier
        if (!(level.getBlockState(pos).getBlock() instanceof LeavesBlock leaf)){
            return false;
        }            

        float distance = LeavesBlock.getOptionalDistanceAt(level.getBlockState(pos)).getAsInt();
        if (pos.getCenter().distanceTo(log.getCenter()) < distance + 0.6){
            return true;
        }

        return false;
    }

    // Recursion
    public static List<BlockPos> getTree(Level level, BlockPos pos){
        List<BlockPos> logs = new ArrayList<BlockPos>();     
        List<BlockPos> leaves = new ArrayList<BlockPos>();     
        
        if (isLog(level, pos)){
            logs.add(pos);
        }else{
            return logs;
        }

        lookForBranch(level, pos, logs);

        for (BlockPos log: logs){
            lookForLeaves(level, log, leaves);
        }

        List<BlockPos> tree = new ArrayList<BlockPos>();     
        for (BlockPos log : logs){
            tree.add(log);
            for (BlockPos leaf : leaves){
                if (isValidLeaf(level, leaf, log)){
                    boolean foundInList = false;
                    for (BlockPos treePos : tree){
                        if (treePos.equals(leaf)){
                            foundInList = true;
                        }
                    }

                    if (foundInList == false){
                        tree.add(leaf);
                    }
                }
            }
        }

        return tree;
    }
}
