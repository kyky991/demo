package com.zing.common.xxl.job.properties;

import lombok.Data;

@Data
public class ExecutorProperties {

	/**
	 * 执行器AppName [选填]
	 * 执行器心跳注册分组依据；为空则关闭自动注册
	 */
	private String appname = "xxl-job-executor";

	/**
	 * 执行器IP [选填]
	 * 默认为空表示自动获取IP，多网卡时可手动设置指定IP，该IP不会绑定Host仅作为通讯实用；
	 * 地址信息用于 "执行器注册" 和 "调度中心请求并触发任务"
	 */
	private String ip;

	/**
	 * 执行器端口号 [选填]
	 * 小于等于0则自动获取；默认端口为9999，单机部署多个执行器时，注意要配置不同执行器端口
	 */
	private Integer port = 0;

	/**
	 * 执行器通讯TOKEN [选填]
	 * 非空时启用
	 */
	private String accessToken;

	/**
	 * 执行器运行日志文件存储磁盘路径 [选填]
	 * 需要对该路径拥有读写权限；为空则使用默认路径
	 */
	private String logPath = "logs/applogs/xxl-job/jobhandler";

	/**
	 * 执行器日志文件保存天数 [选填]
	 * 过期日志自动清理, 限制值大于等于3时生效; 否则, 如-1, 关闭自动清理功能；
	 */
	private Integer logRetentionDays = 30;

}
