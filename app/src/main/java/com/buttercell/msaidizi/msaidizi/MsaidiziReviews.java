package com.buttercell.msaidizi.msaidizi;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.buttercell.msaidizi.R;
import com.buttercell.msaidizi.model.Request;
import com.buttercell.msaidizi.model.Review;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MsaidiziReviews extends Fragment {

    private static final String TAG = "MsaidiziReviews";
    @BindView(R.id.review_list)
    RecyclerView reviewList;
    Unbinder unbinder;
    FirebaseRecyclerAdapter<Review, ReviewViewHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_msaidizi_reviews, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Reviews");

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Msaidizi")
                .child(id)
                .child("userReviews");

        FirebaseRecyclerOptions<Review> options =
                new FirebaseRecyclerOptions.Builder<Review>()
                        .setQuery(query, Review.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Review, ReviewViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReviewViewHolder holder, int position, @NonNull Review model) {

                holder.setRating(model.getReview_rating());
                holder.setReviewComments(model.getReview_comments());
                holder.setReviewerName(model.getReviewer_name());
                holder.setUserImage(model.getReviewer_image(), getContext());


            }

            @Override
            public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.review_layout, parent, false);

                return new ReviewViewHolder(view);
            }
        };

        reviewList.setHasFixedSize(true);
        reviewList.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewList.setAdapter(adapter);
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        public ReviewViewHolder(View itemView) {
            super(itemView);

        }

        public void setUserImage(String image, Context context) {
            CircleImageView imageView = itemView.findViewById(R.id.userImage);
            Glide.with(context).load(image).into(imageView);
        }

        public void setReviewerName(String name) {
            TextView txtUserName = itemView.findViewById(R.id.reviewName);
            txtUserName.setText(name);
        }

        public void setReviewComments(String comments) {
            TextView txtComments = itemView.findViewById(R.id.review_comments);
            txtComments.setText(comments);
        }

        public void setRating(int rating) {
            TextView txtRating = itemView.findViewById(R.id.reviewRating);
            String textRating="";

            switch (rating)
            {
                case 1:
                    textRating="Very Bad";
                    break;
                case 2:
                    textRating="Not Good";
                    break;
                case 3:
                    textRating="Quite Ok";
                    break;
                case 4:
                    textRating="Excellent";
                    break;
            }

            txtRating.setText(textRating);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
