package com.centaurslogic.feedburner.ui.feeds;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.centaurslogic.feedburner.R;
import com.centaurslogic.feedburner.data.FeedsLoader;
import com.centaurslogic.feedburner.ui.FeedFragment;
import com.centaurslogic.feedburner.ui.NetworkTroublesFragment;
import com.centaurslogic.feedburner.util.NetworkReceiver;
import com.centaurslogic.feedburner.util.XMLParser;

import java.util.List;

public class FeedsFragment extends Fragment {
    private final String PATH = "http://feeds.feedburner.com/blogspot/hsDu.xml";
    private final String LINKS_ARRAY_TAG = "linksArray";
    private final String TITLES_ARRAY_TAG = "titlesArray";
    private NetworkReceiver networkReceiver;
    private ProgressBar progressBar;
    private LinearLayout itemsContainerLayout;
    private String[] links;
    private String[] titles;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);
        progressBar = view.findViewById(R.id.movies_list_progress);
        showProgress(true);
        itemsContainerLayout = view.findViewById(R.id.titles_container_layout);
        return view;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            goToDetail((int) view.getTag());
        }
    };

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putStringArray(LINKS_ARRAY_TAG, links);
        outState.putStringArray(TITLES_ARRAY_TAG, titles);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null
                && savedInstanceState.getStringArray(LINKS_ARRAY_TAG) != null
                && savedInstanceState.getStringArray(TITLES_ARRAY_TAG) != null){
            links = savedInstanceState.getStringArray(LINKS_ARRAY_TAG);
            titles = savedInstanceState.getStringArray(TITLES_ARRAY_TAG);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        networkReceiver = new NetworkReceiver(networkCallback, getContext());
        networkReceiver.registerReceiver();
        if (links == null || titles == null){
            loadFeeds();
        } else {
            for (int i = 0; i < titles.length; i++){
                setItems(titles[i], i);
            }
        }
    }

    private void loadFeeds(){
        FeedsLoader feedsLoader = new FeedsLoader();
        feedsLoader.downloadFeeds(PATH, dataCallback);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(networkReceiver);
        super.onPause();
    }

    private void showProgress(boolean isShow){
        if (isShow){
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void goToNetworkTroublesFragment() {
        NetworkTroublesFragment networkConnectionTroublesFragment
                = NetworkTroublesFragment.getInstance(onRetryListener);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, networkConnectionTroublesFragment)
                .commit();
    }

    private NetworkTroublesFragment.OnRetryListener onRetryListener
            = new NetworkTroublesFragment.OnRetryListener() {
        @Override
        public Fragment onRetryGetBackFragment() {
            return new FeedsFragment();
        }
    };

    private NetworkReceiver.INetworkCallback networkCallback = new NetworkReceiver.INetworkCallback() {
        @Override
        public void onWiFiAvailable() {
            if (isAdded()){
                Toast.makeText(getContext(), getString(R.string.wifi_reconnected), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onGSMAvailable() {
            if (isAdded()) {
                Toast.makeText(getContext(), getString(R.string.gsm_data_available), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onNoConnection() {
            if (isAdded()) {
                Toast.makeText(getContext(), getString(R.string.network_not_available), Toast.LENGTH_LONG).show();
            }
            goToNetworkTroublesFragment();
        }
    };

    private FeedsLoader.IDataCallback<List<XMLParser.ThemeItem>> dataCallback = new FeedsLoader.IDataCallback<List<XMLParser.ThemeItem>>() {
        @Override
        public void onResponse(List<XMLParser.ThemeItem> themeItemList) {
            if (themeItemList != null){
                links = new String[themeItemList.size()];
                titles = new String[themeItemList.size()];
                int counter = 0;
                itemsContainerLayout.removeAllViews();
                for(XMLParser.ThemeItem item: themeItemList){
                    links[counter] = item.getLink();
                    titles[counter] = item.getTitle();
                    setItems(item.getTitle(), counter);
                    counter++;
                }
            }
        }
    };

    private void setItems(String currentTitle, int tag){
        showProgress(false);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.item, null,  false);
        layout.setTag(tag);
        layout.setOnClickListener(onClickListener);
        TextView title = layout.findViewById(R.id.title_text_view);
        title.setText(currentTitle);
        itemsContainerLayout.addView(layout);
    }

    private void goToDetail(int index){
        FeedFragment feedFragment
                = FeedFragment.getInstance(links[index]);
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, feedFragment).addToBackStack(null)
                .commit();
    }
}
