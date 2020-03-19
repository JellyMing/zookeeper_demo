package com.hoodinn;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class mainApp {
    public static void main(String[] args) throws Exception {
        System.out.println("hello zk");
        //zk没有连接池的概念
        //watch 观察，回调
        //watch的注册发生在该类型调用get exites...
        //第一类： 在new zk的时候，传入这个watch，session级别的参数，和path和node没有关系
    
        final CountDownLatch cd = new CountDownLatch(1);
        String hosts = "test01:2181,test02:2181,test03:2181";
        ZooKeeper zk = new ZooKeeper(hosts, 3000, new Watcher() {
            //watch 回调方法
            @Override
            public void process(WatchedEvent event) {
                Event.KeeperState state = event.getState();
                Event.EventType type = event.getType();
                String path = event.getPath();
                System.out.println(event.toString());
    
                switch (state) {
                    case Unknown:
                        break;
                    case Disconnected:
                        break;
                    case NoSyncConnected:
                        break;
                    case SyncConnected:
                        System.out.println("Connected...........");
                        cd.countDown();
                        break;
                    case AuthFailed:
                        break;
                    case ConnectedReadOnly:
                        break;
                    case SaslAuthenticated:
                        break;
                    case Expired:
                        break;
                }
    
                switch (type) {
                    case None:
                        break;
                    case NodeCreated:
                        break;
                    case NodeDeleted:
                        break;
                    case NodeDataChanged:
                        break;
                    case NodeChildrenChanged:
                        break;
                }
    
    
            }
        });
    
        cd.await();
        ZooKeeper.States state = zk.getState();
        switch (state) {
            case CONNECTING:
                System.out.println("ing..............");
                break;
            case ASSOCIATING:
                System.out.println("ASSOCIATING ......................");
                break;
            case CONNECTED:
                System.out.println("Connected ......................");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                System.out.println("Closed ......................");
                break;
            case AUTH_FAILED:
                System.out.println("Auth failed ......................");
                break;
            case NOT_CONNECTED:
                break;
        }
    
        String pahtName = zk.create("/ooxx", "olddata".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        Stat stat = new Stat();
        System.out.println("path: " + pahtName);
    
        byte[] node = zk.getData("/ooxx", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("getData watch 2: " + event.toString());
                try {
                    //true 是 default Watcher  new zk 的那个watcher
                    zk.getData("/ooxx",this, stat);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, stat);
    
        System.out.println("node：" + new String(node));
        
        Stat stat1 = zk.setData("/ooxx", "newdata".getBytes(), 0);
    
        zk.setData("/ooxx","newdata01".getBytes(), stat1.getVersion());
        
        
        System.out.println("---------------async start ------------------------");
    
        zk.getData("/ooxx", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
                System.out.println("---------------async callback -------------------------");
                System.out.println("o to string" + o.toString());
                System.out.println(new String(bytes));
    
            }
        },"abc" );
        System.out.println("---------------async over ----------------------");

        
        zk.close();
    }
}
