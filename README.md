## 简介
用Java写的一个使用微信企业号(团队号)进行消息推送的小项目

## 依赖
1. Java8

1. Maven(3.3.9)

1. git 

## 步骤

1. [申请企业号](https://qy.weixin.qq.com/) 具体自行百度

1. 编写配置文件 [cp.properties](https://raw.githubusercontent.com/anjia0532/weixin-qiye-alert/master/src/main/resources/cp.properties)

```bash

$ git clone https://github.com/anjia0532/weixin-qiye-alert.git

$ cd weixin-qiye-alert

$ mvn clean package

$ java -jar ./target/wechat-qiye-alert.jar /path/to/cp.properties tagName msg INFO

```
## 说明

`java -jar ./target/wechat-qiye-alert.jar <配置文件完整路径> <标签名称> <推送信息> [日志输出级别]`

<>必填项

[]选填项

日志默认输出级别是 WARN (可选值为 ALL,TRACE,DEBUG,INFO,WARN,ERROR,OFF)

参见 https://github.com/anjia0532/weixin-qiye-alert/blob/master/src/main/java/com/anjia/WeiXinAlert.java#L50

## 注意
如果指定标签名称不存在，会自动通过api创建一个标签（处于锁定状态），需要管理员，手动解锁，并且添加成员
如果指定标签下没有成员(标签添加部门无效)，则会根据`cp.properties`指定的部门id`PartyId`和人员id`UserId`进行发送
如果部门下没有成员，而且指定的用户也没有关注该企业号，则会**将信息推送给该企业号全部已关注成员，测试时需谨记**
