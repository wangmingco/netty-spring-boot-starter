
cd ../../..
mvn clean install -Dmaven.test.skip=true

cd clients/javafx
# 先利用jlink进行编译
mvn clean javafx:jlink

# 然后用jpackage将上一步编译好的app打包成平台相关的app
# 打包成exe需要将type设置成exe，同时icon需要ico格式的
# 再有就是需要安装 https://github.com/wixtoolset/wix3/releases 和 https://www.microsoft.com/zh-tw/download/confirmation.aspx?id=17718

jpackage --type exe -n nsb -m co.wangming.nsb.javafx/co.wangming.nsb.javafx.NSBApplication --runtime-image ./target/app --temp ./target/temp --dest ./target/dest --icon ./asset/icons8-Emoji-Dragon.png
