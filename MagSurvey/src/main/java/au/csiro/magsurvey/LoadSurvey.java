package au.csiro.magsurvey;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Environment;
import android.app.Dialog;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.DataInputStream;


/**
 * Created by hil32m on 28/01/14.
 */
public class LoadSurvey {

    private List<SurveyPoint> surveyPoints;
    private File file;

    public LoadSurvey(String fileName){
        this.file = new File(fileName);
    }

    public void loadPoints(){

        surveyPoints = new ArrayList<SurveyPoint>();
        String line;
        try {
        // open the file for reading
            //instream = new FileInputStream(file);

        // if file the available for reading

                // prepare the file for reading
                //FileReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(new FileReader(file));
                // read every line of the file into the line-variable, on line at the time
                while ((line = buffreader.readLine()) != null) {
                    String[] elements = line.split(" ");
                    Integer pointNumber = Integer.parseInt(elements[0]);
                    Double pointLat = Double.parseDouble(elements[1]);
                    Double pointLon = Double.parseDouble(elements[2]);
                    Double totalMag = Double.parseDouble(elements[3]);
                    SurveyPoint currentPoint = new SurveyPoint(pointNumber, pointLat, pointLon, totalMag);
                    surveyPoints.add(currentPoint);
                }
                buffreader.close();
        } catch (IOException e1) {
        } catch (Exception ex) {
            // print stack trace.
        } finally {
        // close the file.
        }
    }

    public List<SurveyPoint>  getSurveyPoints() {
        return surveyPoints;
    }


}