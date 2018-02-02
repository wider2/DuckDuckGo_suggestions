package rx.rxjavasearch.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.IgnoreWhen;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.subjects.PublishSubject;
import retrofit2.Retrofit;
import rx.rxjavasearch.R;
import rx.rxjavasearch.RxJavaSearchApplication;
import rx.rxjavasearch.dagger.AppModule;

import rx.rxjavasearch.dagger.DaggerPersistentComponent;
import rx.rxjavasearch.db.Phrase;
import rx.rxjavasearch.model.DuckGoSuggestion;
import rx.rxjavasearch.utils.SharedStatesMap;


public class HomeFragment extends Fragment implements IHomeFragment {

    private static final String TAG = "SEARCH_API";
    SharedStatesMap mSharedStates;
    HomeFragmentPresenter mPresenter;
    int productId;

    @BindView(R.id.tv_output)
    TextView tvOutput;
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.et_query)
    EditText etQuery;

    @Inject
    SharedPreferences sharedPreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RxJavaSearchApplication app = RxJavaSearchApplication.getApp();
        DaggerPersistentComponent.builder().appModule(new AppModule(app)).build().inject(this);
    }

    @AfterViews
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, v);
        try {
            mPresenter = new HomeFragmentPresenter(this, getContext());
            mSharedStates = SharedStatesMap.getInstance();
            productId = mSharedStates.getKeyInt("productId");
            //mSharedStates.setKeyInt("detailsDisplay", 1);


            String key = sharedPreferences.getString("keyPass","");
            Log.wtf(TAG, key);


            etQuery.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mPresenter.getSearch(s.toString());
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return v;
    }

/*
    @UiThread
    @Override
    public void clearAdapter() {
        tvResult.setText("");
    }
*/

    @UiThread
    @Override
    public void refreshResultList(List<DuckGoSuggestion> list) {
        int iCount = 0;
        try {
            tvResult.setText("");
            if (!list.isEmpty()) {
                for (DuckGoSuggestion item : list) {
                    iCount += 1;
                    tvResult.append(iCount + ": " + item.phrase + "\n\n");
                }
            } else {
                tvResult.setText(getString(R.string.not_found));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @UiThread
    @Override
    public void refreshResultScalar(String result) {
        try {
            tvResult.setText(result);


            /*
            mBeer = result;
            if (result.getFavorite() == 0) {
                ivFavorite.setImageResource(R.drawable.favor0);
            } else {
                ivFavorite.setImageResource(R.drawable.favor);
            }

            if (result.getId() == 0) {
                dl.setVisibility(View.GONE);
                svDetails.setVisibility(View.GONE);
                tvOutput.setText(getString(R.string.info_not_found));
            } else {
                dl.setVisibility(View.VISIBLE);
                svDetails.setVisibility(View.VISIBLE);

                tvOutput.setText("");
                tvMain.setText(result.getBeerName());
                tvName.setText(result.getBeerName());
                tvAbv.setText("" + result.getAbv());
                tvIbu.setText("" + result.getIbu());
                tvEbc.setText("" + result.getEbc());

                tvTagline.setText(result.getTagLine());
                tvDescription.setText(result.getDescription());
                tvBrewed.setText(result.getFirstBrewed());
                tvSrm.setText("" + result.getSrm());
                tvPh.setText("" + result.getPh());
                tvLevel.setText("" + result.getAttenuationLevel());
                tvVolume.setText(result.getVolumeValue() + " " + result.getVolumeUnit());
                tvBoilVolume.setText(result.getBoilVolume() + " " + result.getBoilVolumeUnit());
                tvMashTemp.setText(result.getMashTempValue() + " " + result.getMashTempUnit() + ", " + getString(R.string.duration) + " " + result.getMashDuration());
                tvFermentation.setText(result.getFermentationTempValue() + " " + result.getFermentationTempUnit());
                tvFoodPairing.setText(result.getFoodPairing());
                tvBrewersTips.setText(result.getBrewersTips());
                tvBy.setText(result.getContributedBy());

                Picasso.with(getContext())
                        .load(result.getImageUrl())
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.noimage)
                        .into(kenBurnsView);
            }
            mProgressBar.setVisibility(View.GONE);
*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @UiThread
    @IgnoreWhen(IgnoreWhen.State.VIEW_DESTROYED)
    @Override
    public void ExceptionOccurred(Exception ex) {

        tvResult.setText(ex.getMessage());
        ex.printStackTrace();
    }


    @UiThread
    @Override
    public void showErrorServerResponse(Throwable response) {
        mSharedStates.setKeyInt("favoriteDisplay", 0);
        mSharedStates.setKeyInt("detailsDisplay", 0);

        String msg = response.getMessage();
        tvResult.setText(getString(R.string.server_response) + ": " + msg + "\n" + response.getStackTrace());
        if (msg.contains("Unable to resolve host") || msg.contains("No address associated with hostname") || msg.contains("403 Forbidden"))
            tvResult.append("\n\n" + getString(R.string.no_response));
/*
        mProgressBar.setVisibility(View.GONE);
        btReload.setVisibility(View.VISIBLE);
        btReload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //mProgressBar.setVisibility(View.VISIBLE);
                //mPresenter.getBeerList(0, sortFlag, offset);
            }
        });
        */
    }

}