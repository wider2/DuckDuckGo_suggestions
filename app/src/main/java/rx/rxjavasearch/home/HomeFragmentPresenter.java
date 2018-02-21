package rx.rxjavasearch.home;

import android.content.Context;
import android.service.notification.Condition;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.Model;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.internal.SingleCheck;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.rxjavasearch.RxJavaSearchApplication;
import rx.rxjavasearch.dagger.AppModule;
import rx.rxjavasearch.dagger.DaggerPersistentComponent;
//import rx.rxjavasearch.dagger.SingletonComponent;
import rx.rxjavasearch.dagger_coupon.CouponModule;
import rx.rxjavasearch.dagger_coupon.CouponRepository;
import rx.rxjavasearch.dagger_coupon.DaggerCouponComponent;
import rx.rxjavasearch.dagger_coupon.RemoteCoupons;
import rx.rxjavasearch.db.Phrase;
import rx.rxjavasearch.db.Phrase_Table;
import rx.rxjavasearch.model.DuckGoSuggestion;
import rx.rxjavasearch.utils.RetrofitApi;

import static rx.rxjavasearch.utils.GlobalConstants.SERVER_SSL_URL;


public class HomeFragmentPresenter {

    private static final String TAG = "SEARCH_API";
    private IHomeFragment mainView;
    private Context mContext;
    private PublishSubject<String> subject;
    StringBuilder bstr;
    List<Phrase> listPhrase;
    private Retrofit retrofit;


    @Inject
    CouponRepository couponRepository;


    public HomeFragmentPresenter(IHomeFragment mainView, Context ctx) {

        this.mainView = mainView;
        this.mContext = ctx;
        subject = PublishSubject.create();

        DaggerCouponComponent.builder().couponModule(new CouponModule()).build();

        couponRepository = DaggerCouponComponent.create().getCouponRep();
    }

    protected String getTag() {
        return HomeFragmentPresenter.class.getName();
    }


    public static List<Phrase> findByList(String query) {
        return new Select().from(Phrase.class).where(Phrase_Table.name.eq(query)).queryList();
    }

    public static Phrase findByPhrase(String query) {
        return new Select().from(Phrase.class).where(Phrase_Table.name.eq(query)).querySingle();
    }

    public void getSearch(final String query) {
        bstr = new StringBuilder();
        listPhrase = new ArrayList<>();
        String[] viewedArray = null;
        List<DuckGoSuggestion> list = new ArrayList<>();
        try {
/*
            Retrofit retrofit = new Retrofit.Builder()
                    .validateEagerly(true)
                    .baseUrl(SERVER_SSL_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
*/
            //List<Phrase> sentencies = findByPhrase(query);
            //if (!sentencies.isEmpty()) {

            Phrase phrase = findByPhrase(query);
            if (phrase != null) {
                if (phrase.getSentencies().length() > 0) {
                    viewedArray = phrase.getSentencies().split("\n");
                    for (String item : viewedArray) {
                        list.add(new DuckGoSuggestion(item));
                    }
                    mainView.refreshResultList(list);
                }
            } else {

                String result = couponRepository.getAllCoupon();

                retrofit = couponRepository.provideRetrofit();

                final RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);


                subject.onNext(query);
                subject.debounce(300, TimeUnit.MILLISECONDS)
                        .filter(new Predicate<String>() {
                            @Override
                            public boolean test(String x) throws Exception {
                                return x.length() >= 3;
                                //return (x.isEmpty() ? false : true);
                            }
                        })
                        .distinctUntilChanged()
                        .switchMap(new Function<String, Observable<List<DuckGoSuggestion>>>() {
                            @Override
                            public Observable<List<DuckGoSuggestion>> apply(@NonNull String s) throws Exception {
                                return (retrofitApi.getSuggestionsList(s));
                            }
                        })

                        .filter(new Predicate<List<DuckGoSuggestion>>() {
                            @Override
                            public boolean test(List<DuckGoSuggestion> x) throws Exception {
                                return x.size() >= 0;
                            }
                        })

                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<DuckGoSuggestion>>() {
                            @Override
                            public void accept(List<DuckGoSuggestion> list) throws Exception {
                                bstr.setLength(0);
                                for (DuckGoSuggestion item : list) {
                                    bstr.append(item.phrase + "\n");
                                }

                                Phrase phrase = new Phrase();
                                phrase.setName(query);
                                phrase.setSentencies(bstr.toString());
                                phrase.setTimestamp(new Date());
                                phrase.save();

                                mainView.refreshResultList(list);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.wtf(TAG, "Error occured: " + throwable.getMessage());
                                mainView.showErrorServerResponse(throwable);
                            }
                        });
            } //list
        } catch (Exception ex) {
            mainView.ExceptionOccurred(ex);
        }

    }


    public void getSearchSimpleScalar(String query) {

        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .validateEagerly(true)
                    .baseUrl(SERVER_SSL_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            final RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);


            subject.onNext(query);

            subject.debounce(300, TimeUnit.MILLISECONDS)
                    .distinctUntilChanged()
                    .filter(new Predicate<String>() {
                        @Override
                        public boolean test(String x) throws Exception {
                            return x.length() >= 3;
                        }
                    })
                    .switchMap(new Function<String, ObservableSource<?>>() {
                        @Override
                        public ObservableSource<?> apply(@NonNull String s) throws Exception {
                            return (retrofitApi.getSuggestions(s));
                        }
                    })
                    .doOnNext(new Consumer<Object>() {
                        @Override
                        public void accept(Object s) throws Exception {
                            Log.wtf(TAG, s.toString());
                        }
                    })

                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object s) throws Exception {
                            mainView.refreshResultScalar(s.toString());
                        }
                    });

        } catch (Exception ex) {
            mainView.ExceptionOccurred(ex);
        }
    }


    public void getProductDetails(final int product_id) {
        try {
            /*
            Observable.fromCallable(new Callable<Beer>() {
                @Override
                public Beer call() {
                    Beer list = mDb.beerDao().loadById(product_id);
                    return list;
                }
            })
                    .filter(new Predicate<Beer>() {
                        @Override
                        public boolean test(@NonNull Beer beer) throws Exception {
                            return beer.getId() > 0;
                        }
                    })
                    .map(new Function<Beer, Beer>() {
                        @Override
                        public Beer apply(@NonNull Beer beer) throws Exception {
                            Utilities.truncate(beer.getDescription(), 500);
                            return beer;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Beer>() {
                        @Override
                        public void accept(Beer beerList) throws Exception {
                            mainView.refreshResult(beerList);
                        }
                    });
                    */
        } catch (Exception ex) {
            mainView.ExceptionOccurred(ex);
        }
    }
}