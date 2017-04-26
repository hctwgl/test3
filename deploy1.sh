#!/bin/bash
#git checkout new_website_dev_20160430
#git pull
#mvn clean package -Dmaven.test.skip=true

APP_DIR=/home/aladin/project/WAR/app
CORE_DIR=$(cd `dirname $0`; pwd)
GIT_BRANCH=$2
arge=$1
function deploy()
{
timekey=`date +"%Y%m%d%H%m%S"`
echo $GIT_BRANCH


git checkout $GIT_BRANCH
[ $? -ne 0 ] && echo -e '\033[31m[ error ] Failed to checkout the branch\033[0m' && exit 1
git pull
[ $? -ne 0 ] && echo -e '\033[31m[ error ] Failed to pull the branch\033[0m' && exit 1

mvn clean install -Dmaven.test.skip=true 
[ $? -ne 0 ] && echo -e '\033[31m[ error ] Failed to maven the branch\033[0m' && exit 1


ssh -p 10037 aladin@121.196.197.5 "[ -f $APP_DIR/ROOT.war.bak ] && rm -f $APP_DIR/ROOT.war.bak"
ssh -p 10037 aladin@121.196.197.5 "[ -f $APP_DIR/ROOT.war ] && mv $APP_DIR/ROOT.{war,war.bak}"
sleep 2
scp -P 10037 $CORE_DIR/51fbapi-web/target/ROOT.war aladin@121.196.197.5:$APP_DIR/
[ $? -ne 0 ] && echo -e '\033[31m[ error ] Failed to scp the ROOT.war\033[0m' && exit 1
ssh -p 10037 aladin@121.196.197.5 "echo '调用部署' && $APP_DIR/invoke1.sh $arge"
}

function rollback()
{
ssh -p 10037 aladin@121.196.197.5 " echo '调用回滚' && $APP_DIR/invoke1.sh $arge"
}

case $1 in
 deploy)
        deploy
        ;;
 rollback)
        rollback
        ;;
 *)
        echo $"Usage: $0 {deploy|rollback}"  
        exit 1
        ;;
esac
