# 通讯录索引菜单控件

> 仿通讯录索引菜单

- 软件截图

![通讯录索引菜单][1]

 -  使用帮助
 1. 集成索引菜单控件
 
1.1 下载项目工程，将app同级目录下的indexview 文件夹拷贝到你的项目工程下。

1.2 为项目添加依赖，File -> New -> Import Module,选中刚才拷贝的IndexView文件夹即可，如果编译出错，请参照你的项目修改IndexView的build.gradle文件

2.在项目中使用索引菜单控件
2.1 在布局文件中定义索引菜单控件

  ```java
<com.enjoywatch.library.IndexView xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/indexView"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:layout_alignParentRight="true"
        android:paddingBottom="6dp"
        android:paddingTop="6dp"
        app:defaultTextColor="#565656"
        app:index="#ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        app:indexBackground="@drawable/index_view_normal"
        app:indexTouchBackground="@drawable/index_view_touch"
        app:popupBackground="@drawable/overlay_bg"
        app:popupTextColor="#ffffff"
        app:touchTextColor="#565656" />
```
2.1.1 所有可定义属性

| 属性                  | 含义                 | 是否必填             |
| --------------------- | -------------------- | -------------------- |
| index                 | 索引文字             | 必填                 |
| indexBackground       | 索引菜单控件默认背景 | 选填                 |
| indexTouchBackground  | 索引菜单按下背景     | 选填                 |
| defaultTextColor      | 索引菜单默认文字颜色 | 选填                 |
| touchTextColor        | 索引菜单选中文字颜色 | 选填                 |
| popupBackground       | 悬浮窗背景           | 选填                 |
| popupTextColor        | 悬浮窗文字颜色       | 选填                 |
| popupTextSize         | 悬浮窗文字大小       | 选填                 |
| showPopup             | 是否显示悬浮窗       | 选填，默认为true     |
| delayTime             | 悬浮窗延迟消失时间   | 选填，默认为1000ms   |
| defaultSelectPosition | 默认选中菜单项       | 选填，默认选中第一项 |

***

2.2 在Activity里面初始化控件并添加事件监听事件

```java
public class MainActivity extends AppCompatActivity implements IndexView.OnIndexChangeListener {
    private IndexView mIndexView;
    private TextView mTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTv = (TextView) findViewById(R.id.tv);

        mIndexView = (IndexView) findViewById(R.id.indexView);
        //设置选中第一个
        mIndexView.setSelectIndex(1);
        //设置索引切换监听，Activity实现OnIndexChangeListener接口
        mIndexView.setOnIndexChangeListener(this);
        // 设置不显示悬浮窗
        // mIndexView.setShowPopup(false);
    }

    @Override
    public void OnIndexChange(String index, int postion) {
        mTv.setText("当前选中:" + index.charAt(postion));
    }
}
```
 - 关于我


[我的网址][2]
   
  [1]: https://github.com/CB2Git/ImageBed/blob/master/IndexView/indexview.png?raw=true
   [9]: http://www.27house.cn