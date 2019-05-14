package com.weebly.hectorjorozco.sightwordstest.utils;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.MotionEvent;

import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments.MessageDialogFragment;

import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.EMPTY_STRING;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.WORD_LISTS_INFORMATION_FRAGMENT;

/**
 * Class that makes html links on a TextView text clickable. It handles the click on two links, one
 * that shows Dolch words DialogFragment and another that shows Fry words DialogFragment.
 */
public class CustomLinkMovement extends LinkMovementMethod {

    private final Context mContext;
    private final FragmentManager mFragmentManager;

    public interface CustomLinkMovementListener {
        void onAddStudentsLinkClicked();
        void onLoadStudentsLinkClicked();
        void onCreateSampleClassLinkClicked();
        void onHelpLinkClicked();
    }

    public CustomLinkMovement(Context context, FragmentManager fragmentManager){
        mContext = context;
        mFragmentManager = fragmentManager;
    }

    public boolean onTouchEvent(android.widget.TextView widget, android.text.Spannable buffer, android.view.MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
            if (link.length != 0) {
                String url = link[0].getURL();
                if (url.startsWith("Dolch")) {
                    showDolchWordsDialogFragment();
                } else if (url.startsWith("Fry")) {
                    showFryWordsDialogFragment();
                } else if (url.startsWith("Add")) {
                    CustomLinkMovementListener listener = (CustomLinkMovementListener) mContext;
                    listener.onAddStudentsLinkClicked();
                } else if (url.startsWith("Load")) {
                    CustomLinkMovementListener listener = (CustomLinkMovementListener) mContext;
                    listener.onLoadStudentsLinkClicked();
                }else if (url.startsWith("Sample")) {
                    CustomLinkMovementListener listener = (CustomLinkMovementListener) mContext;
                    listener.onCreateSampleClassLinkClicked();
                }else if (url.startsWith("Help")) {
                    CustomLinkMovementListener listener = (CustomLinkMovementListener) mContext;
                    listener.onHelpLinkClicked();
                }
                return true;
            }
        }

        return super.onTouchEvent(widget, buffer, event);
    }

    private void showDolchWordsDialogFragment(){
        MessageDialogFragment messageDialogFragment =
                MessageDialogFragment.newInstance(
                        Html.fromHtml(mContext.getString(R.string.fragment_word_lists_information_dolch_words_dialog_fragment_text)),
                        mContext.getString(R.string.fragment_word_lists_information_dolch_words_dialog_fragment_title),
                        true, WORD_LISTS_INFORMATION_FRAGMENT, EMPTY_STRING);

        messageDialogFragment.show(mFragmentManager,
                mContext.getString(R.string.fragment_word_lists_information_dolch_words_dialog_fragment_tag));
    }

    private void showFryWordsDialogFragment(){
        MessageDialogFragment messageDialogFragment =
                MessageDialogFragment.newInstance(
                        Html.fromHtml(mContext.getString(R.string.fragment_word_lists_information_fry_words_dialog_fragment_text)),
                        mContext.getString(R.string.fragment_word_lists_information_fry_words_dialog_fragment_title),
                        true, WORD_LISTS_INFORMATION_FRAGMENT, EMPTY_STRING);

        messageDialogFragment.show(mFragmentManager,
                mContext.getString(R.string.fragment_word_lists_information_fry_words_dialog_fragment_tag));
    }

}
