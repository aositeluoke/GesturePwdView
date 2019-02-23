## GesturePwdView
##### 1、效果图
![NinePwdView.gif](https://upload-images.jianshu.io/upload_images/3034670-72b00c4ed2fd2aab.gif?imageMogr2/auto-orient/strip)
##### 2、数学知识准备
2.1、三角函数<br/>2.2、两点间的距离计算<br/>2.3、勾股定理<br/>2.4、角度与弧度的换算

##### 3、属性说明
|属性名|默认值|备注|
|:-------:|:-------------:|:-----:|
|nomal_color|#d7d7d7|默认颜色|
|select_color|#8bb8d3|选中颜色|
|error_color|#ff0000|密码错误颜色|
|success_color|#00ff00|密码正确颜色|
|out_ring_width|14|外圆环宽度|
|inner_ring_width|10|内圆环宽度|
|line_width|10|线条宽度|
|min_length|4|最小密码长度|
##### 4、绘制
###### 4.1、绘制圆环
![圆环.png](https://upload-images.jianshu.io/upload_images/3034670-510c9fcd9964b654.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/240)

```
/**
     * 绘制九个圆环
     *
     * @param canvas
     */
    private void drawRing(Canvas canvas) {
        for (CircleItem[] circleItem : circleArray) {
            for (CircleItem item : circleItem) {
                if (mSelectRing.contains(item)) {
                    switch (mCurStatus) {
                        case CHECK:
                            mOutRingPaint.setColor(mSelectColor);
                            mInnerRingPaint.setColor(mSelectColor);
                            break;
                        case ERROR:
                            mOutRingPaint.setColor(mErrorColor);
                            mInnerRingPaint.setColor(mErrorColor);
                            break;
                        case SUCCESS:
                            mOutRingPaint.setColor(mSuccessColor);
                            mInnerRingPaint.setColor(mSuccessColor);
                            break;
                    }
                } else {
                    mOutRingPaint.setColor(mNomalColor);
                    mInnerRingPaint.setColor(mNomalColor);
                }

                canvas.drawCircle(item.centerPoint.x, item.centerPoint.y, mOutRingRadius, mOutRingPaint);
                canvas.drawCircle(item.centerPoint.x, item.centerPoint.y, mInnerRingRadius, mInnerRingPaint);
            }
        }
    }
```
###### 4.2绘制连接线
![连接线.png](https://upload-images.jianshu.io/upload_images/3034670-8f486d8cb235e71f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/240)
```
/**
     * 绘制已连接的线（两圆）
     *
     * @param canvas
     */
    private void drawConnectLine(Canvas canvas) {
        for (int i = 1; i < mSelectRing.size(); i++) {
            PointF startPoint = mSelectRing.get(i - 1).getCenterPoint();
            PointF nextPointF = mSelectRing.get(i).getCenterPoint();

            float distance = getDistance(startPoint, nextPointF);

            float offx1 = mInnerRingRadius / 2f * (nextPointF.x - startPoint.x) / (distance);
            float offy1 = mInnerRingRadius / 2f * (nextPointF.y - startPoint.y) / (distance);
            float startX = startPoint.x + offx1;
            float startY = startPoint.y + offy1;

            float endX = nextPointF.x - offx1;
            float endY = nextPointF.y - offy1;
            canvas.drawLine(startX, startY, endX, endY, mLinePaint);
        }
    }
```
###### 4.3绘制小箭头
&emsp;&emsp;绘制小箭头时，在这里使用的是通过旋转画布后再绘制，小箭头的Path路径并没有改变,需要注意的地方的是起始点x值大于结束点x值时，需要旋转加180度再进行绘制，否则，会绘制出方向相反的小箭头<br/>
![方向相反的小箭头.png](https://upload-images.jianshu.io/upload_images/3034670-dda84832483d0226.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/240)


![小箭头.png](https://upload-images.jianshu.io/upload_images/3034670-ad57fa06371d1bbb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/240)

```
 /**
     * 绘制小箭头
     *
     * @param canvas
     */
    private void drawArrow(Canvas canvas) {
        for (int i = 1; i < mSelectRing.size(); i++) {
            PointF startPointF = mSelectRing.get(i - 1).getCenterPoint();
            PointF endPointF = mSelectRing.get(i).getCenterPoint();
            float tana = (endPointF.y - startPointF.y) / (endPointF.x - startPointF.x);
            float degreeA = (float) Math.atan(tana);
            float B = (float) (Math.toDegrees(degreeA));

            /*终点在起点左侧时，加180*/
            if (endPointF.x < startPointF.x) {
                B += 180;
            }

            canvas.save();
            canvas.translate(endPointF.x, endPointF.y);
            canvas.rotate(B);
            canvas.drawPath(mArrowPath, mArrowPaint);
            canvas.restore();
        }
    }
```
##### 5、总结
&emsp;&emsp;一看到这个需求的时候，脑子一片空白，不知道该怎么下手。在看懂了一些技术文章之后，按照自己的想法从头到尾实现了一遍，并加入了一些自己的计算方法，例如：圆点坐标的计算和小三角的绘制都采用了与众不同的做法。本篇文章到此结束，欢迎朋友们给出意见和想法，让我们共同进步、一起成长！

