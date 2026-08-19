package com.potatohive.client.utils;

import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class PathFinder {

    public static List<BlockPos> findPath(BlockPos start, BlockPos end) {

        List<BlockPos> path = new ArrayList<>();

        int dx = end.getX() - start.getX();
        int dz = end.getZ() - start.getZ();

        int steps = Math.max(Math.abs(dx), Math.abs(dz));

        // Start and end are the same block
        if (steps == 0) {
            path.add(start);
            return path;
        }

        for (int i = 0; i <= steps; i++) {

            int x = start.getX() + dx * i / steps;
            int z = start.getZ() + dz * i / steps;

            path.add(new BlockPos(
                    x,
                    start.getY(),
                    z
            ));
        }

        return path;
    }
}
