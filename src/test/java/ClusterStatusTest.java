import com.lyh.utils.HBaseConnection;
import org.apache.hadoop.hbase.ClusterMetrics;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.client.Admin;

import java.io.IOException;
import java.util.List;

public class ClusterStatusTest {
    public static void main(String[] args) throws IOException {

        Admin admin = HBaseConnection.connection.getAdmin();
        ClusterMetrics clusterMetrics = admin.getClusterMetrics();

        int regionCount = clusterMetrics.getRegionCount();  // 在线 regionServer 个数

        List<ServerName> deadServerNames = clusterMetrics.getDeadServerNames(); // 死亡节点的个数

        String clusterId = clusterMetrics.getClusterId();   // 集群ID

        double averageLoad = clusterMetrics.getAverageLoad();   //平均 region 个数

        String hBaseVersion = clusterMetrics.getHBaseVersion(); //hbase版本

        long requestCount = clusterMetrics.getRequestCount();   //集群当前请求数量

        System.out.println("当前在线 regionServer 数量: "+regionCount);
        System.out.println("死亡节点: ");
        for (ServerName deadServerName : deadServerNames) {
            String serverName = deadServerName.getServerName();
            System.out.println(serverName);
        }
        System.out.println("平均管理 region 数量: "+averageLoad);
        System.out.println("当前集群ID: "+clusterId);
        System.out.println("当前hbase版本: "+hBaseVersion);
        System.out.println("当前集群请求数量: "+requestCount);

    }
}
