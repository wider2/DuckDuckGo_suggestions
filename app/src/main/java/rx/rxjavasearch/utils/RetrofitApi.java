package rx.rxjavasearch.utils;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.rxjavasearch.model.DuckGoSuggestion;

public interface RetrofitApi {

    //https://duckduckgo.com/ac/?q=

    @GET("/ac")
    Observable<String> getSuggestions(@Query("q") String q);

    @GET("/ac")
    Observable<List<DuckGoSuggestion>> getSuggestionsList(@Query("q") String q);

    //@GET("/v2/beers/")
    //Observable<String> getSingleBeer(@Query("key") String key);

}