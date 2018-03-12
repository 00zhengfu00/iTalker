
#### 项目概览   

**项目整体以Material Desigh和ViewPager与Fragment结合。使用第三方推送平台为通讯基础。采用MVP架构。结构分类为主APP(Activity,Fragment)，表情library，语言library，网络library。主要功能涉及登录注册，搜索群与人，，群聊单聊(语音，图片，文件，表情)，好友添加，群组创建，个人信息等IM应用核心功能。并在后台使用Jersey RESTful + Hibernate + MySQL + Tomcat结构实现服务端架构。是一款包含主流IM功能的一款应用，同时结构封装性好。结构层次清晰。注释详细。便于扩展与移植到自己项目作为已有应用的新模块**。

#### 注意 语音模拟器无法发送 应用清晰度与流畅程度与电脑内存有关，与应用无关。

##### 运行时权限与用户注册   
![运行时权限和注册信息.gif](http://upload-images.jianshu.io/upload_images/3983615-9aee7376e1290a70.gif?imageMogr2/auto-orient/strip)

##### 主页面与好友添加      
![主页面与好友添加.gif](http://upload-images.jianshu.io/upload_images/3983615-dee6768100b92d80.gif?imageMogr2/auto-orient/strip)


##### 群组聊天(文字，图片，动态表情，语音)
![群聊.gif](http://upload-images.jianshu.io/upload_images/3983615-c84f2d4ebea89667.gif?imageMogr2/auto-orient/strip)

##### 单人聊天(文字，图片，动态表情，语音)
![聊天.gif](http://upload-images.jianshu.io/upload_images/3983615-097cea4d8e8f5e97.gif?imageMogr2/auto-orient/strip)   

---


#### 更多细节大家可以clone下来自己运行试试。后期功能完善后会上传apk文件，尽请期待。

---

##### 服务器数据库关系图
![图片.png](http://upload-images.jianshu.io/upload_images/3983615-1911fa2f47527d83.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 客户端消息处理关系图
![clipboard.png](http://upload-images.jianshu.io/upload_images/3983615-c828b8e17f651920.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 功能细节图   
![微聊.png](https://upload-images.jianshu.io/upload_images/3983615-6d2af14629c4f382.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 核心技术   
##### 第三方SDK选取
推送服务 选择个推作为通讯基础

对象存储 阿里OSS
实现对发送图片、语音、头像等文件的存储。方便以后读取。

###### 客户端   
网络框架-Retrofit

注解框架-Butterknife

图片框架-Glide

安卓数据库框架-Dbflow

数据存储平台-OSS

###### 服务器  
Jersey-轻量WebService框架

Hibernate-Java数据库操作框架

MySQL-数据库

Gson-数据解析框架

Tomcat-服务器

作为个人了手项目。项目是其他项目灵感所创，在此基础上进行优化扩展，同时后期会实现扩展功能。使其具备一个更接近主流IM应用。项目中的难点与扩展点都会后续在我的博客中写出。同时关于项目中的第三方SDK请大家自己申请，因为毕竟免费容量有限，如果大家喜欢，后期我会上传服务端代码到云服务器中。同时，**地图，视频播放组件，分享，地图，天气，运动，多渠道打包发布，广告接入，友盟统计**等在我**github中其他小项目**中都以实现，想自己扩展项目可以将我其他项目的代码直接拷贝到这个项目中就可以使用。**由于是练手项目难免会有一些小问题，但是整体上比较完善。后期有进行优化**。

#### 最后附上我的博客，项目中的难点与扩展点都会后续在我的博客中写出

### [简书](https://www.jianshu.com/u/70a8f4edb323)

### [CSDN](http://my.csdn.net/gg199402)
