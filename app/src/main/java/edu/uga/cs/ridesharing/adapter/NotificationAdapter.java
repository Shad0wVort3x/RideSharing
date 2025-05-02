package edu.uga.cs.ridesharing.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import edu.uga.cs.ridesharing.R;
/**
 * Adapter to display list of notifications.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<String> notifications;
    /**
     * Constructor.
     * @param notifications list of notification messages.
     */
    public NotificationAdapter(List<String> notifications) {
        this.notifications = notifications;
    }
    /**
     * Inflates item layout for each notification.
     * @param parent   parent view group.
     * @param viewType view type of the new view.
     * @return A new NotificationViewHolder instance.
     */
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }
    /**
     * Bind a notification message to the ViewHolder.
     * @param holder   ViewHolder to bind data to.
     * @param position position of item in list.
     */
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.messageTextView.setText(notifications.get(position));
    }
    /**
     * Returns number of items in notification list.
     * @return Total number of notifications.
     */
    @Override
    public int getItemCount() {
        return notifications.size();
    }
    /**
     * ViewHolder for a single notification.
     */
    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.notificationMessageTextView);
        }
    }
}
