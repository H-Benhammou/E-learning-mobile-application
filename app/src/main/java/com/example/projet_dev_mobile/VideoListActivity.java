package com.example.projet_dev_mobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoListActivity extends AppCompatActivity {

    private LinearLayout videoContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        videoContainer = findViewById(R.id.video_container);

        // Exemples avec de vrais liens YouTube (remplace-les par ceux que tu veux)
        addVideoCard("Introduction au développement mobile Avec Flutter ", "https://www.youtube.com/watch?v=iasOWA6JSKc");
        addVideoCard("Apprendre le WebDesign avec Figma", "https://www.youtube.com/watch?v=MEg05ZOFKtY");
        addVideoCard("API : comprendre l'essentiel", "https://www.youtube.com/watch?v=T0DmHRdtqY0");
        addVideoCard("Comprendre les microservices", "https://www.youtube.com/watch?v=ucHwp1jUS2w");
        addVideoCard("Le Backend pour les Applications Mobiles: Firebase, AWS, MySQL", "https://www.youtube.com/watch?v=77cKpU20QBA");
        addVideoCard("Comment Maîtriser Firebase", "https://www.youtube.com/watch?v=tj3RwdpClMo");

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }

    private void addVideoCard(String title, String url) {
        LinearLayout card = (LinearLayout) getLayoutInflater().inflate(R.layout.item_video_card, null);

        TextView titleText = card.findViewById(R.id.video_title);
        Button playButton = card.findViewById(R.id.play_button);

        titleText.setText(title);
        playButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        videoContainer.addView(card);
    }
}