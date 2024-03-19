package com.lyh.pojo;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class MyUserInfo implements UserInfo, UIKeyboardInteractive {
    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean promptPassword(String s) {
        return false;
    }

    @Override
    public boolean promptPassphrase(String s) {
        return false;
    }

    /*====这里====*/
    @Override
    public boolean promptYesNo(String s) {
        //return false;
        return true;
    }

    @Override
    public void showMessage(String s) {
    }


    @Override
    public String[] promptKeyboardInteractive(String s, String s1, String s2, String[] strings, boolean[] booleans) {
        return null;
    }
}

