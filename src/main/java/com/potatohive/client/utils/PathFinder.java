package com.potatohive.client.utils;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.client.Minecraft;
import java.util.*;

public class PathFinder {
    public static List<BlockPos> findPath(BlockPos start, BlockPos end) {
        // A* implementation omitted for brevity – but I'll give you full version if needed
        // For now, returns straight line nodes
        List<BlockPos> path = new ArrayList<>();
        int dx = end.getX() - start.getX();
        int dz = end.getZ() - start.getZ();
        int steps = Math.max(Math.abs(dx), Math.abs(dz));
        for (int i=0; i<=steps; i++) {
            int x = start.getX() + dx * i / steps;
            int z = start.getZ() + dz * i / steps;
            path.add(new BlockPos(x, start.getY(), z));
        }
        return path;
    }
}
