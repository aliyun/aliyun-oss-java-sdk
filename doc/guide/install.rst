****************
安装OSS JAVA SDK
****************

直接在Eclipse中使用JAR包
=============================

步骤如下：

* 在官方网站下载 `Open Service Java SDK`_ 。
* 解压文件。
* 将解压后文件夹中的文件： aliyun-openservice-<versionId>.jar 以及lib文件夹下的所有文件拷贝到你的工程文件夹中。
* 在Eclipse右键工程 -> Properties -> Java Build Path -> Add JARs 。
* 选择你拷贝的所有JAR文件。

经过上面几步之后，你就可以在工程中使用OSS JAVA SDK了。

.. _Open Service Java SDK: http://bbs.aliyun.com/job.php?spm=0.0.0.0.kfoQsV&action=download&aid=36110

在Maven工程中使用SDK
=======================

在Maven工程中使用JAVA SDK十分简单，只要在在pom.xml文件中加入依赖就可以了。

在 ``dependencies`` 标签内加入如下内容：

.. code-block:: xml

    <dependency>
        <groupId>com.aliyun.openservices</groupId>
        <artifactId>aliyun-openservices</artifactId>
        <version>1.0.10</version>
    </dependency>

version为版本号，随着版本更新可能有变动。

