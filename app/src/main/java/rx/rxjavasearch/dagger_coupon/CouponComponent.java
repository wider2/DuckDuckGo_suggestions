package rx.rxjavasearch.dagger_coupon;

import dagger.Component;
import rx.rxjavasearch.MainActivity;

@Component(modules={CouponModule.class})
public interface CouponComponent {

    //public void inject(MainActivity mainActivity);

    CouponRepository getCouponRep();

}