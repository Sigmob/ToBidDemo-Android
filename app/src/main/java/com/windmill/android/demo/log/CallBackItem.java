package com.windmill.android.demo.log;

/**
 * created by lance on   2021/12/9 : 9:34 上午
 */
public class CallBackItem {
    private String text;
    private String child_text;
    private boolean is_expand;
    private boolean is_callback;

    public CallBackItem(String text, String child_text, boolean is_expand, boolean is_callback) {
        this.text = text;
        this.child_text = child_text;
        this.is_expand = is_expand;
        this.is_callback = is_callback;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getChild_text() {
        return child_text;
    }

    public void setChild_text(String child_text) {
        this.child_text = child_text;
    }

    public boolean is_expand() {
        return is_expand;
    }

    public void set_expand(boolean is_expand) {
        this.is_expand = is_expand;
    }

    public boolean is_callback() {
        return is_callback;
    }

    public void set_callback(boolean is_callback) {
        this.is_callback = is_callback;
    }
}
