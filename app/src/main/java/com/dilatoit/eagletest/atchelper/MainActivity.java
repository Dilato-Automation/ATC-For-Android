package com.dilatoit.eagletest.atchelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashhh on 11/2/16.
 */

public class MainActivity extends Activity implements View.OnClickListener {
    private String WIFI_SSID;
    private String WIFI_PASS;
    private Button connectAtc;
    private Spinner selectSetting;
    private Button start;
    private Button stop;
    private Button manualStart;
    private TextView wifi;
    private TextView wifisetting;
    private TextView state;
    private TextView type;
    private WebView webview;
    private AtcApiHelper atcApiHelper;
    private WifiHelper wifiHelper;
    private List<String> list = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectAtc = (Button) findViewById(R.id.connect_atc);
        selectSetting = (Spinner) findViewById(R.id.select_setting);
        start = (Button) findViewById(R.id.startatc);
        stop = (Button) findViewById(R.id.stopatc);
        manualStart = (Button) findViewById(R.id.manual_start);
        webview = (WebView) findViewById(R.id.webview);
        wifi = (TextView) findViewById(R.id.display_wifi);
        wifisetting = (TextView) findViewById(R.id.change_wifi);
        state = (TextView) findViewById(R.id.display_current_state);
        type = (TextView) findViewById(R.id.display_current_type);
        list.add("2G Normal");
        list.add("2G Good");
        list.add("3G Normal");
        list.add("3G Good");
        list.add("Cable");
        list.add("DSL");
        list.add("Edge Narmal");
        list.add("Edge Good");
        list.add("Edge Lossy");
        list.add("No Connectivity");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectSetting.setAdapter(adapter);
        wifisetting.setOnClickListener(this);
        connectAtc.setOnClickListener(this);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        manualStart.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("wifi",MODE_PRIVATE);
        WIFI_SSID = sharedPreferences.getString("SSID","");
        WIFI_PASS = sharedPreferences.getString("PASS","");
        wifiHelper = new WifiHelper(this,WIFI_SSID,WIFI_PASS,3);
        updateCurrentState();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect_atc:
                SharedPreferences sharedPreferences = getSharedPreferences("wifi",MODE_PRIVATE);
                WIFI_SSID = sharedPreferences.getString("SSID","");
                WIFI_PASS = sharedPreferences.getString("PASS","");
                wifiHelper = new WifiHelper(this,WIFI_SSID,WIFI_PASS,3);
                wifiHelper.connectNetwork();
                updateCurrentState();
                break;
            case R.id.startatc:
                String text = selectSetting.getSelectedItem().toString();
                AtcApiHelper.AtcType type = AtcApiHelper.GetType(text);
                atcApiHelper = new AtcApiHelper(this,type);
                atcApiHelper.Start();
                break;
            case R.id.stopatc:
                atcApiHelper = new AtcApiHelper(this);
                atcApiHelper.StopAny();
                break;
            case R.id.manual_start:
                webview.getSettings().setJavaScriptEnabled(true);
                webview.setWebViewClient(new WebViewClient());
                webview.loadUrl("http://192.168.0.1:8000");
                break;
            case R.id.change_wifi:
                showAddDialog();
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    public void updateCurrentState()
    {
        wifi.setText("");
        state.setText("");
        type.setText("");
        Boolean wifiState = wifiHelper.isOpenWifi();
        if (!wifiState)
        {
            wifi.setText("Wifi没有启用");
        }
        else
        {
            Boolean ssidWanted = wifiHelper.IsConnectWantedWifi();
            if (ssidWanted)
            {
                wifi.setText("网络已连接");
                AtcApiHelper atc = new AtcApiHelper(this);
                atc.SetCurrentState();
            }
            else
            {
                wifi.setText("没有连接到指定网络");
            }
        }
    }

    public void showAddDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.dialog_setting, null);
        final EditText ssid = (EditText) textEntryView.findViewById(R.id.ssid_edit);
        ssid.setText(WIFI_SSID);
        final EditText pass = (EditText)textEntryView.findViewById(R.id.pass_edit);
        pass.setText(WIFI_PASS);
        final CheckBox showpass = (CheckBox)textEntryView.findViewById(R.id.showpass);
        showpass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        AlertDialog.Builder ad1 = new AlertDialog.Builder(MainActivity.this);
        ad1.setTitle("设置网络");
        ad1.setIcon(android.R.drawable.ic_dialog_info);
        ad1.setView(textEntryView);
        ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                SharedPreferences.Editor editor = getSharedPreferences("wifi",MODE_PRIVATE).edit();
                editor.putString("SSID",ssid.getText().toString());
                editor.putString("PASS",pass.getText().toString());
                editor.commit();
            }
        });
        ad1.setNegativeButton("取消",null);
        ad1.show();
    }
}
