package com.centaurslogic.feedburner.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.centaurslogic.feedburner.R;

import java.io.Serializable;

public class NetworkTroublesFragment extends Fragment {
    private final static String RETRY_LISTENER_ARG = "onRetry";

    private OnRetryListener onRetryListener;

    public interface OnRetryListener extends Serializable {
        Fragment onRetryGetBackFragment();
    }

    public static NetworkTroublesFragment getInstance(OnRetryListener onRetryListener){
        Bundle args = new Bundle();
        args.putSerializable(RETRY_LISTENER_ARG, onRetryListener);
        NetworkTroublesFragment networkTroublesFragment = new NetworkTroublesFragment();
        networkTroublesFragment.setArguments(args);
        return networkTroublesFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onRetryListener = (OnRetryListener) getArguments().getSerializable(RETRY_LISTENER_ARG);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_network_troubles, container, false);
        Button retryButton = view.findViewById(R.id.retry_connection_button);
        retryButton.setOnClickListener(onButtonClickListener);
        return view;
    }

    private View.OnClickListener onButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            goBackToPreviousFragment();
        }
    };

    public void goBackToPreviousFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, onRetryListener.onRetryGetBackFragment())
                .commit();
    }
}
