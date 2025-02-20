1.拉取代码后将“数基智能系统内部依赖包.zip”解压到maven本地仓库中

2.在nacos配置中心添加配置文件：  shujiai-ka-onpremise.properties
    配置文件内容复制nacos-config目录下shujiai-ka-onpremise.properties的内容
    
3.修改registry.cn-hangzhou.aliyuncs.com/shujiai/shujiai-ka为自己的镜像仓库地址

4.本地调试需要绑定hosts
onpremise-nacos-server xxx.xxx.xxx.xxx
onpremise-redis-server xxx.xxx.xxx.xxx
onpremise-mysql-server xxx.xxx.xxx.xxx
onpremise-yundong-rule xxx.xxx.xxx.xxx
