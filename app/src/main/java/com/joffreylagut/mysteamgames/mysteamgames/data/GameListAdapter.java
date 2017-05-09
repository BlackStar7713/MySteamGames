package com.joffreylagut.mysteamgames.mysteamgames.data;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joffreylagut.mysteamgames.mysteamgames.R;
import com.joffreylagut.mysteamgames.mysteamgames.models.GameListItem;
import com.joffreylagut.mysteamgames.mysteamgames.ui.GameDetailsActivity;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SharedPreferencesHelper;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SteamAPICalls;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Joffrey on 08/02/2017.
 */

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GamesListViewHolder> {

    private List<GameListItem> gameList;
    private Context context;
    private String recyclerName;

    public GameListAdapter(List<GameListItem> gameList, Context context, String recyclerName) {
        this.gameList = gameList;
        this.context = context;
        this.recyclerName = recyclerName;
    }

    @Override
    public GamesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_game_list, parent, false);
        return new GamesListViewHolder(view);
    }

    public List<GameListItem> getGameList() {
        return this.gameList;
    }

    public void setGameList(List<GameListItem> gameList) {
        this.gameList = gameList;
    }

    @Override
    public void onBindViewHolder(GamesListViewHolder holder, int position) {
        GameListItem game = gameList.get(position);
        holder.bind(game);
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    interface ListItemClickListener {
        void ListItemClicked(String clickedItemName);
    }

    class GamesListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView name;
        TextView timePlayed;
        TextView gamePrice;


        GamesListViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.iv_game_picture);
            name = (TextView) itemView.findViewById(R.id.tv_game_name);
            timePlayed = (TextView) itemView.findViewById(R.id.tv_game_time_played);
            gamePrice = (TextView)itemView.findViewById(R.id.tv_game_price_per_hour);

            itemView.setOnClickListener(this);
        }

        void bind(GameListItem gameItem) {
            // We display the game image
            if(gameItem.getGameImage() != null){
            Picasso.with(image.getContext()).load(gameItem.getGameImage().toString()).into(image);
            }
            // We display the game name
            name.setText(gameItem.getGameName());
            // We display the time played
            String stringTimePlayed = SteamAPICalls.convertTimePlayed(gameItem.getGameTimePlayed());
            if (stringTimePlayed.compareTo("0mn") == 0) {
                timePlayed.setText(context.getResources().getString(R.string.never_played));
            } else {
                timePlayed.setText(stringTimePlayed);
            }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            double nbHours = (double) gameItem.getGameTimePlayed() / 60;
            String gamePriceFinal = String.valueOf(gameItem.getGamePrice());
            switch (gamePriceFinal) {
                case "-1.0":
                    gamePrice.setText("");
                    gamePrice.setVisibility(View.INVISIBLE);
                    break;
                case "0.0":
                    gamePrice.setText(context.getResources().getString(R.string.free));
                    gamePrice.setVisibility(View.VISIBLE);
                    break;
                default:
                    double pricePerHour = gameItem.getGamePrice() / nbHours;
                    DecimalFormat df = new DecimalFormat("#.##");
                    if (gameItem.getGameTimePlayed() == 0) {
                        gamePrice.setText("— " + sharedPreferences.getString(SharedPreferencesHelper.CURRENCY, "$") + "/h");
                    } else {
                        gamePrice.setText(String.valueOf(df.format(pricePerHour)) + " " + sharedPreferences.getString(SharedPreferencesHelper.CURRENCY, "$") + "/h");
                    }
                    gamePrice.setVisibility(View.VISIBLE);
                    break;
            }

        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Intent intent = new Intent(v.getContext(), GameDetailsActivity.class);
            intent.putExtra("gameID", gameList.get(clickedPosition).getGameID());
            intent.putExtra("userID", gameList.get(clickedPosition).getUserID());
            intent.putExtra("recyclerName", recyclerName);
            intent.putExtra("adapterPosition", clickedPosition);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                Bundle bndlAnimation = ActivityOptions.makeCustomAnimation(v.getContext(),
                        R.transition.right_to_left_incoming, R.transition.right_to_left_outgoing)
                        .toBundle();
                ((Activity) context).startActivityForResult(intent, 1, bndlAnimation);
            } else {
                ((Activity) context).startActivityForResult(intent, 1);
            }

        }
    }
}


