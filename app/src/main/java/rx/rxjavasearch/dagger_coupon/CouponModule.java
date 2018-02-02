package rx.rxjavasearch.dagger_coupon;

import dagger.Module;
import dagger.Provides;

@Module
public class CouponModule {

    @Provides
    public CouponRepository getCouponRepository(){
        return new CouponRepository();
    }

}