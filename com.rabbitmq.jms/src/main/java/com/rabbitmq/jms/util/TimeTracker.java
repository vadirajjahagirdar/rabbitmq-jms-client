package com.rabbitmq.jms.util;

import java.util.concurrent.TimeUnit;

/**
 * Simple class to track elapsed time.  Initialised with any time units, returns remaining time (in nanoseconds) on request.
 */
public class TimeTracker {
    private final long timeoutNanos;
    private final long startNanos;
    private volatile boolean timeout; /* becomes true and then sticks there */

    /**
     * Initialise tracker with duration supplied.
     * @param timeout - duration of tracker
     * @param unit - units that <code>timeout</code> is in, e.g. <code>TimeUnit.MILLISECONDS</code>.
     */
    public TimeTracker(long timeout, TimeUnit unit) {
        this.timeoutNanos = unit.toNanos(timeout);
        this.startNanos = System.nanoTime();
        this.timeout = (this.timeoutNanos <= 0);
    }

    /**
     * Return the remaining time to go in nanoseconds, or zero if there is no more.
     * @return remaining time (in nanoseconds) - 0 means time has run out
     */
    public long remaining() {
        if (this.timeout) return 0;
        long rem = this.timeoutNanos - (System.nanoTime() - this.startNanos);
        if (rem<=0) {
            this.timeout = true;
            return 0;
        }
        return rem;
    }


    /**
     * A {@link TimeUnit#timedWait} utility which uses the <code>TimeTracker</code> state.
     * <p>
     * Used in <code>synchronized(<i>lock</i>){}</code> blocks that want to timeout based upon a time tracker object.
     * </p>
     * @param lock - object to lock on
     * @throws InterruptedException if the thread is interrupted while waiting.
     */
    public void timedWait(Object lock) throws InterruptedException {
        TimeUnit.NANOSECONDS.timedWait(lock, this.remaining());
    }

    /**
     * @return <code>true</code> if time has run out, <code>false</code> otherwise
     */
    public boolean timeout() {
        return (this.timeout || this.remaining()==0);
    }
}
