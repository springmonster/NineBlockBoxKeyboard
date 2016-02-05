package com.khch.nineblockboxkeyboard;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private TextView mSearchTextView;
    private PopupWindow mPopupWindow;
    private LinearLayout mPopupLinearLayout;
    private HashMap<Button, LinearLayout> mButtonLinearLayoutHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchTextView = (TextView) findViewById(R.id.activity_search_textview);

    }

    public void onContainerClick(View view) {
        if (view.getId() == R.id.activity_search_keyboard_btn_clear) {
            clearSearchTextView();
        } else if (view.getId() == R.id.activity_search_keyboard_btn_del) {
            deleteLastCharacterFromSearchTextView();
        } else if (view.getId() == R.id.activity_search_ninebox_keyboard_btn_num_one) {
            appendSearchTextViewText(((Button) view).getText().toString());
        } else if (view.getId() == R.id.activity_search_ninebox_keyboard_btn_num_zero) {
            appendSearchTextViewText(((Button) view).getText().toString());
        } else {
            showKeyboardPopupWindow((Button) view);
        }
    }

    private void clearSearchTextView() {
        mSearchTextView.setText("");
    }

    private void deleteLastCharacterFromSearchTextView() {
        String text = mSearchTextView.getText().toString();
        if (text.length() >= 1) {
            mSearchTextView.setText(text.substring(0, text.length() - 1));
        }
    }

    private void appendSearchTextViewText(String input) {
        mSearchTextView.append(input);
    }

    private void showKeyboardPopupWindow(final Button btnParent) {
        final LinearLayout linearLayout = getPopupWindowLinearLayout(btnParent);

        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(linearLayout, ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, true);
        } else {
            mPopupWindow.setContentView(linearLayout);
        }

        linearLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = linearLayout.getMeasuredWidth();
        int popupHeight = linearLayout.getMeasuredHeight();

        int[] location = new int[2];
        btnParent.getLocationOnScreen(location);

        int linearLayoutX = location[0] + btnParent.getWidth() / 2 - popupWidth / 2;
        int linearLayoutY = location[1] + btnParent.getHeight() / 2 - popupHeight / 2;

        mPopupWindow.setFocusable(true); // 添加此属性才能获取焦点
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 添加此属性才能响应back按键
        mPopupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        mPopupWindow.showAtLocation(btnParent, Gravity.NO_GRAVITY, linearLayoutX, linearLayoutY);
    }

    private LinearLayout getPopupWindowLinearLayout(Button btnParent) {
        if (mButtonLinearLayoutHashMap.containsKey(btnParent)) {
            return mButtonLinearLayoutHashMap.get(btnParent);
        } else {
            LinearLayout linearLayout = createButtonLinearLayout(btnParent);
            mButtonLinearLayoutHashMap.put(btnParent, linearLayout);
            return linearLayout;
        }
    }

    private LinearLayout createButtonLinearLayout(Button btnParent) {
        mPopupLinearLayout = new LinearLayout(this);
        mPopupLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        mPopupLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mPopupLinearLayout.setBackground(getResources().getDrawable(R.drawable.activity_search_ninebox_keyboard_popupwindow_selector));

        for (int i = 0; i < btnParent.getText().length(); i++) {
            final String text = String.valueOf(btnParent.getText().charAt(i));
            Button btn = new Button(this);
            btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
            btn.setFocusable(true);
            btn.setFocusableInTouchMode(true);
            btn.setText(text);
            btn.setTextSize(30);
            btn.setTextColor(Color.WHITE);
            btn.setBackground(getResources().getDrawable(R.drawable.activity_search_ninebox_keyboard_button_selector));
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appendSearchTextViewText(text);
                    dismissPopupWindow(mPopupLinearLayout);
                }
            });
            mPopupLinearLayout.addView(btn);
        }
        return mPopupLinearLayout;
    }

    private boolean dismissPopupWindow(LinearLayout linearLayout) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            viewRequestFocus(linearLayout.getChildAt(0)); // reset焦点
            mPopupWindow.dismiss();
            return true;
        }
        return false;
    }

    private void viewRequestFocus(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }
}
