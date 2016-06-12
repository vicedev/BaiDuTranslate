package com.vice.baidutranslate;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {


    private EditText etInput;
    private Button btnTranslate;
    private  EditText etResult;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private String from="auto";
    private String to="auto";

    String[] language={"auto","zh","en","yue","wyw","jp","kor","fra","spa","th","ara","ru","pt","de","it","el","nl","pl","bul","est","dan","fin","cs","rom"
            ,"slo","swe","hu","cht"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etInput = (EditText) findViewById(R.id.et_input);
        btnTranslate = (Button) findViewById(R.id.btn_translate);
        etResult = (EditText) findViewById(R.id.et_result);
        spinnerFrom = (Spinner) findViewById(R.id.spinner_from);
        spinnerTo = (Spinner) findViewById(R.id.spinner_to);

        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String request = etInput.getText().toString();
                RequestUtils requestUtils=new RequestUtils();
                if (!request.isEmpty()){
                    try {
                        requestUtils.translate(request, from, to, new HttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                etResult.setText(result);
                            }

                            @Override
                            public void onFailure(String exception) {
                                etResult.setText(exception);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(MainActivity.this,"请输入要翻译的内容!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                from=language[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                to=language[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}
