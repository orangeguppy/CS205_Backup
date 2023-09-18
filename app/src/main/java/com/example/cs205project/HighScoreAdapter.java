package com.example.cs205project;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HighScoreAdapter extends RecyclerView.Adapter<HighScoreAdapter.HighScoreViewHolder> {

    private List<HighScore> highScores;

    public HighScoreAdapter(List<HighScore> highScores) {
        this.highScores = highScores;
    }

    @Override
    public HighScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.high_score_row, parent, false);
        return new HighScoreViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(HighScoreViewHolder holder, int position) {
        HighScore highScore = highScores.get(position);
        holder.rankTextView.setText(String.valueOf(highScore.getRank()));
        holder.nameTextView.setText(highScore.getName());
        holder.scoreTextView.setText(String.valueOf(highScore.getScore()));
    }

    @Override
    public int getItemCount() {
        return highScores.size();
    }

    public class HighScoreViewHolder extends RecyclerView.ViewHolder {
        public TextView rankTextView, nameTextView, scoreTextView;

        public HighScoreViewHolder(View view) {
            super(view);
            rankTextView = view.findViewById(R.id.rankTextView);
            nameTextView = view.findViewById(R.id.nameTextView);
            scoreTextView = view.findViewById(R.id.scoreTextView);
        }
    }
}

