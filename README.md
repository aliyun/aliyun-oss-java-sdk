# OSS SDK for Java Developers

## Requirements

- Java 6+

## Build

```shell
git clone ...
cd aliyun-oss-java-sdk
mvn clean package -DskipTests
```

- to run function tests, you will have to config user account in src/test/java/com/aliyun/oss/integrationtests/TestConfig.java, 
- to run performance tests, you will have to config user account in runner_conf.xml,
  and make sure your project has corresponding service enabled.


## Authors && Contributors

- [Xing Mengbang](https://github.com/xingfeng2510)
- [Zhang Ting](https://github.com/dengwu12)

## License

licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html)
