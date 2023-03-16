package view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.team34.cse_110_project_team_34.R;

import org.w3c.dom.Text;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import model.User;
import utilities.Calculation;

public class LocationView {

    public final View itemView;
    public final TextView nameView;
    public final ImageView statusView;

    public final TextView timeView;

    public LocationView(User user, View view) {
        this.itemView = view;
        this.nameView = itemView.findViewById(R.id.location_name);
        this.statusView = itemView.findViewById(R.id.indicator);
        this.timeView = itemView.findViewById(R.id.last_live);

        update(user);
    }

    public void update(User user) {
        nameView.setText(user.getName());
        if (user.getLastUpdated() + 60 >= Instant.now().getEpochSecond()) {
            statusView.setImageResource(R.drawable.green_indicator);
            timeView.setVisibility(View.INVISIBLE);
        } else {
            statusView.setImageResource(R.drawable.red_indicator);
            long seconds_since_seen = Instant.now().getEpochSecond() - user.getLastUpdated();
            if (seconds_since_seen < 3600) {
                timeView.setText(seconds_since_seen / 60 + "m");
            } else {
                timeView.setText(seconds_since_seen / 3600 + "h");
            }
            timeView.setVisibility(View.VISIBLE);
        }
    }
}
