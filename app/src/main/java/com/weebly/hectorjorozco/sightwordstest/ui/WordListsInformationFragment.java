package com.weebly.hectorjorozco.sightwordstest.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.utils.CustomLinkMovement;


public class WordListsInformationFragment extends Fragment {

    private FragmentActivity mFragmentActivity;


    public WordListsInformationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentActivity = getActivity();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_word_lists_information, container, false);

        Toolbar toolbar = rootView.findViewById(R.id.fragment_word_lists_information_tool_bar);
        toolbar.setTitle(getString(R.string.fragment_word_lists_information_tool_bar_title));

        // If on a phone set up the back navigation icon on the toolbar
        if (!getResources().getBoolean(R.bool.tablet)) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                }
            });
        }

        TextView informationTextView = rootView.findViewById(R.id.fragment_word_lists_information_text_view);
        informationTextView.setText(Html.fromHtml(getString(R.string.fragment_word_lists_information_text)));
        informationTextView.setMovementMethod(new CustomLinkMovement(mFragmentActivity,
                mFragmentActivity.getSupportFragmentManager()));

        return rootView;
    }

}
