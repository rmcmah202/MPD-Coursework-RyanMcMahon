
/*  Starter project for Mobile Platform Development in Semester B Session 2018/2019
    You should use this project as the starting point for your assignment.
    This project simply reads the data from the required URL and displays the
    raw data in a TextField
*/

//
// Name                 Ryan_McMahon_____
// Student ID           S1520752_________
// Programme of Study   Software Development (Video Games)
//

// Update the package name to include your Student Identifier
package coursework.gcu.me.org.mpdcoursework;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private ListView ListDisplay;
    private Button startButton;
    private Button timeButton;
    private Button backButton;
    private Button marchButton;
    private Button februaryButton;
    boolean marchSorted = false;
    boolean febSorted = false;
    
    private ArrayList<String> textToDisplay = new ArrayList<>();
    private ArrayList<String> links = new ArrayList<>();
    private ArrayList<Integer> days = new ArrayList<>();
    private ArrayList<Integer> months = new ArrayList<>();
    private ArrayList<Float> magnitudes = new ArrayList<>();
    private ArrayList<Float> lats = new ArrayList<>();
    private ArrayList<Float> lons = new ArrayList<>();
    private ArrayList<Integer> depths = new ArrayList<>();
    private ArrayList<String> location = new ArrayList<>();

    Integer currentDataset = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the raw links to the graphical components
        ListDisplay = (ListView) findViewById(R.id.listOfEarthquakes);
        startButton = (Button) findViewById(R.id.refreshButton);
        startButton.setOnClickListener(this);
        timeButton = (Button) findViewById(R.id.monthSelectButton);
        timeButton.setOnClickListener(this);
        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
        backButton.setEnabled(false);
        backButton.setVisibility(View.INVISIBLE);
        februaryButton = (Button) findViewById(R.id.februarySelect);
        februaryButton.setOnClickListener(this);
        februaryButton.setEnabled(false);
        februaryButton.setVisibility(View.INVISIBLE);
        marchButton = (Button) findViewById(R.id.marchSelect);
        marchButton.setOnClickListener(this);
        marchButton.setEnabled(false);
        marchButton.setVisibility(View.INVISIBLE);
        ListDisplay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri = Uri.parse(links.get(position));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        new ProcessInBackground().execute();
    }

    public void onClick(View aview) {

        switch (aview.getId()){

            case R.id.refreshButton:
                new ProcessInBackground().execute();
                break;

            case R.id.monthSelectButton:
                startButton.setEnabled(false);
                startButton.setVisibility(View.INVISIBLE);
                timeButton.setEnabled(false);
                timeButton.setVisibility(View.INVISIBLE);
                backButton.setEnabled(true);
                backButton.setVisibility(View.VISIBLE);
                marchButton.setEnabled(true);
                marchButton.setVisibility(View.VISIBLE);
                februaryButton.setEnabled(true);
                februaryButton.setVisibility(View.VISIBLE);
                break;

            case R.id.backButton:
                startButton.setEnabled(true);
                startButton.setVisibility(View.VISIBLE);
                timeButton.setEnabled(true);
                timeButton.setVisibility(View.VISIBLE);
                backButton.setEnabled(false);
                backButton.setVisibility(View.INVISIBLE);
                marchButton.setText("March");
                marchButton.setEnabled(false);
                marchButton.setVisibility(View.INVISIBLE);
                februaryButton.setText("February");
                februaryButton.setEnabled(false);
                februaryButton.setVisibility(View.INVISIBLE);

                marchSorted = false;
                febSorted = false;

                new ProcessInBackground().execute();
                break;

            case R.id.marchSelect:
                if (!marchSorted) {
                    marchSorted = true;
                    februaryButton.setEnabled(false);
                    februaryButton.setVisibility(View.INVISIBLE);
                    SortMarch();
                    marchButton.setText("Reset data");
                } else if (marchSorted){
                    marchSorted = false;
                    marchButton.setText("March");
                    februaryButton.setEnabled(true);
                    februaryButton.setVisibility(View.VISIBLE);
                    new ProcessInBackground().execute();
                }
                break;

            case R.id.februarySelect:
                if (!febSorted) {
                    febSorted = true;
                    marchButton.setEnabled(false);
                    marchButton.setVisibility(View.INVISIBLE);
                    SortFebruary();
                    februaryButton.setText("Reset data");
                } else if (febSorted) {
                    febSorted = false;
                    februaryButton.setText("February");
                    marchButton.setEnabled(true);
                    marchButton.setVisibility(View.VISIBLE);
                    new ProcessInBackground().execute();
                }
                break;

            default:
                break;}
    }



    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    public class ProcessInBackground extends AsyncTask<Integer, Void, Exception> {

        Exception exception = null;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            textToDisplay = new ArrayList<>();
            links = new ArrayList<>();
            lons = new ArrayList<>();
            lats = new ArrayList<>();
            days = new ArrayList<>();
            months = new ArrayList<>();
            magnitudes = new ArrayList<>();
            depths = new ArrayList<>();
            location = new ArrayList<>();
            currentDataset = -1;
        }

        @Override
        protected Exception doInBackground(Integer... params) {
            try {
                URL url = new URL("http://quakes.bgs.ac.uk/feeds/MhSeismology.xml");

                XmlPullParserFactory blueprint = XmlPullParserFactory.newInstance();

                blueprint.setNamespaceAware(false);

                XmlPullParser pullParser = blueprint.newPullParser();

                pullParser.setInput(getInputStream(url), "UTF_8");

                boolean activeItem = false;

                int eventType = pullParser.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (pullParser.getName().equalsIgnoreCase("item")) {
                            activeItem = true;
                        } else if (pullParser.getName().equalsIgnoreCase("title")) {
                            if (activeItem) {
                                currentDataset++;
                            }
                        }
                        else if (pullParser.getName().equalsIgnoreCase("description")) {
                            if (activeItem) {
                                String input = pullParser.nextText();
                                ParseInformationFromDescription(input);
                            }
                        }
                        else if (pullParser.getName().equalsIgnoreCase("link")) {
                            if (activeItem) {
                                links.add((pullParser.nextText()));
                            }
                        }
                        else if (pullParser.getName().equalsIgnoreCase("pubDate")) {
                            if (activeItem) {
                                ParseInformationFromDate(pullParser.nextText());
                                CreateTitleToDisplay(currentDataset);
                            }
                        }
                        else if (pullParser.getName().equalsIgnoreCase("geo:lat")) {
                            if (activeItem) {
                                lats.add(Float.parseFloat(pullParser.nextText()));
                            }
                        }
                        else if (pullParser.getName().equalsIgnoreCase("geo:long")) {
                            if (activeItem) {
                                lons.add(Float.parseFloat(pullParser.nextText()));
                            }
                        }
                    } else if (eventType == XmlPullParser.END_TAG && pullParser.getName().equalsIgnoreCase("item")) {
                        activeItem = false;
                    }
                    eventType = pullParser.next();
                }
            } catch (MalformedURLException e) {
                exception = e;
            } catch (XmlPullParserException e) {
                exception = e;
            } catch (IOException ae) {
                exception = ae;
            }
            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, textToDisplay);
            ListDisplay.setAdapter(adapter);
        }
    }

    public void ParseInformationFromDescription(String info){
        String[] parts = info.split(" Depth: ");
        String[] significantParts = parts[1].split(" km ; Magnitude: ");

        depths.add(Integer.parseInt(significantParts[0]));
        magnitudes.add(Float.parseFloat(significantParts[1]));

        String[] getLocation = info.split("[:;]");
        location.add(getLocation[5]);
    }

    public void ParseInformationFromDate (String info){
        String[] parts = info.split(" ");

        days.add((Integer.parseInt(parts[1])));

        switch (parts[2]){
            case "Jan":
                months.add(1);
                break;
            case "Feb":
                months.add(2);
                break;
            case "Mar":
                months.add(3);
                break;
            case "Apr":
                months.add(4);
                break;
            case "May":
                months.add(5);
                break;
            case "Jun":
                months.add(6);
                break;
            case "Jul":
                months.add(7);
                break;
            case "Aug":
                months.add(8);
                break;
            case "Sep":
                months.add(9);
                break;
            case "Oct":
                months.add(10);
                break;
            case "Nov":
                months.add(11);
                break;
            case "Dec":
                months.add(12);
                break;
            default:
                break;
        }
    }

    void CreateTitleToDisplay(Integer dataset){
        textToDisplay.add("Earthquake detected in:" + location.get(dataset) + "on " + days.get(dataset) + "." + months.get(dataset) + " . It was Magnitude " + magnitudes.get(dataset) + " at " + depths.get(dataset) + " km deep.");
    }

    void SortFebruary(){
        SortMonths(2);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, textToDisplay);
        ListDisplay.setAdapter(adapter);
    }

    void SortMarch(){
        SortMonths(3);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, textToDisplay);
        ListDisplay.setAdapter(adapter);
    }

    void SortMonths(Integer monthUsed){
        Iterator itrMonths = months.iterator();
        Iterator itrTitles = textToDisplay.iterator();
        Iterator itrLinks = links.iterator();
        Iterator itrLats = lats.iterator();
        Iterator itrLons = lons.iterator();
        Iterator itrDays = days.iterator();
        Iterator itrMags = magnitudes.iterator();
        Iterator itrDepths = depths.iterator();
        Iterator itrLocations = location.iterator();

        while (itrMonths.hasNext()){
            int x = (Integer)itrMonths.next();
            itrTitles.next();
            itrLinks.next();
            itrLats.next();
            itrLons.next();
            itrDays.next();
            itrMags.next();
            itrDepths.next();
            itrLocations.next();
            if (x != monthUsed){
                itrMonths.remove();
                itrDays.remove();
                itrDepths.remove();
                itrLats.remove();
                itrLinks.remove();
                itrLons.remove();
                itrMags.remove();
                itrTitles.remove();
                itrLocations.remove();
            }
        }
    }
}