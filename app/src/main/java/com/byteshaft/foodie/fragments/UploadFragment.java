package com.byteshaft.foodie.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.byteshaft.foodie.R;
import com.byteshaft.foodie.activities.MainActivity;
import com.byteshaft.foodie.utils.AppGlobals;
import com.byteshaft.foodie.utils.Helpers;
import com.byteshaft.foodie.utils.MultiPartUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UploadFragment extends Fragment implements View.OnClickListener {

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    private static final int PICK_IMAGE_MULTIPLE = 1;
    private String imageEncoded;
    private List<String> imagesEncodedList;
    private View mBaseView;
    private ProgressDialog mProgressDialog;
    private ImageView imageView;
    private Button selectImage;
    private Button upload;
    private ArrayList<String> mArrayUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_upload, container, false);
        selectImage = (Button) mBaseView.findViewById(R.id.select_image);
        imageView = (ImageView) mBaseView.findViewById(R.id.image_view);
        upload = (Button) mBaseView.findViewById(R.id.upload);
        upload.setOnClickListener(this);
        selectImage.setOnClickListener(this);
        return mBaseView;
    }

    private void showPictures(ArrayList<String> imagesUrls) {
        int value = 0;
        for (String url : imagesUrls) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setPadding(2, 2, 2, 2);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            value++;
            imageView.setTag(value);
//            Picasso.with(SelectedAdDetail.this)
//                    .load(url)
//                    .resize(150, 150)
//                    .into(imageView);
//            layout.addView(imageView);
//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    System.out.println(v.getTag());
//                    Intent intent = new Intent(getApplicationContext(), productImageView.getClass());
//                    intent.putExtra("url", imagesUrls.get((Integer) v.getTag() - 1));
//                    startActivity(intent);
        }
//            });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_image:
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                } else {
                    openPictures();
                }
                break;
            case R.id.upload:
                if (mArrayUri == null) {
                    Toast.makeText(getActivity(), "please select image", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (!mArrayUri.isEmpty()) {
                        new UploadTask().execute();
                    }
                }
                break;

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("TAG", "Permission granted");
                    openPictures();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Permission denied!"
                            , Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void openPictures() {
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent();
            intent.setType("image/jpeg");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "pictures"), PICK_IMAGE_MULTIPLE);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == MainActivity.RESULT_OK
                    && null != data) {
                mArrayUri = new ArrayList<>();
                Log.i("TAG", "if part");
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<>();
                if (data.getData() != null) {

                    Uri mImageUri = data.getData();
                    mArrayUri.add(getImagePath(mImageUri));
                    Bitmap bitmap = BitmapFactory.decodeFile(getImagePath(mImageUri));
                    imageView.setImageBitmap(bitmap);
                    imageView.setBackground(getResources().getDrawable(R.drawable.border_image));
                    // Get the cursor
                    Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    cursor.close();

                } else {
                    if (data.getClipData() != null) {
                        Log.i("TAG", "else part");
                        ClipData mClipData = data.getClipData();
                        mArrayUri = new ArrayList<>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
//                            final int takeFlags = data.getFlags()
//                                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
//                                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            // Check for the freshest data.
//                            getActivity().getApplicationContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            System.out.println(getRealPathFromURI(getActivity().getApplicationContext(), uri));
                            mArrayUri.add(getImagePath(uri));
                            // Get the cursor
                            Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();
//                            System.out.println(uri);

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri);
                    }
                }
            } else {
                Toast.makeText(getActivity(), "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @NonNull
    private File getFile(Bitmap imageBitmap) {

        Uri tempUri = getImageUri(getActivity().getApplicationContext(), imageBitmap);
        // CALL THIS METHOD TO GET THE ACTUAL PATH
        return new File(getRealPathFromURI(tempUri));
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public String getImagePath(Uri uri) {
        Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getActivity().getApplicationContext().getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    class UploadTask extends AsyncTask<String, String, JSONObject> {

        private boolean internetAvailable = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("uploading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                internetAvailable = true;
                try {
                    MultiPartUtility multiPartUtility =
                            new MultiPartUtility(new URL(AppGlobals.SEND_IMAGES_URL), "POST");
                    multiPartUtility.addFormField("userid",
                            Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USER_ID));
                    multiPartUtility.addFormField("comment", "test");
                    multiPartUtility.addFilePart("file", new File(mArrayUri.get(0)));
                    return new JSONObject(multiPartUtility.finish());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);
            mProgressDialog.dismiss();
            try {
                if (!internetAvailable || s == null || s.getInt("result") == 0) {
                    imageView.setImageResource(android.R.drawable.ic_menu_gallery);
                    Toast.makeText(getActivity(), "image has been uploaded", Toast.LENGTH_SHORT).show();
                } else
                    if (s.getInt("result") == 0) {
                        imageView.setImageResource(android.R.drawable.ic_menu_gallery);
                        Toast.makeText(getActivity(), "image has been uploaded", Toast.LENGTH_SHORT).show();
                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("Response", "" + s);
        }
    }
}
