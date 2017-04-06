echo off

D:
SET PROJECT_HOME=%cd%
ECHO Project HOME:"%PROJECT_HOME%"



:mvn_command
cd %PROJECT_HOME%
ECHO.
ECHO.注意：现在的编译和部署步骤分开，需要先选择编译打包，成功后再选择启动jetty
ECHO.
ECHO 1-生成eclipse工程文件
ECHO.
ECHO 2-编译打包
ECHO.
ECHO 0-退出菜单

set /p isopt=【选择命令】
if /i "%isopt%"=="1" goto mvn_eclipse_again
if /i "%isopt%"=="2" goto mvn_full_package

echo "无效选项，请选择(0-9)"
goto mvn_command

:mvn_eclipse
	ECHO.
	ECHO.
	ECHO.
	ECHO 1-第一次生成eclipse工程文件
	ECHO.
	ECHO 2-重新生成eclipse工程文件
	ECHO.
	ECHO 0-返回
	set /p eopt=【选择命令】
	if /i "%eopt%"=="1" goto mvn_eclipse_first
	if /i "%eopt%"=="2" goto mvn_eclipse_again
	goto mvn_command

:mvn_eclipse_first
	cd %PROJECT_HOME%\all
	echo 生成eclipse工程文件
	start /HIGH mvn install eclipse:eclipse -Dmaven.test.skip -Denforcer.skip -Denv=release
	goto mvn_command

:mvn_eclipse_again
	cd %PROJECT_HOME%\all
	echo 生成eclipse工程文件
	start /HIGH mvn eclipse:clean eclipse:eclipse
	goto mvn_command

	
:mvn_full_package
	cd %PROJECT_HOME%\all
	start /HIGH mvn clean install -Dmaven.test.skip  -DuserProp=%ANTX_PROPERTY%
	goto mvn_command
	
:mvn_end
cd %PROJECT_HOME%
