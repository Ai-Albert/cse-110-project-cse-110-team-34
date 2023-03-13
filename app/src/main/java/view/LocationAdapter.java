package view;

import android.content.SharedPreferences;
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

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    private List<User> users = Collections.emptyList();
    private String public_uid;

    public LocationAdapter(String public_uid) {
        this.public_uid = public_uid;
    }

    /**
     * This time around, the ViewHolder is much simpler, just data.
     * This is closer to "modern" Kotlin Android conventions.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View itemView;
        public final TextView nameView;
        public final ImageView statusView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            // Populate the text views...
            this.nameView = itemView.findViewById(R.id.location_name);
            this.statusView = itemView.findViewById(R.id.indicator);
        }

        public void bind(User user) {
            nameView.setText(user.getName());
            if (user.getLastUpdated() + 60 >= Instant.now().getEpochSecond()) {
                statusView.setImageResource(R.drawable.green_indicator);
            } else {
                statusView.setImageResource(R.drawable.red_indicator);
            }
        }
    }

    public void setUsers(List<User> users) {
        this.users = users;

        User curr = null;
        for (User u : users) {
            if (u.public_code.equals(public_uid)) {
                curr = u;
                break;
            }
        }
        users.remove(curr);

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public long getItemId(int position) {
        // We don't actually have a unique int/long ID on the Note object, so instead
        // we generate a unique ID based on the title. It is possible that two notes
        // could have different titles but the same hash code, but it is beyond unlikely.
        return users.get(position).getUid().hashCode();
    }
}
