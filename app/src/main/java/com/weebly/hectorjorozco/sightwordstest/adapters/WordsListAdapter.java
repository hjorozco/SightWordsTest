package com.weebly.hectorjorozco.sightwordstest.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.models.Word;

import java.util.List;

/**
 * This Adapter creates and binds ViewHolders, that hold a word and a boolean representing its
 * pressed state in the GridView, to a RecyclerView to efficiently display data.
 */
public class WordsListAdapter extends RecyclerView.Adapter<WordsListAdapter.WordViewHolder> {

    private List<Word> mWords;
    private final Context mContext;

    private final boolean mDefaultTest;

    // The adapter constructor
    public WordsListAdapter(Context context, boolean defaultTest) {
        mContext = context;
        mDefaultTest = defaultTest;
    }


    // Called when ViewHolders are created to fill the RecyclerView.
    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.word_list_item, parent, false);

        return new WordViewHolder(view);
    }


    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {

        Word word = mWords.get(position);

        holder.wordTextView.setText(word.getWord());

        // Sets the background color of the displayed word depending on its pressed state
        if (word.getPressed()) {
            holder.wordTextView.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed));
        } else {
            holder.wordTextView.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
        }
    }


    // Returns the number of items to display.
    @Override
    public int getItemCount() {
        if (mWords == null) {
            return 0;
        }
        return mWords.size();
    }


    // Returns the list of Words objects.
    public List<Word> getWordsListData() {
        return mWords;
    }


    // When data changes, this method updates the list of Student Entries
    // and notifies the adapter to use the new values on it
    public void setWordsListData(List<Word> words) {
        mWords = words;
        notifyDataSetChanged();
    }


    // Inner class for creating ViewHolders
    class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView wordTextView;

        WordViewHolder(View view) {
            super(view);
            wordTextView = view.findViewById(R.id.word_list_item_text_view);

            if (!mDefaultTest){
                wordTextView.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryDark));
            }

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            Word word = mWords.get(getAdapterPosition());

            // Checks the pressed state of the word before the click and changes its background color
            // and pressed stated after the click
            if (word.getPressed()) {
                wordTextView.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
                word.setPressed(false);
            } else {
                wordTextView.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed));
                word.setPressed(true);
            }
        }
    }
}