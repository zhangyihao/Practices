# Practices
Some practice when study android

###DragAndDraw
练习在屏幕上绘制矩形

###MPAndroidChartDemo
一个绘制图表的Android开源库，github地址：[https://github.com/PhilJay/MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)

说明：
如果Android项目的minSdkVersion在14以下，需要添加额外的兼容低版本Library或jar。
Libray地址：[NineOldAndroids](https://github.com/JakeWharton/NineOldAndroids)
jar包地址：[nineoldandroids-2.4.0.jar](https://github.com/JakeWharton/NineOldAndroids/downloads)

该库支持的图表类型有：线性图、饼图、柱状图等，具体请参考该项目github介绍部分。

###TestGalleryImageShow
使用HorizonScrollView代替Gallery展示图片，图片可以进行左右滑动，并且长按图片可以进行删除操作。

bugs：

1. 长按图片时，图片右上角显示删除图标，点击删除图标可进行删除，但是，点击图片外的区域（图片失去焦点）时，删除图标不能隐藏。

###RunTracker

利用GPS进行定位的示例。

###PickPicFromSystemGallery

模仿微信的图片选择器。

其中,Sample1Activity示例为：调用系统图库，选择一张图片，然后展示。Sample2Activity示例为：

