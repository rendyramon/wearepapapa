package com.hotcast.vr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hotcast.vr.R;

/**
 * Created by lostnote on 16/2/24.
 */
public class MyDialog extends Dialog {
    public MyDialog(Context context) {
        super(context);
    }

    public MyDialog(Context context, int theme) {
        super(context, theme);
    }

    public static abstract class Builder {
        private Context context;
        private String title;
        //        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private boolean isFocusable1;
        private boolean isFocusable2;
        private boolean isFocusable3;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;

        //        private
        public Builder(Context context) {
            this.context = context;
        }

        public abstract void setCarity1();

        public abstract void setCarity2();

        public abstract void setCarity3();

        public Builder setIsFocusable1(boolean isFocusable) {
            this.isFocusable1 = isFocusable;
            return this;
        }

        public Builder setIsFocusable2(boolean isFocusable) {
            this.isFocusable2 = isFocusable;
            return this;
        }

        public Builder setIsFocusable3(boolean isFocusable) {
            this.isFocusable3 = isFocusable;
            return this;
        }
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

        public int getCheck() {
            switch (rg.getCheckedRadioButtonId()) {
                case R.id.rb_biao:
                    return 0;
                case R.id.rb_gao:
                    return 1;
                case R.id.rb_chao:
                    return 2;
                default:
                    return 0;
            }

        }

        RadioGroup rg;

        public MyDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final MyDialog dialog = new MyDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.layout_dialog, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            // set the dialog title
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            RadioButton rb_biao = (RadioButton) layout.findViewById(R.id.rb_biao);
            RadioButton rb_gao = (RadioButton) layout.findViewById(R.id.rb_gao);
            RadioButton rb_chao = (RadioButton) layout.findViewById(R.id.rb_chao);
            rg = (RadioGroup) layout.findViewById(R.id.rg);

            if (isFocusable1) {
                rg.check(R.id.rb_biao);
            } else if (isFocusable2) {
                rg.check(R.id.rb_gao);
            } else if (isFocusable3) {
                rg.check(R.id.rb_chao);
            }

            rb_biao.setEnabled(isFocusable1);
            rb_gao.setEnabled(isFocusable2);
            rb_chao.setEnabled(isFocusable3);
            System.out.println("---isFocusable1 = " + isFocusable1 + "-isFocusable2= " + isFocusable2 + "-isFocusable3=" + isFocusable3);

            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    System.out.println("---您的选择改变了");
                    switch (checkedId) {
                        case R.id.rb_biao:
                            setCarity1();
                            System.out.println("---您选择了标清");
                            break;
                        case R.id.rb_gao:
                            setCarity2();
                            System.out.println("---您选择了高清");
                            break;
                        case R.id.rb_chao:
                            setCarity3();
                            System.out.println("---您选择了超清");
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
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.negativeButton).setVisibility(
                        View.GONE);
            }
            dialog.setContentView(layout);
            return dialog;
        }
    }
}
