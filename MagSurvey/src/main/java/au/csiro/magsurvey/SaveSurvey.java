package au.csiro.magsurvey;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


/**
 * Created by hil32m on 28/01/14.
 */
public class SaveSurvey {
    private Context context;
    private List<SurveyPoint> surveyPoints;
    private Integer pointNum;



    public SaveSurvey(Context context, List<SurveyPoint> surveyPoints, Integer pointNum) {
        this.context = context;
        this.surveyPoints = surveyPoints;
        this.pointNum = pointNum;
    }

    public void save() {


    LayoutInflater li = LayoutInflater.from(context);
    View promptsView = li.inflate(R.layout.textpromptsave, null);

    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
    alertDialogBuilder.setView(promptsView);

    final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInputSave);

    alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {

            String result = userInput.getText().toString();
            String write;
            if (userInput != null) {
                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath() + "/magSurvey");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, result);

               // FileOutputStream f = null;

                try {
                   // f = new FileOutputStream(file);
                    BufferedWriter buffwriter= new BufferedWriter(new FileWriter(file));
                    //PrintWriter pw = new PrintWriter(f);
                    for (Integer i=0; i < pointNum; i++) {
                        SurveyPoint currentPoint = surveyPoints.get(i);
                        write = i.toString() + " " + currentPoint.getpointLat().toString()  + " " + currentPoint.getpointLon().toString() + " "
                         + currentPoint.getTotalMag().toString();
                        //pw.println(i.toString() + " " + currentPoint.getpointLat().toString()  + " " + currentPoint.getpointLon().toString() + " "
                               // + currentPoint.getTotalMag().toString() + "\r\n");
                        buffwriter.write(write);
                        buffwriter.newLine();
                    }
                    buffwriter.flush();
                    buffwriter.close();
                    //pw.flush();
                    //pw.close();
                    //f.flush();
                   // f.close();
                    new SingleMediaScanner(context, file);

                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                } catch (Exception e) {
                } finally {
                   // if (f != null) {
                   //     f = null;
                   // }
                }
            }
            dialog.dismiss();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        }
    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();

        }
    });


    // create alert dialog
    AlertDialog alertDialog = alertDialogBuilder.create();

    // show it
    alertDialog.show();
}
}