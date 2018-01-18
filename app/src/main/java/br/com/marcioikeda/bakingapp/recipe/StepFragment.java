package br.com.marcioikeda.bakingapp.recipe;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import br.com.marcioikeda.bakingapp.R;
import br.com.marcioikeda.bakingapp.model.Recipe;
import br.com.marcioikeda.bakingapp.model.Step;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link RecipeActivity}
 * in two-pane mode (on tablets) or a {@link StepActivity}
 * on handsets.
 */
public class StepFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String RECIPE_ID = "recipe_id";
    public static final String STEP_ID = "step_id";
    public static final String PLAYER_POSITION_KEY = "video_position_key";
    public static final String PLAYER_STATE_KEY = "video_state_key";

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;

    static boolean isActive = false;
    private Uri mediaUri;
    private long playerPosition = 0;
    private boolean isPlaying = true;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_step, container, false);

        mPlayerView = rootView.findViewById(R.id.playerView);
        RecipeDetailViewModel mViewModel = ViewModelProviders.of(getActivity()).get(RecipeDetailViewModel.class);

        if (savedInstanceState != null) {
            playerPosition = savedInstanceState.getLong(PLAYER_POSITION_KEY);
            isPlaying = savedInstanceState.getBoolean(PLAYER_STATE_KEY);
        }

        if (getArguments().containsKey(RECIPE_ID) && getArguments().containsKey(STEP_ID)){
            int recipeId = getArguments().getInt(RECIPE_ID);
            final int stepId = getArguments().getInt(STEP_ID);

            mViewModel.getRecipe(recipeId).observe(this, new Observer<Recipe>() {
                @Override
                public void onChanged(@Nullable Recipe recipe) {
                    boolean stepFound = false;
                    for (Step step : recipe.getSteps()) {
                        if (step.getId() == stepId) {
                            stepFound = true;
                            bindStep(rootView, step);
                        }
                    }
                    if (!stepFound) {
                        Snackbar.make(rootView, R.string.message_step_notfound, Snackbar.LENGTH_SHORT);
                    }

                }
            });
        } else {
            Snackbar.make(rootView, R.string.message_step_notfound, Snackbar.LENGTH_SHORT);
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        gainVisibility();
    }

    @Override
    public void onPause() {
        super.onPause();
        loseVisibility();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mExoPlayer != null) {
            outState.putLong(PLAYER_POSITION_KEY, playerPosition);
            outState.putBoolean(PLAYER_STATE_KEY, isPlaying);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
    }

    public void loseVisibility() {
        if (mExoPlayer != null) {
            playerPosition = mExoPlayer.getCurrentPosition();
            isPlaying = mExoPlayer.getPlayWhenReady();
            mExoPlayer.setPlayWhenReady(false);
        }
        isActive = false;
    }

    public void gainVisibility() {
        isActive = true;
        if (mediaUri != null) {
            initializePlayer(mediaUri);
        }
    }

    private void bindStep(View view, final Step step) {
        TextView textView = view.findViewById(R.id.item_detail);
        if (textView != null) {
            textView.append(step.getDescription());
        }

        if (!TextUtils.isEmpty(step.getVideoURL())) {
            mediaUri = Uri.parse(step.getVideoURL());
            // Initialize only if it is running
            if (isActive) {
                initializePlayer(mediaUri);
            }
        }
        if (!TextUtils.isEmpty(step.getThumbnailURL())) {
            Picasso.with(getActivity()).load(step.getThumbnailURL()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    mPlayerView.setDefaultArtwork(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    if (TextUtils.isEmpty(step.getVideoURL())) {
                        mPlayerView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
        if (TextUtils.isEmpty(step.getVideoURL() + step.getThumbnailURL())) {
            mPlayerView.setVisibility(View.GONE);
        }
    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            //LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);
            //mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getActivity(), getResources().getString(R.string.app_name));

            MediaSource mediaSource = new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(mediaUri);
            mExoPlayer.prepare(mediaSource);

            mExoPlayer.seekTo(playerPosition);
            mExoPlayer.setPlayWhenReady(isPlaying);
        }
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

}
