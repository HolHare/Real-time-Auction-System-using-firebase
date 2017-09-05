package layout;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.osamakhalid.realtimeauctionsystem.Classes.Post;
import com.example.osamakhalid.realtimeauctionsystem.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class PostAds extends Fragment {
    private int current_year;
    private int current_month;
    private int current_date;
    private Button uploadButton;
    private Button postButton;
    private Button setTimeButton;
    private ImageView post_image;
    private EditText title;
    private EditText description;
    private EditText initialBid;
    private TextView year;
    private TextView month;
    private TextView date;
    private Spinner hours;
    private Spinner mins;
    private Spinner duration;
    private Spinner category;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private static final int RC_PHOTO_PICKER = 1;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Uri downloadUri = null;
    private String mYear;
    private String mMonth;
    private String mDate;
    private String mHours;
    private String mMins;
    private String mDuration;
    private String mCategory;
    private int DIALOG_ID = 0;
    private SimpleDateFormat mdformat;
    private ProgressDialog progressDialog;
    Date currentdate = null;
    Uri imageuri = null;
    public Date enteredStartDate = null;
    public Date enteredEndDate = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        if (container != null) {
//            container.removeAllViews();
//        }
        View view = inflater.inflate(R.layout.fragment_post_ads, container, false);
        mdformat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        uploadButton = (Button) view.findViewById(R.id.upload_photo_button);
        postButton = (Button) view.findViewById(R.id.post_button);
        setTimeButton = (Button) view.findViewById(R.id.set_time_button);
        post_image = (ImageView) view.findViewById(R.id.post_image);
        title = (EditText) view.findViewById(R.id.title);
        description = (EditText) view.findViewById(R.id.description);
        initialBid = (EditText) view.findViewById(R.id.initial_bid);
        year = (TextView) view.findViewById(R.id.start_time_year);
        month = (TextView) view.findViewById(R.id.start_time_month);
        date = (TextView) view.findViewById(R.id.start_time_date);
        hours = (Spinner) view.findViewById(R.id.start_time_hours);
        mins = (Spinner) view.findViewById(R.id.start_time_mins);
        duration = (Spinner) view.findViewById(R.id.duration);
        category = (Spinner) view.findViewById(R.id.categoryOfItem);
        ArrayAdapter<CharSequence> adapterhours = ArrayAdapter.createFromResource(getActivity(), R.array.hours, android.R.layout.simple_spinner_item);
        adapterhours.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adaptermins = ArrayAdapter.createFromResource(getActivity(), R.array.mins, android.R.layout.simple_spinner_item);
        adaptermins.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapterduration = ArrayAdapter.createFromResource(getActivity(), R.array.duration, android.R.layout.simple_spinner_item);
        adapterduration.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapterCategoryOfItem = ArrayAdapter.createFromResource(getActivity(), R.array.category_of_item, android.R.layout.simple_spinner_item);
        adapterCategoryOfItem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapterCategoryOfItem);
        hours.setAdapter(adapterhours);
        mins.setAdapter(adaptermins);
        duration.setAdapter(adapterduration);
        mHours = hours.getSelectedItem().toString();
        mMins = mins.getSelectedItem().toString();
        mCategory = category.getSelectedItem().toString();
        hours.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mHours = hours.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mins.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mMins = mins.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        duration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mDuration = duration.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCategory = category.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("post_photos");
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Posts");
        progressDialog = new ProgressDialog(getActivity());
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String enteredDateString = year.getText().toString() + "/" + month.getText().toString() + "/" + date.getText().toString()
                        + " " + mHours + ":" + mMins;
                try {
                    enteredStartDate = mdformat.parse(enteredDateString);
                    Calendar cal = Calendar.getInstance(); // creates calendar
                    cal.setTime(enteredStartDate); // sets calendar time/date
                    cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + Integer.parseInt(mDuration));
                    System.out.println("checkff" + mdformat.format(cal.getTime()));
                    enteredEndDate = cal.getTime();
                } catch (Exception c) {
                    c.printStackTrace();
                }
                if (TextUtils.isEmpty(title.getText().toString())) {
                    title.setError("Enter title");
                    return;
                } else if (TextUtils.isEmpty(description.getText().toString())) {
                    description.setError("Enter description");
                    return;
                } else if (TextUtils.isEmpty(initialBid.getText().toString())) {
                    initialBid.setError("Enter initial bid");
                    return;
                }
                if (enteredStartDate == null) {
                    Toast.makeText(getActivity(), "Please enter start bidding date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (imageuri == null) {
                    Toast.makeText(getActivity(), "Please upload image", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (currentdate.compareTo(enteredStartDate) == 1) {
                    Toast.makeText(getActivity(), "Entered date is less than current date!", Toast.LENGTH_SHORT).show();
                    year.setText("0000");
                    month.setText("00");
                    date.setText("00");
                    return;
                }
                progressDialog.setMessage("Posting...");
                progressDialog.show();
                StorageReference photoRef = storageReference.child(imageuri.getLastPathSegment());
                photoRef.putFile(imageuri).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadUri = taskSnapshot.getDownloadUrl();
                        String key = reference.push().getKey();
                        Post post = new Post(title.getText().toString(), description.getText().toString(), initialBid.getText().toString(), mCategory, mdformat.format(enteredStartDate), mdformat.format(enteredEndDate), downloadUri.toString(), key, FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reference.child(key).setValue(post);
                        progressDialog.dismiss();
                        title.setText("");
                        description.setText("");
                        initialBid.setText("");
                        year.setText("0000");
                        month.setText("00");
                        date.setText("00");
                        post_image.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.placeholder_image));
                        Toast.makeText(getActivity(), "Successfully posted", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                try {
                    currentdate = mdformat.parse(mdformat.format(c.getTime()));
                } catch (Exception c1) {
                    c1.printStackTrace();
                }
                current_date = c.get(Calendar.DAY_OF_MONTH);
                current_month = c.get(Calendar.MONTH);
                current_year = c.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        year.setText(String.valueOf(i));
                        month.setText(String.valueOf(i1 + 1));
                        date.setText(String.valueOf(i2));
                    }
                }, current_year, current_month, current_date);
                datePickerDialog.show();
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RC_PHOTO_PICKER) {
            imageuri = data.getData();
            Glide.with(getActivity()).load(imageuri).into(post_image);
        }
    }
}
