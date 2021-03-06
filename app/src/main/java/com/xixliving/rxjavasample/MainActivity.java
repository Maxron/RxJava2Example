package com.xixliving.rxjavasample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onSample1Clicked(View view) {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe: " + d);
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Log.d(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "onError: " + e.toString());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        });
    }

    public void onDisposableClicked(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "subscribe: emit 1");
                e.onNext(1);
                Log.d(TAG, "subscribe: emit 2");
                e.onNext(2);
                Log.d(TAG, "subscribe: emit 3");
                e.onNext(3);
                Log.d(TAG, "subscribe: onComplete");
                e.onComplete();
                Log.d(TAG, "subscribe: emit 4");
                e.onNext(4);
            }
        }).subscribe(new Observer<Integer>() {
            private Disposable _disposable;
            private int i;

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe: ");
                _disposable = d;
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Log.d(TAG, "onNext: " + integer);
                i++;
                if (i == 2) {
                    // Disconnect from Observable
                    Log.d(TAG, "dispose");
                    _disposable.dispose();
                    Log.d(TAG, "isDisposed: " + _disposable.isDisposed());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "onError: " + e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        });
    }

    public void onConsumerClicked(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "subscribe: emit 1");
                e.onNext(1);
                Log.d(TAG, "subscribe: emit 2");
                e.onNext(2);
                Log.d(TAG, "subscribe: emit 3");
                e.onNext(3);
                Log.d(TAG, "subscribe: onComplete");
                e.onComplete();
                Log.d(TAG, "subscribe: emit 4");
                e.onNext(4);
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "accept: " + integer);
            }
        });
    }

    public void onFlowableClicked(View view) {
        Flowable.range(1, 10)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe: Start");
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        try {
                            Thread.sleep(1000);
                            Log.d(TAG, "onNext: " + integer);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "onError: ");
                        t.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });
    }

    public void onFlowableRequestClicked(View view) {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 129; i++) {
                    Log.d(TAG, "subscribe: emmit " + i);
                    e.onNext(i);
                }
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                /**
                 * Strategy: MISSING, ERROR, BUFFER, DROP, LATEST
                 */
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe: ");
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "onError: ");
                        t.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });

    }

    public void onSingleClicked(View view) {
        Single.just(1)
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onSuccess(@NonNull Integer integer) {
                        Log.d(TAG, "onSuccess: " + integer);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "onError: " + e);
                        e.printStackTrace();
                    }
                });
    }

    public void onCompletableClicked(View view) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                Log.d(TAG, "run: Completable");
            }
        }).subscribe();
    }

    public void onCompletableWithThenClicked(View view) {
        /**
         * Reference: http://www.jianshu.com/p/45309538ad94
         */
        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull CompletableEmitter e) throws Exception {
                // Can initialize or check something here.
                TimeUnit.SECONDS.sleep(1);
                e.onComplete();
            }
        })
                .andThen(Observable.range(1, 10))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "accept: " + integer);
                    }
                });

        /* The same effect of above.
        Completable.timer(1, TimeUnit.SECONDS)
                .andThen(Observable.range(1, 10))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "accept: " + integer);
                    }
                });
        */
    }

    public void onCompletableAndThen2(View view) {
        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull CompletableEmitter e) throws Exception {
                /**
                 * Can prepare or initialize something here.
                 */
                Log.d(TAG, "subscribe: initialized something ...");
                TimeUnit.SECONDS.sleep(1);
                e.onComplete();
            }
        })
                .observeOn(Schedulers.newThread())
                .andThen(new Publisher<Integer>() {
                    @Override
                    public void subscribe(Subscriber<? super Integer> s) {
                        Log.d(TAG, "subscribe: start");
                        for (int i = 0; i < 10; i++) {
                            Log.d(TAG, "subscribe: onNext " + i);
                            s.onNext(i);
                        }
                        s.onComplete();
                    }
                })
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(Subscription subscription) throws Exception {
                        Log.d(TAG, "accept: subscription");
                        subscription.request(1);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "accept: onNext: " + integer);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.d(TAG, "run: complete");
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, "accept: Error:");
                        throwable.printStackTrace();
                    }
                })
                .subscribe();
    }

    public void onMaybeClicked(View view) {
        Maybe.create(new MaybeOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull MaybeEmitter<Integer> e) throws Exception {
                Log.d(TAG, "subscribe: ");
                Log.d(TAG, "subscribe: emmit success 1");
                e.onSuccess(1);
                Log.d(TAG, "subscribe: emmit success 2, but only 1 can be accept.");
                e.onSuccess(2);
                Log.d(TAG, "subscribe: emmit complete,");
                e.onComplete();

                /**
                 *  Only one method can be invoke and just invoke a methond in this block.
                 *  onSuccess or onComplete can be invoke in this block.
                 */
            }
        }).doOnSuccess(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "accept: " + integer);
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                Log.d(TAG, "run: complete !!");
            }
        }).subscribe();
    }

    public void onMapClicked(View view) {
        final Map<Integer, String> contacts = new HashMap<>();
        contacts.put(1, "aaa");
        contacts.put(2, "bbb");
        contacts.put(3, "ddd");
        contacts.put(4, "xxxx");

        Observable.range(1, contacts.size())
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(@NonNull Integer integer) throws Exception {
                        Log.d(TAG, "apply: integer" + integer);
                        Log.d(TAG, "apply: return value: " + contacts.get(integer));
                        return contacts.get(integer);
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "accept: " + s);
                    }
                });

    }
}
