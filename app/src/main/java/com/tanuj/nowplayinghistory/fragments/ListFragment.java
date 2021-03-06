package com.tanuj.nowplayinghistory.fragments;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tanuj.nowplayinghistory.App;
import com.tanuj.nowplayinghistory.R;
import com.tanuj.nowplayinghistory.Utils;
import com.tanuj.nowplayinghistory.adapters.SongsPagedListAdapter;
import com.tanuj.nowplayinghistory.callbacks.FavoritesItemTouchCallback;
import com.tanuj.nowplayinghistory.callbacks.RecentsItemTouchCallback;
import com.tanuj.nowplayinghistory.viewmodels.SongsListViewModel;

public class ListFragment extends Fragment {

    private static final String EXTRA_SHOW_FAVORITES = "show_favorites";
    private static final String EXTRA_MIN_TIMESTAMP = "min_timestamp";

    private View emptyView;
    private RecyclerView recyclerView;
    private boolean showFavorites;
    private long minTimestamp;
    private SongsPagedListAdapter songsListAdapter;
    private Snackbar notificationAccessSnackbar;

    public static ListFragment newInstance(boolean showFavorites, long minTimestamp) {
        ListFragment fragment = new ListFragment();

        Bundle args = new Bundle();
        args.putBoolean(EXTRA_SHOW_FAVORITES, showFavorites);
        args.putLong(EXTRA_MIN_TIMESTAMP, minTimestamp);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            showFavorites = getArguments().getBoolean(EXTRA_SHOW_FAVORITES);
            minTimestamp = getArguments().getLong(EXTRA_MIN_TIMESTAMP);
        }

        songsListAdapter = new SongsPagedListAdapter();

        SongsListViewModel viewModel = ViewModelProviders.of(this).get(SongsListViewModel.class);
        viewModel.init(App.getDb().songDao(), minTimestamp, showFavorites);
        viewModel.getData().observe(this, songs -> {
            songsListAdapter.submitList(songs);
            setEmptyViewVisibility(Utils.isEmpty(songs));
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        notificationAccessSnackbar = Snackbar.make(container.findViewById(R.id.container),
                "Need notification access", Snackbar.LENGTH_INDEFINITE)
                .setAction("Grant access", v -> startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));

        View view = inflater.inflate(R.layout.list_songs, container, false);
        emptyView = view.findViewById(R.id.empty_view);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        });
        recyclerView.setAdapter(songsListAdapter);
        initItemTouchHelper(recyclerView);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        notificationAccessSnackbar.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Utils.isNotificationAccessGranted()) {
            notificationAccessSnackbar.show();
        }
    }

    private void initItemTouchHelper(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback callback;
        if (showFavorites) {
            callback = new FavoritesItemTouchCallback(recyclerView, songsListAdapter);
        } else {
            callback = new RecentsItemTouchCallback(recyclerView, songsListAdapter);
        }
        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
    }

    private void setEmptyViewVisibility(boolean visible) {
        if (emptyView != null) {
            emptyView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }

        if (recyclerView != null) {
            recyclerView.setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
        }
    }
}
