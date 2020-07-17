package nasugar.com.employeeattendanttrackingsystem.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import nasugar.com.employeeattendanttrackingsystem.R;

public class MainActivity extends AppCompatActivity {
    Button buttonCheckIn, buttonCheckOut;
    ImageView imageViewPhoto;
    int REQUEST_CODE_CAMERA;
    EditText editTextDate, editTextTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);

        buttonCheckIn = findViewById( R.id.btnCheckIn );
        buttonCheckOut = findViewById( R.id.btnCheckOut );
        imageViewPhoto = findViewById( R.id.imageViewPhoto );
        editTextDate = findViewById( R.id.editTextDate );
        editTextTime = findViewById( R.id.editTextTime );

        buttonCheckIn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions( MainActivity.this, new String[] {Manifest.permission.CAMERA},REQUEST_CODE_CAMERA );
            }
        } );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        if (requestCode == REQUEST_CODE_CAMERA && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Check hardware device have camera or not
            if (checkCameraHardware(MainActivity.this)) {
                Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
                startActivityForResult( intent, REQUEST_CODE_CAMERA );
            } else {
                Toast.makeText( this, "This device don't have camre", Toast.LENGTH_SHORT ).show();
            }

        }
    }
    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get( "data" );
            imageViewPhoto.setImageBitmap( bitmap );

            Calendar c = Calendar.getInstance();
//            SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
            SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
            String datetime = dateformat.format(c.getTime());
            editTextDate.setText( datetime );

        }
    }
}