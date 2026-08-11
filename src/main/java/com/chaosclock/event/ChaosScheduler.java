/*
 * Decompiled with CFR 0.152.
 */
package com.chaosclock.event;

import java.util.ArrayList;
import java.util.List;

public class ChaosScheduler {
    private static final List<ScheduledTask> TASKS = new ArrayList<ScheduledTask>();

    public static void schedule(int delayTicks, Runnable action) {
        TASKS.add(new ScheduledTask(Math.max(0, delayTicks), action));
    }

    public static void tick() {
        if (TASKS.isEmpty()) {
            return;
        }
        ArrayList<ScheduledTask> ready = new ArrayList<ScheduledTask>();
        for (ScheduledTask task : TASKS) {
            --task.ticksLeft;
            if (task.ticksLeft > 0) continue;
            ready.add(task);
        }
        TASKS.removeAll(ready);
        for (ScheduledTask task : ready) {
            task.action.run();
        }
    }

    private static class ScheduledTask {
        int ticksLeft;
        final Runnable action;

        ScheduledTask(int ticksLeft, Runnable action) {
            this.ticksLeft = ticksLeft;
            this.action = action;
        }
    }
}

