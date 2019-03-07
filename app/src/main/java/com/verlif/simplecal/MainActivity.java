package com.verlif.simplecal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final static long MAX = 100000000;

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
        switch (value) {
            case "=":
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
                break;
            //运算符号输入
            case "+":
            case "-":
            case "*":
            case "/":
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
                } else {
                    if (first == null)
                        first = result;
                    next = null;
                    cal = value;
                    textView.append(value);
                }
                result = null;
                break;
            //数字按下
            default:
                if (first == null && result != null)
                    textView.append("\n");
                if (first == null) {
                    first = 0L;
                    first += Integer.valueOf(value);
                    cal = null;
                    next = null;
                    result = null;
                } else if (cal == null) {
                    if (first > MAX) {
                        Toast.makeText(this, "this number is too large", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    else {
                        first *= 10;
                        first += Integer.valueOf(value);
                    }
                } else {
                    if (next == null)
                        next = 0L;

                    if (next > MAX) {
                        Toast.makeText(this, "this number is too large", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    else {
                        next *= 10;
                        next += Integer.valueOf(value);
                    }
                }
                //显示框显示
                textView.append(value);
                break;
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
