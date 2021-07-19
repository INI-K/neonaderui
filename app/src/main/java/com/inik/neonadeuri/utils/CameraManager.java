package com.inik.neonadeuri.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import com.inik.neonadeuri.R;
import com.inik.neonadeuri.models.Photo;
import com.inik.neonadeuri.models.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
 * CameraManager 액티비티
 * 카메라를 통해 Photo 객체를 직접 생성하고 싶을 때 사용한다
 *
 * 사용법
 * 1. Photo 객체를 생성하려는 클래스에서 PhotoReciver 인터페이스를 구현한다
 *      - permissionDenied() : 사용자가 권한 요청을 거부했을때 호출되는 메서드
 *      - createPhotoSuccess(Photo) : 성공적으로 Photo 객체를 생성했을때 호출되는 메서드, 매개변수는 생성된 Photo 객체이다
 *      - createPhotoFaile() : Photo 객체 생성에 실패했을때 호출되는 메서드
 *
 * 2. Photo 객체를 생성하려는 클래스에서 CameraManager.photoReceiver = this 를 통해 리시버로 자신을 설정한다
 *
 * 3. CameraManager 액티비티를 시작한다
 */
public class CameraManager extends AppCompatActivity {

    Context context;
    User currentUser;

    Photo photo;

    public static PhotoReceiver photoReceiver;

    // 사진 촬영 인텐트 응답을 위한 상수
    final static int REQUEST_TAKE_PHOTO = 19;

    // 퍼미션 요청 응답을 위한 상수
    final static int REQUEST_PERMISSION_CODE = 69;

    // 필요한 퍼미션
    private String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_manager);

        setVariable();
        setView();

        requestPermissions();
    }

    // 클래스 변수 설정 메서드
    public void setVariable() {
        context = getApplicationContext();
        currentUser = CurrentUserManager.getCurrentUser();
        photo = new Photo();
    }

    // 뷰 설정 메서드
    public void setView() {

    }

    // 권한 설정 메서드
    public void requestPermissions() {
        ArrayList<String> permissionList = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            if (checkSelfPermission(permissions[i]) == PackageManager.PERMISSION_DENIED)
                permissionList.add(permissions[i]);
        }

        // 이미 모든 퍼미션을 획득했다면
        if (permissionList.isEmpty()) {
            takePhoto();
        } else { // 획득하지 못한 퍼미션이 있다면
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), REQUEST_PERMISSION_CODE);
        }
    }

    // 파일 생성 메서드
    private File createImageFile() throws IOException {
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_" + currentUser.getIdx() + "_";
        File image = File.createTempFile(fileName,
                ".jpg",
                getExternalFilesDir(Environment.DIRECTORY_PICTURES));

        photo.setApplicationPath(image.getAbsolutePath());

        return image;
    }

    // 사진 촬영 메서드
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 인텐트를 수신할 앱이 있다면
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            // 임시 파일 생성
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 성공적으로 임시 파일을 생성 했다면
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.inik.neonadeuri", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 하드웨어 권한을 확보하지 못했다면 종료
        for (int i = 0; i < grantResults.length; i++)
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                finishAndRemoveTask();
                photoReceiver.permissionDenied();

                return;
            }

        takePhoto();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                // 성공적으로 반환값을 받았다면
                if (resultCode == RESULT_OK) {
                    File file = new File(photo.getApplicationPath());
                    Bitmap bitmap = null;

                    if (Build.VERSION.SDK_INT >= 29) {
                        try {
                            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), Uri.fromFile(file)));
                            bitmap = rotateImg(bitmap, 0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                            bitmap = rotateImg(bitmap, 90);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (bitmap != null) {
                        photo.setBitmapImg(bitmap);
                        photoReceiver.createPhotoSuccess(photo);
                    } else {
                        photoReceiver.createPhotoFail();
                    }
                }
                break;
            default:
                break;
        }

        finishAndRemoveTask();
    }

    public Bitmap rotateImg(Bitmap src, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        Bitmap rotateImageBitmap = Bitmap.createBitmap(src, 0 , 0 ,src.getWidth(), src.getHeight(), matrix, true);
        File file = new File(photo.getApplicationPath());

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            rotateImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rotateImageBitmap;
    }

    public interface PhotoReceiver {
        void permissionDenied();
        void createPhotoSuccess(Photo photo);
        void createPhotoFail();
    }
}
