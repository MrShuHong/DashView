# DashView

##效果

![效果图](https://github.com/MrShuHong/DashView/blob/master/images/1694046526.jpg)

````

        <!--圆弧的宽度-->
        <attr name="arc_width" format="dimension" />
        <attr name="arc_color" format="reference|color" />

        <!--小刻度-->
        <attr name="small_scale_width" format="dimension" />
        <attr name="small_scale_height" format="dimension" />
        <attr name="small_scale_color" format="reference|color" />

        <!--大刻度-->
        <attr name="big_scale_width" format="dimension" />
        <attr name="big_scale_height" format="dimension" />
        <attr name="big_scale_color" format="reference|color" />

        <!--刻度值的字体-->
        <attr name="scale_text_size" format="dimension"/>
        <attr name="scale_text_color" format="reference|color"/>

        <!--文字颜色-->
        <!--title颜色-->
        <attr name="dash_title_color" format="reference|color" />
        <attr name="dash_title_size" format="dimension" />
        <!--其他文字的颜色-->
        <attr name="dash_text_color" format="reference|color" />
        <attr name="dash_text_size" format="dimension" />

        <!--正常范围颜色-->
        <attr name="normal_arc_color" format="reference|color" />
        <!--危险颜色-->
        <attr name="danger_arc_color" format="reference|color" />
        <!--报警颜色-->
        <attr name="warning_arc_color" format="reference|color" />
        <!--指针颜色-->
        <attr name="pointer_color" format="reference|color"/>

        <!--内部padding值-->
        <attr name="dash_padding" format="dimension"/>
````

````
DashView dash_view = findViewById(R.id.dash_view);
dash_view.setDatas(0,1);
dash_view.setCurrentValue(0.6f);
dash_view.setTextInfo("出水瞬时流量","国标：1-60","09-09 12:33:20");
dash_view.startRender();
````
