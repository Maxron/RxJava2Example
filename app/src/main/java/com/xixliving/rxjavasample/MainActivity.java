package com.xixliving.rxjavasample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

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
                Log.d(TAG, "onError: " + e );
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
}
