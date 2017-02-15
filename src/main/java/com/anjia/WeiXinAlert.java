package com.anjia;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.io.FileExistsException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpInMemoryConfigStorage;
import me.chanjar.weixin.cp.api.WxCpServiceImpl;
import me.chanjar.weixin.cp.bean.WxCpMessage;
import me.chanjar.weixin.cp.bean.WxCpTag;
import me.chanjar.weixin.cp.bean.messagebuilder.TextBuilder;

public class WeiXinAlert {
	

	static Logger logger = LoggerFactory.getLogger(WeiXinAlert.class);
	
	/**
	 * 微信配置类
	 */
	static WxCpInMemoryConfigStorage config = new WxCpInMemoryConfigStorage();
	/**
	 * 微信企业号API类
	 */
	static WxCpServiceImpl wxCpService = new WxCpServiceImpl();
	
	/**
	 * 部门id，标签内无用户时，缺省推送到的部门id
	 */
	static String partyId;
	
	/**
	 * 用户id，标签内无用户时，缺省推送到的用户id
	 */
	static String userId;
	
	/**
	 *
	 * @param args 长度为3的参数列表，args[0] 绝对路径的配置文件路径，args[1] 推送的标签名称 args[2] 推送信息 args[3] 设置日志级别可选值为  ALL,TRACE,DEBUG,INFO,WARN,ERROR,OFF
	 * @throws Exception 
	 *
	 * <p><b>创建日期</b>:2017年2月15日 上午10:07:41</p>
	 * <p><b>修改日期</b>:</p>
	 * @author SN_AnJia(anjia0532@qq.com)
	 * @version 1.0
	 * @since jdk 1.8
	 */
	public static void main(String[] args) throws Exception {
		
		//必要参数校验
		if (args.length<3 || StringUtils.isAnyBlank(args[0],args[1],args[2])) {
			logger.warn("缺少必要参数,{}",StringUtils.join(args));
			throw new Exception("缺少必要参数");
		}
		
		if (args.length==4) {
	        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
	        context.getLogger("com.anjia").setLevel(Level.valueOf(args[3]));
		}
		
		//判断配置文件是否存在
		File propertiesFile=new File(args[0]);
		
		if (!propertiesFile.exists()) {
			logger.warn("无法读取配置文件,{}",args[0]);
			throw new FileExistsException("无法读取配置文件");
		}
		
		//初始化微信企业号配置
		init(propertiesFile);
		
		//推送信息
		msg(args[1],args[2]);
	}
	
	/**
	 *
	 * @param file properties 文件
	 *
	 * <p><b>创建日期</b>:2017年2月15日 上午10:09:44</p>
	 * <p><b>修改日期</b>:</p>
	 * @author SN_AnJia(anjia0532@qq.com)
	 * @version 1.0
	 * @since jdk 1.8
	 */
	public static void init(File file){
		
		logger.info("初始化微信企业号配置");
		
		Properties properties=new Properties();
		
		//自动关闭资源
		try(FileInputStream fis=new FileInputStream(file)) {
			
			properties.load(fis);
			
			//企业号/团队号id
			String corpId=(String)properties.get("CorpID");
			//企业号/团队号 Secret
			String secret=(String)properties.get("Secret");
			//应用id
			String agentId=(String)properties.get("AgentId");
			//推送标签无用户时缺省推送的部门id
			partyId=(String)properties.get("PartyId");
			//推送标签无用户时缺省推送的用户id
			userId=(String)properties.get("UserId");
			
			logger.debug("企业号id:{};企业号Secret:{};应用id:{},部门Id:{};用户Id:{};",corpId,secret,agentId,partyId,userId);
			
			config.setCorpId(corpId);
			config.setCorpSecret(secret);
			config.setAgentId(Integer.parseInt(agentId));
			
		} catch (IOException e) {
			e.printStackTrace();
			//如果异常，直接退出
			System.exit(-1);
		}
		wxCpService.setWxCpConfigStorage(config);
	}
	
	/**
	 *
	 * @param tagName 标签名称
	 * @param msg 推送信息
	 *
	 * <p><b>创建日期</b>:2017年2月15日 上午10:12:38</p>
	 * <p><b>修改日期</b>:</p>
	 * @author SN_AnJia(anjia0532@qq.com)
	 * @version 1.0
	 * @since jdk 1.8
	 */
	public static void msg(String tagName,String msg) {
		
		logger.debug("推送信息");
		
		//必要校验
		if (StringUtils.isAnyBlank(tagName,msg)) {
			logger.warn("缺少必要参数,标签名称:{};推送信息:{}",tagName,msg);
			return;
		}
		
		try {
			String tagId = null;
			
			int flag=0;
			
			//根据标签名称获取标签对象
			Optional<WxCpTag> firstTag=wxCpService.tagGet().stream().filter(t->tagName.equals(t.getName())).findFirst();
			
			logger.debug("根据标签名称获取标签:{}",firstTag);
			
			//如果存在此标签则获取标签id
			if (!firstTag.isPresent()) {
				tagId = wxCpService.tagCreate(tagName);
				logger.warn("标签不存在，新建标签id为:{}",tagId);
			}else{
				//如果不存在此标签则创建并获取标签id
				tagId=firstTag.get().getId();
			}
			
			logger.debug("标签id为:{}",tagId);
			
			//构建文本消息
			TextBuilder messageBuilder = WxCpMessage
					.TEXT()
					.agentId(config.getAgentId())
					.content(msg);
			
			//企业号可以叠加推送到部门，标签，用户
			
			//标签下无用户，则推送部门或者用户
			if (wxCpService.tagGetUsers(tagId).isEmpty()) {
				
				logger.warn("标签下无用户");
				
				//如果部门id有效，并且部门有人，则推送到部门
				if (StringUtils.isNotBlank(partyId) && !wxCpService.departGetUsers(Integer.parseInt(partyId), true, 1).isEmpty()) {
					logger.info("推送到部门，部门id为:{}",partyId);
					messageBuilder.toParty(partyId);
					flag++;
				}
				
				//如果用户id有效且用户已关注，则推送到用户
				if (StringUtils.isNotBlank(userId) && wxCpService.userGet(userId).getStatus() == 1) {
					logger.info("推送到用户，用户id为:{}",userId);
					messageBuilder.toUser(userId);
					flag++;
				}
				
			}else{
				logger.info("推送到标签，标签id为:{}",tagId);
				//如果标签下有用户，则推送标签
				messageBuilder.toTag(tagId);
				flag++;
			}
			
			if (flag==0) {
				logger.warn("指定的部门，标签，用户 均无效，尝试推送到全部用户");
				messageBuilder.toUser("@all");
			}
			
			//推送信息
			wxCpService.messageSend(messageBuilder.build());
			
		} catch (WxErrorException e) {
			
			ExceptionUtils.getStackTrace(e);
			
			e.printStackTrace();
			
		}
	}
}
