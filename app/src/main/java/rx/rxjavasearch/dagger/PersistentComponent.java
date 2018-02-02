package rx.rxjavasearch.dagger;

import javax.inject.Singleton;

import dagger.Component;
import rx.rxjavasearch.MainActivity;
import rx.rxjavasearch.dagger.scope.PerActivity;
import rx.rxjavasearch.home.HomeFragment;


@Singleton
@PerActivity
@Component(
        //dependencies = ApplicationComponent.class,
        modules = {AppModule.class, PersistentModule.class}
)
public interface PersistentComponent {

    void inject(MainActivity activity);

    void inject(HomeFragment homefragment);

}
