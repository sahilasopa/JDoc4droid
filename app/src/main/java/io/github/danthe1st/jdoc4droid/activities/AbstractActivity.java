package io.github.danthe1st.jdoc4droid.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.io.UncheckedIOException;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.danthe1st.jdoc4droid.BuildConfig;
import io.github.danthe1st.jdoc4droid.R;
import lombok.Getter;

public class AbstractActivity extends AppCompatActivity {

    private final Map<Integer, Runnable> keyListeners = new HashMap<>();

    @Getter
    private final ExecutorService threadPool = Executors.newSingleThreadExecutor();

    @Getter
    private SearchView searchView;

    private MenuItem shareButton;

    private Toolbar mActionBarToolbar;

    protected static final String ARG_SHARE_URL = "shareUrl";
    private String shareUrl;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Runnable listener = keyListeners.get(keyCode);
        if (listener != null) {
            listener.run();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setListener(int key, Runnable listener) {
        keyListeners.put(key, listener);
    }

    public Runnable getListener(int key) {
        return keyListeners.get(key);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shareUrl = getIntent().getStringExtra(ARG_SHARE_URL);
        if (getSearchView() != null) {
            reloadTopMenuButtons();
            getSearchView().setQuery("", false);
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
    }

    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = findViewById(R.id.include);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        loadOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void loadOptionsMenu(Menu menu){
        searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();

        shareButton = menu.findItem(R.id.app_bar_share);

        EditText searchEditText = searchView.findViewById( searchView.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null));
        searchEditText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_URI|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                onSearchType(newText);
                return true;
            }
        });

        reloadTopMenuButtons();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_bar_share) {
            onShareButton();
        }
        return super.onOptionsItemSelected(item);
    }

    private void onShareButton(){
        String shareLink=getShareLink();
        if(shareLink!=null){
            Intent sendIntent=new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT,shareLink);
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }
    }

    public void reloadTopMenuButtons(){
        if (supportsSearch()) {
            searchView.setVisibility(View.VISIBLE);
        } else {
            searchView.setVisibility(View.GONE);
        }
        shareButton.setVisible(getShareLink() != null);
    }


    public void runInUIThread(Runnable toRun) {
        new Handler(Looper.getMainLooper()).post(toRun);
    }

    public void onSearch(String search) {
        //default implementation
        Log.w(getClass().getName(), "default implementation of onSearch() called");
    }

    public void onSearchType(String search) {
        //default implementation
    }

    public boolean supportsSearch() {
        return false;
    }

    public String getShareLink(){
        return shareUrl;
    }

    protected Void showError(@StringRes int errorMessage, Throwable e){
        if(e instanceof UncheckedIOException &&e.getCause()!=null){
            e=e.getCause();
        }
        Log.e(this.getClass().getName(), getResources().getString(errorMessage), e);
        runInUIThread(()-> Toast.makeText(this, errorMessage,Toast.LENGTH_LONG).show());
        return null;
    }
}