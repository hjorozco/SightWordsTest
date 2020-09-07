package com.weebly.hectorjorozco.sightwordstest.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.utils.CustomLinkMovement;

import java.util.Objects;


public class HelpFragment extends Fragment {


    public HelpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_help, container, false);

        Toolbar toolbar = rootView.findViewById(R.id.fragment_help_tool_bar);
        toolbar.setTitle(getString(R.string.fragment_help_tool_bar_title));

        if (!getResources().getBoolean(R.bool.tablet)) {

            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentActivity fragmentActivity = getActivity();
                    if (fragmentActivity != null) {
                        getActivity().onBackPressed();
                    }
                }
            });
        }

        TextView helpTextView = rootView.findViewById(R.id.fragment_help_text_view);
        helpTextView.setText(Html.fromHtml(getString(R.string.fragment_help_text)));

        helpTextView.setMovementMethod(new CustomLinkMovement(getActivity(),
                Objects.requireNonNull(getActivity()).getSupportFragmentManager()));

        return rootView;
    }

}
