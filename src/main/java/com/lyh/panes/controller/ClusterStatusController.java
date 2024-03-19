package com.lyh.panes.controller;

import com.jfoenix.controls.JFXButton;
import com.lyh.app.AppController;
import com.lyh.utils.HBaseConnection;
import com.lyh.utils.ui.AddIconUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.hadoop.hbase.ClusterMetrics;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class ClusterStatusController {

    public static String hosts;

    private final static Logger logger = Logger.getLogger(ClusterStatusController.class);

    private static ClusterStatusController instance;

    public static ClusterStatusController getInstance() {
        if (instance == null){
            instance = new ClusterStatusController();
        }
        return instance;
    }

    @FXML
    public JFXButton hidePane;
    @FXML
    public JFXButton cluster;
    @FXML
    public Label cluster_id;
    @FXML
    public Label regionServer_nums;
    @FXML
    public Label dead_regionServer;
    @FXML
    public Label avg_region;
    @FXML
    public Label current_request;
    @FXML
    public Label hbase_version;

    public ClusterStatusController(){}

    @FXML
    public void initialize(){
        addClusterIcon(cluster,"icon/server.png"," hbase集群 ");
        cluster.setTooltip(new Tooltip("点击获取最新集群状态"));
        AddIconUtils.addIconToButton(hidePane,"icon/close.png",15);
        hidePane.setOnAction(event -> {
            AppController.clusterStatus.setSelected(false);
        });
    }

    public static void addClusterIcon(Button button, String iconUrl, String button_text){
        Image image = new Image(iconUrl);
        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setImage(image);
        VBox vBox = new VBox();
        vBox.getChildren().addAll(imageView,new Text(button_text));
        button.setGraphic(vBox);
    }

    public static void setHosts(String hosts) {
        ClusterStatusController.hosts = hosts;
    }

    @FXML
    public void getCurrentClusterStatus() throws IOException {

        if (hosts!=null && !hosts.equals("")){
            Admin admin = HBaseConnection.connection.getAdmin();
            ClusterMetrics clusterMetrics = admin.getClusterMetrics();

            int regionCount = clusterMetrics.getRegionCount();  // 在线 regionServer 个数
            regionServer_nums.setText(regionCount+"");

            List<ServerName> deadServerNames = clusterMetrics.getDeadServerNames(); // 死亡节点
            if (deadServerNames.size()!=0){
                dead_regionServer.setText(deadServerNames.toString());
            }else {
                dead_regionServer.setText("0");
            }

            String clusterId = clusterMetrics.getClusterId();   // 集群ID
            cluster_id.setText(clusterId);
            cluster_id.setTooltip(new Tooltip(clusterId));

            double averageLoad = clusterMetrics.getAverageLoad();   //平均 region 个数
            DecimalFormat decimalFormat = new DecimalFormat("#.00");//四舍五入获取小数点后两位
            avg_region.setText(decimalFormat.format(averageLoad));

            String hBaseVersion = clusterMetrics.getHBaseVersion(); //hbase版本
            hbase_version.setText(hBaseVersion);

            long requestCount = clusterMetrics.getRequestCount();   //集群当前请求数量
            current_request.setText(requestCount+"");

            logger.info("当前在线 regionServer 数量: "+regionCount);
            logger.info("死亡节点: ");
            for (ServerName deadServerName : deadServerNames) {
                String serverName = deadServerName.getServerName();
                logger.info(serverName);
            }
            logger.info("平均管理 region 数量: "+averageLoad);
            logger.info("当前集群ID: "+clusterId);
            logger.info("当前hbase版本: "+hBaseVersion);
            logger.info("当前集群请求数量: "+requestCount);
            admin.close();
        }
    }

    public JFXButton getCluster() {
        return cluster;
    }
}
