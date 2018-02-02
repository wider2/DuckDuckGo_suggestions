package rx.rxjavasearch.dagger_coupon;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static rx.rxjavasearch.utils.GlobalConstants.SERVER_SSL_URL;

public class CouponRepository {
    @Inject
    public CouponRepository(){};

    public String getAllCoupon(){
        return "here my test coupon";
    }

    @Singleton
    //@Provides
    public Retrofit provideRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .validateEagerly(true)
                .baseUrl(SERVER_SSL_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit;
    }
}
