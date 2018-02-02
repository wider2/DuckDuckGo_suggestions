package rx.rxjavasearch.home;

import java.util.List;

import dagger.Provides;
import rx.rxjavasearch.db.Phrase;
import rx.rxjavasearch.model.DuckGoSuggestion;

public interface IHomeFragment {

    void refreshResultList(List<DuckGoSuggestion> list);

    void refreshResultScalar(String list);

    void showErrorServerResponse(Throwable response);

    void ExceptionOccurred(Exception ex);

    //void clearAdapter();
}
