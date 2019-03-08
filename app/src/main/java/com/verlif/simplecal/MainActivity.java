package com.verlif.simplecal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private double first;
    private double next;
    private double result;
    private String cal;
    private TextView textView;

    /** 当前的输入状态码
     *  0   -正在输入第一位数
     *  1   -正在输入符号位
     *  2   -正在输入第二位
     *  3   -等到结果
     */
    private int inputState;
    private boolean isThisPoint;            //当前是否处于输入小数点后数字的状态
    private double pointLocation;              //当前输入的小数位模拟

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //变量初始化
        textView = findViewById(R.id.show);
        resetDecimal();

        textView.setMovementMethod(ScrollingMovementMethod.getInstance());              //设置滚动条
    }

    public void run(View view) {
        //获取按钮显示的文本信息
        String value = ((Button) findViewById(view.getId())).getText().toString();
        //结果输出
        switch (value) {
            case "=":
                switch (inputState) {
                    case 2:             //一般运算判定
                        break;
                    case 3:             //连等判定
                        //自动补全符号和第二位
                        textView.append(cal);
                        textView.append(next + "");
                        first = result;
                        break;
                    default:
                        displayToast(getResources().getString(R.string.illegal_input_tip));
                        return;
                }
                getResult();
                displayResult();
                inputState = 3;
                resetDecimal();
                break;

            //运算符号输入
            case "/":
                if (first == 0) {
                    displayToast(getResources().getString(R.string.illegal_input_tip));
                    return;
                }
            case "+":
            case "-":
            case "*":
                switch (inputState) {
                    case 2:             //未得到结果的连续运算
                        cal = value;
                        getResult();
                        displayResult();             //输出上一步的结果
                        break;
                    case 3:             //得到结果的连续运算
                        cal = value;
                        first = result;
                        break;
                }
                cal = value;
                next = 0;
                textView.append(value);                     //输出本次的运算符
                inputState = 1;
                resetDecimal();
                break;

            //数字按下
            default:
                int number = Integer.valueOf(value);                //记录本次输入的数字
                switch (inputState) {
                    case 0:                     //输入第一个运算数
                        if (isThisPoint) {              //小数判定
                            first += number * pointLocation;
                            pointLocation *= 0.1;
                        }
                        else {                          //整数输入
                            first *= 10;
                            first += number;
                        }
                        inputState = 0;
                        break;
                    case 1:                     //输入第二个运算数
                    case 2:
                        if (isThisPoint) {              //小数判定
                            next += number * pointLocation;
                            pointLocation *= 0.1;
                        }
                        else {                          //整数输入
                            next *= 10;
                            next += number;
                        }
                        inputState = 2;
                        break;
                    case 3:                     //得到结果后
                        textView.append("\n");
                        first = number;
                        inputState = 0;
                    default:
                        break;
                }
                //显示框显示
                textView.append(value);
                break;
        }

    }

    public void clear(View view) {
        first = 0;
        cal = null;
        next = 0;
        result = 0;
        textView.setText("");
        inputState = 0;
        resetDecimal();
    }

    public void square(View view) {
        switch (inputState) {
            case 1:                 //用第一个数平方，忽略后面的运算符
                back(view);
            case 0:
                next = first;
                cal = "*";
                break;
            case 2:                 //先计算前面的运算，在用结果平方
                getResult();
                displayResult();
            case 3:                 //用结果平方
                first = result;
                next = result;
                cal = "*";
                break;
        }
        textView.append(cal + next);
        getResult();
        displayResult();
        inputState = 3;
    }

    public void back(View view) {
        if (textView.getText().length() <= 0)
            return;
        else {
            //得到将会被去除的最后一位
            String value = textView.getText().toString().substring(textView.getText().length() - 1, textView.getText().length());

            if (value.equals("\n"))
                return;
            //小数判定
            if (isThisPoint) {
                if (!value.equals(".")) {             //当删除的小数位时
                    pointLocation *= 10;
                    switch (inputState) {
                        case 0:
                            first -= Integer.valueOf(value) * pointLocation;
                            break;
                        case 2:
                            next -= Integer.valueOf(value) * pointLocation;
                            break;
                    }
                }
                else {
                    resetDecimal();
                }
            }
            else if (inputState == 3) {             //删除结果位
                textView.append("\n0");
                first = 0;
                next = 0;
                cal = null;
                result = 0;
                inputState = 0;
                resetDecimal();
                return;
            }
            else {
                switch (value) {
                    //操作符删除
                    case "+":
                    case "-":
                    case "*":
                    case "/":
                        cal = null;
                        inputState = 0;
                        break;
                    //数字删除
                    default:
                        switch (inputState) {
                            case 0:
                                first -= Integer.valueOf(value);
                                first /= 10;
                                break;
                            case 2:
                                next -= Integer.valueOf(value);
                                next /= 10;
                                break;
                        }
                }
            }
        }
        textView.setText(textView.getText().subSequence(0, textView.getText().length() - 1));
    }

    public void point(View view) {
        isThisPoint = true;
        textView.append(".");
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
        Log.i("RESULT", " cal: " + first + " " + cal + " " + next + " = " + result);
    }
    private void displayToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void displayResult() {
        textView.append("\n" + result);
    }

    /**
     * 重置小数标记，用于运算数输入完毕时
     */
    private void resetDecimal() {
        isThisPoint = false;
        pointLocation = 0.1;
    }

}
