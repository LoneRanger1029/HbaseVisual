package com.lyh.panes;

import javafx.scene.layout.BorderPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;


/**
 * 中心面板 - 展示表格或者代码区
 * 单例模式-保证操作的是同一个代码区
 */
public class MyCenterPane extends BorderPane {
    private final static CodeArea codeArea = new CodeArea();
    private final static MyCenterPane myCenterPane = new MyCenterPane();

    private MyCenterPane(){
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));   //添加行号
        this.setCenter(codeArea);
    }

    public static MyCenterPane getInstance(){
        return myCenterPane;
    }

}
