## 注意：千万别对此脚本格式化，‘=’附近不能有空格
## 如果执行报： .\windowsgo.ps1 : 无法加载文件 D:\company\shujiai-ka\windowsgo.ps1，因为在此系统上禁止运行脚本。
## 请用管理员打开powershell 并执行 -> set-ExecutionPolicy RemoteSigned
# 打包文件
echo "start package`:"
mvn clean package -pl shujiai-ka-start -am  -DskipTests -Ponpremise

# 生成镜像
echo "start build images`:"
cd .\shujiai-ka-start\
docker build -f .\Dockerfile -t registry.cn-hangzhou.aliyuncs.com/shujiai/shujiai-kaserver:2.1.0-xxx --build-arg JAR_FILE=./target/shujiai-ka-start-2.1.0-SNAPSHOT.jar .
if (!$?)
{
    echo 'docker build fail.'
    exit
}
echo "start push images`:"
# docker push registry.cn-hangzhou.aliyuncs.com/shujiai/shujiai-kaserver:2.1.0-xxx
docker save -o target/kaserver210.tar.gz registry.cn-hangzhou.aliyuncs.com/shujiai/shujiai-kaserver:2.1.0-xxx
cd ..\

# 发送消息到飞书群
# $username=git config user.name
# $body="{`"msg_type`":`"text`",`"content`":{`"text`":`"$username deploy esb in $( Get-Date ).`"}}"
# Invoke-WebRequest -Method Post -Body $body -ContentType "application/json;charse=UTF-8" -URI https://open.feishu.cn/open-apis/bot/v2/hook/xxxxxxxxxxxxxxxxxxxxxxxxxxx
