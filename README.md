# PolygonDistributionViewAndroid多边形属性分布器 

两个文件

PolygonDistributionView.kt

attrs_polygon_distribution_view.xml       //放到res/value文件夹下

2021.12.25更新，view生成时添加了覆盖区域加载动画，从中心扩散到各比例位置

xml中的使用方法

    <com.example.mytestbanner.view.PolygonDistributionView
        android:id="@+id/myView"
        android:layout_width="300dp"
        android:layout_height="300dp"
        android:padding="@dimen/dp_20"
        //我的自定义属性
        app:floor_count="5"
        app:angle_count="3"
        app:line_color="@color/purple_200"
        app:line_size="1dp"
        app:text_size="18sp" />


代码中使用

        List<String> title = new ArrayList<>();
        title.add("大哥");
        title.add("大姐");
        title.add("可怜");
        title.add("一下");
        title.add("我吧");
        title.add("三天");
        title.add("没有");
        title.add("吃饭");
        List<Integer> scaleList = new ArrayList<>();
        scaleList.add(3);
        scaleList.add(5);
        scaleList.add(1);
        scaleList.add(5);
        scaleList.add(2);
        scaleList.add(4);
        scaleList.add(5);
        scaleList.add(4);
        polygonDistributionView.setTitle(title)
                .setFloorCount(5)
                .setScaleList(scaleList)
                .setAngleCount(8)
                .setIsShowConnect(true)
                .setIsShowLine(true);
