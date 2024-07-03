## 更新日志
- 2024-4-7 增加Shell命令行支持
![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/4325918b-c76b-4c55-aca6-fd9308090c59)

# HbaseVisual
HBase可视化客户端，支持excel<->hbase双向同步数据
简单说，目前市面上并没有（或者很少，比如web端的Hue）Hbase的可视化编程工具，桌面领域的工具更是少之又少，我的理解是HBase只提供了Java、Python等部分语言API，而对于桌面开发端语言C++、C#等并不支持（尽管可以通过一些跨语言接口比如Thrift、Rest，但是这样效率太低了），而Java桌面开发的技术人员在国内更是少之又少（只有少部分的爱好者，但是不乏很多大佬），所以不只是HBase，很多大数据框架的桌面应用开发都可以通过Java来实现。这一点，prettyZoo就是很好的证明(https://github.com/vran-dev/PrettyZoo)。此外，我们熟悉的 JetBrain 全家桶不都是Java开发出来的嘛。
所以，如果将来真的有幸从事大数据行业之后，待我编码能力日渐精进的情况下，希望自己能够有幸完成部分大数据框架的桌面端可视化工具开发。
说明一下：由于这个软件是我抽时间做的，开发过程异常艰辛（学校自习室没有电源、暖气！每天抽空出去找教室写三四个小时回宿舍充电再去，bug留上课去想，如此反复），虽然可以保证每天4~8个小时的开发，但是由于一些客观原因经常“打断施法”，比如中途教室有人上课、电脑没电（集群已启动咔咔掉电）、bug等，所以一些功能可能存在小bug请见谅，也正因为每天开发的时间不固定，导致经常思维连贯不起来，造成代码冗余、再到屎山，实在没有办法，相信之后在编码能力有所进步之后，一定会回来更新。
## 技术领域：
本作品涉及计算机软件技术，特别是一种基于JavaFX开发的HBase客户端软件，属于数据库管理与访问技术领域。该软件旨在为用户提供一个直观、易用的图形用户界面，以简化对HBase数据库中数据的查询、浏览、修改和删除，以及对于外部数据源如Excel等与HBase进行读写操作的支持。
## 背景技术：
HBase是一个开源的、分布式的、面向列的NoSQL数据库，它运行在Hadoop分布式文件系统（HDFS）之上，为大数据存储提供了高可靠性、高性能、可伸缩性的服务。尽管HBase具有非常强大的功能，但目前市场上的HBase客户端工具相对匮乏。其主要原因是HBase主要提供的是Java API，而没有提供如C++等其他编程语言的原生API，所以用C++等其他编程语言开发HBase的客户端并不实际。而且由于HBase内部工作机制和Java的紧密集成，使用Java API通常可以获得更好的性能，此外，采用Java天然的跨平台性能够帮我们省去对不同系统单独编译的麻烦。
此外，C/S架构的很多B/S架构应用所不具备的优点：
交互性强：C/S架构的PC应用通常具有更丰富的用户界面和更强大的交互功能，可以提供更好的用户体验。
安全性高：由于C/S架构的数据传输通常发生在局域网内，因此其安全性相对较高。此外，C/S架构也可以采用多层认证等安全措施来进一步增强安全性。
处理信息能力强：C/S架构的PC应用通常具有更强大的数据处理和信息管理能力，可以更有效地处理大量数据。
## 内容：
本项目提供了一种基于Java语言开发的HBase桌面客户端软件的实现，该系统能够方便的连接管理我们的HBase集群。具体来说，本系统包括以下部分：
- 主页
![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/55c52617-902e-487a-999a-04272f0e7f7b)
1. 数据查询模块： 该模块利用HBase原生API，用户可以通过可视化的窗口控件轻松的完成对数据的增（改）、删、查等操作，并实现了HBase的16种过滤器，可以方便的进行数据分析和查询。

![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/258924e3-0a69-4d09-aeb8-d5205f3a5f1a)
单行查询
![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/aff50520-0752-42eb-9f7a-c231f50838d6)

扫描查询（全表扫描）：
![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/ef4bf23b-ad5a-44e6-a466-31707327c44d)

范围扫描：
![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/63373ff1-2746-4013-a230-c5f8783bf3f6)

双击表格查询数据：
![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/c78d4df1-1143-496e-8163-f9e47d01228d)

2. 元数据管理模块：支持用户对集群的命名空间、表格等进行相关的创建、删除操作

![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/54900dd9-c84b-48c7-8727-4d91acf2cbf7)

命名空间操作
![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/dbd20de9-f05d-43d9-ac6c-7a1445688156)
![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/12d48788-7ef9-496b-a381-a90f659f74dd)

表操作
![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/adb50b68-4083-4def-aadc-f53988f21d7e)

表的创建：
![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/541e0d63-b387-4857-908d-eb9a4873af5d)

连接管理
![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/9047d400-11f3-4c10-82a2-ad57c02f8261)

![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/0905b108-461a-473c-b1fd-1f6d8ee52551)

3. 数据导入模块： 该模块利用Apache POI技术，使得用户可以方便的使用excel来作为数据源进行数据的导入，而非传统的一次只能插入一个单元格的数据。同时采用了Java的多线程技术，在普通笔记本电脑上可以实现了500条数据/s的传输速率。

![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/fb7b8083-893a-433b-b8dc-812546fe1a97)

![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/1bbcdbf8-14a5-47f2-88c2-c42818fd0422)

4. 数据导出模块：该模块同样使用Apache POI 技术，用户可以直接将HBase表中的数据以excel文件的格式下载到本地，方便用户进一步的数据分析。
![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/ffac83df-83dc-41f5-96fe-7e9c79a92126)

![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/fdfe0575-9f4b-4491-83b0-f611f9d979fd)

导出结果：
![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/1d5f814c-b317-4cf2-b9ac-28b643e38cdf)

5. 集群管理模块： 该模块可以实时获取到当前集群的运行情况，比如集群ID、活跃的RegionServer数量、平均每个RegionServer存储Region的数量、当前集群的请求数量等。
6. 用户日志模块： 该模块实时监控用户的行为数据，对用户的一些行为都会将日志打印输出到控制台，对系统的一些故障报错也会输出到控制台供专业人员查看

![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/35cbd571-0a31-432e-b2a8-675ed1e85e8c)

7. 用户反馈模块：通过JavaMail等技术，用户可以将自己的意见和系统的问题通过该软件自带的邮件发送器发送到作者邮箱，或者调用用户本地的邮箱软件来进行发送以及跳转到作者博客主页进行留言。
![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/3fb4761c-cadd-432b-88ee-e17dd1b32a65)

![image](https://github.com/LoneRanger1029/HbaseVisual/assets/107778802/bdd3306e-329a-4e16-8b2f-556be1706584)

## 版本展望
本软件为本人在校期间利用碎片时间做出来的，所以难免存在一些bug，比如Hbase16种过滤器的实现，很多还在测试阶段，并不能使用。尽管基于HDFS的这种数据存储框架都只支持增加数据并不支持修改，这里依然建议对于重要数据在使用该软件前进行备份！
关于下一个版本，首先重构是肯定的，尽管现在的代码量已经1.4w+，但是冗余度很高，毕竟抽时间做的，很多时候思维连贯不起来，直接重写逻辑。所以在下一个版本，我会对项目重构，对于一些全局性的设置变量，还是用内嵌数据库比较好。此外，还有很多很多要修改的地方。
总的来说，这个项目确实付出了我不少的心血，尽管我是菜鸡一枚。
