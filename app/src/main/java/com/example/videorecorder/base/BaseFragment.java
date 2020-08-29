package com.example.videorecorder.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import butterknife.Unbinder;

public class BaseFragment extends Fragment {


    protected Unbinder binder;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // hide keyboard
    protected void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void switchFragment(int id, Fragment fragment) {
        getChildFragmentManager().beginTransaction().replace(id, fragment).commitAllowingStateLoss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        binder.unbind();
        binder = null;
    }

}
