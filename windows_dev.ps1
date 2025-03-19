## 注意：千万别对此脚本格式化，‘=’附近不能有空格
## 如果执行报： .\windowsgo.ps1 : 无法加载文件 D:\company\shujiai-performance\windowsgo.ps1，因为在此系统上禁止运行脚本。
## 请用管理员打开powershell 并执行 -> set-ExecutionPolicy RemoteSigned
# 打包文件
echo "start package`:"
mvn clean package -pl shujiai-ka-start -am  -DskipTests -PwindowsDev

# 生成镜像
echo "start build images`:"
cd .\shujiai-ka-start\
docker build -f .\Dockerfile -t registry.cn-hangzhou.aliyuncs.com/shujiai/shujiai-kaserver:2.0.0-SNAPSHOT --build-arg JAR_FILE=./target/shujiai-ka-start-2.0.0-SNAPSHOT.jar .
if (!$?)
{
    echo 'docker build fail.'
    exit
}
echo "start push images`:"
docker push registry.cn-hangzhou.aliyuncs.com/shujiai/shujiai-kaserver:2.0.0-SNAPSHOT
cd ..\
