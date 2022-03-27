package com.zing.common.xxl.job.properties;

import lombok.Data;

@Data
public class AdminProperties {

	/**
	 * 调度中心部署地址 [选填]
	 * 如调度中心集群部署存在多个地址则用逗号分隔。
	 * 执行器将会使用该地址进行"执行器心跳注册"和"任务结果回调"；为空则关闭自动注册
	 */
	private String addresses;

}
