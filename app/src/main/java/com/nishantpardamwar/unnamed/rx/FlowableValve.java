package com.nishantpardamwar.unnamed.rx;

import java.util.concurrent.atomic.*;

import org.reactivestreams.*;

import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.internal.fuseable.SimplePlainQueue;
import io.reactivex.rxjava3.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.rxjava3.internal.subscriptions.SubscriptionHelper;
import io.reactivex.rxjava3.internal.util.AtomicThrowable;


final class FlowableValve<T> extends Flowable<T> implements FlowableOperator<T, T>, FlowableTransformer<T, T> {

    final Publisher<? extends T> source;

    final Publisher<Boolean> other;

    final boolean defaultOpen;

    final int bufferSize;

    FlowableValve(Publisher<? extends T> source, Publisher<Boolean> other, boolean defaultOpen, int bufferSize) {
        this.source = source;
        this.other = other;
        this.defaultOpen = defaultOpen;
        this.bufferSize = bufferSize;
    }

    @Override
    protected void subscribeActual(Subscriber<? super T> s) {
        source.subscribe(apply(s));
    }

    @Override
    public Subscriber<? super T> apply(Subscriber<? super T> subscriber) {
        ValveMainSubscriber<T> parent = new ValveMainSubscriber<>(subscriber, bufferSize, defaultOpen);
        subscriber.onSubscribe(parent);
        other.subscribe(parent.other);
        return parent;
    }

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return new FlowableValve<>(upstream, other, defaultOpen, bufferSize);
    }

    static final class ValveMainSubscriber<T>
            extends AtomicInteger
            implements Subscriber<T>, Subscription {

        private static final long serialVersionUID = -2233734924340471378L;

        final Subscriber<? super T> downstream;

        final AtomicReference<Subscription> upstream;

        final AtomicLong requested;

        final SimplePlainQueue<T> queue;

        final OtherSubscriber other;

        final AtomicThrowable error;

        volatile boolean done;

        volatile boolean gate;

        volatile boolean cancelled;

        ValveMainSubscriber(Subscriber<? super T> downstream, int bufferSize, boolean defaultOpen) {
            this.downstream = downstream;
            this.queue = new SpscLinkedArrayQueue<>(bufferSize);
            this.gate = defaultOpen;
            this.other = new OtherSubscriber();
            this.requested = new AtomicLong();
            this.error = new AtomicThrowable();
            this.upstream = new AtomicReference<>();
        }

        @Override
        public void onSubscribe(Subscription s) {
            SubscriptionHelper.deferredSetOnce(this.upstream, requested, s);
        }

        @Override
        public void onNext(T t) {
            queue.offer(t);
            drain();
        }

        @Override
        public void onError(Throwable t) {
            if (error.tryAddThrowableOrReport(t)) {
                drain();
            }
        }

        @Override
        public void onComplete() {
            done = true;
            drain();
        }

        @Override
        public void request(long n) {
            SubscriptionHelper.deferredRequest(upstream, requested, n);
        }

        @Override
        public void cancel() {
            cancelled = true;
            SubscriptionHelper.cancel(upstream);
            SubscriptionHelper.cancel(other);
            error.tryTerminateAndReport();
        }

        void drain() {
            if (getAndIncrement() != 0) {
                return;
            }

            int missed = 1;

            SimplePlainQueue<T> q = queue;
            Subscriber<? super T> a = downstream;
            AtomicThrowable error = this.error;

            for (; ; ) {
                for (; ; ) {
                    if (cancelled) {
                        q.clear();
                        return;
                    }

                    if (error.get() != null) {
                        q.clear();
                        SubscriptionHelper.cancel(upstream);
                        SubscriptionHelper.cancel(other);
                        error.tryTerminateConsumer(a);
                        return;
                    }

                    if (!gate) {
                        break;
                    }

                    boolean d = done;
                    T v = q.poll();
                    boolean empty = v == null;

                    if (d && empty) {
                        SubscriptionHelper.cancel(other);
                        a.onComplete();
                        return;
                    }

                    if (empty) {
                        break;
                    }

                    a.onNext(v);
                }

                missed = addAndGet(-missed);
                if (missed == 0) {
                    break;
                }
            }
        }

        void change(boolean state) {
            gate = state;
            if (state) {
                drain();
            }
        }

        void innerError(Throwable ex) {
            onError(ex);
        }

        void innerComplete() {
            innerError(new IllegalStateException("The valve source completed unexpectedly."));
        }

        final class OtherSubscriber extends AtomicReference<Subscription> implements FlowableSubscriber<Boolean> {

            private static final long serialVersionUID = -3076915855750118155L;

            @Override
            public void onSubscribe(Subscription s) {
                if (SubscriptionHelper.setOnce(this, s)) {
                    s.request(Long.MAX_VALUE);
                }
            }

            @Override
            public void onNext(Boolean t) {
                change(t);
            }

            @Override
            public void onError(Throwable t) {
                innerError(t);
            }

            @Override
            public void onComplete() {
                innerComplete();
            }
        }
    }
}