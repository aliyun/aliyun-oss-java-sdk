**********
快速入门
**********

在这一章里，您将学到如何用OSS Java SDK完成一些基本的操作。

Step 1. 初始化一个OSSClient
============================

OSSClient是与OSS服务交互的客户端，SDK的OSS操作都是通过OSSClient完成的。

下面代码新建了一个OSSClient:

.. code-block:: java

    import com.aliyun.openservices.oss.OSSClient;

    public class Sample {

        public static void main(String[] args) {
            String accessKeyId = "<key>";
            String accessKeySecret = "<secret>";

            // 初始化一个OSSClient
            OSSClient client = new OSSClient(accessKeyId, accessKeySecret);
            
            // 下面是一些调用代码...
            ...
        }
    }

在上面代码中，变量 ``accessKeyId`` 与 ``accessKeySecret`` 是由系统分配给用户的，称为ID对，用于标识用户，为访问OSS做签名验证。

关于OSSClient的详细介绍，参见 :ref:`ossclient-top` 。

Step 2. 新建Bucket
====================

Bucket是OSS上的命名空间，相当于数据的容器，可以存储若干数据实体（Object）。

你可以按照下面的代码新建一个Bucket：

.. code-block:: java

    public void createBucket(String bucketName) {

        // 初始化OSSClient
        OSSClient client = ...;

        // 新建一个Bucket
        client.createBucket(bucketName);
    }

由于Bucket的名字是全局唯一的，所以尽量保证你的 ``bucketName`` 不与别人重复。

关于Bucket的命名规范，参见 :ref:`Bucket命名规范 <bucket-name-rule>`。


Step 3. 上传Object
====================

Object是OSS中最基本的数据单元，你可以把它简单地理解为文件，用下面代码可以实现一个Object的上传：

.. code-block:: java
    
    public void putObject(String bucketName, String key, String filePath) throws FileNotFoundException {

        // 初始化OSSClient
        OSSClient client = ...;

        // 获取指定文件的输入流
        File file = new File(filePath);
        InputStream content = new FileInputStream(file);

        // 创建上传Object的Metadata
        ObjectMetadata meta = new ObjectMetadata();

        // 必须设置ContentLength
        meta.setContentLength(file.length());

        // 上传Object.
        PutObjectResult result = client.putObject(bucketName, key, content, meta);

        // 打印ETag
        System.out.println(result.getETag());    
    }

Object通过InputStream的形式上传到OSS中。在上面的例子里我们可以看出，每上传一个Object，都需要指定和Object关联的ObjectMetadata。ObjectMetaData是用户对该object的描述，由一系列name-value对组成；其中ContentLength是必须设置的，以便SDK可以正确识别上传Object的大小。

Put Object请求处理成功后，OSS会将收到文件的MD5值放在返回结果的ETag中。用户可以根据ETag检验上传的文件与本地的是否一致。

关于Object的命名规范，参见 :ref:`Object命名规范 <object-name-rule>` 。

关于上传Object更详细的信息，参见 :ref:`put-object` 。

Step 4. 列出所有Object
======================

当你完成一系列上传后，可能会需要查看在某个Bucket中有哪些Object，可以通过下面的程序实现：

.. code-block:: java

    public void listObjects(String bucketName) {

        // 初始化OSSClient
        OSSClient client = ...;
        
        // 获取指定bucket下的所有Object信息
        ObjectListing listing = client.listObjects(bucketName);
        
        // 遍历所有Object
        for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
            System.out.println(objectSummary.getKey());
        }
    }

listObjects方法会返回ObjectListing对象，ObjectListing对象包含了此次listObject请求的返回结果。其中我们可以通过ObjetListing中的getObjectSummaries方法获取所有Object的描述信息（List<OSSObjectSummary>）。

Step 5. 获取指定Object
=========================

你可以参考下面的代码简单地实现一个Object的获取：

.. code-block:: java

    public void getObject(String bucketName, String key) throws IOException {

        // 初始化OSSClient
        OSSClient client = ...;

        // 获取Object，返回结果为OSSObject对象
        OSSObject object = client.getObject(bucketName, key);

        // 获取Object的输入流
        InputStream objectContent = object.getObjectContent();

        // 处理Object
        ...      

        // 关闭流 
        objectContent.close();
    }

当调用OSSClient的getObject方法时，会返回一个OSSObject的对象，此对象包含了Object的各种信息。通过OSSObject的getObjectContent方法，还可以获取返回的Object的输入流，你可以读取这个输入流来对Object的内容进行操作；记得在用完之后关闭这个流。










