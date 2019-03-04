package com.verlif.simplecal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Long first;
    Long next;
    Long result;
    String cal;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.show);
        //设置滚动条
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public void run(View view) {
        //获取按钮显示的文本信息
        String value = ((Button) findViewById(view.getId())).getText().toString();
        //结果输出
        if (value.equals("=")) {
            if (next != null && cal != null) {
                //连续按“=”时自动补全运算
                if (result != null) {
                    textView.append(cal);
                    textView.append(next + "");
                    first = result;
                }
                getResult();
                textView.append("\n" + result);
            }
        }
        //运算符号输入
        else if (value.equals("+") || value.equals("-") || value.equals("*") || value.equals("/")) {
            if (first == null && result == null) {
                Toast.makeText(this, "no first number", Toast.LENGTH_LONG).show();
                return;
            }
            //连续运算判定
            if (next != null && first != null) {
                getResult();
                first = result;
                textView.append("\n" + result);
                cal = value;
                textView.append(value);
            }
            else {
                if (first == null)
                    first = result;
                next = null;
                cal = value;
                textView.append(value);
            }
            result = null;
        }
        //数字按下
        else {
            if (result != null)
                clear(view);
            if (cal == null) {
                if (first == null)
                    first = Long.valueOf(0);
                first *= 10;
                first += Integer.valueOf(value);
            }
            else {
                if (next == null || result == null)
                    next = Long.valueOf(0);
                next *= 10;
                next += Integer.valueOf(value);
            }
            //显示框显示
            textView.append(value);
        }

    }

    public void clear(View view) {
        first = null;
        cal = null;
        next = null;
        result = null;
        textView.setText("");
    }

    /**
     * 从变量@params first和运算符@params cal以及变量@params next中求取结果，并将first置为null
     */
    private void getResult() {
        switch (cal) {
            case "+":
                result = first + next;
                break;
            case "-":
                result = first - next;
                break;
            case "*":
                result = first * next;
                break;
            case "/":
                result = first / next;
                break;
        }
        first = null;
    }

}
