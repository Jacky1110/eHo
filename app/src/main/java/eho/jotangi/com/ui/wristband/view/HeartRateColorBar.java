package eho.jotangi.com.ui.wristband.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Locale;

/**
 *  上間隔 2
 *  三角形置頂, 高6, 可設定位置
 *  垂直間隔 2
 *  色條高 20
 *  垂直間隔 2
 *  文字高 16
 *  下間隔 2
 *  色條平均分配View的寬度
 */
public class HeartRateColorBar extends View {

    public enum Type{
        NORMAL,SLEEP
    }

    Type type = Type.NORMAL;

    private int[] barColorNormal = {
            Color.rgb(255,224,97),
            Color.rgb(255,197,102),
            Color.rgb(253,168,103),
            Color.rgb(250,141,105),
            Color.rgb(242,120,109)
    };

    private int[] barColorSleep = {
            Color.rgb(106,80,181),
            Color.rgb(131,89,252),
            Color.rgb(216,208,242)
    };

    private int[] barColor = barColorNormal;

    private String[] barLabel = {
            "熱身放鬆",
            "脂肪燃燒",
            "心肺強化",
            "耐心強化",
            "無氧極限"
    };
    private String[] barLabel2 = {
            "23:00",
            "07:00"
    };


    // 設定相關數值時, levelValues必須比barLabel, barColor多一個, 畫圖時邏輯才不會出錯
    private int[] levelValues = {0, 117,137,156,176, 250};

    private int labelColor = Color.BLACK;
    private int valueColor = Color.WHITE;
    private int triangleColor = Color.rgb(254,104,103);

    private int vw;
    private int vh;
    private int triangleHeight;
    private int triangleWidth;
    private int triangleLeft;
    private int barHeight;
    private int barWidth;
    private float labelSize;
    private int offsetY;

    private int dataValue;

    private Paint.FontMetrics fontMetrics;
    private Path trianglePath;
    private Paint barPaint;
    private Paint textPaint;

    public HeartRateColorBar(Context context) {
        super(context);
        init();
    }

    public HeartRateColorBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeartRateColorBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public HeartRateColorBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        dataValue = levelValues[1];
        barPaint = new Paint();
        barPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setTextSize(labelSize);

        triangleLeft = calculateTriangleLeft();
        trianglePath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        vw = w;
        vh = h;
        offsetY = (h * 2 / 50);
        triangleHeight = h * 6 / 50;
        triangleWidth = h * 12 / 50;
        if(type == Type.NORMAL)
            barHeight = h * 20 / 50;
        else
            barHeight = h * 20 / 50;
        barWidth = w / barColor.length;
        labelSize = h * 16 / 50f;
        updateTrianglePosition();
        textPaint.setTextSize(labelSize);
        fontMetrics = textPaint.getFontMetrics();
    }

    private int calculateTriangleLeft() {

        if (barColor.length < 1) return 0;

        int index = 0;
        while (index < barColor.length) {
            if (dataValue >= levelValues[index] && dataValue < levelValues[index+1]) {
                return index * barWidth + (dataValue - levelValues[index]) * barWidth / (levelValues[index+1]-levelValues[index]);
            }
            index++;
        }
        return 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (vw == 0 || vh == 0) return;
        if (barColor.length == 0) return;

        if(type==Type.NORMAL) {
            // draw triangle
            barPaint.setColor(triangleColor);
            canvas.drawPath(trianglePath, barPaint);
        }


        // draw color bar
        int y = offsetY + triangleHeight + offsetY;
        int x = 0;
        int y2 = y + barHeight;
        if(type==Type.NORMAL) {
            for (int i = 0; i < barColor.length; i++) {
                int x2 = x + barWidth;
                barPaint.setColor(barColor[i]);
                canvas.drawRect(x, y, x2, y2, barPaint);
                x = x2;
            }
        }else{
            for (int i = 0; i < barColor.length; i++) {
                int x2 = x + barWidth;
                barPaint.setColor(barColor[i]);
                canvas.drawRect(x, y, x2, y2, barPaint);
                x = x2;
            }
        }

        if(type==Type.NORMAL) {
            // draw number on color bar
            int y1 = y + offsetY - Math.round(fontMetrics.ascent);
            x = barWidth;
            textPaint.setColor(valueColor);
            for (int i = 1; i < barLabel.length; i++) {
                String v = String.format(Locale.getDefault(), "%d", levelValues[i]);
                int tw = Math.round(textPaint.measureText(v));
                int x1 = x - tw / 2;
                canvas.drawText(v, x1, y1, textPaint);
                x = x + barWidth;
            }
        }

        textPaint.setColor(labelColor);
        y = y2 + offsetY - Math.round(fontMetrics.ascent);
        x = 0;
        if(type==Type.NORMAL) {
            // draw bottom label
            for (int i = 0; i < barLabel.length; i++) {
                int tw = Math.round(textPaint.measureText(barLabel[i]));
                int x1 = x;
                int x2 = x + barWidth;
                if (tw < barWidth) {
                    x1 += (barWidth - tw) / 2;
                }
                canvas.drawText(barLabel[i], x1, y, textPaint);
                x = x2;
            }
        }else{
            labelSize = vh * 12 / 50f;
            textPaint.setTextSize(labelSize);
            canvas.drawText(barLabel2[0], x, y, textPaint);
            canvas.drawText(barLabel2[1], barWidth*(barColor.length)-Math.round(textPaint.measureText(barLabel2[1])), y, textPaint);
        }
    }

    public void setType(Type type){
        this.type = type;
        if(type == Type.SLEEP)
            barColor = barColorSleep;
    }

    public void setDataValue(int v) {
        dataValue = v;
        updateTrianglePosition();
        invalidate();
    }

    private void updateTrianglePosition() {
        triangleLeft = calculateTriangleLeft();
        trianglePath.reset();
        trianglePath.moveTo(triangleLeft, offsetY);
        trianglePath.lineTo(triangleLeft+triangleWidth, offsetY);
        trianglePath.lineTo(triangleLeft+triangleWidth/2, offsetY+triangleHeight);
        trianglePath.lineTo(triangleLeft, offsetY);
        trianglePath.close();
    }

}

