# CustomPasswordFrame
自定义的一个填写数字密码，支持框，下划线，背景图<br/>
自定义的一个密码输入框程序。
功能：
　　　1, 可以设置密码的长度
      2, 可以设置输入框的样式，现在支持框，下划线，背景图
      3, 可以修改框与文字的各种属性

　　　 int pwdNumber ;                                //密码的个数
      int mpwdWidth;                                 //每个字被平分框
    　int mpwdMargin;                                //每个框的边距
    　int pwdFrameColorOfBegin = Color.BLACK;        //框内没有数字的颜色
    　int pwdFrameColorOfEnd = Color.GRAY;           //框内有数据的颜色
      int pwdFrameStrokeWidth = 5;                   //框的宽
      int pwdTextColor = Color.RED;                  //数字的颜色
      int pwdTextSize = 50;                          //数字的大小，默认是画出框的一半大小
      int pwdStyleType = 1;                          //边框的样式，默认为全边框1, 2:下划线 3: 背景图
      int pwdBgBitmap = 0;                           //用户传入的背景图id(与上一个属性相对应,只有上属性为3时，才有效果)
