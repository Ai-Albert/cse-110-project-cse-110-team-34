package view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.team34.cse_110_project_team_34.R;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import model.User;

public class LocationView {

    public final View itemView;
    public final TextView nameView;
    public final ImageView statusView;

    public LocationView(User user, View view) {
        this.itemView = view;
        this.nameView = itemView.findViewById(R.id.location_name);
        this.statusView = itemView.findViewById(R.id.indicator);
        update(user);
    }

    public void update(User user) {
        // TODO: Add positioning logic

        nameView.setText(user.getName());
        if (user.getLastUpdated() + 60 >= Instant.now().getEpochSecond()) {
            statusView.setImageResource(R.drawable.green_indicator);
        } else {
            statusView.setImageResource(R.drawable.red_indicator);
        }
    }
}
