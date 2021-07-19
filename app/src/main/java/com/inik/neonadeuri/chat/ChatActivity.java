package com.inik.neonadeuri.chat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.inik.neonadeuri.R;
import com.inik.neonadeuri.databinding.ActivityChatBinding;
import com.inik.neonadeuri.filterCam.CameraActivity;
import com.inik.neonadeuri.models.MessageData;
import com.inik.neonadeuri.models.Photo;
import com.inik.neonadeuri.models.RoomData;
import com.inik.neonadeuri.models.User;
import com.inik.neonadeuri.retrofit.Result;
import com.inik.neonadeuri.retrofit.RetrofitClient;
import com.inik.neonadeuri.utils.CameraManager;
import com.inik.neonadeuri.utils.CurrentUserManager;
import com.inik.neonadeuri.utils.JSONManager;
import com.inik.neonadeuri.utils.SaveSharedPreference;
import com.inik.neonadeuri.utils.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.security.AccessController.getContext;


public class ChatActivity extends AppCompatActivity implements CameraManager.PhotoReceiver {
    private static final int PICK_FROM_CAMERA = 0;

    private static final int PICK_FROM_ALBUM = 1;

    private static final int CROP_FROM_iMAGE = 2;

    private Uri mImageCaptureUri;

    private ImageView iv_UserPhoto;

    private int id_view;

    private String absoultePath;
    Photo editProfilePhoto;
    private ActivityChatBinding binding;

    private io.socket.client.Socket mSocket;
    private RetrofitClient retrofitClient;

    private String username;
    private String roomNumber;
    private ChatAdapter adapter;
    private String targets;
    private String targetsid;
    private String chatText;

    public User currentUser = CurrentUserManager.getCurrentUser();
    public Context context;

    private Gson gson = new Gson();

    private final int SELECT_IMAGE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main_color));

        CameraManager.photoReceiver = this;
        context = getApplicationContext();
        getSupportActionBar().hide();

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadCurrent();
        // loadChatNumber();


    }

    private void init() {

        Intent intent = getIntent();
        //username = intent.getStringExtra("userSender");
        username = currentUser.getNickname();
        targets = intent.getStringExtra("userReceiver");
        targetsid = intent.getStringExtra("userReceiverId");
        roomNumber = targets;


        try {
            mSocket = IO.socket("http://218.39.138.57:3000");
            Log.d("SOCKET", "Connection success : " + mSocket.id());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        retrofitClient = RetrofitClient.getInstance();

        adapter = new ChatAdapter(getApplicationContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);

        // 메시지 전송 버튼
        binding.sendBtn.setOnClickListener(v -> sendMessage());
        // 이미지 전송 버튼
        binding.imageBtn.setOnClickListener(v -> {
//            System.out.println("이미지 버튼 눌림");
//            Intent imageIntent = new Intent(Intent.ACTION_PICK);
//            imageIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//            startActivityForResult(imageIntent, SELECT_IMAGE);
            imageShow();
        });

        mSocket.connect();

        mSocket.on(Socket.EVENT_CONNECT, args -> {
            mSocket.emit("enter", gson.toJson(new RoomData(username, roomNumber, targets)));

        });
        mSocket.on("update", args -> {
            MessageData data = gson.fromJson(args[0].toString(), MessageData.class);
            addChat(data);
        });

    }


    // 리사이클러뷰에 채팅 추가
    private void addChat(MessageData data) {
        runOnUiThread(() -> {
            if (data.getType().equals("ENTER") || data.getType().equals("LEFT")) {
                adapter.addItem(new ChatItem(data.getFrom(), data.getContent(), toDate(data.getSendTime()), ChatType.CENTER_MESSAGE));
                binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            } else if (data.getType().equals("IMAGE")) {
                adapter.addItem(new ChatItem(data.getFrom(), data.getContent(), toDate(data.getSendTime()), ChatType.LEFT_IMAGE));
                binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            } else {
                adapter.addItem(new ChatItem(data.getFrom(), data.getContent(), toDate(data.getSendTime()), ChatType.LEFT_MESSAGE));
                binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }


    private void sendMessage() {
        mSocket.emit("newMessage", gson.toJson(new MessageData("MESSAGE",
                username,
                roomNumber,
                binding.contentEdit.getText().toString(),
                System.currentTimeMillis())));
        sendNoti(username, binding.contentEdit.getText().toString());
        Log.d("MESSAGE", new MessageData("MESSAGE",
                username,
                roomNumber,
                binding.contentEdit.getText().toString(),
                System.currentTimeMillis()).toString());
        adapter.addItem(new ChatItem(username, binding.contentEdit.getText().toString(), toDate(System.currentTimeMillis()), ChatType.RIGHT_MESSAGE));
        binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        binding.contentEdit.setText("");


    }

    // 이미지 uri로부터 실제 파일 경로를 알아냄
    private String getRealPathFromURI(Uri contentUri, Context context) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();

        return result;
    }

    // Node.js 서버에 이미지를 업로드
    public void uploadImage(Uri imageUri, Context context) {
        File image = new File(getRealPathFromURI(imageUri, context));
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), image);

        MultipartBody.Part body = MultipartBody.Part.createFormData("image", image.getName(), requestBody);

        retrofitClient.getApiService().uploadImage(body).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                if (result.getResult() == 1) {
                    Log.d("PHOTO", "Upload success : " + result.getImageUri());
                    sendImage(result.getImageUri());
                } else {
                    Log.d("PHOTO", "Upload failed");
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("PHOTO", "Upload failed222 : " + t.getMessage());
            }
        });
    }

    private void sendImage(String imageUri) {
        mSocket.emit("newImage", gson.toJson(new MessageData("IMAGE",
                username,
                roomNumber,
                imageUri,
                System.currentTimeMillis())));
        Log.d("IMAGE", new MessageData("IMAGE",
                username,
                roomNumber,
                imageUri,
                System.currentTimeMillis()).toString());

    }

    // System.currentTimeMillis를 몇시:몇분 am/pm 형태의 문자열로 반환
    private String toDate(long currentMiliis) {
        return new SimpleDateFormat("hh:mm a").format(new Date(currentMiliis));
    }

    // 이미지를 갤러리에서 선택했을 때 이벤트
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Uri selectedImageUri;
//
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            System.out.println("사진 버튼");
//
//            selectedImageUri = data.getData();
//            uploadImage(selectedImageUri, getApplicationContext());
//            adapter.addItem(new ChatItem(username, String.valueOf(selectedImageUri), toDate(System.currentTimeMillis()), ChatType.RIGHT_IMAGE));
//            binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
//        }
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case PICK_FROM_ALBUM: {
                Uri selectedImageUri;
                selectedImageUri = data.getData();
                System.out.println("앨범 이미지 uri" + selectedImageUri);
                uploadImage(selectedImageUri, getApplicationContext());
                adapter.addItem(new ChatItem(username, String.valueOf(selectedImageUri), toDate(System.currentTimeMillis()), ChatType.RIGHT_IMAGE));
                binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                break;
            }
            case PICK_FROM_CAMERA: {
                System.out.println("진입");
                Uri selectedImageUri;
                selectedImageUri = Uri.parse(editProfilePhoto.getApplicationPath());
                System.out.println("카메라 이미지 uri" + selectedImageUri);
                uploadImage(selectedImageUri, getApplicationContext());
                adapter.addItem(new ChatItem(username, String.valueOf(selectedImageUri), toDate(System.currentTimeMillis()), ChatType.RIGHT_IMAGE));
                binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                break;
            }
        }
    }

    // 이전 버튼을 누를 시 방을 나가고 소켓 통신 종료
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.emit("left", gson.toJson(new RoomData(username, roomNumber, targets)));
        mSocket.disconnect();
    }

    public void loadChat() {
        String baseURL = "http://218.39.138.57/and2/";
        String apiURL = "ChatData.php";


        RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
        StringRequest request = new StringRequest(
                Request.Method.GET,
                baseURL + apiURL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray list = jsonObject.getJSONArray("room");
                            for (int i = 0; i < list.length(); i++) {
                                Long time = Long.valueOf(list.getJSONObject(i).getString("date"));
                                if (list.getJSONObject(i).getString("user_name").equals(username) && list.getJSONObject(i).getString("user_profile").equals("null") && list.getJSONObject(i).getString("user_group").equals(roomNumber)) {
                                    adapter.addItem(new ChatItem(username, list.getJSONObject(i).getString("user_content"), toDate(time), ChatType.RIGHT_MESSAGE));
                                    binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                                } else if (list.getJSONObject(i).getString("user_name") != username && list.getJSONObject(i).getString("user_profile").equals("null") && list.getJSONObject(i).getString("user_group").equals(roomNumber)) {
                                    adapter.addItem(new ChatItem(list.getJSONObject(i).getString("user_name"), list.getJSONObject(i).getString("user_content"), toDate(time), ChatType.LEFT_MESSAGE));
                                    binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                                } else if (list.getJSONObject(i).getString("user_name").equals(username) && list.getJSONObject(i).getString("user_content").equals("null") && list.getJSONObject(i).getString("user_group").equals(roomNumber)) {
                                    adapter.addItem(new ChatItem(username, list.getJSONObject(i).getString("user_profile"), toDate(time), ChatType.RIGHT_IMAGE));
                                    binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                                } else if (list.getJSONObject(i).getString("user_name") != (username) && list.getJSONObject(i).getString("user_content").equals("null") && list.getJSONObject(i).getString("user_group").equals(roomNumber)) {
                                    adapter.addItem(new ChatItem(list.getJSONObject(i).getString("user_name"), list.getJSONObject(i).getString("user_profile"), toDate(time), ChatType.LEFT_IMAGE));
                                    binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                });
        request.setShouldCache(false);
        queue.add(request);
    }

    public void sendNoti(String username, String chatText) {
        String serverUrl2 = "http://218.39.138.57/and2/loadToken.php";
        SimpleMultiPartRequest smpr = new SimpleMultiPartRequest(Request.Method.POST, serverUrl2, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                System.out.println("토큰 리스폰스 확인" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray list = jsonObject.getJSONArray("token");
                    String token = list.getJSONObject(0).getString("token");

                    String serverUrl2 = "http://218.39.138.57/and2/sendNoti.php";
                    SimpleMultiPartRequest smpr = new SimpleMultiPartRequest(Request.Method.POST, serverUrl2, new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
                    //요청 객체에 보낼 데이터를 추가
                    smpr.addStringParam("to", token);
                    smpr.addStringParam("username", username);
                    smpr.addStringParam("chatText", chatText);
                    smpr.addStringParam("roomNumber", roomNumber);

                    //이미지 파일 추가
                    //요청객체를 서버로 보낼 우체통 같은 객체 생성
                    RequestQueue requestQueue = Volley.newRequestQueue(context);
                    requestQueue.add(smpr);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //요청 객체에 보낼 데이터를 추가
        smpr.addStringParam("idx", String.valueOf(targetsid));
        //이미지 파일 추가
        //요청객체를 서버로 보낼 우체통 같은 객체 생성
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(smpr);
    }

    public void loadCurrent() {
        String baseURL = "http://218.39.138.57/and2/";
        String apiURL = "loadUser.php";
        String idx = SaveSharedPreference.getUserid(context);

        RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
        StringRequest request = new StringRequest(
                Request.Method.GET,
                baseURL + apiURL + "?idx=" + idx,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        CurrentUserManager.setCurrentUser(JSONManager.jsonObjectToUser(response));
                        init();
                        loadChat();

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                });
        request.setShouldCache(false);
        queue.add(request);
    }

    public void doTakePhotoAction() // 카메라 촬영 후 이미지 가져오기

    {
        Intent intent = new Intent(this, CameraManager.class);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    public void doTakeAlbumAction() // 앨범에서 이미지 가져오기
    {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }


    public void imageShow() {
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("카메라");
        ListItems.add("앨범 선택");
        final CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

        final List SelectedItems = new ArrayList();
        int defaultItem = 0;
        SelectedItems.add(defaultItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("다음중 하나를 선택해주세요");
        builder.setSingleChoiceItems(items, defaultItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SelectedItems.clear();
                        SelectedItems.add(which);
                    }
                });
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String msg = "";

                        if (!SelectedItems.isEmpty()) {
                            int index = (int) SelectedItems.get(0);
                            msg = ListItems.get(index);
                        }

                        if (msg.equals("카메라")) {
                            doTakePhotoAction();
                        }
                        if (msg.equals("앨범 선택")) {
                            doTakeAlbumAction();
                        }
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    private void storeCropImage(Bitmap bitmap, String filePath) {
        // SmartWheel 폴더를 생성하여 이미지를 저장하는 방식이다.
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartWheel";
        File directory_SmartWheel = new File(dirPath);
        if (!directory_SmartWheel.exists()) // SmartWheel 디렉터리에 폴더가 없다면 (새로 이미지를 저장할 경우에 속한다.)
            directory_SmartWheel.mkdir();
        File copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try {
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            // sendBroadcast를 통해 Crop된 사진을 앨범에 보이도록 갱신한다.
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(copyFile)));
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void permissionDenied() {

    }

    @Override
    public void createPhotoSuccess(Photo photo) {
        this.editProfilePhoto = photo;

        System.out.println("진입");
        Uri selectedImageUri;
        selectedImageUri = Uri.parse(editProfilePhoto.getApplicationPath());
        System.out.println("카메라 이미지 uri" + selectedImageUri);



        File image = new File(editProfilePhoto.getApplicationPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), image);

        MultipartBody.Part body = MultipartBody.Part.createFormData("image", image.getName(), requestBody);

        retrofitClient.getApiService().uploadImage(body).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                if (result.getResult() == 1) {
                    Log.d("PHOTO", "Upload success : " + result.getImageUri());
                    sendImage(result.getImageUri());
                } else {
                    Log.d("PHOTO", "Upload failed");
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("PHOTO", "Upload failed222 : " + t.getMessage());
            }
        });




        adapter.addItem(new ChatItem(username, String.valueOf(selectedImageUri), toDate(System.currentTimeMillis()), ChatType.RIGHT_IMAGE));
        binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);

    }

    @Override
    public void createPhotoFail() {

    }
}
