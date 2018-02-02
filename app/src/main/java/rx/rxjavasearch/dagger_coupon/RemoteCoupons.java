package rx.rxjavasearch.dagger_coupon;

import android.telecom.RemoteConnection;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RemoteCoupons {

    @Inject
    RemoteConnection remoteConnection;

    @Inject
    public RemoteCoupons(){}

    public String getRemoteCoupon(){
        return "get remote coupon";
        //return "get remote coupon "+remoteConnection.getCoupon();
    }

}
