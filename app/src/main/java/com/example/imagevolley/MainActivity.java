package com.example.imagevolley;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView img;
    private EditText edt;
    private Button button_upload, button_choose;
    private final int IMG_REQUEST = 1;
    private Bitmap bitmap;
    private String urlimage = "http://shameed.000webhostapp.com/img_upload.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = findViewById(R.id.img_pic);
        edt = findViewById(R.id.edit_pic);
        button_choose = findViewById(R.id.but_choose);
        button_upload = findViewById(R.id.but_up);
        button_choose.setOnClickListener(this);
        button_upload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.but_choose:
                selectImage();
                break;
            case R.id.but_up:
                uploadImage();
                break;
        }

    }

    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                img.setImageBitmap(bitmap);
                img.setVisibility(View.VISIBLE);
                edt.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadImage() {
//        Log.wtf("sha","read");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlimage, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("sha", response);
                    JSONObject jsonObject = new JSONObject(response);
                    String Response = jsonObject.getString("response");
//                    Log.i("sha",Response);
                    Toast.makeText(MainActivity.this, Response, Toast.LENGTH_LONG).show();
                    img.setImageResource(0);
                    img.setVisibility(View.GONE);
                    edt.setText("");
                    edt.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("sha", error.toString());
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                error.printStackTrace();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", edt.getText().toString());
                params.put("image", imgtoString(bitmap));
                Log.i("sha", params.toString());
                return params;
            }
        };
        Mysingleton.getInstance(MainActivity.this).addRequest(stringRequest);

    }

    private String imgtoString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] img = byteArrayOutputStream.toByteArray();
        Log.i("sha", "returned");
        return Base64.encodeToString(img, Base64.DEFAULT);

    }
}
