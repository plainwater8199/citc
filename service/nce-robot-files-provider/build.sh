#!/bin/bash
base_image=robot-files-service
if [ $# -eq 1 ];
then
   echo "构建版本:"$1
else
   echo "请输入版本号"
   exit
fi
#echo "构建版本:"$base_image":"$1
mvn clean package
docker_image=10.136.30.167:10009/citc-nce/$base_image:$1
echo "docker镜像名称："$docker_image
docker build -t $docker_image .
docker push $docker_image