package com.hotcast.vr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hotcast.vr.R;

/**
 * Created by lostnote on 16/2/24.
 */
public class GlassesDialog extends Dialog {
    public GlassesDialog(Context context) {
        super(context);
    }

    public GlassesDialog(Context context, int theme) {
        super(context, theme);
    }

    public static abstract class Builder {
        private Context context;
        private String title;
        //        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;

        //        private
        public Builder(Context context) {
            this.context = context;
        }

        public abstract void YouCanDo();

        public void refresh(int id) {
            Drawable choose = context.getResources().getDrawable(R.drawable.select_glasses_choose);
            Drawable nomal = context.getResources().getDrawable(R.drawable.select_glasses_nomal);
            choose.setBounds(0, 0, choose.getMinimumWidth(), choose.getMinimumHeight());
            nomal.setBounds(0, 0, nomal.getMinimumWidth(), nomal.getMinimumHeight());
            switch (id) {
                case R.id.bt_dlodlo:
                    bt_dlodlo.setCompoundDrawables(null, null, choose, null);
                    bt_google.setCompoundDrawables(null, null, nomal, null);
                    bt_xiaozha.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen2.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen3.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen4.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen5.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen6.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen7.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen8.setCompoundDrawables(null, null, nomal, null);
                    break;
                case R.id.bt_google:
                    System.out.println("---2222");
                    bt_dlodlo.setCompoundDrawables(null, null, nomal, null);
                    bt_google.setCompoundDrawables(null, null, choose, null);
                    bt_xiaozha.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen2.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen3.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen4.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen5.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen6.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen7.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen8.setCompoundDrawables(null, null, nomal, null);
                    break;
                case R.id.bt_xiaozha:
                    System.out.println("---3333");
                    bt_dlodlo.setCompoundDrawables(null, null, nomal, null);
                    bt_google.setCompoundDrawables(null, null, nomal, null);
                    bt_xiaozha.setCompoundDrawables(null, null, choose, null);
                    bt_baofen.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen2.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen3.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen4.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen5.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen6.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen7.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen8.setCompoundDrawables(null, null, nomal, null);
                    break;
                case R.id.bt_baofen1:
                    System.out.println("---4444");
                    bt_dlodlo.setCompoundDrawables(null, null, nomal, null);
                    bt_google.setCompoundDrawables(null, null, nomal, null);
                    bt_xiaozha.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen.setCompoundDrawables(null, null, choose, null);
                    bt_baofen2.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen3.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen4.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen5.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen6.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen7.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen8.setCompoundDrawables(null, null, nomal, null);
                    break;
                case R.id.bt_baofen3:
                    System.out.println("---4444");
                    bt_dlodlo.setCompoundDrawables(null, null, nomal, null);
                    bt_google.setCompoundDrawables(null, null, nomal, null);
                    bt_xiaozha.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen2.setCompoundDrawables(null, null, choose, null);
                    bt_baofen.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen3.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen4.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen5.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen6.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen7.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen8.setCompoundDrawables(null, null, nomal, null);

                    break;
                case R.id.bt_baofen4:
                    System.out.println("---4444");
                    bt_dlodlo.setCompoundDrawables(null, null, nomal, null);
                    bt_google.setCompoundDrawables(null, null, nomal, null);
                    bt_xiaozha.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen3.setCompoundDrawables(null, null, choose, null);
                    bt_baofen2.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen4.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen5.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen6.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen7.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen8.setCompoundDrawables(null, null, nomal, null);
                    break;
                case R.id.bt_baofen5:
                    System.out.println("---4444");
                    bt_dlodlo.setCompoundDrawables(null, null, nomal, null);
                    bt_google.setCompoundDrawables(null, null, nomal, null);
                    bt_xiaozha.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen4.setCompoundDrawables(null, null, choose, null);
                    bt_baofen2.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen3.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen5.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen6.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen7.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen8.setCompoundDrawables(null, null, nomal, null);
                    break;
                case R.id.bt_baofen6:
                    System.out.println("---4444");
                    bt_dlodlo.setCompoundDrawables(null, null, nomal, null);
                    bt_google.setCompoundDrawables(null, null, nomal, null);
                    bt_xiaozha.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen5.setCompoundDrawables(null, null, choose, null);
                    bt_baofen2.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen3.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen4.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen6.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen7.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen8.setCompoundDrawables(null, null, nomal, null);
                    break;
                case R.id.bt_baofen7:
                    System.out.println("---4444");
                    bt_dlodlo.setCompoundDrawables(null, null, nomal, null);
                    bt_google.setCompoundDrawables(null, null, nomal, null);
                    bt_xiaozha.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen6.setCompoundDrawables(null, null, choose, null);
                    bt_baofen2.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen3.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen4.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen5.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen7.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen8.setCompoundDrawables(null, null, nomal, null);
                    break;
                case R.id.bt_baofen8:
                    System.out.println("---4444");
                    bt_dlodlo.setCompoundDrawables(null, null, nomal, null);
                    bt_google.setCompoundDrawables(null, null, nomal, null);
                    bt_xiaozha.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen7.setCompoundDrawables(null, null, choose, null);
                    bt_baofen2.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen3.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen4.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen5.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen6.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen8.setCompoundDrawables(null, null, nomal, null);
                    break;
                case R.id.bt_baofen9:
                    System.out.println("---4444");
                    bt_dlodlo.setCompoundDrawables(null, null, nomal, null);
                    bt_google.setCompoundDrawables(null, null, nomal, null);
                    bt_xiaozha.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen8.setCompoundDrawables(null, null, choose, null);
                    bt_baofen2.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen3.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen4.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen5.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen6.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen7.setCompoundDrawables(null, null, nomal, null);
                    bt_baofen.setCompoundDrawables(null, null, nomal, null);
                    break;
                default:
                    break;
            }

        }

        ;

        /**
         * Set the Dialog message from resource
         *
         * @param
         * @return
         */
//        public Builder setColor(int color) {
//            this.color = (int) context.getText(color);
//            return this;
//        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public int getChooseItem() {
            switch (rg.getCheckedRadioButtonId()) {
                case R.id.bt_dlodlo:
                    return 1;
                case R.id.bt_google:
                    return 2;
                case R.id.bt_xiaozha:
                    return 3;
                case R.id.bt_baofen1:
                    return 4;
                case R.id.bt_baofen3:
                    return 5;
                case R.id.bt_baofen4:
                    return 6;
                case R.id.bt_baofen5:
                    return 7;
                case R.id.bt_baofen6:
                    return 8;
                case R.id.bt_baofen7:
                    return 9;
                case R.id.bt_baofen8:
                    return 10;
                case R.id.bt_baofen9:
                    return 11;
                default:
                    return -1;
            }
        }

        public void setChooseItem(int i) {
            switch (i) {
                case 1:
                    rg.check(R.id.bt_dlodlo);
                    break;
                case 2:
                    rg.check(R.id.bt_google);
                    break;
                case 3:
                    rg.check(R.id.bt_xiaozha);
                    break;
                case 4:
                    rg.check(R.id.bt_baofen1);
                    break;
                case 5:
                    rg.check(R.id.bt_baofen3);
                    break;
                case 6:
                    rg.check(R.id.bt_baofen4);
                    break;
                case 7:
                    rg.check(R.id.bt_baofen5);
                    break;
                case 8:
                    rg.check(R.id.bt_baofen6);
                    break;
                case 9:
                    rg.check(R.id.bt_baofen7);
                    break;
                case 10:
                    rg.check(R.id.bt_baofen8);
                    break;
                case 11:
                    rg.check(R.id.bt_baofen9);
                    break;

            }
        }

        RadioButton bt_dlodlo;
        RadioButton bt_google;
        RadioButton bt_xiaozha;
        RadioButton bt_baofen;
        RadioButton bt_baofen2;
        RadioButton bt_baofen3;
        RadioButton bt_baofen4;
        RadioButton bt_baofen5;
        RadioButton bt_baofen6;
        RadioButton bt_baofen7;
        RadioButton bt_baofen8;
        RadioGroup rg;
        Window window;

        public GlassesDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final GlassesDialog dialog = new GlassesDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.selectglass_window, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            // set the dialog title
            bt_dlodlo = (RadioButton) layout.findViewById(R.id.bt_dlodlo);
            bt_google = (RadioButton) layout.findViewById(R.id.bt_google);
            bt_xiaozha = (RadioButton) layout.findViewById(R.id.bt_xiaozha);
            bt_baofen = (RadioButton) layout.findViewById(R.id.bt_baofen1);
            bt_baofen2 = (RadioButton) layout.findViewById(R.id.bt_baofen3);
            bt_baofen3 = (RadioButton) layout.findViewById(R.id.bt_baofen4);
            bt_baofen4 = (RadioButton) layout.findViewById(R.id.bt_baofen5);
            bt_baofen5 = (RadioButton) layout.findViewById(R.id.bt_baofen6);
            bt_baofen6 = (RadioButton) layout.findViewById(R.id.bt_baofen7);
            bt_baofen7 = (RadioButton) layout.findViewById(R.id.bt_baofen8);
            bt_baofen8 = (RadioButton) layout.findViewById(R.id.bt_baofen9);


            rg = (RadioGroup) layout.findViewById(R.id.rg);
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    refresh(rg.getCheckedRadioButtonId());
                    switch (checkedId) {
                        case R.id.bt_dlodlo:
                            YouCanDo();
                            break;
                        case R.id.bt_google:
                            YouCanDo();
                            break;
                        case R.id.bt_xiaozha:
                            YouCanDo();
                            break;
                    }
                }
            });
            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveButton))
                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.positiveButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {

                                    positiveButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);
            }
            // set the cancel button
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeButton))
                        .setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {

                                    negativeButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else {
                layout.findViewById(R.id.negativeButton).setVisibility(
                        View.GONE);
            }
            dialog.setContentView(layout);
            return dialog;
        }
    }
}
