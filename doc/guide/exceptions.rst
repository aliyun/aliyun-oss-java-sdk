****************
异常
****************

OSS Java SDK 中有两种异常 ``ClientException`` 以及 ``OSSException`` ， 他们都继承自或者间接继承自 ``RuntimeException`` 。

ClientException
==================

ClientException指SDK内部出现的异常，比如未设置BucketName，网络无法到达等等。ClientException中可以通过 ``getErrorCode`` 方法获取到具体的错误码，目前支持以下几种：

==============================  =============================================
错误码                            描述
==============================  =============================================
ConnectionTimeout               连接超时
SocketTimeout                   Socket超时
InvalidResponse                 无法识别的返回结果
Unknown                         其他原因引起的异常
==============================  =============================================


OSSException
===================

OSSException指服务器端错误，它来自于对服务器错误信息的解析。OSSException一般有以下几个成员：

* **Code：** OSS返回给用户的错误码。
* **Message：** OSS给出的详细错误信息。
* **RequestId：** 用于唯一标识该次请求的UUID；当你无法解决问题时，可以凭这个RequestId来请求OSS开发工程师的帮助。
* **HostId：** 用于标识访问的OSS集群（目前统一为oss.aliyuncs.com）

下面是OSS中常见的异常：

==============================  =============================================
错误码                            描述
==============================  =============================================
AccessDenied                    拒绝访问
BucketAlreadyExists             Bucket已经存在
BucketNotEmpty                  Bucket不为空
EntityTooLarge                  实体过大
EntityTooSmall                  实体过小
FileGroupTooLarge               文件组过大
FilePartNotExist                文件Part不存在
FilePartStale                   文件Part过时
InvalidArgument                 参数格式错误
InvalidAccessKeyId              Access Key ID不存在
InvalidBucketName               无效的Bucket名字
InvalidDigest                   无效的摘要
InvalidObjectName               无效的Object名字
InvalidPart                     无效的Part
InvalidPartOrder                无效的part顺序
InvalidTargetBucketForLogging   Logging操作中有无效的目标bucket
InternalError                   OSS内部发生错误
MalformedXML                    XML格式非法
MethodNotAllowed                不支持的方法
MissingArgument                 缺少参数
MissingContentLength            缺少内容长度
NoSuchBucket                    Bucket不存在
NoSuchKey                       文件不存在
NoSuchUpload                    Multipart Upload ID不存在
NotImplemented                  无法处理的方法
PreconditionFailed              预处理错误
RequestTimeTooSkewed            发起请求的时间和服务器时间超出15分钟
RequestTimeout                  请求超时
SignatureDoesNotMatch           签名错误
TooManyBuckets                  用户的Bucket数目超过限制
==============================  =============================================

