package com.lyh.pojo;

import com.jcraft.jsch.Session;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextArea;

public class MyTextArea extends TextArea {

    private Session session;

    public static String user = "lyh";
    public static String password = "123456";
    public static String host = "hadoop102";
    public static int port = 22;


    // 头 [lyh@hadoop102 ~]$
    public static int title_start;  // 头的起始索引
    public static int title_end;    // 头的结束索引

    public static boolean needTitle = true;    // 是否需要加title
    public static boolean isMultiRow = false;    // 是否需要加title

    public void startShell(){
        Shell shell = new Shell();
        shell.show(host,user,password,port);
    }

    public MyTextArea() {

        this.caretPositionProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                System.out.println("current= "+newValue);
            }
        });
//        this.setOnKeyPressed(event -> {
//            switch (event.getCode()) {
//                // 如果这一行有title 不允许光标上移
//                case UP: {
//                    event.consume();
//                    break;
//                }
//                case ENTER: {
//                    String command = this.getText(title_end,this.getCaretPosition()).trim();
//                    if (command.equals("clear")){
//                        this.clear();
//                        title_start = this.getCaretPosition();
//                        String title = new Title(user, host, current_dir).toString();
//                        title_end = title_start + title.length();
//                        this.appendText(title);
//                        event.consume();    // 阻止回车键
//                        return;
//                    }
//                    session = SSHLinux.connect(host, port, user, password);
//                    String res = SSHLinux.exeCommand(session,command);
//                    this.appendText("\n"+res);
//                    // 是不是多行命令
//                    if (getText(this.getCaretPosition()-2,this.getCaretPosition()).contains("\\")){
//                        isMultiRow = true;
//                        needTitle = false;
//                    }else {
//                        isMultiRow = false;
//                        needTitle = true;
//                    }
//                    this.appendText("\n");
//                    if (needTitle){
//                        System.out.println("需要添加 title");
//                        title_start = this.getCaretPosition();
//                        String title = new Title().toString();
//                        title_end = title_start + title.length();
//                        System.out.println("start=" + title_start + ",end=" + title_end);
//                        this.appendText(title);
//                    }else {
//                        System.out.println("不需要加 title");
//                        StringBuilder builder = new StringBuilder();
//                        for (int i = 0; i < (title_end - title_start); i++){
//                            builder.append(" ");
//                        }
//                        this.appendText(builder.toString());
//                    }
//                    // 删除多余的回车
//                    event.consume();
//                    // 设置光标到最后一行
//                    this.positionCaret(this.getLength());
//                    break;
//                }
//                case LEFT: {
//                    if (this.getCaretPosition() - 1 <= title_end) {
//                        event.consume();
//                        break;
//                    }
//                }
//                case DELETE:
//                case BACK_SPACE:{
//                    if (this.getCaretPosition() <= title_end) {
//                        event.consume();
//                        break;
//                    }
//                }
//            }
//        });
    }
}
