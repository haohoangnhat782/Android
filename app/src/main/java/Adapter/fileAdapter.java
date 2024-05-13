package Adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.week10.MainActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.week10.MusicPlayerActivity;
import com.example.week10.MyFile;
import com.example.week10.R;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

/** @noinspection ALL*/
public class fileAdapter extends RecyclerView.Adapter<fileAdapter.ViewHolder> {
    final private MainActivity mainActivity;
    final private List<MyFile> fileList;
    private File currentDirectory;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewNamefile;
        TextView textViewNameAlbum;
        ImageView imageViewIconfile;

        LinearLayout itemfile;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNamefile = itemView.findViewById(R.id.textViewNamefile);
            textViewNameAlbum = itemView.findViewById(R.id.textViewNameAlbum);
            imageViewIconfile = itemView.findViewById(R.id.imageViewIconfile);
            itemfile = itemView.findViewById(R.id.item_file);
        }
    }
    public void setCurrentDirectory(File directory) {
        this.currentDirectory = directory;
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }


    public fileAdapter(List<MyFile> fileList, MainActivity mainActivity) {
        this.fileList = fileList;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MyFile currentFile = fileList.get(position);
            holder.textViewNamefile.setText(currentFile.getName());


        if (currentFile.isDirectory()) {
            holder.imageViewIconfile.setImageResource(R.drawable.icon_folder);
            holder.textViewNameAlbum.setText(""); // Xóa nội dung album
        } else if (isMusicFile(currentFile)) {

            try {
                String album = getAlbum(currentFile.getPath());

                if (!album.isEmpty()) {
                    holder.textViewNameAlbum.setText("("+album+")");
                } else {
                    holder.textViewNameAlbum.setText("Unknown Album");
                }
            } catch (IOException e) {
                e.printStackTrace();
                holder.textViewNameAlbum.setText("");
            }
            holder.imageViewIconfile.setImageResource(R.drawable.icon_music);

            // Xóa ảnh hiện tại để tránh việc hiển thị ảnh không đúng cho các vị trí tái sử dụng
            holder.imageViewIconfile.setImageBitmap(null);
            // Kiểm tra xem file có ảnh hay không
            try {
                if (hasImageFile(currentFile.getPath())&&isMusicFile(currentFile)) {
                    // Bắt đầu tải ảnh nhạc bất đồng bộ
                    mainActivity.hasMusicFile = true;
                    new LoadMusicImageTask(holder.imageViewIconfile).execute(currentFile.getPath());
                }else{
                    holder.imageViewIconfile.setImageResource(R.drawable.icon_music);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            holder.imageViewIconfile.setImageResource(R.drawable.icon_folder);
            holder.textViewNameAlbum.setText(""); // Xóa nội dung album
        }


        holder.itemfile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // chạm vào, thay đổi màu nền
                        holder.itemfile.setBackgroundColor(Color.BLUE);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // bỏ chạm khôi phục màu nền ban đầu
                        holder.itemfile.setBackgroundColor(Color.WHITE);
                        break;
                }
                return false;
            }



        });



        holder.itemView.setOnClickListener(v -> {
            // Kiểm tra xem tệp tin được nhấn vào có phải là thư mục hay không
            if (currentFile.isDirectory()) {
                // Thực hiện logic khi nhấn vào thư mục
                mainActivity.traverseDirectory(new File(currentFile.getPath()));

            } else if (isMusicFile(currentFile)) {
                String musicFilePath = currentFile.getPath();

                // Chuyển sang MusicPlayerActivity và truyền đường dẫn file nhạc qua Intent
                Intent intent = new Intent(mainActivity, MusicPlayerActivity.class);
                intent.putExtra("musicFilePath", musicFilePath);
                intent.putExtra("musicPath", currentDirectory.getPath());
                mainActivity.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }




    // Kiểm tra xem tệp tin có phải là tệp nhạc hay không
    private boolean isMusicFile(MyFile file) {
        String extension = file.getExtension();
        return extension != null && (extension.equalsIgnoreCase(".mp3")
                || extension.equalsIgnoreCase(".wav")
                || extension.equalsIgnoreCase(".flac"));
    }

    private String getAlbum(String filePath) throws IOException {
        // Logic để lấy thông tin album từ đường dẫn tệp tin
        // Ví dụ: sử dụng MediaMetadataRetriever để lấy album từ file nhạc

        // Tạo một đối tượng MediaMetadataRetriever
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        // Lấy thông tin album
        String album = null;
        try {
            retriever.setDataSource(filePath);
            album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        } finally {
            // Giải phóng đối tượng MediaMetadataRetriever
            retriever.release();
        }
        return album;
    }

    private boolean hasImageFile(String filePath) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            byte[] albumArt = retriever.getEmbeddedPicture();
            return albumArt != null && albumArt.length > 0;
        } finally {
            if (retriever != null) {
                try {
                    retriever.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static class LoadMusicImageTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        LoadMusicImageTask(ImageView imageView) {
            imageViewReference = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String filePath = params[0];
            MediaMetadataRetriever retriever= new MediaMetadataRetriever();
            retriever.setDataSource(filePath);

            byte[] albumArt = retriever.getEmbeddedPicture();
            if (albumArt != null && albumArt.length > 0) {
                return BitmapFactory.decodeByteArray(albumArt, 0, albumArt.length);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }




}