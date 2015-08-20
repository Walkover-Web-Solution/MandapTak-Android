package com.mandaptak.android.EditProfile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mandaptak.android.Adapter.LayoutAdapter;
import com.mandaptak.android.Utils.Common;
import com.mandaptak.android.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import org.lucasr.twowayview.TwoWayLayoutManager;
import org.lucasr.twowayview.widget.DividerItemDecoration;
import org.lucasr.twowayview.widget.TwoWayView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

public class FinalEditProfileFragment extends Fragment {
    public final static int REQUEST_CODE = 11;
    private static final int FILE_SELECT_CODE = 0;
    View rootView;
    TwoWayView imageList;
    TextView importPhotosButton, uploadBiodata;
    Context context;
    LayoutAdapter photoAdapter;
    Common mApp;
    ArrayList<String> selectedPhotos = new ArrayList<>();

    public FinalEditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_final_edit_profile, container, false);
        context = getActivity();
        mApp = (Common) context.getApplicationContext();
        imageList = (TwoWayView) rootView.findViewById(R.id.list);
        importPhotosButton = (TextView) rootView.findViewById(R.id.import_photos);
        uploadBiodata = (TextView) rootView.findViewById(R.id.upload_biodata);
        photoAdapter = new LayoutAdapter(getActivity(), selectedPhotos);
        imageList.setOrientation(TwoWayLayoutManager.Orientation.HORIZONTAL);
        imageList.setHasFixedSize(true);
        imageList.setLongClickable(true);
        imageList.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        imageList.setAdapter(photoAdapter);

        importPhotosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View importImageDialog = View.inflate(context, R.layout.image_picker_dialog, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setView(importImageDialog);
                RelativeLayout takeCameraButton = (RelativeLayout) importImageDialog.findViewById(R.id.take_camera_button);
                RelativeLayout importGalleryButton = (RelativeLayout) importImageDialog.findViewById(R.id.gallery_import_button);
                RelativeLayout importFbButton = (RelativeLayout) importImageDialog.findViewById(R.id.fb_import_button);
                takeCameraButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PhotoPickerIntent intent = new PhotoPickerIntent(context);
                        intent.setPhotoCount(10 - selectedPhotos.size());
                        intent.setShowCamera(true);
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                });
                importGalleryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PhotoPickerIntent intent = new PhotoPickerIntent(context);
                        intent.setPhotoCount(10 - selectedPhotos.size());
                        intent.setShowCamera(false);
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                });
                importFbButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                alertDialog.show();
            }
        });
        uploadBiodata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
        return rootView;
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case FILE_SELECT_CODE:
                    Uri uri = data.getData();
                    String path = mApp.getPath(context, uri);
                    if (path != null) {
                        final File file = new File(path);
                        mApp.show_PDialog(context, "Uploading..");
                        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Profile");
                        parseQuery.getInBackground(ParseUser.getCurrentUser().getParseObject("profileId").getObjectId(), new GetCallback<ParseObject>() {
                            @Override
                            public void done(final ParseObject parseObject, ParseException e) {
                                try {
                                    final ParseFile parseFile = new ParseFile(file.getName(), read(file));
                                    parseFile.saveInBackground(new SaveCallback() {
                                        public void done(ParseException e) {
                                            parseObject.put("bioData", parseFile);
                                            parseObject.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        uploadBiodata.setText(file.getName());
                                                        mApp.dialog.dismiss();
                                                        Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        e.printStackTrace();
                                                        mApp.dialog.dismiss();
                                                        Toast.makeText(context, "Error while uploading file", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }, new ProgressCallback() {
                                        public void done(Integer percentDone) {
                                            // Update your progress spinner here. percentDone will be between 0 and 100.
                                            if (percentDone == 100) {
                                                mApp.updateDialogProgress(percentDone, "Finishing..");
                                            } else {
                                                mApp.updateDialogProgress(percentDone, "Uploading: " + percentDone + "%");
                                            }
                                        }
                                    });
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                    mApp.dialog.dismiss();
                                    Toast.makeText(context, "Error while uploading file", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(context, "Error: File not found", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case REQUEST_CODE:
                    if (data != null) {
                        selectedPhotos.clear();
                        List<String> photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                        if (photos != null) {
                            selectedPhotos.addAll(photos);
                            photoAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
            }
        }
    }

    public byte[] read(File file) throws IOException {
        ByteArrayOutputStream ous = null;
        InputStream ios = null;
        try {
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(file);
            int read;
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
        } finally {
            try {
                if (ous != null)
                    ous.close();
            } catch (IOException ignored) {
            }

            try {
                if (ios != null)
                    ios.close();
            } catch (IOException ignored) {
            }
        }
        return ous.toByteArray();
    }
}
