package com.example.petly;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private String currentUsername;
    private Context context;
    private ExecutorService srv;

    public UserAdapter(Context context, List<User> userList, String currentUsername) {
        this.userList = userList;
        this.currentUsername = currentUsername;
        this.context = context;
        this.srv = Executors.newCachedThreadPool();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userUsername.setText("Username: "+user.getUsername());
        holder.userEmail.setText("User Email: " +user.getEmail());

        Pet pet = user.getPet();

        if (pet != null) {
            Log.i("pet","null degil");
            holder.petName.setText("Pet name:" + pet.getName());
            holder.petType.setText("Pet type: " + pet.getType());
            holder.petAge.setText("Pet Age: " +pet.getAge());
            holder.petGender.setText("Pet Gender: "+ pet.getGender());
            holder.petDescription.setText("Description: "+ pet.getDescription());
            holder.petMeettype.setText("MeetType: "+pet.getMeettype());
        }

        holder.likeButton.setOnClickListener(v -> {
            checkMatches(currentUsername, user.getUsername());
        });
    }

    private void checkMatches(String username1, String username2) {
        PetlyRepository repo = new PetlyRepository();
        String requestBody = "{\"username1\":\"" + username1 + "\",\"username2\":\"" + username2 + "\"}";

        Handler handler = new Handler(msg -> {
            String response = (String) msg.obj;
            Toast.makeText(context, "Match response: " + response, Toast.LENGTH_SHORT).show();
            return true;
        });

        repo.checkMatches(srv, handler, requestBody);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userUsername, userEmail;
        TextView petName, petType, petBreed, petAge, petGender, petDescription, petMeettype;
        Button likeButton;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userUsername = itemView.findViewById(R.id.user_name);
            userEmail = itemView.findViewById(R.id.user_email);
            petName = itemView.findViewById(R.id.pet_name);
            petType = itemView.findViewById(R.id.pet_type);
            petAge = itemView.findViewById(R.id.pet_age);
            petGender = itemView.findViewById(R.id.pet_gender);
            petDescription = itemView.findViewById(R.id.pet_description);
            petMeettype = itemView.findViewById(R.id.pet_meettype);
            likeButton = itemView.findViewById(R.id.like_button);
        }
    }
}
