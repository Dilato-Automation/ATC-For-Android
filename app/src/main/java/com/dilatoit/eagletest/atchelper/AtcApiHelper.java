package com.dilatoit.eagletest.atchelper;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Ashhh on 11/2/16.
 */

public class AtcApiHelper{
    private Context _context;
    private AtcType _type;

    public AtcApiHelper(Context context)
    {
        _context = context;
    }

    public AtcApiHelper(Context context,AtcType type)
    {
        _context = context;
        _type = type;
    }

    public void Start()
    {
        new Thread() {
            public void run() {
                HttpURLConnection connect = null;
                int respons = 0;
                try{
                    URL url = new URL("http://192.168.0.1:8000/api/v1/shape/");
                    connect = (HttpURLConnection) url.openConnection();
                    connect.setDoOutput(true);
                    connect.setDoInput(true);
                    connect.setRequestProperty( "Content-Type", "application/json" );
                    connect.setRequestProperty("Accept", "application/json");
                    connect.setRequestMethod("POST");
                    connect.setConnectTimeout(3000);
                    connect.setReadTimeout(3000);
                    String temp = getPayload(_type);
                    DataOutputStream out = new DataOutputStream(connect.getOutputStream());
                    out.write(temp.getBytes());
                    out.flush ();
                    out.close ();
                    respons = connect.getResponseCode();
                }catch(Exception ex){
                    ex.printStackTrace();
                }finally {
                    if(connect != null){
                        connect.disconnect();
                    }
                    if (respons == 201){
                        ((Activity)(_context)).runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                Toast.makeText(_context,"Shaped Successfully",Toast.LENGTH_SHORT).show();
                                TextView state = (TextView)((Activity)(_context)).findViewById(R.id.display_current_state);
                                TextView type = (TextView)((Activity)(_context)).findViewById(R.id.display_current_type);
                                state.setText("启用");
                                type.setText(GetTypeName(_type));
                            }
                        });
                    }
                    else{
                        ((Activity)(_context)).runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                Toast.makeText(_context,"Shaped Failed",Toast.LENGTH_SHORT).show();
                                TextView state = (TextView)((Activity)(_context)).findViewById(R.id.display_current_state);
                                TextView type = (TextView)((Activity)(_context)).findViewById(R.id.display_current_type);
                                state.setText("未启用");
                                type.setText("");
                            }
                        });
                    }
                }
            }
        }.start();
    }

    public void StopAny()
    {
        new Thread() {
            public void run() {
                HttpURLConnection connect = null;
                int respons = 0;
                try{
                    URL url = new URL("http://192.168.0.1:8000/api/v1/shape/");
                    connect = (HttpURLConnection) url.openConnection();
                    connect.setRequestMethod("DELETE");
                    connect.setConnectTimeout(3000);
                    connect.setReadTimeout(3000);
                    respons = connect.getResponseCode();
                }catch(Exception ex){
                    ex.printStackTrace();
                }finally {
                    if(connect != null){
                        connect.disconnect();
                    }
                    if (respons == 204){
                        ((Activity)(_context)).runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                Toast.makeText(_context,"Unshaped Successfully",Toast.LENGTH_SHORT).show();
                                TextView state = (TextView)((Activity)(_context)).findViewById(R.id.display_current_state);
                                TextView type = (TextView)((Activity)(_context)).findViewById(R.id.display_current_type);
                                state.setText("未启用");
                                type.setText("");
                            }
                        });
                    }
                    else if (respons == 400){
                        ((Activity)(_context)).runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                Toast.makeText(_context,"No need to Unshaped",Toast.LENGTH_SHORT).show();
                                TextView state = (TextView)((Activity)(_context)).findViewById(R.id.display_current_state);
                                TextView type = (TextView)((Activity)(_context)).findViewById(R.id.display_current_type);
                                state.setText("未启用");
                                type.setText("");
                            }
                        });
                    }
                    else {
                        ((Activity)(_context)).runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                Toast.makeText(_context,"Unshaped Failed",Toast.LENGTH_SHORT).show();
                                TextView state = (TextView)((Activity)(_context)).findViewById(R.id.display_current_state);
                                TextView type = (TextView)((Activity)(_context)).findViewById(R.id.display_current_type);
                                state.setText("未启用");
                                type.setText("");
                            }
                        });
                    }
                }
            }
        }.start();
    }

    public void SetCurrentState()
    {
        new Thread() {
            public void run() {
                HttpURLConnection connect = null;
                int respons = 0;
                String res = "";
                try{
                    URL url = new URL("http://192.168.0.1:8000/api/v1/shape/");
                    connect = (HttpURLConnection) url.openConnection();
                    connect.setRequestMethod("GET");
                    connect.setConnectTimeout(3000);
                    connect.setReadTimeout(3000);
                    respons = connect.getResponseCode();
                    InputStream in = connect.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while((line = reader.readLine())!= null){
                        sb.append(line);
                    }
                    res = GetTypeName(sb.toString());
                }catch(Exception ex){
                    ex.printStackTrace();
                }finally {
                    if(connect != null){
                        connect.disconnect();
                    }
                    if (respons == 200)
                    {
                        final String finalName = res;
                        ((Activity)(_context)).runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                TextView state = (TextView)((Activity)(_context)).findViewById(R.id.display_current_state);
                                state.setText("启用");
                                TextView dis_type = (TextView)((Activity)(_context)).findViewById(R.id.display_current_type);
                                dis_type.setText(finalName);
                            }
                        });
                    }
                    else
                    {
                        ((Activity)(_context)).runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                TextView state = (TextView)((Activity)(_context)).findViewById(R.id.display_current_state);
                                state.setText("未启用");
                            }
                        });
                    }
                }
            }
        }.start();
    }

    public enum AtcType
    {
        TwoGDevelopingRural,
        TwoGDevelopingUrban,
        ThreeGAverage,
        ThreeGGood,
        Cable,
        DSL,
        EdgeAverage,
        EdgeGood,
        EdgeLossy,
        NoConnectivity,
    }

    public static AtcType GetType(String type)
    {
        switch(type)
        {
            case "2G Normal":
                return AtcType.TwoGDevelopingRural;
            case "2G Good":
                return AtcType.TwoGDevelopingUrban;
            case "3G Normal":
                return AtcType.ThreeGAverage;
            case "3G Good":
                return AtcType.ThreeGGood;
            case "Cable":
                return AtcType.Cable;
            case "DSL":
                return AtcType.DSL;
            case "Edge Narmal":
                return AtcType.EdgeAverage;
            case "Edge Good":
                return AtcType.EdgeGood;
            case "Edge Lossy":
                return AtcType.EdgeLossy;
            case "No Connectivity":
                return AtcType.NoConnectivity;

        }
        return null;
    }

    public static String GetTypeName(AtcType type)
    {
        switch(type)
        {
            case TwoGDevelopingRural:
                return "2G Normal";
            case TwoGDevelopingUrban:
                return "2G Good";
            case ThreeGAverage:
                return "3G Normal";
            case ThreeGGood:
                return "3G Good";
            case Cable:
                return "Cable";
            case DSL:
                return "DSL";
            case EdgeAverage:
                return "Edge Narmal";
            case EdgeGood:
                return "Edge Good";
            case EdgeLossy:
                return "Edge Lossy";
            case NoConnectivity:
                return "No Connectivity";
        }
        return "";
    }

    public static String GetTypeName(String respons)
    {
        for (AtcType item : AtcType.values ()) {
            String temp = getPayload(item).replaceAll("\n","").replaceAll(" ", "");
            if (temp.equals(respons))
            {
                return GetTypeName(item);
            }
        }
        return "";
    }

    private static String getPayload(AtcType type)
    {
        switch (type) {
            case TwoGDevelopingRural:
                return "{\n" +
                        "    \"down\": {\n" +
                        "        \"rate\": 20,\n" +
                        "        \"loss\": {\n" +
                        "            \"percentage\": 2.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"delay\": {\n" +
                        "            \"delay\": 650,\n" +
                        "            \"jitter\": 0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"corruption\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"reorder\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0,\n" +
                        "            \"gap\": 0\n" +
                        "        },\n" +
                        "        \"iptables_options\": []\n" +
                        "    },\n" +
                        "    \"up\": {\n" +
                        "        \"rate\": 18,\n" +
                        "        \"loss\": {\n" +
                        "            \"percentage\": 2.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"delay\": {\n" +
                        "            \"delay\": 650,\n" +
                        "            \"jitter\": 0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"corruption\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"reorder\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0,\n" +
                        "            \"gap\": 0\n" +
                        "        },\n" +
                        "        \"iptables_options\": []\n" +
                        "    }\n" +
                        "}";
            case TwoGDevelopingUrban:
                return "{\n" +
                        "    \"down\": {\n" +
                        "        \"rate\": 35,\n" +
                        "        \"loss\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"delay\": {\n" +
                        "            \"delay\": 650,\n" +
                        "            \"jitter\": 0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"corruption\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"reorder\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0,\n" +
                        "            \"gap\": 0\n" +
                        "        },\n" +
                        "        \"iptables_options\": []\n" +
                        "    },\n" +
                        "    \"up\": {\n" +
                        "        \"rate\": 32,\n" +
                        "        \"loss\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"delay\": {\n" +
                        "            \"delay\": 650,\n" +
                        "            \"jitter\": 0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"corruption\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"reorder\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0,\n" +
                        "            \"gap\": 0\n" +
                        "        },\n" +
                        "        \"iptables_options\": []\n" +
                        "    }\n" +
                        "}";
            case ThreeGAverage:
                return "{\n" +
                        "    \"down\": {\n" +
                        "        \"rate\": 780,\n" +
                        "        \"loss\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"delay\": {\n" +
                        "            \"delay\": 100,\n" +
                        "            \"jitter\": 0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"corruption\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"reorder\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0,\n" +
                        "            \"gap\": 0\n" +
                        "        },\n" +
                        "        \"iptables_options\": []\n" +
                        "    },\n" +
                        "    \"up\": {\n" +
                        "        \"rate\": 330,\n" +
                        "        \"loss\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"delay\": {\n" +
                        "            \"delay\": 100,\n" +
                        "            \"jitter\": 0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"corruption\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"reorder\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0,\n" +
                        "            \"gap\": 0\n" +
                        "        },\n" +
                        "        \"iptables_options\": []\n" +
                        "    }\n" +
                        "}";
            case ThreeGGood:
                return "{\n" +
                        "    \"down\": {\n" +
                        "        \"rate\": 850,\n" +
                        "        \"loss\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"delay\": {\n" +
                        "            \"delay\": 90,\n" +
                        "            \"jitter\": 0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"corruption\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"reorder\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0,\n" +
                        "            \"gap\": 0\n" +
                        "        },\n" +
                        "        \"iptables_options\": []\n" +
                        "    },\n" +
                        "    \"up\": {\n" +
                        "        \"rate\": 420,\n" +
                        "        \"loss\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"delay\": {\n" +
                        "            \"delay\": 100,\n" +
                        "            \"jitter\": 0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"corruption\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"reorder\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0,\n" +
                        "            \"gap\": 0\n" +
                        "        },\n" +
                        "        \"iptables_options\": []\n" +
                        "    }\n" +
                        "}";
            case Cable:
                return "{\n" +
                        "    \"down\": {\n" +
                        "        \"rate\": 6000,\n" +
                        "        \"loss\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"delay\": {\n" +
                        "            \"delay\": 2,\n" +
                        "            \"jitter\": 0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"corruption\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"reorder\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0,\n" +
                        "            \"gap\": 0\n" +
                        "        },\n" +
                        "        \"iptables_options\": []\n" +
                        "    },\n" +
                        "    \"up\": {\n" +
                        "        \"rate\": 1000,\n" +
                        "        \"loss\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"delay\": {\n" +
                        "            \"delay\": 2,\n" +
                        "            \"jitter\": 0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"corruption\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"reorder\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0,\n" +
                        "            \"gap\": 0\n" +
                        "        },\n" +
                        "        \"iptables_options\": []\n" +
                        "    }\n" +
                        "}";
            case DSL:
                return "{\n" +
                        "    \"down\": {\n" +
                        "        \"rate\": 2000,\n" +
                        "        \"loss\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"delay\": {\n" +
                        "            \"delay\": 5,\n" +
                        "            \"jitter\": 0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"corruption\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"reorder\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0,\n" +
                        "            \"gap\": 0\n" +
                        "        },\n" +
                        "        \"iptables_options\": []\n" +
                        "    },\n" +
                        "    \"up\": {\n" +
                        "        \"rate\": 256,\n" +
                        "        \"loss\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"delay\": {\n" +
                        "            \"delay\": 5,\n" +
                        "            \"jitter\": 0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"corruption\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"reorder\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0,\n" +
                        "            \"gap\": 0\n" +
                        "        },\n" +
                        "        \"iptables_options\": []\n" +
                        "    }\n" +
                        "}";
            case EdgeAverage:
                return "{\n" +
                        "    \"down\": {\n" +
                        "        \"rate\": 240,\n" +
                        "        \"loss\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"delay\": {\n" +
                        "            \"delay\": 400,\n" +
                        "            \"jitter\": 0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"corruption\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"reorder\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0,\n" +
                        "            \"gap\": 0\n" +
                        "        },\n" +
                        "        \"iptables_options\": []\n" +
                        "    },\n" +
                        "    \"up\": {\n" +
                        "        \"rate\": 200,\n" +
                        "        \"loss\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"delay\": {\n" +
                        "            \"delay\": 440,\n" +
                        "            \"jitter\": 0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"corruption\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"reorder\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0,\n" +
                        "            \"gap\": 0\n" +
                        "        },\n" +
                        "        \"iptables_options\": []\n" +
                        "    }\n" +
                        "}";
            case EdgeGood:
                return "{\n" +
                        "    \"down\": {\n" +
                        "        \"rate\": 250,\n" +
                        "        \"loss\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"delay\": {\n" +
                        "            \"delay\": 350,\n" +
                        "            \"jitter\": 0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"corruption\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"reorder\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0,\n" +
                        "            \"gap\": 0\n" +
                        "        },\n" +
                        "        \"iptables_options\": []\n" +
                        "    },\n" +
                        "    \"up\": {\n" +
                        "        \"rate\": 200,\n" +
                        "        \"loss\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"delay\": {\n" +
                        "            \"delay\": 370,\n" +
                        "            \"jitter\": 0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"corruption\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"reorder\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0,\n" +
                        "            \"gap\": 0\n" +
                        "        },\n" +
                        "        \"iptables_options\": []\n" +
                        "    }\n" +
                        "}";
            case EdgeLossy:
                return "{\n" +
                        "    \"down\": {\n" +
                        "        \"rate\": 240,\n" +
                        "        \"loss\": {\n" +
                        "            \"percentage\": 1.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"delay\": {\n" +
                        "            \"delay\": 400,\n" +
                        "            \"jitter\": 0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"corruption\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"reorder\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0,\n" +
                        "            \"gap\": 0\n" +
                        "        },\n" +
                        "        \"iptables_options\": []\n" +
                        "    },\n" +
                        "    \"up\": {\n" +
                        "        \"rate\": 200,\n" +
                        "        \"loss\": {\n" +
                        "            \"percentage\": 1.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"delay\": {\n" +
                        "            \"delay\": 440,\n" +
                        "            \"jitter\": 0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"corruption\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"reorder\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0,\n" +
                        "            \"gap\": 0\n" +
                        "        },\n" +
                        "        \"iptables_options\": []\n" +
                        "    }\n" +
                        "}";
            case NoConnectivity:
                return "{\n" +
                        "    \"down\": {\n" +
                        "        \"rate\": 1,\n" +
                        "        \"loss\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"delay\": {\n" +
                        "            \"delay\": 0,\n" +
                        "            \"jitter\": 0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"corruption\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"reorder\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0,\n" +
                        "            \"gap\": 0\n" +
                        "        },\n" +
                        "        \"iptables_options\": []\n" +
                        "    },\n" +
                        "    \"up\": {\n" +
                        "        \"rate\": 1,\n" +
                        "        \"loss\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"delay\": {\n" +
                        "            \"delay\": 0,\n" +
                        "            \"jitter\": 0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"corruption\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0\n" +
                        "        },\n" +
                        "        \"reorder\": {\n" +
                        "            \"percentage\": 0.0,\n" +
                        "            \"correlation\": 0.0,\n" +
                        "            \"gap\": 0\n" +
                        "        },\n" +
                        "        \"iptables_options\": []\n" +
                        "    }\n" +
                        "}";
        }
        return null;
    }
}
