## 简介
用Java写的一个使用微信企业号(团队号)进行消息推送的小项目

### 为何使用微信企业号(团队号)

1. 微信使用较邮箱频繁，有问题及时知晓

1. 如果手机停机，短信不可达

1. 短信受限于70字符，如果大段文字，容易导致短信费激增(微信文本消息，最长2048个字节 1汉字=2字母=2字节)

1. 微信企业号支持发送文本、图片、语音、视频、文件、图文等消息类型，方式多样

1. 减少短信费用（报警类的短信量会比较大）

1. 部分手机会或邮箱会将报警信息拉入黑名单，导致干系人无法及时获知报警信息

1. 基于标签发送，更灵活的管理接收报警信息的人员，不必要硬编码

### 不足

1. 客户端未登陆，客户端无网络情况下，会延迟接收（但不会丢信息）

1. 未认证企业号叫团队号，上限为200人

1. api创建的标签默认是锁定状态，需要管理员手动解锁并添加成员


## 依赖

1. Java8

1. Maven(3.3.9)

1. Git

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

日志默认输出级别是 ERROR (可选值为 ALL,TRACE,DEBUG,INFO,WARN,ERROR,OFF)

参见 https://github.com/anjia0532/weixin-qiye-alert/blob/master/src/main/java/com/anjia/WeiXinAlert.java#L50

## 注意
如果指定标签名称不存在，会自动通过api创建一个标签（处于锁定状态），需要管理员，手动解锁，并且添加成员
如果指定标签下没有成员(标签添加部门无效)，则会根据`cp.properties`指定的部门id`PartyId`和人员id`UserId`进行发送
如果部门下没有成员，而且指定的用户也没有关注该企业号，则会**将信息推送给该企业号全部已关注成员，测试时需谨记**
