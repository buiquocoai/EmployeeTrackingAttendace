package nasugar.com.employeeattendanttrackingsystem.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import nasugar.com.employeeattendanttrackingsystem.R;
import nasugar.com.employeeattendanttrackingsystem.ServerConnection.HASH;
import nasugar.com.employeeattendanttrackingsystem.ServerConnection.SERVER;

public class LoginActivity extends AppCompatActivity {
    EditText edtUserName, edtPaddword;
    Button btnLogin;
    ProgressBar pgbLoading;
    TextView tvResult;
    ResultSet resultSet;
    CheckBox chkRememberMe;
    SharedPreferences  sharedPreferences;
    final int CHAN_PW_CODE=4; //Code dùng để gửi gói Intent qua Activity_User
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_login);
        AnhXa();

        //Khai bao Luu tru thong tin dang nhap
        sharedPreferences = getSharedPreferences( "dataLogin", MODE_PRIVATE );
        edtUserName.setText( sharedPreferences.getString( "usernam", "" ) );
        edtPaddword.setText( sharedPreferences.getString( "password", "" ) );
        chkRememberMe.setChecked( sharedPreferences.getBoolean( "check",false ) );

        //bat su kien login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoLogin  doLogin = new DoLogin();
                doLogin.execute("");//Thực thi doLogin.doInBackground();
            }
        });
    }

    private void AnhXa() {
        btnLogin=(Button)this.findViewById(R.id.btnLogin);
        edtUserName=(EditText)this.findViewById(R.id.edtUserName);
        edtPaddword=(EditText)this.findViewById(R.id.edtPassword);
        pgbLoading=(ProgressBar)this.findViewById(R.id.pgbLoading);
        tvResult=(TextView)this.findViewById(R.id.tvResult);
        pgbLoading.setVisibility(View.GONE);
        chkRememberMe = findViewById( R.id.chkRememberMe );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//Hàm này sẽ xử lý nếu khi mình gửi Intent là startActivityForResult, nhấn F8, bỏ qua cái này, xem xuống dưới trước
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHAN_PW_CODE) { //Kiểm tra xem code trả về có đúng ban đầu không này không
            // Phài chắc chắn là thành công mới gửi
            if (resultCode == RESULT_OK) {
                String rs = data.getStringExtra("PASSWORD");
                Toast.makeText(this, "Mật khẩu đã đổi thành " + rs, Toast.LENGTH_LONG).show();
                edtPaddword.setText(rs);
                btnLogin.setVisibility(View.VISIBLE);
            }
        }
    }

    //---------------------------------------------------------------------------------------------
    public class DoLogin extends AsyncTask<String,String,String>
    {

        String z = ""; //cái này dùng để hứng kết quả khi chạy hàm truy vấn tới SQL Server
        String hoten=""; //Cái này sẽ chứa họ tên đầy đủ khi truy vấn được
        Boolean isSuccess = false; //Biến nhận biết là có truy vấn thành công hay không

        String userid = edtUserName.getText().toString(); // biến cục bộ, xài cục bộ
        String password = edtPaddword.getText().toString(); // như trên


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = SERVER.Connect(); //khởi tạo kết nối tới server, SERVER chính là class riêng, tìm trong table java
                if (con == null) {
                    z = "Không thể kết nối với Server"; //Tiếng Việt :D
                } else {

                String query = "select * from SystemUser where UserName='" + userid + "' and Password='" + HASH.md5(password) + "'";
//                    String query = "select * from Users";
                    //trên đây là câu truy vấn
                    Statement stmt = con.createStatement(); //blah blah blah
                    resultSet = stmt.executeQuery(query); //thực thi và trả về một cục ResultSet, nó là gì thì Google, tui chịu

                    //ResultSet rs = SERVER.executeQuery(query);
                    if(resultSet.next())//nếu trong resultset không null thì sẽ trả về True
                    {
                        hoten=resultSet.getString("UserDescription");//Hàm lấy giá trị của tên cột (trường: field name) truyền vào
                        z = "Hi, " + hoten; //Hey, i'm TONA
                        //tvResult.setText(hoten);
                        isSuccess=true; //Oánh dấu chủ quyền, làm dấu thôi, để biết là hàm nó chạy tới đây, xíu mình dùng biến này kiểm tra coi thử chạy tới đây hay không đó mà, chạy tới đây nghĩa là thành công rồi đó.
                        con.close();//Đấm vỡ mồm SERVER xong thì phải băng bó cho nó.
                    }
                    else
                    {
                        z = "Tài khoản không tồn tại !";//Lại Tiếng Việt
                        isSuccess = false; //Chạy tới đây là hỏng rồi
                    }
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                z = "Lỗi !";
            }
            return z;//Trả về, thứ này chính là cái doInBackground(String... params), nó buộc mình phải trả về String, do tui khai báo là String thôi, nếu ban đầu khai kiểu khác thì nó sẽ bắt trả về kiểu khác, trong bài trước mình trả về null, vì mình chẳng cần bắt gì cả, chỉ chạy thôi.

        }

        @Override
        protected void onProgressUpdate(String... values) {
            //super.onProgressUpdate(values); //Thường dùng để thay đổi trạng thái tiến trình đang làm tới % blah blah, tui k xài
        }

        @Override
        protected void onPostExecute(String r) {// sau khi tiến trình kết thúc thì sẽ gọi tới hàm này
            pgbLoading.setVisibility(View.GONE);//Tắt cái cục xoay xoay đi
            Toast.makeText(LoginActivity.this,r,Toast.LENGTH_SHORT).show(); // cái r chính là cái mà nó lấy từ cái hàm doInBackground(String... params), hàm này return z (String), nó sẽ quăng qua hàm này để thực hiện cái bên trong
            if(isSuccess) {//kiểm tra chủ quyền của mình có tới vị trí đánh dấu nãy không :D
                Toast.makeText(LoginActivity.this,r,Toast.LENGTH_SHORT).show();

                Intent intent= new Intent(LoginActivity.this, MainActivity.class);//tạo ra một "gói" Intent gửi từ this đến UserActivity.class
                //setContentView(android.view.View);
                intent.putExtra("USERNAME",userid);//nhét cái userid vô intent và đặt khóa là USERNAME
                intent.putExtra("PASSWORD",password);//như trên
                intent.putExtra("HOTEN",hoten);//như rứa
                startActivityForResult(intent,CHAN_PW_CODE);//gửi đi, có đợi trả về, nếu trả về sẽ chạy cái hồi nãy nhấn F8
                //startActivity(intent); kiểu này sẽ không đợi trả về
                //intent

                //Ghi nho thong tin dang nhap
                CheckRememberMe( userid,password );

            }
        }
    }
private void CheckRememberMe(String uID, String pW){

    SharedPreferences.Editor editor = sharedPreferences.edit();
    if (chkRememberMe.isChecked()) {
        //Luu thong tin dang nhap khi user click vao nut Remember me
        editor.putString( "usernam", uID );
        editor.putString( "password", pW );
        editor.putBoolean( "check", true );
        editor.commit();
    } else {
        //Xoa thong tin dang nhap khi user khong click vao nut Remember me
        editor.remove( "usernam" );
        editor.remove( "password" );
        editor.remove( "check" );
        editor.commit();
    }
}

}