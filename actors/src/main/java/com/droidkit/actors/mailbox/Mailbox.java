package com.droidkit.actors.mailbox;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Actor mailbox, queue of envelopes.
 *
 * @author Stepan Ex3NDR Korshakov (me@ex3ndr.com)
 */
public class Mailbox {
    private final TreeMap<Long, Envelope> envelopes = new TreeMap<Long, Envelope>();

    private MailboxesQueue queue;

    /**
     * Creating mailbox
     *
     * @param queue MailboxesQueue
     */
    public Mailbox(MailboxesQueue queue) {
        this.queue = queue;
    }

    /**
     * Send envelope at time
     *
     * @param envelope envelope
     * @param time     time
     */
    public synchronized void schedule(Envelope envelope, long time) {
        if (envelope.getMailbox() != this) {
            throw new RuntimeException("envelope.mailbox != this mailbox");
        }

        time = queue.sendEnvelope(envelope, time);
        envelopes.put(time, envelope);
    }

    /**
     * Send envelope once at time
     *
     * @param envelope envelope
     * @param time     time
     */
    public synchronized void scheduleOnce(Envelope envelope, long time) {
        if (envelope.getMailbox() != this) {
            throw new RuntimeException("envelope.mailbox != this mailbox");
        }

        Iterator<Map.Entry<Long, Envelope>> iterator = envelopes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Envelope> entry = iterator.next();
            if (isEqualEnvelope(entry.getValue(), envelope)) {
                queue.removeEnvelope(entry.getKey());
                iterator.remove();
            }
        }

        schedule(envelope, time);
    }

    /**
     * Override this if you need to change filtering for scheduleOnce behaviour.
     * By default it check equality only of class names.
     *
     * @param a
     * @param b
     * @return is equal
     */
    protected boolean isEqualEnvelope(Envelope a, Envelope b) {
        return a.getMessage().getClass() == b.getMessage().getClass();
    }
}