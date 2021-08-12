//package edu.neu.madcourse.topdog;
//
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.os.Bundle;
//
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatDialogFragment;
//
//
//
//public class CustomDialog extends AppCompatDialogFragment {
//
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("Giving Pats!")
//                .setMessage("By clicking on other user's names, you will be assigning pats to their pets.")
//                .setPositiveButton("Yes, I understand!", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//     //   AlertDialog alertDialog = builder.create()
//        return builder.create();
//    }
//}
