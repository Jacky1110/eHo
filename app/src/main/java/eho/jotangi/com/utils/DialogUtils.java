package eho.jotangi.com.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import eho.jotangi.com.R;

public class DialogUtils {
    public static AlertDialog appDialog;

    public interface OneButtonClickListener {
        void onButton1Clicked();
    }

    public interface TwoButtonClickListener {
        void onButton1Clicked();
        void onButton2Clicked();
    }

    public interface TwoButtonAndCancelClickListener {
        void onButton1Clicked();
        void onButton2Clicked();
        void onCancel();
    }

    public static View createDialogView(int layoutid, ViewGroup viewGroup, String title, String message, String bnlabel,OneButtonClickListener listener) {
        View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(layoutid, viewGroup, false);

        TextView bnText = dialogView.findViewById(R.id.tv_bn1_label);
        bnText.setText(bnlabel);

        TextView txtTitle = dialogView.findViewById(R.id.tv_title);
        txtTitle.setText(title);

        TextView txtContextL = dialogView.findViewById(R.id.tv_context);
        txtContextL.setText(message);

        ConstraintLayout bnLayout = dialogView.findViewById(R.id.bn1);
        bnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButton1Clicked();
                closeDialog();
            }
        });
        return dialogView;
    }

    public static View create2ButtonAndCancelDialogView(
            int layoutid,
            ViewGroup viewGroup,
            String title,
            String message,
            String bn1label,
            String bn2label,
           TwoButtonAndCancelClickListener listener
    ) {
        View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(layoutid, viewGroup, false);

        TextView tvTitle = dialogView.findViewById(R.id.tv_title);
        tvTitle.setText(title);

        TextView tvContext = dialogView.findViewById(R.id.tv_context);
        tvContext.setText(message);

        TextView bn1Text = dialogView.findViewById(R.id.tv_bn1_label);
        bn1Text.setText(bn1label);

        ConstraintLayout bn1Layout = dialogView.findViewById(R.id.bn1);
        bn1Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButton1Clicked();
                closeDialog();
            }
        });

        TextView bn2Text = dialogView.findViewById(R.id.tv_bn2_label);
        bn2Text.setText(bn2label);

        ConstraintLayout bn2Layout = dialogView.findViewById(R.id.bn2);
        bn2Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButton2Clicked();
                closeDialog();
            }
        });

        ConstraintLayout bnCancelLayout = dialogView.findViewById(R.id.bncancel);
        bnCancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancel();
                closeDialog();
            }
        });

        return dialogView;
    }

    public static void showMyDialog(ViewGroup viewGroup, String title, String message, String btnlabel, OneButtonClickListener listener) {
        View dialogView = createDialogView(R.layout.dialog_one_button, viewGroup, title, message, btnlabel, listener);
        buildDialog(dialogView, viewGroup.getContext());
        appDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
    }

    public static void showMyDialog2(ViewGroup viewGroup, String title, String message, String btnlabel, OneButtonClickListener listener) {
        View dialogView = createDialogView(R.layout.dialog_one_button2, viewGroup, title, message, btnlabel, listener);
        buildDialog(dialogView, viewGroup.getContext());
        appDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
    }

    public static void showMyDialog(ViewGroup viewGroup, int titleid, int messageid, int btnlabelid, OneButtonClickListener listener) {
        Context context = viewGroup.getContext();
        String title = (titleid > 0) ? context.getString(titleid) : "";
        String message = (messageid > 0) ? context.getString(messageid) : "";
        String btnlabel = (btnlabelid > 0) ? context.getString(btnlabelid) : context.getString(R.string.button_confirm);
        showMyDialog(viewGroup, title, message, btnlabel, listener);
    }

    public static void showMyDialog(ViewGroup viewGroup, int titleid, int messageid, String parm1, int btnlabelid, OneButtonClickListener listener) {
        Context context = viewGroup.getContext();
        String title = (titleid > 0) ? context.getString(titleid) : "";
        String message = (messageid > 0) ? context.getString(messageid, parm1) : "";
        String btnlabel = (btnlabelid > 0) ? context.getString(btnlabelid) : context.getString(R.string.button_confirm);
        showMyDialog(viewGroup, title, message, btnlabel, listener);
    }

    public static void showMyDialog2(ViewGroup viewGroup, int titleid, int messageid, String parm1, int btnlabelid, OneButtonClickListener listener) {
        Context context = viewGroup.getContext();
        String title = (titleid > 0) ? context.getString(titleid) : "";
        String message = (messageid > 0) ? context.getString(messageid, parm1) : "";
        String btnlabel = (btnlabelid > 0) ? context.getString(btnlabelid) : context.getString(R.string.button_confirm);
        showMyDialog2(viewGroup, title, message, btnlabel, listener);
    }

    public static void show2ButtonAndCancelDialog(
            ViewGroup viewGroup,
            String title,
            String message,
            String bn1label,
            String bn2label,
            TwoButtonAndCancelClickListener listener
    ) {
        View dialogView = create2ButtonAndCancelDialogView(
                com.jotangi.baseutils.R.layout.dialog_two_button_with_cancel_vertical,
                viewGroup,
                title,
                message,
                bn1label,
                bn2label,
                listener);
        buildDialog(dialogView, viewGroup.getContext());
        appDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
    }

    public static void show2ButtonAndCancelDialog(
            ViewGroup viewGroup,
            int titleid,
            int messageid,
            int bn1labelid,
            int bn2labelid,
          TwoButtonAndCancelClickListener listener) {
        Context context = viewGroup.getContext();
        String title = (titleid > 0) ? context.getString(titleid) : "";
        String message = (messageid > 0) ? context.getString(messageid) : "";
        String bn1label = (bn1labelid > 0) ? context.getString(bn1labelid) : context.getString(com.jotangi.baseutils.R.string.button_confirm);
        String bn2label = (bn2labelid > 0) ? context.getString(bn2labelid) : context.getString(R.string.button_confirm);
        show2ButtonAndCancelDialog(viewGroup, title, message, bn1label, bn2label, listener);
    }

    public static void buildDialog(View dialogView, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setView(dialogView);
        builder.setCancelable(true);
        appDialog = builder.create();
        appDialog.setCanceledOnTouchOutside(false);
        //appDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        appDialog.show();
    }

    public static void closeDialog() {
        if (appDialog != null) {
            appDialog.dismiss();
            appDialog = null;
        }
    }
}
