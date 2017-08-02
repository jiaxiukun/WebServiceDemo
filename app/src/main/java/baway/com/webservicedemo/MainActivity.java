package baway.com.webservicedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * webservice 基本都是类似于这个类一样用  只需要改些别的东西就可以了
 */

public class MainActivity extends AppCompatActivity {
    private EditText phoneSecEditText;
    private TextView resultView;
    private Button queryButton;
    private SoapObject object;
    private SoapPrimitive city;
    private SoapPrimitive corp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneSecEditText = (EditText) findViewById(R.id.phone_sec);
        resultView = (TextView) findViewById(R.id.result_text);
        queryButton = (Button) findViewById(R.id.query_btn);

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 手机号码（段）
                String phoneSec = phoneSecEditText.getText().toString().trim();
                // 简单判断用户输入的手机号码（段）是否合法
                if (TextUtils.isEmpty(phoneSec)) {
                    // 给出错误提示
                    phoneSecEditText.setError("您输入的手机号码（段）有误！");
                    phoneSecEditText.requestFocus();
                    // 将显示查询结果的TextView清空
                    resultView.setText("");
                    return;
                }
                // 查询手机号码（段）信息
                getRemoteInfo(phoneSec);
            }


        });
    }

    public void getRemoteInfo(final String phoneSec) {
        // 命名空间
        final String nameSpace = "http://www.36wu.com/";
        // 调用的方法名称
        final String methodName = "GetMobileOwnership";
        // EndPoint   当时的网址只能保存一天  所以空指针异常
        final String endPoint = "http://web.36wu.com/MobileService.asmx?WSDL";
        // SOAP Action
        final String soapAction = nameSpace + methodName;
        new Thread(new Runnable() {


            private SoapObject object;

            public void run() {
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);

                // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
                rpc.addProperty("mobile", phoneSec);
                rpc.addProperty("format", "json");
                rpc.addProperty("authkey", "e5d20ec3ee49450a9cdd43d35cef4f5c");
                // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
                SoapSerializationEnvelope envelope = new
                        SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
                envelope.bodyOut = rpc;
                // 设置是否调用的是dotNet开发的WebService
                envelope.dotNet = true;
                // 等价于envelope.bodyOut = rpc;
                envelope.setOutputSoapObject(rpc);

                HttpTransportSE transport = new HttpTransportSE(endPoint);
                try {
                    // 调用WebService
                    transport.call(soapAction, envelope);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 获取返回的数据
                object = (SoapObject) envelope.bodyIn;
                // 获取返回的结果
                System.out.println(object);
                SoapObject result = (SoapObject) object.getProperty("GetMobileOwnershipResult");
                SoapObject data= (SoapObject) result.getProperty("data");
                city = (SoapPrimitive) data.getPrimitiveProperty("city");
                corp = (SoapPrimitive) data.getPrimitiveProperty("corp");
                System.out.println(city+"---");
                // 将WebService返回的结果显示在TextView中
                // 此处一定要在主线程中,因为不能在子线程中更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultView.setText(city+"  "+corp);
                    }
                });

            }
        }).start();




    }
}