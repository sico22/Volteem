package volteem.com.volteem.view;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import volteem.com.volteem.R;
import volteem.com.volteem.model.entity.VolteemCommonException;
import volteem.com.volteem.presenter.RegisterActivityPresenter;
import volteem.com.volteem.util.PermissionUtil;
import volteem.com.volteem.util.VolteemConstants;

public class RegisterActivity extends AppCompatActivity implements RegisterActivityPresenter.View {

    private static final int GALLERY_INTENT = 1;
    private EditText mEmail, mPassword, mPhone, mCity, mBirthDate, mFirstName, mLastname, mConfirmPass;
    private CircleImageView circleImageView;
    private long birthdate;
    private Spinner spinner;
    private List<String> gender = new ArrayList<>();
    private String mGender = "Gender";
    private RegisterActivityPresenter presenter;
    private Uri uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        presenter = new RegisterActivityPresenter(this);
        presenter.onCreate();
        spinner = findViewById(R.id.register_gender);
        mEmail = findViewById(R.id.register_email);
        mPassword = findViewById(R.id.register_password);
        mPhone = findViewById(R.id.register_phone);
        mCity = findViewById(R.id.register_city);
        mBirthDate = findViewById(R.id.register_birthdate);
        mFirstName = findViewById(R.id.register_first_name);
        mLastname = findViewById(R.id.register_last_name);
        mConfirmPass = findViewById(R.id.register_password_confirm);
        circleImageView = findViewById(R.id.register_add_photo);

        mBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar myCalendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        month++;
                        String birthDateString = dayOfMonth + "/" + month + "/" + year;
                        mBirthDate.setText(birthDateString);
                        month--;
                        myCalendar.set(year, month, dayOfMonth, 12, 15, 0);
                        birthdate = myCalendar.getTimeInMillis();

                    }
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        gender.add("Gender");
        gender.add("Male");
        gender.add("Female");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gender);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = spinner.getSelectedItem().toString();
                if (!TextUtils.equals(selected, "Gender")) {
                    mGender = selected;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });

        findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eMail = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                String confirmPassword = mConfirmPass.getText().toString();
                String firstName = mFirstName.getText().toString();
                String lastName = mLastname.getText().toString();
                String city = mCity.getText().toString();
                String phone = mPhone.getText().toString();
                presenter.registerUser(eMail, password, confirmPassword, firstName, lastName, birthdate, city, phone, mGender, uri);
            }
        });

        findViewById(R.id.button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityByClass(LoginActivity.class);
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (PermissionUtil.isStorageReadPermissionGranted(getApplicationContext())) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, VolteemConstants.GALLERY_INTENT);

                } else {
                    Snackbar.make(view, "Please allow storage permission", Snackbar.LENGTH_LONG).setAction("Set Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        }
                    }).show();
                }
            }
        });
    }

    private void startActivityByClass(Class activity) {
        Intent intent = new Intent(RegisterActivity.this, activity);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void onRegisterSuccessful() {
        Toast.makeText(this, VolteemConstants.MESSAGE_ACCOUNT_CREATED, Toast.LENGTH_LONG).show();
        startActivityByClass(MainActivity.class);
    }

    @Override
    public void onBackPressed() {
        startActivityByClass(LoginActivity.class);
    }

    @Override
    public void onRegisterFailed(@NonNull VolteemCommonException volteemCommonException) {
        String error = volteemCommonException.getMessage();
        switch (volteemCommonException.getCause()) {
            case VolteemConstants.EXCEPTION_EMAIL:
                mEmail.setError(error);
                mEmail.requestFocus();
                break;
            case VolteemConstants.EXCEPTION_PASSWORD:
                mPassword.setError(error);
                mPassword.requestFocus();
                break;
            case VolteemConstants.EXCEPTION_CONFIRM_PASSWORD:
                mConfirmPass.setError(error);
                mConfirmPass.requestFocus();
                break;
            case VolteemConstants.EXCEPTION_FIRST_NAME:
                mFirstName.setError(error);
                mFirstName.requestFocus();
                break;
            case VolteemConstants.EXCEPTION_LAST_NAME:
                mLastname.setError(error);
                mLastname.requestFocus();
                break;
            case VolteemConstants.EXCEPTION_CITY:
                mCity.setError(error);
                mCity.requestFocus();
                break;
            case VolteemConstants.EXCEPTION_PHONE:
                mPhone.setError(error);
                mPhone.requestFocus();
                break;
            case VolteemConstants.EXCEPTION_BIRTH_DATE:
                mBirthDate.setError(error);
                mBirthDate.requestFocus();
                break;
            case VolteemConstants.EXCEPTION_GENDER:
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && (data != null)) {
            uri = data.getData();
            Glide.with(this).load(uri).into(circleImageView);
        }
    }
}
