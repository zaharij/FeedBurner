package com.centaurslogic.feedburner.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.centaurslogic.feedburner.R;

public class FeedFragment extends Fragment {
    private final static String FEED_URL = "feedUrl";
    private WebView webView;

    public static FeedFragment getInstance(String feedUrl){
        Bundle args = new Bundle();
        args.putString(FEED_URL, feedUrl);
        FeedFragment feedFragment = new FeedFragment();
        feedFragment.setArguments(args);
        return feedFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        final ProgressBar progressBar = view.findViewById(R.id.detail_progress_bar);
        webView = view.findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
        });
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        }
        else {
            if (getArguments() != null && getArguments().getString(FEED_URL) != null){
                webView.loadUrl(getArguments().getString(FEED_URL));
            }
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }
}
