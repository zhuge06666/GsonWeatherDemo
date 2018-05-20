package cn.edu.gdmec.android.gsonweatherdemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
  private EditText et;
  private Button btn;
  private Thread thread;
  private TextView tv;
  private WeatherBean bean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et=findViewById(R.id.et);
        btn=findViewById(R.id.btn);
        tv=findViewById(R.id.tv);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thread=new Thread(){
                    @Override
                    public void run() {
                        getHttpGson(et.getText().toString().trim());
                    }
                };
                thread.start();
            }
        });

    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
               String a=handleobj(msg.obj.toString());
               tv.setText(a);
            }
        }
    };
    private String handleobj(String json){
        Gson gson=new Gson();
        bean=gson.fromJson(json,WeatherBean.class);
        String a="温度："+bean.getData().getWendu()+"\n湿度："+bean.getData().getShidu()+
                "\n最高温度："+bean.getData().getForecast().get(0).getHigh()+
                "\n最低温度："+bean.getData().getForecast().get(0).getLow();
        return a;
    }
    private void getHttpGson(String city){
        try {
            URL url=new URL("https://www.sojson.com/open/api/weather/json.shtml?city="+city);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                InputStream is=conn.getInputStream();
                InputStreamReader isr=new InputStreamReader(is);
                BufferedReader br=new BufferedReader(isr);
                StringBuffer sbf=new StringBuffer();
                String line=null;
                while ((line=br.readLine())!=null){
                    sbf.append(line);
                }
                Message message=new Message();
                message.what=1;
                message.obj=sbf.toString();
              handler.sendMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
