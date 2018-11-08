#项目说明
>
    说明:
    系统主要使用SpringBoot 1.5.12 + Mybtais + PageHelper架构搭建，内嵌tomcat容器        ，项目前期使用maven进行管理。
    项目支持多数据源、支持读写分离，默认是写数据库，读的暂时不开启，后续需要时再开启。
    支持session Redis 集群共享；
    支持RedisMQ消息队列、RedisTopic消息订阅、支持动态定时任务
    idea需要安装lombok-1.16.20.jar插件;下载地址(https://www.projectlombok.org/downloads/lombok.jar)

###系统结构

通讯报文格式为JSON:

响应报文格式:
```
{
	"respCode":""
	"respMsg":""
	"data":{
		"xxxx":"123"
	}
```

|模块名称	|描述描述	|项目类型	|项目编码	|jdk要求
|:--|
|partner-web| 管理端服务 |maven(jar)|utf-8|1.8
|partner-common| 公共组件 |maven(jar)|utf-8|1.8
|partner-netty| netty组件 |maven(jar)|utf-8|1.8

|项目名称|环境	|数据库信息	|账号密码
|:--|
|mysql数据库|dev|127.0.0.1:3306 partner |root/root
|----
|Redis| dev| 127.0.0.1:6379	db:3 |无授权码

|项目名称|开发环境端口|测试环境端口|UAT环境端口|url定义
|:--|
|partner-web|8888|8888|8888|http://127.0.0.1:{port}/web
|partner-datamodel|8889|8889|8889|http://127.0.0.1:{port}/datamodel

git代码地址: https://github.com/zaiou/partner-base.git

------------------------------------------------------------------------

### 运行DEMO
    说明:
		所有项目通过直接右键com..zaiou.xxxxx.*Application.java 启动运行。
		
    * web模块:(因与前端交互设计的增删改查比较多，因此一级功能按模块来划分)
        * 包名称按照一级功能来分,然后有多少个子功能就有多少个controller,controller转到service那里进行业务处理。
        * com.zaiou.web.*
            * Application.java	启动类
            * MybatisGeneratorPlugs.java 	mybatis插件生成类
            
        * com.zaiou.web.mybatis.mapper.*	--> mybatis读写接口 java存放处(自动生成)
        
        * com.zaiou.web.mybatis.mapper.read.* --> mybatis只读接口(自动生成,暂时没开放)
        
        * com.zaiou.web.controller.*
            * UserController.java			用户管理
        
        * com.zaiou.web.constant.* 	常量类存放处
       
        
        * com.zaiou.web.service.* 	业务处理流程实现类该功能的业务都写在这里 命名:交易码Service
        
        
        * com.zaiou.web.vo.*		vo对象存放处命名:交易码Req/交易码Resp
        
        * com.zaiou.web.vo.ext.*	vo对象子对象存放处命名:交易码ReqExt/交易码RespExt/
        
        * com.zaiou.web.vo.list.*	vo对象子集合存放处命名:交易码ReqList/交易码RespList/
    
    ----
    
    * common模块:
    	* com.zaiou.common.annotation --> 自定义注解类存放
    		* IdCardValidate.java			-->身份证校验
    		* Desensitized.java			-->Json脱敏相关
            * SensitiveSerialized.java -->Json脱敏相关
    
    	* com.zaiou.common.aspect --> aop处理存放处(作用域全局)
    		* PrinterControllerAspect.java	--> 统一入口及出口日志输出
    
    	* com.zaiou.common.config -->公共配置
    		* CustomBeanConfiguration.java 	--> 自定义bean对象,例如通讯使用到的RestTemplate、Gson、HttpHeaders等均可在这里定义。
    		* CustomBeanNameGenerator.java	--> 自定义bean的生成策略，例如IB001,上下文id为IB001,使用此策略则上下文id为iB001,主要针对交易码连续2位以上为大写字母的，其它没影响。（待定）
    		* CustomFilterConfiguration.java	--> 自定义拦截器声明，例如线程名设置、xss拦截器等。
    		* ScheduleConfig.java	--> 任务调度线程池相关配置，例如线程池大小。
    		* Tomcat8Config.java	--> 自定义tomcat的优化等配置，例如线程池大小、Apache APR、NIO、NIO2等
    
    	* com.zaiou.common.constant.*	--> 常量存放 
    	
    	* com.zaiou.common.db.*	--> dao代理通用方法，简化sql调用
    
    	* com.zaiou.common.enums.*	--> 枚举类存放 
    
    	* com.zaiou.common.exception.*	--> 自定义异常
    	 
    	* com.zaiou.common.filter.*	--> 自定义过滤器拦截器实现类
    
    	* com.zaiou.common.mybatis.config	--> 数据库相关配置
    		* CustomSessionFactory.java			--> 公共po存放对象
    		* DataSourceConfiguration.java		--> 系统多数据源配置
    		* DruidMonitorConfiguration.java	--> 阿里链接池监控配置
    		* MybatisConfiguration.java			--> 读写数据源配置
    		* MybatisReadConfiguration.java		--> 只读数据源配置,根据mybatis.read.mapper-locations.enable开启或关闭
    	* com.zaiou.common.mybatis.interceptor	--> 拦截器,SQL日志打印
    	* com.zaiou.common.mybatis.mapper	--> 基础mapper
    	* com.zaiou.common.mybatis.po	--> 公共po存放对象
    	
    	* com.zaiou.common.redis.config --> redis配置
    		* RedisConfiguration.java		--> redis数据源等配置
    		* RedisObjectSerializer.java	--> redis序列化对象
    		* RedisSubscriptionListenerConfig.java -->redis topic 消息订阅监听器等配置
    		
    	* com.zaiou.common.redis.constant -->redis 常量
    	
    	* com.zaiou.common.redis.service -->redis常用操作接口 (待定)
    		* JedisService.java		--> 使用JedisPool执行Redis的常用命令
    		* MQProcessService.java	--> 使用MQ服务处理类 所有MQ服务都要实现这个类
    		* MQService.java	--> 使用MQ服务管理类，用于启动、手动注册MQ服务类
    		* RedisLockService.java		--> Redis锁接口,根据id或者key去锁定接口
    		* RedisService.java			--> 使用RedisTemplate执行Redis的常用命令
    		* TopicProcessService.java	--> JedisPool执行Redis的常用命令
    		* TopicService.java			--> 消息订阅管理类，提供发送接口(TopicObj类)
    		
    	* com.zaiou.common.redis.service.impl -->上述接口的实现类 (待定)
    		* AbstractTopicProcessService.java	--> MQ服务实现抽象类，所有MQ服务都要继承这个类(NOTE)
    		* RedisLockServiceImpl.java		--> 实现类
    		* RedisMQServiceImpl.java		--> 实现类
    		* RedisServiceImpl.java		--> 实现类
    		* TopicServiceImpl.java		--> 消息订阅管理类发送类,利用RedisTemplate进行发布消息
    		
    	* com.zaiou.common.redis.vo -->redis vo
    		
    	* com.zaiou.common.service	--> 业务接口类
    	* com.zaiou.common.utils		--> 工具类存放处
    	* com.zaiou.common.vo		--> 公共vo存放处
    
    ----
    
    * datamodel模块:
        * config 配置文件
        * com.zaiou.datamodel.api 项目入口
        * com.zaiou.datamodel.config 配置文件
        * com.zaiou.datamodel.mybatis.mapper mapper类
        * com.zaiou.datamodel.redis redis处理
    
    ---
    
### 项目主要详细
    * common模块
        * 系统错误码(异常处理)
        说明: com.zaiou.common.enums.ResultInfo定义系统异常返回码，异常返回码默认都会定义在sys_result_info表中，各个系统需要抛出异常通过com.zaiou.common.exception.BussinessException类根据错误码首先通过com.zaiou.common.service.CacheService类中的String getResultMsg(String system, String code)从redis缓存中获取异常信息，缓存没有的话从sys_result_info表获取异常信息并更新到缓存
        * 系统参数
        说明: 系统参数是那些可以重新设置值的参数;com.zaiou.common.constant中定义系统参数的key,系统参数信息默认都会 定义在sys_params表中；如果想获取系统参数首先通过com.zaiou.common.redis.constant.RedisKey的String getSyspara(String code)函数根据系统参数的code获取系统参数保存在redis缓存中的key;然后根据com.zaiou.common.service.CacheService类中的String getSysParams(String code)函数根据系统参数redis的key从缓存中获取系统参数的值；缓存没有的话从sys_params中获取并更新到缓存
        
    * web模块
        * 用户登录
        说明: 1、com.zaiou.web.controller.system.UserController中添加用户登录接口 ,用户 通过输入用户账号和密码，首先通过用户账号在sys_user表中查找用户，如果用户存在通过md5加盐加密用户密码和sys_user表中的用户密码进行比较，密码相同验证通过，密码不同的话提示用户密码错误，错误六次将被锁定；在密码相同验证通过的情况下后端会生成一个用户token,根据用户的token生成一个redis的key,用户相关信息为redis的值，并配置用户登录超时时间；另外根据用户账号生成一个reids的key,token为reids的值，并配置用户登录超时时间；最后登陆成功会更新密码错误次数为0次；      2、com.zaiou.web.interceptor.AuthInterceptor中设置用户会话拦截器，classpath:authIgnore.properties属性文件配置不需要用户会话拦截接口；需要会话拦截的接口会通过前端header传过来的token获取用户会话信息的reids的key,根据key获取用户信息（用户信息不存在的话登陆超时被下线），判断为首次登陆的话需要修改用户密码；然后根据用户账号获取缓存中用户token的key,根据key获取用户token,token不存在强制下线，前端的token和reids中的token不一致，用户在别处登陆；然后更新token和用户超时时间；最后将用户信息的reids的key放到attrbute中
        * 操作日志
        说明：com.zaiou.common.enums.LogOperateEnum中定义需要添加操作日志的接口，com.zaiou.web.vo.*中请求的vo类中需要记录日志的字段添加@Log注解(eg: @Log(fileName="登录账号"));com.zaiou.web.aspect.LogAspect切面类中对控制器进行拦截，当接口操作成功且需要添加操作日志的添加成功的操作记录;当接口操作抛出异常且需要添加操作日志的时候，只有在错误码为WEB_1001，即密码错误的时候才会添加操作日志，其他操作抛出异常不加入日志
    
    * datamodel模块
        * redis消息队列
        说明：redis做消息队列的订阅发布功能；首先实现订阅功能，com.zaiou.datamodel.config.StartupListener--->com.zaiou.datamodel.redis.subscribe.SubscribeService订阅相关频道；任何实现消息处理功能，通过订阅的频道接受发布的消息，com.zaiou.datamodel.redis.subscribe.SubscribeService--->com.zaiou.datamodel.redis.listener.RequestMsgListener--->com.zaiou.datamodel.redis.handler.DispatchMessageHandler--->com.zaiou.datamodel.redis.handler.impl.MessageThreadImpl--->com.zaiou.datamodel.redis.handler.impl.MessageHandlerImpl实现发布消息的消息处理；最后发布消息，在相关接口实现
        
        
        
        
