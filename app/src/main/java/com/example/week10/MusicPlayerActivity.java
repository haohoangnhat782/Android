package com.example.week10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.media.MediaMetadataRetriever;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;



public class MusicPlayerActivity extends AppCompatActivity {


    private MediaPlayer mediaPlayer;
    private ArrayList<String> musicFilesList;
    private int pauseIcon = android.R.drawable.ic_media_pause;
    private int playIcon = android.R.drawable.ic_media_play;

    private TextView textViewNameMusic;
    private ImageView imageView;
    private TextView textViewNameSinger;

    private SeekBar seekBarCurrentTime;

    private boolean isSeekBarDragging = false;

    private Handler handler = new Handler();
    private TextView textViewRemainingTime;
    private Integer currentIndex = 0;
     private String currentDirectoryPath ;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        // Khởi tạo các thành phần giao diện
        imageView = findViewById(R.id.imgMusic);
        textViewNameMusic = findViewById(R.id.NameMusic);
        textViewNameMusic.setTextColor(Color.rgb(255,192,203));

        textViewNameSinger = findViewById(R.id.NameSinger);
        textViewNameSinger.setTextColor(Color.rgb(255,192,203));

        ImageView buttonPre = findViewById(R.id.buttonPre);
        ImageView buttonPause = findViewById(R.id.buttonPause);
        buttonPause.setImageResource(R.drawable.icon_pause);
        ImageView buttonNext = findViewById(R.id.buttonNext);
        seekBarCurrentTime = findViewById(R.id.seekBar);
        textViewRemainingTime = findViewById(R.id.remainingTime);
        textViewRemainingTime.setTextColor(Color.rgb(255,192,203));

        seekBarCurrentTime.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        seekBarCurrentTime.setThumb(getResources().getDrawable(R.drawable.icon_thumb));
//        seekBarCurrentTime.setBackgroundColor(Color.MAGENTA);
        currentDirectoryPath = getIntent().getStringExtra("musicPath");

        // Lấy danh sách các file nhạc từ thư mục hiện tại
        musicFilesList = getMusicFilesFromCurrentDirectory();

            String musicFilePath = getIntent().getStringExtra("musicFilePath");


            if (musicFilePath == null){
                playMusicAtIndex(musicFilesList.get(currentIndex));
            }else {
                playMusicAtIndex(musicFilePath);
            }



            seekBarCurrentTime.setMax(mediaPlayer.getDuration());
            seekBarCurrentTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    if (mediaPlayer != null && fromUser) {
                            mediaPlayer.seekTo(progress);
                            seekBarCurrentTime.setProgress(progress);
                            if(mediaPlayer.isPlaying()){
                                // Lấy tổng thời gian của bài nhạc
                                int duration = mediaPlayer.getDuration();

                                // Tính toán phần trăm tiến độ
                                float progressPercent = (float) progress / seekBar.getMax();

                                // Tính toán vị trí mới dựa trên phần trăm tiến độ
                                int position = (int) (duration * progressPercent);

                                // Cập nhật vị trí của MediaPlayer
                                mediaPlayer.seekTo(position);
                            }
                    }

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    isSeekBarDragging = true;
                    mediaPlayer.pause();
                    buttonPause.setImageResource(R.drawable.icon_play);

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    isSeekBarDragging = false;
                    if (mediaPlayer != null && mediaPlayer.isPlaying()||mediaPlayer != null && !mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        buttonPause.setImageResource(R.drawable.icon_pause);

                    }
                }
            });


                // Bắt đầu cập nhật thanh SeekBar và thời gian còn lại
                handler.postDelayed(updateSeekBar, 0);

        // Xử lý sự kiện nút Next
        buttonNext.setOnClickListener(view -> {
           if(currentIndex < musicFilesList.size() - 1){
               currentIndex++;
               playMusicAtIndex(musicFilesList.get(currentIndex));
               buttonPause.setImageResource(R.drawable.icon_pause);

           }
        });

        buttonPause.setOnClickListener(view -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                buttonPause.setImageResource(R.drawable.icon_play);
            } else if(mediaPlayer != null && !mediaPlayer.isPlaying()){
                mediaPlayer.start();
                buttonPause.setImageResource(R.drawable.icon_pause);

            }
        });

        // Xử lý sự kiện nút Previous
        buttonPre.setOnClickListener(view -> {
            if(currentIndex > 0){
                currentIndex--;
                playMusicAtIndex(musicFilesList.get(currentIndex));
                buttonPause.setImageResource(R.drawable.icon_pause);

            }
        });
        mediaPlayer.setOnCompletionListener(mp -> {
            // Kiểm tra nếu currentIndex là bài hát cuối cùng trong danh sách
            buttonPause.setImageResource(R.drawable.icon_play);
            if (currentIndex == musicFilesList.size() - 1) {
                // Dừng phát nhạc và thiết lập currentIndex về 0
                stopAndReleaseMediaPlayer();
                currentIndex = 0;

            } else {
                // Tăng currentIndex lên 1 để chuyển sang bài tiếp theo
                currentIndex++;
                playMusicAtIndex(musicFilesList.get(currentIndex));
                buttonPause.setImageResource(R.drawable.icon_pause);
            }
        });
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Giải phóng tài nguyên khi Activity bị hủy
        stopAndReleaseMediaPlayer();
    }

    private void playMusicAtIndex(String musicFilePath) {
        // Dừng và giải phóng MediaPlayer nếu đang phát nhạc
            stopAndReleaseMediaPlayer();
            mediaPlayer = MediaPlayer.create(this, Uri.parse(musicFilePath));

            // Kiểm tra xem MediaPlayer đã được tạo thành công hay không
            if (mediaPlayer != null) {
                // Phát nhạc
                mediaPlayer.start();
            }

        mediaPlayer.setOnBufferingUpdateListener((mediaPlayer, pc) -> {
            double ratio = pc/100.0;
            int buff = (int) ((mediaPlayer.getDuration()) * ratio);
            seekBarCurrentTime.setSecondaryProgress(buff);
        });


            // Lấy tên của bài hát từ metadata và gán vào textViewNameMusic
            MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
            metadataRetriever.setDataSource(musicFilePath);
            String title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            textViewNameMusic.setText(title);

            // Lấy tên ca sĩ từ metadata và gán vào textViewNameSinger
            String artist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            textViewNameSinger.setText(artist);

            // Lấy ảnh từ metadata và chuyển đổi thành Bitmap
            byte[] artwork = metadataRetriever.getEmbeddedPicture();
            if (artwork != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(artwork, 0, artwork.length);
                imageView.setImageBitmap(bitmap);
            } else {
                // Nếu không tìm thấy ảnh, bạn có thể gán một ảnh mặc định vào imageView
                imageView.setImageResource(R.drawable.icon_music);
            }

    }


    private Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                seekBarCurrentTime.setProgress(currentPosition);
                updateRemainingTime(currentPosition);

            }
            handler.postDelayed(this, 1000); // Cập nhật SeekBar mỗi giây
        }
    };

    private void updateRemainingTime(int currentPosition) {
        int totalDuration = mediaPlayer.getDuration();
        int remainingTime = totalDuration - currentPosition;

        // Kiểm tra nếu thời gian còn lại âm, đặt nó về 0
        if (remainingTime < 0) {
            remainingTime = 0;
        }

        // Định dạng thời gian dạng mm:ss
        String remainingTimeString = String.format("%02d:%02d",
                (remainingTime % 3600000) / 60000,
                (remainingTime % 60000) / 1000);

        textViewRemainingTime.setText(remainingTimeString);
    }
    private void stopAndReleaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }



    private ArrayList<String> getMusicFilesFromCurrentDirectory() {
        ArrayList<String> musicFilesList = new ArrayList<>();


        // Kiểm tra xem đường dẫn thư mục hiện tại có tồn tại không
        if (currentDirectoryPath != null) {
            File currentDirectory = new File(currentDirectoryPath);

            // Kiểm tra xem thư mục hiện tại có tồn tại không
            if (currentDirectory.exists() && currentDirectory.isDirectory()) {
                File[] files = currentDirectory.listFiles();

                // Lặp qua tất cả các tệp trong thư mục hiện tại
                if (files != null) {
                    for (File file : files) {
                        // Kiểm tra xem tệp có phải là tệp nhạc hay không
                        if (isMusicFile(file)) {
                            // Lưu đường dẫn của tệp nhạc vào danh sách
                            musicFilesList.add(file.getPath());
                        }
                    }
                }
            }
        }

        return musicFilesList;
    }


    private boolean isMusicFile(File file) {
        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String[] supportedExtensions = {"mp3", "wav", "ogg"}; // Các định dạng file nhạc hỗ trợ

        for (String extension : supportedExtensions) {
            if (fileExtension.equalsIgnoreCase(extension)) {
                return true;
            }
        }

        return false;
    }

}





